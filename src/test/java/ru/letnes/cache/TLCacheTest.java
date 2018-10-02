package ru.letnes.cache;

import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.letnes.cache.strategy.CacheStrategy;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.*;

public class TLCacheTest {
    private static final String KEY = "KEY";
    private static final String VALUE = "VALUE";
    private static final int DEF_CAPACITY = 10;
    private TLCache<String, String> cache;
    @Mock
    private CacheStrategy<String, String> fileCache;
    @Mock
    private CacheStrategy<String, String> ramCache;
    @Mock
    private TLCacheBuilder tlCacheBuilder;

    @BeforeMethod
    public void setUp() throws IOException {
        initMocks(this);
        when(tlCacheBuilder.getFileCache()).thenReturn(fileCache);
        when(tlCacheBuilder.getRamCache()).thenReturn(ramCache);
        when(tlCacheBuilder.getFileCapacity()).thenReturn(DEF_CAPACITY);
        when(tlCacheBuilder.getRamCapacity()).thenReturn(DEF_CAPACITY);
        cache = spy(new TLCache<>(tlCacheBuilder));
    }

    @Test
    public void testPut() throws IOException, ClassNotFoundException {
        doNothing().when(cache).releaseCache();

        cache.put(KEY, VALUE);

        verify(cache, times(1)).releaseCache();
        verify(ramCache, times(1)).put(KEY, VALUE);
    }

    @Test
    public void testGet() throws IOException, ClassNotFoundException {
        when(ramCache.get(KEY)).thenReturn(VALUE);

        String value = cache.get(KEY);

        verify(ramCache, times(1)).get(KEY);
        assertEquals(value, VALUE);
    }

    @Test
    public void testGetIfValueInFileCache() throws IOException, ClassNotFoundException {
        when(fileCache.containsKey(KEY)).thenReturn(true);
        when(fileCache.remove(KEY)).thenReturn(VALUE);
        doNothing().when(cache).put(KEY, VALUE);

        String value = cache.get(KEY);

        verify(ramCache, times(1)).get(KEY);
        verify(fileCache, times(1)).containsKey(KEY);
        verify(fileCache, times(1)).remove(KEY);
        verify(cache, times(1)).put(KEY, VALUE);
        assertEquals(value, VALUE);
    }

    @Test
    public void testClearCache() throws IOException {
        doNothing().when(ramCache).clearCache();
        doNothing().when(fileCache).clearCache();

        cache.clearCache();

        verify(ramCache, times(1)).clearCache();
        verify(fileCache, times(1)).clearCache();
    }

    @Test
    public void testRemove() throws IOException, ClassNotFoundException {
        doReturn(VALUE).when(ramCache).remove(KEY);
        doReturn(VALUE).when(fileCache).remove(KEY);

        String removeItem = cache.remove(KEY);

        verify(ramCache, times(1)).remove(KEY);
        verify(fileCache, times(1)).remove(KEY);
        assertEquals(removeItem, VALUE);
    }

    @Test
    public void testContainsKeyInRamAndFile() {
        doReturn(true).when(ramCache).containsKey(KEY);
        doReturn(true).when(fileCache).containsKey(KEY);

        boolean isContains = cache.containsKey(KEY);

        verify(ramCache, times(1)).containsKey(KEY);
        assertTrue(isContains);
    }

    @Test
    public void testContainsKeyInFile() {
        doReturn(false).when(ramCache).containsKey(KEY);
        doReturn(true).when(fileCache).containsKey(KEY);

        boolean isContains = cache.containsKey(KEY);

        verify(ramCache, times(1)).containsKey(KEY);
        verify(fileCache, times(1)).containsKey(KEY);
        assertTrue(isContains);
    }

    @Test
    public void testContainsKeyInRam() {
        doReturn(true).when(ramCache).containsKey(KEY);
        doReturn(false).when(fileCache).containsKey(KEY);

        boolean isContains = cache.containsKey(KEY);

        verify(ramCache, times(1)).containsKey(KEY);
        assertTrue(isContains);
    }

    @Test
    public void testContainsKeyIfKeyNotContains() {
        doReturn(false).when(ramCache).containsKey(KEY);
        doReturn(false).when(fileCache).containsKey(KEY);

        boolean isContains = cache.containsKey(KEY);

        verify(ramCache, times(1)).containsKey(KEY);
        verify(fileCache, times(1)).containsKey(KEY);
        assertFalse(isContains);
    }

    @Test
    public void testSize() {
        int ramSize = 0;
        doReturn(ramSize).when(ramCache).size();

        int size = cache.size();

        verify(ramCache, times(1)).size();
        assertEquals(size, ramSize);
    }

    @Test
    public void testReleaseCache() throws IOException, ClassNotFoundException {
        final int[] ramSize = {20};
        final int[] fileSize = {20};
        when(ramCache.size()).thenReturn(ramSize[0]);
        when(ramCache.getDisplasedElement()).thenReturn(KEY);
        when(ramCache.remove(KEY)).thenAnswer((Answer<String>) invocationOnMock -> {
            when(ramCache.size()).thenReturn(--ramSize[0]);
            return VALUE;
        });
        when(fileCache.size()).thenReturn(fileSize[0]);
        when(fileCache.getDisplasedElement()).thenReturn(KEY);
        when(fileCache.remove(KEY)).thenAnswer((Answer<String>) invocationOnMock -> {
            when(fileCache.size()).thenReturn(--fileSize[0]);
            return VALUE;
        });
        doNothing().when(fileCache).put(KEY, VALUE);

        cache.releaseCache();

        verify(ramCache, atLeastOnce()).size();
        verify(ramCache, atLeastOnce()).getDisplasedElement();
        verify(ramCache, atLeastOnce()).remove(KEY);
        verify(fileCache, atLeastOnce()).size();
        verify(fileCache, atLeastOnce()).getDisplasedElement();
        verify(fileCache, atLeastOnce()).remove(KEY);
        verify(fileCache, atLeastOnce()).put(KEY, VALUE);
    }

    @Test
    public void testClose() throws IOException {
        doNothing().when(ramCache).close();
        doNothing().when(fileCache).close();

        cache.close();

        verify(ramCache, times(1)).close();
        verify(fileCache, times(1)).close();
    }
}