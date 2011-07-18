package com.cordys.coe.util.xml.dom;

import com.cordys.coe.util.StringUtils;
import com.cordys.coe.util.exceptions.XMLWrapperException;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

/**
 * This class contains helper functions for handling XML via the Xerces API.
 *
 * @author  pgussow
 */
public class XMLHelper
{
    /**
     * Holds the logger to use.
     */
    private static Logger lLogger = Logger.getLogger(XMLHelper.class);

    /**
     * This method creates an attribute with the given name and value.
     *
     * @param  sName    The name of the attribute.
     * @param  sValue   The value of the atrtibute.
     * @param  nParent  The parent node.
     */
    public static void createAttribute(String sName, String sValue, Node nParent)
    {
        if ((nParent != null) && (nParent instanceof Element))
        {
            Element eParent = (Element) nParent;
            eParent.setAttribute(sName, sValue);
        }
    }

    /**
     * This method creates a CDATA node with the given name and value under the passed on parent
     * using the same namespace as it's parent.
     *
     * @param   sName    The name of the new node.
     * @param   sValue   The value of the new node.
     * @param   nParent  The parent node.
     *
     * @return  The newly created node.
     */
    public static Node createCDataElementWithParentNS(String sName, String sValue, Node nParent)
    {
        Node nReturn = null;

        if (nParent != null)
        {
            Document dDoc = nParent.getOwnerDocument();
            nReturn = createElementWithParentNS(sName, nParent);

            Node nTemp = dDoc.createCDATASection(sValue);
            nReturn.appendChild(nTemp);
            nParent.appendChild(nReturn);
        }

        return nReturn;
    }

    /**
     * This method returns a document builder that can be used to parse strings containing XML.
     *
     * @param   bNamespaceAware  Whether or not the document builder should be namespace aware.
     *
     * @return  The new document builder
     */
    public static DocumentBuilder createDocumentBuilder(boolean bNamespaceAware)
    {
        // Create the document builder.
        DocumentBuilderFactory dbfFactory = DocumentBuilderFactory.newInstance();
        dbfFactory.setNamespaceAware(bNamespaceAware);

        DocumentBuilder dbBuilder = null;

        try
        {
            dbBuilder = dbfFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e)
        {
            // Ignore the exception.
            lLogger.warn("Error creating the document builder", e);
        }

        return dbBuilder;
    }

    /**
     * This method creates a namespace-aware document based on the passed on inputstream.
     *
     * @param   isStream  The inputstream that needs to be parsed.
     *
     * @return  The document for this stream.
     *
     * @throws  XMLWrapperException  DOCUMENTME
     */
    public static Document createDocumentFromStream(InputStream isStream)
                                             throws XMLWrapperException
    {
        return createDocumentFromStream(isStream, true);
    }

    /**
     * This method creates a document based on the passed on inputstream.
     *
     * @param   isStream        The inputstream that needs to be parsed.
     * @param   namespaceAware  If the document should be namespace-aware.
     *
     * @return  The document for this stream.
     *
     * @throws  XMLWrapperException  DOCUMENTME
     */
    public static Document createDocumentFromStream(InputStream isStream, boolean namespaceAware)
                                             throws XMLWrapperException
    {
        DocumentBuilder dbBuilder = createDocumentBuilder(namespaceAware);

        Document dReturn;

        try
        {
            dReturn = dbBuilder.parse(isStream);
        }
        catch (Exception e)
        {
            throw new XMLWrapperException(e);
        }

        return dReturn;
    }

    /**
     * This method creates a namespace-aware document in which the passed on XML is loaded.
     *
     * @param   sXML  The XML to parse.
     *
     * @return  The new document.
     */
    public static Document createDocumentFromXML(String sXML)
    {
        return createDocumentFromXML(sXML, true);
    }

