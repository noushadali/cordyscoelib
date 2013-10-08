package com.cordys.coe.tools.snapshot.data.collector;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.cordys.coe.tools.snapshot.data.Constants;

/**
 * This object stores the result of the datacollection of the JMX WSI collector.
 * 
 * @author pgussow
 */
@XmlRootElement(name = "JMXWSIResult", namespace = Constants.NS)
@XmlType(propOrder = { "installed", "m_wsis" })
public class JMXWSIResult
{
    /** Holds whether or not the JMX WebServiceInspector is actually installed. */
    private boolean m_isInstalled;
    /** Holds the web service interfaces that have been called. */
    @XmlElement(name = "WebServiceInterface", namespace = Constants.NS)
    @XmlElementWrapper(name = "WebServiceInterfaceList", namespace = Constants.NS)
    private List<WebServiceInterface> m_wsis = new ArrayList<WebServiceInterface>();

    /**
     * This method gets the web service interfaces that have been called.
     * 
     * @return The web service interfaces that have been called.
     */
    public List<WebServiceInterface> getWebServiceInterfaces()
    {
        return new ArrayList<WebServiceInterface>(m_wsis);
    }

    /**
     * This method adds the given wsi to he web service interfaces that have been called.
     * 
     * @param wsi The wsi to add.
     */
    public void addWebServiceInterface(WebServiceInterface wsi)
    {
        if (!m_wsis.contains(wsi))
        {
            m_wsis.add(wsi);
        }
    }

    /**
     * This method removes the given wsi from the web service interfaces that have been called.
     * 
     * @param wsi The wsi to remove.
     */
    public void removeWebServiceInterface(WebServiceInterface wsi)
    {
        if (m_wsis.contains(wsi))
        {
            m_wsis.remove(wsi);
        }
    }

    /**
     * This method clears the web service interfaces that have been called.
     */
    public void clearWebServiceInterfaces()
    {
        m_wsis.clear();
    }

    /**
     * This method gets whether or not the JMX WebServiceInspector is actually installed.
     * 
     * @return Whether or not the JMX WebServiceInspector is actually installed.
     */
    @XmlElement(name = "IsInstalled", namespace = Constants.NS)
    public boolean isInstalled()
    {
        return m_isInstalled;
    }

    /**
     * This method sets whether or not the JMX WebServiceInspector is actually installed.
     * 
     * @param isInstalled Whether or not the JMX WebServiceInspector is actually installed.
     */
    public void setInstalled(boolean isInstalled)
    {
        m_isInstalled = isInstalled;
    }
}
