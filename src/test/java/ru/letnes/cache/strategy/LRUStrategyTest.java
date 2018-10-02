package ru.letnes.cache.strategy;

import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.letnes.cache.ICache;

import java.util.Date;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.*;

public class LRUStrategyTest {
    private static final String KEY = "KEY";
    private static final String KEY2 = "KEY2";
    private static final Date DATE = new Date();
    private AbstractStrategy<String, String, Date> lruStrategy;
    @Mock
    private ICache<String, String> cache;
    @BeforeMethod
    public void setUp() {
        initMocks(this);
        lruStrategy = new LRUStrategy<>();
        lruStrategy.setCache(cache);
    }

    @Test
    public void testGetDefaultAttributeValue() {
        Date defaultAttributeValue = lruStrategy.getDefaultAttributeValue();

        assertTrue(new Date().getTime() >= defaultAttributeValue.getTime());
    }

    @Test
    public void testChangeAttributeValue() {
        lruStrategy.attributeMap.put(KEY, DATE);
        Date date = lruStrategy.changeAttributeValue(KEY);

        assertEquals(date.getTime(), DATE.getTime());
    }

    @Test
    public void testRemoveAttributeValue() {
        lruStrategy.attributeMap.put(KEY, DATE);
        lruStrategy.removeAttributeValue(KEY);

        assertNull(lruStrategy.attributeMap.get(KEY));
    }

    @Test
    public void testGetDisplasedElement() {
        lruStrategy.attributeMap.put(KEY, DATE);
        lruStrategy.attributeMap.put(KEY2, new Date());
        String displasedElement = lruStrategy.getDisplasedElement();

        assertEquals(displasedElement, KEY);
    }
}