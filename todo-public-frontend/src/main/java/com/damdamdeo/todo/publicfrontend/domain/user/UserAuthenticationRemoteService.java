package com.damdamdeo.todo.publicfrontend.domain.user;

public interface UserAuthenticationRemoteService {

    AccessToken login(String username, String password) throws UsernameOrPasswordInvalidException;

    AccessToken refreshToken(String refreshToken) throws RefreshTokenInvalidException;

}
