/**
 * (c) 2007 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util.soap;

import com.cordys.coe.actester.ISoapRequestInstance;
import com.cordys.coe.util.test.junit.WcpStubTestCase;
import com.eibus.soap.BodyBlock;
import com.eibus.xml.nom.Find;
import com.eibus.xml.nom.Node;

/**
 * Test case for SoapFaultInfo class.
 *
 * @author mpoyhone
 */
public class SoapFaultInfoTest extends WcpStubTestCase
{
    /**
     * Test method for {@link com.cordys.coe.util.soap.SoapFaultInfo#findSoapFault(int)}.
     */
    public void testFindSoapFault() throws Exception
    {
        String sXml;
        SoapFaultInfo info;
        
        ///////////////////////////////////////////////////
        // Test a standard SOAP:Fault.
        ///////////////////////////////////////////////////
        info = SoapFaultInfo.findSoapFault(loadXmlResource("testdata/SOAPFault_Standard.xml"));
        assertNotNull(info);
        assertEquals("soap:MustUnderstand", info.getFaultcode());
        assertEquals("Mandatory Header error.", info.getFaultstring());
        assertEquals("<detail><w:source xmlns:w=\"http://www.wrox.com/\"><module>endpoint.asp</module><line>203</line></w:source></detail>", 
                        Node.writeToString(info.getDetail(), false));
        
        ///////////////////////////////////////////////////
        // Test a Cordys C2 SOAP:Fault.
        ///////////////////////////////////////////////////
        sXml = "<SOAP:Envelope\r\n" + 
                "   xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n" + 
                "   <SOAP:Header>\r\n" + 
                "       <header>\r\n" + 
                "       </header>\r\n" + 
                "   </SOAP:Header>\r\n" + 
                "   <SOAP:Body>\r\n" + 
                "       <SOAP:Fault\r\n" + 
                "           xmlns=\"http://schemas.cordys.com/4.2/coboc\">\r\n" + 
                "           <SOAP:faultcode>Server.error</SOAP:faultcode>\r\n" + 
                "           <SOAP:faultstring>The request did not process successfully.</SOAP:faultstring>\r\n" + 
                "           <SOAP:detail>\r\n" + 
                "               <mytest>\r\n" + 
                "                   <test>abc</test>\r\n" + 
                "               </mytest>\r\n" + 
                "           </SOAP:detail>\r\n" + 
                "       </SOAP:Fault>\r\n" + 
                "   </SOAP:Body>\r\n" + 
                "</SOAP:Envelope>";
        
        info = SoapFaultInfo.findSoapFault(loadXmlResource("testdata/SOAPFault_C2.xml"));
        assertNotNull(info);
        assertEquals("Server.error", info.getFaultcode());
        assertEquals("The request did not process successfully.", info.getFaultstring());
        assertEquals("<SOAP:detail><mytest><test>abc</test></mytest></SOAP:detail>", 
                        Node.writeToString(info.getDetail(), false));     
        
        ///////////////////////////////////////////////////
        // Test a Cordys C3 SOAP:Fault.
        ///////////////////////////////////////////////////
        info = SoapFaultInfo.findSoapFault(loadXmlResource("testdata/SOAPFault_C3.xml"));
        assertNotNull(info);
        assertEquals("Server", info.getFaultcode());
        assertEquals("http://schemas.cordys.com/bpm/execution/1.0", info.getFaultactor());
        assertEquals("The request did not process successfully.", info.getFaultstring());
        assertEquals("<detail><cordys:errorcode xmlns:cordys=\"http://schemas.cordys.com/General/1.0/\">Server.exception</cordys:errorcode><cordys:errordetail xmlns:cordys=\"http://schemas.cordys.com/General/1.0/\">Exception occurred...</cordys:errordetail></detail>", 
                        Node.writeToString(info.getDetail(), false));   
        
        ///////////////////////////////////////////////////
        // Test an invalid SOAP envelope.
        ///////////////////////////////////////////////////
        int xEnvelope = parse("<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/penelope/\"></SOAP:Envelope>");
        try {
            SoapFaultInfo.findSoapFault(xEnvelope);
            fail("Invalid SOAP envelope accepted.");
        }
        catch (Exception expected) {
        }
        
        ///////////////////////////////////////////////////
        // Test a valid SOAP response.
        ///////////////////////////////////////////////////
        sXml = "<SOAP:Envelope\r\n" + 
                "   xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n" + 
                "   <SOAP:Header>\r\n" + 
                "       <header>\r\n" + 
                "       </header>\r\n" + 
                "   </SOAP:Header>\r\n" + 
                "   <SOAP:Body>\r\n" + 
                "       <GetXMLObjectResponse\r\n" + 
                "           xmlns=\"http://schemas.cordys.com/4.2/coboc\">\r\n" + 
                "           <tuple />\r\n" + 
                "       </GetXMLObjectResponse>\r\n" + 
                "   </SOAP:Body>\r\n" + 
                "</SOAP:Envelope>";
        
        info = SoapFaultInfo.findSoapFault(parse(sXml));
        assertNull(info);
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SoapFaultInfo#findSoapFaultNode(int)}.
     */
    public void testFindSoapFaultNode() throws Exception
    {
        String sXml;
        int xEnvelope;
        int xFaultNode;
        
        ///////////////////////////////////////////////////
        // Test a standard SOAP:Fault.
        ///////////////////////////////////////////////////
        xEnvelope = loadXmlResource("testdata/SOAPFault_Standard.xml");
        xFaultNode = SoapFaultInfo.findSoapFaultNode(xEnvelope);
        assertTrue(xFaultNode != 0);
        assertEquals(Find.firstMatch(xEnvelope, "<>lChild<><>"), xFaultNode);
        
        ///////////////////////////////////////////////////
        // Test a Cordys C2 SOAP:Fault.
        ///////////////////////////////////////////////////
        xEnvelope = loadXmlResource("testdata/SOAPFault_C2.xml");
        xFaultNode = SoapFaultInfo.findSoapFaultNode(xEnvelope);
        assertTrue(xFaultNode != 0);
        assertEquals(Find.firstMatch(xEnvelope, "<>lChild<><>"), xFaultNode);
        
        ///////////////////////////////////////////////////
        // Test an invalid SOAP envelope.
        ///////////////////////////////////////////////////
        xEnvelope = parse("<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/penelope/\"></SOAP:Envelope>");
        try {
            SoapFaultInfo.findSoapFaultNode(xEnvelope);
            fail("Invalid SOAP envelope accepted.");
        }
        catch (Exception expected) {
        }
        
        ///////////////////////////////////////////////////
        // Test a valid SOAP response.
        ///////////////////////////////////////////////////
        sXml = "<SOAP:Envelope\r\n" + 
                "   xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n" + 
                "   <SOAP:Header>\r\n" + 
                "       <header>\r\n" + 
                "       </header>\r\n" + 
                "   </SOAP:Header>\r\n" + 
                "   <SOAP:Body>\r\n" + 
                "       <GetXMLObjectResponse\r\n" + 
                "           xmlns=\"http://schemas.cordys.com/4.2/coboc\">\r\n" + 
                "           <tuple />\r\n" + 
                "       </GetXMLObjectResponse>\r\n" + 
                "   </SOAP:Body>\r\n" + 
                "</SOAP:Envelope>";
        
        xEnvelope = parse(sXml);
        xFaultNode = SoapFaultInfo.findSoapFaultNode(xEnvelope);
        assertTrue(xFaultNode == 0);
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SoapFaultInfo#dispose()}.
     */
    public void testDispose() throws Exception
    {
        SoapFaultInfo info = new SoapFaultInfo();
        
        info.setFaultcode("code");
        info.setFaultstring("string");
        info.setDetail(1234);
        info.dispose();
        assertNull(info.getFaultcode());
        assertNull(info.getFaultstring());
        assertTrue(info.getDetail() == 0);
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SoapFaultInfo#getDetail()}.
     */
    public void testGetDetail() throws Exception
    {
        SoapFaultInfo info = new SoapFaultInfo();
        
        // Insert a dummy NOM handle.
        info.setDetail(1234);
        assertTrue(info.getDetail() == 1234);
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SoapFaultInfo#getFaultcode()}.
     */
    public void testGetFaultcode() throws Exception
    {
        SoapFaultInfo info = new SoapFaultInfo();
        
        info.setFaultcode("code");
        assertEquals("code", info.getFaultcode());
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SoapFaultInfo#getFaultcode()}.
     */
    public void testGetFaultactor() throws Exception
    {
        SoapFaultInfo info = new SoapFaultInfo();
        
        info.setFaultactor("actor");
        assertEquals("actor", info.getFaultactor());
    }
    
    /**
     * Test method for {@link com.cordys.coe.util.soap.SoapFaultInfo#getFaultstring()}.
     */
    public void testGetFaultstring() throws Exception
    {
        SoapFaultInfo info = new SoapFaultInfo();
        
        info.setFaultstring("string");
        assertEquals("string", info.getFaultstring());
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SoapFaultInfo#toString()}.
     */
    public void testToString() throws Exception
    {
        SoapFaultInfo info;
        String sXml;
        String sContent;
        
        // Test XML detail node.
        sContent = "<w:source xmlns:w=\"http://www.wrox.com/\"><module>endpoint.asp</module><line>203</line></w:source>"; 
        sXml = "<detail>" + sContent + "</detail>";
        
        info = new SoapFaultInfo();
        info.setFaultcode("code");
        info.setFaultactor("actor");
        info.setFaultstring("string");
        info.setDetail(parse(sXml));
        assertEquals(String.format("[faultcode='code', faultactor='actor', faultstring='string', detail='%s']", sContent),
                     info.toString());
        
        // Test test detail node.
        sContent = "This is an error."; 
        sXml = "<detail>" + sContent + "</detail>";
        
        info = new SoapFaultInfo();
        info.setFaultcode("code");
        info.setFaultactor("actor");
        info.setFaultstring("string");
        info.setDetail(parse(sXml));
        assertEquals(String.format("[faultcode='code', faultactor='actor', faultstring='string', detail='%s']", sContent),
                     info.toString());        
    }
    
    /**
     * Test method for {@link com.cordys.coe.util.soap.SoapFaultInfo.createConnectorSoapFault(BodyBlock, Throwable, boolean)}.
     */
    public void testCreateConnectorSoapFaultDetailNode() throws Exception {
        String responseXml = "<soap-request id=\"Template\">\r\n" + 
                "<implementation/>\r\n" + 
                "<request-xml>\r\n" + 
                "  <SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n" + 
                "   <SOAP:Body>\r\n" + 
                "     <Response/>\r\n" + 
                "   </SOAP:Body>\r\n" + 
                "  </SOAP:Envelope>\r\n" + 
                "</request-xml>\r\n" + 
                "</soap-request>"; 
        ISoapRequestInstance req = stubFactory.createSoapRequestInstance(parse(responseXml));
        BodyBlock bb = req.getResponse();
        String detailXml;
        SoapFaultInfo info;
        
        try {
            ///////////////////////////////////////////////////
            // Test with a no SOAP detail element.
            ///////////////////////////////////////////////////
            info = new SoapFaultInfo("FAULT-CODE", "FAULT-STRING");
            info.createConnectorSoapFault(bb);
            assertEquals("<SOAP:Fault><faultcode>FAULT-CODE</faultcode><faultstring>FAULT-STRING</faultstring><detail/></SOAP:Fault>", 
                            Node.writeToString(bb.getXMLNode(), false));
            
            ///////////////////////////////////////////////////
            // Test with a single SOAP detail element.
            ///////////////////////////////////////////////////
            detailXml = "<detail><mydata>test</mydata></detail>";
            info = new SoapFaultInfo("FAULT-CODE", "FAULT-STRING");
            info.setDetail(parse(detailXml));
            info.createConnectorSoapFault(bb);
            assertEquals("<SOAP:Fault><faultcode>FAULT-CODE</faultcode><faultstring>FAULT-STRING</faultstring>" + detailXml + "</SOAP:Fault>", 
                            Node.writeToString(bb.getXMLNode(), false));
            
            ///////////////////////////////////////////////////
            // Test with two SOAP detail elements.
            ///////////////////////////////////////////////////
            detailXml = "<detail><mydata>test</mydata><another>123</another></detail>";
            info = new SoapFaultInfo("FAULT-CODE", "FAULT-STRING");
            info.setDetail(parse(detailXml));
            info.createConnectorSoapFault(bb);
            assertEquals("<SOAP:Fault><faultcode>FAULT-CODE</faultcode><faultstring>FAULT-STRING</faultstring>" + detailXml + "</SOAP:Fault>", 
                            Node.writeToString(bb.getXMLNode(), false));    
            
            ///////////////////////////////////////////////////
            // Test with an exception in SOAP detail element.
            ///////////////////////////////////////////////////
            info = new SoapFaultInfo("FAULT-CODE", "FAULT-STRING");
            info.createConnectorSoapFault(bb, new Exception("MyFailureString"), false);
            assertTrue(Node.writeToString(bb.getXMLNode(), false).contains("java.lang.Exception: MyFailureString"));
            
            ////////////////////////////////////////////////////////
            // Test with an XML stack trace in SOAP detail element.
            ////////////////////////////////////////////////////////
            info = new SoapFaultInfo("FAULT-CODE", "FAULT-STRING");
            info.createConnectorSoapFault(bb, new Exception("MyFailureString"), true);
            assertTrue(Node.writeToString(bb.getXMLNode(), false).contains("<exception class=\"java.lang.Exception\" message=\"MyFailureString\"><method class=\"com.cordys.coe.util.soap.SoapFaultInfoTest\""));            
        } 
        finally {
            // Clean the NOM nodes.
            req.clear();
        }
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SoapFaultInfo.createConnectorSoapFault(BodyBlock, Throwable, boolean)}.
     */
    public void testCreateConnectorSoapFaultDetailNodeCloning() throws Exception {
        String responseXml = "<soap-request id=\"Template\">\r\n" + 
                "<implementation/>\r\n" + 
                "<request-xml>\r\n" + 
                "  <SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n" + 
                "   <SOAP:Body>\r\n" + 
                "     <Response/>\r\n" + 
                "   </SOAP:Body>\r\n" + 
                "  </SOAP:Envelope>\r\n" + 
                "</request-xml>\r\n" + 
                "</soap-request>"; 
        ISoapRequestInstance req = stubFactory.createSoapRequestInstance(parse(responseXml));
        BodyBlock bb = req.getRequest();
        String detailXml;
        SoapFaultInfo info;
        
        ///////////////////////////////////////////////////
        // Test with a detail node children cloned
        ///////////////////////////////////////////////////
        detailXml = "<detail><mydata>test</mydata></detail>";
        info = new SoapFaultInfo("FAULT-CODE", "FAULT-STRING");
        info.setDetail(parse(detailXml));
        info.createConnectorSoapFault(bb, true);
        assertEquals(detailXml, Node.writeToString(info.getDetail(), false));
        
        ///////////////////////////////////////////////////
        // Test with a detail node children moved
        ///////////////////////////////////////////////////
        detailXml = "<detail><mydata>test</mydata></detail>";
        info = new SoapFaultInfo("FAULT-CODE", "FAULT-STRING");
        info.setDetail(parse(detailXml));
        info.createConnectorSoapFault(bb, false);
        assertEquals("<detail/>", Node.writeToString(info.getDetail(), false)); 
        
        // Clean the NOM nodes.
        req.clear();
    }
}
