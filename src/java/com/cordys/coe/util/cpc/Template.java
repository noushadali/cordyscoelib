package com.cordys.coe.util.cpc;

import com.cordys.coe.util.soap.ISOAPWrapper;
import com.cordys.coe.util.soap.SOAPWrapper;

import com.eibus.connector.nom.Connector;

import com.eibus.xml.nom.Node;
import com.eibus.xml.xpath.NodeSet;
import com.eibus.xml.xpath.ResultNode;
import com.eibus.xml.xpath.XPath;

import java.io.IOException;

import org.xml.sax.SAXException;

/**
 * DOCUMENTME.
 *
 * @author  $author$
 */
public class Template
{
    /**
     * Retrieve a Template from Coboc.
     *
     * @param   connector  Connector
     * @param   userDn     User DN
     * @param   version    organization, isv, user or null
     * @param   template   Template key
     *
     * @return  Template
     *
     * @throws  SAXException  SAXException
     * @throws  IOException   IOException
     */
    public static int get(Connector connector, String userDn, String version, String template)
                   throws SAXException, IOException
    {
        if ((version != null) &&
                !(version.equalsIgnoreCase("organization") || version.equalsIgnoreCase("isv") ||
                      version.equalsIgnoreCase("user")))
        {
            throw new IllegalArgumentException("version '" + version + "' not a valid argument");
        }

        int iEnvRequest = 0;
        int response = 0;
        int returnNode = 0;

        final ISOAPWrapper swSoap = new SOAPWrapper(connector);

        try
        {
            swSoap.setUser(userDn);

            final int iMethod = swSoap.createSoapMethod(null, "GetXMLObject",
                                                        "http://schemas.cordys.com/1.0/coboc");
            final int key = Node.createTextElement("key", template, iMethod);
            Node.setAttribute(key, "filter", "template");
            Node.setAttribute(key, "type", "entity");

            if (version != null)
            {
                Node.setAttribute(key, "version", version);
            }
            iEnvRequest = SOAPWrapper.getEnvelope(iMethod);
            response = swSoap.sendAndWait(iEnvRequest, true);

            XPath xpathXsd = XPath.getXPathInstance("//GetXMLObjectResponse/tuple/old/ENTITY/XSD_SCHEMA/child::*");
            NodeSet xsdNodeSet = xpathXsd.selectNodeSet(response);
            long resultNode = xsdNodeSet.next();

            if (resultNode != 0L)
            {
                int node = ResultNode.getElementNode(resultNode);
                returnNode = Node.clone(node, true);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error fetching Coboc template '" + template + "'", e);
        }
        finally
        {
            swSoap.freeXMLNodes();
        }
        return returnNode;
    }
}
