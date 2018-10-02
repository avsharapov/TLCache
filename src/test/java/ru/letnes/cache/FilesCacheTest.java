package ru.letnes.cache;

import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.letnes.cache.filesUtil.IFiles;

import java.io.*;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class FilesCacheTest {
    private static final String KEY = "KEY";
    private static final String VALUE = "VALUE";
    private static final String PATH_VALUE = "PATH_VALUE";
    private static final String SEPARATOR = "/";
    private static final String UUID = "12345";
    private FilesCache<String, String> cache;
    @Mock
    private IFiles mockFiles;
    @Mock
    private Path mockCacheDir;
    @Mock
    private Path mockPath;
    @Mock
    private OutputStream mockOutputStream;
    @Mock
    private ObjectOutput mockObjectOutputStream;
    @Mock
    private InputStream mockInputStream;
    @Mock
    private ObjectInput mockObjectInputStream;

    @BeforeMethod
    public void setUp() throws IOException {
        initMocks(this);
        when(mockFiles.createTempDir(anyString())).thenReturn(mockCacheDir);
        when(mockCacheDir.toString()).thenReturn("mockCacheDir");
        cache = spy(new FilesCache<>(mockFiles));
        cache.cacheMap = spy(cache.cacheMap);
    }

    @Test
    public void testPut() throws IOException {
        when(mockFiles.getDefaultFileSystemSeparator()).thenReturn(SEPARATOR);
        when(mockFiles.getRandomUUID()).thenReturn(UUID);
        when(mockFiles.getPath(mockCacheDir.toString() + SEPARATOR + UUID + ".temp")).thenReturn(mockPath);
        when(mockPath.toString()).thenReturn(PATH_VALUE);
        when(mockFiles.getOutputStream(mockPath)).thenReturn(mockOutputStream);
        when(mockFiles.getObjectOutputStream(mockOutputStream)).thenReturn(mockObjectOutputStream);
        doNothing().when(mockObjectOutputStream).writeObject(VALUE);

        cache.put(KEY, VALUE);

        verify(mockObjectOutputStream, times(1)).writeObject(VALUE);
        verify(cache.cacheMap, times(1)).put(KEY, PATH_VALUE);
        assertEquals(PATH_VALUE, cache.cacheMap.get(KEY));
    }

    @Test
    public void testGet() throws IOException, ClassNotFoundException {
        cache.cacheMap.put(KEY, PATH_VALUE);
        when(mockFiles.getPath(PATH_VALUE)).thenReturn(mockPath);
        when(mockFiles.getInputStream(mockPath)).thenReturn(mockInputStream);
        when(mockFiles.getObjectInputStream(mockInputStream)).thenReturn(mockObjectInputStream);
        when(mockObjectInputStream.readObject()).thenReturn(VALUE);

        String s = cache.get(KEY);

        verify(mockObjectInputStream, times(1)).readObject();
        assertEquals(s, VALUE);
    }

    @Test
    public void testGetIfKeyNotExist() throws IOException, ClassNotFoundException {
        when(mockFiles.getPath(PATH_VALUE)).thenReturn(mockPath);

        String s = cache.get(KEY);

        assertNull(s);
    }

    @Test
    public void testClearCache() throws IOException {
        when(mockFiles.getPath(mockCacheDir)).thenReturn(mockPath);
        Path p1 = mock(Path.class);
        File f1 = mock(File.class);
        when(p1.toFile()).thenReturn(f1);
        when(f1.delete()).thenReturn(true);
        Path p2 = mock(Path.class);
        File f2 = mock(File.class);
        when(p2.toFile()).thenReturn(f2);
        when(f2.delete()).thenReturn(true);
        Path p3 = mock(Path.class);
        File f3 = mock(File.class);
        when(p3.toFile()).thenReturn(f3);
        when(f3.delete()).thenReturn(true);
        Path p4 = mock(Path.class);
        File f4 = mock(File.class);
        when(p4.toFile()).thenReturn(f4);
        when(f4.delete()).thenReturn(true);
        when(mockFiles.getPathStream(mockPath)).thenReturn(Stream.of(p1, p2, p3, p4));

        cache.clearCache();

        verify(p1, times(0)).toFile();
        verify(p2, times(1)).toFile();
        verify(p3, times(1)).toFile();
        verify(p4, times(1)).toFile();
        verify(f1, times(0)).delete();
        verify(f2, times(1)).delete();
        verify(f3, times(1)).delete();
        verify(f4, times(1)).delete();
        verify(cache.cacheMap, times(1)).clear();
        assertEquals(0, cache.cacheMap.size());
    }

    @Test
    public void testRemove() throws IOException, ClassNotFoundException {
        cache.cacheMap.put(KEY, PATH_VALUE);
        when(mockFiles.getPath(PATH_VALUE)).thenReturn(mockPath);
        when(mockFiles.getInputStream(mockPath)).thenReturn(mockInputStream);
        when(mockFiles.getObjectInputStream(mockInputStream)).thenReturn(mockObjectInputStream);
        when(mockObjectInputStream.readObject()).thenReturn(VALUE);
        when(mockFiles.deleteIfExist(mockPath)).thenReturn(true);
        String remoteItem = cache.remove(KEY);

        assertEquals(remoteItem, VALUE);
        verify(mockObjectInputStream, times(1)).readObject();
        verify(mockFiles, times(1)).deleteIfExist(mockPath);
    }

    @Test
    public void testContainsKey() {
        boolean b = cache.containsKey(KEY);

        boolean b1 = verify(cache.cacheMap, times(1)).containsKey(KEY);
        assertEquals(b1, b);
    }

    @Test
    public void testSize() {
        int size = cache.size();

        int size1 = verify(cache.cacheMap, times(1)).size();
        assertEquals(size1, size);
    }

    @Test
    public void testClose() throws IOException {
        when(mockFiles.getPath(mockCacheDir)).thenReturn(mockPath);
        when(mockFiles.deleteIfExist(mockPath)).thenReturn(true);

        cache.close();

        verify(cache, times(1)).clearCache();
        verify(mockFiles, times(1)).deleteIfExist(mockPath);
    }
}