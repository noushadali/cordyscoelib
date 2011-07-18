package com.cordys.coe.util.config;

import com.cordys.coe.util.exceptions.AbstractCoEException;

/**
 * Exception object for the configuration manager.
 *
 * @author  pgussow
 */
public class ConfigurationManagerException extends AbstractCoEException
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
     * The errorcode for errors wrt the user home folder.
     */
    public static final String EC_USER_FOLDER = EC_PREFIX + "00002";
    /**
     * The errorcode for errors wrt the configuration file.
     */
    public static final String EC_CONFIG_FILE = EC_PREFIX + "00003";
    /**
     * The errorcode for errors wrt the configuration XML.
     */
    public static final String EC_CONFIGURATION = EC_PREFIX + "00004";

    /**
     * Constructor. Creates a new exception object.
     *
     * @param  sErrorCode  The errorcode for this exception.
     */
    public ConfigurationManagerException(String sErrorCode)
    {
        this(sErrorCode, null, null, null);
    }

    /**
     * Creates a new ConfigurationManagerException object.
     *
     * @param  sErrorCode  The errorcode for this exception.
     * @param  tCause      The exception that caused this exception.
     */
    public ConfigurationManagerException(String sErrorCode, Throwable tCause)
    {
        this(sErrorCode, null, null, tCause);
    }

    /**
     * Creates a new ConfigurationManagerException object.
     *
     * @param  sErrorCode     The errorcode for this exception.
     * @param  sShortMessage  A short message for this exception.
     */
    public ConfigurationManagerException(String sErrorCode, String sShortMessage)
    {
        this(sErrorCode, sShortMessage, null, null);
    }

    /**
     * Creates a new ConfigurationManagerException object.
     *
     * @param  sErrorCode     The errorcode for this exception.
     * @param  sShortMessage  A short message for this exception.
     * @param  tCause         The exception that caused this exception.
     */
    public ConfigurationManagerException(String sErrorCode, String sShortMessage, Throwable tCause)
    {
        this(sErrorCode, sShortMessage, null, tCause);
    }

    /**
     * Creates a new ConfigurationManagerException object.
     *
     * @param  sErrorCode        The errorcode for this exception.
     * @param  sShortMessage     A short message for this exception.
     * @param  sDetailedMessage  A more detailed message for the exception.
     */
    public ConfigurationManagerException(String sErrorCode, String sShortMessage,
                                         String sDetailedMessage)
    {
        super(sErrorCode, sShortMessage, sDetailedMessage);
    }

    /**
     * Creates a new ConfigurationManagerException object.
     *
     * @param  sErrorCode        The errorcode for this exception.
     * @param  sShortMessage     A short message for this exception.
     * @param  sDetailedMessage  A more detailed message for the exception.
     * @param  tCause            The exception that caused this exception.
     */
    public ConfigurationManagerException(String sErrorCode, String sShortMessage,
                                         String sDetailedMessage, Throwable tCause)
    {
        super(sErrorCode, sShortMessage, sDetailedMessage, tCause);
    }
}
