package com.ecommerce.catalog.db;

import com.ecommerce.catalog.model.Category;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

public class CategoryDAO {
    private final SessionFactory sessionFactory;

    public CategoryDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Optional<Category> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Category category = session.get(Category.class, id);
            return Optional.ofNullable(category);
        }
    }

    public List<Category> findAll() {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Category> criteria = builder.createQuery(Category.class);
            Root<Category> root = criteria.from(Category.class);
            
            criteria.select(root);
            criteria.orderBy(builder.asc(root.get("name")));
            
            Query<Category> query = session.createQuery(criteria);
            return query.getResultList();
        }
    }

    public List<Category> findRootCategories() {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Category> criteria = builder.createQuery(Category.class);
            Root<Category> root = criteria.from(Category.class);
            
            criteria.select(root);
            criteria.where(builder.isNull(root.get("parent")));
            criteria.orderBy(builder.asc(root.get("name")));
            
            Query<Category> query = session.createQuery(criteria);
            return query.getResultList();
        }
    }

    public List<Category> findByParentId(Long parentId) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Category> criteria = builder.createQuery(Category.class);
            Root<Category> root = criteria.from(Category.class);
            
            criteria.select(root);
            criteria.where(builder.equal(root.get("parent").get("id"), parentId));
            criteria.orderBy(builder.asc(root.get("name")));
            
            Query<Category> query = session.createQuery(criteria);
            return query.getResultList();
        }
    }

    public Optional<Category> findByName(String name) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Category> criteria = builder.createQuery(Category.class);
            Root<Category> root = criteria.from(Category.class);
            
            criteria.select(root);
            criteria.where(builder.equal(root.get("name"), name));
            
            Query<Category> query = session.createQuery(criteria);
            List<Category> results = query.getResultList();
            
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        }
    }

    public Category save(Category category) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        
        try {
            session.saveOrUpdate(category);
            transaction.commit();
            return category;
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
            Category category = session.get(Category.class, id);
            if (category != null) {
                session.delete(category);
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
            Root<Category> root = criteria.from(Category.class);
            
            criteria.select(builder.count(root));
            
            Query<Long> query = session.createQuery(criteria);
            return query.getSingleResult();
        }
    }
}