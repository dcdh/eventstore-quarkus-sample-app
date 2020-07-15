package com.damdamdeo.todo.publicfrontend.domain.authentication;

public interface AccessToken {

    String accessToken();

    Long expiresIn();

    String refreshToken();

    Long refreshExpiresIn();

}
