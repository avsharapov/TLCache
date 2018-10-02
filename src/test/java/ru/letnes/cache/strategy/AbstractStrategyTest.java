package ru.letnes.cache.strategy;

import ru.letnes.cache.ICache;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.*;

public class AbstractStrategyTest {
    private static final String KEY = "KEY";
    private static final String VALUE = "VALUE";
    private AbstractStrategy<String, String, Object> abstractStrategy;
    @Mock
    private ICache<String, String> cache;

    @SuppressWarnings("unchecked")
    @BeforeMethod
    public void setUp() {
        initMocks(this);
        abstractStrategy = mock(AbstractStrategy.class, withSettings().useConstructor().defaultAnswer(CALLS_REAL_METHODS));
        abstractStrategy.setCache(cache);
        when(abstractStrategy.getDefaultAttributeValue()).thenReturn(new Object());
    }

    @Test
    public void testPut() throws IOException, ClassNotFoundException {
        abstractStrategy.put(KEY, VALUE);

        verify(cache, times(1)).put(KEY, VALUE);
        assertNotNull(abstractStrategy.attributeMap.get(KEY));
    }

    @Test
    public void testGet() throws IOException, ClassNotFoundException {
        when(cache.get(KEY)).thenReturn(VALUE);
        abstractStrategy.get(KEY);

        verify(cache, times(1)).get(KEY);
        verify(abstractStrategy, times(1)).changeAttributeValue(KEY);
    }

    @Test
    public void testClearCache() throws IOException {
        abstractStrategy.clearCache();

        verify(cache, times(1)).clearCache();
        assertEquals(abstractStrategy.attributeMap.size(), 0);
    }

    @Test
    public void testRemove() throws IOException, ClassNotFoundException {
        abstractStrategy.remove(KEY);

        verify(cache, times(1)).remove(KEY);
        verify(abstractStrategy, times(1)).removeAttributeValue(KEY);
    }

    @Test
    public void testSize() {
        abstractStrategy.size();

        verify(cache, times(1)).size();
    }

    @Test
    public void testContainsKey() {
        abstractStrategy.containsKey(KEY);

        verify(cache, times(1)).containsKey(KEY);
    }

    @Test
    public void testClose() throws IOException {
        abstractStrategy.close();

        verify(cache, times(1)).close();
    }
}