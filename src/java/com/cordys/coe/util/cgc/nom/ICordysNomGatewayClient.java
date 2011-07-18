/**
 * (c) 2006 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util.cgc.nom;

import com.cordys.coe.util.cgc.CordysGatewayClientException;
import com.cordys.coe.util.cgc.CordysSOAPException;
import com.cordys.coe.util.cgc.ICordysGatewayClientBase;

/**
 * Interface for Cordys gateway client with NOM request methods.
 *
 * @author  mpoyhone
 */
public interface ICordysNomGatewayClient extends ICordysGatewayClientBase
{
    /**
     * This method creates a SOAP message with the given name and namespace.
     *
     * @param   xRequest    The SOAP:Envelope to add it to.
     * @param   sMethod     The name of the method.
     * @param   sNamespace  The namespace of the method.
     *
     * @return  The NOM node of the method. To get the root element of the message call
     *          Node.getRoot()
     *
     * @throws  CordysGatewayClientException  DOCUMENTME
     */
    int addMethod(int xRequest, String sMethod, String sNamespace)
           throws CordysGatewayClientException;

    /**
     * This method creates a SOAP message with the given name and namespace.
     *
     * @param   sMethodName  The name of the method.
     * @param   sNamespace   The namespace of the method.
     *
     * @return  The NOM node of the method. To get the root element of the message call
     *          Node.getRoot()
     *
     * @throws  CordysGatewayClientException  DOCUMENTME
     */
    int createMessage(String sMethodName, String sNamespace)
               throws CordysGatewayClientException;

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The
     * tag returned is the NOM node of the SOAP:Envelope.
     *
     * @param   xRequest  The request envelope NOM node.
     *
     * @return  The response NOM node.
     *
     * @throws  CordysGatewayClientException  DOCUMENTME
     * @throws  CordysSOAPException           DOCUMENTME
     */
    int requestFromCordys(int xRequest)
                   throws CordysGatewayClientException, CordysSOAPException;

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The
     * tag returned is the NOM node of the SOAP:Envelope.
     *
     * @param   xRequest  The request envelope NOM node.
     * @param   lTimeout  The timeout to use.
     *
     * @return  The response NOM node.
     *
     * @throws  CordysGatewayClientException  DOCUMENTME
     * @throws  CordysSOAPException           DOCUMENTME
     */
    int requestFromCordys(int xRequest, long lTimeout)
                   throws CordysGatewayClientException, CordysSOAPException;

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The
     * tag returned is the NOM node of the SOAP:Envelope.
     *
     * @param   xRequest     The request envelope NOM node.
     * @param   lTimeout     The timeout to use.
     * @param   sSoapAction  SOAP action to be set in the request.
     *
     * @return  The response NOM node.
     *
     * @throws  CordysGatewayClientException  DOCUMENTME
     * @throws  CordysSOAPException           DOCUMENTME
     */
    int requestFromCordys(int xRequest, long lTimeout, String sSoapAction)
                   throws CordysGatewayClientException, CordysSOAPException;

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The
     * tag returned is the pointer to the SOAP:Envelope. The resulting methods will ge a prefix
     * 'res'. So if you want to use an XPath on the result use: '//res:tuple' to get all the tuples.
     * This method will not wait if the serverwatcher indicates the server is down.
     *
     * @param   xRequest  The request envelope NOM node.
     *
     * @return  The response NOM node.
     *
     * @throws  CordysGatewayClientException  DOCUMENTME
     * @throws  CordysSOAPException           DOCUMENTME
     */
    int requestFromCordysNoBlocking(int xRequest)
                             throws CordysGatewayClientException, CordysSOAPException;

    /**
     * This method sends the request to Cordys. The response is put back into an XML structure. The
     * tag returned is the pointer to the SOAP:Envelope. The resulting methods will ge a prefix
     * 'res'. So if you want to use an XPath on the result use: '//res:tuple' to get all the tuples.
     * This method will not wait if the serverwatcher indicates the server is down.
     *
     * @param   xRequest  The request envelope NOM node.
     * @param   lTimeout  The timeout to use.
     *
     * @return  The response NOM node.
     *
     * @throws  CordysGatewayClientException  DOCUMENTME
     * @throws  CordysSOAPException           DOCUMENTME
     */
    int requestFromCordysNoBlocking(int xRequest, long lTimeout)
                             throws CordysGatewayClientException, CordysSOAPException;
}
