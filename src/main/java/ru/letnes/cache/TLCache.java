package ru.letnes.cache;

import ru.letnes.cache.strategy.CacheStrategy;

import java.io.IOException;
import java.io.Serializable;

/**
 * Класс кеша, организующий работу двух уровней кеширования с заданными стратегиями.
 *
 * @param <K> тип ключа для кешей обеих уровней
 * @param <V> тип значения для кешей обеих уровней
 * @author avsharapov
 */
class TLCache<K, V extends Serializable> implements ICache<K, V> {
    private final CacheStrategy<K, V> ramCache;
    private final CacheStrategy<K, V> fileCache;
    private final int ramCapacity;
    private final int fileCapacity;

    TLCache(TLCacheBuilder<K, V> builder) throws IOException {
        this.ramCapacity = builder.getRamCapacity();
        this.fileCapacity = builder.getFileCapacity();
        this.ramCache = builder.getRamCache();
        this.fileCache = builder.getFileCache();
    }

    @Override
    public void put(K key, V value) throws IOException, ClassNotFoundException {
        releaseCache();
        ramCache.put(key, value);
    }

    @Override
    public V get(K key) throws IOException, ClassNotFoundException {
        V value = ramCache.get(key);
        if (value == null && fileCache.containsKey(key)) {
            value = fileCache.remove(key);
            put(key, value);
        }
        return value;
    }

    @Override
    public void clearCache() throws IOException {
        fileCache.clearCache();
        ramCache.clearCache();
    }

    @Override
    public V remove(K key) throws IOException, ClassNotFoundException {
        fileCache.remove(key);
        return ramCache.remove(key);
    }

    @Override
    public boolean containsKey(K key) {
        return ramCache.containsKey(key) || fileCache.containsKey(key);
    }

    @Override
    public int size() {
        return ramCache.size();
    }


    protected void releaseCache() throws IOException, ClassNotFoundException {
        while (ramCache.size() >= ramCapacity) {
            K displasedElementKey = ramCache.getDisplasedElement();
            V displasedElementValue = ramCache.remove(displasedElementKey);
            while (fileCache.size() >= fileCapacity) {
                fileCache.remove(fileCache.getDisplasedElement());
            }
            fileCache.put(displasedElementKey, displasedElementValue);
        }

    }

    @Override
    public void close() throws IOException {
        fileCache.close();
        ramCache.close();
    }
}
