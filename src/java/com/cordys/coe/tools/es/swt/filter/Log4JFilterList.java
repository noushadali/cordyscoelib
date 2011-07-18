package com.cordys.coe.tools.es.swt.filter;

import com.cordys.coe.tools.es.swt.Log4JFilterComposite;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * DOCUMENTME.
 *
 * @author  $author$
 */
public class Log4JFilterList
{
    /**
     * DOCUMENTME.
     */
    private HashMap<String, ESCLog4JFilter> m_lhmFilters = new HashMap<String, ESCLog4JFilter>(50);
    /**
     * DOCUMENTME.
     */
    private Set<ILog4JFilterListViewer> m_sChangeListeners = new HashSet<ILog4JFilterListViewer>();

    /**
     * Constructor.
     */
    public Log4JFilterList()
    {
        super();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  lflvViewer
     */
    public void addChangeListener(ILog4JFilterListViewer lflvViewer)
    {
        m_sChangeListeners.add(lflvViewer);
    }

    /**
     * Add a new filter the collection of filters.
     *
     * @param  sCategory  The category.
     * @param  sLevel     The level.
     * @param  bCapture   Whether or not to capture the event.
     */
    public void addFilter(String sCategory, String sLevel, boolean bCapture)
    {
        if (!m_lhmFilters.containsKey(sCategory))
        {
            ESCLog4JFilter elfFilter = new ESCLog4JFilter(sCategory, sLevel, bCapture);

            m_lhmFilters.put(sCategory, elfFilter);

            Iterator<ILog4JFilterListViewer> iterator = m_sChangeListeners.iterator();

            while (iterator.hasNext())
            {
                iterator.next().addLog4JFilter(elfFilter);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  elfFilter
     */
    public void filterChanged(ESCLog4JFilter elfFilter)
    {
        Iterator<ILog4JFilterListViewer> iterator = m_sChangeListeners.iterator();

        while (iterator.hasNext())
        {
            iterator.next().updateLog4JFilter(elfFilter);
        }
    }

    /**
     * This method returns the level for this category.
     *
     * @param   sCategory  The category.
     *
     * @return  The level for it.
     */
    public String getCategoryLevel(String sCategory)
    {
        String sReturn = Log4JFilterComposite.LEVEL_STRINGS[0];

        if (m_lhmFilters.containsKey(sCategory))
        {
            ESCLog4JFilter elfFilter = m_lhmFilters.get(sCategory);
            sReturn = elfFilter.getLevel();
        }

        return sReturn;
    }

    /**
     * This method returns the choices for the given property.
     *
     * @param   sProperty  The name of the property.
     *
     * @return  The array with choices.
     */
    public String[] getChoices(String sProperty)
    {
        if (Log4JFilterComposite.COL_LEVEL.equals(sProperty))
        {
            return Log4JFilterComposite.LEVEL_STRINGS;
        }
        else
        {
            return new String[] {};
        }
    }

    /**
     * Return the collection of filters.
     *
     * @return  DOCUMENTME
     */
    public HashMap<String, ESCLog4JFilter> getFilters()
    {
        return m_lhmFilters;
    }

    /**
     * This method returns whether or not this category should be captured or not.
     *
     * @param   sCategory  The category.
     *
     * @return  True is the category should be captured. If the category is not found true is
     *          returned as well.
     */
    public boolean isCapture(String sCategory)
    {
        boolean bReturn = true;

        if (m_lhmFilters.containsKey(sCategory))
        {
            ESCLog4JFilter elfFilter = m_lhmFilters.get(sCategory);
            bReturn = elfFilter.shouldCapture();
        }

        return bReturn;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  lflvViewer
     */
    public void removeChangeListener(ILog4JFilterListViewer lflvViewer)
    {
        m_sChangeListeners.remove(lflvViewer);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  elfFilter
     */
    public void removeFilter(ESCLog4JFilter elfFilter)
    {
        m_lhmFilters.remove(elfFilter);

        Iterator<ILog4JFilterListViewer> iterator = m_sChangeListeners.iterator();

        while (iterator.hasNext())
        {
            iterator.next().removeLog4JFilter(elfFilter);
        }
    }
}
