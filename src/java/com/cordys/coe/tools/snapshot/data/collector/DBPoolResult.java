package com.cordys.coe.tools.snapshot.data.collector;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.cordys.coe.tools.snapshot.data.Constants;

/**
 * This class contains the result of the of the collection of the DB Connection pool information.
 * 
 * @author localpg
 */
@XmlRootElement(name = "DBPoolResult", namespace = Constants.NS)
public class DBPoolResult
{
    /**
     * The pool information.
     */
    private List<DBConnectionPoolInfo> m_results = new ArrayList<DBConnectionPoolInfo>();

    /**
     * This method returns the DB Connection Pool Info to include in the dump.
     * 
     * @return The DB Connection Pool Info to include in the dump.
     */
    @XmlElement(name = "DBConnectionPoolInfo", namespace = Constants.NS)
    @XmlElementWrapper(name = "DBConnectionPoolInfoList", namespace = Constants.NS)
    public List<DBConnectionPoolInfo> getDBConnectionPoolInfoList()
    {
        return m_results;
    }

    /**
     * This method adds the given DB Connection Pool Info.
     * 
     * @param data The DB Connection Pool Info to add.
     */
    public void addDBConnectionPoolInfo(DBConnectionPoolInfo data)
    {
        m_results.add(data);
    }
}
