package ru.letnes.cache.strategy;

import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.letnes.cache.ICache;

import java.util.Date;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.*;

public class MRUStrategyTest {
    private static final String KEY = "KEY";
    private static final String KEY2 = "KEY2";
    private static final Date DATE = new Date();
    private AbstractStrategy<String, String, Date> mruStrategy;
    @Mock
    private ICache<String, String> cache;
    @BeforeMethod
    public void setUp() {
        initMocks(this);
        mruStrategy = new MRUStrategy<>();
        mruStrategy.setCache(cache);
    }

    @Test
    public void testGetDefaultAttributeValue() {
        Date defaultAttributeValue = mruStrategy.getDefaultAttributeValue();

        assertTrue(new Date().getTime() >= defaultAttributeValue.getTime());
    }

    @Test
    public void testChangeAttributeValue() {
        mruStrategy.attributeMap.put(KEY, DATE);
        Date date = mruStrategy.changeAttributeValue(KEY);

        assertEquals(date.getTime(), DATE.getTime());
    }

    @Test
    public void testRemoveAttributeValue() {
        mruStrategy.attributeMap.put(KEY, DATE);
        mruStrategy.removeAttributeValue(KEY);

        assertNull(mruStrategy.attributeMap.get(KEY));
    }

    @Test
    public void testGetDisplasedElement() {
        mruStrategy.attributeMap.put(KEY, DATE);
        mruStrategy.attributeMap.put(KEY2, new Date());
        String displasedElement = mruStrategy.getDisplasedElement();

        assertEquals(displasedElement, KEY2);
    }
}