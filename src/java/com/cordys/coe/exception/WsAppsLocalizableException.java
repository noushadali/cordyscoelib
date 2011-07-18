package com.cordys.coe.exception;

import com.cordys.cpc.bsf.busobject.exception.ILocalizableException;

import com.eibus.localization.IStringResource;

/**
 * This class functions the same as the LocalizableException class, but it also implements the
 * WsAppServer ILocalizableException. The reason to split these 2 classes is to avoid that if you
 * run outside of WsAppServer that you need to include the WsAppServer jar files on the classpath.
 *
 * @author  pgussow
 */
public class WsAppsLocalizableException extends ServerLocalizableException
    implements ILocalizableException
{
    /**
     * Creates a new WsAppsLocalizableException object.
     *
     * @param  srMessage     The localizable message.
     * @param  aoParameters  The list of parameters for the localizeable message.
     */
    public WsAppsLocalizableException(IStringResource srMessage, Object... aoParameters)
    {
        super(srMessage, aoParameters);
    }

    /**
     * Creates a new WsAppsLocalizableException object.
     *
     * @param  sFaultActor   The actor for the current fault.
     * @param  srMessage     The localizable message.
     * @param  aoParameters  The list of parameters for the localizeable message.
     */
    public WsAppsLocalizableException(String sFaultActor, IStringResource srMessage,
                                      Object... aoParameters)
    {
        super(sFaultActor, srMessage, aoParameters);
    }

    /**
     * Creates a new WsAppsLocalizableException object.
     *
     * @param  tCause        The exception that caused this exception.
     * @param  srMessage     The localizable message.
     * @param  aoParameters  The list of parameters for the localizeable message.
     */
    public WsAppsLocalizableException(Throwable tCause, IStringResource srMessage,
                                      Object... aoParameters)
    {
        super(tCause, srMessage, aoParameters);
    }

    /**
     * Creates a new WsAppsLocalizableException object.
     *
     * @param  tCause        The exception that caused this exception.
     * @param  sFaultActor   The actor for the current fault.
     * @param  srMessage     The localizable message.
     * @param  aoParameters  The list of parameters for the localizeable message.
     */
    public WsAppsLocalizableException(Throwable tCause, String sFaultActor,
                                      IStringResource srMessage, Object... aoParameters)
    {
        super(tCause, sFaultActor, srMessage, aoParameters);
    }

    /**
     * Creates a new WsAppsLocalizableException object.
     *
     * @param  tCause             The exception that caused this exception.
     * @param  plPreferredLocale  The preferred locale for this exception. It defaults to the SOAP
     *                            locale.
     * @param  sFaultActor        The actor for the current fault.
     * @param  srMessage          The localizable message.
     * @param  aoParameters       The list of parameters for the localizeable message.
     */
    public WsAppsLocalizableException(Throwable tCause, PreferredLocale plPreferredLocale,
                                      String sFaultActor, IStringResource srMessage,
                                      Object... aoParameters)
    {
        super(tCause, plPreferredLocale, sFaultActor, srMessage, aoParameters);
    }

    /**
     * This methos returns the localizable message for this exception.
     *
     * @return  The localizable message for this exception.
     *
     * @see     com.cordys.cpc.bsf.busobject.exception.ILocalizableException#getLocalizableMessageID()
     */
    public IStringResource getLocalizableMessageID()
    {
        return getMessageObject();
    }

    /**
     * This method gets the message parameters.
     *
     * @return  The message parameters.
     *
     * @see     com.cordys.cpc.bsf.busobject.exception.ILocalizableException#getMessageParameters()
     */
    @Override public Object[] getMessageParameters()
    {
        return super.getMessageParameters();
    }
}
