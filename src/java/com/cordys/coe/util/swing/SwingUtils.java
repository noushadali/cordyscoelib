package com.cordys.coe.util.swing;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JDialog;

/**
 * This class holds some Swing utility methods.
 *
 * @author  pgussow
 */
public class SwingUtils
{
    /**
     * This method centers the given dialog.
     *
     * @param  dDialog  The dialog to center.
     */
    public static void centerDialog(JDialog dDialog)
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension labelSize = dDialog.getSize();
        dDialog.setLocation((screenSize.width / 2) - (labelSize.width / 2),
                            (screenSize.height / 2) - (labelSize.height / 2));
    }
}
