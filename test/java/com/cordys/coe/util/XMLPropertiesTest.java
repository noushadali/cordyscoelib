package com.cordys.coe.util;

import com.eibus.xml.nom.Document;

import junit.framework.TestCase;

/**
 * This class contains testcases for the XMLProperties class.
 *
 * @author  pgussow
 */
public class XMLPropertiesTest extends TestCase
{
    /**
     * Holds the document to use.
     */
    private Document m_dDoc;

    /**
     * This method tests the property class without a namespace.
     *
     * @throws  Exception  In case of parse exceptions.
     */
    public void testDefaultNamespaces()
                               throws Exception
    {
        int iXML = m_dDoc.parseString("<object xmlns=\"http://some.com\" attr1=\"attrvalue\"><tag1>value1</tag1><tag2>2</tag2><nested attr1=\"attrnested\">" +
                                      "<tag1>nested1</tag1><tag2>22</tag2></nested></object>");

        XMLProperties xp = new XMLProperties(iXML);

        assertEquals("attrvalue", xp.getAttrStringValue("attr1"));
        assertEquals("value1", xp.getStringValue("tag1"));
        assertEquals(2, xp.getIntegerValue("tag2"));

        XMLProperties xp1 = xp.getXMLProperties("nested");

        assertEquals("attrnested", xp1.getAttrStringValue("attr1"));
        assertEquals("nested1", xp1.getStringValue("tag1"));
        assertEquals(22, xp1.getIntegerValue("tag2"));
    }

    /**
     * This method tests the property class without a namespace.
     *
     * @throws  Exception  In case of parse exceptions.
     */
    public void testNamespacePrefixes()
                               throws Exception
    {
        int iXML = m_dDoc.parseString("<main:object xmlns:main=\"http://domain.com\" attr1=\"attrvalue\">" +
                                      "<main:tag1>value1</main:tag1><main:tag2>2</main:tag2>" +
                                      "<main:nested attr1=\"attrnested\" xmlns:nested=\"http://domain.com\">" +
                                      "<nested:tag1>nested1</nested:tag1><nested:tag2>22</nested:tag2>" +
                                      "</main:nested></main:object>");

        XMLProperties xp = new XMLProperties(iXML);

        assertEquals("attrvalue", xp.getAttrStringValue("attr1"));
        assertEquals("value1", xp.getStringValue("tag1"));
        assertEquals(2, xp.getIntegerValue("tag2"));

        XMLProperties xp1 = xp.getXMLProperties("nested");

        assertEquals("attrnested", xp1.getAttrStringValue("attr1"));
        assertEquals("nested1", xp1.getStringValue("tag1"));
        assertEquals(22, xp1.getIntegerValue("tag2"));
    }

    /**
     * This method tests the property class without a namespace.
     *
     * @throws  Exception  In case of parse exceptions.
     */
    public void testWithoutNamespaces()
                               throws Exception
    {
        int iXML = m_dDoc.parseString("<object attr1=\"attrvalue\"><tag1>value1</tag1><tag2>2</tag2><nested attr1=\"attrnested\">" +
                                      "<tag1>nested1</tag1><tag2>22</tag2></nested></object>");

        XMLProperties xp = new XMLProperties(iXML);

        assertEquals("attrvalue", xp.getAttrStringValue("attr1"));
        assertEquals("value1", xp.getStringValue("tag1"));
        assertEquals(2, xp.getIntegerValue("tag2"));

        XMLProperties xp1 = xp.getXMLProperties("nested");

        assertEquals("attrnested", xp1.getAttrStringValue("attr1"));
        assertEquals("nested1", xp1.getStringValue("tag1"));
        assertEquals(22, xp1.getIntegerValue("tag2"));
    }

    /**
     * Sets up the testcase. It will create the NOM document.
     *
     * @throws  Exception
     *
     * @see     junit.framework.TestCase#setUp()
     */
    @Override protected void setUp()
                            throws Exception
    {
        m_dDoc = new Document();
    }
}
