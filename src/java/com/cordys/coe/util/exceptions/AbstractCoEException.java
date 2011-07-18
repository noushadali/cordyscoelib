package com.cordys.coe.util.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * This is a base exception which can hold an error code, short message, detailed message and a
 * cause.
 *
 * @author  pgussow
 */
public abstract class AbstractCoEException extends Exception
{
    /**
     * Holds the detailed message of this exception.
     */
    private String m_sDetailedMessage;
    /**
     * Holds the errorcode for this exception.
     */
    private String m_sErrorCode;
    /**
     * Holds the short message of this exception.
     */
    private String m_sShortMessage;

    /**
     * Constructor. Creates a new exception object.
     *
     * @param  sErrorCode  The errorcode for this exception.
     */
    public AbstractCoEException(String sErrorCode)
    {
        this(sErrorCode, null, null, null);
    }

    /**
     * Creates a new AbstractCoEException object.
     *
     * @param  sErrorCode  The errorcode for this exception.
     * @param  tCause      The exception that caused this exception.
     */
    public AbstractCoEException(String sErrorCode, Throwable tCause)
    {
        this(sErrorCode, null, null, tCause);
    }

    /**
     * Creates a new AbstractCoEException object.
     *
     * @param  sErrorCode     The errorcode for this exception.
     * @param  sShortMessage  A short message for this exception.
     */
    public AbstractCoEException(String sErrorCode, String sShortMessage)
    {
        this(sErrorCode, sShortMessage, null, null);
    }

    /**
     * Creates a new AbstractCoEException object.
     *
     * @param  sErrorCode     The errorcode for this exception.
     * @param  sShortMessage  A short message for this exception.
     * @param  tCause         The exception that caused this exception.
     */
    public AbstractCoEException(String sErrorCode, String sShortMessage, Throwable tCause)
    {
        this(sErrorCode, sShortMessage, null, tCause);
    }

    /**
     * Creates a new AbstractCoEException object.
     *
     * @param  sErrorCode        The errorcode for this exception.
     * @param  sShortMessage     A short message for this exception.
     * @param  sDetailedMessage  A more detailed message for the exception.
     */
    public AbstractCoEException(String sErrorCode, String sShortMessage, String sDetailedMessage)
    {
        super(sErrorCode);
        m_sErrorCode = sErrorCode;
        m_sShortMessage = sShortMessage;
        m_sDetailedMessage = sDetailedMessage;
    }

    /**
     * Creates a new AbstractCoEException object.
     *
     * @param  sErrorCode        The errorcode for this exception.
     * @param  sShortMessage     A short message for this exception.
     * @param  sDetailedMessage  A more detailed message for the exception.
     * @param  tCause            The exception that caused this exception.
     */
    public AbstractCoEException(String sErrorCode, String sShortMessage, String sDetailedMessage,
                                Throwable tCause)
    {
        super(sErrorCode, tCause);
        m_sErrorCode = sErrorCode;
        m_sShortMessage = sShortMessage;
        m_sDetailedMessage = sDetailedMessage;
    }

    /**
     * This method gets the detailed message of this exception..
     *
     * @return  The detailed message of this exception..
     */
    public String getDetailedMessage()
    {
        return m_sDetailedMessage;
    }

    /**
     * This method gets the errorcode for this exception.
     *
     * @return  The errorcode for this exception.
     */
    public String getErrorCode()
    {
        return m_sErrorCode;
    }

    /**
     * This method returns the message that should be displayed.
     *
     * @return  The message that should be displayed.
     */
    @Override public String getMessage()
    {
        StringBuffer sbReturn = new StringBuffer(m_sErrorCode);

        if ((m_sShortMessage != null) && (m_sShortMessage.length() > 0))
        {
            sbReturn.append(": ");
            sbReturn.append(m_sShortMessage);
        }

        return sbReturn.toString();
    }

    /**
     * This method returns the string-representation of the stacktrace of the passed on exception.
     *
     * @return  The string-representation of the stacktrace.
     */
    public String getOwnStackTrace()
    {
        // Get the stack-trace
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        printStackTrace(pw);
        pw.flush();

        return sw.getBuffer().toString();
    }

    /**
     * This method gets the short message of this exception..
     *
     * @return  The short message of this exception..
     */
    public String getShortMessage()
    {
        return m_sShortMessage;
    }

    /**
     * Returns a short description of this throwable. If this <code>Throwable</code> object was
     * created with a non-null detail message string, then the result is the concatenation of three
     * strings:
     *
     * <ul>
     *   <li>The name of the actual class of this object</li>
     *   <li>": " (a colon and a space)</li>
     *   <li>The result of the {@link #getMessage} method for this object</li>
     * </ul>
     *
     * <p>If this <code>Throwable</code> object was created with a <tt>null</tt> detail message
     * string, then the name of the actual class of this object is returned.</p>
     *
     * @return  A string representation of this throwable.
     */
    @Override public String toString()
    {
        StringBuffer sbReturn = new StringBuffer(getClass().getName());
        String sMessage = getLocalizedMessage();

        if ((sMessage != null) && (sMessage.length() > 0))
        {
            sbReturn.append(": ");
            sbReturn.append(sMessage);
        }

        if ((m_sDetailedMessage != null) && (m_sDetailedMessage.length() > 0))
        {
            sbReturn.append("\nDetails: " + m_sDetailedMessage);
        }

        return sbReturn.toString();
    }
}
