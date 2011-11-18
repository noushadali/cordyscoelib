package com.cordys.coe.tools.snapshot.data.handler;

import com.cordys.coe.tools.jmx.StringUtils;
import com.cordys.coe.tools.snapshot.data.Constants;
import com.cordys.coe.tools.snapshot.data.StackTraceElementWrapper;

import java.util.ArrayList;
import java.util.List;

import javax.management.openmbean.CompositeData;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

/**
 * This class is used to write up a report the dumpAllThreads response.
 *
 * @author  localpg
 */
@XmlType(propOrder = { "fullDump", "m_threads" })
public class DumpAllThreads
    implements ICustomDataHandler
{
    /**
     * Holds the textual representation of the full content.
     */
    private String m_fullDump;
    /**
     * Holds the list of thread information that was retrieved.
     */
    @XmlElement(name = "ThreadInfo", namespace = Constants.NS)
    @XmlElementWrapper(name = "ThreadInfoList", namespace = Constants.NS)
    private List<ThreadInfo> m_threads = new ArrayList<DumpAllThreads.ThreadInfo>();

    /**
     * This method returns the Stack Trace Elements of the exception.
     *
     * @return  The Stack Trace Elements of the exception.
     */
    public List<ThreadInfo> getThreadInfoList()
    {
        return m_threads;
    }

    /**
     * This method adds the given Stack Trace Elements of the exception.
     *
     * @param  data  The Stack Trace Elements of the exception to add.
     */
    public void addThreadInfo(ThreadInfo data)
    {
        m_threads.add(data);
    }

    /**
     * This method gets the textual representation of the full content.
     *
     * @return  The textual representation of the full content.
     */
    @XmlElement(name = "FullDump", namespace = Constants.NS)
    public String getFullDump()
    {
        return m_fullDump;
    }

    /**
     * This method sets the textual representation of the full content.
     *
     * @param  fullDump  The textual representation of the full content.
     */
    public void setFullDump(String fullDump)
    {
        m_fullDump = fullDump;
    }

    /**
     * @see  com.cordys.coe.tools.snapshot.data.handler.ICustomDataHandler#parse(java.lang.Object)
     */
    @Override public void parse(Object value)
    {
        StringBuilder threadDetails = new StringBuilder(1024);
        CompositeData[] allThreads = (CompositeData[]) value;

        for (CompositeData threadData : allThreads)
        {
            String threadName = StringUtils.toString(threadData.get("threadName"), false);
            String threadState = StringUtils.toString(threadData.get("threadState"), false);
            String suspended = StringUtils.toString(threadData.get("suspended"), false);
            String threadId = StringUtils.toString(threadData.get("threadId"), false);

            ThreadInfo ti = new ThreadInfo();
            ti.setName(threadName);
            ti.setSuspended(Boolean.parseBoolean(suspended));
            ti.setState(threadState);
            ti.setThreadID(threadId);

            threadDetails.append(threadName).append("(").append(threadState).append(", ").append(suspended).append(")\n");

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

                ti.addStackTraceElement(new StackTraceElementWrapper(className, methodName, filename, lineNumber,
                                                                     isNative));

                threadDetails.append(className);
                threadDetails.append(".");
                threadDetails.append(methodName);

                threadDetails.append("(");

                if (isNative)
                {
                    threadDetails.append("Native Method");
                }
                else
                {
                    threadDetails.append(filename);

                    if (lineNumber > 0)
                    {
                        threadDetails.append(":").append(lineNumber);
                    }
                }
                threadDetails.append(")\n");
            }

            // The thread info
            addThreadInfo(ti);

            threadDetails.append("\n");
        }

        setFullDump(threadDetails.toString());
    }

    /**
     * Wraps the information about a certain thread.
     */
    @XmlType(propOrder = { "name", "threadID", "state", "suspended", "m_stackTraceElements" })
    public static class ThreadInfo
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
    }
}
