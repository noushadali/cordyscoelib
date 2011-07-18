package com.cordys.coe.util.wcpproperties;

/**
 * This class wraps around the class info.
 *
 * @author  pgussow
 */
public class ClassInfo
{
    /**
     * Holds the class name.
     */
    private String m_sClass;
    /**
     * Holds the sourcepath on which the class is found.
     */
    private SourcePath m_spPath;

    /**
     * Creates a new ClassInfo object.
     *
     * @param  sClass  The class name.
     * @param  spPath  The sourcepath on which the class is found.
     */
    public ClassInfo(String sClass, SourcePath spPath)
    {
        m_sClass = sClass;
        m_spPath = spPath;
    }

    /**
     * DOCUMENTME.
     *
     * @param   oObject  obj
     *
     * @return
     *
     * @see     java.lang.Object#equals(java.lang.Object)
     */
    @Override public boolean equals(Object oObject)
    {
        return ((oObject instanceof ClassInfo) && (oObject.hashCode() == this.hashCode()));
    }

    /**
     * This method gets the class name.
     *
     * @return  The class name.
     */
    public String getClassName()
    {
        return m_sClass;
    }

    /**
     * This method gets the sourcepath on which the class is found..
     *
     * @return  The sourcepath on which the class is found..
     */
    public SourcePath getSourcepath()
    {
        return m_spPath;
    }

    /**
     * DOCUMENTME.
     *
     * @return  The hashcode for the object.
     *
     * @see     java.lang.Object#hashCode()
     */
    @Override public int hashCode()
    {
        return m_sClass.hashCode() ^ m_spPath.getComponentName().hashCode();
    }

    /**
     * This method sets the class name.
     *
     * @param  sClass  The class name.
     */
    public void setClassName(String sClass)
    {
        m_sClass = sClass;
    }

    /**
     * This method sets the sourcepath on which the class is found..
     *
     * @param  spPath  The sourcepath on which the class is found..
     */
    public void setSourcepath(SourcePath spPath)
    {
        m_spPath = spPath;
    }
}
