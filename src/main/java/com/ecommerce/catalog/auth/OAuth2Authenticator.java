package com.ecommerce.catalog.auth;

import com.ecommerce.catalog.config.EcommerceCatalogConfiguration;
import io.dropwizard.auth.Authenticator;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.spec.SecretKeySpec;
import java.security.Principal;
import java.util.Optional;

public class OAuth2Authenticator implements Authenticator<String, Principal> {
    private final EcommerceCatalogConfiguration.OAuth2Config oauth2Config;
    private final JwtDecoder jwtDecoder;

    public OAuth2Authenticator(EcommerceCatalogConfiguration.OAuth2Config oauth2Config) {
        this.oauth2Config = oauth2Config;
        
        // Create JWT decoder with client secret (simplified implementation)
        SecretKeySpec secretKey = new SecretKeySpec(
            oauth2Config.getClientSecret().getBytes(), 
            MacAlgorithm.HS256.getName()
        );
        this.jwtDecoder = NimbusJwtDecoder.withSecretKey(secretKey)
            .macAlgorithm(MacAlgorithm.HS256)
            .build();
    }

    @Override
    public Optional<Principal> authenticate(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            String subject = jwt.getSubject();
            String scope = jwt.getClaimAsString("scope");
            
            // Validate scope contains required permissions
            if (subject != null && scope != null && 
                (scope.contains("catalog:read") || scope.contains("catalog:write"))) {
                return Optional.of(new Principal() {
                    @Override
                    public String getName() {
                        return subject;
                    }
                });
            }
        } catch (Exception e) {
            // Token validation failed
        }
        return Optional.empty();
    }
}