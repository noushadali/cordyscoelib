package com.cordys.coe.tools.snapshot.data;

import com.cordys.coe.util.general.Util;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * This class wraps a throwable in a JAXB compliant way.
 *
 * @author  localpg
 */
public class ThrowableWrapper
{
    /**
     * Holds the message.
     */
    private String m_message;
    /**
     * Holds the cause of this exception.
     */
    private ThrowableWrapper m_cause;
    /**
     * Holds the data that has been collected.
     */
    @XmlElement(name = "StackTraceElement", namespace = Constants.NS)
    @XmlElementWrapper(name = "StackTrace", namespace = Constants.NS)
    private ArrayList<StackTraceElementWrapper> m_stackTraceElements = new ArrayList<StackTraceElementWrapper>();
    /**
     * Holds the full stack trace in human readble format.
     */
    private String m_fullTrace;
    /**
     * Holds the classname of the source exception.
     */
    private String m_className;

    /**
     * This method gets the classname of the source exception.
     *
     * @return  The classname of the source exception.
     */
    @XmlElement(name = "ClassName", namespace = Constants.NS)
    public String getClassName()
    {
        return m_className;
    }

    /**
     * This method sets the classname of the source exception.
     *
     * @param  className  The classname of the source exception.
     */
    public void setClassName(String className)
    {
        m_className = className;
    }

    /**
     * This method gets the full stack trace in human readble format.
     *
     * @return  The full stack trace in human readble format.
     */
    @XmlElement(name = "FullTrace", namespace = Constants.NS)
    public String getFullTrace()
    {
        return m_fullTrace;
    }

    /**
     * This method sets the full stack trace in human readble format.
     *
     * @param  fullTrace  The full stack trace in human readble format.
     */
    public void setFullTrace(String fullTrace)
    {
        m_fullTrace = fullTrace;
    }

    /**
     * This method gets the cause of this exception.
     *
     * @return  The cause of this exception.
     */
    @XmlElement(name = "Cause", namespace = Constants.NS)
    public ThrowableWrapper getCause()
    {
        return m_cause;
    }

    /**
     * This method sets the cause of this exception.
     *
     * @param  cause  The cause of this exception.
     */
    public void setCause(ThrowableWrapper cause)
    {
        m_cause = cause;
    }

    /**
     * This method gets the message.
     *
     * @return  The message.
     */
    @XmlElement(name = "Message", namespace = Constants.NS)
    public String getMessage()
    {
        return m_message;
    }

    /**
     * This method sets the message.
     *
     * @param  message  The message.
     */
    public void setMessage(String message)
    {
        m_message = message;
    }

    /**
     * This method returns the Stack Trace Elements of the exception.
     *
     * @return  The Stack Trace Elements of the exception.
     */
    public ArrayList<StackTraceElementWrapper> getStackTraceElementList()
    {
        return m_stackTraceElements;
    }

    /**
     * This method adds the given Stack Trace Elements of the exception.
     *
     * @param  data  The Stack Trace Elements of the exception to add.
     */
    public void addStackTraceElement(StackTraceElementWrapper data)
    {
        m_stackTraceElements.add(data);
    }

    /**
     * This method creates a throwable wrapper that can be written to XML via JAXB.
     *
     * @param   t  The throwable to wrap.
     *
     * @return  The created wrapper.
     */
    public static ThrowableWrapper getInstance(Throwable t)
    {
        ThrowableWrapper retVal = new ThrowableWrapper();

        // The full readble stack trace.
        retVal.setClassName(t.getClass().getName());
        retVal.setFullTrace(Util.getStackTrace(t));

        // Set the message
        retVal.setMessage(t.getMessage());

        // Wrap the stack trace.
        StackTraceElement[] trace = t.getStackTrace();

        if (trace != null)
        {
            for (StackTraceElement st : trace)
            {
                retVal.addStackTraceElement(StackTraceElementWrapper.getInstance(st));
            }
        }

        // Set the cause.
        Throwable cause = t.getCause();

        if (cause != null)
        {
            retVal.setCause(ThrowableWrapper.getInstance(cause));
        }

        return retVal;
    }
}
