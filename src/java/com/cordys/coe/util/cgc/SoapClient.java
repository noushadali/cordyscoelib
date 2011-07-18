/**
 * (c) 2007 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util.cgc;

import com.cordys.coe.util.cgc.nom.ICordysNomGatewayClient;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Node;
import com.eibus.xml.nom.XMLException;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * A simple command line SOAP client for the <code>CordysGatewayClient</code> class.
 *
 * @author  mpoyhone
 */
public class SoapClient
{
    /**
     * DOCUMENTME.
     */
    static Document dDoc = new Document();

    /**
     * DOCUMENTME.
     *
     * @param  args  DOCUMENTME
     */
    public static void main(String[] args)
    {
        if ((args.length < 4) || (args.length > 5))
        {
            usage();
            System.exit(1);
        }

        String sUrlStr = args[0];
        String sRequestFile = args[1];
        String sUserName = args[2];
        String sPassword = args[3];
        String sSoapAction = (args.length > 4) ? args[4] : null;
        URL uUrl = null;

        try
        {
            uUrl = new URL(sUrlStr);
        }
        catch (MalformedURLException e)
        {
            System.err.println("Invalid URL: " + sUrlStr);
            e.printStackTrace();
            System.exit(1);
        }

        int xRequest = 0;
        int xResponse = 0;

        try
        {
            xRequest = dDoc.load(sRequestFile);
        }
        catch (XMLException e)
        {
            System.err.println("Unable to load request XML file: " + sRequestFile);
            e.printStackTrace();
        }

        ICordysNomGatewayClient client = null;

        try
        {
            client = CGCFactory.createNOMBasedCGC(sUserName, sPassword, uUrl);
            client.setLoginToCordysOnConnect(false);

            try
            {
                client.connect();
            }
            catch (Exception e)
            {
                System.err.println("Unable to connect to URL: " + uUrl);
                e.printStackTrace();
                System.exit(1);
            }

            try
            {
                xResponse = client.requestFromCordys(xRequest, 30000L, sSoapAction);
            }
            catch (Exception e)
            {
                System.err.println("SOAP request failed.");
                e.printStackTrace();
                System.exit(1);
            }

            System.out.println(Node.writeToString(xResponse, true));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (xRequest != 0)
            {
                Node.delete(xRequest);
                xRequest = 0;
            }

            if (xResponse != 0)
            {
                Node.delete(xResponse);
                xResponse = 0;
            }

            if (client != null)
            {
                client.disconnect();
            }
        }
    }

    /**
     * DOCUMENTME.
     */
    private static void usage()
    {
        System.out.println(String.format("Usage: java %s <URL> <SOAP Request File> <user> <password> [SOAP action]",
                                         SoapClient.class.getName()));
        System.out.println("   Currently only Basic authentication is supported.");
    }
}
