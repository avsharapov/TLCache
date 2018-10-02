package ru.letnes.cache.strategy;

import ru.letnes.cache.ICache;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Базовый класс реализующий основные операции стратегии с кешем.
 *
 * @param <K> тип ключа в кеше
 * @param <V> тип значения в кеше
 * @param <P> тип аттрибута требуемого для реализации стратегии вытеснения объектов в кеше
 * @author avsharapov
 */
public abstract class AbstractStrategy<K, V extends Serializable, P> implements CacheStrategy<K, V> {
    ConcurrentHashMap<K, P> attributeMap;
    private ICache<K, V> cache;

    AbstractStrategy() {
        this.attributeMap = new ConcurrentHashMap<>();
    }

    @Override
    public void setCache(ICache<K, V> cache) {
        this.cache = cache;
    }

    @Override
    public void put(K key, V value) throws IOException, ClassNotFoundException {
        this.attributeMap.put(key, getDefaultAttributeValue());
        this.cache.put(key, value);
    }

    @Override
    public V get(K key) throws IOException, ClassNotFoundException {
        changeAttributeValue(key);
        return this.cache.get(key);
    }

    @Override
    public void clearCache() throws IOException {
        this.cache.clearCache();
        this.attributeMap.clear();
    }

    @Override
    public V remove(K key) throws IOException, ClassNotFoundException {
        removeAttributeValue(key);
        return this.cache.remove(key);
    }

    @Override
    public int size() {
        return this.cache.size();
    }

    @Override
    public boolean containsKey(K key) {
        return this.cache.containsKey(key);
    }

    protected abstract P getDefaultAttributeValue();

    protected abstract P changeAttributeValue(K key);

    protected abstract P removeAttributeValue(K key);

    @Override
    public void close() throws IOException {
        this.cache.close();
    }

}
