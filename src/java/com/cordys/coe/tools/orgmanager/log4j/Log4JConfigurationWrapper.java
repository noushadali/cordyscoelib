package com.cordys.coe.tools.orgmanager.log4j;

import com.cordys.coe.util.xml.dom.XMLHelper;
import com.cordys.coe.util.xml.dom.XPathHelper;

import java.util.ArrayList;

import java.util.LinkedHashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class is used to wrap the configuration.
 *
 * @author  pgussow
 */
public class Log4JConfigurationWrapper
{
    /**
     * Holds all appenders for this configuration file.
     */
    private LinkedHashMap<String, Appender> m_lhmAppenders = new LinkedHashMap<String, Appender>();
    /**
     * Holds all appenders for this configuration file.
     */
    private ArrayList<Category> m_alCategories = new ArrayList<Category>();
    /**
     * Holds all renderers for this configuration file.
     */
    private ArrayList<Renderer> m_alRenderers = new ArrayList<Renderer>();
    /**
     * Holds the root definition.
     */
    private Root m_rRoot;
    /**
     * Holds the debug value.
     */
    private String m_sDebug;
    /**
     * Holds the threshold.
     */
    private String m_sThreshold;

    /**
     * Creates a new Log4JConfigurationWrapper object.
     */
    public Log4JConfigurationWrapper()
    {
    }

    /**
     * Creates a new Log4JConfigurationWrapper object.
     *
     * @param  eLog4JConfiguration  The current configuration to analyze.
     */
    public Log4JConfigurationWrapper(Element eLog4JConfiguration)
    {
        parseConfiguration(eLog4JConfiguration);
    }

    /**
     * This method returns the appender with the given name.
     * 
     * @param sAppenderName The name of the appender.
     */
    public Appender getAppender(String sAppenderName) 
    {
        return m_lhmAppenders.get(sAppenderName);
    }

    /**
     * This method gets the appenders for this configuration.
     *
     * @return  The appenders for this configuration.
     */
    public ArrayList<Appender> getAppenders()
    {
        return new ArrayList<Appender>(m_lhmAppenders.values());
    }

    /**
     * This method gets the categories defined.
     *
     * @return  The categories defined.
     */
    public ArrayList<Category> getCategories()
    {
        return new ArrayList<Category>(m_alCategories);
    }

    /**
     * This method gets the debug value.
     *
     * @return  The debug value.
     */
    public String getDebug()
    {
        return m_sDebug;
    }

    /**
     * This method gets the renderers for this configuration.
     *
     * @return  The renderers for this configuration.
     */
    public ArrayList<Renderer> getRenderers()
    {
        return new ArrayList<Renderer>(m_alRenderers);
    }

    /**
     * This method gets the root definition.
     *
     * @return  The root definition.
     */
    public Root getRoot()
    {
        return m_rRoot;
    }

    /**
     * This method gets the threshold.
     *
     * @return  The threshold.
     */
    public String getThreshold()
    {
        return m_sThreshold;
    }

    /**
     * This method sets the appenders based on the given list.
     *
     * @param  alAppenders  The new list of appenders.
     */
    public void setAppenders(ArrayList<Appender> alAppenders)
    {
        m_lhmAppenders.clear();
        
        for (Appender a : alAppenders) 
        {
            m_lhmAppenders.put(a.getName(), a);
        }
    }

    /**
     * This method sets the categories based on the given list.
     *
     * @param  alCategories  The new list of categories.
     */
    public void setCategories(ArrayList<Category> alCategories)
    {
        m_alCategories = new ArrayList<Category>(alCategories);
    }

    /**
     * This method sets the debug value.
     *
     * @param  sDebug  The debug value.
     */
    public void setDebug(String sDebug)
    {
        m_sDebug = sDebug;
    }

    /**
     * This method sets the threshold.
     *
     * @param  sThreshold  The threshold.
     */
    public void setThreshold(String sThreshold)
    {
        m_sThreshold = sThreshold;
    }

    /**
     * This method writes the current configuration back to XML.
     *
     * @return  The creates element.
     */
    public Element toXML()
    {
        Element eReturn = null;

        Document dDoc = XMLHelper.createDocumentBuilder(true).newDocument();

        eReturn = dDoc.createElementNS("http://jakarta.apache.org/log4j/", "configuration");
        eReturn.setPrefix("log4j");
        eReturn.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:log4j",
                               "http://jakarta.apache.org/log4j/");

        if ((getDebug() != null) && (getDebug().length() > 0))
        {
            eReturn.setAttribute("debug", getDebug());
        }

        if ((getThreshold() != null) && (getThreshold().length() > 0))
        {
            eReturn.setAttribute("threshold", getThreshold());
        }

        for (Renderer r : m_alRenderers)
        {
            r.toXML(eReturn);
        }

        for (Appender a : m_lhmAppenders.values())
        {
            a.toXML(eReturn);
        }

        for (Category c : m_alCategories)
        {
            c.toXML(eReturn);
        }

        if (m_rRoot != null)
        {
            m_rRoot.toXML(eReturn);
        }

        return eReturn;
    }

    /**
     * This method parses the current configuration.
     *
     * @param  eLog4JConfiguration  The current configuration.
     */
    private void parseConfiguration(Element eLog4JConfiguration)
    {
        try
        {
            NodeList nl = XPathHelper.selectNodeList(eLog4JConfiguration, "./renderer");

            for (int iCount = 0; iCount < nl.getLength(); iCount++)
            {
                Renderer r = new Renderer((Element) nl.item(iCount));
                m_alRenderers.add(r);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error parsing configuration file", e);
        }

        try
        {
            NodeList nl = XPathHelper.selectNodeList(eLog4JConfiguration, "./appender");

            for (int iCount = 0; iCount < nl.getLength(); iCount++)
            {
                Appender a = new Appender((Element) nl.item(iCount));
                m_lhmAppenders.put(a.getName(), a);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error parsing configuration file (appenders)", e);
        }

        try
        {
            NodeList nl = XPathHelper.selectNodeList(eLog4JConfiguration, "./category");

            for (int iCount = 0; iCount < nl.getLength(); iCount++)
            {
                Category r = new Category((Element) nl.item(iCount));
                m_alCategories.add(r);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error parsing configuration file (category)", e);
        }

        try
        {
            Element eRoot = (Element) XPathHelper.selectSingleNode(eLog4JConfiguration, "./root");

            if (eRoot != null)
            {
                m_rRoot = new Root(eRoot);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error parsing configuration file (category)", e);
        }
    }
}
