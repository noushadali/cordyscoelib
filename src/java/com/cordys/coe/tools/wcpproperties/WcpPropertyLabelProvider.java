package com.cordys.coe.tools.wcpproperties;

import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Table;

/**
 * This class provides the labels for the given objects.
 *
 * @author  pgussow
 */
class WcpPropertyLabelProvider extends LabelProvider
    implements ITableLabelProvider, ITableColorProvider, DisposeListener
{
    /**
     * The ID of the column for the property name.
     */
    private static final int COL_NAME = 0;
    /**
     * The ID of the column for the property value.
     */
    private static final int COL_VALUE = 1;
    /**
     * Holds the color yellow.
     */
    private Color m_cYellow = new Color(null, new RGB(255, 255, 0));

    /**
     * Constructor.
     *
     * @param  tParent  The parent component.
     */
    public WcpPropertyLabelProvider(Table tParent)
    {
        tParent.addDisposeListener(this);
    }

    /**
     * This method returns the background color to use.
     *
     * @param   oElement      The current object.
     * @param   iColumnIndex  The column index.
     *
     * @return  The color to use.
     *
     * @see     org.eclipse.jface.viewers.ITableColorProvider#getBackground(java.lang.Object, int)
     */
    public Color getBackground(Object oElement, int iColumnIndex)
    {
        if (oElement instanceof ActualProperty)
        {
            ActualProperty apProp = (ActualProperty) oElement;

            if (apProp.getWcpProperty() != null)
            {
                if (apProp.getWcpProperty().isMandatory())
                {
                    return m_cYellow;
                }
            }
        }

        return null;
    }

    /**
     * This method returns the image for the given column.
     *
     * @param   oElement      The element to get the image for.
     * @param   iColumnIndex  The column index.
     *
     * @return  Always null.
     */
    public Image getColumnImage(Object oElement, int iColumnIndex)
    {
        return null;
    }

    /**
     * This method returns the text for the given column.
     *
     * @param   oElement      The element to get the text for.
     * @param   iColumnIndex  The column index.
     *
     * @return  The proper text for the object.
     */
    public String getColumnText(Object oElement, int iColumnIndex)
    {
        String sReturn = "";

        if (oElement instanceof ActualProperty)
        {
            ActualProperty apProp = (ActualProperty) oElement;

            switch (iColumnIndex)
            {
                case COL_NAME:
                    sReturn = apProp.getName();
                    break;

                case COL_VALUE:
                    sReturn = apProp.getValue();
                    break;
            }
        }

        if (sReturn == null)
        {
            sReturn = "";
        }

        return sReturn;
    }

    /**
     * This method returns the foreground color to use.
     *
     * @param   oElement      The current object.
     * @param   iColumnIndex  The column index.
     *
     * @return  The color to use.
     *
     * @see     org.eclipse.jface.viewers.ITableColorProvider#getForeground(java.lang.Object, int)
     */
    public Color getForeground(Object oElement, int iColumnIndex)
    {
        return null;
    }

    /**
     * This method is called when the object is disposed.
     *
     * @param  deEvent  The dispose event.
     *
     * @see    org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
     */
    public void widgetDisposed(DisposeEvent deEvent)
    {
        m_cYellow.dispose();
    }
}
