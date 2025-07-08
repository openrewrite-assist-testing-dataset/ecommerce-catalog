package com.ecommerce.catalog.health;

import com.codahale.metrics.health.HealthCheck;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class DatabaseHealthCheck extends HealthCheck {
    private final SessionFactory sessionFactory;

    public DatabaseHealthCheck(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    protected Result check() throws Exception {
        try (Session session = sessionFactory.openSession()) {
            session.createNativeQuery("SELECT 1").getSingleResult();
            return Result.healthy("Database connection is healthy");
        } catch (Exception e) {
            return Result.unhealthy("Database connection failed: " + e.getMessage());
        }
    }
}