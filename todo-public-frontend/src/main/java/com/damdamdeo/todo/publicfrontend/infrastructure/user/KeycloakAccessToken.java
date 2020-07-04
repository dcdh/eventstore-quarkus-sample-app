package com.damdamdeo.todo.publicfrontend.infrastructure.user;

import com.damdamdeo.todo.publicfrontend.domain.user.AccessToken;
import org.keycloak.representations.AccessTokenResponse;

import java.util.Objects;

public final class KeycloakAccessToken implements AccessToken {

    private final String accessToken;
    private final Long expiresIn;
    private final String refreshToken;
    private final Long refreshExpiresIn;

    public KeycloakAccessToken(final String accessToken,
                               final Long expiresIn,
                               final String refreshToken,
                               final Long refreshExpiresIn) {
        this.accessToken = Objects.requireNonNull(accessToken);
        this.expiresIn = Objects.requireNonNull(expiresIn);
        this.refreshToken = Objects.requireNonNull(refreshToken);
        this.refreshExpiresIn = Objects.requireNonNull(refreshExpiresIn);
    }

    public KeycloakAccessToken(final AccessTokenResponse accessTokenResponse) {
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
        KeycloakAccessToken that = (KeycloakAccessToken) o;
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
