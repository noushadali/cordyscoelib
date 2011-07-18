/**
 * (c) 2007 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util.xml;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Node;

/**
 * Test cases for XMLHelpers class.
 *
 * @author mpoyhone
 */
public class XMLHelpersTest extends TestCase
{
    private static Document dDoc = new Document();
    private List<Integer> lGarbage = new ArrayList<Integer>(50);
    
    
    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        for (int iNode : lGarbage)
        {
            Node.delete(iNode);
        }
        
        lGarbage.clear();
        
        super.tearDown();
    }

    private int parse(String sXml) throws Exception {
        int iNode = dDoc.parseString(sXml);
        
        lGarbage.add(iNode);
        
        return iNode;
    }

    /**
     * Test method for {@link com.cordys.coe.util.xml.XMLHelpers#getNamespacePrefix(int, java.lang.String)}.
     */
    public void testGetNamespacePrefix() throws Exception
    {
        String sXml;
        String sPrefix;
        
        ///////////////////////////////////////////////////
        // Test a simple SOAP message.
        ///////////////////////////////////////////////////
        sXml = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n" + 
                "  <soap:Body xmlns:m=\"http://www.example.org/stock\">\r\n" + 
                "    <m:GetStockPrice>\r\n" + 
                "      <m:StockName>IBM</m:StockName>\r\n" + 
                "    </m:GetStockPrice>\r\n" + 
                "  </soap:Body>\r\n" + 
                "</soap:Envelope>";
        
        sPrefix = XMLHelpers.getNamespacePrefix(parse(sXml), "http://schemas.xmlsoap.org/soap/envelope/");
        assertEquals("soap:", sPrefix);
        
        ///////////////////////////////////////////////////
        // Test a simple SOAP message with another namespace.
        ///////////////////////////////////////////////////
        sXml = "<MYNS:Envelope xmlns:MYNS=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n" + 
                "  <MYNS:Body xmlns:m=\"http://www.example.org/stock\">\r\n" + 
                "    <m:GetStockPrice>\r\n" + 
                "      <m:StockName>IBM</m:StockName>\r\n" + 
                "    </m:GetStockPrice>\r\n" + 
                "  </MYNS:Body>\r\n" + 
                "</MYNS:Envelope>";
        
        sPrefix = XMLHelpers.getNamespacePrefix(parse(sXml), "http://schemas.xmlsoap.org/soap/envelope/");
        assertEquals("MYNS:", sPrefix);         
        
        ///////////////////////////////////////////////////
        // Test the default namespace
        ///////////////////////////////////////////////////
        sXml = "<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n" + 
                "  <Body xmlns:m=\"http://www.example.org/stock\">\r\n" + 
                "  </Body>\r\n" + 
                "</Envelope>";
        
        sPrefix = XMLHelpers.getNamespacePrefix(parse(sXml), "http://schemas.xmlsoap.org/soap/envelope/");
        assertEquals("", sPrefix);
        
        ///////////////////////////////////////////////////
        // Test the a wrong namespace
        ///////////////////////////////////////////////////
        sXml = "<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/nomatch\">\r\n" + 
                "  <Body xmlns:m=\"http://www.example.org/stock\">\r\n" + 
                "  </Body>\r\n" + 
                "</Envelope>";
        
        sPrefix = XMLHelpers.getNamespacePrefix(parse(sXml), "http://schemas.xmlsoap.org/soap/envelope/");
        assertNull(sPrefix);      
        
        ///////////////////////////////////////////////////
        // Test a wrong namespace with a prefix.
        ///////////////////////////////////////////////////
        sXml = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/nomatch\">\r\n" + 
                "  <soap:Body xmlns:m=\"http://www.example.org/stock\">\r\n" + 
                "  </soap:Body>\r\n" + 
                "</soap:Envelope>";   
        
        sPrefix = XMLHelpers.getNamespacePrefix(parse(sXml), "http://schemas.xmlsoap.org/soap/envelope/");
        assertNull(sPrefix);  
        
        ///////////////////////////////////////////////////
        // Test with extra namespaces
        ///////////////////////////////////////////////////
        sXml = "<soap:Envelope xmlns:test1=\"testing\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n" + 
                "  <soap:Body xmlns:m=\"http://www.example.org/stock\">\r\n" + 
                "  </soap:Body>\r\n" + 
                "</soap:Envelope>";   
        
        sPrefix = XMLHelpers.getNamespacePrefix(parse(sXml), "http://schemas.xmlsoap.org/soap/envelope/");
        assertEquals("soap:", sPrefix);       

        sXml = "<soap:Envelope xmlns:test1=\"testing\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:test2=\"testing\">\r\n" + 
        "  <soap:Body xmlns:m=\"http://www.example.org/stock\">\r\n" + 
        "  </soap:Body>\r\n" + 
        "</soap:Envelope>";   

        sPrefix = XMLHelpers.getNamespacePrefix(parse(sXml), "http://schemas.xmlsoap.org/soap/envelope/");
        assertEquals("soap:", sPrefix);            
    }

}
