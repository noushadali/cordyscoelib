package com.cordys.coe.util.xml.nom;

import com.cordys.coe.util.CoeLibConnector;
import com.cordys.coe.util.FileUtils;
import com.cordys.coe.util.xml.dom.XSDValidator;
import com.cordys.coe.util.xml.dom.XSDValidatorResult;

import com.eibus.connector.nom.Connector;

import com.eibus.util.system.EIBProperties;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Find;
import com.eibus.xml.nom.Node;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.ArrayList;

/**
 * Provides methods for XSD validation.
 *
 * <p>Example of validationResult:</p>
 *
 * <pre>
   &lt;validationResult
        validated="false"&gt;
        &lt;message&gt;error: Element type &quot;to2&quot; must be declared.&lt;/message&gt;
        &lt;message&gt;error: The content of element type &quot;note&quot; must match &quot;(to,from,heading,body)&quot;.&lt;/message&gt;
    &lt;/validationResult&gt;
 * </pre>
 *
 * <p>TODO: implement a pool of validators in XSDValidator.</p>
 *
 * @author  hvdvlier
 */
public class XSDUtils
{
    /**
     * DOCUMENTME.
     */
    private static final Document DOC = new Document();

    /**
     * DOCUMENTME.
     *
     * @param   filename       DOCUMENTME
     * @param   xmlToValidate  DOCUMENTME
     *
     * @return  DOCUMENTME
     *
     * @throws  Exception  DOCUMENTME
     */
    public static int _xsdValidateByFile(String filename, int xmlToValidate)
                                  throws Exception
    {
        String xmlStr = Node.writeToString(xmlToValidate, false);
        Document doc = Node.getDocument(xmlToValidate);

        String expandedFilename = FileUtils.getAbsoluteFileName(filename,
                                                                EIBProperties.getInstallDir());
        String xsd = readFile(expandedFilename);

        return xsdValidate(doc, xsd, xmlStr);
    }

    /**
     * DOCUMENTME.
     *
     * @param   templateName   DOCUMENTME
     * @param   userDn         DOCUMENTME
     * @param   xmlToValidate  DOCUMENTME
     * @param   version        DOCUMENTME
     *
     * @return  DOCUMENTME
     *
     * @throws  Exception  DOCUMENTME
     */
    public static int _xsdValidateByTemplate(String templateName, String userDn, int xmlToValidate,
                                             String version)
                                      throws Exception
    {
        int resultNode = 0;
        // int xml = Node.getFirstElement(xmlToValidate);
        String xmlStr = Node.writeToString(xmlToValidate, false);
        Document doc = Node.getDocument(xmlToValidate);

        Connector connector = CoeLibConnector.getInstance();
        int templateNode = 0;

        try
        {
            templateNode = com.cordys.coe.util.cpc.Template.get(connector, userDn, version,
                                                                templateName);

            String xsd = Node.writeToString(templateNode, false);
            resultNode = xsdValidate(doc, xsd, xmlStr);
        }
        finally
        {
            Node.delete(templateNode);
        }

        return resultNode;
    }

    /**
     * DOCUMENTME.
     *
     * @param   templateName        DOCUMENTME
     * @param   instanceProperties  DOCUMENTME
     * @param   xmlToValidate       DOCUMENTME
     * @param   version             DOCUMENTME
     *
     * @return  DOCUMENTME
     *
     * @throws  Exception  DOCUMENTME
     */
    public static int _xsdValidateByTemplate(String templateName, int instanceProperties,
                                             int xmlToValidate, String version)
                                      throws Exception
    {
        int instancePropNode = instanceProperties;
        int iUserDn = Find.firstMatch(instancePropNode, "<instanceProperties><currentOwner>");
        String userDn = Node.getDataWithDefault(iUserDn, "");

        int result = _xsdValidateByTemplate(templateName, userDn, xmlToValidate, version);

        return result;
    }

    /**
     * Validate a XML to a XSD.
     *
     * @param   xsd            The XSD
     * @param   xmlToValidate  The Xml to validate
     *
     * @return  Xml ValidationResult xml structure
     *
     * @throws  Exception  Exception
     */
    public static int xsdValidate(String xsd, String xmlToValidate)
                           throws Exception
    {
        return xsdValidate(null, xsd, xmlToValidate);
    }

