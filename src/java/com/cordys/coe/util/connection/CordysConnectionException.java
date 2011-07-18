package com.cordys.coe.util.connection;

import com.cordys.coe.util.exceptions.AbstractCoEException;

/**
 * This exception is used for the ICordysConnection framework.
 *
 * @author  pgussow
 */
public class CordysConnectionException extends AbstractCoEException
{
    /**
     * The prefix for the errorcode.
     */
    private static final String EC_PREFIX = "CME_";
    /**
     * The errorcode for general exceptions.
     */
    public static final String EC_GENERAL = EC_PREFIX + "00001";
    /**
     * The errorcode for errors wrt the creation of a connection.
     */
    public static final String EC_CREATION = EC_PREFIX + "00002";
    /**
     * The errorcode for errors wrt the creation of a connection.
     */
    public static final String EC_UNSUPPORTED_CONFIGURATION = EC_PREFIX + "00003";
    /**
     * The errorcode for errors wrt creating a soap message.
     */
    public static final String EC_CREATE_SOAPMESSAGE = EC_PREFIX + "00004";
    /**
     * The errorcode for errors wrt sending a soap message.
     */
    public static final String EC_SENDING = EC_PREFIX + "00005";
    /**
     * The errorcode for errors wrt LDAP calls.
     */
    public static final String EC_LDAP = EC_PREFIX + "00006";

    /**
     * Constructor. Creates a new exception object.
     *
     * @param  sErrorCode  The errorcode for this exception.
     */
    public CordysConnectionException(String sErrorCode)
    {
        this(sErrorCode, null, null, null);
    }

    /**
     * Creates a new CordysConnectionException object.
     *
     * @param  sErrorCode  The errorcode for this exception.
     * @param  tCause      The exception that caused this exception.
     */
    public CordysConnectionException(String sErrorCode, Throwable tCause)
    {
        this(sErrorCode, null, null, tCause);
    }

    /**
     * Creates a new CordysConnectionException object.
     *
     * @param  sErrorCode     The errorcode for this exception.
     * @param  sShortMessage  A short message for this exception.
     */
    public CordysConnectionException(String sErrorCode, String sShortMessage)
    {
        this(sErrorCode, sShortMessage, null, null);
    }

    /**
     * Creates a new CordysConnectionException object.
     *
     * @param  sErrorCode     The errorcode for this exception.
     * @param  sShortMessage  A short message for this exception.
     * @param  tCause         The exception that caused this exception.
     */
    public CordysConnectionException(String sErrorCode, String sShortMessage, Throwable tCause)
    {
        this(sErrorCode, sShortMessage, null, tCause);
    }

    /**
     * Creates a new CordysConnectionException object.
     *
     * @param  sErrorCode        The errorcode for this exception.
     * @param  sShortMessage     A short message for this exception.
     * @param  sDetailedMessage  A more detailed message for the exception.
     */
    public CordysConnectionException(String sErrorCode, String sShortMessage,
                                     String sDetailedMessage)
    {
        super(sErrorCode, sShortMessage, sDetailedMessage);
    }

    /**
     * Creates a new CordysConnectionException object.
     *
     * @param  sErrorCode        The errorcode for this exception.
     * @param  sShortMessage     A short message for this exception.
     * @param  sDetailedMessage  A more detailed message for the exception.
     * @param  tCause            The exception that caused this exception.
     */
    public CordysConnectionException(String sErrorCode, String sShortMessage,
                                     String sDetailedMessage, Throwable tCause)
    {
        super(sErrorCode, sShortMessage, sDetailedMessage, tCause);
    }
}
