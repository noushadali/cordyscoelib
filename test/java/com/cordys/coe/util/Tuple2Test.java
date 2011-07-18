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
public class Tuple2Test extends TestCase
{
    /**
     * Test method for {@link com.cordys.coe.util.Tuple2#hashCode()} and {@link com.cordys.coe.util.Tuple2#equals(java.lang.Object)}.
     */
    public void testHashCodeEquals()
    {
        Tuple2<String, Integer> t1 = new Tuple2<String, Integer>("aa", 100); 
        Tuple2<String, Integer> t2 = new Tuple2<String, Integer>("aa", 100);
        Tuple2<String, Integer> t3 = new Tuple2<String, Integer>("bb", 100);
        Tuple2<String, Integer> t4 = new Tuple2<String, Integer>("aa", 101);
        Tuple2<String, Integer> t5 = new Tuple2<String, Integer>("bb", 101);
        
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
     * Test method for {@link com.cordys.coe.util.Tuple2#hashCode()} and {@link com.cordys.coe.util.Tuple2#equals(java.lang.Object)}.
     */
    public void testHashCodeEqualsArray()
    {
        Tuple2<String[], int[]> t1 = new Tuple2<String[], int[]>(new String[] { "aa" }, new int[] { 100 }); 
        Tuple2<String[], int[]> t2 = new Tuple2<String[], int[]>(new String[] { "aa" }, new int[] { 100 });
        Tuple2<String[], int[]> t3 = new Tuple2<String[], int[]>(new String[] { "bb" }, new int[] { 100 });
        Tuple2<String[], int[]> t4 = new Tuple2<String[], int[]>(new String[] { "aa" }, new int[] { 101 });
        Tuple2<String[], int[]> t5 = new Tuple2<String[], int[]>(new String[] { "bb" }, new int[] { 101 });
        Tuple2<String[], int[]> t6 = new Tuple2<String[], int[]>(new String[] { "aa", "bb" }, new int[] { 100 });
        Tuple2<String[], int[]> t7 = new Tuple2<String[], int[]>(new String[] { "aa", "bb" }, new int[] { 100, 101 });
        
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
     * Test method for {@link com.cordys.coe.util.Tuple2#getValue1()}.
     */
    public void testGetValues()
    {
        Tuple2<String, Integer> t1 = new Tuple2<String, Integer>("aa", 100); 
        
        assertEquals(t1.getValue1(), "aa");
        assertEquals((long) t1.getValue2(), 100);
    }

    /**
     * Test method for {@link com.cordys.coe.util.Tuple2#toString()}.
     */
    public void testToString()
    {
        Tuple2<String, Integer> t1 = new Tuple2<String, Integer>("aa", 100); 
        
        assertEquals(t1.toString(), "Tuple2 [value1=aa, value2=100]");
    }
}
