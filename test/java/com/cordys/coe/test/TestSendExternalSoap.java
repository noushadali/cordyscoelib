package com.cordys.coe.test;

import com.cordys.coe.util.soap.SoapHelpers;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Node;

import java.net.URL;
import java.util.Properties;

/**
 * DOCUMENTME
 * .
 *
 * @author  pgussow
 */
public class TestSendExternalSoap
{
    /**
     * Holds the document to use.
     */
    private static Document s_dDoc = new Document();

    /**
     * Main method.
     *
     * @param  saArguments  The commandline arguments.
     */
    public static void main(String[] saArguments)
    {
        try
        {
        	Properties systemSettings = System.getProperties();
        	systemSettings.put("http.proxySet","true") ;
            systemSettings.put("http.proxyHost","srv-nl-ffi01.vanenburg.com") ;
            systemSettings.put("http.proxyPort", "80") ;
            systemSettings.put("http.keepAlive", "true") ;

        	
            URL uURL = new URL("http", "srv-nl-ffi01.vanenburg.com", 80, "http://mucdeves01.skytec-ag.net/cordys/com.eibus.web.soap.Gateway.wcp?organization=o%3DSiemensHC%2Ccn%3Dcordys%2Co%3Dww001.siemens.net&timeout=86400000");
            int iRequest = s_dDoc.parseString("<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP:Body><GetInstallationInfo xmlns=\"http://schemas.cordys.com/1.0/monitor\" /></SOAP:Body></SOAP:Envelope>");
            String sUserName = "tdouden";
            String sUserPassword = "tdOuden123";

            int iResponse = SoapHelpers.sendExternalSOAPRequest(uURL, iRequest, sUserName,
                                                                sUserPassword);

            System.out.println(Node.writeToString(iResponse, true));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
