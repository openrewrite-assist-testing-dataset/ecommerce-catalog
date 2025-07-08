package com.ecommerce.catalog.db;

import com.ecommerce.catalog.model.Product;
import com.ecommerce.catalog.model.Category;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDAO {
    private final SessionFactory sessionFactory;

    public ProductDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Optional<Product> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Product product = session.get(Product.class, id);
            return Optional.ofNullable(product);
        }
    }

    public List<Product> findAll(int offset, int limit) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Product> criteria = builder.createQuery(Product.class);
            Root<Product> root = criteria.from(Product.class);
            
            criteria.select(root);
            criteria.orderBy(builder.asc(root.get("id")));
            
            Query<Product> query = session.createQuery(criteria);
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            
            return query.getResultList();
        }
    }

    public List<Product> findByCategoryId(Long categoryId, int offset, int limit) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Product> criteria = builder.createQuery(Product.class);
            Root<Product> root = criteria.from(Product.class);
            Join<Product, Category> categoryJoin = root.join("category");
            
            criteria.select(root);
            criteria.where(builder.equal(categoryJoin.get("id"), categoryId));
            criteria.orderBy(builder.asc(root.get("id")));
            
            Query<Product> query = session.createQuery(criteria);
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            
            return query.getResultList();
        }
    }

    public List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, int offset, int limit) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Product> criteria = builder.createQuery(Product.class);
            Root<Product> root = criteria.from(Product.class);
            
            List<Predicate> predicates = new ArrayList<>();
            
            if (minPrice != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            
            criteria.select(root);
            criteria.where(predicates.toArray(new Predicate[0]));
            criteria.orderBy(builder.asc(root.get("price")));
            
            Query<Product> query = session.createQuery(criteria);
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            
            return query.getResultList();
        }
    }

    public List<Product> search(String searchTerm, int offset, int limit) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Product> criteria = builder.createQuery(Product.class);
            Root<Product> root = criteria.from(Product.class);
            
            String pattern = "%" + searchTerm.toLowerCase() + "%";
            Predicate namePredicate = builder.like(builder.lower(root.get("name")), pattern);
            Predicate descriptionPredicate = builder.like(builder.lower(root.get("description")), pattern);
            
            criteria.select(root);
            criteria.where(builder.or(namePredicate, descriptionPredicate));
            criteria.orderBy(builder.asc(root.get("name")));
            
            Query<Product> query = session.createQuery(criteria);
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            
            return query.getResultList();
        }
    }

    public Product save(Product product) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        
        try {
            session.saveOrUpdate(product);
            transaction.commit();
            return product;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public void delete(Long id) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        
        try {
            Product product = session.get(Product.class, id);
            if (product != null) {
                session.delete(product);
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public long count() {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
            Root<Product> root = criteria.from(Product.class);
            
            criteria.select(builder.count(root));
            
            Query<Long> query = session.createQuery(criteria);
            return query.getSingleResult();
        }
    }
}