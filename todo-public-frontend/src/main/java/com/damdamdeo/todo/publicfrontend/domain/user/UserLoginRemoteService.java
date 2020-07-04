package com.damdamdeo.todo.publicfrontend.domain.user;

public interface UserLoginRemoteService {

    AccessToken login(String username, String password) throws UsernameOrPasswordInvalidException;

}
