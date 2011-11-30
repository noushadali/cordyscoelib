package com.cordys.coe.util.soap;

import com.cordys.coe.util.FileUtils;
import com.cordys.coe.util.xml.XMLHelpers;

import com.eibus.directory.soap.DirectoryException;
import com.eibus.directory.soap.LDAPDirectory;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Node;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A collection of SOAP messagin related utility methods.
 *
 * @author  mpoyhone
 */
public class SoapHelpers
{
    /**
     * Adds the needed SOAP HTTP headers to the map.
     *
     * @param  mHeaderMap   Header map.
     * @param  sSoapAction  Optional SOAP action. If not <code>null</code>, SOAPAction header is
     *                      added.
     */
    public static void addSoapHeaders(Map<String, String> mHeaderMap, String sSoapAction)
    {
        mHeaderMap.put("Content-Type", "text/xml; charset=utf-8");

        if (sSoapAction != null)
        {
            mHeaderMap.put("SOAPAction", sSoapAction);
        }
    }

    /**
     * Returns the organization DN from user DN string.
     *
     * @param   sUserDN  The user DN.
     *
     * @return  The corresponding organization DN.
     */
    public static String getOrganizationFromUser(String sUserDN)
    {
        int iPos;
        final String sMatchStr = ",cn=organizational users,";

        iPos = sUserDN.indexOf(sMatchStr);

        if (iPos == -1)
        {
            return null;
        }

        iPos += sMatchStr.length();

        if (iPos >= sUserDN.length())
        {
            return null;
        }

        return sUserDN.substring(iPos);
    }

    /**
     * Resolves the receiver when the method parameters are specified.
     *
     * @param   sOrganization  The organization in which the soap node has to be resolved
     * @param   sNamespace     The namespace of the method.
     * @param   sMethod        The name of the method.
     *
     * @return  The dn of the receiver soap node to which the request has to be sent.
     *
     * @throws  SOAPException
     */
    public static String getReceiver(String sOrganization, String sNamespace, String sMethod)
                              throws SOAPException
    {
        String sReceiver;
        LDAPDirectory ldLdapDirectory;

        try
        {
            ldLdapDirectory = LDAPDirectory.getDefaultInstance();

            if (sOrganization != null)
            {
                ldLdapDirectory.setOrganization(sOrganization);
            }

            if ((sOrganization == null) || (sOrganization.length() == 0))
            {
                // Resolve the Receiver to whom to send.
                sReceiver = ldLdapDirectory.findSOAPNode(sNamespace, sMethod);
            }
            else
            {
                // Resolve the Receiver to whom to send.
                sReceiver = ldLdapDirectory.findSOAPNode(sOrganization, sNamespace, sMethod);
            }
        }
        catch (DirectoryException de)
        {
            throw new SOAPException(de.getMessage() + " : " + de);
        }

        return sReceiver;
    }

    /**
     * Resolves the receiver when the method parameters are specified.
     *
     * @param   sUserDN     The user DN that will be used to get the target organization in which
     *                      the soap node has to be resolved.
     * @param   sNamespace  The namespace of the method.
     * @param   sMethod     The name of the method.
     *
     * @return  The dn of the receiver soap node to which the request has to be sent.
     *
     * @throws  SOAPException
     */
    public static String getReceiverByUser(String sUserDN, String sNamespace, String sMethod)
                                    throws SOAPException
    {
        String sOrgDN = getOrganizationFromUser(sUserDN);

        if (sOrgDN == null)
        {
            throw new SOAPException("Unable to get the organization DN from user DN.");
        }

        return getReceiver(sOrgDN, sNamespace, sMethod);
    }

    /**
     * Returns the SOAP namespace prefix from the SOAP:Envelope. If the prefix could not be found,
     * value 'SOAP' is returned.
     *
     * @param       iSoapEnvelopeNode  SOAP envelope XML node.
     *
     * @return      The namespace prefix.
     *
     * @see         getSoapNamespacePrefix instead.
     * @deprecated  The name is misleading. Use
     */
    public static String getSoapNamespace(int iSoapEnvelopeNode)
    {
        return getSoapNamespace(iSoapEnvelopeNode, "SOAP");
    }

