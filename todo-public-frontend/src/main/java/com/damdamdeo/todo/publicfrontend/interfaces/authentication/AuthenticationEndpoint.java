package com.damdamdeo.todo.publicfrontend.interfaces.authentication;

import com.damdamdeo.todo.publicfrontend.domain.authentication.AccessToken;
import com.damdamdeo.todo.publicfrontend.domain.authentication.RefreshTokenInvalidException;
import com.damdamdeo.todo.publicfrontend.domain.authentication.UserAuthenticationRemoteService;
import com.damdamdeo.todo.publicfrontend.domain.authentication.UsernameOrPasswordInvalidException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.quarkus.security.identity.SecurityIdentity;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Path("/authentication")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Authentication")
public class AuthenticationEndpoint {

    @Inject
    SecurityIdentity keycloakSecurityContext;

    @Inject
    UserAuthenticationRemoteService userAuthenticationRemoteService;

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
        return new AccessTokenDto(userAuthenticationRemoteService.login(username, password));
    }

    @POST
    @Path("/akveo/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @NoCache
    public AkveoAccessTokenDto akveoLogin(@FormParam("username") final String username,
                                          @FormParam("password") final String password) throws UsernameOrPasswordInvalidException {
        return new AkveoAccessTokenDto(userAuthenticationRemoteService.login(username, password));
    }

    @POST
    @Path("/refresh-token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @NoCache
    public AccessTokenDto renewToken(@FormParam("refreshToken") final String refreshToken) throws RefreshTokenInvalidException {
        return new AccessTokenDto(userAuthenticationRemoteService.refreshToken(refreshToken));
    }

    @POST
    @Path("/akveo/refresh-token")
    @Consumes(MediaType.APPLICATION_JSON)
    @NoCache
    public AkveoAccessTokenDto akveoRenewToken(final AkveoRefreshTokenDto akveoRefreshTokenDto) throws RefreshTokenInvalidException {
        return new AkveoAccessTokenDto(userAuthenticationRemoteService.refreshToken(akveoRefreshTokenDto.getRefreshToken()));
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @RegisterForReflection
    public static final class AkveoRefreshTokenDto {

        private final AkveoRefreshTokenTokenDto akveoRefreshTokenTokenDto;

        public AkveoRefreshTokenDto(@JsonProperty("token") final AkveoRefreshTokenTokenDto akveoRefreshTokenTokenDto) {
            this.akveoRefreshTokenTokenDto = akveoRefreshTokenTokenDto;
        }

        public AkveoRefreshTokenTokenDto getAkveoRefreshTokenTokenDto() {
            return akveoRefreshTokenTokenDto;
        }

        public String getRefreshToken() {
            return akveoRefreshTokenTokenDto.getRefreshToken();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AkveoRefreshTokenDto)) return false;
            AkveoRefreshTokenDto that = (AkveoRefreshTokenDto) o;
            return Objects.equals(akveoRefreshTokenTokenDto, that.akveoRefreshTokenTokenDto);
        }

        @Override
        public int hashCode() {
            return Objects.hash(akveoRefreshTokenTokenDto);
        }

        @Override
        public String toString() {
            return "AkveoRefreshTokenDto{" +
                    "akveoRefreshTokenTokenDto=" + akveoRefreshTokenTokenDto +
                    '}';
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @RegisterForReflection
    public static final class AkveoRefreshTokenTokenDto {

        private final String refreshToken;

        public AkveoRefreshTokenTokenDto(@JsonProperty("refresh_token") final String refreshToken) {
            this.refreshToken = refreshToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AkveoRefreshTokenTokenDto)) return false;
            AkveoRefreshTokenTokenDto that = (AkveoRefreshTokenTokenDto) o;
            return Objects.equals(refreshToken, that.refreshToken);
        }

        @Override
        public int hashCode() {
            return Objects.hash(refreshToken);
        }

        @Override
        public String toString() {
            return "AkveoRefreshTokenTokenDto{" +
                    "refreshToken='" + refreshToken + '\'' +
                    '}';
        }
    }

    @RegisterForReflection
    public static final class AkveoAccessTokenDto {

        private final AkveoAccessTokenTokenDto akveoAccessTokenTokenDto;

        public AkveoAccessTokenDto(final AccessToken accessToken) {
            this.akveoAccessTokenTokenDto = new AkveoAccessTokenTokenDto(accessToken);
        }

        @JsonProperty("token")
        public AkveoAccessTokenTokenDto getAkveoAccessTokenTokenDto() {
            return akveoAccessTokenTokenDto;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AkveoAccessTokenDto)) return false;
            AkveoAccessTokenDto that = (AkveoAccessTokenDto) o;
            return Objects.equals(akveoAccessTokenTokenDto, that.akveoAccessTokenTokenDto);
        }

        @Override
        public int hashCode() {
            return Objects.hash(akveoAccessTokenTokenDto);
        }

        @Override
        public String toString() {
            return "AkveoAccessTokenDto{" +
                    "akveoAccessTokenTokenDto=" + akveoAccessTokenTokenDto +
                    '}';
        }
    }

    @RegisterForReflection
    public static final class AkveoAccessTokenTokenDto {

        private final Long expiresIn;
        private final String accessToken;
        private final String refreshToken;

        public AkveoAccessTokenTokenDto(final AccessToken accessToken) {
            this.accessToken = accessToken.accessToken();
            this.expiresIn = accessToken.expiresIn();
            this.refreshToken = accessToken.refreshToken();
        }

        @JsonProperty("expires_in")
        public Long getExpiresIn() {
            return expiresIn;
        }

        @JsonProperty("access_token")
        public String getAccessToken() {
            return accessToken;
        }

        @JsonProperty("refresh_token")
        public String getRefreshToken() {
            return refreshToken;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AkveoAccessTokenTokenDto)) return false;
            AkveoAccessTokenTokenDto that = (AkveoAccessTokenTokenDto) o;
            return Objects.equals(expiresIn, that.expiresIn) &&
                    Objects.equals(accessToken, that.accessToken) &&
                    Objects.equals(refreshToken, that.refreshToken);
        }

        @Override
        public int hashCode() {
            return Objects.hash(expiresIn, accessToken, refreshToken);
        }

        @Override
        public String toString() {
            return "AkveoAccessTokenTokenDto{" +
                    "expiresIn=" + expiresIn +
                    ", accessToken='" + accessToken + '\'' +
                    ", refreshToken='" + refreshToken + '\'' +
                    '}';
        }
    }

    @RegisterForReflection
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

    @RegisterForReflection
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
