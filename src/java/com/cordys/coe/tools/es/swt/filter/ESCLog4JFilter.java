package com.cordys.coe.tools.es.swt.filter;

/**
 * Holds the details for the filter.
 *
 * @author  pgussow
 */
public class ESCLog4JFilter
{
    /**
     * Holds whether or not to capture the events for this category.
     */
    private boolean m_bCapture;
    /**
     * Holds the value for the category.
     */
    private String m_sCategory;
    /**
     * Holds the level.
     */
    private String m_sLevel;

    /**
     * Creates a new ESCLog4JFilter object.
     *
     * @param  sCategory  The category.
     * @param  sLevel     The level.
     * @param  bCapture   Whether or not this category should be captured.
     */
    public ESCLog4JFilter(String sCategory, String sLevel, boolean bCapture)
    {
        m_sCategory = sCategory;
        m_sLevel = sLevel;

        if ((m_sLevel == null) || (m_sLevel.length() == 0))
        {
            m_sLevel = "(NO FILTER)";
        }
        m_bCapture = bCapture;
    }

    /**
     * This method gets the category.
     *
     * @return  The category.
     */
    public String getCategory()
    {
        return m_sCategory;
    }

    /**
     * This method gets the level.
     *
     * @return  The level.
     */
    public String getLevel()
    {
        return m_sLevel;
    }

    /**
     * This method sets wether or not the events should be captured for this category.
     *
     * @param  m_bCapture  Whether or not the events should be captured for this category.
     */
    public void setCapture(boolean m_bCapture)
    {
        this.m_bCapture = m_bCapture;
    }

    /**
     * This method sets the category.
     *
     * @param  m_sCategory  The category.
     */
    public void setCategory(String m_sCategory)
    {
        this.m_sCategory = m_sCategory;
    }

    /**
     * This method sets the level.
     *
     * @param  m_sLevel  The level.
     */
    public void setLevel(String m_sLevel)
    {
        this.m_sLevel = m_sLevel;
    }

    /**
     * This method gets whether or not the events should be captured for this category.
     *
     * @return  Whether or not the events should be captured for this category.
     */
    public boolean shouldCapture()
    {
        return m_bCapture;
    }
}
