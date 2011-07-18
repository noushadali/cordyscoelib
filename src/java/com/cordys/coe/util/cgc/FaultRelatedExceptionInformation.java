package com.cordys.coe.util.cgc;

/**
 * This class wraps the FaultRelatedException tag within the SOAP fault. This tag contains the Java
 * stack trace for the error message.
 *
 * @author  pgussow
 */
public class FaultRelatedExceptionInformation
{
    /**
     * Holds the parsed exception for this stacktrace.
     */
    private ParsedException m_peException;
    /**
     * Holds the raw content of the information.
     */
    private String m_sRawContent;

    /**
     * Creates a new FaultRelatedExceptionInformation object.
     *
     * @param  sContent  The raw content of the FaultRelatedException tag.
     */
    public FaultRelatedExceptionInformation(String sContent)
    {
        m_sRawContent = sContent;

        try
        {
            m_peException = new ParsedException(sContent);
        }
        catch (Exception e)
        {
            // Ignore it.
        }
    }

    /**
     * This method gets the ParsedException object that analyzed this exception.
     *
     * @return  The ParsedException object that analyzed this exception.
     */
    public ParsedException getParsedException()
    {
        return m_peException;
    }

    /**
     * This method gets the raw content for this tag.
     *
     * @return  The raw content for this tag.
     */
    public String getRawContent()
    {
        return m_sRawContent;
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

        if (m_peException != null)
        {
            sbReturn.append(m_sRawContent);
        }
        else
        {
            sbReturn.append(m_peException.toString());
        }

        return sbReturn.toString();
    }
}
