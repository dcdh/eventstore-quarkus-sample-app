package com.damdamdeo.todo.publicfrontend.domain.authentication;

public interface UserAuthenticationRemoteService {

    AccessToken login(String username, String password) throws UsernameOrPasswordInvalidException;

    AccessToken refreshToken(String refreshToken) throws RefreshTokenInvalidException;

}