    /**
     * Validate a XML to a XSD.
     *
     * @param   aDoc           The document node
     * @param   xsd            The XSD
     * @param   xmlToValidate  The Xml to validate
     *
     * @return  Xml ValidationResult xml structure
     *
     * @throws  Exception  Exception
     */
    public static int xsdValidate(Document aDoc, String xsd, String xmlToValidate)
                           throws Exception
    {
        int resultNode = 0;
        Document doc = aDoc;

        if (doc == null)
        {
            doc = DOC;
        }

        try
        {
            XSDValidator validator = XSDValidator.getInstance();
            XSDValidatorResult result = validator.isValid(xmlToValidate, xsd);

            resultNode = doc.createElement("validationResult");
            createResultXml(result, resultNode);
        }
        catch (Exception e)
        {
            throw new Exception("Error validating XSD", e);
        }

        return resultNode;
    }

    /**
     * Validate a XML to a XSD that is stored in a file.
     *
     * @param   filename       The filename of the XSD, relative filenames are relative to cordys
     *                         install dir
     * @param   xmlToValidate  The Xml node
     *
     * @return  Xml ValidationResult xml structure
     *
     * @throws  Exception  Exception
     */
    public static int xsdValidateByFile(String filename, int xmlToValidate)
                                 throws Exception
    {
        int xml = Node.getFirstElement(xmlToValidate);
        return _xsdValidateByFile(filename, xml);
    }

    /**
     * Validate a XML to a CoBOC template (XSD-schema).
     *
     * @param   templateName   The CoBOC template name
     * @param   userDn         The DN of the use for fetching the template
     * @param   xmlToValidate  The XML node
     *
     * @return  Xml validationResult xml structure
     *
     * @throws  Exception  Exception
     */
    public static int xsdValidateByTemplate(String templateName, String userDn, int xmlToValidate)
                                     throws Exception
    {
        return xsdValidateByTemplate(templateName, userDn, xmlToValidate, null);
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
    public static int xsdValidateByTemplate(String templateName, int instanceProperties,
                                            int xmlToValidate)
                                     throws Exception
    {
        return xsdValidateByTemplate(templateName, instanceProperties, xmlToValidate, null);
    }

    /**
     * Validate a XML to a CoBOC template (XSD-schema).
     *
     * @param   templateName   The CoBOC template name
     * @param   userDn         The DN of the use for fetching the template
     * @param   xmlToValidate  The XML node
     * @param   version        The version of the template (organization, isv, user or null)
     *
     * @return  Xml validationResult xml structure
     *
     * @throws  Exception  Exception
     */
    public static int xsdValidateByTemplate(String templateName, String userDn, int xmlToValidate,
                                            String version)
                                     throws Exception
    {
        int xml = Node.getFirstElement(xmlToValidate);
        return _xsdValidateByTemplate(templateName, userDn, xml, version);
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
    public static int xsdValidateByTemplate(String templateName, int instanceProperties,
                                            int xmlToValidate, String version)
                                     throws Exception
    {
        int xml = Node.getFirstElement(xmlToValidate);
        int result = _xsdValidateByTemplate(templateName, instanceProperties, xml, version);

        return result;
    }

    /**
     * DOCUMENTME.
     *
     * @param  result      DOCUMENTME
     * @param  resultNode  DOCUMENTME
     */
    private static void createResultXml(XSDValidatorResult result, int resultNode)
    {
        Node.setAttribute(resultNode, "validated", String.valueOf(result.isValid()));

        ArrayList<String> messages = result.getMessages();

        for (int i = 0; i < messages.size(); i++)
        {
            Node.createTextElement("message", (String) messages.get(i), resultNode);
        }
    }

    /**
     * DOCUMENTME.
     *
     * @param   fileName  DOCUMENTME
     *
     * @return  DOCUMENTME
     *
     * @throws  IOException  DOCUMENTME
     */
    private static String readFile(String fileName)
                            throws IOException
    {
        byte[] cont = null;
        File file = new File(fileName);
        FileInputStream fi = new FileInputStream(file);

        try
        {
            long len = file.length();
            cont = new byte[(int) len];
            fi.read(cont);
        }
        finally
        {
            fi.close();
        }

        return new String(cont, "UTF-8");
    }
}
