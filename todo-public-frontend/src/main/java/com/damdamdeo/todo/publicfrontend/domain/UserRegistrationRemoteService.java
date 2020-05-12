package com.damdamdeo.todo.publicfrontend.domain;

public interface UserRegistrationRemoteService {

    void register(String username, String password, String email) throws UsernameOrEmailAlreadyUsedException, UnexpectedException;

}
