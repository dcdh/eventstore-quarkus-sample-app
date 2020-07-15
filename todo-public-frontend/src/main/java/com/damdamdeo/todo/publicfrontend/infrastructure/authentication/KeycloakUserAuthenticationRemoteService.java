package com.damdamdeo.todo.publicfrontend.infrastructure.authentication;

import com.damdamdeo.todo.publicfrontend.domain.authentication.AccessToken;
import com.damdamdeo.todo.publicfrontend.domain.authentication.RefreshTokenInvalidException;
import com.damdamdeo.todo.publicfrontend.domain.authentication.UserAuthenticationRemoteService;
import com.damdamdeo.todo.publicfrontend.domain.authentication.UsernameOrPasswordInvalidException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.WebApplicationException;

@ApplicationScoped
public class KeycloakUserAuthenticationRemoteService implements UserAuthenticationRemoteService {

    private final KeycloakClientRemoteService keycloakClientRemoteService;
    private final String clientId;
    private final String secret;

    public KeycloakUserAuthenticationRemoteService(@ConfigProperty(name = "quarkus.oidc.client-id") final String clientId,
                                                   @ConfigProperty(name = "quarkus.oidc.credentials.secret") final String secret,
                                                   @RestClient final KeycloakClientRemoteService keycloakClientRemoteService) {
        this.keycloakClientRemoteService = keycloakClientRemoteService;
        this.clientId = clientId;
        this.secret = secret;
    }

    @Override
    public AccessToken login(final String username, final String password) throws UsernameOrPasswordInvalidException {
        try {
            return this.keycloakClientRemoteService.obtainAccessToken("password", username, password, clientId, secret);
        } catch (final WebApplicationException e) {
            if (e.getResponse().getStatus() == 401) {
                throw new UsernameOrPasswordInvalidException();
            }
            throw e;
        }
    }

    @Override
    public AccessToken refreshToken(final String refreshToken) throws RefreshTokenInvalidException {
        try {
            return this.keycloakClientRemoteService.refreshToken("refresh_token", refreshToken, this.clientId);
        } catch (final WebApplicationException e) {
            if (e.getResponse().getStatus() == 400) {
                throw new RefreshTokenInvalidException();
            }
            throw e;
        }
    }

}