    /**
     * This method creates a new document in which the passed on XML is loaded.
     *
     * @param   sXML            The XML to parse.
     * @param   namespaceAware  If the document should be namespace-aware.
     *
     * @return  The new document.
     */
    public static Document createDocumentFromXML(String sXML, boolean namespaceAware)
    {
        Document dReturn = null;

        DocumentBuilder dbBuilder = createDocumentBuilder(namespaceAware);

        if (dbBuilder != null)
        {
            try
            {
                dReturn = dbBuilder.parse(new ByteArrayInputStream(sXML.getBytes("UTF-8")));
            }
            catch (Exception e)
            {
                // Ignore the exception, we'll return null.
                lLogger.warn("Error parsing the XML " + sXML, e);
            }
        }

        return dReturn;
    }

    /**
     * This method creates a new document in which the passed on XML is loaded.
     *
     * @param   sXML            The XML to parse.
     * @param   namespaceAware  If the document should be namespace-aware.
     *
     * @return  The new document.
     *
     * @throws  Exception  In case of any parsing exceptions.
     */
    public static Document createDocumentFromXMLExc(String sXML, boolean namespaceAware)
                                             throws Exception
    {
        Document dReturn = null;

        DocumentBuilder dbBuilder = createDocumentBuilder(namespaceAware);

        if (dbBuilder != null)
        {
            dReturn = dbBuilder.parse(new ByteArrayInputStream(sXML.getBytes("UTF-8")));
        }

        return dReturn;
    }

    /**
     * This method creates a textnode with the given name and value under the passed on parent.
     *
     * @param   sName    The name of the new node.
     * @param   nParent  The parent node.
     *
     * @return  The newly created node.
     */
    public static Element createElement(String sName, Node nParent)
    {
        Element eReturn = null;

        if (nParent != null)
        {
            Document dDoc = nParent.getOwnerDocument();
            eReturn = dDoc.createElement(sName);
            nParent.appendChild(eReturn);
        }
        else
        {
            Document oDoc = createDocumentBuilder(false).newDocument();
            eReturn = oDoc.createElement(sName);
        }

        return eReturn;
    }

    /**
     * This method creates a textnode with the given name and value under the passed on parent.
     *
     * @param   sName       The name of the new node.
     * @param   sNamespace  The namespace for the element.
     * @param   nParent     The parent node.
     *
     * @return  The newly created node.
     */
    public static Element createElementNS(String sName, String sNamespace, Node nParent)
    {
        Element eReturn = null;

        if (nParent != null)
        {
            Document dDoc = nParent.getOwnerDocument();
            eReturn = dDoc.createElementNS(sNamespace, sName);
            nParent.appendChild(eReturn);
        }
        else
        {
            Document oDoc = createDocumentBuilder(false).newDocument();
            eReturn = oDoc.createElement(sName);
        }

        return eReturn;
    }

    /**
     * This method creates a textnode with the given name and value under the passed on parent.
     *
     * @param   sName    The name of the new node.
     * @param   nParent  The parent node.
     *
     * @return  The newly created node.
     */
    public static Element createElementWithParentNS(String sName, Node nParent)
    {
        Element eReturn = null;

        if (nParent != null)
        {
            Document dDoc = nParent.getOwnerDocument();
            String namespaceURI = nParent.getNamespaceURI();
            if (StringUtils.isSet(namespaceURI))
            {
            	eReturn = dDoc.createElementNS(namespaceURI, sName);
            }
            else
            {
            	//Create element with empty namespace.
            	eReturn = dDoc.createElement(sName);
            }
            
            nParent.appendChild(eReturn);
        }
        else
        {
            Document oDoc = createDocumentBuilder(false).newDocument();
            eReturn = oDoc.createElement(sName);
        }

        return eReturn;
    }

    /**
     * This method creates a new textnode with the passed on value.
     *
     * @param  sValue   The value for the new textnode.
     * @param  nParent  The parent node.
     */
    public static void createText(String sValue, Node nParent)
    {
        if (nParent != null)
        {
            Document dDoc = nParent.getOwnerDocument();
            Node nTemp = dDoc.createTextNode(sValue);
            nParent.appendChild(nTemp);
        }
    }

