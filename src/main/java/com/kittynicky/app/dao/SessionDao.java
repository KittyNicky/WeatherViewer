package com.kittynicky.app.dao;

import com.kittynicky.app.entity.Session;
import com.kittynicky.app.entity.User;
import com.kittynicky.app.util.HibernateSessionFactory;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SessionDao implements Dao<UUID, Session> {
    private static final SessionDao INSTANCE = new SessionDao();
    private static final String FROM_SESSION_BY_USER = "from Session " +
                                                       "where user = :user";
    private static final String DELETE_EXPIRES_SESSIONS = "delete from public.sessions " +
                                                          "where expires_at < :expired_at";
    private final SessionFactory sessionFactory = HibernateSessionFactory.getSessionFactory();

    public static SessionDao getInstance() {
        return INSTANCE;
    }


    public Optional<Session> findByUser(User user) {
        try (org.hibernate.Session openSession = sessionFactory.openSession()) {
            Query<Session> query = openSession.createQuery(FROM_SESSION_BY_USER, Session.class)
                    .setParameter("user", user);
            try {
                return Optional.of(query.getSingleResult());
            } catch (NoResultException e) {
                return Optional.empty();
            }
        }
    }

    @Override
    public Optional<Session> findById(UUID id) {
        try (org.hibernate.Session openSession = sessionFactory.openSession()) {
            return Optional.ofNullable(openSession.get(Session.class, id));
        }
    }

    @Override
    public UUID save(Session session) {
        EntityManager entityManager = sessionFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(session);
            entityManager.flush();
            transaction.commit();
        } catch (EntityExistsException e) {
            update(session);
        } catch (Exception e) {
            transaction.rollback();
            throw new RuntimeException(e);
        }
        return session.getId();
    }

    @Override
    public UUID update(Session session) {
        EntityManager entityManager = sessionFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(session);
            entityManager.flush();
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw new RuntimeException(e);
        }
        return session.getId();
    }

    @Override
    public UUID delete(Session session) {
        EntityManager entityManager = sessionFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.remove(session);
            entityManager.flush();
            transaction.commit();
        }
        catch (Exception e) {
            transaction.rollback();
            throw new RuntimeException(e);
        }
        return session.getId();
    }

    public void removeAllExpiredSessions(LocalDateTime expired_at) {
        try (org.hibernate.Session openSession = sessionFactory.openSession()) {
            Transaction transaction = openSession.getTransaction();
            transaction.begin();
            openSession.createNativeQuery(DELETE_EXPIRES_SESSIONS)
                    .setParameter("expired_at", Timestamp.valueOf(expired_at))
                    .executeUpdate();
            transaction.commit();
        }
    }
}