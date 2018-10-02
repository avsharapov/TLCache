package ru.letnes.cache;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Реализация кеша уровня оперативной памяти.
 * Элементы кеш хранятся в коллекции ConcurrentHashMap.
 *
 * @param <K> тип ключа
 * @param <V> тип значения
 * @author avsharapov
 */
public class RamCache<K, V extends Serializable> implements ICache<K, V> {
    ConcurrentMap<K, V> cacheMap;

    RamCache() {
        this.cacheMap = new ConcurrentHashMap<>();
    }

    @Override
    public void put(K key, V value) {
        this.cacheMap.put(key, value);
    }

    @Override
    public V get(K key) {
        return this.cacheMap.get(key);
    }

    @Override
    public void clearCache() {
        this.cacheMap.clear();
    }

    @Override
    public V remove(K key) {
        return this.cacheMap.remove(key);
    }

    @Override
    public boolean containsKey(K key) {
        return this.cacheMap.containsKey(key);
    }

    @Override
    public int size() {
        return this.cacheMap.size();
    }

    @Override
    public void close() {
        cacheMap.clear();
    }
}
