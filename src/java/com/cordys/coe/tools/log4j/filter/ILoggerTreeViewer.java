package com.cordys.coe.tools.log4j.filter;

/**
 * This interface describes the logger tree viewer.
 *
 * @author  pgussow
 */
public interface ILoggerTreeViewer
{
    /**
     * This method adds the category to the logging.
     *
     * @param   sCategory  The full category.
     *
     * @return  The corresponding logger config.
     */
    IFilterConfiguration addCategory(String sCategory);

    /**
     * This method is called when the filter configuration has changed.
     *
     * @param  fcChanged  The changed filter configuration.
     */
    void filterChanged(IFilterConfiguration fcChanged);

    /**
     * This method returns the logger config for the specified category.
     *
     * @param   sCategory  The full category (com.cordys.something.Class).
     *
     * @return  The found logger config. If it could not be found it will be created.
     */
    IFilterConfiguration getFilterConfiguration(String sCategory);

    /**
     * This method gets the root logger config.
     *
     * @return  The root logger config.
     */
    IFilterConfiguration getRoot();

    /**
     * THis method refreshes the tree.
     */
    void refresh();

    /**
     * this method cleans the current category.
     *
     * @param  sCategory  The full category.
     */
    void removeCategorySpecificConfig(String sCategory);

    /**
     * This method sets the level of the category config.
     *
     * @param  sCategory  The category.
     * @param  sLevel     The level to set.
     */
    void setCategoryLevel(String sCategory, String sLevel);

    /**
     * This method sets the root level.
     *
     * @param  lcRoot  The root config.
     */
    void setRoot(IFilterConfiguration lcRoot);
}
