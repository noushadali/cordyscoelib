package com.cordys.coe.tools.wcpproperties;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import org.eclipse.swt.SWT;

/**
 * This class takes care of the sorting of the properties.
 *
 * @author  pgussow
 */
public class WcpPropertySorter extends ViewerSorter
{
    /**
     * Identifies to sort by the column 'name'.
     */
    public static final String SORT_PROPERTY_NAME = "name"; // $NON-NLS-1$
    /**
     * Identifies to sort by the column 'value'.
     */
    public static final String SORT_PROPERTY_VALUE = "value"; // $NON-NLS-1$
    /**
     * Holds the current direction for sorting.
     */
    private int m_iDirection = SWT.DOWN;
    /**
     * Holds the current sort column.
     */
    private String m_sColumn = null;

    /**
     * Creates a new WcpPropertySorter object.
     *
     * @param  sColumn     The column to sort by.
     * @param  iDirection  The directorion for sorting.
     */
    public WcpPropertySorter(String sColumn, int iDirection)
    {
        super();
        m_sColumn = sColumn;
        m_iDirection = iDirection;
    }

    /**
     * This method is called to sort the properties.
     *
     * @param   vViewer  The current viewer.
     * @param   oFirst   The first obejct.
     * @param   oSecond  The second object.
     *
     * @return  0 if they are equal, negative int when oFirst is greater then oSecond and a positive
     *          int when oSecond is greater then oFirst.
     */
    @Override public int compare(Viewer vViewer, Object oFirst, Object oSecond)
    {
        int iReturn = 0;

        if (oFirst instanceof ActualProperty)
        {
            ActualProperty apFirst = (ActualProperty) oFirst;

            if (oSecond instanceof ActualProperty)
            {
                ActualProperty apSecond = (ActualProperty) oSecond;

                if (m_sColumn.equals(SORT_PROPERTY_NAME))
                {
                    iReturn = apFirst.getName().compareTo(apSecond.getName());
                }
                else if (m_sColumn.equals(SORT_PROPERTY_VALUE))
                {
                    iReturn = apFirst.getValue().compareTo(apSecond.getValue());
                }
                else
                {
                    iReturn = apFirst.getName().compareTo(apSecond.getName());
                }

                if (m_iDirection == SWT.DOWN)
                {
                    iReturn = iReturn * -1;
                }
            }
        }

        return iReturn;
    }
}
