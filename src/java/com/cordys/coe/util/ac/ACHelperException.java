package com.cordys.coe.util.ac;

import com.cordys.coe.exception.ClientLocalizableException;
import com.eibus.localization.IStringResource;

/**
 * Holds the Class ACHelperException.
 */
public class ACHelperException extends ClientLocalizableException
{

    /**
     * Instantiates a new aC helper exception.
     * 
     * @param srMessage The sr message
     * @param aoParameters The ao parameters
     */
    public ACHelperException(IStringResource srMessage, Object... aoParameters)
    {
        super(srMessage, aoParameters);
    }

    /**
     * Instantiates a new aC helper exception.
     * 
     * @param tCause The t cause
     * @param srMessage The sr message
     * @param aoParameters The ao parameters
     */
    public ACHelperException(Throwable tCause, IStringResource srMessage, Object... aoParameters)
    {
        super(tCause, srMessage, aoParameters);
    }

}
