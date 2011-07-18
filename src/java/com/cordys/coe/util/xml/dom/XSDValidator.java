package com.cordys.coe.util.xml.dom;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.xerces.parsers.DOMParser;

import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;

/**
 * Validate an XML to a Orchestrator template (XSD). TODO: implement a pool of validators.
 *
 * @author  hvdvliert
 */
public class XSDValidator
    implements ErrorHandler
{
    /**
     * DOCUMENTME.
     */
    private static final int ERROR = 1;
    /**
     * DOCUMENTME.
     */
    private static final int FATAL = 2;
    /**
     * DOCUMENTME.
     */
    private static final int WARNING = 3;
    /**
     * DOCUMENTME.
     */
    private static XSDValidator oValidateUtil = null;
    /**
     * DOCUMENTME.
     */
    private DOMParser parser;

    /**
     * DOCUMENTME.
     */
    private XSDValidatorResult validationResult = null;

    /**
     * Creates a new XSDValidator object.
     *
     * @throws  SAXNotRecognizedException  DOCUMENTME
     * @throws  SAXNotSupportedException   DOCUMENTME
     */
    private XSDValidator()
                  throws SAXNotRecognizedException, SAXNotSupportedException
    {
        parser = new DOMParser();
        parser.setEntityResolver(new CPCResolver());
        parser.setFeature("http://xml.org/sax/features/validation", true);
        parser.setFeature("http://apache.org/xml/features/validation/schema", true);
        parser.setErrorHandler(this);
    }

    /**
     * Returns an instance of the ValidateXml object.
     *
     * @return  DOCUMENTME
     *
     * @throws  SAXNotRecognizedException  DOCUMENTME
     * @throws  SAXNotSupportedException   DOCUMENTME
     */
    public static XSDValidator getInstance()
                                    throws SAXNotRecognizedException, SAXNotSupportedException
    {
        if (oValidateUtil == null)
        {
            oValidateUtil = new XSDValidator();
        }
        return oValidateUtil;
    }

    /**
     * Add a error to the validationMessages XML returned by method <code>isValid</code>.
     *
     * @param   ex  DOCUMENTME
     *
     * @throws  SAXException  The exception with the found error
     */
    public void error(SAXParseException ex)
               throws SAXException
    {
        validationResult.setValid(false);
        addMessageToValidationResult(ERROR, ex.getMessage());
    }

    /**
     * Add a fatal error to the validationMessages XML returned by method <code>isValid</code>.
     *
     * @param   arg0  DOCUMENTME
     *
     * @throws  SAXException  The exception with the found error
     */
    public void fatalError(SAXParseException arg0)
                    throws SAXException
    {
        validationResult.setValid(false);
        addMessageToValidationResult(FATAL, arg0.getMessage());
    }

    /**
     * Validate a XML to a schema.
     *
     * <p>Example of the validation message output:</p>
     *
     * <pre>
        <error>Datatype error: In element 'id' : '' is not a decimal.</error>
        <error>Datatype error: In element 'companyCode' : '' is not a decimal.</error>
        <error>Datatype error: In element 'timeStamp' : '' is not a decimal.</error>
        <error>Datatype error: In element 'inReplyToMessageId' : '' is not a decimal.</error>
        <error>Datatype error: In element 'id' : '' is not a decimal.</error>
        <error>Datatype error: In element 'nr' : '' is not a decimal.</error>
     *</pre>
     *
     * @param   stXml     The XML that must be validated
     * @param   stSchema  The schema to validate against
     *
     * @return  XSDValidatorResult validation result
     *
     * @throws  SAXException  DOCUMENTME
     * @throws  IOException   DOCUMENTME
     */
    public synchronized XSDValidatorResult isValid(String stXml, String stSchema)
                                            throws SAXException, IOException
    {
        validationResult = new XSDValidatorResult();
        validationResult.setValid(true);

        parser.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",
                           stSchema);

        StringReader oStringReader = new StringReader(stXml);
        InputSource oInputSource = new InputSource(oStringReader);
        parser.parse(oInputSource);

        return validationResult;
    }

    /**
     * Add a warning to the validationMessages XML returned by method <code>isValid</code>.
     *
     * @param   ex  DOCUMENTME
     *
     * @throws  SAXException  The exception with the found error
     */
    public void warning(SAXParseException ex)
                 throws SAXException
    {
        validationResult.setValid(false);
        addMessageToValidationResult(WARNING, ex.getMessage());
    }

    /**
     * DOCUMENTME.
     *
     * @param  messageType  DOCUMENTME
     * @param  message      DOCUMENTME
     */
    private void addMessageToValidationResult(int messageType, String message)
    {
        String sMessageType = "";

        if (messageType == WARNING)
        {
            sMessageType = "warning";
        }
        else if (messageType == ERROR)
        {
            sMessageType = "error";
        }
        else if (messageType == FATAL)
        {
            sMessageType = "fatal";
        }

        validationResult.addMessage(sMessageType + ": " + message);
    }
}

/**
 * DOCUMENTME.
 *
 * @author  $author$
 */
class CPCResolver
    implements EntityResolver
{
    /**
     * DOCUMENTME.
     *
     * @param   publicId  DOCUMENTME
     * @param   systemId  DOCUMENTME
     *
     * @return  DOCUMENTME
     *
     * @throws  IOException   DOCUMENTME
     * @throws  SAXException  DOCUMENTME
     */
    public InputSource resolveEntity(String publicId, String systemId)
                              throws IOException, SAXException
    {
        StringWriter stringOut = new StringWriter();
        // create your string representation of the XML file
        stringOut.write(systemId);

        StringReader strReader = new StringReader(stringOut.toString());
        return new InputSource(strReader);
    }
}
