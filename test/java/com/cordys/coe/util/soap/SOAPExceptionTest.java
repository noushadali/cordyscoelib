package com.cordys.coe.util.soap;

import com.cordys.coe.util.test.junit.NomTestCase;

/**
 * Test case for the SOAP Exception.
 *
 * @author  pgussow
 */
public class SOAPExceptionTest extends NomTestCase
{
    /**
     * Array of files for different SOAP messages.
     */
    private static final String[] files = { "testdata/SoapMessage_BOP4.xml" };
    /**
     * Array of files for different SOAP:Faults.
     */
    private static String[] soapFaultFiles = { "testdata/SOAPFault_BOP4.xml" };

    /**
     * Test method for {@link com.cordys.coe.util.soap.SOAPException#SOAPException(Throwable, String, int, int)}.
     *
     * @throws  Exception  In case of any exceptions
     */
    public void testCreationSOAPException()
                                   throws Exception
    {
        int request = loadXmlResource(files[0]);
        int response = loadXmlResource(soapFaultFiles[0]);

        SOAPException e = new SOAPException(null, "Error received", request, response);

        assertEquals("Message not correct.", "Error received", e.getMessage());
        
        assertEquals("Faultcode not correct.", "ns0:Client", e.getFaultCode());
        assertEquals("Faultstring not correct.", "SearchLDAP method does not have a proper search root.", e.getFaultString());
        assertEquals("Faultactor not correct.", "http://schemas.cordys.com/1.0/ldap", e.getFaultActor());
        
    }
}