    /**
     * Returns the SOAP namespace prefix from the SOAP:Envelope.
     *
     * @param       iSoapEnvelopeNode  SOAP envelope XML node.
     * @param       sDefaultPrefix     The value to be returned if no SOAP namepsace prefix was
     *                                 found.
     *
     * @return      The namespace prefix.
     *
     * @see         getSoapNamespacePrefix instead.
     * @deprecated  The name is misleading. Use
     */
    public static String getSoapNamespace(int iSoapEnvelopeNode, String sDefaultPrefix)
    {
        int iCount = Node.getNumAttributes(iSoapEnvelopeNode);

        for (int i = 0; i <= iCount; i++)
        {
            String sPrefix = Node.getAttributePrefix(iSoapEnvelopeNode, i);

            if ((sPrefix == null) || !sPrefix.equals("xmlns"))
            {
                continue;
            }

            String sName = Node.getAttributeName(iSoapEnvelopeNode, i);

            if (sName == null)
            {
                continue;
            }

            String sValue = Node.getAttribute(iSoapEnvelopeNode, sName);

            if (sValue.toLowerCase().startsWith("http://schemas.xmlsoap.org/soap/envelope/"))
            {
                return Node.getAttributeLocalName(iSoapEnvelopeNode, i);
            }
        }

        return sDefaultPrefix;
    }

    /**
     * Returns the SOAP namespace prefix from the SOAP:Envelope. The returned value will contain a
     * colon at the end (e.g. SOAP:) if the prefix was found or an it will be empty string if the no
     * prefix is not set or <code>null</code> if the SOAP namespace URI was not found (the passed
     * node is not a valid SOAP envelope). This way the returned value can be used in a Find.match()
     * expression like:
     *
     * <pre>
        String sSoapPrefix = SoapHelpers.getSoapNamespacePrefix(iEnvelopeNode);
        int iSoapFaultNode = Find.firstMatch(iResultNode,
                                             String.format("&lt;&gt;&lt;%sBody&gt;&lt;%sFault&gt;",
                                                           sSoapPrefix, sSoapPrefix));
     * </pre>
     *
     * @param   iSoapEnvelopeNode  SOAP envelope XML node.
     *
     * @return  The namespace prefix.
     */
    public static String getSoapNamespacePrefix(int iSoapEnvelopeNode)
    {
        return XMLHelpers.getNamespacePrefix(iSoapEnvelopeNode,
                                             "http://schemas.xmlsoap.org/soap/envelope/");
    }

    /**
     * This method send a SOAP request to an external system over an HTTP connection. It sends the
     * HTTP request and it will return the response. If a technical error occurs, an exception is
     * thrown.
     *
     * @param   uURL           The URL holding the SOAP request receiver address.
     * @param   iRequest       The string holding the SOAP request XML (must also have the SOAP
     *                         envlope).
     * @param   sUserName      User name for basic HTTP authentication or, null if no authentication
     *                         is needed.
     * @param   sUserPassword  User password for basic HTTP authentication or, null if no
     *                         authentication is needed.
     *
     * @return  The SOAP response XML from the HTTP server.
     *
     * @throws  SOAPException  DOCUMENTME
     */
    public static int sendExternalSOAPRequest(URL uURL, int iRequest, String sUserName,
                                              String sUserPassword)
                                       throws SOAPException
    {
        return sendExternalSOAPRequest(uURL, iRequest, sUserName, sUserPassword, null);
    }