    /**
     * This method creates a textnode with the given name and value under the passed on parent.
     *
     * @param   sName    The name of the new node.
     * @param   sValue   The value of the new node.
     * @param   nParent  The parent node.
     *
     * @return  The newly created node.
     */
    public static Node createTextElement(String sName, String sValue, Node nParent)
    {
        Node nReturn = null;

        if (nParent != null)
        {
            Document dDoc = nParent.getOwnerDocument();
            nReturn = dDoc.createElement(sName);

            Node nTemp = dDoc.createTextNode(sValue);
            nReturn.appendChild(nTemp);
            nParent.appendChild(nReturn);
        }

        return nReturn;
    }

    /**
     * This method creates a textnode with the given name and value under the passed on parent using
     * the same namespace as it's parent.
     *
     * @param   sName    The name of the new node.
     * @param   sValue   The value of the new node.
     * @param   nParent  The parent node.
     *
     * @return  The newly created node.
     */
    public static Node createTextElementWithParentNS(String sName, String sValue, Node nParent)
    {
        Node nReturn = null;

        if (nParent != null)
        {
            Document dDoc = nParent.getOwnerDocument();
            nReturn = createElementWithParentNS(sName, nParent);

            Node nTemp = dDoc.createTextNode(sValue);
            nReturn.appendChild(nTemp);
            nParent.appendChild(nReturn);
        }

        return nReturn;
    }

    /**
     * This is an XML helper method that will create the proper XML. The hashmap can contain nested
     * hashmaps.
     *
     * @param  dDoc      The document to use.
     * @param  nRoot     The root element.
     * @param  hmValues  The hashmap containing the tags.
     */
    @SuppressWarnings("unchecked")
    public static void createXML(Document dDoc, Node nRoot, HashMap<String, ?> hmValues)
    {
        for (Iterator<String> iTags = hmValues.keySet().iterator(); iTags.hasNext();)
        {
            String sTagName = (String) iTags.next();

            Element eTemp = dDoc.createElement(sTagName);
            nRoot.appendChild(eTemp);

            Object oValue = hmValues.get(sTagName);

            if (oValue instanceof String)
            {
                String sValue = (String) oValue;

                if ((sValue != null) && (sValue.length() > 0))
                {
                    eTemp.appendChild(dDoc.createTextNode((String) oValue));
                }
            }
            else if (oValue instanceof HashMap)
            {
                createXML(dDoc, eTemp, (HashMap<String, ?>) oValue);
            }
        }
    }

    /**
     * This method returns the data of the given node. If the node was not found or contained no
     * data null is returned.
     *
     * @param   nNode   The node to search under.
     * @param   sXPath  The XPath to execute.
     *
     * @return  The data of the passed on node.
     *
     * @throws  XMLWrapperException  DOCUMENTME
     */
    public static String getData(Node nNode, String sXPath)
                          throws XMLWrapperException
    {
        String sReturn = null;

        if (nNode != null)
        {
            Node nTemp = null;

            try
            {
                nTemp = XPathHelper.selectSingleNode(nNode, sXPath);
            }
            catch (TransformerException e)
            {
                throw new XMLWrapperException(e);
            }

            if (nTemp != null)
            {
                sReturn = nTemp.getNodeValue();
            }
        }

        return sReturn;
    }

    /**
     * This method returns the XML node pointing to the soap response. This node has to be used as a
     * base for searching. This is because of the way that Xerces handles namespaces. The method
     * responses will receive a prefix that is specified by sPrefix. This is needed to use it in
     * XPath. The XPath should look like this: //sPrefix:tuple.
     *
     * @param   nEnvelope  The envelope node.
     * @param   sPrefix    The prefix that should be added.
     *
     * @return  The pointer to the SOAP response.
     *
     * @throws  XMLWrapperException  DOCUMENTME
     */
    public static Node getMethodResponse(Node nEnvelope, String sPrefix)
                                  throws XMLWrapperException
    {
        Node[] anTemp = getMethodResponses(nEnvelope, sPrefix);

        return anTemp[0];
    }

