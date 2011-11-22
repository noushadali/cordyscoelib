package com.cordys.coe.tools.snapshot.data.handler;

import com.cordys.coe.tools.jmx.StringUtils;
import com.cordys.coe.tools.snapshot.data.Constants;
import com.cordys.coe.tools.snapshot.data.StackTraceElementWrapper;

import java.util.ArrayList;

import javax.management.openmbean.CompositeData;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

/**
 * Wraps the information about a certain thread.
 */
@XmlType(propOrder = { "name", "threadID", "state", "suspended", "stackTrace", "m_stackTraceElements" })
public class ThreadInfo
{
    /**
     * Holds the name of the thread.
     */
    private String m_name;
    /**
     * Holds the state of the thread.
     */
    private String m_state;
    /**
     * Holds the ID of the thread.
     */
    private String m_threadId;
    /**
     * Holds the data that has been collected.
     */
    @XmlElement(name = "StackTraceElement", namespace = Constants.NS)
    @XmlElementWrapper(name = "StackTrace", namespace = Constants.NS)
    private ArrayList<StackTraceElementWrapper> m_stackTraceElements = new ArrayList<StackTraceElementWrapper>();
    /**
     * Holds whether or not the thread is currently suspended.
     */
    private boolean m_suspended;

    /**
     * This method gets whether or not the thread is currently suspended.
     *
     * @return  Whether or not the thread is currently suspended.
     */
    @XmlElement(name = "Suspended", namespace = Constants.NS)
    public boolean getSuspended()
    {
        return m_suspended;
    }

    /**
     * This method sets whether or not the thread is currently suspended.
     *
     * @param  suspended  Whether or not the thread is currently suspended.
     */
    public void setSuspended(boolean suspended)
    {
        m_suspended = suspended;
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
     * This method gets the ID of the thread.
     *
     * @return  The ID of the thread.
     */
    @XmlElement(name = "ThreadID", namespace = Constants.NS)
    public String getThreadID()
    {
        return m_threadId;
    }

    /**
     * This method sets the ID of the thread.
     *
     * @param  threadId  The ID of the thread.
     */
    public void setThreadID(String threadId)
    {
        m_threadId = threadId;
    }

    /**
     * This method gets the state of the thread.
     *
     * @return  The state of the thread.
     */
    @XmlElement(name = "State", namespace = Constants.NS)
    public String getState()
    {
        return m_state;
    }

    /**
     * This method sets the state of the thread.
     *
     * @param  state  The state of the thread.
     */
    public void setState(String state)
    {
        m_state = state;
    }

    /**
     * This method gets the name of the thread.
     *
     * @return  The name of the thread.
     */
    @XmlElement(name = "Name", namespace = Constants.NS)
    public String getName()
    {
        return m_name;
    }

    /**
     * This method sets the name of the thread.
     *
     * @param  name  The name of the thread.
     */
    public void setName(String name)
    {
        m_name = name;
    }

    /**
     * Holds the stack trace in human readable format.
     */
    private String m_stackTrace;

    /**
     * This method gets the stack trace in human readable format.
     *
     * @return  The stack trace in human readable format.
     */
    @XmlElement(name = "StackTraceDisplay", namespace = Constants.NS)
    public String getStackTrace()
    {
        return m_stackTrace;
    }

    /**
     * This method sets the stack trace in human readable format.
     *
     * @param  stackTrace  The stack trace in human readable format.
     */
    public void setStackTrace(String stackTrace)
    {
        m_stackTrace = stackTrace;
    }

    /**
     * This method parses the composite data that holds teh information for the threads.
     *
     * @param  threadData  The thread data.
     */
    public void parseData(CompositeData threadData)
    {
        String threadName = StringUtils.toString(threadData.get("threadName"), false);
        String threadState = StringUtils.toString(threadData.get("threadState"), false);
        String suspended = StringUtils.toString(threadData.get("suspended"), false);
        String threadId = StringUtils.toString(threadData.get("threadId"), false);

        setName(threadName);
        setSuspended(Boolean.parseBoolean(suspended));
        setState(threadState);
        setThreadID(threadId);

        // Now get the stack trace
        CompositeData[] elements = (CompositeData[]) threadData.get("stackTrace");

        for (Object temp : elements)
        {
            CompositeData stackTraceElement = (CompositeData) temp;

            String className = StringUtils.toString(stackTraceElement.get("className"), false);
            String methodName = StringUtils.toString(stackTraceElement.get("methodName"), false);
            boolean isNative = (Boolean) stackTraceElement.get("nativeMethod");
            String filename = StringUtils.toString(stackTraceElement.get("fileName"), false);
            int lineNumber = (Integer) stackTraceElement.get("lineNumber");

            addStackTraceElement(new StackTraceElementWrapper(className, methodName, filename, lineNumber, isNative));
        }

        setStackTrace(toString());
    }

    /**
     * @see  java.lang.Object#toString()
     */
    @Override public String toString()
    {
        StringBuilder sb = new StringBuilder(1024);

        sb.append(getName()).append("(").append(getState()).append(", ").append(getSuspended()).append(", ").append(getThreadID()).append(")\n");

        ArrayList<StackTraceElementWrapper> sts = getStackTraceElementList();

        for (StackTraceElementWrapper st : sts)
        {
            sb.append(st.getClassName());
            sb.append(".");
            sb.append(st.getMethodName());

            sb.append("(");

            if (st.getNative())
            {
                sb.append("Native Method");
            }
            else
            {
                sb.append(st.getFilename());

                if (st.getLineNumber() > 0)
                {
                    sb.append(":").append(st.getLineNumber());
                }
            }
            sb.append(")\n");
        }

        return sb.toString();
    }
}
