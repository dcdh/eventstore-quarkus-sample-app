package com.damdamdeo.todo.publicfrontend.interfaces;

import io.quarkus.security.identity.SecurityIdentity;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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

    @GET
    @Path("/me")
    @Produces(MediaType.APPLICATION_JSON)
    @NoCache
    public UserDTO me() {
        return new UserDTO(keycloakSecurityContext);
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
