package ru.letnes;

import ru.letnes.cache.ICache;
import ru.letnes.cache.TLCacheBuilder;
import ru.letnes.cache.filesUtil.FilesImpl;
import ru.letnes.cache.strategy.LRUStrategy;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        try (ICache<String, String> cache = TLCacheBuilder.<String, String> newBuilder()
                .setRamCapacity(10)
                .setFileCapacity(10)
                .setFileCacheStrategy(new LRUStrategy<>())
                .setRamCacheStrategy(new LRUStrategy<>())
                .setFileImpl(new FilesImpl())
                .build()) {
            for (int i = 0; i < 22; i++) {
                String s = String.valueOf(i);
                cache.put(s, s);
            }
            for (int i = 0; i < 22; i++) {
                logger.log(Level.INFO, cache.get(String.valueOf(i)));
            }
            for (int i = 0; i < 22; i++) {
                String val = cache.get(String.valueOf(i));
                if (val == null) {
                    String s = String.valueOf(i);
                    cache.put(s, s);
                    logger.log(Level.INFO, s);
                } else {
                    logger.log(Level.INFO, val);
                }
            }
            logger.log(Level.INFO, "Final");
        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.INFO, e.getMessage(), e);
        }
    }
}