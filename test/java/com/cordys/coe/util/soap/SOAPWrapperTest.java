/**
 * (c) 2007 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util.soap;

import com.cordys.coe.actester.IConnectorStub;
import com.cordys.coe.actester.MessageWaitData;
import com.cordys.coe.util.FileUtils;
import com.cordys.coe.util.template.VariableContext;
import com.cordys.coe.util.template.XmlTemplate;
import com.cordys.coe.util.test.junit.WcpStubTestCase;
import com.eibus.connector.nom.Connector;
import com.eibus.xml.nom.Find;
import com.eibus.xml.nom.Node;

/**
 * Test cases for SOAPWrapper class.
 *
 * @author mpoyhone
 */
@SuppressWarnings("deprecation")
public class SOAPWrapperTest extends WcpStubTestCase
{
    /**
     * Connector instance for all tests.
     */
    private static Connector cConnector;
    /**
     * Contains the parse SOAP message template.
     */
    private static XmlTemplate xtSoapMessageTemplate;
    /**
     * Array of files for different SOAP:Faults.
     */
    private static String[] soapFaultFiles = {
            "testdata/SOAPFault_C2.xml",
            "testdata/SOAPFault_C3.xml",
            "testdata/SOAPFault_Standard.xml",
    };
    
    static {
        try
        {
            cConnector = Connector.getInstance("SOAPWrapperTest");
        }
        catch (Exception e)
        {
            throw new IllegalStateException("Unable to create a new connector instance.", e);
        }
        
        try
        {
            String sXml = FileUtils.readTextResourceContents("testdata/SoapMessageTemplate.xml", SOAPWrapperTest.class);
            
            xtSoapMessageTemplate = XmlTemplate.createFromXml(sXml, dDoc);
        }
        catch (Exception e)
        {
            throw new IllegalStateException("Unable to parse the SOAP request template.", e);
        }
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SOAPWrapper#SOAPWrapper()}.
     */
    public void testSOAPWrapper() throws Exception
    {
        SOAPWrapper sw = new SOAPWrapper();
        
        assertTrue(sw.getConnector().isOpen());
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SOAPWrapper#SOAPWrapper(com.eibus.connector.nom.Connector)}.
     */
    public void testSOAPWrapperConnector() throws Exception
    {
        SOAPWrapper sw = new SOAPWrapper(cConnector);
        
        cConnector.close();
        assertTrue(sw.getConnector() == cConnector);
        assertTrue(sw.getConnector().isClosed());
        cConnector.open();
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SOAPWrapper#getBody(int)}.
     */
    public void testGetBody() throws Exception
    {
        String xml;
        int envelope;
        
        ///////////////////////////////////////////////////
        // Test with a set user.
        ///////////////////////////////////////////////////
        xml =
            "<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\">" + 
            "<SOAP:Header>" + 
            "<header xmlns=\"http://schemas.cordys.com/General/1.0/\">" + 
            "<sender><user>DUMMYUSER</user></sender>" + 
            "</header>" + 
            "</SOAP:Header>" + 
            "<SOAP:Body></SOAP:Body>" + 
            "</SOAP:Envelope>";
        envelope = parse(xml);
        
        assertEquals(Find.firstMatch(envelope, "<><SOAP:Body>"), SOAPWrapper.getBody(envelope));
    }
    
    /**
     * Test method for {@link com.cordys.coe.util.soap.SOAPWrapper#getBody(int)}.
     */
    public void testDifferentPrefixGetBody() throws Exception
    {
        String xml;
        int envelope;
        
        ///////////////////////////////////////////////////
        // Test with a set user.
        ///////////////////////////////////////////////////
        xml =
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" + 
            "<soapenv:Header>" + 
            "<header xmlns=\"http://schemas.cordys.com/General/1.0/\">" + 
            "<sender><user>DUMMYUSER</user></sender>" + 
            "</header>" + 
            "</soapenv:Header>" + 
            "<soapenv:Body></soapenv:Body>" + 
            "</soapenv:Envelope>";
        envelope = parse(xml);
        
        assertEquals(Find.firstMatch(envelope, "<><soapenv:Body>"), SOAPWrapper.getBody(envelope));
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SOAPWrapper#getEnvelope(int)}.
     */
    public void testGetEnvelope() throws Exception
    {
        String xml;
        int envelope;
        
        ///////////////////////////////////////////////////
        // Test with a set user.
        ///////////////////////////////////////////////////
        xml =
            "<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\">" + 
            "<SOAP:Header>" + 
            "<header xmlns=\"http://schemas.cordys.com/General/1.0/\">" + 
            "<sender><user>DUMMYUSER</user></sender>" + 
            "</header>" + 
            "</SOAP:Header>" + 
            "<SOAP:Body><method xmlns='dummymethodns'/></SOAP:Body>" + 
            "</SOAP:Envelope>";
        envelope = parse(xml);
        
        assertEquals(envelope, SOAPWrapper.getEnvelope(Find.firstMatch(envelope, "<><SOAP:Body><>")));
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SOAPWrapper#getRequestUser(int)}.
     */
    public void testGetRequestUser() throws Exception
    {
        String xml;
        int envelope;
        
        ///////////////////////////////////////////////////
        // Test with a set user.
        ///////////////////////////////////////////////////
        xml =
            "<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\">" + 
            "<SOAP:Header>" + 
            "<header xmlns=\"http://schemas.cordys.com/General/1.0/\">" + 
            "<sender><user>DUMMYUSER</user></sender>" + 
            "</header>" + 
            "</SOAP:Header>" + 
            "<SOAP:Body></SOAP:Body>" + 
            "</SOAP:Envelope>";
        envelope = parse(xml);
        
        assertEquals(SOAPWrapper.getRequestUser(envelope), "DUMMYUSER");
        
        ///////////////////////////////////////////////////
        // Test with a missing user element.
        ///////////////////////////////////////////////////
        xml =
            "<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\">" + 
            "<SOAP:Header>" + 
            "<header xmlns=\"http://schemas.cordys.com/General/1.0/\">" + 
            "<sender></sender>" + 
            "</header>" + 
            "</SOAP:Header>" + 
            "<SOAP:Body></SOAP:Body>" + 
            "</SOAP:Envelope>";
        envelope = parse(xml);
        
        assertEquals(SOAPWrapper.getRequestUser(envelope), "");        
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SOAPWrapper#setRequestUser(int, java.lang.String)}.
     */
    public void setRequestUser() throws Exception
    {
        String xml;
        int envelope;
        
        ///////////////////////////////////////////////////
        // Test with a set user element (it must be deleted).
        ///////////////////////////////////////////////////
        xml =
            "<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\">" + 
            "<SOAP:Header>" + 
            "<header xmlns=\"http://schemas.cordys.com/General/1.0/\">" + 
            "<sender><user>OLDDUMMY</user></sender>" + 
            "</header>" + 
            "</SOAP:Header>" +
            "<SOAP:Body></SOAP:Body>" + 
            "</SOAP:Envelope>";
        envelope = parse(xml);
        
        SOAPWrapper.setRequestUser(envelope, "DUMMYUSER");
        assertEquals(Node.getData(Find.firstMatch(envelope, "<><><sender>")), "DUMMYUSER");
        assertEquals(Node.getData(Find.firstMatch(envelope, "<><><sender><user>")), "DUMMYUSER");
        
        ///////////////////////////////////////////////////
        // Test with no user element.
        ///////////////////////////////////////////////////
        xml =
            "<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\">" + 
            "<SOAP:Header>" + 
            "<header xmlns=\"http://schemas.cordys.com/General/1.0/\">" + 
            "<sender></sender>" + 
            "</header>" + 
            "</SOAP:Header>" + 
            "<SOAP:Body></SOAP:Body>" + 
            "</SOAP:Envelope>";
        envelope = parse(xml);
        
        SOAPWrapper.setRequestUser(envelope, "DUMMYUSER");
        assertEquals(Node.getData(Find.firstMatch(envelope, "<><><sender>")), "DUMMYUSER");        
        assertEquals(Node.getData(Find.firstMatch(envelope, "<><><sender><user>")), "DUMMYUSER");
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SOAPWrapper#addMethod(int, java.lang.String, java.lang.String)}.
     */
    public void testAddMethod() throws Exception
    {
        VariableContext vcContext; 
        ISOAPWrapper sw = new SOAPWrapper();
        String methodname = "MyMethod";
        String namespace = "http://mynamespace.org/1.0/test/case";
        int methodNode;
     
        ///////////////////////////////////////////////////
        // Test with no existing message.
        ///////////////////////////////////////////////////
        try {
            int controlEnvelope;  
            
            vcContext = new VariableContext(dDoc);
            vcContext.setVariable("METHOD", "<MyMethod xmlns=\"http://mynamespace.org/1.0/test/case\"/>");
            controlEnvelope = xtSoapMessageTemplate.renderTemplate(vcContext);
            addNomGarbage(controlEnvelope);

            methodNode = sw.addMethod(0, methodname, namespace);
            
            assertNodesEqual(controlEnvelope, Node.getRoot(methodNode));
        }
        finally {
            sw.freeXMLNodes();
        }
        
        ///////////////////////////////////////////////////
        // Test with one existing message.
        ///////////////////////////////////////////////////
        try {
            int existingEnvelope;  
            
            vcContext = new VariableContext(dDoc);
            vcContext.setVariable("METHOD", "<ExistingMethod xmlns=\"http://mynamespace.org/1.0/test/case\"/>");
            existingEnvelope = xtSoapMessageTemplate.renderTemplate(vcContext);
            addNomGarbage(existingEnvelope);
            
            int controlEnvelope;  
            
            vcContext = new VariableContext(dDoc);
            vcContext.setVariable("METHOD", "<ExistingMethod xmlns=\"http://mynamespace.org/1.0/test/case\"/>");
            vcContext.setVariable("METHOD2",  "<MyMethod xmlns=\"http://mynamespace.org/1.0/test/case\"/>");
            controlEnvelope = xtSoapMessageTemplate.renderTemplate(vcContext);
            addNomGarbage(controlEnvelope);
            
            methodNode = sw.addMethod(existingEnvelope, methodname, namespace);
            
            assertNodesEqual(controlEnvelope, Node.getRoot(methodNode));      
        }
        finally {
            sw.freeXMLNodes();
        }
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SOAPWrapper#addXMLGarbage(int)}.
     */
    public void testAddXMLGarbage() throws Exception
    {
        int countBefore = dDoc.getNumUsedNodes(false);
        int node = parseNoGarbage("<xml/>");
        ISOAPWrapper sw = new SOAPWrapper(cConnector);
        
        sw.addXMLGarbage(node);
        
        int countBeforeDelete = dDoc.getNumUsedNodes(false);
        
        assertEquals(countBefore + 1, countBeforeDelete);
        
        sw.freeXMLNodes();
        
        int countAfterDelete = dDoc.getNumUsedNodes(false);
        
        assertEquals(countBefore, countAfterDelete);
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SOAPWrapper#createSoapMethod(java.lang.String, java.lang.String)}.
     */
    public void testCreateSoapMethodStringString() throws Exception
    {
        //TODO: fail("Not yet implemented");
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SOAPWrapper#createSoapMethod(java.lang.String, java.lang.String, java.lang.String)}.
     */
    public void testCreateSoapMethodStringStringString() throws Exception
    {
        //TODO: fail("Not yet implemented");
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SOAPWrapper#createSoapMethod(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
     */
    public void testCreateSoapMethodStringStringStringString() throws Exception
    {
        //TODO: fail("Not yet implemented");
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SOAPWrapper#createSoapMethod(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
     */
    public void testCreateSoapMethodStringStringStringStringString() throws Exception
    {
        //TODO: fail("Not yet implemented");
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SOAPWrapper#getOrganization()}.
     */
    public void testGetOrganization() throws Exception
    {
        ISOAPWrapper sw = new SOAPWrapper(cConnector);
        String dn = "myorg";
        
        sw.setOrganization(dn);
        assertEquals(sw.getOrganization(), dn);
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SOAPWrapper#getTimeOut()}.
     */
    public void testGetTimeOut() throws Exception
    {
        ISOAPWrapper sw = new SOAPWrapper(cConnector);
        
        sw.setTimeOut(123);
        assertEquals(sw.getTimeOut(), 123);
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SOAPWrapper#getUser()}.
     */
    public void testGetUser() throws Exception
    {
        ISOAPWrapper sw = new SOAPWrapper(cConnector);
        String dn = "myuserdn";
        
        sw.setUser(dn);
        assertEquals(sw.getUser(), dn);
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SOAPWrapper#isCollecting()}.
     */
    public void testIsCollecting() throws Exception
    {
        ISOAPWrapper sw = new SOAPWrapper(cConnector);
        
        sw.setCollecting(true);
        assertEquals(sw.isCollecting(), true);

        sw.setCollecting(false);
        assertEquals(sw.isCollecting(), false);
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SOAPWrapper#sendAndWait(int)}.
     */
    public void testSendAndWaitInt() throws Exception
    {
        VariableContext vcContext;
        IConnectorStub connStub = stubFactory.convertConnector(cConnector);
        ISOAPWrapper sw = new SOAPWrapper(cConnector);
     
        ///////////////////////////////////////////////////
        // Test with request and response.
        ///////////////////////////////////////////////////
        try {
            int controlRequestEnvelope;
            int controlResponseEnvelope;
            
            vcContext = new VariableContext(dDoc);
            vcContext.setVariable("METHOD", 
                    "<MyMethod xmlns=\"http://mynamespace.org/1.0/test/case\">" +
                    "<data>here is the data</data>" +
                    "</MyMethod>" );
            controlRequestEnvelope = xtSoapMessageTemplate.renderTemplate(vcContext);
            addNomGarbage(controlRequestEnvelope);
            
            vcContext.setVariable("METHOD", 
                    "<MyMethodResponse xmlns=\"http://mynamespace.org/1.0/test/case\">" +
                    "<responsedata>here is the response data</responsedata>" +
                    "</MyMethodResponse>");
            controlResponseEnvelope = xtSoapMessageTemplate.renderTemplate(vcContext);
            
            // First put the response into the connector.
            connStub.insertResponse(controlResponseEnvelope, 1);

            // Mark that we are waiting for the request.
            MessageWaitData requestWait = connStub.createConnectorRequestWaitObject(controlRequestEnvelope, 1);

            // Then send the request.
            int actualResponseEnvelope = sw.sendAndWait(controlRequestEnvelope);
            int actualRequestEnvelope = requestWait.getResponseNode();
            
            // Check the results. The connector request should be the same as the control request
            // and the connector response should be the same as the control response.
            assertTrue(actualRequestEnvelope != 0);
            assertNodesEqual(controlRequestEnvelope, actualRequestEnvelope);
            assertNodesEqual(controlResponseEnvelope, actualResponseEnvelope);
        }
        finally {
            sw.freeXMLNodes();
        }
    }
    
    /**
     * Test method for {@link com.cordys.coe.util.soap.SOAPWrapper#sendAndWait(int)}.
     */
    public void testSendAndWaitInt_SOAPFault() throws Exception
    {
        VariableContext vcContext;
        IConnectorStub connStub = stubFactory.convertConnector(cConnector);
     
        ///////////////////////////////////////////////////
        // Test with request and SOAP:Fault.
        ///////////////////////////////////////////////////
        for (String faultFile : soapFaultFiles)
        {
            ISOAPWrapper sw = new SOAPWrapper(cConnector);
            
            try {
                int controlRequestEnvelope;
                int controlFaultEnvelope = loadXmlResourceNoGarbage(faultFile);
                
                vcContext = new VariableContext(dDoc);
                vcContext.setVariable("METHOD", 
                        "<MyMethod xmlns=\"http://mynamespace.org/1.0/test/case\">" +
                        "<data>here is the data</data>" +
                        "</MyMethod>" );
                controlRequestEnvelope = xtSoapMessageTemplate.renderTemplate(vcContext);
                addNomGarbage(controlRequestEnvelope);
                
                // First put the response into the connector.
                connStub.insertResponse("MyMethodResponse", "http://mynamespace.org/1.0/test/case", controlFaultEnvelope, 1);
    
                // Mark that we are waiting for the request.
                connStub.createConnectorRequestWaitObject(controlRequestEnvelope, 1);
    
                // Then send the request. This should throw a SOAPException.
                try {
                    sw.sendAndWait(controlRequestEnvelope);
                    fail("sendAndWait didn't throw an exception on SOAP:Fault. Fault file: " + faultFile);
                } 
                catch (SOAPException expected) {
                    
                }
            }
            finally {
                sw.freeXMLNodes();
            }
        }
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SOAPWrapper#sendAndWait(int, long)}.
     */
    public void testSendAndWaitIntLong() throws Exception
    {
        VariableContext vcContext;
        IConnectorStub connStub = stubFactory.convertConnector(cConnector);
        ISOAPWrapper sw = new SOAPWrapper(cConnector);
     
        /////////////////////////////////////////////////////
        // Test with request and response with a timeout.
        // We are only checking for the timeout value here.
        /////////////////////////////////////////////////////
        try {
            // Timeout value
            long lTimeout = 123;
            
            // Setup the request and response.
            int controlRequestEnvelope;
            int controlResponseEnvelope;
            
            vcContext = new VariableContext(dDoc);
            vcContext.setVariable("METHOD", 
                    "<MyMethod xmlns=\"http://mynamespace.org/1.0/test/case\">" +
                    "<data>here is the data</data>" +
                    "</MyMethod>" );
            controlRequestEnvelope = xtSoapMessageTemplate.renderTemplate(vcContext);
            addNomGarbage(controlRequestEnvelope);
            
            vcContext.setVariable("METHOD", 
                    "<MyMethodResponse xmlns=\"http://mynamespace.org/1.0/test/case\">" +
                    "<responsedata>here is the response data</responsedata>" +
                    "</MyMethodResponse>");
            controlResponseEnvelope = xtSoapMessageTemplate.renderTemplate(vcContext);

            MessageWaitData requestWait;
            
            connStub.insertResponse(controlResponseEnvelope, 1);
            requestWait = connStub.createConnectorRequestWaitObject(controlRequestEnvelope, 1);

            // Then send the request.
            sw.sendAndWait(controlRequestEnvelope, lTimeout);
            
            // Check the result (this is the request timeout value set to the connector).
            assertEquals(lTimeout, requestWait.getResponseTimeout());
        }
        finally {
            sw.freeXMLNodes();
        }
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SOAPWrapper#sendAndWait(int, boolean)}.
     */
    public void testSendAndWaitIntBoolean() throws Exception
    {
        ISOAPWrapper swNoFault = new SOAPWrapper(cConnector);
        VariableContext vcContext;
        IConnectorStub connStub = stubFactory.convertConnector(cConnector);
        
        ///////////////////////////////////////////////////
        // Test with request and SOAP:Fault with no checking.
        ///////////////////////////////////////////////////
        try {
            int controlRequestEnvelope;
            int controlFaultEnvelope  = loadXmlResourceNoGarbage(soapFaultFiles[0]);
            
            vcContext = new VariableContext(dDoc);
            vcContext.setVariable("METHOD", 
                    "<MyMethod xmlns=\"http://mynamespace.org/1.0/test/case\">" +
                    "<data>here is the data</data>" +
                    "</MyMethod>" );
            controlRequestEnvelope = xtSoapMessageTemplate.renderTemplate(vcContext);
            addNomGarbage(controlRequestEnvelope);
            
            // First put the response into the connector.
            connStub.insertResponse("MyMethodResponse", "http://mynamespace.org/1.0/test/case", controlFaultEnvelope, 1);

            // Mark that we are waiting for the request.
            MessageWaitData requestWait = connStub.createConnectorRequestWaitObject(controlRequestEnvelope, 1);

            // Then send the request.
            int actualResponseEnvelope = swNoFault.sendAndWait(controlRequestEnvelope, false);
            int actualRequestEnvelope = requestWait.getResponseNode();
            
            // Check the results. The connector request should be the same as the control request
            // and the connector response should be the same as the control response.
            assertTrue(actualRequestEnvelope != 0);
            assertNodesEqual(controlRequestEnvelope, actualRequestEnvelope);
            assertNodesEqual(controlFaultEnvelope, actualResponseEnvelope);
        }
        finally {
            swNoFault.freeXMLNodes();
        }        
     
        ///////////////////////////////////////////////////
        // Test with request and SOAP:Fault with checking.
        ///////////////////////////////////////////////////
        for (String faultFile : soapFaultFiles)
        {
            ISOAPWrapper sw = new SOAPWrapper(cConnector);
            
            try {
                int controlRequestEnvelope;
                int controlFaultEnvelope = loadXmlResourceNoGarbage(faultFile);
                
                vcContext = new VariableContext(dDoc);
                vcContext.setVariable("METHOD", 
                        "<MyMethod xmlns=\"http://mynamespace.org/1.0/test/case\">" +
                        "<data>here is the data</data>" +
                        "</MyMethod>" );
                controlRequestEnvelope = xtSoapMessageTemplate.renderTemplate(vcContext);
                addNomGarbage(controlRequestEnvelope);
                
                // First put the response into the connector.
                connStub.insertResponse("MyMethodResponse", "http://mynamespace.org/1.0/test/case", controlFaultEnvelope, 1);
    
                // Mark that we are waiting for the request.
                connStub.createConnectorRequestWaitObject(controlRequestEnvelope, 1);
    
                // Then send the request. This should throw a SOAPException.
                try {
                    sw.sendAndWait(controlRequestEnvelope, true);
                    fail("sendAndWait didn't throw an exception on SOAP:Fault. Fault file: " + faultFile);
                } 
                catch (SOAPException expected) {
                    
                }
            }
            finally {
                sw.freeXMLNodes();
            }
        }        
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SOAPWrapper#setCollecting(boolean)}.
     */
    public void testSetCollecting() throws Exception
    {
        //TODO: fail("Not yet implemented");
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SOAPWrapper#setConnector(com.eibus.connector.nom.Connector)}.
     */
    public void testSetConnector() throws Exception
    {
        SOAPWrapper sw = new SOAPWrapper();
        Connector dummy = Connector.getInstance("DUMMY");
        
        sw.setConnector(dummy);
        assertEquals(dummy, sw.getConnector());
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SOAPWrapper#setOrganization(java.lang.String)}.
     */
    public void testSetOrganization() throws Exception
    {
        //TODO: fail("Not yet implemented");
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SOAPWrapper#setTimeOut(long)}.
     */
    public void testSetTimeOut() throws Exception
    {
        //TODO: fail("Not yet implemented");
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SOAPWrapper#setUser(java.lang.String)}.
     */
    public void testSetUser() throws Exception
    {
        //TODO: fail("Not yet implemented");
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SOAPWrapper#createSoapFault(java.lang.Throwable)}.
     */
    public void testCreateSoapFaultThrowable() throws Exception
    {
        //TODO: fail("Not yet implemented");
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SOAPWrapper#createSoapFault(java.lang.Throwable, java.lang.String, java.lang.String, java.lang.String)}.
     */
    public void testCreateSoapFaultThrowableStringStringString() throws Exception
    {
        //TODO: fail("Not yet implemented");
    }
}
