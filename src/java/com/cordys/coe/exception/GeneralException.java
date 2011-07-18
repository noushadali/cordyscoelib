/**
 * © 2003 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys R&D B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * A general exception with a Throwable and an additional message.
 *
 * @author  pgussow
 */
public class GeneralException extends Exception
{
    /**
     * Holds the additional Throwable.
     */
    private Throwable tCause = null;

    /**
     * Creates a new instance of <code>GeneralException</code> without a cause.
     */
    public GeneralException()
    {
        super();
    }

    /**
     * Creates a new instance of <code>GeneralException</code> based on the the throwable.
     *
     * @param  tThrowable  The source throwable.
     */
    public GeneralException(Throwable tThrowable)
    {
        super("");

        if (tThrowable != null)
        {
            tCause = tThrowable.fillInStackTrace();
        }
    }

    /**
     * Constructs an instance of <code>GeneralException</code> with the specified detail message.
     *
     * @param  sMessage  the detail message.
     */
    public GeneralException(String sMessage)
    {
        super(sMessage);
    }

    /**
     * Creates a new instance of <code>GeneralException</code> based on the the throwable.
     *
     * @param  tThrowable  The cause.
     * @param  sMessage    The additional message.
     */
    public GeneralException(Throwable tThrowable, String sMessage)
    {
        super(sMessage);

        if (tThrowable != null)
        {
            tCause = tThrowable.fillInStackTrace();
        }
    }

    /**
     * Returns the causing exception object.
     *
     * @return  DOCUMENTME
     */
    @Override public Throwable getCause()
    {
        return tCause;
    }

    /**
     * Returns a String-representation of the object.
     *
     * @return  A String-representation of the object.
     */
    @Override public String toString()
    {
        String sReturn = super.toString();

        if (tCause != null)
        {
            StringWriter sw = new StringWriter();
            tCause.printStackTrace(new PrintWriter(sw));
            sReturn = sReturn + "\nOriginal Exception:\n" + sw.getBuffer().toString();
        }
        return sReturn;
    }
}
