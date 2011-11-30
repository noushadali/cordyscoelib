package com.cordys.coe.util.xml.nom;

import com.cordys.coe.util.FileUtils;

import com.eibus.util.system.EIBProperties;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Node;
import com.eibus.xml.xpath.NodeSet;
import com.eibus.xml.xpath.ResultNode;
import com.eibus.xml.xpath.XPath;
import com.eibus.xml.xpath.XPathMetaInfo;
import com.eibus.xml.xpath.XPathResult;
import com.eibus.xml.xpath.XSLT;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class provides XML Utilities for use in Studio.
 *
 * @author  hvdvliert
 */
public class XmlUtils
{
    /**
     * Evaluate a xpath expression to a String.
     *
     * <p>Example of a Business Process expression:</p>
     *
     * <pre>
               com.cordys.coe.util.bpm.XmlUtils::evaluate(/xml, "/xml/order[id='12']")
     * </pre>
     *
     * @param   xml    the xml node
     * @param   xpath  the xpath expression
     *
     * @return  string the result
     */
    public static String evaluate(int xml, String xpath)
    {
        int xmlNode = Node.unlink(Node.getFirstElement(xml));
        String result = _evaluate(xmlNode, xpath);
        Node.appendToChildren(xmlNode, xml);

        return result;
    }

    /**
     * DOCUMENTME.
     *
     * @param   xmlNode  DOCUMENTME
     * @param   xpath    DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    public static String _evaluate(int xmlNode, String xpath)
    {
        String result = "";

        com.eibus.xml.xpath.XPath oXPath = com.eibus.xml.xpath.XPath.getXPathInstance(xpath);

        XPathResult xpathResult = oXPath.evaluate(xmlNode);
        int type = xpathResult.getType();

        switch (type)
        {
            case XPathResult.XPATH_BOOLEAN:
                result = String.valueOf(xpathResult.getBooleanResult());

                break;

            case XPathResult.XPATH_NODESET:

                NodeSet nodeSet = xpathResult.removeNodeSetFromResult();

                if (nodeSet.hasNext())
                {
                    long resultNode = nodeSet.next();

                    if (ResultNode.isAttribute(resultNode))
                    {
                        result = ResultNode.getStringValue(resultNode);
                    }
                    else
                    {
                        int iNode = ResultNode.getElementNode(resultNode);
                        result = Node.writeToString(iNode, false);
                    }
                }

                break;

            case XPathResult.XPATH_NUMBER:
                result = String.valueOf(xpathResult.getNumberResult());

                break;

            case XPathResult.XPATH_STRING:
                result = xpathResult.getStringResult();

                break;
        }

        return result;
    }

    /**
     * Evaluate a xpath expression and returns an NodeObject (XML).
     *
     * <p>Example of a Business Process expression:</p>
     *
     * <pre>
               com.cordys.coe.util.bpm.XmlUtils::evaluateToXml(/xml, "/xml/order[id='12']")
     * </pre>
     *
     * @param   xml    the xml node
     * @param   xpath  the xpath expression
     *
     * @return  the result xml
     */
    public static int evaluateToXml(int xml, String xpath)
    {
        int xmlNode = Node.unlink(Node.getFirstElement(xml));
        int result = _evaluateToXml(xmlNode, xpath);
        Node.appendToChildren(xmlNode, xml);

        return result;
    }

