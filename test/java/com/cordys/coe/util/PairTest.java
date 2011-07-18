/**
 * (c) 2007 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util;

import junit.framework.TestCase;

/**
 * Test case for class Pair.
 *
 * @author mpoyhone
 */
public class PairTest extends TestCase
{
    /**
     * Test method for {@link com.cordys.coe.util.Pair#hashCode()}.
     */
    public void testHashCode()
    {
        String s1 = "aaa";
        String s2 = "bbb";
        Pair<String, String> p = new Pair<String, String>(s1, s2);

        assertTrue(p.hashCode() != s1.hashCode());
        assertTrue(p.hashCode() != s2.hashCode());
    }

    /**
     * Test method for {@link com.cordys.coe.util.Pair#Pair()}.
     */
    public void testPair()
    {
        Pair<Object, Object> p = new Pair<Object, Object>();
        
        assertEquals(p.getFirst(), null);
        assertEquals(p.getSecond(), null);
    }

    /**
     * Test method for {@link com.cordys.coe.util.Pair#Pair(java.lang.Object, java.lang.Object)}.
     */
    public void testPairT1T2()
    {
        String v1 = "v1";
        boolean v2 = true; 
        Pair<String, Boolean> p = new Pair<String, Boolean>(v1, v2);
        
        assertEquals(p.getFirst(), v1);
        assertEquals(p.getSecond(), new Boolean(v2));
    }

    /**
     * Test method for {@link com.cordys.coe.util.Pair#setFirst(java.lang.Object)}.
     */
    public void testSetFirst()
    {
        String s = "value";
        Pair<String, String> p = new Pair<String, String>();
        
        assertEquals(p.getFirst(), null);
        p.setFirst(s);
        assertEquals(p.getFirst(), s);
    }

    /**
     * Test method for {@link com.cordys.coe.util.Pair#setSecond(java.lang.Object)}.
     */
    public void testSetSecond()
    {
        String s = "value";
        Pair<String, String> p = new Pair<String, String>();
        
        assertEquals(p.getSecond(), null);
        p.setSecond(s);
        assertEquals(p.getSecond(), s);
    }

    /**
     * Test method for {@link com.cordys.coe.util.Pair#toString()}.
     */
    public void testToString()
    {
        String v1 = "v1";
        boolean v2 = true; 
        Pair<String, Boolean> p = new Pair<String, Boolean>(v1, v2);
        
        assertEquals("[v1, true]", p.toString());
    }

    /**
     * Test method for {@link com.cordys.coe.util.Pair#equals(java.lang.Object)}.
     */
    public void testEqualsObject()
    {
        {
            Pair<String, String> p1, p2;
            
            p1 = new Pair<String, String>();
            p2 = new Pair<String, String>();
            assertTrue(p1.equals(p2));
        }
        
        {
            Pair<String, String> p1, p2;
            
            p1 = new Pair<String, String>(new String("a"), new String("b"));
            p2 = new Pair<String, String>(new String("a"), new String("b"));
            assertTrue(p1.equals(p2));
        }
        
        {
            Pair<String, String> p1, p2;
            
            p1 = new Pair<String, String>(new String("a"), new String("b"));
            p2 = new Pair<String, String>(new String("a"), new String("bb"));
            assertFalse(p1.equals(p2));
        }       
        
        {
            Pair<String, String> p1, p2;
            
            p1 = new Pair<String, String>(new String("a"), new String("b"));
            p2 = new Pair<String, String>();
            assertFalse(p1.equals(p2));
        }           
        
        {
            Pair<String, String> p1, p2;
            
            p1 = new Pair<String, String>();
            p2 = new Pair<String, String>(new String("a"), new String("b"));
            assertFalse(p1.equals(p2));
        }
    }

}
