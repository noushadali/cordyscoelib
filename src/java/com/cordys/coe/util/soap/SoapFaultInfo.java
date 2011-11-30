/**
 * (c) 2007 Cordys R&D B.V. All rights reserved. The computer program(s) is the proprietary information of Cordys B.V. and
 * provided under the relevant License Agreement containing restrictions on use and disclosure. Use is subject to the License
 * Agreement.
 */
package com.cordys.coe.util.soap;

import com.cordys.coe.util.general.Util;
import com.cordys.coe.util.xml.NamespaceDefinitions;
import com.cordys.coe.util.xml.XMLHelpers;
import com.cordys.coe.util.xml.nom.XPathHelper;
import com.eibus.soap.BodyBlock;
import com.eibus.xml.nom.Node;
import com.eibus.xml.xpath.XPathMetaInfo;

/**
 * Contains information from a SOAP:Fault. This class also contains a static method that can be used for checking the SOAP message
 * for a SOAP:Fault element. The original faultdetail element NOM node refenrece is kept in this object, so the caller must take
 * care that the <code>dispose</code> method is called to clear this reference before the SOAP XML is deleted so that the NOM node
 * is not accidentally used after this. As the faultdetail node is not cloned this class will not impose a possible memory leak
 * situation.
 * 
 * @author mpoyhone
 */
public class SoapFaultInfo
{
    /**
     * Contains the faultactor.
     */
    private String sFaultactor;
    /**
     * Contains the faultcode.
     */
    private String sFaultcode;
    /**
     * Contains the faultstring.
     */
    private String sFaultstring;
    /**
     * Contains the faultdetail element.
     */
    private int xDetail;
    /** The namespace/prefix mappings. */
    private static XPathMetaInfo s_xmi = new XPathMetaInfo();

    static
    {
        s_xmi.addNamespaceBinding("soap", NamespaceDefinitions.XMLNS_SOAP_1_1);
    }

    /**
     * Constructor for SoapFaultInfo.
     */
    public SoapFaultInfo()
    {
    }

    /**
     * Constructor for SoapFaultInfo. Used for creating SOAP fault responses.
     * 
     * @param faultcode Fault code.
     */
    public SoapFaultInfo(String faultcode)
    {
        this(faultcode, null, null);
    }

    /**
     * Constructor for SoapFaultInfo. Used for creating SOAP fault responses.
     * 
     * @param faultcode Fault code.
     * @param faultstring Optional fault string.
     */
    public SoapFaultInfo(String faultcode, String faultstring)
    {
        sFaultcode = faultcode;
        sFaultstring = faultstring;
    }

    /**
     * Constructor for SoapFaultInfo. Used for creating SOAP fault responses.
     * 
     * @param faultcode Fault code.
     * @param faultstring Optional fault string.
     * @param faultactor Optional fault actor.
     */
    public SoapFaultInfo(String faultcode, String faultstring, String faultactor)
    {
        sFaultcode = faultcode;
        sFaultstring = faultstring;
        sFaultactor = faultactor;
    }

