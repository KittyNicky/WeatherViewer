package com.kittynicky.app.util;

import com.kittynicky.app.entity.Location;
import com.kittynicky.app.entity.Session;
import com.kittynicky.app.entity.User;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

@UtilityClass
public class HibernateSessionFactory {
    @Getter
    private static final SessionFactory sessionFactory;

    static {
        try {
            Configuration configuration = new Configuration().configure()
                    .addAnnotatedClass(Location.class)
                    .addAnnotatedClass(Session.class)
                    .addAnnotatedClass(User.class);
            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties());
            sessionFactory = configuration.buildSessionFactory(builder.build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}