    /**
     * DOCUMENTME.
     *
     * @param   xml    DOCUMENTME
     * @param   xpath  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    public static int _evaluateToXml(int xml, String xpath)
    {
        String result = "";
        Document doc = Node.getDocument(xml);
        int resultNode = doc.createElement("result");
        boolean isNull = true;
        int type = 0;

        com.eibus.xml.xpath.XPath oXPath = com.eibus.xml.xpath.XPath.getXPathInstance(xpath);

        XPathResult xpathResult = oXPath.evaluate(xml);
        type = xpathResult.getType();

        switch (type)
        {
            case XPathResult.XPATH_BOOLEAN:
                result = String.valueOf(xpathResult.getBooleanResult());
                Node.createTextElement("result", result, resultNode);
                isNull = false;

                break;

            case XPathResult.XPATH_NODESET:

                NodeSet nodeSet = xpathResult.removeNodeSetFromResult();
                // int nodeSetResult = 0;

                while (nodeSet.hasNext())
                {
                    long lResultNode = nodeSet.next();

                    if (ResultNode.isAttribute(lResultNode))
                    {
                        result = ResultNode.getStringValue(lResultNode);
                        // Node.createTextElement("result", result,
                        // resultNode);
                        // resultNode = doc.createElement("result");
                        Node.setData(resultNode, result);
                    }
                    else
                    {
                        int iNode = ResultNode.getElementNode(lResultNode);

                        // if (nodeSetResult == 0)
                        // {
                        // nodeSetResult = Node.createElement("result",
                        // resultNode);
                        // }

                        Node.appendToChildren(Node.clone(iNode, true), resultNode);
                    }

                    isNull = false;
                }

                break;

            case XPathResult.XPATH_NUMBER:
                result = String.valueOf(xpathResult.getNumberResult());
                Node.createTextElement("result", result, resultNode);
                isNull = false;

                break;

            case XPathResult.XPATH_STRING:
                result = xpathResult.getStringResult();
                Node.createTextElement("result", result, resultNode);
                isNull = false;

                break;
        }

        if (isNull)
        {
            int node = Node.createElement("result", resultNode);
            Node.setAttribute(node, "isNull", String.valueOf(isNull));
        }
        else
        {
            // int node = Node.getFirstChild(resultNode);
            Node.setAttribute(resultNode, "type", getXPathType(type));
            Node.setAttribute(resultNode, "isNull", String.valueOf(isNull));
        }

        return resultNode;
    }

    /**
     * Transform a XML document using a Stylesheet to a String.
     *
     * @param   xml    The Xml
     * @param   aXSLT  The xslt
     *
     * @return  transformed String
     */
    public static String xslStrTransform(int xml, int aXSLT)
    {
        int xmlNode = Node.unlink(Node.getFirstElement(xml));
        int xsltNode = Node.unlink(Node.getFirstElement(aXSLT));

        String result = _xslStrTransform(xmlNode, xsltNode);

        Node.appendToChildren(xmlNode, xml);
        Node.appendToChildren(xsltNode, aXSLT);

        return result;
    }

    /**
     * DOCUMENTME.
     *
     * @param   xml    DOCUMENTME
     * @param   aXSLT  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    public static String _xslStrTransform(int xml, int aXSLT)
    {
        String xsltStr = Node.writeToString(aXSLT, false);
        XSLT xslt = XSLT.parseFromString(xsltStr);

        return xslt.xslTransformToString(xml);
    }

    /**
     * Transform a XML document to a String using a Stylesheet stored in a file.
     *
     * @param   xml           The xml document
     * @param   filenameXSLT  The filename of the XSLT. Relative files are relative to the Cordys
     *                        install directory.
     *
     * @return  the transformed String
     *
     * @throws  Exception
     */
    public static String xslStrTransformByFile(int xml, String filenameXSLT)
                                        throws Exception
    {
        int node = Node.unlink(Node.getFirstElement(xml));
        String result = _xslStrTransformByFile(node, filenameXSLT);
        Node.appendToChildren(node, xml);
        return result;
    }

    /**
     * DOCUMENTME.
     *
     * @param   xml           DOCUMENTME
     * @param   filenameXSLT  DOCUMENTME
     *
     * @return  DOCUMENTME
     *
     * @throws  Exception  DOCUMENTME
     */
    public static String _xslStrTransformByFile(int xml, String filenameXSLT)
                                         throws Exception
    {
        XSLT xslt = getXsltFromFile(filenameXSLT);
        return xslt.xslTransformToString(xml);
    }

