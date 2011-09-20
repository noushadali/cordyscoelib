/**
 * (c) 2007 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util.xml.nom;

import com.cordys.coe.util.test.junit.NomTestCase;

import com.eibus.xml.nom.Node;
import com.eibus.xml.xpath.XPath;
import com.eibus.xml.xpath.XPathMetaInfo;

import java.util.Date;

/**
 * Test cases for the XPathHelper class.
 *
 * @author  mpoyhone
 */
public class XPathHelperTest extends NomTestCase
{
    /**
     * @see  junit.framework.TestCase#setUp()
     */
    public void setUp()
               throws Exception
    {
        super.setUp();
    }

    /**
     * @see  junit.framework.TestCase#tearDown()
     */
    public void tearDown()
                  throws Exception
    {
        super.tearDown();
    }

    /**
     * Test method for {@link com.cordys.coe.util.xml.nom.XPathHelper#getBooleanValue(int, java.lang.String, boolean)}.
     */
    public void testGetBooleanValue()
    {
        // fail("Not yet implemented");
    }

    /**
     * Test method for {@link com.cordys.coe.util.xml.nom.XPathHelper#getIntegerValue(int, java.lang.String, boolean)}.
     */
    public void testGetIntegerValue()
    {
        // fail("Not yet implemented");
    }

    /**
     * Test method for {@link com.cordys.coe.util.xml.nom.XPathHelper#getStringValue(int, java.lang.String, boolean)}.
     */
    public void testGetStringValueIntStringBoolean()
    {
        // fail("Not yet implemented");
    }

    /**
     * Test method for {@link com.cordys.coe.util.xml.nom.XPathHelper#getStringValue(int, com.eibus.xml.xpath.XPath,
     * com.eibus.xml.xpath.XPathMetaInfo, boolean)}.
     */
    public void testGetStringValueIntXPathXPathMetaInfoBoolean()
    {
        int root = parse("<a><b attr='b1' /><b attr='b2' /><b>b3&amp;value</b><b>b4</b></a>");

        // Node selection tests.
        assertEquals("b1b2", XPathHelper.getStringValue(root, XPath.getXPathInstance("//b/@attr"), null, true));
        assertEquals("b3&valueb4", XPathHelper.getStringValue(root, XPath.getXPathInstance("//b"), null, true));
        assertEquals("b3&valueb4", XPathHelper.getStringValue(root, XPath.getXPathInstance("//b/text()"), null, true));
        assertEquals("b3&value", XPathHelper.getStringValue(root, XPath.getXPathInstance("//b"), null, false));
        assertEquals("b3&value", XPathHelper.getStringValue(root, XPath.getXPathInstance("//b/text()"), null, false));
        assertEquals("b4", XPathHelper.getStringValue(root, XPath.getXPathInstance("/a/b[4]/text()"), null, true));

        // String value tests.
        assertEquals("b", XPathHelper.getStringValue(root, XPath.getXPathInstance("local-name(/a/*)"), null, true));
        assertEquals("b", XPathHelper.getStringValue(root, XPath.getXPathInstance("local-name(/a/*)"), null, false));
        assertEquals("b1",
                     XPathHelper.getStringValue(root, XPath.getXPathInstance("string(/a/b[1]/@attr)"), null, false));

        // Boolean value tests.
        assertEquals("true", XPathHelper.getStringValue(root, XPath.getXPathInstance("/a/b/@attr = 'b1'"), null, true));
    }