    /**
     * Tries to find a SOAP:Fault from the given SOAP:Envelope.
     * 
     * @param xSoapEnvelope SOAP:Envelope node.
     * @return SOAP:Fault information or <code>null</code> if the SOAP:Fault was not present.
     */
    public static SoapFaultInfo findSoapFault(int xSoapEnvelope)
    {
        String sSoapPrefix = SoapHelpers.getSoapNamespacePrefix(xSoapEnvelope);

        if (sSoapPrefix == null)
        {
            throw new IllegalArgumentException("SOAP namespace not found from the response.");
        }

        // Check for SOAP fault
        int xSoapFaultNode = XPathHelper.selectSingleNode(xSoapEnvelope, "soap:Body/soap:Fault");

        if (xSoapFaultNode == 0)
        {
            return null;
        }

        SoapFaultInfo info = new SoapFaultInfo();

        int xNode;

        if ((xNode = XPathHelper.selectSingleNode(xSoapFaultNode, ".//*[local-name()='faultcode']")) != 0)
        {
            // Standard SOAP faultcode.
            String value = Node.getData(xNode);

            // Remove the namespace prefix from the faultcode, if it is present.
            String prefix = XMLHelpers.getNamespacePrefix(xNode, "http://schemas.xmlsoap.org/soap/envelope/");

            if ((prefix != null) && (prefix.length() > 0) && value.startsWith(prefix))
            {
                value = value.substring(prefix.length());
            }

            info.setFaultcode(value);
        }
        else if ((xNode = XPathHelper.selectSingleNode(xSoapFaultNode, ".//soap:faultcode", s_xmi)) != 0)
        {
            // Cordys C2 SOAP faultcode.
            info.setFaultcode(Node.getData(xNode));
        }

        if ((xNode = XPathHelper.selectSingleNode(xSoapFaultNode, ".//*[local-name()='faultactor']")) != 0)
        {
            // Standard SOAP faultactor. There is no faultactor in C2
            info.setFaultactor(Node.getData(xNode));
        }

        if ((xNode = XPathHelper.selectSingleNode(xSoapFaultNode, ".//*[local-name()='faultstring']")) != 0)
        {
            // Standard SOAP faultstring.
            info.setFaultstring(Node.getData(xNode));
        }
        else if ((xNode = XPathHelper.selectSingleNode(xSoapFaultNode, ".//*[local-name()='faultstring']")) != 0)
        {
            // Cordys C2 SOAP faultstring.
            info.setFaultstring(Node.getData(xNode));
        }

        if ((xNode = XPathHelper.selectSingleNode(xSoapFaultNode, ".//*[local-name()='detail']")) != 0)
        {
            // Standard SOAP faultdetail.
            info.setDetail(xNode);
        }
        else if ((xNode = XPathHelper.selectSingleNode(xSoapFaultNode, ".//soap:detail", s_xmi)) != 0)
        {
            // Cordys C2 SOAP faultdetail.
            info.setDetail(xNode);
        }

        return info;
    }

    /**
     * Tries to find a SOAP:Fault node from the given SOAP:Envelope.
     * 
     * @param xSoapEnvelope SOAP:Envelope node.
     * @return SOAP:Fault node or zero if the SOAP:Fault was not present.
     */
    public static int findSoapFaultNode(int xSoapEnvelope)
    {
        String sSoapPrefix = SoapHelpers.getSoapNamespacePrefix(xSoapEnvelope);

        if (sSoapPrefix == null)
        {
            throw new IllegalArgumentException("SOAP namespace not found from the response.");
        }

        // Check for SOAP fault
        int xSoapFaultNode = XPathHelper.selectSingleNode(xSoapEnvelope, "soap:Body/soap:Fault");

        return xSoapFaultNode;
    }

    /**
     * Creates a NOM connector SOAP:Fault based on the information in this object. Detail nodes are cloned to the created XML.
     * 
     * @param bbResponse Response body block.
     */
    public void createConnectorSoapFault(BodyBlock bbResponse)
    {
        createConnectorSoapFault(bbResponse, true, null, false);
    }

    /**
     * Creates a NOM connector SOAP:Fault based on the information in this object.
     * 
     * @param bbResponse Response body block.
     * @param bCloneDetailNodes If <code>true</code> the detail nodes are cloned, otherwise they are moved from the original XML.
     */
    public void createConnectorSoapFault(BodyBlock bbResponse, boolean bCloneDetailNodes)
    {
        createConnectorSoapFault(bbResponse, bCloneDetailNodes, null, false);
    }

    /**
     * Creates a NOM connector SOAP:Fault based on the information in this object. If the exception is given the, the details node
     * in the class is ignored and the exception stack trace is added to the created details node.
     * 
     * @param bbResponse Response body block.
     * @param tException Exception to be used for the detail node or <code>null</code> if the the node in this object should be
     *            used.
     * @param bUseXmlStacktrace If <code>true</code> the generated stack trace will be in XML format, otherwise the standard Java
     *            stacktrace is used.
     */
    public void createConnectorSoapFault(BodyBlock bbResponse, Throwable tException, boolean bUseXmlStacktrace)
    {
        createConnectorSoapFault(bbResponse, true, tException, bUseXmlStacktrace);
    }

