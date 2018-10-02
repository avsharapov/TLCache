package ru.letnes.cache.strategy;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Date;
import java.util.Map;

/**
 * Реализация стретегии MRU. В первую очередь вытесняется последний использованный элемент.
 *
 * @param <K> тип ключа в кеше
 * @param <V> тип значения в кеше
 * @author avsharapov
 */
public final class MRUStrategy<K, V extends Serializable> extends AbstractStrategy<K, V, Date> {

    @Override
    protected Date getDefaultAttributeValue() {
        return new Date();
    }

    @Override
    protected Date changeAttributeValue(K key) {
        return this.attributeMap.put(key, new Date());
    }

    @Override
    protected Date removeAttributeValue(K key) {
        return this.attributeMap.remove(key);
    }

    @Override
    public K getDisplasedElement() {
        return this.attributeMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(new AbstractMap.SimpleEntry<>(null, null))
                .getKey();
    }
}
