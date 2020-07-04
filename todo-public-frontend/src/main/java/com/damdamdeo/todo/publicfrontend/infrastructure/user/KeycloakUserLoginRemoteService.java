package com.damdamdeo.todo.publicfrontend.infrastructure.user;

import com.damdamdeo.todo.publicfrontend.domain.user.AccessToken;
import com.damdamdeo.todo.publicfrontend.domain.user.UserLoginRemoteService;
import com.damdamdeo.todo.publicfrontend.domain.user.UsernameOrPasswordInvalidException;
import org.apache.commons.lang3.Validate;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.authorization.client.util.HttpResponseException;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class KeycloakUserLoginRemoteService implements UserLoginRemoteService {

    // TODO je devrai injecter les conf keycloak !!! realm, username, password et clientId!

    private final AuthzClient authzClient;

    public KeycloakUserLoginRemoteService(@ConfigProperty(name = "quarkus.oidc.auth-server-url") final String authServerUrl,
                                          @ConfigProperty(name = "quarkus.oidc.client-id") final String clientId,
                                          @ConfigProperty(name = "quarkus.oidc.credentials.secret") final String secret) {
        final Pattern pattern = Pattern.compile("^(.*)\\/realms\\/(.*)$");
        final Matcher matcher = pattern.matcher(authServerUrl);
        Validate.validState(matcher.matches());
        final String serverUrl = matcher.group(1);
        final String userRealm = matcher.group(2);
        final Map<String, Object> clientCredentials = new HashMap<>();
        clientCredentials.put("secret", secret);
        this.authzClient = AuthzClient.create(
                new Configuration(serverUrl, userRealm, clientId, clientCredentials, null));
    }

    @Override
    public AccessToken login(final String username, final String password) throws UsernameOrPasswordInvalidException {
        try {
            return new KeycloakAccessToken(this.authzClient.obtainAccessToken(username, password));
        } catch (final HttpResponseException e) {
            if (e.getStatusCode() == 401) {
                throw new UsernameOrPasswordInvalidException();
            }
            throw e;
        }
    }

}
