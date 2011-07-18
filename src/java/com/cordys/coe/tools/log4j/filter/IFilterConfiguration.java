package com.cordys.coe.tools.log4j.filter;

/**
 * DOCUMENTME.
 *
 * @author  $author$
 */
public interface IFilterConfiguration
{
    /**
     * Holds all the levels.
     */
    String[] LEVEL_STRINGS = new String[]
                             {
                                 "(NO FILTER)", "TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL"
                             };

    /**
     * This method adds a child logger.
     *
     * @param  lcConfig  the logger config.
     */
    void addChild(IFilterConfiguration lcConfig);

    /**
     * This method cleans the configuration.
     */
    void clean();

    /**
     * This method gets the category.
     *
     * @return  The category.
     */
    String getCategory();

    /**
     * This method gets the child logger for a certain category.
     *
     * @param   sSubCategory  The sub category.
     *
     * @return  The child logger.
     */
    IFilterConfiguration getChildLogger(String sSubCategory);

    /**
     * This method returns in an array all child loggers.
     *
     * @return  All child loggers.
     */
    IFilterConfiguration[] getChildLoggers();

    /**
     * This method gets the level.
     *
     * @return  The level.
     *
     * @see     com.cordys.coe.tools.log4j.filter.IFilterConfiguration#getLevel()
     */
    String getLevel();

    /**
     * This method gets the parent LoggerConfig.
     *
     * @return  The parent LoggerConfig.
     */
    IFilterConfiguration getParentLogger();

    /**
     * This method gets the sub category.
     *
     * @return  The sub category.
     */
    String getSubCategory();

    /**
     * This method returns if this logger has children.
     *
     * @return  true if the config has child loggers. Otherwise false.
     */
    boolean hasChildren();

    /**
     * This method gets whether or not this logger has specific configuration.
     *
     * @return  Whether or not this logger has specific configuration.
     */
    boolean hasSpecificConfiguration();

    /**
     * This method sets wether or not the events should be captured for this category.
     *
     * @param  m_bCapture  Whether or not the events should be captured for this category.
     */
    void setCapture(boolean m_bCapture);

    /**
     * This method sets the capture property of the current object and all the sub-objects.
     *
     * @param  bCapture  The new value for the capture property.
     */
    void setCaptureAndAllSubs(boolean bCapture);

    /**
     * This method sets the category.
     *
     * @param  sCategory  The category.
     */
    void setCategory(String sCategory);

    /**
     * This method sets the level.
     *
     * @param  m_sLevel  The level.
     */
    void setLevel(String m_sLevel);

    /**
     * This method sets the log level for this category and copies this level to all children.
     *
     * @param  sLevel  The new threshold level.
     */
    void setLevelAndAllSubs(String sLevel);

    /**
     * This method gets whether or not the events should be captured for this category.
     *
     * @return  Whether or not the events should be captured for this category.
     */
    boolean shouldCapture();
}
