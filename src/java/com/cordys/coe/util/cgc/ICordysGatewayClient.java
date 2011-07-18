package com.cordys.coe.util.cgc;

import com.cordys.coe.util.cgc.config.ICGCConfiguration;

import com.novell.ldap.LDAPEntry;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This interface describes the methods that the Cordys web gateway supports. This is the DOM version of the client. All
 * commong methods are defined in class <code>ICordysGatewayClient</code>.
 */
public interface ICordysGatewayClient extends ICordysGatewayClientBase
{
    /**
     * This method creates a SOAP message with the given name and namespace.
     *
     * @param   nRequest    The SOAP:Envelope to add it to.
     * @param   sMethod     The name of the method.
     * @param   sNamespace  The namespace of the method.
     *
     * @return  The Element of the method. To get the root element of the message call
     *          eReturn.getOwnerDocument().getDocumentElement()
     *
     * @throws  CordysGatewayClientException  In case of any exceptions.
     */
    Node addMethod(Node nRequest, String sMethod, String sNamespace)
            throws CordysGatewayClientException;

    /**
     * This method creates a SOAP message with the given name and namespace.
     *
     * @param   sMethodName  The name of the method.
     * @param   sNamespace   The namespace of the method.
     *
     * @return  The Element of the method. To get the root element of the message call
     *          eReturn.getOwnerDocument().getDocumentElement()
     *
     * @throws  CordysGatewayClientException  In case of any exceptions.
     */
    Element createMessage(String sMethodName, String sNamespace)
                   throws CordysGatewayClientException;

    /**
     * This method gets the configuration details.
     *
     * @return  The configuration details.
     */
    ICGCConfiguration getConfiguration();

    /**
     * Returns the namespace-aware response flag value.
     *
     * @return  If <code>true</code> response documents are namespace-aware.
     */
    boolean getNamespaceAwareResponses();

    /**
     * This method returns the SAML token for the current user.
     *
     * @return  The SAML token for the current user.
     */
    Node getSAMLToken();

    /**
     * This method returns the search root of the current LDAP.
     *
     * @return  The search root.
     */
    String getSearchRoot();

    /**
     * This method inserts the specified entry in LDAP.
     *
     * @param   leNew  The new entry to insert.
     *
     * @return  The new entry.
     *
     * @throws  CordysGatewayClientException  In case of any exceptions.
     */
    LDAPEntry insertLDAPEntry(LDAPEntry leNew)
                       throws CordysGatewayClientException;

