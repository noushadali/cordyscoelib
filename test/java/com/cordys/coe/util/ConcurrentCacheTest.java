/**
 * (c) 2008 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util;

import junit.framework.TestCase;

/**
 * Test cases for ConcurrentCache.
 *
 * @author mpoyhone
 */
public class ConcurrentCacheTest extends TestCase
{
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
     * Test method for {@link com.cordys.coe.util.ConcurrentCache#put(java.lang.Object, java.lang.Object)}.
     */
    public void testPut()
    {
        String key = "key123";
        String value = "value123";
        ConcurrentCache<String, String> cache = new ConcurrentCache<String, String>();
        
        assertNull(cache.peek(key));
        cache.put(key, value);
        assertEquals(value, cache.peek(key));
        
        cache.put(key, null);
        assertNull(cache.peek(key));
    }

    /**
     * Test method for {@link com.cordys.coe.util.ConcurrentCache#get(java.lang.Object, com.cordys.coe.util.ConcurrentCache.IValueLoader, java.lang.Object[])}.
     */
    public void testGet()
    {
        final String key = "key123";
        final String value = "value123";
        ConcurrentCache<String, String> cache = new ConcurrentCache<String, String>();
        ConcurrentCache.IValueLoader<String> loader = new ConcurrentCache.IValueLoader<String>() {
            public String loadEntry(Object... args)
            {
                assertNotNull(args);
                assertEquals(1, args.length);
                assertEquals(key, args[0]);
                return value;
            }
        };
        
        assertEquals(value, cache.get(key, loader, key));
        assertEquals(value, cache.peek(key));
    }

    /**
     * Test method for {@link com.cordys.coe.util.ConcurrentCache#remove(java.lang.Object)}.
     */
    public void testRemove()
    {
        String key1 = "key123";
        String value1 = "value123";
        String key2 = "key456";
        String value2 = "value456";
        ConcurrentCache<String, String> cache = new ConcurrentCache<String, String>();
        
        cache.put(key1, value1);
        cache.put(key2, value2);
        assertEquals(value1, cache.peek(key1));
        assertEquals(value2, cache.peek(key2));
        cache.remove(key2);
        assertEquals(value1, cache.peek(key1));
        assertNull(cache.peek(key2));
    }

    /**
     * Test method for {@link com.cordys.coe.util.ConcurrentCache#clear()}.
     */
    public void testClear()
    {
        String key1 = "key123";
        String value1 = "value123";
        String key2 = "key456";
        String value2 = "value456";
        ConcurrentCache<String, String> cache = new ConcurrentCache<String, String>();
        
        cache.put(key1, value1);
        cache.put(key2, value2);
        assertEquals(value1, cache.peek(key1));
        assertEquals(value2, cache.peek(key2));
        cache.clear();
        assertNull(cache.peek(key1));
        assertNull(cache.peek(key2));
    }

}
