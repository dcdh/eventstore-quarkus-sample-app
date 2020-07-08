package com.damdamdeo.todo.publicfrontend.interfaces;

import com.damdamdeo.todo.publicfrontend.domain.user.AccessToken;
import com.damdamdeo.todo.publicfrontend.domain.user.UserLoginRemoteService;
import com.damdamdeo.todo.publicfrontend.domain.user.UsernameOrPasswordInvalidException;
import io.quarkus.security.identity.SecurityIdentity;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "User")
public class AuthenticationEndpoint {

    @Inject
    SecurityIdentity keycloakSecurityContext;

    @Inject
    UserLoginRemoteService userLoginRemoteService;

    @GET
    @Path("/me")
    @Produces(MediaType.APPLICATION_JSON)
    @NoCache
    public UserDTO me() {
        return new UserDTO(keycloakSecurityContext);
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @NoCache
    public AccessTokenDto login(@FormParam("username") final String username,
                      @FormParam("password") final String password) throws UsernameOrPasswordInvalidException {
        return new AccessTokenDto(userLoginRemoteService.login(username, password));
    }

    public static final class AccessTokenDto {

        private final String accessToken;
        private final Long expiresIn;
        private final String refreshToken;
        private final Long refreshExpiresIn;

        AccessTokenDto(final AccessToken accessToken) {
            this.accessToken = accessToken.accessToken();
            this.expiresIn = accessToken.expiresIn();
            this.refreshToken = accessToken.refreshToken();
            this.refreshExpiresIn = accessToken.refreshExpiresIn();
        }

        public String getAccessToken() {
            return accessToken;
        }

        public Long getExpiresIn() {
            return expiresIn;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public Long getRefreshExpiresIn() {
            return refreshExpiresIn;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AccessTokenDto that = (AccessTokenDto) o;
            return Objects.equals(accessToken, that.accessToken) &&
                    Objects.equals(expiresIn, that.expiresIn) &&
                    Objects.equals(refreshToken, that.refreshToken) &&
                    Objects.equals(refreshExpiresIn, that.refreshExpiresIn);
        }

        @Override
        public int hashCode() {
            return Objects.hash(accessToken, expiresIn, refreshToken, refreshExpiresIn);
        }

        @Override
        public String toString() {
            return "AccessTokenDto{" +
                    "accessToken='" + accessToken + '\'' +
                    ", expiresIn=" + expiresIn +
                    ", refreshToken='" + refreshToken + '\'' +
                    ", refreshExpiresIn=" + refreshExpiresIn +
                    '}';
        }
    }

    public static final class UserDTO {

        private final String userName;
        private final Set<String> roles;
        private final Map<String, Object> attributes;
        private final boolean isAnonymous;

        UserDTO(final SecurityIdentity securityContext) {
            this.userName = securityContext.getPrincipal().getName();
            this.roles = securityContext.getRoles();
            this.attributes = securityContext.getAttributes();
            this.isAnonymous = securityContext.isAnonymous();
        }

        public String getUserName() {
            return userName;
        }

        public Set<String> getRoles() {
            return roles;
        }

        public Map<String, Object> getAttributes() {
            return attributes;
        }

        public boolean isAnonymous() {
            return isAnonymous;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserDTO userDTO = (UserDTO) o;
            return isAnonymous == userDTO.isAnonymous &&
                    Objects.equals(userName, userDTO.userName) &&
                    Objects.equals(roles, userDTO.roles) &&
                    Objects.equals(attributes, userDTO.attributes);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userName, roles, attributes, isAnonymous);
        }

        @Override
        public String toString() {
            return "User{" +
                    "userName='" + userName + '\'' +
                    ", roles=" + roles +
                    ", attributes=" + attributes +
                    ", isAnonymous=" + isAnonymous +
                    '}';
        }
    }

}
