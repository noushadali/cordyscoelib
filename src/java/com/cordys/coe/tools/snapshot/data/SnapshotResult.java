package com.cordys.coe.tools.snapshot.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class wraps the data that is collected by the snapshot grabber.
 *
 * @author  localpg
 */
@XmlRootElement(name = "SnapshotResult", namespace = Constants.NS)
public class SnapshotResult
{
    /**
     * Holds the data that has been collected.
     */
    @XmlElement(name = "SnapshotData", namespace = Constants.NS)
    @XmlElementWrapper(name = "SnapshotDataList", namespace = Constants.NS)
    private List<SnapshotData> m_data = new ArrayList<SnapshotData>();

    /**
     * This method returns the snapshot data to include in the dump.
     *
     * @return  The snapshot data to include in the dump.
     */
    public List<SnapshotData> getSnapshotDataList()
    {
        return m_data;
    }

    /**
     * This method adds the given SnapshotData.
     *
     * @param  data  The SnapshotData to add.
     */
    public void addSnapshotData(SnapshotData data)
    {
        m_data.add(data);
    }
}
