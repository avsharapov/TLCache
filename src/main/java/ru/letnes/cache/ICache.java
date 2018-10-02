package ru.letnes.cache;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;

/**
 * Базовый интерфейс кеша
 *
 * @param <K>
 * @param <V>
 */
public interface ICache<K, V extends Serializable> extends Closeable {
    void put(K key, V value) throws IOException, ClassNotFoundException;

    V get(K key) throws IOException, ClassNotFoundException;

    void clearCache() throws IOException;

    V remove(K key) throws IOException, ClassNotFoundException;

    boolean containsKey(K key);

    int size();
}
