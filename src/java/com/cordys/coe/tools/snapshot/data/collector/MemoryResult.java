package com.cordys.coe.tools.snapshot.data.collector;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.cordys.coe.tools.snapshot.data.Constants;

/**
 * This class contains the result of the of the collection of the memory information that is exposed by the JVM.
 * 
 * @author localpg
 */
@XmlRootElement(name = "MemoryResult", namespace = Constants.NS)
@XmlType(propOrder = { "heap", "nonHeap", "m_results" })
public class MemoryResult
{

    /**
     * Holds the main heap memory details.
     */
    private MemoryDetail m_heap;
    /**
     * Holds the non heap details.
     */
    private MemoryDetail m_nonHeap;
    /**
     * Holds the results of the counter.
     */
    @XmlElement(name = "MemoryDetail", namespace = Constants.NS)
    @XmlElementWrapper(name = "MemoryDetailList", namespace = Constants.NS)
    private List<MemoryDetail> m_results = new ArrayList<MemoryDetail>();

    /**
     * This method gets the non heap details.
     * 
     * @return The non heap details.
     */
    @XmlElement(name = "NonHeap", namespace = Constants.NS)
    public MemoryDetail getNonHeap()
    {
        return m_nonHeap;
    }

    /**
     * This method sets the non heap details.
     * 
     * @param nonHeap The non heap details.
     */
    public void setNonHeap(MemoryDetail nonHeap)
    {
        m_nonHeap = nonHeap;
    }

    /**
     * This method gets the main heap memory details.
     * 
     * @return The main heap memory details.
     */
    @XmlElement(name = "Heap", namespace = Constants.NS)
    public MemoryDetail getHeap()
    {
        return m_heap;
    }

    /**
     * This method sets the main heap memory details.
     * 
     * @param heap The main heap memory details.
     */
    public void setHeap(MemoryDetail heap)
    {
        m_heap = heap;
    }

    /**
     * This method returns the memory details to include in the dump.
     * 
     * @return The memory details to include in the dump.
     */
    public List<MemoryDetail> getMemoryDetailList()
    {
        return m_results;
    }

    /**
     * This method adds the given memory details.
     * 
     * @param data The memory details to add.
     */
    public void addMemoryDetail(MemoryDetail data)
    {
        m_results.add(data);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        if (m_heap != null)
        {
            return m_heap.toString();
        }

        return super.toString();
    }
}
