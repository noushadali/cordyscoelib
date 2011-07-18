package com.cordys.coe.util.exceptions;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

/**
 * This class is used by the XML to channel the exceptions. The different errorcodes are defined
 * within this class.
 *
 * @author  pgussow
 */
public class XMLWrapperException extends AbstractCoEException
{
    /**
     * The prefix for the errorcode.
     */
    private static final String EC_PREFIX = "XWE_";
    /**
     * The errorcode for a general exceptions.
     */
    public static final String EC_UNKNOWN = EC_PREFIX + "00001";
    /**
     * The errorcode for an exception that indicates a error in the configuration of the parser.
     */
    public static final String EC_PARSER_CONFIGURATION = EC_PREFIX + "00002";
    /**
     * The errorcode for when there was an exception during internal IO.
     */
    public static final String EC_IO = EC_PREFIX + "00003";
    /**
     * The errorcode for an exception that indicates a error in SAX.
     */
    public static final String EC_SAX = EC_PREFIX + "00004";
    /**
     * The errorcode for an exception that indicates a error during transforming the xml.
     */
    public static final String EC_TRANSFORMER = EC_PREFIX + "00005";

    /**
     * Creates a new AbstractFTAException object.
     *
     * @param  tCause  The exception that caused this exception.
     */
    public XMLWrapperException(Throwable tCause)
    {
        super(convertException2ErrorCode(tCause), tCause);
    }

    /**
     * DOCUMENTME.
     *
     * @param   aoCause  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    private static String convertException2ErrorCode(Throwable aoCause)
    {
        String sErrorCode = EC_UNKNOWN;

        if (aoCause instanceof ParserConfigurationException)
        {
            sErrorCode = EC_PARSER_CONFIGURATION;
        }
        else if (aoCause instanceof IOException)
        {
            sErrorCode = EC_IO;
        }
        else if (aoCause instanceof SAXException)
        {
            sErrorCode = EC_SAX;
        }
        else if (aoCause instanceof TransformerException)
        {
            sErrorCode = EC_TRANSFORMER;
        }
        return sErrorCode;
    }
}
