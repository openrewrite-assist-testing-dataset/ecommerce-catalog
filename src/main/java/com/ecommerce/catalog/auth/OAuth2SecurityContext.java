package com.ecommerce.catalog.auth;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

public class OAuth2SecurityContext implements SecurityContext {
    private final Object principal;
    private final String scheme;
    private final ContainerRequestContext requestContext;

    public OAuth2SecurityContext(Object principal, String scheme, ContainerRequestContext requestContext) {
        this.principal = principal;
        this.scheme = scheme;
        this.requestContext = requestContext;
    }

    @Override
    public Principal getUserPrincipal() {
        return new Principal() {
            @Override
            public String getName() {
                return principal.toString();
            }
        };
    }

    @Override
    public boolean isUserInRole(String role) {
        return true;
    }

    @Override
    public boolean isSecure() {
        return requestContext.getSecurityContext().isSecure();
    }

    @Override
    public String getAuthenticationScheme() {
        return scheme;
    }
}