    /**
     * This method returns the XML node pointing to the soap response. This node has to be used as a
     * base for searching. This is because of the way that Xerces handles namespaces. The method
     * responses will receive a prefix that is specified by sPrefix. This is needed to use it in
     * XPath. The XPath should look like this: //sPrefix:tuple.
     *
     * @param   nEnvelope  The envelope node.
     * @param   sPrefix    The prefix that should be added.
     *
     * @return  The pointer to the SOAP response.
     *
     * @throws  XMLWrapperException  DOCUMENTME
     */
    public static Node[] getMethodResponses(Node nEnvelope, String sPrefix)
                                     throws XMLWrapperException
    {
        ArrayList<Node> alTemp = new ArrayList<Node>();

        Node nBody = null;

        try
        {
            nBody = XPathHelper.selectSingleNode(nEnvelope, "/SOAP:Envelope/SOAP:Body");
        }
        catch (TransformerException te)
        {
            throw new XMLWrapperException(te);
        }

        if (nBody != null)
        {
            Node nCurrent = nBody.getFirstChild();

            while (nCurrent != null)
            {
                if (nCurrent.getNodeType() == Node.ELEMENT_NODE)
                {
                    nCurrent.setPrefix(sPrefix);
                    alTemp.add(nCurrent);
                }
                nCurrent = nCurrent.getNextSibling();
            }
        }

        return alTemp.toArray(new Node[alTemp.size()]);
    }

    /**
     * This method returns a String array with the data for each node that matched the XPAth.
     *
     * @param   nNode   the node to execute the XPath on.
     * @param   sXPath  the XPath to execute.
     *
     * @return  All the data for the nodes.
     *
     * @throws  XMLWrapperException  DOCUMENTME
     */
    public static String[] getMultipleData(Node nNode, String sXPath)
                                    throws XMLWrapperException
    {
        String[] asReturn = null;
        ArrayList<String> alValues = new ArrayList<String>();

        NodeList nlList;

        try
        {
            nlList = XPathHelper.selectNodeList(nNode, sXPath);
        }
        catch (TransformerException e)
        {
            throw new XMLWrapperException(e);
        }

        for (int iCount = 0; iCount < nlList.getLength(); iCount++)
        {
            Node nTemp = nlList.item(iCount);
            String sTemp = XMLHelper.getData(nTemp, "./text()");
            alValues.add(sTemp);
        }

        asReturn = alValues.toArray(new String[alValues.size()]);

        return asReturn;
    }

    /**
     * This method selects all the nodes that matches the passed on Xpath expression.
     *
     * @param   nNode   The node so search.
     * @param   sXPath  The XPath expression.
     *
     * @return  The nodelist containing all nodes matching the given XPath.
     *
     * @throws  XMLWrapperException  DOCUMENTME
     */
    public static NodeList getNodeList(Node nNode, String sXPath)
                                throws XMLWrapperException
    {
        NodeList nlReturn = null;

        try
        {
            nlReturn = XPathHelper.selectNodeList(nNode, sXPath);
        }
        catch (TransformerException e)
        {
            throw new XMLWrapperException(e);
        }

        return nlReturn;
    }

    /**
     * This method loads a XML file into a document. The parser used is namespace aware.
     *
     * @param   sFilename  The name of the file.
     *
     * @return  The document for this file.
     *
     * @throws  XMLWrapperException  In case of any exceptions.
     */
    public static Document loadXMLFile(String sFilename)
                                throws XMLWrapperException
    {
        return loadXMLFile(sFilename, true);
    }

