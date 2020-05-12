package com.damdamdeo.todo.publicfrontend.infrastructure;

import com.damdamdeo.todo.publicfrontend.domain.UnexpectedException;
import com.damdamdeo.todo.publicfrontend.domain.UsernameOrEmailAlreadyUsedException;
import com.damdamdeo.todo.publicfrontend.domain.UserRegistrationRemoteService;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;

import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.apache.commons.lang3.Validate;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.singletonList;

// https://stackoverflow.com/questions/51158642/keycloak-api-createuser-java

@ApplicationScoped
public class KeycloakUserRegistrationRemoteService implements UserRegistrationRemoteService {

    private final Keycloak keycloak;
    private final String userRealm;
    private final RoleRepresentation frontendUserRole;
    private final RetryPolicy<Object> retryPolicy;

    public KeycloakUserRegistrationRemoteService(@ConfigProperty(name = "quarkus.oidc.auth-server-url") final String authServerUrl) {
        // quarkus.oidc.auth-server-url=http://localhost:8087/auth/realms/todos
        // -> http://localhost:8087/auth
        // -> todos
        final Pattern pattern = Pattern.compile("^(.*)\\/realms\\/(.*)$");
        final Matcher matcher = pattern.matcher(authServerUrl);
        Validate.validState(matcher.matches());
        final String serverUrl = matcher.group(1);
        this.keycloak = Keycloak.getInstance(serverUrl, "master", "keycloak", "keycloak", "admin-cli");
        this.userRealm = matcher.group(2);
        this.frontendUserRole = keycloak.realm(userRealm)
                .roles()
                .list()
                .stream()
                .filter(roleRepresentation -> "frontend-user".equals(roleRepresentation.getName()))
                .findFirst().get();
        this.retryPolicy = new RetryPolicy<>()
                .handle(Exception.class)
                .withDelay(Duration.ofSeconds(1))
                .withMaxRetries(100);
    }

    @Override
    public void register(final String username, final String password, final String email) throws UsernameOrEmailAlreadyUsedException, UnexpectedException {
        final RealmResource realmResource = keycloak.realm(userRealm);
        final UsersResource usersResource = realmResource.users();

        final UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(username);
        userRepresentation.setEmail(email);
        userRepresentation.setEnabled(true);

        final CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(password);
        userRepresentation.setCredentials(singletonList(credentialRepresentation));

        // https://keycloak.discourse.group/t/realm-role-is-not-assigned-while-creating-user/2271
        // https://stackoverflow.com/questions/60812831/unable-to-assign-realm-role-to-a-newly-created-keycloak-user-via-admin-rest-api
        // https://stackoverflow.com/questions/57390389/the-realmroles-parameter-is-ignored-when-adding-a-user-via-the-keycloak-api
        // https://issues.redhat.com/browse/KEYCLOAK-12460
        // https://stackoverflow.com/questions/51158642/keycloak-api-createuser-java
        // userRepresentation.setRealmRoles(singletonList("frontend-user"));

        final Response response = usersResource.create(userRepresentation);
        switch (response.getStatus()) {
            case 201:
                final String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
                // Here is a SPOF. If my request - to associate an user with the 'frontend-user' group - fails, the user
                // won't be able to access rest api.
                // A retry is used as a quick win. However, a more robust solution would be to use Debezium to push
                // keycloak user data into a topic and associate group and user from a kafka consumer.
                Failsafe.with(retryPolicy).run(() -> keycloak.realm(userRealm)
                        .users()
                        .get(userId)
                        .roles()
                        .realmLevel()
                        .add(singletonList(frontendUserRole)));
                break;
            case 409:
                throw new UsernameOrEmailAlreadyUsedException(username, email);
            default:
                // TODO enrich but first I have to find a case ... if there are ...
                throw new UnexpectedException(response.getStatus());
        }
    }

}
