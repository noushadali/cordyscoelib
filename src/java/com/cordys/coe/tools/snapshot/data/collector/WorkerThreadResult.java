package com.cordys.coe.tools.snapshot.data.collector;

import com.cordys.coe.tools.snapshot.data.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Wrapper class for the worker thread dispatchers that are found within the connector.
 *
 * @author  localpg
 */
@XmlRootElement(name = "WorkerThreadResult", namespace = Constants.NS)
public class WorkerThreadResult
{
    /**
     * Holds the results of the counter.
     */
    @XmlElement(name = "ThreadDispatcher", namespace = Constants.NS)
    @XmlElementWrapper(name = "ThreadDispatcherList", namespace = Constants.NS)
    private List<DispatcherInfo> m_results = new ArrayList<DispatcherInfo>();

    /**
     * This method returns the dispatcher info to include in the dump.
     *
     * @return  The dispatcher info to include in the dump.
     */
    public List<DispatcherInfo> getDispatcherInfoList()
    {
        return m_results;
    }

    /**
     * This method adds the given dispatcher info.
     *
     * @param  data  The dispatcher info to add.
     */
    public void addDispatcherInfo(DispatcherInfo data)
    {
        m_results.add(data);
    }
}
