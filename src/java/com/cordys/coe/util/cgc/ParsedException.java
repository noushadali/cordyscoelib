package com.cordys.coe.util.cgc;

import java.util.ArrayList;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents a Java exception that was parsed from a string.
 *
 * @author  pgussow
 */
public class ParsedException
{
    /**
     * Holds the stacktrace elements.
     */
    private ArrayList<String> m_alStackTrace = new ArrayList<String>();
    /**
     * Holds the cause for this exception.
     */
    private ParsedException m_peCause;
    /**
     * Holds the exception class.
     */
    private String m_sExceptionClass;
    /**
     * Holds the message (if any) for this exception.
     */
    private String m_sExceptionMessage;
    /**
     * Holds the raw string containing the exception.
     */
    private String m_sRawException;

    /**
     * Creates a new ParsedException object.
     *
     * @param  sException  The raw exception string.
     */
    public ParsedException(String sException)
    {
        m_sRawException = sException;

        if (sException != null)
        {
            String[] asElements = m_sRawException.split("\n");

            if ((asElements != null) && (asElements.length > 0))
            {
                // The first line always starts with the classname and optionally followed by ':
                // <message>'.
                Matcher mMatcher = Pattern.compile("([^:]+)(:(.*)){0,1}").matcher(asElements[0]);

                if (mMatcher.find())
                {
                    m_sExceptionClass = mMatcher.group(1);
                    m_sExceptionMessage = mMatcher.group(3);

                    if (m_sExceptionMessage != null)
                    {
                        m_sExceptionMessage = m_sExceptionMessage.trim();

                        if (m_sExceptionMessage.length() == 0)
                        {
                            m_sExceptionMessage = null;
                        }
                    }
                }

                // The rest of the lines are either:
                // 1. Part of the stacktrace for this exception. this can be either 'at ' or '... '
                // 2. The cause of the exception.
                for (int iCount = 1; iCount < asElements.length; iCount++)
                {
                    String sLine = asElements[iCount];

                    if (sLine != null)
                    {
                        sLine = sLine.trim();

                        if (sLine.startsWith("at ") || sLine.startsWith("... "))
                        {
                            // Stacktrace element
                            m_alStackTrace.add(sLine);
                        }
                        else if (sLine.startsWith("Caused by"))
                        {
                            // Nested exception
                            StringBuilder sbNested = new StringBuilder();
                            sbNested.append(sLine.substring("Caused by: ".length()));
                            sbNested.append("\n");

                            for (int iNestedCount = iCount + 1; iNestedCount < asElements.length;
                                     iNestedCount++)
                            {
                                sbNested.append(asElements[iNestedCount]);
                                sbNested.append("\n");
                            }

                            m_peCause = new ParsedException(sbNested.toString());
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * This method gets the cause for this exception.
     *
     * @return  The cause for this exception.
     */
    public ParsedException getCause()
    {
        return m_peCause;
    }

    /**
     * This method gets the exception class.
     *
     * @return  The exception class.
     */
    public String getExceptionClass()
    {
        return m_sExceptionClass;
    }

    /**
     * This method gets the exception message.
     *
     * @return  The exception message.
     */
    public String getExceptionMessage()
    {
        return m_sExceptionMessage;
    }

    /**
     * This method gets the raw string containing the exception.
     *
     * @return  The raw string containing the exception.
     */
    public String getRawException()
    {
        return m_sRawException;
    }

    /**
     * This method returns the string representation of the object.
     *
     * @return  The string representation of the object.
     *
     * @see     java.lang.Object#toString()
     */
    @Override public String toString()
    {
        StringBuilder sbReturn = new StringBuilder();

        sbReturn.append("Class: " + getExceptionClass());

        if (m_sExceptionMessage != null)
        {
            sbReturn.append("\nMessage: " + getExceptionMessage());
        }

        if (m_alStackTrace.size() > 0)
        {
            sbReturn.append("\nStacktrace:\n");

            for (String sStacktrace : m_alStackTrace)
            {
                sbReturn.append(sStacktrace + "\n");
            }
        }

        if (m_peCause != null)
        {
            sbReturn.append("Caused by:\n");
            sbReturn.append(m_peCause.toString());
        }

        return sbReturn.toString();
    }
}
