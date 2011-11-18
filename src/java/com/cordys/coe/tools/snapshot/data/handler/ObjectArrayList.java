package com.cordys.coe.tools.snapshot.data.handler;

import com.cordys.coe.tools.snapshot.data.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * This class wraps arrays.
 *
 * @author  localpg
 */
public class ObjectArrayList
{
    /**
     * Holds all the composite data objects that were retrieved.
     */
    @XmlElement(name = "Object", namespace = Constants.NS)
    @XmlElementWrapper(name = "ObjectList", namespace = Constants.NS)
    List<Object> m_allData = new ArrayList<Object>();

    /**
     * Creates a new ObjectList object.
     */
    public ObjectArrayList()
    {
    }

    /**
     * Creates a new ObjectList object.
     *
     * @param  data  The list of data objects.
     */
    public ObjectArrayList(Object[] data)
    {
        for (Object compositeData : data)
        {
            addObject(DataHandlerFactory.handleData(compositeData, null));
        }
    }

    /**
     * This method returns all the composite Data objects in this list.
     *
     * @return  All the composite data objects in this list.
     */
    public List<Object> getObjectList()
    {
        return m_allData;
    }

    /**
     * This method adds the composite data to the list.
     *
     * @param  compositeData  The composite data to add to the list.
     */
    public void addObject(Object compositeData)
    {
        m_allData.add(compositeData);
    }
}
