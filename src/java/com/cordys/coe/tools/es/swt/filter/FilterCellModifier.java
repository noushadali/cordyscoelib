package com.cordys.coe.tools.es.swt.filter;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.jface.viewers.ICellModifier;

import org.eclipse.swt.widgets.TableItem;

/**
 * DOCUMENTME.
 *
 * @author  $author$
 */
public class FilterCellModifier
    implements ICellModifier
{
    /**
     * DOCUMENTME.
     */
    private ArrayList<String> m_alColumnNames;
    /**
     * DOCUMENTME.
     */
    private Log4JFilterList m_lflFilterList;

    /**
     * Constructor.
     *
     * @param  saColumnNames  an instance of a TableViewerExample
     * @param  lflFilterList  DOCUMENTME
     */
    public FilterCellModifier(String[] saColumnNames, Log4JFilterList lflFilterList)
    {
        super();
        m_alColumnNames = new ArrayList<String>(Arrays.asList(saColumnNames));
        m_lflFilterList = lflFilterList;
    }

    /**
     * @see  org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object, java.lang.String)
     */
    public boolean canModify(Object oElement, String sProperty)
    {
        return true;
    }

    /**
     * @see  org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object, java.lang.String)
     */
    public Object getValue(Object oElement, String sProperty)
    {
        // Find the index of the column
        int iColumnIndex = m_alColumnNames.indexOf(sProperty);

        Object oReturn = null;
        ESCLog4JFilter elfFilter = (ESCLog4JFilter) oElement;

        switch (iColumnIndex)
        {
            case 0: // CATEGORY
                oReturn = elfFilter.getCategory();
                break;

            case 1: // LEVEL

                String stringValue = elfFilter.getLevel();
                String[] choices = m_lflFilterList.getChoices(sProperty);
                int iIndex = choices.length - 1;

                while (!stringValue.equals(choices[iIndex]) && (iIndex > 0))
                {
                    --iIndex;
                }
                oReturn = new Integer(iIndex);
                break;

            case 2: // SHOULD CAPTURE
                oReturn = new Boolean(elfFilter.shouldCapture());
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

        TableItem tiItem = (TableItem) element;
        ESCLog4JFilter elfFilter = (ESCLog4JFilter) tiItem.getData();
        String sValue;

        switch (iColumnIndex)
        {
            case 0: // CATEGORY
                sValue = ((String) value).trim();
                elfFilter.setCategory(sValue);
                break;

            case 1: // LEVEL
                sValue = m_lflFilterList.getChoices(property)[((Integer) value).intValue()].trim();

                if (!elfFilter.getLevel().equals(sValue))
                {
                    elfFilter.setLevel(sValue);
                }
                break;

            case 2: // CAPTURE
                elfFilter.setCapture(((Boolean) value).booleanValue());
                break;

            default:
        }
        m_lflFilterList.filterChanged(elfFilter);
    }
}
