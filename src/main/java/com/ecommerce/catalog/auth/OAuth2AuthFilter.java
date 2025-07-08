package com.ecommerce.catalog.auth;

import io.dropwizard.auth.AuthFilter;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

@Priority(Priorities.AUTHENTICATION)
public class OAuth2AuthFilter<P extends Principal> extends AuthFilter<String, P> {
    
    protected io.dropwizard.auth.Authenticator<String, P> authenticator;
    protected String prefix;
    protected String realm;
    protected io.dropwizard.auth.UnauthorizedHandler unauthorizedHandler;
    protected org.slf4j.Logger logger;
    
    private OAuth2AuthFilter() {}

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        final String header = requestContext.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        
        if (header == null) {
            throw new WebApplicationException(unauthorizedHandler.buildResponse(prefix, realm));
        }

        final int space = header.indexOf(' ');
        if (space <= 0) {
            throw new WebApplicationException(unauthorizedHandler.buildResponse(prefix, realm));
        }

        final String method = header.substring(0, space);
        if (!prefix.equalsIgnoreCase(method)) {
            throw new WebApplicationException(unauthorizedHandler.buildResponse(prefix, realm));
        }

        final String token = header.substring(space + 1);
        
        try {
            final Optional<P> result = authenticator.authenticate(token);
            if (result.isPresent()) {
                requestContext.setSecurityContext(new OAuth2SecurityContext(result.get(), "OAuth2", requestContext));
                return;
            }
        } catch (Exception e) {
            logger.warn("Error authenticating OAuth2 token", e);
        }

        throw new WebApplicationException(unauthorizedHandler.buildResponse(prefix, realm));
    }

    public static class Builder<P extends Principal> {
        private io.dropwizard.auth.Authenticator<String, P> authenticator;
        private String prefix = "Bearer";
        
        public Builder<P> setAuthenticator(io.dropwizard.auth.Authenticator<String, P> authenticator) {
            this.authenticator = authenticator;
            return this;
        }
        
        public Builder<P> setPrefix(String prefix) {
            this.prefix = prefix;
            return this;
        }
        
        public OAuth2AuthFilter<P> buildAuthFilter() {
            OAuth2AuthFilter<P> filter = new OAuth2AuthFilter<>();
            filter.authenticator = authenticator;
            filter.prefix = prefix;
            filter.realm = "oauth2";
            filter.unauthorizedHandler = new io.dropwizard.auth.DefaultUnauthorizedHandler();
            filter.logger = org.slf4j.LoggerFactory.getLogger(OAuth2AuthFilter.class);
            return filter;
        }
    }
}