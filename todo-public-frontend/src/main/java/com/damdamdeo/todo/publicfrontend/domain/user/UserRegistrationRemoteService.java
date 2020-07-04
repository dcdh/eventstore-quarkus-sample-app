package com.damdamdeo.todo.publicfrontend.domain.user;

public interface UserRegistrationRemoteService {

    void register(String username, String password, String email) throws UsernameOrEmailAlreadyUsedException, UnexpectedException;

}
