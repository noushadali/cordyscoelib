package com.cordys.coe.util.exceptions;

import com.cordys.coe.util.cgc.CordysSOAPException;

import java.io.IOException;

import org.apache.commons.httpclient.HttpException;

/**
 * This class is used by the XML to channel the exceptions. The different errorcodes are defined
 * within this class.
 *
 * @author  pgussow
 */
public class CommunicationException extends AbstractCoEException
{
    /**
     * The prefix for the errorcode.
     */
    private static final String EC_PREFIX = "CE_";
    /**
     * The errorcode for a general exceptions.
     */
    public static final String EC_UNKNOWN = EC_PREFIX + "00001";
    /**
     * The errorcode for an exception that indicates a error in the http communication.
     */
    public static final String EC_HTTP = EC_PREFIX + "00002";
    /**
     * The errorcode for an exception that indicates a error during mapping the XML.
     */
    public static final String EC_XML = EC_PREFIX + "00003";
    /**
     * The errorcode for an exception on Cordys SOAP.
     */
    public static final String EC_SOAP = EC_PREFIX + "00004";
    /**
     * The errorcode for any configuration exceptions.
     */
    public static final String EC_CONFIGURATION = EC_PREFIX + "00005";

    /**
     * Creates a new AbstractFTAException object.
     *
     * @param  tCause  The exception that caused this exception.
     */
    public CommunicationException(Throwable tCause)
    {
        super(convertException2ErrorCode(tCause), tCause);
    }

    /**
     * Creates a new AbstractFTAException object.
     *
     * @param  asMessage  DOCUMENTME
     * @param  tCause     The exception that caused this exception.
     */
    public CommunicationException(String asMessage, Throwable tCause)
    {
        super(convertException2ErrorCode(tCause), asMessage, tCause);
    }

    /**
     * Creates a new CommunicationException object.
     *
     * @param  sErrorCode     The errorcode for this exception.
     * @param  sShortMessage  A short message for this exception.
     */
    public CommunicationException(String sErrorCode, String sShortMessage)
    {
        super(sErrorCode, sShortMessage);
    }

    /**
     * Creates a new CommunicationException object.
     *
     * @param  sErrorCode     The errorcode for this exception.
     * @param  sShortMessage  A short message for this exception.
     * @param  tCause         The exception that caused this exception.
     */
    public CommunicationException(String sErrorCode, String sShortMessage, Throwable tCause)
    {
        super(sErrorCode, sShortMessage, tCause);
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

        if ((aoCause instanceof HttpException) || (aoCause instanceof IOException))
        {
            sErrorCode = EC_HTTP;
        }
        else if (aoCause instanceof XMLWrapperException)
        {
            sErrorCode = EC_XML;
        }
        else if (aoCause instanceof CordysSOAPException)
        {
            sErrorCode = EC_SOAP;
        }
        return sErrorCode;
    }
}
