package com.cordys.coe.util.xml.dom;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import junit.framework.TestCase;

/**
 * The Class XPathHelperTest.
 */
public class XPathHelperTest extends TestCase
{
    /**
     * Test set string value.
     * 
     * @throws Exception the exception
     */
    public void testSetStringValue() throws Exception
    {
        Document doc = XMLHelper.createDocumentFromStream(XPathHelperTest.class.getResourceAsStream("setstringvalue.xml"));
        Element root = doc.getDocumentElement();
        
        boolean tmp = false;

        //Using the /text()
        tmp = XPathHelper.setStringValue(root, "//tag_no_ns/text()", "ChangedValue");
        if (tmp == false)
        {
            fail("Setting failed!");
        }
        
        assertEquals("Change value is not correctly read back.", true, "ChangedValue".equals(XPathHelper.getStringValue(root, "//tag_no_ns/text()")));
        
        //Implicitly using the text()
        tmp = XPathHelper.setStringValue(root, "//tag_no_ns", "ChangedValue");
        if (tmp == false)
        {
            fail("Setting failed!");
        }

        assertEquals("Change value is not correctly read back.", true, "ChangedValue".equals(XPathHelper.getStringValue(root, "//tag_no_ns")));
        
        //Using a non-existent tag.
        tmp = XPathHelper.setStringValue(root, "//nonsense_tag/text()", "ChangedValue");
        assertEquals("When no tag was found the method should return false.", false, tmp);
    }
}
