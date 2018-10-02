package ru.letnes.cache;

import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.letnes.cache.filesUtil.IFiles;
import ru.letnes.cache.strategy.CacheStrategy;

import java.io.IOException;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class TLCacheBuilderTest {
    private static final int DEF_CAPACITY = 10;
    private static final int NEW_CAPACITY = 15;
    private TLCacheBuilder<String, String> cacheBuilder;
    @Mock
    private ICache<String, String> mockFileCache;
    @Mock
    private ICache<String, String> mockRamCache;
    @Mock
    private CacheStrategy<String, String> mockFileCacheStrategy;
    @Mock
    private CacheStrategy<String, String> mockRamCacheStrategy;
    @Mock
    private IFiles mockFileImpl;

    @BeforeMethod
    public void setUp() {
        initMocks(this);
        cacheBuilder = spy(TLCacheBuilder.newBuilder());
    }

    @Test
    public void testNewBuilder() {
        TLCacheBuilder<String, String> tlCacheBuilder = cacheBuilder.newBuilder();
        assertNotNull(tlCacheBuilder);
    }

    @Test
    public void testGetRamCapacity() {
        int ramCapacity = cacheBuilder.getRamCapacity();
        assertEquals(ramCapacity, DEF_CAPACITY);
    }

    @Test
    public void testGetFileCapacity() {
        int fileCapacity = cacheBuilder.getFileCapacity();
        assertEquals(fileCapacity, DEF_CAPACITY);
    }

    @Test
    public void testSetRamCapacity() {
        cacheBuilder.setRamCapacity(NEW_CAPACITY);
        assertEquals(cacheBuilder.getRamCapacity(), NEW_CAPACITY);
    }

    @Test
    public void testSetFileCapacity() {
        cacheBuilder.setFileCapacity(NEW_CAPACITY);
        assertEquals(cacheBuilder.getFileCapacity(), NEW_CAPACITY);
    }

    @Test
    public void testSetFileCache() {
        TLCacheBuilder<String, String> tlCacheBuilder = cacheBuilder.setFileCache(mockFileCache);
        assertNotNull(tlCacheBuilder);
    }

    @Test
    public void testSetRamCache() {
        TLCacheBuilder<String, String> tlCacheBuilder = cacheBuilder.setRamCache(mockRamCache);
        assertNotNull(tlCacheBuilder);
    }

    @Test
    public void testSetFileImpl() {
        TLCacheBuilder<String, String> tlCacheBuilder = cacheBuilder.setFileImpl(mockFileImpl);
        assertNotNull(tlCacheBuilder);
    }

    @Test
    public void testGetRamCache() {
        cacheBuilder.setRamCacheStrategy(mockRamCacheStrategy);
        CacheStrategy<String, String> ramCache = cacheBuilder.getRamCache();
        assertEquals(ramCache, mockRamCacheStrategy);
    }

    @Test
    public void testGetFileCache() throws IOException {
        cacheBuilder.setFileCache(mockFileCache);
        cacheBuilder.setFileCacheStrategy(mockFileCacheStrategy);
        CacheStrategy<String, String> fileCache = cacheBuilder.getFileCache();
        assertEquals(fileCache, mockFileCacheStrategy);
    }

    @Test
    public void testBuild() throws IOException {
        when(cacheBuilder.getFileImpl()).thenReturn(mockFileImpl);
        ICache<String, String> build = cacheBuilder.build();
        assertNotNull(build);
    }
}