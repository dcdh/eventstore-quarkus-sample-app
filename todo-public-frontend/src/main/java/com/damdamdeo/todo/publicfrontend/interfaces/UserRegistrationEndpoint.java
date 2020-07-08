package com.damdamdeo.todo.publicfrontend.interfaces;

import com.damdamdeo.todo.publicfrontend.domain.user.UserRegistrationRemoteService;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/registration")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Registration")
public class UserRegistrationEndpoint {

    @Inject
    UserRegistrationRemoteService userRegistrationRemoteService;

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @NoCache
    public void register(@FormParam("username") final String username,
                         @FormParam("password") final String password,
                         @FormParam("email") final String email) throws Exception {
        this.userRegistrationRemoteService.register(username, password, email);
    }

}
