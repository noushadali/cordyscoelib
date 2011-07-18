package com.cordys.coe.util.bpm;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Node;
import com.eibus.xml.nom.NodeObject;
import com.eibus.xml.nom.XMLException;

import java.io.UnsupportedEncodingException;

import java.lang.reflect.InvocationTargetException;

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
     * <p>-</p>
     *
     * @param   xml    the xml node
     * @param   xpath  the xpath expression
     *
     * @return  string the result
     */
    public static String evaluate(NodeObject xml, String xpath)
    {
        return com.cordys.coe.util.xml.nom.XmlUtils._evaluate(xml.getNode(), xpath);
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
     * @return  NodeObject the result xml
     */
    public static NodeObject evaluateToXml(NodeObject xml, String xpath)
    {
        int resultNode = com.cordys.coe.util.xml.nom.XmlUtils._evaluateToXml(xml.getNode(), xpath);

        Document doc = Node.getDocument(xml.getNode());
        int returnNode = doc.createElement("evaluateToXml");
        Node.appendToChildren(resultNode, returnNode);

        return new NodeObject(returnNode);
    }

    /**
     * DOCUMENTME.
     *
     * @param   args  DOCUMENTME
     *
     * @throws  SecurityException             DOCUMENTME
     * @throws  IllegalArgumentException      DOCUMENTME
     * @throws  NoSuchMethodException         DOCUMENTME
     * @throws  IllegalAccessException        DOCUMENTME
     * @throws  InvocationTargetException     DOCUMENTME
     * @throws  UnsupportedEncodingException  DOCUMENTME
     * @throws  XMLException                  DOCUMENTME
     */
    public static void main(String[] args)
                     throws SecurityException, IllegalArgumentException, NoSuchMethodException,
                            IllegalAccessException, InvocationTargetException,
                            UnsupportedEncodingException, XMLException
    {
        Document doc = new Document();
        int node = doc.parseString("<xml><persoon><name>piet</name></persoon><persoon><name>jan</name></persoon></xml>");
        NodeObject xmlNode = new NodeObject(node);

        node = doc.parseString("<xpaths><xpath>/xml/persoon[1]/name/text()</xpath><xpath>/xml/persoon[2]/name/text()</xpath></xpaths>");

        NodeObject xpathNode = new NodeObject(node);

        String out = printf(xmlNode, "hi %1$s %2$s", xpathNode);
        System.out.print(out);
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
    public static String printf(NodeObject xml, String format, NodeObject xpaths)
                         throws SecurityException, NoSuchMethodException, IllegalArgumentException,
                                IllegalAccessException, InvocationTargetException
    {
        String result = com.cordys.coe.util.xml.nom.XmlUtils.printf(xml.getNode(), format,
                                                                    xpaths.getNode());
        return result;
    }

    /**
     * Validate a XML to an schema that is stored in a file.
     *
     * <p>Example of a Business Process expression:</p>
     *
     * <pre>
       com.cordys.coe.util.xml.bpm.XmlUtils::xsdValidateByFile("c:\xslt.xslt", /xml/message)
       Result:        &lt;validationResult validated="false"&gt;
                      <error>Datatype error: In element 'id' : '' is not a decimal.</error>
                      <error>Datatype error: In element 'companyCode' : '' is not a decimal.</error>
                      <error>Datatype error: In element 'timeStamp' : '' is not a decimal.</error>
                      <error>Datatype error: In element 'inReplyToMessageId' : '' is not a decimal.</error>
                      <error>Datatype error: In element 'id' : '' is not a decimal.</error>
                      <error>Datatype error: In element 'nr' : '' is not a decimal.</error>
               &lt;validationResult validated="true"&gt; </pre>
     *
     * @param   filename       the filename of the Xslt. Relative files are relative to the Cordys
     *                         install directory.
     * @param   xmlToValidate  the xml to validate
     *
     * @return  a xml with the validation result
     *
     * @throws  Exception
     */
    public static NodeObject xsdValidateByFile(String filename, NodeObject xmlToValidate)
                                        throws Exception
    {
        int xmlToValidateNode = xmlToValidate.getNode();
        int resultNode = com.cordys.coe.util.xml.nom.XSDUtils._xsdValidateByFile(filename,
                                                                                 xmlToValidateNode);

        Document doc = Node.getDocument(xmlToValidateNode);
        int returnNode = doc.createElement("xsdValidateByFile");
        Node.appendToChildren(resultNode, returnNode);

        return new NodeObject(returnNode);
    }

    /**
     * Validate a XML to a CoBOC template (XSD).
     *
     * <p>Example of a Business Process expression:</p>
     *
     * <pre>
       com.cordys.coe.util.bpm.XmlUtils::xsdValidateByTemplate("/xml", /instanceProperties, /xml/message)
       Result:        &lt;validationResult validated="false"&gt;
                      <error>Datatype error: In element 'id' : '' is not a decimal.</error>
                      <error>Datatype error: In element 'companyCode' : '' is not a decimal.</error>
                      <error>Datatype error: In element 'timeStamp' : '' is not a decimal.</error>
                      <error>Datatype error: In element 'inReplyToMessageId' : '' is not a decimal.</error>
                      <error>Datatype error: In element 'id' : '' is not a decimal.</error>
                      <error>Datatype error: In element 'nr' : '' is not a decimal.</error>
               &lt;validationResult validated="true"&gt;
     * </pre>
     *
     * @param   templateName        the CoBOC template name
     * @param   instanceProperties  the instance properties from the messagemap
     *                              (/instanceProperties)
     * @param   xmlToValidate       the Xml to validate
     *
     * @return  a xml with the validation result
     *
     * @throws  Exception
     */
    public static NodeObject xsdValidateByTemplate(String templateName,
                                                   NodeObject instanceProperties,
                                                   NodeObject xmlToValidate)
                                            throws Exception
    {
        int xmlToValidateNode = xmlToValidate.getNode();
        int resultNode = com.cordys.coe.util.xml.nom.XSDUtils._xsdValidateByTemplate(templateName,
                                                                                     instanceProperties
                                                                                     .getNode(),
                                                                                     xmlToValidateNode,
                                                                                     null);

        Document doc = Node.getDocument(xmlToValidateNode);
        int returnNode = doc.createElement("xsdValidateByTemplate");
        Node.appendToChildren(resultNode, returnNode);

        return new NodeObject(returnNode);
    }

    /**
     * Validate a XML to a CoBOC template (XSD).
     *
     * <p>Example of a Business Process expression:</p>
     *
     * <pre>
       com.cordys.coe.util.bpm.XmlUtils::xsdValidateByTemplate("/xml", /instanceProperties, /xml/message, "organization")
       Result:        &lt;validationResult validated="false"&gt;
                      <error>Datatype error: In element 'id' : '' is not a decimal.</error>
                      <error>Datatype error: In element 'companyCode' : '' is not a decimal.</error>
                      <error>Datatype error: In element 'timeStamp' : '' is not a decimal.</error>
                      <error>Datatype error: In element 'inReplyToMessageId' : '' is not a decimal.</error>
                      <error>Datatype error: In element 'id' : '' is not a decimal.</error>
                      <error>Datatype error: In element 'nr' : '' is not a decimal.</error>
               &lt;validationResult validated="true"&gt; </pre>
     *
     * @param   templateName        the CoBOC template name
     * @param   instanceProperties  the instance properties from the messagemap
     *                              (/instanceProperties)
     * @param   xmlToValidate       the Xml to validate
     * @param   version             One of the following values: user, organization, isv
     *
     * @return  a xml with the validation result
     *
     * @throws  Exception
     */
    public static NodeObject xsdValidateByTemplate(String templateName,
                                                   NodeObject instanceProperties,
                                                   NodeObject xmlToValidate, String version)
                                            throws Exception
    {
        int xmlToValidateNode = xmlToValidate.getNode();
        int resultNode = com.cordys.coe.util.xml.nom.XSDUtils._xsdValidateByTemplate(templateName,
                                                                                     instanceProperties
                                                                                     .getNode(),
                                                                                     xmlToValidateNode,
                                                                                     version);

        Document doc = Node.getDocument(xmlToValidateNode);
        int returnNode = doc.createElement("xsdValidateByTemplate");
        Node.appendToChildren(resultNode, returnNode);

        return new NodeObject(returnNode);
    }

    /**
     * Transform a XML document using a Stylesheet to a String.
     *
     * @param   xml    The Xml
     * @param   aXSLT  The xslt
     *
     * @return  transformed String
     */
    public static String xslStrTransform(NodeObject xml, NodeObject aXSLT)
    {
        int aXSLTNode = aXSLT.getNode();

        int xsltNode = Node.unlink(Node.getFirstElement(aXSLTNode));
        String result = com.cordys.coe.util.xml.nom.XmlUtils._xslStrTransform(xml.getNode(),
                                                                              xsltNode);
        Node.appendToChildren(xsltNode, aXSLTNode);

        return result;
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
    public static String xslStrTransformByFile(NodeObject xml, String filenameXSLT)
                                        throws Exception
    {
        String result = com.cordys.coe.util.xml.nom.XmlUtils._xslStrTransformByFile(xml.getNode(),
                                                                                    filenameXSLT);
        return result;
    }

    /**
     * Transform a XML document using a Stylesheet to an XML.
     *
     * @param   xml    The Xml
     * @param   aXSLT  The xslt
     *
     * @return  transformed Xml
     */
    public static NodeObject xslTransform(NodeObject xml, NodeObject aXSLT)
    {
        int aXSLTNode = aXSLT.getNode();

        int xsltNode = Node.unlink(Node.getFirstElement(aXSLTNode));
        int resultNode = com.cordys.coe.util.xml.nom.XmlUtils._xslTransform(xml.getNode(),
                                                                            xsltNode);
        Node.appendToChildren(xsltNode, aXSLTNode);

        Document doc = Node.getDocument(xml.getNode());
        int returnNode = doc.createElement("xslTransform");
        Node.appendToChildren(resultNode, returnNode);

        return new NodeObject(returnNode);
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
    public static NodeObject xslTransformByFile(NodeObject xml, String filenameXSLT)
                                         throws Exception
    {
        int resultNode = com.cordys.coe.util.xml.nom.XmlUtils._xslTransformByFile(xml.getNode(),
                                                                                  filenameXSLT);

        Document doc = Node.getDocument(xml.getNode());
        int returnNode = doc.createElement("xslTransformByFile");
        Node.appendToChildren(resultNode, returnNode);

        return new NodeObject(returnNode);
    }
}
