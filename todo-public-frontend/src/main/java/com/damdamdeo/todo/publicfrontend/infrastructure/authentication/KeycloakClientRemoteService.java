package com.damdamdeo.todo.publicfrontend.infrastructure.authentication;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/")
@ApplicationScoped
@RegisterRestClient(configKey="keycloak-api")
public interface KeycloakClientRemoteService {

    @POST
    @Path("/protocol/openid-connect/token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    KeycloakAccessTokenDTO obtainAccessToken(@FormParam("grant_type") String grantType,
                                             @FormParam("username") String username,
                                             @FormParam("password") String password,
                                             @FormParam("client_id") String clientId,
                                             @FormParam("client_secret") String clientSecret);


    @POST
    @Path("/protocol/openid-connect/token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    KeycloakAccessTokenDTO refreshToken(@FormParam("grant_type") String grantType,
                                        @FormParam("refresh_token") String refreshToken,
                                        @FormParam("client_id") String clientId);
}
