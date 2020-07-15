package com.damdamdeo.todo.publicfrontend.infrastructure.user;

import com.damdamdeo.todo.publicfrontend.domain.user.AccessToken;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.keycloak.representations.AccessTokenResponse;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import java.util.Objects;

@RegisterForReflection
public final class KeycloakAccessTokenDTO implements AccessToken {

    private final String accessToken;
    private final Long expiresIn;
    private final String refreshToken;
    private final Long refreshExpiresIn;

    @JsonbCreator
    public KeycloakAccessTokenDTO(@JsonbProperty("access_token") final String accessToken,
                                  @JsonbProperty("expires_in") final Long expiresIn,
                                  @JsonbProperty("refresh_token") final String refreshToken,
                                  @JsonbProperty("refresh_expires_in") final Long refreshExpiresIn) {
        this.accessToken = Objects.requireNonNull(accessToken);
        this.expiresIn = Objects.requireNonNull(expiresIn);
        this.refreshToken = Objects.requireNonNull(refreshToken);
        this.refreshExpiresIn = Objects.requireNonNull(refreshExpiresIn);
    }

    public KeycloakAccessTokenDTO(final AccessTokenResponse accessTokenResponse) {
        this(accessTokenResponse.getToken(),
                accessTokenResponse.getExpiresIn(),
                accessTokenResponse.getRefreshToken(),
                accessTokenResponse.getRefreshExpiresIn());
    }

    @Override
    public String accessToken() {
        return accessToken;
    }

    @Override
    public Long expiresIn() {
        return expiresIn;
    }

    @Override
    public String refreshToken() {
        return refreshToken;
    }

    @Override
    public Long refreshExpiresIn() {
        return refreshExpiresIn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeycloakAccessTokenDTO that = (KeycloakAccessTokenDTO) o;
        return Objects.equals(accessToken, that.accessToken) &&
                Objects.equals(expiresIn, that.expiresIn) &&
                Objects.equals(refreshToken, that.refreshToken) &&
                Objects.equals(refreshExpiresIn, that.refreshExpiresIn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessToken, expiresIn, refreshToken, refreshExpiresIn);
    }
}
