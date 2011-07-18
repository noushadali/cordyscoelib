/**
 * (c) 2007 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util.xml;

import junit.framework.TestCase;

/**
 * Test case for XMLQuery class.
 *
 * @author mpoyhone
 */
public class XMLQueryTest extends TestCase
{

    /**
     * Test method for {@link com.cordys.coe.util.xml.XMLQuery#XMLQuery()}.
     */
    public void testXMLQuery()
    {
        XMLQuery q = new XMLQuery();
        
        assertNull(q.getQueryPath());
        assertNull(q.getQueryAttribute());
    }

    /**
     * Test method for {@link com.cordys.coe.util.xml.XMLQuery#XMLQuery(java.lang.String)}.
     */
    public void testXMLQueryString()
    {
        XMLQuery q = new XMLQuery("//a/@b");
        
        assertEquals("?<a>", q.getQueryPath());
        assertEquals("b", q.getQueryAttribute());
    }

    /**
     * Test method for {@link com.cordys.coe.util.xml.XMLQuery#composeQuery(java.lang.String)}.
     */
    public void testComposeQuery()
    {
        XMLQuery q;
     
        ///////////////////////////
        q = new XMLQuery();
        q.composeQuery("a");
        assertEquals("<a>", q.getQueryPath());
        
        ///////////////////////////
        q = new XMLQuery();
        q.composeQuery("a/@b");
        assertEquals("<a>", q.getQueryPath());
        assertEquals("b", q.getQueryAttribute());
        
        
        ///////////////////////////
        q = new XMLQuery();
        q.composeQuery("a[@b='test']/c");
        assertEquals("<a b=\"test\"><c>", q.getQueryPath());
        
        ///////////////////////////
        q = new XMLQuery();
        q.composeQuery("//a");
        assertEquals("?<a>", q.getQueryPath());    
        
        ///////////////////////////
        q = new XMLQuery();
        q.composeQuery("//a/b");
        assertEquals("?<a><b>", q.getQueryPath());          
        
        ///////////////////////////
        q = new XMLQuery();
        q.composeQuery("a//b");
        assertEquals("<a>?<b>", q.getQueryPath());         
        
        ///////////////////////////
        q = new XMLQuery();
        q.composeQuery("/a");
        assertEquals("<a>", q.getQueryPath());       
        
        ///////////////////////////
        q = new XMLQuery();
        q.composeQuery("/a/*/..");
        assertEquals("<a><>parent", q.getQueryPath());           
        
        ///////////////////////////
        q = new XMLQuery();
        q.composeQuery("/");
        assertEquals("<>", q.getQueryPath());
    }

//    /**
//     * Test method for {@link com.cordys.coe.util.xml.XMLQuery#findAllNodes(int)}.
//     */
//    public void testFindAllNodes()
//    {
//        fail("Not yet implemented");
//    }
//
//    /**
//     * Test method for {@link com.cordys.coe.util.xml.XMLQuery#findNode(int)}.
//     */
//    public void testFindNodeInt()
//    {
//        fail("Not yet implemented");
//    }
//
//    /**
//     * Test method for {@link com.cordys.coe.util.xml.XMLQuery#findNode(int, boolean)}.
//     */
//    public void testFindNodeIntBoolean()
//    {
//        fail("Not yet implemented");
//    }
//
//    /**
//     * Test method for {@link com.cordys.coe.util.xml.XMLQuery#findValue(int, java.lang.String)}.
//     */
//    public void testFindValue()
//    {
//        fail("Not yet implemented");
//    }
//
//    /**
//     * Test method for {@link com.cordys.coe.util.xml.XMLQuery#getQueryAttribute()}.
//     */
//    public void testGetQueryAttribute()
//    {
//        fail("Not yet implemented");
//    }
//
//    /**
//     * Test method for {@link com.cordys.coe.util.xml.XMLQuery#getQueryPath()}.
//     */
//    public void testGetQueryPath()
//    {
//        fail("Not yet implemented");
//    }
//
//    /**
//     * Test method for {@link com.cordys.coe.util.xml.XMLQuery#setQueryAttribute(java.lang.String)}.
//     */
//    public void testSetQueryAttribute()
//    {
//        fail("Not yet implemented");
//    }
//
//    /**
//     * Test method for {@link com.cordys.coe.util.xml.XMLQuery#setQueryPath(java.lang.String)}.
//     */
//    public void testSetQueryPath()
//    {
//        fail("Not yet implemented");
//    }
//
//    /**
//     * Test method for {@link com.cordys.coe.util.xml.XMLQuery#toString()}.
//     */
//    public void testToString()
//    {
//        fail("Not yet implemented");
//    }

}