    /**
     * This method send a SOAP request to an external system over an HTTP connection. It sends the
     * HTTP request and it will return the response. If a technical error occurs, an exception is
     * thrown.
     *
     * @param   uURL           The URL holding the SOAP request receiver address.
     * @param   sRequest       The string holding the SOAP request XML (must also have the SOAP
     *                         envlope).
     * @param   dDoc           XML document used for parsing the response, is null no response is
     *                         returned.
     * @param   sUserName      User name for basic HTTP authentication or, null if no authentication
     *                         is needed.
     * @param   sUserPassword  User password for basic HTTP authentication or, null if no
     *                         authentication is needed.
     *
     * @return  The SOAP response XML from the HTTP server.
     *
     * @throws  SOAPException  DOCUMENTME
     */
    public static int sendExternalSOAPRequest(URL uURL, String sRequest, Document dDoc,
                                              String sUserName, String sUserPassword)
                                       throws SOAPException
    {
        return sendExternalSOAPRequest(uURL, sRequest, dDoc, sUserName, sUserPassword, null);
    }

    /**
     * This method send a SOAP request to an external system over an HTTP connection. It sends the
     * HTTP request and it will return the response. If a technical error occurs, an exception is
     * thrown.
     *
     * @param   uURL           The URL holding the SOAP request receiver address.
     * @param   iRequest       The string holding the SOAP request XML (must also have the SOAP
     *                         envlope).
     * @param   sUserName      User name for basic HTTP authentication or, null if no authentication
     *                         is needed.
     * @param   sUserPassword  User password for basic HTTP authentication or, null if no
     *                         authentication is needed.
     * @param   sSoapAction    The SOAPAction string to be used.
     *
     * @return  The SOAP response XML from the HTTP server.
     *
     * @throws  SOAPException  DOCUMENTME
     */
    public static int sendExternalSOAPRequest(URL uURL, int iRequest, String sUserName,
                                              String sUserPassword, String sSoapAction)
                                       throws SOAPException
    {
        return sendExternalSOAPRequest(uURL, Node.writeToString(iRequest, false),
                                       Node.getDocument(iRequest), sUserName, sUserPassword,
                                       sSoapAction);
    }

    /**
     * This method send a SOAP request to an external system over an HTTP connection. It sends the
     * HTTP request and it will return the response. If a technical error occurs, an exception is
     * thrown.
     *
     * @param   uURL           The URL holding the SOAP request receiver address.
     * @param   sRequest       The string holding the SOAP request XML (must also have the SOAP
     *                         envlope).
     * @param   dDoc           XML document used for parsing the response, is null no response is
     *                         returned.
     * @param   sUserName      User name for basic HTTP authentication or, null if no authentication
     *                         is needed.
     * @param   sUserPassword  User password for basic HTTP authentication or, null if no
     *                         authentication is needed.
     * @param   sSoapAction    The SOAPAction string to be used.
     *
     * @return  The SOAP response XML from the HTTP server.
     *
     * @throws  SOAPException  DOCUMENTME
     */
    public static int sendExternalSOAPRequest(URL uURL, String sRequest, Document dDoc,
                                              String sUserName, String sUserPassword,
                                              String sSoapAction)
                                       throws SOAPException
    {
        byte[] baBytes;
        ByteArrayInputStream baisInput;

        try
        {
            baBytes = sRequest.getBytes("UTF-8");
            baisInput = new ByteArrayInputStream(baBytes);
        }
        catch (UnsupportedEncodingException e)
        {
            throw new SOAPException(e);
        }

        return sendExternalSOAPRequest(uURL, baisInput, baBytes.length, dDoc, sUserName,
                                       sUserPassword, sSoapAction);
    }