    /**
     * Cleans up the SOAP:Fault information. This method sets the faultdetail node to zero, so that it cannot be accessed after it
     * is being deleted (although this method does not delete that node).
     */
    public void dispose()
    {
        sFaultcode = null;
        sFaultstring = null;
        xDetail = 0;
    }

    /**
     * Returns the detail.
     * 
     * @return Returns the detail.
     */
    public int getDetail()
    {
        return xDetail;
    }

    /**
     * Returns the faultactor.
     * 
     * @return Returns the faultactor.
     */
    public String getFaultactor()
    {
        return sFaultactor;
    }

    /**
     * Returns the faultcode.
     * 
     * @return Returns the faultcode.
     */
    public String getFaultcode()
    {
        return sFaultcode;
    }

    /**
     * Returns the faultstring.
     * 
     * @return Returns the faultstring.
     */
    public String getFaultstring()
    {
        return sFaultstring;
    }

    /**
     * The detail to set.
     * 
     * @param aDetail The detail to set.
     */
    public void setDetail(int aDetail)
    {
        xDetail = aDetail;
    }

    /**
     * Sets the faultactor.
     * 
     * @param faultactor The faultactor to be set.
     */
    public void setFaultactor(String faultactor)
    {
        this.sFaultactor = faultactor;
    }

    /**
     * The faultcode to set.
     * 
     * @param aFaultcode The faultcode to set.
     */
    public void setFaultcode(String aFaultcode)
    {
        sFaultcode = aFaultcode;
    }

    /**
     * The faultstring to set.
     * 
     * @param aFaultstring The faultstring to set.
     */
    public void setFaultstring(String aFaultstring)
    {
        sFaultstring = aFaultstring;
    }

    /**
     * Returns the SOAP:Fault information as a string. The format is: [faultcode='CODE', faultactor='ACTOR', faultstring='STRING',
     * detail='DETAIL']
     * 
     * @return DOCUMENTME
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuffer sMsg = new StringBuffer(512);

        sMsg.append("[faultcode='");

        if (sFaultcode != null)
        {
            sMsg.append(sFaultcode);
        }

        sMsg.append("', faultactor='");

        if (sFaultactor != null)
        {
            sMsg.append(sFaultactor);
        }

        sMsg.append("', faultstring='");

        if (sFaultstring != null)
        {
            sMsg.append(sFaultstring);
        }

        sMsg.append("', detail='");

        if (xDetail != 0)
        {
            int xChild = Node.getFirstChild(xDetail);

            while (xChild != 0)
            {
                sMsg.append(Node.writeToString(xChild, false));
                xChild = Node.getNextSibling(xChild);
            }
        }
        sMsg.append("']");

        return sMsg.toString();
    }

    /**
     * Creates a NOM connector SOAP:Fault based on the information in this object. If the exception is given the, the details node
     * in the class is ignored and the exception stack trace is added to the created details node.
     * 
     * @param bbResponse Response body block.
     * @param bCloneDetailNodes If <code>true</code> the detail node are cloned.
     * @param tException Exception to be used for the detail node or <code>null</code> if the the node in this object should be
     *            used.
     * @param bUseXmlStacktrace If <code>true</code> the generated stack trace will be in XML format, otherwise the standard Java
     *            stacktrace is used.
     */
    @SuppressWarnings("deprecation")
    private void createConnectorSoapFault(BodyBlock bbResponse, boolean bCloneDetailNodes, Throwable tException,
            boolean bUseXmlStacktrace)
    {
        int xFaultDetails = bbResponse.createSOAPFault(getFaultcode(), getFaultstring());

        if (tException != null)
        {
            if (bUseXmlStacktrace)
            {
                Util.appendExceptionToXml(tException, xFaultDetails);
            }
            else
            {
                String sStackTrace = Util.getStackTrace(tException);

                Node.getDocument(xFaultDetails).createText(sStackTrace, xFaultDetails);
            }
        }
        else if (xDetail != 0)
        {
            int xChild = Node.getFirstChild(xDetail);

            while (xChild != 0)
            {
                int xNext = Node.getNextSibling(xChild);
                int xTmp = bCloneDetailNodes ? Node.duplicate(xChild) : xChild;

                Node.appendToChildren(xTmp, xFaultDetails);
                xChild = xNext;
            }
        }
    }
}