    /**
     * Transform a XML document using a Stylesheet to an XML.
     *
     * @param   xml    The Xml
     * @param   aXSLT  The xslt
     *
     * @return  transformed Xml
     */
    public static int xslTransform(int xml, int aXSLT)
    {
        int xmlNode = Node.unlink(Node.getFirstElement(xml));
        int xsltNode = Node.unlink(Node.getFirstElement(aXSLT));

        int resultNode = _xslTransform(xmlNode, xsltNode);

        Node.appendToChildren(xmlNode, xml);
        Node.appendToChildren(xsltNode, aXSLT);

        return resultNode;
    }

    /**
     * DOCUMENTME.
     *
     * @param   xml    DOCUMENTME
     * @param   aXSLT  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    public static int _xslTransform(int xml, int aXSLT)
    {
        String xsltStr = Node.writeToString(aXSLT, false);
        XSLT xslt = XSLT.parseFromString(xsltStr);
        int resultNode = xslt.xslTransform(xml);

        return resultNode;
    }

    /**
     * Transform a XML document using a Stylesheet stored in a file.
     *
     * @param   xml           The xml document
     * @param   filenameXSLT  The filename of the XSLT. Relative files are relative to the Cordys
     *                        install directory.
     *
     * @return  the transformed Xml
     *
     * @throws  Exception
     */
    public static int xslTransformByFile(int xml, String filenameXSLT)
                                  throws Exception
    {
        int node = Node.unlink(Node.getFirstElement(xml));
        int resultNode = _xslTransformByFile(node, filenameXSLT);
        Node.appendToChildren(node, xml);
        return resultNode;
    }

    /**
     * DOCUMENTME.
     *
     * @param   xml           DOCUMENTME
     * @param   filenameXSLT  DOCUMENTME
     *
     * @return  DOCUMENTME
     *
     * @throws  Exception  DOCUMENTME
     */
    public static int _xslTransformByFile(int xml, String filenameXSLT)
                                   throws Exception
    {
        XSLT xslt = getXsltFromFile(filenameXSLT);
        return xslt.xslTransform(xml);
    }

    /**
     * Format a string using the java sprintf formating style. Arguments are a list of XPaths in
     * xml. Only string formatting is supported (%$s).
     *
     * <p>Example of a Business Process expression:</p>
     *
     * <pre>
        &lt;xml&gt;
            &lt;persoon&gt;
                &lt;name&gt;piet&lt;/name&gt;
            &lt;/persoon&gt;
            &lt;persoon&gt;
                &lt;name&gt;jan&lt;/name&gt;
            &lt;/persoon&gt;
        &lt;/xml&gt;

        &lt;xpaths&gt;
            &lt;xpath&gt;/xml/persoon[1]/name/text()&lt;/xpath&gt;
            &lt;xpath&gt;/xml/persoon[2]/name/text()&lt;/xpath&gt;
        &lt;/xpaths&gt;

        com.cordys.coe.util.bpm.XmlUtils::printf(/xml,"Hi %1$s %2$s", /xpaths)
     * </pre>
     *
     * @param   xml     the xml node
     * @param   format  A format string as described in Format string syntax of the java Formatting
     *                  class
     * @param   xpaths  the xpaths to the argument values
     *
     * @return  string the result
     *
     * @throws  SecurityException          DOCUMENTME
     * @throws  NoSuchMethodException      DOCUMENTME
     * @throws  IllegalArgumentException   DOCUMENTME
     * @throws  IllegalAccessException     DOCUMENTME
     * @throws  InvocationTargetException  DOCUMENTME
     */
    public static String printf(int xml, String format, int xpaths)
                         throws SecurityException, NoSuchMethodException, IllegalArgumentException,
                                IllegalAccessException, InvocationTargetException
    {
        int[] xpathNodes = XPathHelper.selectNodes(xpaths, "./*[local-name()='xpath']");
        Object[] xpathResultArr = new Object[xpathNodes.length];

        for (int i = 0; i < xpathNodes.length; i++)
        {
            String xpathResult = Node.getDataWithDefault(xpathNodes[i], "");
            String value = _evaluate(xml, xpathResult);
            xpathResultArr[i] = value;
        }

        Class<?>[] types = new Class[2];
        types[0] = String.class;
        types[1] = Object[].class;

        Method m = String.class.getMethod("format", (Class[]) types);

        Object[] args = new Object[2];
        args[0] = format;
        args[1] = xpathResultArr;

        return (String) m.invoke(null, args);
    }