    /**
     * Test method for {@link com.cordys.coe.util.xml.nom.XPathHelper#getStringValue(int, com.eibus.xml.xpath.XPath,
     * com.eibus.xml.xpath.XPathMetaInfo, boolean)}.
     */
    public void testGetStringValueIntXPathXPathMetaInfoBoolean_Namespace()
    {
        String ns1 = "http://my-ns-1";
        String ns2 = "http://my-ns-2";
        String ns3 = "http://my-ns-3";
        XPathMetaInfo info = new XPathMetaInfo();

        info.addNamespaceBinding("ns1", ns1);
        info.addNamespaceBinding("ns2", ns2);
        info.addNamespaceBinding("ns3", ns3);

        int root = parse("<ns1:a xmlns:ns1=\"" + ns1 + "\" xmlns:ns2=\"" + ns2 + "\" xmlns:ns3=\"" + ns3 + "\">" +
                         "<ns2:b attr='b1' /><ns2:b ns3:attr='b2' /><ns2:b>b3&amp;value</ns2:b><ns2:b>b4</ns2:b></ns1:a>");

        // Node selection tests.
        // This test fails. It returns concatenated attr values.
        // assertEquals("b1", XPathHelper.getStringValue(root, XPath.getXPathInstance("//ns2:b/@attr"), info, true));

        assertEquals("b2", XPathHelper.getStringValue(root, XPath.getXPathInstance("//ns2:b/@ns3:attr"), info, true));
        assertEquals("b3&valueb4", XPathHelper.getStringValue(root, XPath.getXPathInstance("//ns2:b"), info, true));
        assertEquals("b3&valueb4",
                     XPathHelper.getStringValue(root, XPath.getXPathInstance("//ns2:b/text()"), info, true));
        assertEquals("b3&value", XPathHelper.getStringValue(root, XPath.getXPathInstance("//ns2:b"), info, false));
        assertEquals("b3&value",
                     XPathHelper.getStringValue(root, XPath.getXPathInstance("//ns2:b/text()"), info, false));
        assertEquals("b4",
                     XPathHelper.getStringValue(root, XPath.getXPathInstance("/ns1:a/ns2:b[4]/text()"), info, true));

        // String value tests.
        assertEquals("b", XPathHelper.getStringValue(root, XPath.getXPathInstance("local-name(/ns1:a/*)"), info, true));
        assertEquals("b",
                     XPathHelper.getStringValue(root, XPath.getXPathInstance("local-name(/ns1:a/*)"), info, false));
        assertEquals("b1",
                     XPathHelper.getStringValue(root, XPath.getXPathInstance("string(/ns1:a/ns2:b[1]/@attr)"), info,
                                                false));

        // This test fails assertEquals("", XPathHelper.getStringValue(root,
        // XPath.getXPathInstance("string(/ns1:a/ns2:b[2]/@attr)"), info, false));

        assertEquals("b2",
                     XPathHelper.getStringValue(root, XPath.getXPathInstance("string(/ns1:a/ns2:b[2]/@ns3:attr)"), info,
                                                false));

        // Boolean value tests.
        assertEquals("true",
                     XPathHelper.getStringValue(root, XPath.getXPathInstance("/ns1:a/ns2:b/@attr = 'b1'"), info, true));
    }

    /**
     * Test method for {@link com.cordys.coe.util.xml.nom.XPathHelper#setNodeValue(int, com.eibus.xml.xpath.XPath,
     * com.eibus.xml.xpath.XPathMetaInfo, boolean)}.
     */
    public void testSetNodeValueIntXPathXPathMetaInfoBoolean()
    {
        int root = parse("<a><b attr='b1' /><b attr='b2' /><b>b3&amp;value</b><b>b4</b></a>");
        int test;
        int control;

        test = addNomGarbage(Node.clone(root, true));
        control = parse("<a><b attr='X' /><b attr='X' /><b>b3&amp;value</b><b>b4</b></a>");
        assertTrue(XPathHelper.setNodeValue(test, XPath.getXPathInstance("//b/@attr"), null, "X", true));
        assertNodesEqual(control, test);

        test = addNomGarbage(Node.clone(root, true));
        control = parse("<a><b attr='X' /><b attr='b2' /><b>b3&amp;value</b><b>b4</b></a>");
        assertTrue(XPathHelper.setNodeValue(test, XPath.getXPathInstance("//b/@attr"), null, "X", false));
        assertNodesEqual(control, test);

        test = addNomGarbage(Node.clone(root, true));
        control = parse("<a><b attr='b1'>X</b><b attr='b2'>X</b><b>X</b><b>X</b></a>");
        assertTrue(XPathHelper.setNodeValue(test, XPath.getXPathInstance("//b"), null, "X", true));
        assertNodesEqual(control, test);

        test = addNomGarbage(Node.clone(root, true));
        control = parse("<a><b attr='b1'>X</b><b attr='b2' /><b>b3&amp;value</b><b>b4</b></a>");
        assertTrue(XPathHelper.setNodeValue(test, XPath.getXPathInstance("//b"), null, "X", false));
        assertNodesEqual(control, test);

        test = addNomGarbage(Node.clone(root, true));
        control = root;
        assertFalse(XPathHelper.setNodeValue(test, XPath.getXPathInstance("//b/@attr2"), null, "X", true));
        assertNodesEqual(control, test);
    }

    /**
     * Test method for {@link com.cordys.coe.util.xml.nom.XPathHelper#selectNodes(int, java.lang.String)}.
     */
    public void testSelectNodes()
    {
        // fail("Not yet implemented");
    }

    /**
     * Test method for {@link com.cordys.coe.util.xml.nom.XPathHelper#selectSingleNode(int, java.lang.String)}.
     */
    public void testSelectSingleNode()
    {
        // fail("Not yet implemented");
    }

    /**
     * This class tests the parsing of the date values. It also checks if micro/nanoseconds are ignored.
     */
    public void testDateValueParsing()
    {
        int root = parse("<a>" +
                         "<b>2011-01-01T15:00:00.123</b>" +
                         "<c>2011-01-01T15:00:00.1234</c>" +
                         "</a>");

        Date d1 = XPathHelper.getDateValue(root, "b/text()");

        assertEquals(1293894000123L, d1.getTime());

        Date d2 = XPathHelper.getDateValue(root, "c/text()");
        assertEquals(1293894000123L, d2.getTime());
    }
}
