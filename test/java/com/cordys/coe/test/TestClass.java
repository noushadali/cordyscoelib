package com.cordys.coe.test;

import com.cordys.coe.util.soap.SOAPWrapper;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Node;

/**
 * DOCUMENTME.
 *
 * @author  $author$
 */
public class TestClass
{
    /**
     * DOCUMENTME.
     */
    private static Document s_dDoc = new Document();

    /**
     * DOCUMENTME.
     *
     * @param   lTime  DOCUMENTME
     *
     * @throws  InterruptedException  DOCUMENTME
     */
    public static void causeTimeout(long lTime)
                             throws InterruptedException
    {
        Thread.sleep(lTime);
        
    }

    /**
     * Main method.
     *
     * @param  saArguments  The commandline arguments.
     */
    public static void main(String[] saArguments)
    {
        try
        {
            testDifferentPrefixGetBody();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Test method for {@link com.cordys.coe.util.soap.SOAPWrapper#getBody(int)}.
     *
     * @throws  Exception  DOCUMENTME
     */
    public static void testDifferentPrefixGetBody()
                                           throws Exception
    {
        String xml;
        int envelope;

        ///////////////////////////////////////////////////
        // Test with a set user.
        ///////////////////////////////////////////////////
        xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
              "<soapenv:Header>" +
              "<header xmlns=\"http://schemas.cordys.com/General/1.0/\">" +
              "<sender><user>DUMMYUSER</user></sender>" +
              "</header>" +
              "</soapenv:Header>" +
              "<soapenv:Body></soapenv:Body>" +
              "</soapenv:Envelope>";
        envelope = s_dDoc.parseString(xml);

        int iNode = SOAPWrapper.getBody(envelope);

        System.out.println("Node: " + Node.writeToString(iNode, true));
    }
}
