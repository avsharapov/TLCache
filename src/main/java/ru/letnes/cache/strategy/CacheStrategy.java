package ru.letnes.cache.strategy;

import ru.letnes.cache.ICache;

import java.io.Serializable;

/**
 * Интерфейс для собственной реализации стратегии кеширования.
 *
 * @param <K>
 * @param <V>
 */
public interface CacheStrategy<K, V extends Serializable> extends ICache<K, V> {
    K getDisplasedElement();

    void setCache(ICache<K, V> cache);
}