    /**
     * DOCUMENTME.
     *
     * @param   xpathType  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    private static String getXPathType(int xpathType)
    {
        if (xpathType == com.eibus.xml.xpath.XPathResult.XPATH_BOOLEAN)
        {
            return "boolean";
        }

        if (xpathType == com.eibus.xml.xpath.XPathResult.XPATH_CUSTOM)
        {
            return "custom";
        }

        if (xpathType == com.eibus.xml.xpath.XPathResult.XPATH_INVALID)
        {
            return "invalid";
        }

        if (xpathType == com.eibus.xml.xpath.XPathResult.XPATH_NODESET)
        {
            return "nodeset";
        }

        if (xpathType == com.eibus.xml.xpath.XPathResult.XPATH_NUMBER)
        {
            return "number";
        }

        if (xpathType == com.eibus.xml.xpath.XPathResult.XPATH_STRING)
        {
            return "string";
        }

        return "";
    }

    /**
     * DOCUMENTME.
     *
     * @param   filenameXSLT  DOCUMENTME
     *
     * @return  DOCUMENTME
     *
     * @throws  Exception  DOCUMENTME
     */
    private static XSLT getXsltFromFile(String filenameXSLT)
                                 throws Exception
    {
        String absFileNameXslt = FileUtils.getAbsoluteFileName(filenameXSLT,
                                                               EIBProperties.getInstallDir());
        XSLT xslt = null;

        try
        {
            xslt = XSLT.parseFromFile(absFileNameXslt);
        }
        catch (Exception e)
        {
            throw new Exception("Error loading XSLT from file '" + absFileNameXslt + "'", e);
        }

        if (xslt == null)
        {
            throw new Exception("Unable to parse XSLT '" + filenameXSLT + "'");
        }

        return xslt;
    }

    /**
     * Sorts nodes pointed by the XPath using the key XPath which is relative to each selected node.
     * Note that the node XPath must select nodes at the same level!.
     *
     * @param  root      Root node.
     * @param  elemPath  XPath to select the nodes to be sorted.
     * @param  keyPath   XPath to get the node sort key.
     * @param  pathInfo  Extra information for the XPath, e.g. namespace prefixes.
     */
    public static void sortNodes(int root, XPath elemPath, final XPath keyPath,
                                 final XPathMetaInfo pathInfo)
    {
        Comparator<Integer> comparator = new Comparator<Integer>()
        {
            public int compare(Integer n1, Integer n2)
            {
                if ((n1 == null) || (n2 == null))
                {
                    throw new NullPointerException("One of the nodes is not set.");
                }

                try
                {
                    String v1 = XPathHelper.getStringValue(n1, keyPath, pathInfo, false);
                    String v2 = XPathHelper.getStringValue(n2, keyPath, pathInfo, false);

                    if (v1 == null)
                    {
                        return (v2 != null) ? -1 : 0;
                    }
                    else if (v2 == null)
                    {
                        return (v1 != null) ? 1 : 0;
                    }

                    return v1.compareTo(v2);
                }
                catch (Exception e)
                {
                    throw new IllegalArgumentException("XPath operation failed.", e);
                }
            }
        };

        sortNodes(root, elemPath, pathInfo, comparator);
    }