    /**
     * This method loads a XML file into a document.
     *
     * @param   sFilename        The name of the file.
     * @param   bNamespaceAware  Indicates whether or not the parser should be namespace-aware
     *
     * @return  The document for this file.
     *
     * @throws  XMLWrapperException  In case of any exceptions.
     */
    public static Document loadXMLFile(String sFilename, boolean bNamespaceAware)
                                throws XMLWrapperException
    {
        Document dReturn = null;

        DocumentBuilder dbBuilder = XMLHelper.createDocumentBuilder(bNamespaceAware);

        if (dbBuilder != null)
        {
            try
            {
                dReturn = dbBuilder.parse(new FileInputStream(sFilename));
            }
            catch (FileNotFoundException e)
            {
                throw new XMLWrapperException(e);
            }
            catch (SAXException e)
            {
                throw new XMLWrapperException(e);
            }
            catch (IOException e)
            {
                throw new XMLWrapperException(e);
            }
        }
        return dReturn;
    }

    /**
     * This method returns the data of the given node. If the node was not found or contained no
     * data null is returned.
     *
     * @param   nNode   The node to search under.
     * @param   sXPath  The XPath to execute.
     *
     * @return  The data of the passed on node.
     *
     * @throws  XMLWrapperException  DOCUMENTME
     */
    public static String prGetData(Node nNode, String sXPath)
                            throws XMLWrapperException
    {
        return prGetData(nNode, sXPath, null);
    }

    /**
     * This method returns the data of the given node. If the node was not found or contained no
     * data null is returned.
     *
     * @param   nNode     The node to search under.
     * @param   sXPath    The XPath to execute.
     * @param   sDefault  The default value.
     *
     * @return  The data of the passed on node.
     *
     * @throws  XMLWrapperException  DOCUMENTME
     */
    public static String prGetData(Node nNode, String sXPath, String sDefault)
                            throws XMLWrapperException
    {
        String sReturn = null;

        if (nNode != null)
        {
            Node nTemp = null;

            try
            {
                nTemp = XPathHelper.prSelectSingleNode(nNode, sXPath);
            }
            catch (TransformerException e)
            {
                throw new XMLWrapperException(e);
            }

            if (nTemp != null)
            {
                sReturn = nTemp.getNodeValue();
            }
        }

        if (sReturn == null)
        {
            sReturn = sDefault;
        }

        return sReturn;
    }

    /**
     * This method returns an new Node, without any prefixes set.
     *
     * @param       aoNode
     *
     * @return      A new Node (in another document), without prefixes
     *
     * @throws      XMLWrapperException  DOCUMENTME
     *
     * @deprecated  This method should not be used since it uses the CoEDomWriter which is not
     *              perfect.
     */
    public static Node stripPrefixes(Node aoNode)
                              throws XMLWrapperException
    {
        CoEDOMWriter oWriter = new CoEDOMWriter(aoNode);

        oWriter.disablePrefixes();

        DocumentBuilderFactory oFactory = DocumentBuilderFactory.newInstance();

        // oFactory.setNamespaceAware(true);
        Document oDoc = null;

        try
        {
            DocumentBuilder oBuilder = oFactory.newDocumentBuilder();

            // Parse the XML
            oDoc = oBuilder.parse(new ByteArrayInputStream(oWriter.getBytes()));
        }
        catch (ParserConfigurationException pce)
        {
            throw new XMLWrapperException(pce);
        }
        catch (IOException ioe)
        {
            throw new XMLWrapperException(ioe);
        }
        catch (SAXException se)
        {
            throw new XMLWrapperException(se);
        }
        return oDoc.getDocumentElement();
    }

    /**
     * Convert a document XML to string.
     *
     * @param   aoDocument  The document containing the xml
     *
     * @return  The xml as string
     */
    public static String XML2String(Document aoDocument)
    {
        return XML2String(aoDocument.getDocumentElement());
    }

    /**
     * Convert a document XML to string.
     *
     * @param   aoNode  The node containing the xml
     *
     * @return  The xml as string
     */
    public static String XML2String(Node aoNode)
    {
        return NiceDOMWriter.write(aoNode);
    }
}
