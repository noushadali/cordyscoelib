package com.cordys.coe.util.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import com.eibus.util.system.EIBProperties;
import com.eibus.xml.nom.Node;

/**
 * This class can be used to validate XML based on the given schema.
 * 
 * @author pgussow
 */
public class ValidateXML
{
    /**
     * This method will validate the XML based on the given schema.
     * 
     * @param xml The XML node to validate.
     * @param xsdLocation The location of the XSD file.
     * @return The errorhandler that holds the status.
     * @throws SAXException In case the SAX parser throws errors.
     * @throws ParserConfigurationException In case the XML parser throws exceptions.
     * @throws IOException In case the file could not be read.
     */
    public static IValidationErrorHandler validate(int xml, String xsdLocation) throws SAXException,
            ParserConfigurationException, IOException
    {
        return validate(xml, xsdLocation, false);
    }

    /**
     * This method will validate the XML based on the given schema.
     * 
     * @param xml The XML node to validate.
     * @param xsdLocation The location of the XSD file.
     * @return The errorhandler that holds the status.
     * @throws SAXException In case the SAX parser throws errors.
     * @throws ParserConfigurationException In case the XML parser throws exceptions.
     * @throws IOException In case the file could not be read.
     */
    public static IValidationErrorHandler validate(int xml, String xsdLocation, boolean throwException) throws SAXException,
            ParserConfigurationException, IOException
    {
        String xmlString = Node.writeToStringWithNSInfo(xml, true);
        File xsdFile = new File(EIBProperties.getInstallDir(), xsdLocation);

        // Set up the schema
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(xsdFile);

        // Set up the XML parser
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setIgnoringComments(true);
        dbf.setSchema(schema);

        // Create the new document builder
        DocumentBuilder db = dbf.newDocumentBuilder();
        IValidationErrorHandler retVal = new LocalErrorHandler(xmlString, xsdLocation);
        db.setErrorHandler(retVal);

        db.parse(new ByteArrayInputStream(xmlString.getBytes()));

        if (!retVal.isValid() && throwException)
        {
            throw new IllegalArgumentException("The XML does not validate:\n" + retVal.toString() + "\n\nOriginal XML:\n"
                    + xmlString);
        }

        return retVal;
    }
}
