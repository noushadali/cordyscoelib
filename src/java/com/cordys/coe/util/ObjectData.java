package com.cordys.coe.util;

/**
 * This class can be used in a UI where you need to wrap an object with a descriptive name (i.e. for
 * a combobox).
 *
 * @author  pgussow
 */
public class ObjectData<T1>
{
    /**
     * Holds the wrapped value.
     */
    private T1 m_oValue;
    /**
     * Holds the descriptive name.
     */
    private String m_sDescription;

    /**
     * Creates a new ObjectData object.
     *
     * @param  sDescription  The descriptive name.
     * @param  oValue        The actual object.
     */
    public ObjectData(String sDescription, T1 oValue)
    {
        m_sDescription = sDescription;
        m_oValue = oValue;
    }

    /**
     * This method gets the descriptive name.
     *
     * @return  The descriptive name.
     */
    public String getDescription()
    {
        return m_sDescription;
    }

    /**
     * This method gets the actual value object.
     *
     * @return  The actual value object.
     */
    public T1 getValue()
    {
        return m_oValue;
    }

    /**
     * This method sets the descriptive name.
     *
     * @param  sDescription  The descriptive name.
     */
    public void setDescription(String sDescription)
    {
        m_sDescription = sDescription;
    }

    /**
     * This method sets the actual value object.
     *
     * @param  oValue  The actual value object.
     */
    public void setValue(T1 oValue)
    {
        m_oValue = oValue;
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

        sbReturn.append(getDescription());

        return sbReturn.toString();
    }
}
