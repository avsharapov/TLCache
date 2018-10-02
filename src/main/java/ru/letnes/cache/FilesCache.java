package ru.letnes.cache;

import ru.letnes.cache.filesUtil.IFiles;

import java.io.*;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

/**
 * Реализация кеша уровня файловой системы.
 * Хранит временные файлы в системной темповой директории,
 * создавая под файлы кеша поддиректорию с приставкой "tlcache".
 * Каждый элемент кеша и файл в котором он хранится именуется уникальным UUID.
 *
 * @param <K> тип ключа
 * @param <V> тип значения
 * @author avsharapov
 */
class FilesCache<K, V extends Serializable> implements ICache<K, V> {
    ConcurrentMap<K, String> cacheMap;
    private IFiles files;
    private Path cacheDir;

    FilesCache(IFiles files) throws IOException {
        this.cacheMap = new ConcurrentHashMap<>();
        this.files = files;
        this.cacheDir = files.createTempDir("tlcache");
    }

    @Override
    public void put(K key, V value) throws IOException {
        Path pathToObject = files.getPath(cacheDir.toString()
                + files.getDefaultFileSystemSeparator()
                + files.getRandomUUID()
                + ".temp");
        try (OutputStream stream = files.getOutputStream(pathToObject);
             ObjectOutput oos = files.getObjectOutputStream(stream)) {
            oos.writeObject(value);
            cacheMap.put(key, pathToObject.toString());
        }
    }

    @Override
    public V get(K key) throws IOException, ClassNotFoundException {
        if (cacheMap.containsKey(key)) {
            Path pathToObject = files.getPath(cacheMap.get(key));
            try (InputStream stream = files.getInputStream(pathToObject);
                 ObjectInput objectStream = files.getObjectInputStream(stream)) {
                return (V) objectStream.readObject();
            }
        }
        return null;
    }

    @Override
    public void clearCache() throws IOException {
        Path pathToObject = files.getPath(cacheDir);
        try (Stream<Path> pathStream = files.getPathStream(pathToObject)) {
            pathStream.skip(1)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
        cacheMap.clear();
    }

    @Override
    public V remove(K key) throws IOException, ClassNotFoundException {
        String remoteItem = cacheMap.remove(key);
        Path pathToObject = files.getPath(remoteItem);
        try (InputStream stream = files.getInputStream(pathToObject);
             ObjectInput objectStream = files.getObjectInputStream(stream)) {
            V v = (V) objectStream.readObject();
            files.deleteIfExist(pathToObject);
            return v;
        }
    }

    @Override
    public boolean containsKey(K key) {
        return cacheMap.containsKey(key);
    }

    @Override
    public int size() {
        return cacheMap.size();
    }

    @Override
    public void close() throws IOException {
        clearCache();
        files.deleteIfExist(files.getPath(cacheDir));
    }
}
