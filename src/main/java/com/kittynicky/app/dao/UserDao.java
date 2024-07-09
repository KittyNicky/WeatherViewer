package com.kittynicky.app.dao;

import com.kittynicky.app.entity.User;
import com.kittynicky.app.util.HibernateSessionFactory;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDao implements Dao<Integer, User> {
    private static final UserDao INSTANCE = new UserDao();
    public static final String FROM_USER_BY_LOGIN = "from User where login =: login";
    private final SessionFactory sessionFactory = HibernateSessionFactory.getSessionFactory();

    public static UserDao getInstance() {
        return INSTANCE;
    }

    @Override
    public Optional<User> findById(Integer id) {
        try (Session openSession = sessionFactory.openSession()) {
            return Optional.ofNullable(openSession.get(User.class, id));
        }
    }

    public Optional<User> findByLogin(String login) {
        try (Session openSession = sessionFactory.openSession()) {
            Query<User> query = openSession.createQuery(FROM_USER_BY_LOGIN, User.class)
                    .setParameter("login", login);
            try {
                return Optional.of(query.getSingleResult());
            } catch (NoResultException e) {
                return Optional.empty();
            }
        }
    }

    @Override
    public Integer save(User user) {
        EntityManager entityManager = sessionFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(user);
            entityManager.flush();
            transaction.commit();
        } catch (EntityExistsException | ConstraintViolationException e) {
            transaction.rollback();
            throw new EntityExistsException("User '" + user.getLogin() + "' already exists");
        } catch (Exception e) {
            transaction.rollback();
            throw new RuntimeException(e);
        }
        return user.getId();
    }

    @Override
    public Integer delete(User user) {
        return 0;
    }

    @Override
    public Integer update(User entity) {
        return 0;
    }
}
