package ru.letnes.cache;


import ru.letnes.cache.filesutil.FilesImpl;
import ru.letnes.cache.filesutil.IFiles;
import ru.letnes.cache.strategy.CacheStrategy;
import ru.letnes.cache.strategy.LRUStrategy;

import java.io.IOException;
import java.io.Serializable;

/**
 * Билдер, позволяющий создать и сконфигурировать объект кеша.
 * Можно подставлять собственные реализации кешей и стретегий обеих уровней, а так же
 * задать объект-реализацию работы с файловой системой и  емкости кешей обоих уровней.
 *
 * @param <K> тип ключа для кешей обеих уровней
 * @param <V> тип значения для кешей обеих уровней
 * @author avsharapov
 */
public class TLCacheBuilder<K, V extends Serializable> {
    private static final int DEF_CAPACITY = 10;
    private int ramCapacity;
    private ICache<K, V> ramCache;
    private int fileCapacity;
    private ICache<K, V> fileCache;
    private CacheStrategy<K, V> fileCacheStrategy;
    private CacheStrategy<K, V> ramCacheStrategy;
    private IFiles fileImpl;

    private TLCacheBuilder() {
        this.ramCapacity = DEF_CAPACITY;
        this.fileCapacity = DEF_CAPACITY;
    }


    public static <K, V extends Serializable> TLCacheBuilder newBuilder() {
        return new TLCacheBuilder<K, V>();
    }

    int getRamCapacity() {
        return ramCapacity;
    }

    int getFileCapacity() {
        return fileCapacity;
    }

    public TLCacheBuilder<K, V> setRamCapacity(int ramCapacity) {
        this.ramCapacity = ramCapacity;
        return this;
    }

    public TLCacheBuilder<K, V> setFileCapacity(int fileCapacity) {
        this.fileCapacity = fileCapacity;
        return this;
    }

    public TLCacheBuilder<K, V> setFileCacheStrategy(CacheStrategy<K, V> fileCacheStrategy) {
        this.fileCacheStrategy = fileCacheStrategy;
        return this;
    }

    public TLCacheBuilder<K, V> setRamCacheStrategy(CacheStrategy<K, V> ramCacheStrategy) {
        this.ramCacheStrategy = ramCacheStrategy;
        return this;
    }

    public TLCacheBuilder<K, V> setFileCache(ICache<K, V> fileCache) {
        this.fileCache = fileCache;
        return this;
    }

    public TLCacheBuilder<K, V> setRamCache(ICache<K, V> ramCache) {
        this.ramCache = ramCache;
        return this;
    }

    public TLCacheBuilder<K, V> setFileImpl(IFiles fileImpl) {
        this.fileImpl = fileImpl;
        return this;
    }

    CacheStrategy<K, V> getRamCache() {
        if (ramCache == null)
            ramCache = new RamCache<>();
        if (ramCacheStrategy == null)
            ramCacheStrategy = new LRUStrategy<>();
        ramCacheStrategy.setCache(ramCache);
        return ramCacheStrategy;
    }

    CacheStrategy<K, V> getFileCache() throws IOException {
        if (fileCache == null)
            fileCache = new FilesCache<>(getFileImpl());
        if (fileCacheStrategy == null)
            fileCacheStrategy = new LRUStrategy<>();
        fileCacheStrategy.setCache(fileCache);
        return fileCacheStrategy;
    }

    IFiles getFileImpl() {
        return fileImpl == null ? new FilesImpl() : fileImpl;
    }

    public ICache<K, V> build() throws IOException {
        return new TLCache<>(this);
    }
}
