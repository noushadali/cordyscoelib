package com.cordys.coe.test;

import com.cordys.coe.util.cgc.CGCFactory;
import com.cordys.coe.util.cgc.ICordysGatewayClient;
import com.cordys.coe.util.xml.dom.NiceDOMWriter;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * DOCUMENTME.
 *
 * @author  $author$
 */
public class DemoCordysGatewayClient
{
    /**
     * Main method.
     *
     * @param  saArguments  The commandline arguments.
     */
    public static void main(String[] saArguments)
    {
        try
        {
            // Configure Log4J for stdout.
            BasicConfigurator.configure();
            Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.WARN);

            String sServer = "srv-nl-ces70";
            String sUsername = "pgussow";
            String sPassword = "unknown";
            String sOrganization = "o=system,cn=cordys,o=vanenburg.com";

            // Create the session
            ICordysGatewayClient cgc = null;
            cgc = CGCFactory.createCGCForUsernamePasswordAuthentication("NTDOM\\" + sUsername,
                                                                        sPassword, sServer, 80,
                                                                        false);

            cgc.setOrganization(sOrganization);

            cgc.connect();

            // Create the base method XML
            Element eMethod = cgc.createMessage("GetXMLObject",
                                                "http://schemas.cordys.com/1.0/xmlstore");
            Document dDoc = eMethod.getOwnerDocument();

            Element eKey = dDoc.createElement("key");
            Node nData = dDoc.createTextNode("/Cordys/WCP/Application Connector/com.cordys.cpc.bsf.connector.BsfConnector");
            eKey.appendChild(nData);
            eMethod.appendChild(eKey);

            // Send the request
            Element eResponse = cgc.requestFromCordys(dDoc.getDocumentElement());

            System.out.println(NiceDOMWriter.write(eResponse));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
