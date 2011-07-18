package com.cordys.coe.util;

/**
 * This class wraps a name-value pair.
 *
 * @author  pgussow
 */
public class NameValuePair
{
    /**
     * Holds the name for this pair.
     */
    private String m_sName;

    /**
     * Holds the value for this pair.
     */
    private String m_sValue;

    /**
     * Creates a new NameValuePair object.
     *
     * @param  sName   The name for this pair.
     * @param  sValue  The value for this pair.
     */
    public NameValuePair(String sName, String sValue)
    {
        m_sName = sName;
        m_sValue = sValue;
    }

    /**
     * @see  java.lang.Object#equals(java.lang.Object)
     */
    @Override public boolean equals(Object oOther)
    {
        boolean bReturn = false;

        if (oOther instanceof NameValuePair)
        {
            NameValuePair nvpOther = (NameValuePair) oOther;

            if (nvpOther.getName().equals(m_sName) && nvpOther.getValue().equals(m_sValue))
            {
                bReturn = true;
            }
        }

        return bReturn;
    }

    /**
     * This method gets the name for this pair.
     *
     * @return  The name for this pair.
     */
    public String getName()
    {
        return m_sName;
    }

    /**
     * This method gets the value for this pair.
     *
     * @return  The value for this pair.
     */
    public String getValue()
    {
        return m_sValue;
    }

    /**
     * This method sets the name for this pair.
     *
     * @param  sName  The name for this pair.
     */
    public void setName(String sName)
    {
        m_sName = sName;
    }

    /**
     * This method sets the value for this pair.
     *
     * @param  sValue  The value for this pair.
     */
    public void setValue(String sValue)
    {
        m_sValue = sValue;
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

        sbReturn.append(m_sValue);

        return sbReturn.toString();
    }
}
