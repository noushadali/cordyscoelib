package com.cordys.coe.exception;

import javax.xml.namespace.QName;

import com.cordys.cpc.bsf.busobject.exception.ILocalizableException;
import com.eibus.localization.IStringResource;
import com.eibus.soap.fault.Fault;

/**
 * This class functions the same as the ServerRuntimeLocalizableException class, but it also implements the WsAppServer
 * ILocalizableException. The reason to split these 2 classes is to avoid that if you run outside of WsAppServer that you need to
 * include the WsAppServer jar files on the classpath.
 * 
 * @author pgussow
 */
public class WsAppsRuntimeLocalizableException extends ServerRuntimeLocalizableException implements ILocalizableException
{
    /** The fault code to use for the exception. The default is SERVER */
    private QName m_faultCode = Fault.Codes.SERVER;

    /**
     * Creates a new WsAppsRuntimeLocalizableException object.
     * 
     * @param srMessage the sr message
     * @param aoParameters the ao parameters
     */
    public WsAppsRuntimeLocalizableException(IStringResource srMessage, Object... aoParameters)
    {
        this((Throwable) null, srMessage, aoParameters);
    }

    /**
     * Creates a new WsAppsRuntimeLocalizableException object.
     * 
     * @param sFaultActor the s fault actor
     * @param srMessage the sr message
     * @param aoParameters the ao parameters
     */
    public WsAppsRuntimeLocalizableException(String sFaultActor, IStringResource srMessage, Object... aoParameters)
    {
        this((Throwable) null, (String) null, srMessage, aoParameters);
    }

    /**
     * Creates a new WsAppsRuntimeLocalizableException object.
     * 
     * @param tCause the t cause
     * @param srMessage the sr message
     * @param aoParameters the ao parameters
     */
    public WsAppsRuntimeLocalizableException(Throwable tCause, IStringResource srMessage, Object... aoParameters)
    {
        this(tCause, null, srMessage, aoParameters);
    }

    /**
     * Creates a new WsAppsRuntimeLocalizableException object.
     * 
     * @param tCause the t cause
     * @param sFaultActor the s fault actor
     * @param srMessage the sr message
     * @param aoParameters the ao parameters
     */
    public WsAppsRuntimeLocalizableException(Throwable tCause, String sFaultActor, IStringResource srMessage,
            Object... aoParameters)
    {
        this(tCause, PreferredLocale.SOAP_LOCALE, sFaultActor, srMessage, aoParameters);
    }

    /**
     * Creates a new WsAppsRuntimeLocalizableException object.
     * 
     * @param tCause the t cause
     * @param plPreferredLocale the pl preferred locale
     * @param sFaultActor the s fault actor
     * @param srMessage the sr message
     * @param aoParameters the ao parameters
     */
    public WsAppsRuntimeLocalizableException(Throwable tCause, PreferredLocale plPreferredLocale, String sFaultActor,
            IStringResource srMessage, Object... aoParameters)
    {
        super(tCause, plPreferredLocale, sFaultActor, srMessage, aoParameters);
    }

    /**
     * Creates a new WsAppsRuntimeLocalizableException object.
     * 
     * @param cause The cause of this exception.
     * @param preferredLocale the preferred locale for this exception.
     * @param faultCode the fault code to use. Default is SERVER.
     * @param faultActor the fault actor
     * @param message the actual message for the exception.
     * @param parameters the parameters for the message.
     */
    public WsAppsRuntimeLocalizableException(Throwable cause, PreferredLocale preferredLocale, QName faultCode,
            String faultActor, IStringResource message, Object... parameters)
    {
        super(cause, preferredLocale, faultActor, message, parameters);

        m_faultCode = faultCode;
    }

    /**
     * @see com.cordys.cpc.bsf.busobject.exception.ILocalizableException#getLocalizableMessageID()
     */
    public IStringResource getLocalizableMessageID()
    {
        return getMessageObject();
    }

    /**
     * @see com.cordys.cpc.bsf.busobject.exception.ILocalizableException#getMessageParameters()
     */
    @Override
    public Object[] getMessageParameters()
    {
        return super.getMessageParameters();
    }

    /**
     * @see com.cordys.cpc.bsf.busobject.exception.ILocalizableException#setFaultCode(javax.xml.namespace.QName)
     */
    @Override
    public void setFaultCode(QName faultCode)
    {
        m_faultCode = faultCode;
    }

    /**
     * @see com.cordys.cpc.bsf.busobject.exception.ILocalizableException#getFaultCode()
     */
    @Override
    public QName getFaultCode()
    {
        return m_faultCode;
    }
}
