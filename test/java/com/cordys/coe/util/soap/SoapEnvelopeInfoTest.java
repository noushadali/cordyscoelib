/**
 * (c) 2007 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util.soap;

import com.cordys.coe.util.test.junit.NomTestCase;
import com.eibus.xml.nom.Find;

/**
 * Test case for class SoapEnvelopeInfo
 *
 * @author mpoyhone
 */
public class SoapEnvelopeInfoTest extends NomTestCase
{
    /**
     * Array of files for different SOAP messages.
     */
    private static final String[] files = {
        "testdata/SoapMessage_NoPrefix.xml",
        "testdata/SoapMessage_Standard.xml",
        //"testdata/SoapMessage_UnqualifiedNS.xml", // NOM cannot handle this currently.
    };
    /**
     * Array of files for different SOAP:Faults.
     */
    private static String[] soapFaultFiles = {
            "testdata/SOAPFault_C2.xml",
            "testdata/SOAPFault_C3.xml",
            "testdata/SOAPFault_Standard.xml",
    };
    
    /**
     * Test method for {@link com.cordys.coe.util.soap.SoapEnvelopeInfo#parseSoapEnvelope(int, boolean)}.
     */
    public void testParseSoapEnvelope() throws Exception
    {
        for (String f : files)
        {
            int envelope = loadXmlResource(f);
            int controlHeader = Find.firstMatch(envelope, "<><>");
            int controlCordysHeader = Find.firstMatch(envelope, "<><><>");
            int controlBody = Find.firstMatch(envelope, "<><>right");
            int controlMethod = Find.firstMatch(envelope, "<><>right<><>");
            SoapEnvelopeInfo info = SoapEnvelopeInfo.parseSoapEnvelope(envelope, true);
            
            assertNotNull(info);
            assertNull(info.getSoapFault());
            assertEquals("SOAP header failed in file: " + f, controlHeader, info.getHeader());
            assertEquals("Cordys SOAP header failed in file: " + f, controlCordysHeader, info.getCordysHeader());
            assertEquals("SOAP body failed in file: " + f, controlBody, info.getBody());
            assertEquals("SOAP method failed in file: " + f, controlMethod, info.getFirstMethod());
        }
    }
    
    /**
     * Test method for {@link com.cordys.coe.util.soap.SoapEnvelopeInfo#parseSoapEnvelope(int, boolean)}.
     */
    public void testParseSoapEnvelope_SOAPFault() throws Exception
    {
        for (String f : soapFaultFiles)
        {
            int envelope = loadXmlResource(f);
            int controlBody = Find.firstMatch(envelope, "<><>right");
            int controlMethod = Find.firstMatch(envelope, "<><>right<><>");
            SoapEnvelopeInfo info = SoapEnvelopeInfo.parseSoapEnvelope(envelope, true);
            
            assertTrue(controlBody != 0);
            assertNotNull(info);
            assertNotNull(info.getSoapFault());
            assertEquals("SOAP body failed in file: " + f, controlBody, info.getBody());
            assertEquals("SOAP method failed in file: " + f, controlMethod, info.getFirstMethod());
        }
    }
    
    /**
     * Test method for {@link com.cordys.coe.util.soap.SoapEnvelopeInfo#parseSoapEnvelope(int, boolean)}.
     */
    public void testParseSoapEnvelope_SOAPFault_NoParse() throws Exception
    {
        for (String f : soapFaultFiles)
        {
            int envelope = loadXmlResource(f);
            SoapEnvelopeInfo info = SoapEnvelopeInfo.parseSoapEnvelope(envelope, false);
            
            assertNotNull(info);
            assertNull(info.getSoapFault());
        }
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SoapEnvelopeInfo#dispose()}.
     */
    public void testDispose() throws Exception
    {
        int envelope = loadXmlResource(files[0]);
        SoapEnvelopeInfo info = SoapEnvelopeInfo.parseSoapEnvelope(envelope, false);
        
        assertNotNull(info);
        
        info.dispose();
        assertTrue(info.getEnvelope() == 0);
        assertTrue(info.getHeader() == 0);
        assertTrue(info.getCordysHeader() == 0);
        assertTrue(info.getBody() == 0);
        assertTrue(info.getFirstMethod() == 0);
        assertNull(info.getSoapFault());
    }
    

    /**
     * Test method for {@link com.cordys.coe.util.soap.SoapEnvelopeInfo#dispose()}.
     */
    public void testDispose_SOAPFault() throws Exception
    {
        int envelope = loadXmlResource(soapFaultFiles[0]);
        SoapEnvelopeInfo info = SoapEnvelopeInfo.parseSoapEnvelope(envelope, true);
        
        assertNotNull(info);
        
        info.dispose();
        assertTrue(info.getEnvelope() == 0);
        assertTrue(info.getHeader() == 0);
        assertTrue(info.getCordysHeader() == 0);
        assertTrue(info.getBody() == 0);
        assertTrue(info.getFirstMethod() == 0);
        assertNull(info.getSoapFault());
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SoapEnvelopeInfo#findHeaderFromEnvelope(int)}.
     */
    public void testFindHeaderFromEnvelope() throws Exception
    {
        for (String f : files)
        {
            int envelope = loadXmlResource(f);
            int controlHeader = Find.firstMatch(envelope, "<><>");
            
            assertTrue(controlHeader != 0);
            
            assertEquals("SOAP file failed: " + f, controlHeader, SoapEnvelopeInfo.findHeaderFromEnvelope(envelope));
        }
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SoapEnvelopeInfo#findBodyFromEnvelope(int)}.
     */
    public void testFindBodyFromEnvelope() throws Exception
    {
        for (String f : files)
        {
            int envelope = loadXmlResource(f);
            int controlBody = Find.firstMatch(envelope, "<><>right");
            
            assertTrue(controlBody != 0);
            
            assertEquals("SOAP file failed: " + f, controlBody, SoapEnvelopeInfo.findBodyFromEnvelope(envelope));
        }
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SoapEnvelopeInfo#findFirstMethodFromEnvelope(int)}.
     */
    public void testFindFirstMethodFromEnvelope() throws Exception
    {
        for (String f : files)
        {
            int envelope = loadXmlResource(f);
            int controlMethod = Find.firstMatch(envelope, "<><>right<><>");
            
            assertTrue(controlMethod != 0);
            
            assertEquals("SOAP file failed: " + f, controlMethod, SoapEnvelopeInfo.findFirstMethodFromEnvelope(envelope));
        }
    }
}
