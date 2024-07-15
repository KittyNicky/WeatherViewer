package com.kittynicky.app.dao;

import java.util.Optional;

public interface Dao<K, T> {

    Optional<T> findById(K id);

    K save(T t);

    K update(T entity);

    K delete(T t);
}
