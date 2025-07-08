package com.ecommerce.catalog.auth;

import com.ecommerce.catalog.config.EcommerceCatalogConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.security.Principal;
import java.util.Optional;

import static org.junit.Assert.assertFalse;

public class OAuth2AuthenticatorTest {

    private OAuth2Authenticator oauth2Authenticator;
    private EcommerceCatalogConfiguration.OAuth2Config oauth2Config;

    @Before
    public void setUp() {
        oauth2Config = new EcommerceCatalogConfiguration.OAuth2Config();
        oauth2Config.setClientId("test-client");
        oauth2Config.setClientSecret("test-secret");
        oauth2Config.setScope("catalog:read catalog:write");
        
        oauth2Authenticator = new OAuth2Authenticator(oauth2Config);
    }

    @Test
    public void testAuthenticate_ReturnsEmpty_WhenTokenIsInvalid() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        Optional<Principal> result = oauth2Authenticator.authenticate(invalidToken);

        // Then
        assertFalse("Should return empty for invalid token", result.isPresent());
    }

    @Test
    public void testAuthenticate_ReturnsEmpty_WhenTokenIsNull() {
        // Given
        String nullToken = null;

        // When
        Optional<Principal> result = oauth2Authenticator.authenticate(nullToken);

        // Then
        assertFalse("Should return empty for null token", result.isPresent());
    }

    @Test
    public void testAuthenticate_ReturnsEmpty_WhenTokenIsEmpty() {
        // Given
        String emptyToken = "";

        // When
        Optional<Principal> result = oauth2Authenticator.authenticate(emptyToken);

        // Then
        assertFalse("Should return empty for empty token", result.isPresent());
    }

    @Test
    public void testAuthenticate_ReturnsEmpty_WhenTokenIsMalformed() {
        // Given
        String malformedToken = "not-a-jwt-token";

        // When
        Optional<Principal> result = oauth2Authenticator.authenticate(malformedToken);

        // Then
        assertFalse("Should return empty for malformed token", result.isPresent());
    }
}