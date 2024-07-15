package com.kittynicky.app.dao;

import com.kittynicky.app.entity.Location;
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
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationDao implements Dao<Integer, Location> {
    private static final LocationDao INSTANCE = new LocationDao();
    private final SessionFactory sessionFactory = HibernateSessionFactory.getSessionFactory();

    private static final String FIND_BY_USER = "from Location where user = :user";
    private static final String FIND_BY_LATITUDE_AND_LONGITUDE = "from Location " + "where user = :user " + "and latitude = :latitude " + "and longitude = :longitude";


    public static LocationDao getInstance() {
        return INSTANCE;
    }

    @Override
    public Optional<Location> findById(Integer id) {
        try (Session openSession = sessionFactory.openSession()) {
            return Optional.ofNullable(openSession.get(Location.class, id));
        }
    }

    public List<Location> findByUser(User user) {
        try (Session openSession = sessionFactory.openSession()) {
            Query<Location> query = openSession.createQuery(FIND_BY_USER, Location.class).setParameter("user", user);
            return query.list();
        }
    }

    public Optional<Location> findByUserAndLatitudeAndLongitude(User user, BigDecimal latitude, BigDecimal longitude) {
        try (Session openSession = sessionFactory.openSession()) {
            Query<Location> query = openSession.createQuery(FIND_BY_LATITUDE_AND_LONGITUDE, Location.class).setParameter("user", user).setParameter("latitude", latitude).setParameter("longitude", longitude);
            try {
                return Optional.ofNullable(query.getSingleResult());
            } catch (NoResultException exception) {
                return Optional.empty();
            }
        }
    }

    @Override
    public Integer save(Location location) {
        EntityManager entityManager = sessionFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(location);
            entityManager.flush();
            transaction.commit();
        } catch (EntityExistsException e) {
            transaction.rollback();
            throw new EntityExistsException("Location already exists");
        }
        return location.getId();
    }

    @Override
    public Integer delete(Location location) {
        EntityManager entityManager = sessionFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.remove(location);
            entityManager.flush();
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw new RuntimeException(e);
        }
        return location.getId();
    }

    @Override
    public Integer update(Location location) {
        return 0;
    }
}
