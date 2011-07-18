package com.cordys.coe.tools.log4j.filter;

import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * This class holds the details of the configuration for a certain category.
 *
 * @author  pgussow
 */
public class FilterConfiguration
    implements IFilterConfiguration
{
    /**
     * Holds whether or not to capture the events for this category.
     */
    private boolean m_bCapture;
    /**
     * Holds the parent logger.
     */
    private IFilterConfiguration m_lcParent;
    /**
     * Holds all the child loggers.
     */
    private LinkedHashMap<String, IFilterConfiguration> m_lhmChildLoggers = new LinkedHashMap<String, IFilterConfiguration>();
    /**
     * Holds the value for the category.
     */
    private String m_sCategory;
    /**
     * Holds the level.
     */
    private String m_sLevel;
    /**
     * Holds the sub category.
     */
    private String m_sSubCategory = "";

    /**
     * Creates a new LoggerConfig object.
     *
     * @param  sSubCategory  The category.
     * @param  lcParent      The parent configuration.
     */
    public FilterConfiguration(String sSubCategory, IFilterConfiguration lcParent)
    {
        this(sSubCategory, LEVEL_STRINGS[0], true, lcParent);
    }

    /**
     * Creates a new ESCLog4JFilter object.
     *
     * @param  sSubCategory  The category.
     * @param  sLevel        The level.
     * @param  bCapture      Whether or not this category should be captured.
     * @param  lcParent      The parent category.
     */
    public FilterConfiguration(String sSubCategory, String sLevel, boolean bCapture,
                               IFilterConfiguration lcParent)
    {
        m_lcParent = lcParent;
        m_sLevel = sLevel;
        m_bCapture = bCapture;

        if ((m_sLevel == null) || (m_sLevel.length() == 0))
        {
            m_sLevel = LEVEL_STRINGS[0];
        }

        if (lcParent == null)
        {
            // The root object.
            m_sSubCategory = "root";
            m_sCategory = "";
        }
        else
        {
            m_sSubCategory = sSubCategory;

            String sTemp = lcParent.getCategory();

            if (sTemp.length() > 0)
            {
                sTemp += ".";
            }
            m_sCategory = sTemp + m_sSubCategory;
        }
    }

    /**
     * This method adds a child logger.
     *
     * @param  lcConfig  the logger config.
     *
     * @see    com.cordys.coe.tools.log4j.filter.IFilterConfiguration#addChild(com.cordys.coe.tools.log4j.filter.IFilterConfiguration)
     */
    public void addChild(IFilterConfiguration lcConfig)
    {
        m_lhmChildLoggers.put(lcConfig.getSubCategory(), lcConfig);
    }

    /**
     * This method cleans the configuration.
     *
     * @see  com.cordys.coe.tools.log4j.filter.IFilterConfiguration#clean()
     */
    public void clean()
    {
        m_sLevel = null;
        m_bCapture = true;
    }

    /**
     * This method gets the category.
     *
     * @return  The category.
     *
     * @see     com.cordys.coe.tools.log4j.filter.IFilterConfiguration#getCategory()
     */
    public String getCategory()
    {
        return m_sCategory;
    }

    /**
     * This method gets the child logger for a certain category.
     *
     * @param   sSubCategory  The sub category.
     *
     * @return  The child logger.
     *
     * @see     com.cordys.coe.tools.log4j.filter.IFilterConfiguration#getChildLogger(java.lang.String)
     */
    public IFilterConfiguration getChildLogger(String sSubCategory)
    {
        IFilterConfiguration lcReturn = null;

        if (m_lhmChildLoggers.containsKey(sSubCategory))
        {
            lcReturn = m_lhmChildLoggers.get(sSubCategory);
        }

        return lcReturn;
    }

    /**
     * This method returns in an array all child loggers.
     *
     * @return  All child loggers.
     *
     * @see     com.cordys.coe.tools.log4j.filter.IFilterConfiguration#getChildLoggers()
     */
    public IFilterConfiguration[] getChildLoggers()
    {
        IFilterConfiguration[] alcReturn = new IFilterConfiguration[m_lhmChildLoggers.size()];
        int iCount = 0;

        for (Iterator<IFilterConfiguration> iChildLoggers = m_lhmChildLoggers.values().iterator();
                 iChildLoggers.hasNext();)
        {
            alcReturn[iCount] = iChildLoggers.next();
            iCount++;
        }

        return alcReturn;
    }

    /**
     * This method gets the level.
     *
     * @return  The level.
     *
     * @see     com.cordys.coe.tools.log4j.filter.IFilterConfiguration#getLevel()
     */
    public String getLevel()
    {
        return m_sLevel;
    }

    /**
     * This method gets the parent LoggerConfig.
     *
     * @return  The parent LoggerConfig.
     *
     * @see     com.cordys.coe.tools.log4j.filter.IFilterConfiguration#getParentLogger()
     */
    public IFilterConfiguration getParentLogger()
    {
        return m_lcParent;
    }

    /**
     * This method gets the sub category.
     *
     * @return  The sub category.
     *
     * @see     com.cordys.coe.tools.log4j.filter.IFilterConfiguration#getSubCategory()
     */
    public String getSubCategory()
    {
        return m_sSubCategory;
    }

    /**
     * This method returns if this logger has children.
     *
     * @return  true if the config has child loggers. Otherwise false.
     *
     * @see     com.cordys.coe.tools.log4j.filter.IFilterConfiguration#hasChildren()
     */
    public boolean hasChildren()
    {
        return m_lhmChildLoggers.size() > 0;
    }

    /**
     * This method gets whether or not this logger has specific configuration.
     *
     * @return  Whether or not this logger has specific configuration.
     *
     * @see     com.cordys.coe.tools.log4j.filter.IFilterConfiguration#hasSpecificConfiguration()
     */
    public boolean hasSpecificConfiguration()
    {
        return (((m_sLevel != null) && (m_sLevel.length() > 0)) || (m_bCapture == false));
    }

    /**
     * This method sets wether or not the events should be captured for this category.
     *
     * @param  m_bCapture  Whether or not the events should be captured for this category.
     *
     * @see    com.cordys.coe.tools.log4j.filter.IFilterConfiguration#setCapture(boolean)
     */
    public void setCapture(boolean m_bCapture)
    {
        this.m_bCapture = m_bCapture;
    }

    /**
     * This method sets the capture property of the current object and all the sub-objects.
     *
     * @param  bCapture  The new value for the capture property.
     *
     * @see    com.cordys.coe.tools.log4j.filter.IFilterConfiguration#setCaptureAndAllSubs(boolean)
     */
    public void setCaptureAndAllSubs(boolean bCapture)
    {
        setCapture(bCapture);

        for (Iterator<IFilterConfiguration> iChildren = m_lhmChildLoggers.values().iterator();
                 iChildren.hasNext();)
        {
            IFilterConfiguration fcChild = iChildren.next();
            fcChild.setCaptureAndAllSubs(bCapture);
        }
    }

    /**
     * This method sets the category.
     *
     * @param  sCategory  The category.
     *
     * @see    com.cordys.coe.tools.log4j.filter.IFilterConfiguration#setCategory(java.lang.String)
     */
    public void setCategory(String sCategory)
    {
        m_sCategory = sCategory;
    }

    /**
     * This method sets the level.
     *
     * @param  m_sLevel  The level.
     *
     * @see    com.cordys.coe.tools.log4j.filter.IFilterConfiguration#setLevel(java.lang.String)
     */
    public void setLevel(String m_sLevel)
    {
        this.m_sLevel = m_sLevel;
    }

    /**
     * This method sets the log level for this category and copies this level to all children.
     *
     * @param  sLevel  The new threshold level.
     *
     * @see    com.cordys.coe.tools.log4j.filter.IFilterConfiguration#setLevelAndAllSubs(java.lang.String)
     */
    public void setLevelAndAllSubs(String sLevel)
    {
        setLevel(sLevel);

        for (Iterator<IFilterConfiguration> iChildren = m_lhmChildLoggers.values().iterator();
                 iChildren.hasNext();)
        {
            IFilterConfiguration fcChild = iChildren.next();
            fcChild.setLevelAndAllSubs(sLevel);
        }
    }

    /**
     * This method gets whether or not the events should be captured for this category.
     *
     * @return  Whether or not the events should be captured for this category.
     *
     * @see     com.cordys.coe.tools.log4j.filter.IFilterConfiguration#shouldCapture()
     */
    public boolean shouldCapture()
    {
        return m_bCapture;
    }
}
