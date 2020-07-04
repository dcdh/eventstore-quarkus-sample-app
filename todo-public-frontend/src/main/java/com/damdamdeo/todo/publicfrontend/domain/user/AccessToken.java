package com.damdamdeo.todo.publicfrontend.domain.user;

public interface AccessToken {

    String accessToken();

    Long expiresIn();

    String refreshToken();

    Long refreshExpiresIn();

}