    /**
     * This method reads the specified entry from LDAP.
     *
     * @param   sDN  The DN to read.
     *
     * @return  The read entry.
     *
     * @throws  CordysGatewayClientException  In case of any exceptions.
     */
    LDAPEntry readLDAPEntry(String sDN)
                     throws CordysGatewayClientException;

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The tag returned is the
     * pointer to the SOAP:Envelope.
     *
     * @param   eRequest  The request envelope.
     *
     * @return  The response.
     *
     * @throws  CordysGatewayClientException  In case of any exception other then a SOAP exception.
     * @throws  CordysSOAPException           For SOAP related exceptions.
     */
    Element requestFromCordys(Element eRequest)
                       throws CordysGatewayClientException, CordysSOAPException;

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The tag returned is the
     * pointer to the SOAP:Envelope.
     *
     * @param   eRequest   The request envelope.
     * @param   sReceiver  The DN of the receiving SOAP processor
     *
     * @return  The response.
     *
     * @throws  CordysGatewayClientException  In case of any exception other then a SOAP exception.
     * @throws  CordysSOAPException           For SOAP related exceptions.
     */
    Element requestFromCordys(Element eRequest, String sReceiver)
                       throws CordysGatewayClientException, CordysSOAPException;

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The tag returned is the
     * pointer to the SOAP:Envelope.
     *
     * @param   eRequest  The request envelope.
     * @param   lTimeout  The timeout to use.
     *
     * @return  The response.
     *
     * @throws  CordysGatewayClientException  In case of any exception other then a SOAP exception.
     * @throws  CordysSOAPException           For SOAP related exceptions.
     */
    Element requestFromCordys(Element eRequest, long lTimeout)
                       throws CordysGatewayClientException, CordysSOAPException;

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The tag returned is the
     * pointer to the SOAP:Envelope.
     *
     * @param   eRequest     The request envelope.
     * @param   lTimeout     The timeout to use.
     * @param   sSoapAction  SOAP action to be set in the request.
     *
     * @return  The response.
     *
     * @throws  CordysGatewayClientException  In case of any exception other then a SOAP exception.
     * @throws  CordysSOAPException           For SOAP related exceptions.
     */
    Element requestFromCordys(Element eRequest, long lTimeout, String sSoapAction)
                       throws CordysGatewayClientException, CordysSOAPException;

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The tag returned is the
     * pointer to the SOAP:Envelope.
     *
     * @param   eRequest       The request envelope.
     * @param   lTimeout       The timeout to use.
     * @param   sOrganization  The organization to send it to.
     * @param   sSoapAction    SOAP action to be set in the request.
     *
     * @return  The response.
     *
     * @throws  CordysGatewayClientException  In case of any exception other then a SOAP exception.
     * @throws  CordysSOAPException           For SOAP related exceptions.
     */
    Element requestFromCordys(Element eRequest, long lTimeout, String sOrganization, String sSoapAction)
                       throws CordysGatewayClientException, CordysSOAPException;

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The tag returned is the
     * pointer to the SOAP:Envelope.
     *
     * @param   eRequest       The request envelope.
     * @param   lTimeout       The timeout to use.
     * @param   sOrganization  The organization to send it to.
     * @param   sSoapAction    SOAP action to be set in the request.
     * @param   sReceiver      The DN of the receiving SOAP processor
     *
     * @return  The response.
     *
     * @throws  CordysGatewayClientException  In case of any exception other then a SOAP exception.
     * @throws  CordysSOAPException           For SOAP related exceptions.
     */
    Element requestFromCordys(Element eRequest, long lTimeout, String sOrganization, String sSoapAction,
                              String sReceiver)
                       throws CordysGatewayClientException, CordysSOAPException;

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The tag returned is the
     * pointer to the SOAP:Envelope.
     *
     * @param   eRequest  The request envelope.
     *
     * @return  The response.
     *
     * @throws  CordysGatewayClientException  In case of any exception other then a SOAP exception.
     * @throws  CordysSOAPException           For SOAP related exceptions.
     */
    Element requestFromCordysNoBlocking(Element eRequest)
                                 throws CordysGatewayClientException, CordysSOAPException;

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The tag returned is the
     * pointer to the SOAP:Envelope.
     *
     * @param   eRequest   The request envelope.
     * @param   sReceiver  The DN of the receiving SOAP processor
     *
     * @return  The response.
     *
     * @throws  CordysGatewayClientException  In case of any exception other then a SOAP exception.
     * @throws  CordysSOAPException           For SOAP related exceptions.
     */
    Element requestFromCordysNoBlocking(Element eRequest, String sReceiver)
                                 throws CordysGatewayClientException, CordysSOAPException;

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The tag returned is the
     * pointer to the SOAP:Envelope.
     *
     * @param   eRequest  The request envelope.
     * @param   lTimeout  The timeout to use.
     *
     * @return  The response.
     *
     * @throws  CordysGatewayClientException  In case of any exception other then a SOAP exception.
     * @throws  CordysSOAPException           For SOAP related exceptions.
     */
    Element requestFromCordysNoBlocking(Element eRequest, long lTimeout)
                                 throws CordysGatewayClientException, CordysSOAPException;

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The tag returned is the
     * pointer to the SOAP:Envelope.
     *
     * @param   eRequest     The request envelope.
     * @param   lTimeout     The timeout to use.
     * @param   sSoapAction  SOAP action to be set in the request.
     *
     * @return  The response.
     *
     * @throws  CordysGatewayClientException  In case of any exception other then a SOAP exception.
     * @throws  CordysSOAPException           For SOAP related exceptions.
     */
    Element requestFromCordysNoBlocking(Element eRequest, long lTimeout, String sSoapAction)
                                 throws CordysGatewayClientException, CordysSOAPException;

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The tag returned is the
     * pointer to the SOAP:Envelope.
     *
     * @param   eRequest       The request envelope.
     * @param   lTimeout       The timeout to use.
     * @param   sOrganization  The organization to send it to.
     * @param   sSoapAction    SOAP action to be set in the request.
     *
     * @return  The response.
     *
     * @throws  CordysGatewayClientException  In case of any exception other then a SOAP exception.
     * @throws  CordysSOAPException           For SOAP related exceptions.
     */
    Element requestFromCordysNoBlocking(Element eRequest, long lTimeout, String sOrganization,
                                        String sSoapAction)
                                 throws CordysGatewayClientException, CordysSOAPException;

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The tag returned is the
     * pointer to the SOAP:Envelope.
     *
     * @param   eRequest       The request envelope.
     * @param   lTimeout       The timeout to use.
     * @param   sOrganization  The organization to send it to.
     * @param   sSoapAction    SOAP action to be set in the request.
     * @param   sReceiver      The DN of the receiving SOAP processor
     *
     * @return  The response.
     *
     * @throws  CordysGatewayClientException  In case of any exception other then a SOAP exception.
     * @throws  CordysSOAPException           For SOAP related exceptions.
     */
    Element requestFromCordysNoBlocking(Element eRequest, long lTimeout, String sOrganization, String sSoapAction,
                                        String sReceiver)
                                 throws CordysGatewayClientException, CordysSOAPException;

    /**
     * This method searches LDAP for certain entries.
     *
     * @param   sSearchRoot  The search root.
     * @param   iLDAPScope   The scope.
     * @param   sFilter      The filter.
     *
     * @return  The list of LDAP entries.
     *
     * @throws  CordysGatewayClientException  In case of any exceptions.
     */
    LDAPEntry[] searchLDAP(String sSearchRoot, int iLDAPScope, String sFilter)
                    throws CordysGatewayClientException;

    /**
     * Sets whether the response documents should be namespace-aware.
     *
     * @param  value  If <code>true</code> response documents are namespace-aware.
     */
    void setNamespaceAwareResponses(boolean value);

    /**
     * This method updates the specified entry in LDAP. If the leOld is null it will be considered an insert.
     *
     * @param   leOld  The old version of the entry.
     * @param   leNew  The new version of the entry.
     *
     * @return  The updated/new entry.
     *
     * @throws  CordysGatewayClientException  In case of any exceptions.
     */
    LDAPEntry updateLDAPEntry(LDAPEntry leOld, LDAPEntry leNew)
                       throws CordysGatewayClientException;
}
