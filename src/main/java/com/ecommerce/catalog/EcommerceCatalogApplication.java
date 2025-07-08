package com.ecommerce.catalog;

import com.ecommerce.catalog.config.EcommerceCatalogConfiguration;
import com.ecommerce.catalog.health.DatabaseHealthCheck;
import com.ecommerce.catalog.resources.ProductResource;
import com.ecommerce.catalog.resources.CategoryResource;
import com.ecommerce.catalog.auth.OAuth2AuthFilter;
import com.ecommerce.catalog.model.Product;
import com.ecommerce.catalog.model.Category;
import io.dropwizard.core.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.hibernate.SessionFactory;

import javax.ws.rs.container.ContainerRequestFilter;
import java.security.Principal;

public class EcommerceCatalogApplication extends Application<EcommerceCatalogConfiguration> {
    
    private final HibernateBundle<EcommerceCatalogConfiguration> hibernateBundle =
            new HibernateBundle<EcommerceCatalogConfiguration>(Product.class, Category.class) {
                @Override
                public DataSourceFactory getDataSourceFactory(EcommerceCatalogConfiguration configuration) {
                    return configuration.getDataSourceFactory();
                }
            };

    public static void main(String[] args) throws Exception {
        new EcommerceCatalogApplication().run(args);
    }

    @Override
    public String getName() {
        return "ecommerce-catalog";
    }

    @Override
    public void initialize(Bootstrap<EcommerceCatalogConfiguration> bootstrap) {
        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new MigrationsBundle<EcommerceCatalogConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(EcommerceCatalogConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
    }

    @Override
    public void run(EcommerceCatalogConfiguration configuration, Environment environment) {
        final SessionFactory sessionFactory = hibernateBundle.getSessionFactory();
        
        // Configure OAuth2 authentication (simplified)
        final ContainerRequestFilter oauth2Filter = new OAuth2AuthFilter.Builder<Principal>()
            .setAuthenticator(new com.ecommerce.catalog.auth.OAuth2Authenticator(configuration.getOAuth2Config()))
            .setPrefix("Bearer")
            .buildAuthFilter();
            
        environment.jersey().register(new AuthDynamicFeature(oauth2Filter));
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(Principal.class));
        
        // Register resources
        environment.jersey().register(new ProductResource(sessionFactory));
        environment.jersey().register(new CategoryResource(sessionFactory));
        
        // Register health checks
        environment.healthChecks().register("database", new DatabaseHealthCheck(sessionFactory));
        
        // Configure Swagger 2.0
        configureSwagger(environment);
    }
    
    private void configureSwagger(Environment environment) {
        BeanConfig config = new BeanConfig();
        config.setTitle("E-commerce Catalog API");
        config.setDescription("API for managing products and categories");
        config.setVersion("2.0.0");
        config.setSchemes(new String[]{"http", "https"});
        config.setHost("localhost:8080");
        config.setBasePath("/catalog/v2");
        config.setResourcePackage("com.ecommerce.catalog.resources");
        config.setScan(true);
        
        environment.jersey().register(new ApiListingResource());
        environment.jersey().register(new SwaggerSerializers());
    }
}