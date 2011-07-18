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
public class Tuple5Test extends TestCase
{
    /**
     * Test method for {@link com.cordys.coe.util.Tuple5#hashCode()} and {@link com.cordys.coe.util.Tuple5#equals(java.lang.Object)}.
     */
    public void testHashCodeEquals()
    {
        Tuple5<String, Integer, Boolean, Long, Short> t1 = new Tuple5<String, Integer, Boolean, Long, Short>("aa", 100, true, 200L, (short) 5); 
        Tuple5<String, Integer, Boolean, Long, Short> t2 = new Tuple5<String, Integer, Boolean, Long, Short>("aa", 100, true, 200L, (short) 5);
        Tuple5<String, Integer, Boolean, Long, Short> t3 = new Tuple5<String, Integer, Boolean, Long, Short>("bb", 100, false, 200L, (short) 5);
        Tuple5<String, Integer, Boolean, Long, Short> t4 = new Tuple5<String, Integer, Boolean, Long, Short>("aa", 101, false, 300L, (short) 5);
        Tuple5<String, Integer, Boolean, Long, Short> t5 = new Tuple5<String, Integer, Boolean, Long, Short>("bb", 101, true, 300L, (short) 6);
        
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
     * Test method for {@link com.cordys.coe.util.Tuple5#hashCode()} and {@link com.cordys.coe.util.Tuple5#equals(java.lang.Object)}.
     */
    public void testHashCodeEqualsArray()
    {
        Tuple5<String[], int[], boolean[], long[], short[]> t1 = new Tuple5<String[], int[], boolean[], long[], short[]> (new String[] { "aa" }, new int[] { 100 }, new boolean[] { false }, new long[] { 22 }, new short[] { 44 }); 
        Tuple5<String[], int[], boolean[], long[], short[]>  t2 = new Tuple5<String[], int[], boolean[], long[], short[]> (new String[] { "aa" }, new int[] { 100 }, new boolean[] { false }, new long[] { 22 }, new short[] { 44 });
        Tuple5<String[], int[], boolean[], long[], short[]>  t3 = new Tuple5<String[], int[], boolean[], long[], short[]> (new String[] { "bb" }, new int[] { 100 }, new boolean[] { false }, new long[] { 33 }, new short[] { 55 });
        Tuple5<String[], int[], boolean[], long[], short[]>  t4 = new Tuple5<String[], int[], boolean[], long[], short[]> (new String[] { "aa" }, new int[] { 101 }, new boolean[] { true }, new long[] { 22 }, new short[] { 55 });
        Tuple5<String[], int[], boolean[], long[], short[]>  t5 = new Tuple5<String[], int[], boolean[], long[], short[]> (new String[] { "bb" }, new int[] { 101 }, new boolean[] { false }, new long[] { 33 }, new short[] { 44 });
        Tuple5<String[], int[], boolean[], long[], short[]>  t6 = new Tuple5<String[], int[], boolean[], long[], short[]> (new String[] { "aa", "bb" }, new int[] { 100 }, new boolean[] { false }, new long[] { 22 }, new short[] { 44 });
        Tuple5<String[], int[], boolean[], long[], short[]>  t7 = new Tuple5<String[], int[], boolean[], long[], short[]> (new String[] { "aa", "bb" }, new int[] { 100, 101 }, new boolean[] { false }, new long[] { 22 }, new short[] { 44 });
        
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
     * Test method for {@link com.cordys.coe.util.Tuple5#getValue1()}.
     */
    public void testGetValues()
    {
        Tuple5<String, Integer, Boolean, Long, Short> t1 = new Tuple5<String, Integer, Boolean, Long, Short>("aa", 100, true, 200L, (short) 5); 
        
        assertEquals(t1.getValue1(), "aa");
        assertEquals((long) t1.getValue2(), 100);
        assertEquals((boolean) t1.getValue3(), true);
        assertEquals((long) t1.getValue4(), 200L);
        assertEquals((short) t1.getValue5(), 5);
    }

    /**
     * Test method for {@link com.cordys.coe.util.Tuple5#toString()}.
     */
    public void testToString()
    {
        Tuple5<String, Integer, Boolean, Long, Short> t1 = new Tuple5<String, Integer, Boolean, Long, Short>("aa", 100, true, 200L, (short) 5); 
        
        assertEquals(t1.toString(), "Tuple4 [value1=aa, value2=100, value3=true, value4=200, value5=5]");
    }
}
