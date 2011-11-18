package com.cordys.coe.tools.snapshot.data;

import javax.xml.bind.annotation.XmlElement;

/**
 * This class wraps the data from the stack trace element.
 *
 * @author  localpg
 */
public class StackTraceElementWrapper
{
    /**
     * Holds the name of the class containing the execution point.
     */
    private String m_className;
    /**
     * Holds the name of the source file containing the execution point represented by this stack trace element.
     */
    private String m_filename;
    /**
     * Holds the line number of the source line containing the execution point represented by this stack trace element.
     */
    private int m_lineNumber;
    /**
     * Holds the line number of the source line containing the execution point represented by this stack trace element.
     */
    private String m_methodName;
    /**
     * Holds whether or not the method is a native method.
     */
    private boolean m_isNative;

    /**
     * Creates a new StackTraceElementWrapper object.
     */
    public StackTraceElementWrapper()
    {
    }

    /**
     * Creates a new StackTraceElementWrapper object.
     *
     * @param  className   The name of the class containing the execution point.
     * @param  methodName  The line number of the source line containing the execution point represented by this stack
     *                     trace element.
     * @param  filename    The name of the source file containing the execution point represented by this stack trace
     *                     element.
     * @param  lineNumber  The line number of the source line containing the execution point represented by this stack
     *                     trace element.
     * @param  isNative    Whether or not the method is a native method.
     */
    public StackTraceElementWrapper(String className, String methodName, String filename, int lineNumber,
                                    boolean isNative)
    {
        m_className = className;
        m_filename = filename;
        m_lineNumber = lineNumber;
        m_methodName = methodName;
        m_isNative = isNative;
    }

    /**
     * This method gets the line number of the source line containing the execution point represented by this stack
     * trace element.
     *
     * @return  The line number of the source line containing the execution point represented by this stack trace
     *          element.
     */
    @XmlElement(name = "Method", namespace = Constants.NS)
    public String getMethodName()
    {
        return m_methodName;
    }

    /**
     * This method sets the line number of the source line containing the execution point represented by this stack
     * trace element.
     *
     * @param  methodName  The line number of the source line containing the execution point represented by this stack
     *                     trace element.
     */
    public void setMethodName(String methodName)
    {
        m_methodName = methodName;
    }

    /**
     * This method gets whether or not the method is a native method.
     *
     * @return  Whether or not the method is a native method.
     */
    @XmlElement(name = "Native", namespace = Constants.NS)
    public boolean getNative()
    {
        return m_isNative;
    }

    /**
     * This method sets whether or not the method is a native method.
     *
     * @param  isNative  Whether or not the method is a native method.
     */
    public void setNative(boolean isNative)
    {
        m_isNative = isNative;
    }

    /**
     * This method gets the line number of the source line containing the execution point represented by this stack
     * trace element.
     *
     * @return  The line number of the source line containing the execution point represented by this stack trace
     *          element.
     */
    @XmlElement(name = "Line", namespace = Constants.NS)
    public int getLineNumber()
    {
        return m_lineNumber;
    }

    /**
     * This method sets the line number of the source line containing the execution point represented by this stack
     * trace element.
     *
     * @param  lineNumber  The line number of the source line containing the execution point represented by this stack
     *                     trace element.
     */
    public void setLineNumber(int lineNumber)
    {
        m_lineNumber = lineNumber;
    }

    /**
     * This method gets the name of the source file containing the execution point represented by this stack trace
     * element.
     *
     * @return  The name of the source file containing the execution point represented by this stack trace element.
     */
    @XmlElement(name = "Filename", namespace = Constants.NS)
    public String getFilename()
    {
        return m_filename;
    }

    /**
     * This method sets the name of the source file containing the execution point represented by this stack trace
     * element.
     *
     * @param  filename  The name of the source file containing the execution point represented by this stack trace
     *                   element.
     */
    public void setFilename(String filename)
    {
        m_filename = filename;
    }

    /**
     * This method gets the name of the class containing the execution point.
     *
     * @return  The name of the class containing the execution point.
     */
    @XmlElement(name = "ClassName", namespace = Constants.NS)
    public String getClassName()
    {
        return m_className;
    }

    /**
     * This method sets the name of the class containing the execution point.
     *
     * @param  className  The name of the class containing the execution point.
     */
    public void setClassName(String className)
    {
        m_className = className;
    }

    /**
     * This method returns an instance of the stack trace element wrapper based on the given stack trace element.
     *
     * @param   st  The stack trace element.
     *
     * @return  The stack trace element wrapper that is created.
     */
    public static StackTraceElementWrapper getInstance(StackTraceElement st)
    {
        StackTraceElementWrapper retVal = new StackTraceElementWrapper();

        retVal.setClassName(st.getClassName());
        retVal.setFilename(st.getFileName());
        retVal.setLineNumber(st.getLineNumber());
        retVal.setMethodName(st.getMethodName());

        return retVal;
    }
}
