package com.cordys.coe.tools.snapshot.data.handler;

import com.cordys.coe.tools.snapshot.data.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * Holds the list of composite data.
 *
 * @author  localpg
 */
public class CompositeDataList
{
    /**
     * Holds all the composite data objects that were retrieved.
     */
    @XmlElement(name = "CompositeData", namespace = Constants.NS)
    @XmlElementWrapper(name = "CompositeDataList", namespace = Constants.NS)
    List<CompositeData> m_allData = new ArrayList<CompositeData>();

    /**
     * Creates a new CompositeDataList object.
     */
    public CompositeDataList()
    {
    }

    /**
     * Creates a new CompositeDataList object.
     *
     * @param  data  The list of data objects.
     */
    public CompositeDataList(javax.management.openmbean.CompositeData[] data)
    {
        for (javax.management.openmbean.CompositeData compositeData : data)
        {
            addCompositeData(new CompositeData(compositeData));
        }
    }

    /**
     * This method returns all the composite Data objects in this list.
     *
     * @return  All the composite data objects in this list.
     */
    public List<CompositeData> getCompositeDataList()
    {
        return m_allData;
    }

    /**
     * This method adds the composite data to the list.
     *
     * @param  compositeData  The composite data to add to the list.
     */
    public void addCompositeData(CompositeData compositeData)
    {
        m_allData.add(compositeData);
    }
}
