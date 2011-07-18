/**
 * (c) 2007 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util;

import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

/**
 * Test case for CollectionUtils class.
 *
 * @author mpoyhone
 */
@SuppressWarnings("unchecked")
public class CollectionUtilsTest extends TestCase
{
     /**
      * Test method for {@link com.cordys.coe.util.CollectionUtils#arrayToHashMap(java.lang.Object[][])}.
      */
     @SuppressWarnings("rawtypes")
	public void testPairsToHashMap()
     {
         ///////////////////////////////////////////////////
         // Test with simple string values.
         ///////////////////////////////////////////////////
         Pair[] a1 = {
                 new Pair("key1", "value1"),
                 new Pair("key2", "value2"),
                 new Pair("key3", "value3"),
             };
         Map<String, String> m1 = CollectionUtils.pairsToHashMap(a1);
         
         assertTrue(m1 != null);
         assertEquals(a1.length, m1.size());
         
         for (Pair o : a1)
         {
             assertTrue("Key: '" + o.getFirst() + "' not found.", m1.get(o.getFirst()) != null);
             assertEquals(o.getSecond(), m1.get(o.getFirst()));
         }
         
         ///////////////////////////////////////////////////
         // Test with differnent type of value.
         ///////////////////////////////////////////////////
         Pair[] a2 = {
                 new Pair("key1", 1),
                 new Pair("key2", 2),
                 new Pair("key3", 3),
             };
         Map<String, Integer> m2 = CollectionUtils.pairsToHashMap(a2);
         
         assertTrue(m2 != null);
         assertEquals(a2.length, m2.size());
         
         for (Pair o : a2)
         {
             assertTrue("Key: '" + o.getFirst() + "' not found.", m2.get(o.getFirst()) != null);
             assertEquals(o.getSecond(), m2.get(o.getFirst()));
         }
         
         ///////////////////////////////////////////////////
         // Test with an empty array/map.
         ///////////////////////////////////////////////////
         Pair[] a3 = { };
         Map<String, Integer> m3 = CollectionUtils.pairsToHashMap(a3);

         assertTrue(m3 != null);
         assertEquals(0, m3.size());         
     }
    /**
     * Test method for {@link com.cordys.coe.util.CollectionUtils#arrayToHashMap(java.lang.Object[][])}.
     */
    public void testArrayToHashMap()
    {
        ///////////////////////////////////////////////////
        // Test with simple string values.
        ///////////////////////////////////////////////////
        Object[][] a1 = {
                { "key1", "value1" },
                { "key2", "value2" },
                { "key3", "value3" },
            };
        Map<String, String> m1 = CollectionUtils.arrayToHashMap(a1);
        
        assertTrue(m1 != null);
        assertEquals(a1.length, m1.size());
        
        for (Object[] o : a1)
        {
            assertTrue("Key: '" + o[0] + "' not found.", m1.get(o[0]) != null);
            assertEquals(o[1], m1.get(o[0]));
        }
        
        ///////////////////////////////////////////////////
        // Test with differnent type of value.
        ///////////////////////////////////////////////////
        Object[][] a2 = {
                { "key1", 1 },
                { "key2", 2 },
                { "key3", 3 },
            };
        Map<String, Integer> m2 = CollectionUtils.arrayToHashMap(a2);
        
        assertTrue(m2 != null);
        assertEquals(a2.length, m2.size());
        
        for (Object[] o : a2)
        {
            assertTrue("Key: '" + o[0] + "' not found.", m2.get(o[0]) != null);
            assertEquals(o[1], m2.get(o[0]));
        }
        
        ///////////////////////////////////////////////////
        // Test with an empty array/map.
        ///////////////////////////////////////////////////
        Object[][] a3 = { };
        Map<String, Integer> m3 = CollectionUtils.arrayToHashMap(a3);

        assertTrue(m3 != null);
        assertEquals(0, m3.size());
    }

    /**
     * Test method for {@link com.cordys.coe.util.CollectionUtils#arrayToHashSet(T[])}.
     */
    public void testArrayToHashSet()
    {
        ///////////////////////////////////////////////////
        // Test with string values.
        ///////////////////////////////////////////////////
        String[] a1 = { "value1",  "value2",  "value3" };
        Set<String> s1 = CollectionUtils.arrayToHashSet(a1);
        
        assertTrue(s1 != null);
        assertEquals(a1.length, s1.size());
        
        for (String v : a1)
        {
            assertTrue("Value: '" + v + "' not found.", s1.contains(v));
        }
        
        ///////////////////////////////////////////////////
        // Test with integer values.
        ///////////////////////////////////////////////////
        Integer[] a2 = { 1, 2, 3 };
        Set<Integer> s2 = CollectionUtils.arrayToHashSet(a2);
        
        assertTrue(s2 != null);
        assertEquals(a2.length, s2.size());
        
        for (Integer v : a2)
        {
            assertTrue("Value: '" + v + "' not found.", s2.contains(v));
        }
        
        ///////////////////////////////////////////////////
        // Test with an empty array/map.
        ///////////////////////////////////////////////////
        Integer[] a3 = {  };
        Set<Integer> s3 = CollectionUtils.arrayToHashSet(a3);
        
        assertEquals(0, s3.size());
    }

}
