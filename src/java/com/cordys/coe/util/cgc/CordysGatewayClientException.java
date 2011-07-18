package com.cordys.coe.util.cgc;

import com.cordys.coe.exception.ClientLocalizableException;

import com.eibus.localization.IStringResource;

/**
 * This class wraps all non=soap exceptions that can occur in Cordys.
 *
 * @author  pgussow
 */
public class CordysGatewayClientException extends ClientLocalizableException
{
    /**
     * Creates a new LocalizableException object.
     *
     * @param  srMessage     The localizable message.
     * @param  aoParameters  The list of parameters for the localizeable message.
     */
    public CordysGatewayClientException(IStringResource srMessage, Object... aoParameters)
    {
        super(srMessage, aoParameters);
    }

    /**
     * Creates a new LocalizableException object.
     *
     * @param  tCause        The exception that caused this exception.
     * @param  srMessage     The localizable message.
     * @param  aoParameters  The list of parameters for the localizeable message.
     */
    public CordysGatewayClientException(Throwable tCause, IStringResource srMessage,
                                        Object... aoParameters)
    {
        super(tCause, srMessage, aoParameters);
    }
}