    /**
     * This method send a SOAP request to an external system over an HTTP connection. It sends the
     * HTTP request and it will return the response. If a technical error occurs, an exception is
     * thrown.
     *
     * @param   uURL            The URL holding the SOAP request receiver address.
     * @param   isInput         The input stream for the SOAP request contents.
     * @param   iContentLength  SOAP request size in bytes.
     * @param   dDoc            XML document used for parsing the response, is null no response is
     *                          returned.
     * @param   sUserName       User name for basic HTTP authentication or, null if no
     *                          authentication is needed.
     * @param   sUserPassword   User password for basic HTTP authentication or, null if no
     *                          authentication is needed.
     * @param   sSoapAction     The SOAPAction string to be used.
     *
     * @return  The SOAP response XML from the HTTP server.
     *
     * @throws  SOAPException  DOCUMENTME
     */
    public static int sendExternalSOAPRequest(URL uURL, InputStream isInput, int iContentLength,
                                              Document dDoc, String sUserName, String sUserPassword,
                                              String sSoapAction)
                                       throws SOAPException
    {
        String sResult;
        int iResultNode;
        Map<String, String> mHeaders = new HashMap<String, String>();

        addSoapHeaders(mHeaders, sSoapAction);

        // Send the request.
        try
        {
            sResult = sendHTTPRequest(uURL, isInput, iContentLength, sUserName, sUserPassword,
                                      mHeaders);
        }
        catch (IOException e)
        {
            throw new SOAPException(e, "SOAP request failed.");
        }

        if (dDoc == null)
        {
            return 0;
        }

        try
        {
            // Parse the response.
            iResultNode = dDoc.parseString(sResult);
        }
        catch (Exception e)
        {
            throw new SOAPException(e, "Invalid XML received\n" + sResult);
        }

        SoapFaultInfo faultInfo = SoapFaultInfo.findSoapFault(iResultNode);

        if (faultInfo != null)
        {
            String sFaultMessage = faultInfo.toString();

            // Delete the response.
            faultInfo.dispose();
            Node.delete(iResultNode);

            throw new SOAPException("SOAP fault received. " + sFaultMessage);
        }

        return iResultNode;
    }

    /**
     * This method send a HTTP request to an external system and returns the response data. If a
     * technical error occurs, an exception is thrown.
     *
     * @param   uURL           The URL holding the HTTP server address.
     * @param   sRequest       The string holding the HTTP request contents.
     * @param   sUserName      User name for basic HTTP authentication or, null if no authentication
     *                         is needed.
     * @param   sUserPassword  User password for basic HTTP authentication or, null if no
     *                         authentication is needed.
     * @param   mHeaders       A map listing extra HTTP headers to be added to the request, or null
     *                         if no extra headers are needed.
     *
     * @return  The HTTP response from the HTTP server.
     *
     * @throws  IOException  DOCUMENTME
     */
    public static String sendHTTPRequest(URL uURL, String sRequest, final String sUserName,
                                         final String sUserPassword, Map<String, String> mHeaders)
                                  throws IOException
    {
        byte[] baBytes;
        ByteArrayInputStream baisInput;

        try
        {
            baBytes = sRequest.getBytes("UTF-8");
            baisInput = new ByteArrayInputStream(baBytes);
        }
        catch (UnsupportedEncodingException e)
        {
            throw new IOException("Unable to convert input string to bytes : " + e);
        }

        return sendHTTPRequest(uURL, baisInput, baBytes.length, sUserName, sUserPassword, mHeaders);
    }

