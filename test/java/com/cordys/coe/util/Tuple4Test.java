/**
 * (c) 2009 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util;

import junit.framework.TestCase;

/**
 * Test case for tuple class.
 *
 * @author mpoyhone
 */
public class Tuple4Test extends TestCase
{
    /**
     * Test method for {@link com.cordys.coe.util.Tuple4#hashCode()} and {@link com.cordys.coe.util.Tuple4#equals(java.lang.Object)}.
     */
    public void testHashCodeEquals()
    {
        Tuple4<String, Integer, Boolean, Long> t1 = new Tuple4<String, Integer, Boolean, Long>("aa", 100, true, 200L); 
        Tuple4<String, Integer, Boolean, Long> t2 = new Tuple4<String, Integer, Boolean, Long>("aa", 100, true, 200L);
        Tuple4<String, Integer, Boolean, Long> t3 = new Tuple4<String, Integer, Boolean, Long>("bb", 100, false, 200L);
        Tuple4<String, Integer, Boolean, Long> t4 = new Tuple4<String, Integer, Boolean, Long>("aa", 101, false, 300L);
        Tuple4<String, Integer, Boolean, Long> t5 = new Tuple4<String, Integer, Boolean, Long>("bb", 101, true, 300L);
        
        assertTrue(t1.hashCode() == t2.hashCode());
        assertFalse(t1.hashCode() == t3.hashCode());
        assertFalse(t1.hashCode() == t4.hashCode());
        assertFalse(t1.hashCode() == t5.hashCode());
        
        assertTrue(t1.equals(t1));
        assertTrue(t1.equals(t2));
        assertFalse(t1.equals(t3));
        assertFalse(t1.equals(t4));
        assertFalse(t1.equals(t5));
    }
    
    /**
     * Test method for {@link com.cordys.coe.util.Tuple4#hashCode()} and {@link com.cordys.coe.util.Tuple4#equals(java.lang.Object)}.
     */
    public void testHashCodeEqualsArray()
    {
        Tuple4<String[], int[], boolean[], long[]> t1 = new Tuple4<String[], int[], boolean[], long[]>(new String[] { "aa" }, new int[] { 100 }, new boolean[] { false }, new long[] { 22 }); 
        Tuple4<String[], int[], boolean[], long[]> t2 = new Tuple4<String[], int[], boolean[], long[]>(new String[] { "aa" }, new int[] { 100 }, new boolean[] { false }, new long[] { 22 });
        Tuple4<String[], int[], boolean[], long[]> t3 = new Tuple4<String[], int[], boolean[], long[]>(new String[] { "bb" }, new int[] { 100 }, new boolean[] { false }, new long[] { 33 });
        Tuple4<String[], int[], boolean[], long[]> t4 = new Tuple4<String[], int[], boolean[], long[]>(new String[] { "aa" }, new int[] { 101 }, new boolean[] { true }, new long[] { 22 });
        Tuple4<String[], int[], boolean[], long[]> t5 = new Tuple4<String[], int[], boolean[], long[]>(new String[] { "bb" }, new int[] { 101 }, new boolean[] { false }, new long[] { 33 });
        Tuple4<String[], int[], boolean[], long[]> t6 = new Tuple4<String[], int[], boolean[], long[]>(new String[] { "aa", "bb" }, new int[] { 100 }, new boolean[] { false }, new long[] { 22 });
        Tuple4<String[], int[], boolean[], long[]> t7 = new Tuple4<String[], int[], boolean[], long[]>(new String[] { "aa", "bb" }, new int[] { 100, 101 }, new boolean[] { false }, new long[] { 22 });
        
        assertTrue(t1.hashCode() == t2.hashCode());
        assertFalse(t1.hashCode() == t3.hashCode());
        assertFalse(t1.hashCode() == t4.hashCode());
        assertFalse(t1.hashCode() == t5.hashCode());
        assertFalse(t1.hashCode() == t6.hashCode());
        assertFalse(t1.hashCode() == t7.hashCode());
        
        assertTrue(t1.equals(t1));
        assertTrue(t1.equals(t2));
        assertFalse(t1.equals(t3));
        assertFalse(t1.equals(t4));
        assertFalse(t1.equals(t5));
        assertFalse(t1.equals(t6));
        assertFalse(t1.equals(t7));
    }

    /**
     * Test method for {@link com.cordys.coe.util.Tuple4#getValue1()}.
     */
    public void testGetValues()
    {
        Tuple4<String, Integer, Boolean, Long> t1 = new Tuple4<String, Integer, Boolean, Long>("aa", 100, true, 200L); 
        
        assertEquals(t1.getValue1(), "aa");
        assertEquals((long) t1.getValue2(), 100);
        assertEquals((boolean) t1.getValue3(), true);
        assertEquals((long) t1.getValue4(), 200L);
    }

    /**
     * Test method for {@link com.cordys.coe.util.Tuple4#toString()}.
     */
    public void testToString()
    {
        Tuple4<String, Integer, Boolean, Long> t1 = new Tuple4<String, Integer, Boolean, Long>("aa", 100, true, 200L); 
        
        assertEquals(t1.toString(), "Tuple4 [value1=aa, value2=100, value3=true, value4=200]");
    }
}
