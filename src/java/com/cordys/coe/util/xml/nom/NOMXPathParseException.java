package com.cordys.coe.util.xml.nom;

/**
 * Exception class for the NOMXpath class.
 *
 * @author  pgussow
 */
public class NOMXPathParseException extends Exception
{
    /**
     * Creates a new NOMXPathParseException object.
     */
    public NOMXPathParseException()
    {
        super();
    }

    /**
     * Creates a new NOMXPathParseException object.
     *
     * @param  message
     * @param  cause
     */
    public NOMXPathParseException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Creates a new NOMXPathParseException object.
     *
     * @param  message
     */
    public NOMXPathParseException(String message)
    {
        super(message);
    }

    /**
     * Creates a new NOMXPathParseException object.
     *
     * @param  cause
     */
    public NOMXPathParseException(Throwable cause)
    {
        super(cause);
    }
}
