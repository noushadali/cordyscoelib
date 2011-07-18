/**
 * (c) 2008 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util.xml.nom;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.cordys.coe.util.test.junit.NomTestCase;
import com.cordys.coe.util.xml.NamespaceDefinitions;
import com.eibus.xml.nom.Node;
import com.eibus.xml.xpath.XPath;
import com.eibus.xml.xpath.XPathMetaInfo;

/**
 * Test cases for the XPathWrapper
 *
 * @author mpoyhone
 */
public class XPathWrapperTest extends NomTestCase
{
    /**
     * Namespace used to test the namespace bindings.
     */
    private static final String TEST_NAMESPACE = "uri:test";

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
     * Test method for {@link com.cordys.coe.util.xml.nom.XPathWrapper#create(com.eibus.xml.xpath.XPath, com.eibus.xml.xpath.XPathMetaInfo)}.
     */
    public void testCreate_FromXPathObject()
    {
        XPath xp = XPath.getXPathInstance("//a");
        XPathMetaInfo metainfo = new XPathMetaInfo();
        XPathWrapper wrapper = XPathWrapper.create(xp, metainfo);
        
        assertEquals(xp, wrapper.getXPath());
        assertEquals(metainfo, wrapper.getMetaInfo());
    }

    /**
     * Test method for {@link com.cordys.coe.util.xml.nom.XPathWrapper#create(java.lang.String)}.
     */
    public void testCreate()
    {
        String xpath = "//a";
        XPathWrapper wrapper = XPathWrapper.create(xpath);
        
        assertEquals(xpath, wrapper.getXPath().getSourceExpression());
        assertNamespaceBinding(wrapper, "");
    }

    /**
     * Test method for {@link com.cordys.coe.util.xml.nom.XPathWrapper#create(java.lang.String, java.util.Map)}.
     */
    public void testCreate_WithBindingMap()
    {
        String xpath = "//ns:a";
        Map<String, String> bindings = new HashMap<String, String>();
        bindings.put("ns", TEST_NAMESPACE);
        XPathWrapper wrapper = XPathWrapper.create(xpath, bindings);
        
        assertEquals(xpath, wrapper.getXPath().getSourceExpression());
        assertNamespaceBinding(wrapper, TEST_NAMESPACE);
    }

    /**
     * Test method for {@link com.cordys.coe.util.xml.nom.XPathWrapper#create(java.lang.String, java.lang.String[])}.
     */
    public void testCreate_WithBindingStringArray()
    {
        String xpath = "//ns:a";
        XPathWrapper wrapper = XPathWrapper.create(xpath, "ns " + TEST_NAMESPACE);
        
        assertEquals(xpath, wrapper.getXPath().getSourceExpression());
        assertNamespaceBinding(wrapper, TEST_NAMESPACE);
    }

    /**
     * Test method for {@link com.cordys.coe.util.xml.nom.XPathWrapper#create(java.lang.String, java.lang.String[][])}.
     */
    public void testCreate_WithBindingArray()
    {
        String xpath = "//ns:a";
        XPathWrapper wrapper = XPathWrapper.create(xpath, new String[][] { { "ns", TEST_NAMESPACE } });
        
        assertEquals(xpath, wrapper.getXPath().getSourceExpression());
        assertNamespaceBinding(wrapper, TEST_NAMESPACE);
    }

    /**
     * Test method for {@link com.cordys.coe.util.xml.nom.XPathWrapper#createWithDefaultBindings(java.lang.String)}.
     */
    public void testCreateWithDefaultBindings()
    {
        String xpath = "//SOAP:a";
        XPathWrapper wrapper = XPathWrapper.createWithDefaultBindings(xpath);
        
        assertEquals(xpath, wrapper.getXPath().getSourceExpression());
        assertNamespaceBinding(wrapper, NamespaceDefinitions.XMLNS_SOAP_1_1);
    }

    /**
     * Test method for {@link com.cordys.coe.util.xml.nom.XPathWrapper#createWithDefaultBindings(java.lang.String, java.util.Map)}.
     */
    public void testCreateWithDefaultBindings_WithBindingMap()
    {
        // Test the custom bindings.
        String xpath = "//ns:a";
        Map<String, String> bindings = new HashMap<String, String>();
        bindings.put("ns", TEST_NAMESPACE);
        XPathWrapper wrapper = XPathWrapper.createWithDefaultBindings(xpath, bindings);
        
        assertEquals(xpath, wrapper.getXPath().getSourceExpression());
        assertNamespaceBinding(wrapper, TEST_NAMESPACE);
        
        // Test default bindings.
        xpath = "//SOAP:a";
        wrapper = XPathWrapper.createWithDefaultBindings(xpath, bindings);
        
        assertEquals(xpath, wrapper.getXPath().getSourceExpression());
        assertNamespaceBinding(wrapper, NamespaceDefinitions.XMLNS_SOAP_1_1);
    }

    /**
     * Test method for {@link com.cordys.coe.util.xml.nom.XPathWrapper#createWithDefaultBindings(java.lang.String, java.lang.String[])}.
     */
    public void testCreateWithDefaultBindings_WithBindingStringArray()
    {
        // Test the custom bindings.
        String xpath = "//ns:a";
        String binding =  "ns " + TEST_NAMESPACE;
        XPathWrapper wrapper = XPathWrapper.createWithDefaultBindings(xpath, binding);
        
        assertEquals(xpath, wrapper.getXPath().getSourceExpression());
        assertNamespaceBinding(wrapper, TEST_NAMESPACE);
        
        // Test default bindings.
        xpath = "//SOAP:a";
        wrapper = XPathWrapper.createWithDefaultBindings(xpath, binding);
        
        assertEquals(xpath, wrapper.getXPath().getSourceExpression());
        assertNamespaceBinding(wrapper, NamespaceDefinitions.XMLNS_SOAP_1_1);
    }

    /**
     * Test method for {@link com.cordys.coe.util.xml.nom.XPathWrapper#createWithDefaultBindings(java.lang.String, java.lang.String[][])}.
     */
    public void testCreateWithDefaultBindings_WithBindingArray()
    {
        // Test the custom bindings.
        String xpath = "//ns:a";
        String[][] bindings = new String[][] { { "ns", TEST_NAMESPACE } };
        XPathWrapper wrapper = XPathWrapper.createWithDefaultBindings(xpath, bindings);
        
        assertEquals(xpath, wrapper.getXPath().getSourceExpression());
        assertNamespaceBinding(wrapper, TEST_NAMESPACE);
        
        // Test default bindings.
        xpath = "//SOAP:a";
        wrapper = XPathWrapper.createWithDefaultBindings(xpath, bindings);
        
        assertEquals(xpath, wrapper.getXPath().getSourceExpression());
        assertNamespaceBinding(wrapper, NamespaceDefinitions.XMLNS_SOAP_1_1);
    }

    /**
     * Test method for {@link com.cordys.coe.util.xml.nom.XPathWrapper#getMetaInfo()}.
     */
    public void testGetMetaInfo()
    {
        XPath xp = XPath.getXPathInstance("//a");
        XPathMetaInfo metainfo = new XPathMetaInfo();
        XPathWrapper wrapper = XPathWrapper.create(xp, metainfo);
        
        assertEquals(metainfo, wrapper.getMetaInfo());
    }

    /**
     * Test method for {@link com.cordys.coe.util.xml.nom.XPathWrapper#getStringValue(int)}.
     */
    public void testGetStringValue()
    {
        int root = parse("<a><b attr='b1' /><b attr='b2' /><b>b3&amp;value</b><b>b4</b></a>");

        // Node selection tests (attributes).
        assertEquals("b1", XPathWrapper.create("//b/@attr").getStringValue(root));
        assertEquals("b2", XPathWrapper.create("//b[2]/@attr").getStringValue(root));

        // Node selection tests (text node selection).
        assertEquals("b3&value", XPathWrapper.create("//b").getStringValue(root));
        assertEquals("b3&value", XPathWrapper.create("//b/text()").getStringValue(root));

        // Node selection tests (node index).
        assertEquals("b4", XPathWrapper.create("/a/b[4]").getStringValue(root));
        assertEquals("b4", XPathWrapper.create("/a/b[4]/text()").getStringValue(root));

        // Boolean value tests.
        assertEquals("true",  XPathWrapper.create("/a/b/@attr = 'b1'").getStringValue(root));
        assertEquals("true",  XPathWrapper.create("true()").getStringValue(root));
        assertEquals("false",  XPathWrapper.create("false()").getStringValue(root));

        // Number value tests.
        assertEquals("4",  XPathWrapper.create("count(//b)").getStringValue(root));
        assertEquals("1",  XPathWrapper.create("count(//b[@attr='b2'])").getStringValue(root));
    }

    /**
     * Test method for {@link com.cordys.coe.util.xml.nom.XPathWrapper#getStringValue(int, java.lang.String)}.
     */
    public void testGetStringValue_WithDefault()
    {
        int root = parse("<a><b attr='b1' /><b attr='b2' /><b>b3&amp;value</b><b>b4</b></a>");

        // Attribute selection.
        assertEquals("b1", XPathWrapper.create("//b/@attr").getStringValue(root, "DEFAULT"));
        assertEquals("DEFAULT", XPathWrapper.create("//b/@attr2").getStringValue(root, "DEFAULT"));

        // Node selection tests.
        assertEquals("b4", XPathWrapper.create("//b[4]").getStringValue(root, "DEFAULT"));
        assertEquals("DEFAULT", XPathWrapper.create("//b[5]").getStringValue(root, "DEFAULT"));

        // Boolean value tests.
        assertEquals("true",  XPathWrapper.create("true()").getStringValue(root, "DEFAULT"));

        // Number value tests.
        assertEquals("4",  XPathWrapper.create("count(//b)").getStringValue(root, "DEFAULT"));
    }

    /**
     * Test method for {@link com.cordys.coe.util.xml.nom.XPathWrapper#getAllStringValues(int)}.
     */
    public void testGetAllStringValues()
    {
        int root = parse("<a><b attr='b1' /><b attr='b2' /><b>b3&amp;value</b><b>b4</b></a>");

        // Attribute selection.
        assertArraysEqual(new String[] { "b1", "b2"}, XPathWrapper.create("//b/@attr").getAllStringValues(root));
        assertArraysEqual(new String[] { }, XPathWrapper.create("//b/@attr2").getAllStringValues(root));

        // Node selection tests.
        assertArraysEqual(new String[] { "b4" }, XPathWrapper.create("//b[4]").getAllStringValues(root));
        assertArraysEqual(new String[] { }, XPathWrapper.create("//b[5]").getAllStringValues(root));

        // Boolean value tests.
        assertArraysEqual(new String[] { "true" },  XPathWrapper.create("true()").getAllStringValues(root));

        // Number value tests.
        assertArraysEqual(new String[] { "4" },  XPathWrapper.create("count(//b)").getAllStringValues(root));
    }

    /**
     * Test method for {@link com.cordys.coe.util.xml.nom.XPathWrapper#getIntegerValue(int, int)}.
     */
    public void testGetIntegerValue()
    {
        int root = parse("<a><b a='1' /><b a='2' /><b>3</b><b>4</b><x>dummy</x><space> </space></a>");

        // Test valid number values converted from a string.
        assertEquals(1, XPathWrapper.create("//b/@a").getIntegerValue(root, -1));
        assertEquals(2, XPathWrapper.create("//b[2]/@a").getIntegerValue(root, -1));
        assertEquals(3, XPathWrapper.create("//b[3]").getIntegerValue(root, -1));
        assertEquals(4, XPathWrapper.create("//b[4]").getIntegerValue(root, -1));
        assertEquals(-1, XPathWrapper.create("//b/@attr2").getIntegerValue(root, -1));
        assertEquals(-1, XPathWrapper.create("//b[5]").getIntegerValue(root, -1));

        // Test valid number values.
        assertEquals(4,  XPathWrapper.create("count(//b)").getIntegerValue(root, -1));
        assertEquals(2,  XPathWrapper.create("count(//b[@a])").getIntegerValue(root, -1));

        // Test illegal number values.
        try {
            XPathWrapper.create("true()").getIntegerValue(root, -1);
            fail("Invalid number value passed the test.");
        }
        catch (IllegalArgumentException expected) {
        }
        try {
            XPathWrapper.create("//x").getIntegerValue(root, -1);
            fail("Invalid number value passed the test.");
        }
        catch (IllegalArgumentException expected) {
        }
        try {
            XPathWrapper.create("//space").getIntegerValue(root, -1);
            fail("Invalid number value passed the test.");
        }
        catch (IllegalArgumentException expected) {
        }
    }

    /**
     * Test method for {@link com.cordys.coe.util.xml.nom.XPathWrapper#getAllIntegerValues(int)}.
     */
    public void testGetAllIntegerValues()
    {
        int root = parse("<a><b a='1' /><b a='2' /><b>3</b><b>4</b><x>dummy</x><space> </space></a>");

        // Attribute selection.
        assertArraysEqual(new int[] { 1, 2 }, XPathWrapper.create("//b/@a").getAllIntegerValues(root));
        assertArraysEqual(new int[] { }, XPathWrapper.create("//b/@a2").getAllIntegerValues(root));

        // Node selection tests.
        assertArraysEqual(new int[] { 3, 4 }, XPathWrapper.create("//b").getAllIntegerValues(root));
        assertArraysEqual(new int[] { 4 }, XPathWrapper.create("//b[4]").getAllIntegerValues(root));
        assertArraysEqual(new int[] { }, XPathWrapper.create("//b[5]").getAllIntegerValues(root));

        // Test illegal number values.
        try {
            XPathWrapper.create("true()").getAllIntegerValues(root);
            fail("Invalid number value passed the test.");
        }
        catch (IllegalArgumentException expected) {
        }
        try {
            XPathWrapper.create("//x").getAllIntegerValues(root);
            fail("Invalid number value passed the test.");
        }
        catch (IllegalArgumentException expected) {
        }
        try {
            XPathWrapper.create("//space").getAllIntegerValues(root);
            fail("Invalid number value passed the test.");
        }
        catch (IllegalArgumentException expected) {
        }

        // Number value tests.
        assertArraysEqual(new int[] { 4 },  XPathWrapper.create("count(//b)").getAllIntegerValues(root));
    }

    /**
     * Test method for {@link com.cordys.coe.util.xml.nom.XPathWrapper#getLongValue(int, long)}.
     */
    public void testGetLongValue()
    {
        int root = parse("<a><b a='1' /><b a='2' /><b>3</b><b>4</b><x>dummy</x><space> </space></a>");

        // Test valid number values converted from a string.
        assertEquals(1L, XPathWrapper.create("//b/@a").getLongValue(root, -1L));
        assertEquals(2L, XPathWrapper.create("//b[2]/@a").getLongValue(root, -1L));
        assertEquals(3L, XPathWrapper.create("//b[3]").getLongValue(root, -1L));
        assertEquals(4L, XPathWrapper.create("//b[4]").getLongValue(root, -1L));
        assertEquals(-1L, XPathWrapper.create("//b/@attr2").getLongValue(root, -1L));
        assertEquals(-1L, XPathWrapper.create("//b[5]").getLongValue(root, -1L));

        // Test valid number values.
        assertEquals(4L,  XPathWrapper.create("count(//b)").getLongValue(root, -1L));
        assertEquals(2L,  XPathWrapper.create("count(//b[@a])").getLongValue(root, -1L));

        // Test illegal number values.
        try {
            XPathWrapper.create("true()").getLongValue(root, -1);
            fail("Invalid number value passed the test.");
        }
        catch (IllegalArgumentException expected) {
        }
        try {
            XPathWrapper.create("//x").getLongValue(root, -1);
            fail("Invalid number value passed the test.");
        }
        catch (IllegalArgumentException expected) {
        }
        try {
            XPathWrapper.create("//space").getLongValue(root, -1);
            fail("Invalid number value passed the test.");
        }
        catch (IllegalArgumentException expected) {
        }
    }

    /**
     * Test method for {@link com.cordys.coe.util.xml.nom.XPathWrapper#getAllLongValues(int)}.
     */
    public void testGetAllLongValues()
    {
        int root = parse("<a><b a='1' /><b a='2' /><b>3</b><b>4</b><x>dummy</x><space> </space></a>");

        // Attribute selection.
        assertArraysEqual(new long[] { 1, 2 }, XPathWrapper.create("//b/@a").getAllLongValues(root));
        assertArraysEqual(new long[] { }, XPathWrapper.create("//b/@a2").getAllLongValues(root));

        // Node selection tests.
        assertArraysEqual(new long[] { 3, 4 }, XPathWrapper.create("//b").getAllLongValues(root));
        assertArraysEqual(new long[] { 4 }, XPathWrapper.create("//b[4]").getAllLongValues(root));
        assertArraysEqual(new long[] { }, XPathWrapper.create("//b[5]").getAllLongValues(root));

        // Test illegal number values.
        try {
            XPathWrapper.create("true()").getAllLongValues(root);
            fail("Invalid number value passed the test.");
        }
        catch (IllegalArgumentException expected) {
        }
        try {
            XPathWrapper.create("//x").getAllLongValues(root);
            fail("Invalid number value passed the test.");
        }
        catch (IllegalArgumentException expected) {
        }
        try {
            XPathWrapper.create("//space").getAllLongValues(root);
            fail("Invalid number value passed the test.");
        }
        catch (IllegalArgumentException expected) {
        }

        // Number value tests.
        assertArraysEqual(new long[] { 4 },  XPathWrapper.create("count(//b)").getAllLongValues(root));
    }

    /**
     * Test method for {@link com.cordys.coe.util.xml.nom.XPathWrapper#getDoubleValue(int, double)}.
     */
    public void testGetDoubleValue()
    {
        int root = parse("<a><b a='1' /><b a='2' /><b>3.123</b><b>4.0000001</b><x>dummy</x><space> </space></a>");

        // Test valid number values converted from a string.
        assertEquals(1.0, XPathWrapper.create("//b/@a").getDoubleValue(root, -1));
        assertEquals(2.0, XPathWrapper.create("//b[2]/@a").getDoubleValue(root, -1));
        assertEquals(3.123, XPathWrapper.create("//b[3]").getDoubleValue(root, -1));
        assertEquals(4.0000001, XPathWrapper.create("//b[4]").getDoubleValue(root, -1));
        assertEquals(-1.0, XPathWrapper.create("//b/@attr2").getDoubleValue(root, -1));
        assertEquals(-1.0, XPathWrapper.create("//b[5]").getDoubleValue(root, -1));

        // Test valid number values.
        assertEquals(4.0,  XPathWrapper.create("count(//b)").getDoubleValue(root, -1));
        assertEquals(2.0,  XPathWrapper.create("count(//b[@a])").getDoubleValue(root, -1));

        // Test illegal number values.
        try {
            XPathWrapper.create("true()").getDoubleValue(root, -1);
            fail("Invalid number value passed the test.");
        }
        catch (IllegalArgumentException expected) {
        }
        try {
            XPathWrapper.create("//x").getDoubleValue(root, -1);
            fail("Invalid number value passed the test.");
        }
        catch (IllegalArgumentException expected) {
        }
        try {
            XPathWrapper.create("//space").getDoubleValue(root, -1);
            fail("Invalid number value passed the test.");
        }
        catch (IllegalArgumentException expected) {
        }
    }

    /**
     * Test method for {@link com.cordys.coe.util.xml.nom.XPathWrapper#getAllDoubleValues(int)}.
     */
    public void testGetAllDoubleValues()
    {
        int root = parse("<a><b a='1' /><b a='2' /><b>3.123</b><b>4.0000001</b><x>dummy</x><space> </space></a>");

        // Attribute selection.
        assertArraysEqual(new double[] { 1.0, 2.0 }, XPathWrapper.create("//b/@a").getAllDoubleValues(root));
        assertArraysEqual(new double[] { }, XPathWrapper.create("//b/@a2").getAllDoubleValues(root));

        // Node selection tests.
        assertArraysEqual(new double[] { 3.123, 4.0000001 }, XPathWrapper.create("//b").getAllDoubleValues(root));
        assertArraysEqual(new double[] { 4.0000001 }, XPathWrapper.create("//b[4]").getAllDoubleValues(root));
        assertArraysEqual(new double[] { }, XPathWrapper.create("//b[5]").getAllDoubleValues(root));

        // Test illegal number values.
        try {
            XPathWrapper.create("true()").getAllDoubleValues(root);
            fail("Invalid number value passed the test.");
        }
        catch (IllegalArgumentException expected) {
        }
        try {
            XPathWrapper.create("//x").getAllDoubleValues(root);
            fail("Invalid number value passed the test.");
        }
        catch (IllegalArgumentException expected) {
        }
        try {
            XPathWrapper.create("//space").getAllDoubleValues(root);
            fail("Invalid number value passed the test.");
        }
        catch (IllegalArgumentException expected) {
        }

        // Number value tests.
        assertArraysEqual(new double[] { 4 },  XPathWrapper.create("count(//b)").getAllDoubleValues(root));
    }

    /**
     * Test method for {@link com.cordys.coe.util.xml.nom.XPathWrapper#getBooleanValue(int, boolean)}.
     */
    public void testGetBooleanValue()
    {
        int root = parse("<a><b a='true' /><b a='false' /><b>0</b><b>no</b><x>dummy</x><space> </space></a>");

        // Test valid number values converted from a string.
        assertEquals(true, XPathWrapper.create("//b/@a").getBooleanValue(root, false));
        assertEquals(false, XPathWrapper.create("//b[2]/@a").getBooleanValue(root, true));
        assertEquals(false, XPathWrapper.create("//b[3]").getBooleanValue(root, true));
        assertEquals(false, XPathWrapper.create("//b[4]").getBooleanValue(root, true));
        assertEquals(true, XPathWrapper.create("//b/@attr2").getBooleanValue(root, true));
        assertEquals(true, XPathWrapper.create("//b[5]").getBooleanValue(root, true));

        // Test valid boolean values.
        assertEquals(true,  XPathWrapper.create("true()").getBooleanValue(root, false));
        assertEquals(false,  XPathWrapper.create("false()").getBooleanValue(root, true));

        // Test illegal boolean values.
        try {
            XPathWrapper.create("string(//b)").getBooleanValue(root, false);
            fail("Invalid boolean value passed the test.");
        }
        catch (IllegalArgumentException expected) {
        }
        try {
            XPathWrapper.create("count(//b)").getBooleanValue(root, false);
            fail("Invalid boolean value passed the test.");
        }
        catch (IllegalArgumentException expected) {
        }
        try {
            XPathWrapper.create("//x").getBooleanValue(root, false);
            fail("Invalid boolean value passed the test.");
        }
        catch (IllegalArgumentException expected) {
        }
        try {
            XPathWrapper.create("//space").getBooleanValue(root, false);
            fail("Invalid boolean value passed the test.");
        }
        catch (IllegalArgumentException expected) {
        }
    }

    /**
     * Test method for {@link com.cordys.coe.util.xml.nom.XPathWrapper#getAllBooleanValues(int)}.
     */
    public void testGetAllBooleanValues()
    {
        int root = parse("<a><b a='true' /><b a='false' /><b>0</b><b>no</b><x>dummy</x><space> </space></a>");

        // Attribute selection.
        assertArraysEqual(new boolean[] { true, false }, XPathWrapper.create("//b/@a").getAllBooleanValues(root));
        assertArraysEqual(new boolean[] { }, XPathWrapper.create("//b/@a2").getAllBooleanValues(root));

        // Node selection tests.
        assertArraysEqual(new boolean[] { false, false }, XPathWrapper.create("//b").getAllBooleanValues(root));
        assertArraysEqual(new boolean[] { false }, XPathWrapper.create("//b[4]").getAllBooleanValues(root));
        assertArraysEqual(new boolean[] { }, XPathWrapper.create("//b[5]").getAllBooleanValues(root));

        // Test illegal number values.
        try {
            XPathWrapper.create("count(//b)").getAllBooleanValues(root);
            fail("Invalid boolean value passed the test.");
        }
        catch (IllegalArgumentException expected) {
        }
        try {
            XPathWrapper.create("//x").getAllBooleanValues(root);
            fail("Invalid boolean value passed the test.");
        }
        catch (IllegalArgumentException expected) {
        }
        try {
            XPathWrapper.create("//space").getAllBooleanValues(root);
            fail("Invalid boolean value passed the test.");
        }
        catch (IllegalArgumentException expected) {
        }

        // Boolean value tests.
        assertArraysEqual(new boolean[] { true },  XPathWrapper.create("true()").getAllBooleanValues(root));
        assertArraysEqual(new boolean[] { false },  XPathWrapper.create("false()").getAllBooleanValues(root));
    }

    /**
     * Test method for {@link com.cordys.coe.util.xml.nom.XPathWrapper#selectSingleNode(int)}.
     */
    public void testSelectSingleNode()
    {
        int root = parse("<a><b a='true' /><b a='false' /><b>0</b><b>no</b><x>dummy</x><space> </space></a>");

        // Attribute selection.
        assertEquals("true", Node.getAttribute(XPathWrapper.create("//b/@a").selectSingleNode(root), "a"));
    }

    /**
     * Test method for {@link com.cordys.coe.util.xml.nom.XPathWrapper#selectNodes(int)}.
     */
    public void testSelectNodes()
    {
        int root = parse("<a><b a='true' /><b a='false' /><b>0</b><b>no</b><x>dummy</x><space> </space></a>");
        int[] res;
        
        // Attribute selection.
        res = XPathWrapper.create("//b[@a]").selectNodes(root);
        assertNotNull(res);
        assertEquals(2, res.length);
        assertEquals("true", Node.getAttribute(res[0], "a"));
        assertEquals("false", Node.getAttribute(res[1], "a"));
    }

    /**
     * Tests that the two arrays are the same by Arrays.toString()
     */
    private <T> void assertArraysEqual(T[] control, T[] test)
    {
        assertEquals(Arrays.toString(control), Arrays.toString(test));
    }

    /**
     * Tests that the two arrays are the same by Arrays.toString()
     */
    private void assertArraysEqual(int[] control, int[] test)
    {
        assertEquals(Arrays.toString(control), Arrays.toString(test));
    }

    /**
     * Tests that the two arrays are the same by Arrays.toString()
     */
    private void assertArraysEqual(long[] control, long[] test)
    {
        assertEquals(Arrays.toString(control), Arrays.toString(test));
    }

    /**
     * Tests that the two arrays are the same by Arrays.toString()
     */
    private void assertArraysEqual(double[] control, double[] test)
    {
        assertEquals(Arrays.toString(control), Arrays.toString(test));
    }

    /**
     * Tests that the two arrays are the same by Arrays.toString()
     */
    private void assertArraysEqual(boolean[] control, boolean[] test)
    {
        assertEquals(Arrays.toString(control), Arrays.toString(test));
    }
    
    /**
     * Tests that the bindings are implemented properly. This matches XML element 'a' with the given namespace.
     * 
     * @param wrapper XPathWrapper to test.
     * @paran namespace Namespace.
     */
    private void assertNamespaceBinding(XPathWrapper wrapper, String namespace)
    {
        int testNode = parse("<root><a xmlns='" + namespace + "' /></root>");
        
        assertTrue("XPathWrapper didn't match any node.", wrapper.selectSingleNode(testNode) != 0);
    }
}
