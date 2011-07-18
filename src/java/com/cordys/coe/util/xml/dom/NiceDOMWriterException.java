package com.cordys.coe.util.xml.dom;

/**
 * This exception is used for the NiceDOMWriter exceptions.
 *
 * @author  pgussow
 */
public class NiceDOMWriterException extends RuntimeException
{
    /**
     * Creates a new NiceDOMWriterException object.
     *
     * @param  sMessage  The exception message.
     * @param  tCause    The cause for this exception.
     */
    public NiceDOMWriterException(String sMessage, Throwable tCause)
    {
        super(sMessage, tCause);
    }
}
