package com.cordys.coe.exception;

import com.eibus.localization.IStringResource;
import com.eibus.localization.StringFormatter;

import com.eibus.soap.BodyBlock;
import com.eibus.soap.SOAPTransaction;
import com.eibus.soap.fault.Fault;
import com.eibus.soap.fault.FaultDetail;

import com.eibus.util.logger.CordysLogger;
import com.eibus.util.system.EIBProperties;

import java.util.Locale;

/**
 * Runtime variant of the server localizable exception.
 *
 * @author pgussow
 */
public class ServerRuntimeLocalizableException extends RuntimeException
{
    /**
     * Holds the logger that is used.
     */
    private static final CordysLogger LOG = CordysLogger.getCordysLogger(ServerLocalizableException.class);
    /**
     * Holds the list of parameters for the localizable message.
     */
    private Object[] m_aoParameters = null;
    /**
     * This variable holds the locale to use for the getLocalizedMessage.
     */
    private Locale m_lLocale = null;
    /**
     * Holds the way the preferred locale should be read.
     */
    private PreferredLocale m_plPreferredLocale = PreferredLocale.SOAP_LOCALE;
    /**
     * Holds the actor for the given fault (No restrictions from the BasicProfile, but usually an
     * URL).
     */
    private String m_sFaultActor;
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
    public ServerRuntimeLocalizableException(IStringResource srMessage, Object... aoParameters)
    {
        this((Throwable) null, srMessage, aoParameters);
    }

    /**
     * Creates a new LocalizableException object.
     *
     * @param  sFaultActor   The actor for the current fault.
     * @param  srMessage     The localizable message.
     * @param  aoParameters  The list of parameters for the localizeable message.
     */
    public ServerRuntimeLocalizableException(String sFaultActor, IStringResource srMessage,
                                             Object... aoParameters)
    {
        this((Throwable) null, (String) null, srMessage, aoParameters);
    }

    /**
     * Creates a new LocalizableException object.
     *
     * @param  tCause        The exception that caused this exception.
     * @param  srMessage     The localizable message.
     * @param  aoParameters  The list of parameters for the localizeable message.
     */
    public ServerRuntimeLocalizableException(Throwable tCause, IStringResource srMessage,
                                             Object... aoParameters)
    {
        this(tCause, null, srMessage, aoParameters);
    }

    /**
     * Creates a new LocalizableException object.
     *
     * @param  tCause        The exception that caused this exception.
     * @param  sFaultActor   The actor for the current fault.
     * @param  srMessage     The localizable message.
     * @param  aoParameters  The list of parameters for the localizeable message.
     */
    public ServerRuntimeLocalizableException(Throwable tCause, String sFaultActor,
                                             IStringResource srMessage, Object... aoParameters)
    {
        this(tCause, PreferredLocale.SOAP_LOCALE, sFaultActor, srMessage, aoParameters);
    }

    /**
     * Creates a new LocalizableException object.
     *
     * @param  tCause             The exception that caused this exception.
     * @param  plPreferredLocale  The preferred locale for this exception. It defaults to the SOAP
     *                            locale.
     * @param  sFaultActor        The actor for the current fault.
     * @param  srMessage          The localizable message.
     * @param  aoParameters       The list of parameters for the localizeable message.
     */
    public ServerRuntimeLocalizableException(Throwable tCause, PreferredLocale plPreferredLocale,
                                             String sFaultActor, IStringResource srMessage,
                                             Object... aoParameters)
    {
        super(((srMessage != null) ? srMessage.getResourceID() : "EC_UNKNOWN"), tCause);

        // The srMessage MUST be set.
        if (srMessage == null)
        {
            throw new IllegalArgumentException("The message code must be set");
        }

        m_plPreferredLocale = plPreferredLocale;

        if (m_plPreferredLocale == null)
        {
            m_plPreferredLocale = PreferredLocale.SOAP_LOCALE;
        }
        m_srMessage = srMessage;
        m_aoParameters = aoParameters;
        m_sFaultActor = sFaultActor;
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
        // First we have to know if there is a Locale context set.
        Locale lLocale = m_lLocale;

        if (lLocale == null)
        {
            // Now we have to set the locale to the preferred locale.
            switch (m_plPreferredLocale)
            {
                case SOAP_LOCALE:

                    SOAPTransaction st = SOAPTransaction.getCurrentSOAPTransaction();

                    if (st != null)
                    {
                        lLocale = st.getLocale();
                    }
                    break;

                case MANAGEMENT_LOCALE:
                    lLocale = EIBProperties.get_ManagementLocale();
                    break;

                case CORDYS_SYSTEM_LOCALE:
                    lLocale = EIBProperties.get_SystemDefaultLocale();
                    break;

                case SYSTEM_LOCALE:
                    lLocale = Locale.getDefault();
                    break;
            }

            // For some reason the locale is still not set, so use the JVM default
            if (lLocale == null)
            {
                lLocale = Locale.getDefault();
            }
        }

        if (LOG.isDebugEnabled())
        {
            LOG.debug("Going to use locale " + lLocale + " to render message with ID " +
                      m_srMessage.getFullyQualifiedResourceID());
        }

        return StringFormatter.format(lLocale, m_srMessage, m_aoParameters);
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
     * This method gets the preferred locale for this exception object.
     *
     * @return  The preferred locale for this exception object.
     */
    public PreferredLocale getPreferredLocale()
    {
        return m_plPreferredLocale;
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

    /**
     * This method sets the preferred locale for this exception object.
     *
     * @param  plPreferredLocale  The preferred locale for this exception object.
     */
    public void setPreferredLocale(PreferredLocale plPreferredLocale)
    {
        m_plPreferredLocale = plPreferredLocale;
    }

    /**
     * This method will convert this exception to a SOAP fault.
     *
     * @param   bbResponse  The response bodyblock.
     *
     * @return  The proper SOAP fault object to use.
     */
    public Fault toSOAPFault(BodyBlock bbResponse)
    {
        Fault fReturn = null;

        if ((m_sFaultActor != null) && (m_sFaultActor.length() > 0))
        {
            fReturn = bbResponse.createSOAPFault(Fault.Codes.SERVER, m_sFaultActor, m_srMessage,
                                                 m_aoParameters);
        }
        else
        {
            fReturn = bbResponse.createSOAPFault(Fault.Codes.SERVER, m_srMessage, m_aoParameters);
        }

        // Add the stack trace
        FaultDetail fdDetail = fReturn.getDetail();
        fdDetail.addDetailEntry(this);

        return fReturn;
    }

    /**
     * Holds the different type of locales that can be used.
     *
     * @author  pgussow
     */
    public enum PreferredLocale
    {
        SOAP_LOCALE,
        MANAGEMENT_LOCALE,
        CORDYS_SYSTEM_LOCALE,
        SYSTEM_LOCALE;
    }
}
