package com.cordys.coe.exception;

import com.eibus.localization.IStringResource;

import java.text.MessageFormat;

import java.util.Locale;

/**
 * This class enables you to use real Cordys localizations with your exceptions. Outside of proper
 * SOAP Fault creation it will also make sure that when you do a printStackTrace() the proper Locale
 * is used.<br/>
 * Note: This class has to work both server side (on a server where Cordys is installed) as client
 * side (where no Cordys is installed). In order to make sure this is possible the only dependency
 * for this class is the managementlib.jar. It needs it for the IStringResource interface.
 *
 * @author  pgussow
 */
public class ClientLocalizableException extends Exception
{
    /**
     * Holds the list of parameters for the localizable message.
     */
    private Object[] m_aoParameters = null;
    /**
     * This variable holds the locale to use for the getLocalizedMessage.
     */
    private Locale m_lLocale = null;
    /**
     * Holds the localizable error message.
     */
    private IStringResource m_srMessage = null;

    /**
     * Creates a new LocalizableException object.
     *
     * @param  srMessage     The localizable message.
     * @param  aoParameters  The list of parameters for the localizeable message.
     */
    public ClientLocalizableException(IStringResource srMessage, Object... aoParameters)
    {
        this((Throwable) null, srMessage, aoParameters);
    }

    /**
     * Creates a new LocalizableException object.
     *
     * @param  tCause        The exception that caused this exception.
     * @param  srMessage     The localizable message.
     * @param  aoParameters  The list of parameters for the localizeable message.
     */
    public ClientLocalizableException(Throwable tCause, IStringResource srMessage,
                                      Object... aoParameters)
    {
        super(((srMessage != null) ? srMessage.getResourceID() : "EC_UNKNOWN"), tCause);

        // The srMessage MUST be set.
        if (srMessage == null)
        {
            throw new IllegalArgumentException("The message code must be set");
        }

        m_srMessage = srMessage;
        m_aoParameters = aoParameters;
    }

    /**
     * This method gets the locale that should be used for logging the exception.
     *
     * @return  The locale that should be used for logging the exception.
     */
    public Locale getLocale()
    {
        return m_lLocale;
    }

    /**
     * Creates a localized description of this throwable. Subclasses may override this method in
     * order to produce a locale-specific message. For subclasses that do not override this method,
     * the default implementation returns the same result as <code>getMessage()</code>.
     *
     * @return  The localized description of this throwable.
     */
    @Override public String getLocalizedMessage()
    {
        String sReturn = null;
        // First we have to know if there is a Locale context set.
        Locale lLocale = m_lLocale;

        if (lLocale == null)
        {
            lLocale = Locale.getDefault();
        }

        // Do this on our own to avoid the use of the Cordys String formatter.
        sReturn = m_srMessage.getMessage(lLocale);

        if ((m_aoParameters != null) && (m_aoParameters.length > 0))
        {
            // There are parameters, so they need to be substituted.
            sReturn = new MessageFormat(sReturn, lLocale).format(m_aoParameters, new StringBuffer(),
                                                                 null).toString();
        }

        return sReturn;
    }

    /**
     * This method gets the message for this exception.
     *
     * @return  The message for this exception.
     */
    public IStringResource getMessageObject()
    {
        return m_srMessage;
    }

    /**
     * This method gets the message parameters.
     *
     * @return  The message parameters.
     *
     * @see     com.cordys.cpc.bsf.busobject.exception.ILocalizableException#getMessageParameters()
     */
    public Object[] getMessageParameters()
    {
        return m_aoParameters;
    }

    /**
     * This method sets the locale that should be used for logging the exception. When you set this
     * locale it will override the PreferredLocale.
     *
     * @param  lLocale  The locale that should be used for logging the exception.
     */
    public void setLocale(Locale lLocale)
    {
        m_lLocale = lLocale;
    }

    /**
     * This method sets the localizable message that should be used.
     *
     * @param  srMessage     The localizable message.
     * @param  aoParameters  The parameters for the message.
     *
     * @see    com.cordys.cpc.bsf.busobject.exception.ILocalizableException#setLocalizableMessage(com.eibus.localization.IStringResource,
     *         java.lang.Object[])
     */
    public void setLocalizableMessage(IStringResource srMessage, Object... aoParameters)
    {
        m_srMessage = srMessage;
        m_aoParameters = aoParameters;
    }
}