    /**
     * Sorts nodes pointed by the XPath using the given comparator. Note that the XPath must select
     * nodes at the same level!.
     *
     * @param  root        Root node.
     * @param  elemPath    XPath to select the nodes to be sorted.
     * @param  pathInfo    Extra information for the XPath, e.g. namespace prefixes.
     * @param  comparator  Comparator used for the sorting.
     */
    public static void sortNodes(int root, XPath elemPath, XPathMetaInfo pathInfo,
                                 Comparator<Integer> comparator)
    {
        NodeSet nodeSet = elemPath.selectNodeSet(root, pathInfo);
        int[] nodeArray = nodeSet.getElementNodes();

        if (nodeArray.length == 0)
        {
            return;
        }

        List<Integer> nodeList = new ArrayList<Integer>(nodeArray.length);
        int parentNode = 0;

        for (int n : nodeArray)
        {
            if (parentNode == 0)
            {
                parentNode = Node.getParent(n);
            }

            n = Node.unlink(n);
            nodeList.add(n);
        }

        if (parentNode == 0)
        {
            throw new IllegalStateException("Parent node not set.");
        }

        Collections.sort(nodeList, comparator);

        for (Integer n : nodeList)
        {
            Node.appendToChildren(n, parentNode);
        }
    }

    /**
     * Removes all namespace declarations from this node.
     *
     * @param  node  XML node.
     */
    public static void removeNamespaceDeclarations(int node)
    {
        for (int i = 1, count = Node.getNumChildren(node); i <= count; i++)
        {
            String name = Node.getAttributeName(node, i);

            if (name.startsWith("xmlns"))
            {
                if ((name.length() == 5) || ((name.length() > 5) && (name.charAt(5) == ':')))
                {
                    Node.removeAttribute(node, name);
                    i--;
                }
            }
        }
    }

    /**
     * Removes the given namespace declarations from this node.
     *
     * @param  node           XML node.
     * @param  namespaceUris  All declaration of these URI's will be removed.
     */
    public static void removeNamespaceDeclarations(int node, String[] namespaceUris)
    {
        for (int i = 1, count = Node.getNumChildren(node); i <= count; i++)
        {
            String name = Node.getAttributeName(node, i);

            if (name.startsWith("xmlns"))
            {
                if ((name.length() == 5) || ((name.length() > 5) && (name.charAt(5) == ':')))
                {
                    boolean remove = false;

                    if (namespaceUris != null)
                    {
                        for (String uri : namespaceUris)
                        {
                            String nodeUri = Node.getAttribute(node, name);

                            if ((uri != null) && uri.equals(nodeUri))
                            {
                                remove = true;
                            }
                        }
                    }
                    else
                    {
                        remove = true;
                    }

                    if (remove)
                    {
                        Node.removeAttribute(node, name);
                        i--;
                    }
                }
            }
        }
    }

    /**
     * DOCUMENTME.
     *
     * @param   args  DOCUMENTME
     *
     * @throws  Exception  DOCUMENTME
     */
    public static void main(String[] args)
                     throws Exception
    {
        Document doc = new Document();

        int xmlNode = doc.parseString("<xml><persoon><name>piet</name></persoon><persoon><name>jan</name></persoon></xml>");
        /*NodeObject xmlNode = new NodeObject(node);
         *
         * int xpathNode =
         * doc.parseString("<xpaths><xpath>/xml/persoon[1]/name/text()</xpath><xpath>/xml/persoon[2]/name/text()</xpath></xpaths>");
         * //NodeObject xpathNode = new NodeObject(node);
         *
         * String out = printf(xmlNode, "hi %1$s %2$s", xpathNode);System.out.print(out);*/
        // int xmlNode = doc.parseString("<xml/>");
        // int node =
        // xsdValidateByFile("C:\\data\\Projects\\CordysDuplo\\Word\\PolicyRequestCommon.xsd",
        // xmlNode);
        // System.out.print(Node.writeToString(node,true));

        // int node =
        // XSDUtils.xsdValidateByFile("C:\\data\\Projects\\CordysDuplo\\Word\\PolicyRequestCommon.xsd",
        // xmlNode);
        int xx = _evaluateToXml(xmlNode, "/xml/persoon");
        System.out.print(Node.writeToString(xx, true));
    }
}
