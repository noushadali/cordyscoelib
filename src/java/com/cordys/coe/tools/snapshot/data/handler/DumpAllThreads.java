package com.cordys.coe.tools.snapshot.data.handler;

import com.cordys.coe.tools.snapshot.data.Constants;
import com.cordys.coe.tools.snapshot.data.StackTraceElementWrapper;

import java.util.ArrayList;
import java.util.List;

import javax.management.openmbean.CompositeData;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class is used to write up a report the dumpAllThreads response.
 *
 * @author  localpg
 */
@XmlRootElement(name = "DumpAllThreads", namespace = Constants.NS)
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
    private List<ThreadInfo> m_threads = new ArrayList<ThreadInfo>();

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
            ThreadInfo ti = new ThreadInfo();
            ti.parseData(threadData);

            threadDetails.append(ti.getName()).append("(").append(ti.getState()).append(", ").append(ti.getSuspended())
                         .append(")\n");

            ArrayList<StackTraceElementWrapper> sts = ti.getStackTraceElementList();

            for (StackTraceElementWrapper st : sts)
            {
                threadDetails.append(st.getClassName());
                threadDetails.append(".");
                threadDetails.append(st.getMethodName());

                threadDetails.append("(");

                if (st.getNative())
                {
                    threadDetails.append("Native Method");
                }
                else
                {
                    threadDetails.append(st.getFilename());

                    if (st.getLineNumber() > 0)
                    {
                        threadDetails.append(":").append(st.getLineNumber());
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
}
