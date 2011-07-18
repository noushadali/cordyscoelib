/**
 * (c) 2007 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util;

import java.util.Arrays;
import java.util.LinkedList;

import junit.framework.TestCase;

/**
 * TODO Describe the class.
 *
 * @author mpoyhone
 */
public class ListMapTest extends TestCase
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
     * Test method for {@link com.cordys.coe.util.ListMap#containsValue(java.lang.Object)}.
     */
    public void testListClassConstructor()
    {
        ListMap<String, Integer> m = new ListMap<String, Integer>(LinkedList.class);
        
        m.put("11", 1);
        
        assertEquals(m.get("11"), Arrays.asList(1));
        assertNull(m.get("22"));
    }

    /**
     * Test method for {@link com.cordys.coe.util.ListMap#containsValue(java.lang.Object)}.
     */
    public void testContainsValue()
    {
        ListMap<String, Integer> m = new ListMap<String, Integer>();
        
        m.put("11", 1);
        m.put("22", 2);
        m.put("33", 3);
        
        assertTrue(m.containsKey("11"));
        assertTrue(m.containsKey("22"));
        assertTrue(m.containsKey("33"));
        assertTrue(m.containsValue(1));
        assertTrue(m.containsValue(2));
        assertTrue(m.containsValue(3));
        assertFalse(m.containsValue(4));
    }

    /**
     * Test method for {@link com.cordys.coe.util.ListMap#get(java.lang.Object)}.
     */
    public void testGet()
    {
        ListMap<String, Integer> m = new ListMap<String, Integer>();
        
        m.put("11", 1);
        
        assertEquals(m.get("11"), Arrays.asList(1));
        assertNull(m.get("22"));
    }

    /**
     * Test method for {@link com.cordys.coe.util.ListMap#values()}.
     */
    public void testValues()
    {
        ListMap<String, Integer> m = new ListMap<String, Integer>();
        
        m.put("11", 1);
        m.put("11", 2);
        m.put("11", 3);
        
        assertEquals(m.values(), Arrays.asList(1, 2, 3));
    }

}
