package com.ecommerce.catalog.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;
import io.dropwizard.db.DataSourceFactory;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class EcommerceCatalogConfiguration extends Configuration {
    
    @Valid
    @NotNull
    @JsonProperty("database")
    private DataSourceFactory database = new DataSourceFactory();
    
    @JsonProperty("oauth2")
    private OAuth2Config oauth2Config = new OAuth2Config();
    
    @JsonProperty("catalogSettings")
    private CatalogSettings catalogSettings = new CatalogSettings();

    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    public void setDataSourceFactory(DataSourceFactory factory) {
        this.database = factory;
    }

    public OAuth2Config getOAuth2Config() {
        return oauth2Config;
    }

    public void setOAuth2Config(OAuth2Config oauth2Config) {
        this.oauth2Config = oauth2Config;
    }

    public CatalogSettings getCatalogSettings() {
        return catalogSettings;
    }

    public void setCatalogSettings(CatalogSettings catalogSettings) {
        this.catalogSettings = catalogSettings;
    }

    public static class OAuth2Config {
        @JsonProperty("clientId")
        private String clientId = "catalog-client";
        
        @JsonProperty("clientSecret")
        private String clientSecret = "catalog-secret-2023";
        
        @JsonProperty("scope")
        private String scope = "catalog:read catalog:write";
        
        @JsonProperty("tokenEndpoint")
        private String tokenEndpoint = "https://auth.example.com/oauth/token";
        
        @JsonProperty("userInfoEndpoint")
        private String userInfoEndpoint = "https://auth.example.com/oauth/userinfo";

        public String getClientId() { return clientId; }
        public void setClientId(String clientId) { this.clientId = clientId; }
        
        public String getClientSecret() { return clientSecret; }
        public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }
        
        public String getScope() { return scope; }
        public void setScope(String scope) { this.scope = scope; }
        
        public String getTokenEndpoint() { return tokenEndpoint; }
        public void setTokenEndpoint(String tokenEndpoint) { this.tokenEndpoint = tokenEndpoint; }
        
        public String getUserInfoEndpoint() { return userInfoEndpoint; }
        public void setUserInfoEndpoint(String userInfoEndpoint) { this.userInfoEndpoint = userInfoEndpoint; }
    }

    public static class CatalogSettings {
        @JsonProperty("maxProductsPerPage")
        private int maxProductsPerPage = 100;
        
        @JsonProperty("defaultPageSize")
        private int defaultPageSize = 20;
        
        @JsonProperty("enableCategoryHierarchy")
        private boolean enableCategoryHierarchy = true;
        
        @JsonProperty("stockThreshold")
        private int stockThreshold = 10;

        public int getMaxProductsPerPage() { return maxProductsPerPage; }
        public void setMaxProductsPerPage(int maxProductsPerPage) { this.maxProductsPerPage = maxProductsPerPage; }
        
        public int getDefaultPageSize() { return defaultPageSize; }
        public void setDefaultPageSize(int defaultPageSize) { this.defaultPageSize = defaultPageSize; }
        
        public boolean isEnableCategoryHierarchy() { return enableCategoryHierarchy; }
        public void setEnableCategoryHierarchy(boolean enableCategoryHierarchy) { this.enableCategoryHierarchy = enableCategoryHierarchy; }
        
        public int getStockThreshold() { return stockThreshold; }
        public void setStockThreshold(int stockThreshold) { this.stockThreshold = stockThreshold; }
    }
}