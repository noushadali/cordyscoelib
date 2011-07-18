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
public class Tuple3Test extends TestCase
{
    /**
     * Test method for {@link com.cordys.coe.util.Tuple3#hashCode()} and {@link com.cordys.coe.util.Tuple3#equals(java.lang.Object)}.
     */
    public void testHashCodeEquals()
    {
        Tuple3<String, Integer, Boolean> t1 = new Tuple3<String, Integer, Boolean>("aa", 100, true); 
        Tuple3<String, Integer, Boolean> t2 = new Tuple3<String, Integer, Boolean>("aa", 100, true);
        Tuple3<String, Integer, Boolean> t3 = new Tuple3<String, Integer, Boolean>("bb", 100, false);
        Tuple3<String, Integer, Boolean> t4 = new Tuple3<String, Integer, Boolean>("aa", 101, false);
        Tuple3<String, Integer, Boolean> t5 = new Tuple3<String, Integer, Boolean>("bb", 101, true);
        
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
     * Test method for {@link com.cordys.coe.util.Tuple3#hashCode()} and {@link com.cordys.coe.util.Tuple3#equals(java.lang.Object)}.
     */
    public void testHashCodeEqualsArray()
    {
        Tuple3<String[], int[], boolean[]> t1 = new Tuple3<String[], int[], boolean[]>(new String[] { "aa" }, new int[] { 100 }, new boolean[] { false }); 
        Tuple3<String[], int[], boolean[]> t2 = new Tuple3<String[], int[], boolean[]>(new String[] { "aa" }, new int[] { 100 }, new boolean[] { false });
        Tuple3<String[], int[], boolean[]> t3 = new Tuple3<String[], int[], boolean[]>(new String[] { "bb" }, new int[] { 100 }, new boolean[] { false });
        Tuple3<String[], int[], boolean[]> t4 = new Tuple3<String[], int[], boolean[]>(new String[] { "aa" }, new int[] { 101 }, new boolean[] { true });
        Tuple3<String[], int[], boolean[]> t5 = new Tuple3<String[], int[], boolean[]>(new String[] { "bb" }, new int[] { 101 }, new boolean[] { false });
        Tuple3<String[], int[], boolean[]> t6 = new Tuple3<String[], int[], boolean[]>(new String[] { "aa", "bb" }, new int[] { 100 }, new boolean[] { false });
        Tuple3<String[], int[], boolean[]> t7 = new Tuple3<String[], int[], boolean[]>(new String[] { "aa", "bb" }, new int[] { 100, 101 }, new boolean[] { false });
        
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
     * Test method for {@link com.cordys.coe.util.Tuple3#getValue1()}.
     */
    public void testGetValues()
    {
        Tuple3<String, Integer, Boolean> t1 = new Tuple3<String, Integer, Boolean>("aa", 100, true); 
        
        assertEquals(t1.getValue1(), "aa");
        assertEquals((long) t1.getValue2(), 100);
        assertEquals((boolean) t1.getValue3(), true);
    }

    /**
     * Test method for {@link com.cordys.coe.util.Tuple3#toString()}.
     */
    public void testToString()
    {
        Tuple3<String, Integer, Boolean> t1 = new Tuple3<String, Integer, Boolean>("aa", 100, true); 
        
        assertEquals(t1.toString(), "Tuple3 [value1=aa, value2=100, value3=true]");
    }
}
