package com.cordys.coe.tools.log4j.filter;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.jface.viewers.ICellModifier;

import org.eclipse.swt.widgets.TreeItem;

/**
 * This is the cell modifier for the log level.
 *
 * @author  pgussow
 */
class FilterCellModifier
    implements ICellModifier
{
    /**
     * Holds al lthe column names.
     */
    private ArrayList<String> m_alColumnNames;
    /**
     * Holds the list of the current filters.
     */
    private ILoggerTreeViewer m_lflFilterList;

    /**
     * Constructor.
     *
     * @param  saColumnNames    an instance of a TableViewerExample
     * @param  ltvLoggerViewer  The LoggerViewer.
     */
    public FilterCellModifier(String[] saColumnNames, ILoggerTreeViewer ltvLoggerViewer)
    {
        super();
        m_alColumnNames = new ArrayList<String>(Arrays.asList(saColumnNames));
        m_lflFilterList = ltvLoggerViewer;
    }

    /**
     * @see  org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object, java.lang.String)
     */
    public boolean canModify(Object oElement, String sProperty)
    {
        if ("Level".equals(sProperty))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * @see  org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object, java.lang.String)
     */
    public Object getValue(Object oElement, String sProperty)
    {
        // Find the index of the column
        int iColumnIndex = m_alColumnNames.indexOf(sProperty);

        Object oReturn = null;
        IFilterConfiguration fcFilter = (IFilterConfiguration) oElement;

        switch (iColumnIndex)
        {
            case 0: // CATEGORY
                oReturn = fcFilter.getCategory();
                break;

            case 1: // LEVEL

                String stringValue = fcFilter.getLevel();
                String[] choices = IFilterConfiguration.LEVEL_STRINGS;
                int iIndex = choices.length - 1;

                while (!stringValue.equals(choices[iIndex]) && (iIndex > 0))
                {
                    --iIndex;
                }
                oReturn = new Integer(iIndex);
                break;

            default:
                oReturn = "";
        }
        return oReturn;
    }

    /**
     * @see  org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object, java.lang.String,
     *       java.lang.Object)
     */
    public void modify(Object element, String property, Object value)
    {
        // Find the index of the column
        int iColumnIndex = m_alColumnNames.indexOf(property);

        TreeItem tiItem = (TreeItem) element;
        IFilterConfiguration elfFilter = (IFilterConfiguration) tiItem.getData();
        String sValue;

        switch (iColumnIndex)
        {
            case 0: // CATEGORY
                sValue = ((String) value).trim();
                elfFilter.setCategory(sValue);
                break;

            case 1: // LEVEL
                sValue = IFilterConfiguration.LEVEL_STRINGS[((Integer) value).intValue()].trim();

                if (!elfFilter.getLevel().equals(sValue))
                {
                    elfFilter.setLevel(sValue);
                }
                break;

            default:
        }
        m_lflFilterList.filterChanged(elfFilter);
    }
}