    /**
     * This method send a HTTP request to an external system and returns the response data. If a
     * technical error occurs, an exception is thrown.
     *
     * @param   uURL            The URL holding the HTTP server address.
     * @param   isInput         The input stream for the HTTP request contents.
     * @param   iContentLength  HTTP request size in bytes.
     * @param   sUserName       User name for basic HTTP authentication or, null if no
     *                          authentication is needed.
     * @param   sUserPassword   User password for basic HTTP authentication or, null if no
     *                          authentication is needed.
     * @param   mHeaders        A map listing extra HTTP headers to be added to the request, or null
     *                          if no extra headers are needed.
     *
     * @return  The HTTP response from the HTTP server.
     *
     * @throws  IOException  DOCUMENTME
     */
    public static String sendHTTPRequest(URL uURL, InputStream isInput, int iContentLength,
                                         final String sUserName, final String sUserPassword,
                                         Map<String, String> mHeaders)
                                  throws IOException
    {
    	//If a proxy server is set, add the keep-alive setting
    	if (System.getProperty("http.proxyHost") != null)
    	{
    		mHeaders.put("Proxy-Connection", "Keep-Alive");
    	}
    	
        // Check if we need authentication.
        if (sUserName != null)
        {
            Authenticator aAuth = new Authenticator()
            {
                PasswordAuthentication paAuth = new PasswordAuthentication(sUserName,
                                                                           sUserPassword
                                                                           .toCharArray());

                protected PasswordAuthentication getPasswordAuthentication()
                {
                    return paAuth;
                }
            };

            Authenticator.setDefault(aAuth);
        }

        HttpURLConnection urlConn = (HttpURLConnection) uURL.openConnection();

        // Set the HTTP request method.
        urlConn.setRequestMethod("POST");

        // Set the needed HTTP headers.
        if (mHeaders != null)
        {
            for (Iterator<String> iIter = mHeaders.keySet().iterator(); iIter.hasNext();)
            {
                String sName = iIter.next();
                String sValue = mHeaders.get(sName);

                urlConn.addRequestProperty(sName, sValue);
            }
        }

        return sendHTTPRequest(urlConn, isInput, iContentLength);
    }

    /**
     * This method send a HTTP request to an external system and returns the response data. If a
     * technical error occurs, an exception is thrown.
     *
     * @param   urlConn         HttpURLConnection to be used for the HTTP request. This needs to be
     *                          properly configured.
     * @param   isInput         The input stream for the HTTP request contents.
     * @param   iContentLength  HTTP request size in bytes.
     *
     * @return  The HTTP response from the HTTP server.
     *
     * @throws  IOException  DOCUMENTME
     */
    public static String sendHTTPRequest(HttpURLConnection urlConn, InputStream isInput,
                                         int iContentLength)
                                  throws IOException
    {
        OutputStream osPostData = null;
        InputStream isResponse = null;

        try
        {
            byte[] baBytes = new byte[32767];

            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);

            // Write the POST-data
            osPostData = urlConn.getOutputStream();

            // Write the HTTP request body.
            for (int i = 0; i < iContentLength; i++)
            {
                int iRead = isInput.read(baBytes);

                if (iRead <= 0)
                {
                    break;
                }

                osPostData.write(baBytes, 0, iRead);
            }

            // Send the request
            urlConn.connect();

            // Read the response code.
            int iStatusCode = urlConn.getResponseCode();

            // Read the HTTP response body.
            StringBuffer sbResult = new StringBuffer();

            try
            {
                if (iStatusCode == 200)
                {
                    isResponse = urlConn.getInputStream();
                }
                else
                {
                    isResponse = urlConn.getErrorStream();
                }
            }
            catch (Exception ignored)
            {
            }

            if (isResponse != null)
            {
                int iCount = 0;
                byte bByte = 0;

                while ((bByte = (byte) isResponse.read()) != -1)
                {
                    baBytes[iCount] = bByte;
                    iCount++;

                    if (iCount == 32766)
                    {
                        sbResult.append(new String(baBytes, 0, iCount, "UTF-8"));
                        iCount = 0;
                    }
                }

                if (iCount != 0)
                {
                    sbResult.append(new String(baBytes, 0, iCount, "UTF-8"));
                }
            }

            if (sbResult.length() == 0)
            {
                // No data returned, so check the status code. For SOAP:Fault we
                // receive status code 500 starting from C3.
                if (!((iStatusCode >= 200) && (iStatusCode < 300)))
                {
                    throw new IOException("The service returned: " + iStatusCode + " " +
                                          urlConn.getResponseMessage());
                }
            }

            // Fill the return-string with the response that was received.
            return sbResult.toString();
        }
        catch (Exception e)
        {
            throw new IOException("HTTP request to " + urlConn.getURL() + " failed : " + e);
        }
        finally
        {
            FileUtils.closeStream(isResponse);
            FileUtils.closeStream(osPostData);
        }
    }
}
