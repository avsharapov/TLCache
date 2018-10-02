package ru.letnes.cache;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class RamCacheTest {
    private static final String KEY = "KEY";
    private static final String VALUE = "VALUE";
    private RamCache<String, String> cache;

    @BeforeMethod
    public void setUp() {
        cache = new RamCache<>();
        cache.cacheMap = spy(cache.cacheMap);
    }

    @Test
    public void testPut() {
        cache.put(KEY, VALUE);

        verify(cache.cacheMap, times(1)).put(KEY, VALUE);
        assertEquals(cache.cacheMap.get(KEY), VALUE);
    }

    @Test
    public void testGet() {
        cache.cacheMap.put(KEY, VALUE);

        String value = cache.get(KEY);

        verify(cache.cacheMap, times(1)).get(KEY);
        assertEquals(value, VALUE);
    }

    @Test
    public void testClearCache() {
        cache.clearCache();

        verify(cache.cacheMap, times(1)).clear();
    }

    @Test
    public void testRemove() {
        cache.cacheMap.put(KEY, VALUE);

        String remove = cache.remove(KEY);

        verify(cache.cacheMap, times(1)).remove(KEY);
        assertEquals(remove, VALUE);
    }

    @Test
    public void testContainsKey() {
        cache.cacheMap.put(KEY, VALUE);

        boolean valueIsContains = cache.containsKey(KEY);

        verify(cache.cacheMap, times(1)).containsKey(KEY);
        assertTrue(valueIsContains);
    }

    @Test
    public void testSize() {
        cache.size();

        verify(cache.cacheMap, times(1)).size();
    }

    @Test
    public void testClose() {
        cache.close();

        verify(cache.cacheMap, times(1)).clear();
    }
}