package com.cordys.coe.tools.testtool.methods;

/**
 * This method wraps the information for a specific method.
 *
 * @author  pgussow
 */
public abstract class BaseMethodInfo
    implements IMethodInfo
{
    /**
     * Holds the method set DN for the current method.
     */
    private String m_methodSetDN;
    /**
     * Holds the implementation type for the method.
     */
    private String m_sImplementationType;
    /**
     * Holds the name of the method.
     */
    private String m_sName;
    /**
     * Holds the namespace for this method.
     */
    private String m_sNamespace;

    /**
     * Creates a new BaseMethodInfo object.
     *
     * @param  sImplementationType  The implementation type for the method.
     * @param  sName                The name of the method.
     * @param  sNamespace           The namespace for this method.
     */
    public BaseMethodInfo(String sImplementationType, String sName, String sNamespace)
    {
        m_sImplementationType = sImplementationType;
        m_sName = sName;
        m_sNamespace = sNamespace;
    }

    /**
     * This method gets the implementation type for the method.
     *
     * @return  The implementation type for the method.
     *
     * @see     com.cordys.coe.tools.testtool.methods.IMethodInfo#getImplementationType()
     */
    public String getImplementationType()
    {
        return m_sImplementationType;
    }

    /**
     * This method gets the method set DN for the current method.
     *
     * @return  The method set DN for the current method.
     */
    public String getMethodSetDN()
    {
        return m_methodSetDN;
    }

    /**
     * This method gets the name of the method.
     *
     * @return  The name of the method.
     *
     * @see     com.cordys.coe.tools.testtool.methods.IMethodInfo#getName()
     */
    public String getName()
    {
        return m_sName;
    }

    /**
     * This method gets the namespace for this method.
     *
     * @return  The namespace for this method.
     *
     * @see     com.cordys.coe.tools.testtool.methods.IMethodInfo#getNamespace()
     */
    public String getNamespace()
    {
        return m_sNamespace;
    }

    /**
     * This method sets the implementation type for the method.
     *
     * @param  sImplementationType  The implementation type for the method.
     *
     * @see    com.cordys.coe.tools.testtool.methods.IMethodInfo#setImplementationType(java.lang.String)
     */
    public void setImplementationType(String sImplementationType)
    {
        m_sImplementationType = sImplementationType;
    }

    /**
     * This method sets the method set DN for the current method.
     *
     * @param  methodSetDN  The method set DN for the current method.
     */
    public void setMethodSetDN(String methodSetDN)
    {
        m_methodSetDN = methodSetDN;
    }

    /**
     * This method sets the name of the method.
     *
     * @param  sName  The name of the method.
     *
     * @see    com.cordys.coe.tools.testtool.methods.IMethodInfo#setName(java.lang.String)
     */
    public void setName(String sName)
    {
        m_sName = sName;
    }

    /**
     * This method sets the namespace for this method.
     *
     * @param  sNamespace  The namespace for this method.
     *
     * @see    com.cordys.coe.tools.testtool.methods.IMethodInfo#setNamespace(java.lang.String)
     */
    public void setNamespace(String sNamespace)
    {
        m_sNamespace = sNamespace;
    }
}
