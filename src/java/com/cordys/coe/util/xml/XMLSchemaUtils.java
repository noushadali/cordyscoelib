package com.cordys.coe.util.xml;

import com.cordys.coe.util.general.Util;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Node;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;

import java.io.File;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class contains methods that can work on XML documents and schemas. For example there is a method that can validate a piece
 * of XML against a schema.
 * 
 * @author pgussow
 */
public class XMLSchemaUtils
{
    /**
     * Identifies the code for missing XML source file.
     */
    private static final int CODE_MISSING_XML_SOURCE_FILE = 1;
    /**
     * Identifies the message for missing XML source file.
     */
    private static final String MESS_MISSING_XML_SOURCE_FILE = "The XML source file is not specified or does not exist.";
    /**
     * Identifies the code for missing XSD file.
     */
    private static final int CODE_MISSING_XSD_FILE = 2;
    /**
     * Identifies the message for missing XSD file.
     */
    private static final String MESS_MISSING_XSD_FILE = "The XSD file is not specified or does not exist.";
    /**
     * Identifies a general exception during execution.
     */
    private static final int CODE_GENERAL_EXCEPTION = 99;
    /**
     * Identifies that everything went ok and that the XML is valid against the schema.
     */
    private static final int CODE_OK = 0;
    /**
     * Identifies a validation error.
     */
    private static final int CODE_VALIDATION_ERROR = 3;

    /**
     * Main method. For testing purposes.
     * 
     * @param saArgs the commandline arguments.
     */
    public static void main(String[] saArgs)
    {
        int iReturn = XMLSchemaUtils.validateXMLSchema("c:/temp/XMPInputFileErr.xml", "c:/temp/$pain.001.003.01.xsd", true);
        System.out.println(Node.writeToString(iReturn, true));
    }

    /**
     * This method will validate an external XML file against its XSD schema. The returning XML looks like this:
     * 
     * <pre>
     *        <result>
     *          <code></code>
     *          <errors>
     *            <error>
     *              <line></line>
     *              <column></column>
     *              <message></message>
     *             </error>
     *          </errors>
     *        </result>
     * </pre>
     * 
     * @param sXMLFileName Name plus path of the XML file that will be validated.
     * @param sXSDFileName Name plus path of the XSD schema used to validate the XML file against
     * @param bNamespaceInDocument If true it means that the location of the XSD is specified in the XML document that has to be
     *            validated. If false then sXSDFilename must contain the location of the XSD to validate against.
     * @return This method returns a piece of XML.
     */
    public static int validateXMLSchema(String sXMLFileName, String sXSDFileName, boolean bNamespaceInDocument)
    {
        int iReturn = 0;
        Document dDoc = new Document();
        iReturn = dDoc.createElement("result");

        File fFile = new File(sXMLFileName);
        File fXSD = null;

        if ((sXSDFileName != null) && (sXSDFileName.length() > 0))
        {
            fXSD = new File(sXSDFileName);
        }

        if ((sXMLFileName == null) || (sXMLFileName.length() == 0) || !fFile.exists())
        {
            createResult(iReturn, CODE_MISSING_XML_SOURCE_FILE, MESS_MISSING_XML_SOURCE_FILE);
        }
        else if (!bNamespaceInDocument
                && ((sXSDFileName == null) || (sXSDFileName.length() == 0) || (fXSD == null) || !fXSD.exists()))
        {
            createResult(iReturn, CODE_MISSING_XSD_FILE, MESS_MISSING_XSD_FILE);
        }
        else
        {
            ErrorChecker errors;

            try
            {
                DOMParser parser = new DOMParser();
                parser.setFeature("http://xml.org/sax/features/validation", true);
                parser.setFeature("http://xml.org/sax/features/namespaces", true);
                parser.setFeature("http://apache.org/xml/features/validation/schema", true);
                parser.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);

                if (!bNamespaceInDocument)
                {
                    parser.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", sXSDFileName);
                }

                errors = new ErrorChecker();
                parser.setErrorHandler(errors);
                parser.parse(sXMLFileName);

                // Now check if the validation went OK.
                ArrayList<ParseError> alErrors = errors.getAllErrors();

                if (alErrors.size() == 0)
                {
                    // Everything went ok and the schema is valid
                    createResult(iReturn, CODE_OK, null);
                }
                else
                {
                    // XML document is invalid. Now create a list of all the errors.
                    dDoc.createTextElement("code", String.valueOf(CODE_VALIDATION_ERROR), iReturn);

                    int iErrorsNode = dDoc.createElement("errors", iReturn);

                    for (Iterator<ParseError> iErrors = alErrors.iterator(); iErrors.hasNext();)
                    {
                        ParseError peError = (ParseError) iErrors.next();
                        peError.toXML(iErrorsNode);
                    }
                }
            }
            catch (Exception e)
            {
                createResult(iReturn, CODE_GENERAL_EXCEPTION, "Error validating the XML:\n" + Util.getStackTrace(e));
            }
        }

        return iReturn;
    }

    /**
     * This method creates a non-line related error.
     * 
     * @param iParent The parent XML node.
     * @param iCode The resultcode.
     * @param sMessage The detailed message.
     */
    private static void createResult(int iParent, int iCode, String sMessage)
    {
        Document dDoc = Node.getDocument(iParent);
        dDoc.createTextElement("code", String.valueOf(iCode), iParent);

        if ((sMessage != null) && (sMessage.length() > 0))
        {
            int iErrors = dDoc.createElement("errors", iParent);
            int iErrorNode = dDoc.createElement("error", iErrors);
            dDoc.createTextElement("message", sMessage, iErrorNode);
        }
    }
}
