package com.cordys.coe.tools.orgmanager;

import java.awt.Component;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JTable;

import javax.swing.table.DefaultTableCellRenderer;

/**
 * This cell renderer can be used to render the status of a SOAP processor.
 *
 * @author  pgussow
 */
public class ProcessorStatusRenderer extends DefaultTableCellRenderer
{
    /**
     * @see  javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,java.lang.Object,
     *       boolean, boolean, int, int)
     */
    @Override public Component getTableCellRendererComponent(JTable tbltable, Object oValue,
                                                             boolean bIsSelected, boolean bHasFocus,
                                                             int iRow, int iColumn)
    {
        String sStatus = "Stopped";

        if (oValue != null)
        {
            sStatus = oValue.toString();
        }

        // Call the super to make sure the selection is displayed properly.
        super.getTableCellRendererComponent(tbltable, sStatus, bIsSelected, bHasFocus, iRow,
                                            iColumn);

        String sImage = "soapprocessorstopped.gif";

        if ("Started".equals(sStatus))
        {
            sImage = "soapprocessorstarted.gif";
        }
        else if ("Starting".equals(sStatus))
        {
            sImage = "soapprocessorstarting.gif";
        }
        else if ("Configuration Error".equals(sStatus))
        {
            sImage = "soapprocessorerror.gif";
        }

        // Set the proper icon.
        setIcon(new ImageIcon(Toolkit.getDefaultToolkit().createImage(OrganizationManager.class
                                                                      .getResource(sImage))));

        return this;
    }
}
