/**
 * (c) 2007 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util.soap;

import com.eibus.xml.nom.Node;

/**
 * Helper class for parsing the SOAP envelope information. This class keeps references to the passed
 * NOM nodes, so care must be taken that these references are cleared with the clear method before
 * the nodes are deleted.
 *
 * @author  mpoyhone
 */
public class SoapEnvelopeInfo
{
    /**
     * Contains the SOAP body node.
     */
    private int body;
    /**
     * Contains the Cordys-specific SOAP header node.
     */
    private int cordysHeader;
    /**
     * Contains the SOAP envelope node.
     */
    private int envelope;
    /**
     * Contains the SOAP header node.
     */
    private int header;
    /**
     * Contains the optional SOAP fault information.
     */
    private SoapFaultInfo soapFault;

    /**
     * Returns SOAP:Body node from the envelope.
     *
     * @param   envelopeNode  SOAP:Envelope node.
     *
     * @return  SOAP:Body node or zero if none was found.
     */
    public static int findBodyFromEnvelope(int envelopeNode)
    {
        if (envelopeNode == 0)
        {
            return 0;
        }

        String soapPrefix = SoapHelpers.getSoapNamespacePrefix(envelopeNode);

        if (soapPrefix == null)
        {
            return 0;
        }

        return Node.getElement(envelopeNode, soapPrefix + "Body");
    }

    /**
     * Returns first method node inside the SOAP:Body.
     *
     * @param   envelopeNode  SOAP:Envelope node.
     *
     * @return  First method node node or zero if none was found.
     */
    public static int findFirstMethodFromEnvelope(int envelopeNode)
    {
        int bodyBode = findBodyFromEnvelope(envelopeNode);

        if (bodyBode == 0)
        {
            return 0;
        }

        return Node.getFirstElement(bodyBode);
    }

    /**
     * Returns SOAP:Header node from the envelope.
     *
     * @param   envelopeNode  SOAP:Envelope node.
     *
     * @return  SOAP:Header node or zero if none was found.
     */
    public static int findHeaderFromEnvelope(int envelopeNode)
    {
        if (envelopeNode == 0)
        {
            return 0;
        }

        String soapPrefix = SoapHelpers.getSoapNamespacePrefix(envelopeNode);

        if (soapPrefix == null)
        {
            return 0;
        }

        return Node.getElement(envelopeNode, soapPrefix + "Header");
    }

    /**
     * Parses the passes SOAP:Envelope node into a new structure.
     *
     * @param   envelopeNode    SOAP:Envelope node.
     * @param   parseSoapFault  If <code>true</code> SOAP:Fault information is also parsed.
     *
     * @return  A new structure containing the parsed information or <code>null</code> if the node
     *          was not a valid SOAP envelope.
     */
    public static SoapEnvelopeInfo parseSoapEnvelope(int envelopeNode, boolean parseSoapFault)
    {
        String soapPrefix = SoapHelpers.getSoapNamespacePrefix(envelopeNode);

        if (soapPrefix == null)
        {
            return null;
        }

        SoapEnvelopeInfo res = new SoapEnvelopeInfo();

        res.envelope = envelopeNode;
        res.header = Node.getElement(res.envelope, soapPrefix + "Header");

        if (res.header != 0)
        {
            res.cordysHeader = Node.getElement(res.header, "header");
        }

        res.body = Node.getElement(res.envelope, soapPrefix + "Body");

        if ((res.body != 0) && parseSoapFault)
        {
            res.soapFault = SoapFaultInfo.findSoapFault(envelopeNode);
        }

        return res;
    }

    /**
     * Cleans up the SOAP envelope information. This method sets the all nodes to zero, so that they
     * cannot be accessed after it is being deleted (although this method does not delete that
     * node).
     *
     * <p>If the SoapFaultInfo object is set, its dispose method is also called.</p>
     */
    public void dispose()
    {
        envelope = 0;
        header = 0;
        cordysHeader = 0;
        body = 0;

        if (soapFault != null)
        {
            soapFault.dispose();
            soapFault = null;
        }
    }

    /**
     * Returns the body.
     *
     * @return  Returns the body.
     */
    public int getBody()
    {
        return body;
    }

    /**
     * Returns the cordysHeader.
     *
     * @return  Returns the cordysHeader.
     */
    public int getCordysHeader()
    {
        return cordysHeader;
    }

    /**
     * Returns the envelope.
     *
     * @return  Returns the envelope.
     */
    public int getEnvelope()
    {
        return envelope;
    }

    /**
     * Returns the first method inside the body.
     *
     * @return  Returns the first method inside the body.
     */
    public int getFirstMethod()
    {
        return (body != 0) ? Node.getFirstElement(body) : 0;
    }

    /**
     * Returns the header.
     *
     * @return  Returns the header.
     */
    public int getHeader()
    {
        return header;
    }

    /**
     * Returns the soapFault.
     *
     * @return  Returns the soapFault.
     */
    public SoapFaultInfo getSoapFault()
    {
        return soapFault;
    }
}
