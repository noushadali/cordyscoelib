package com.cordys.coe.util.swt;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

/**
 * This class contains utility methods for SWT.
 *
 * @author  pgussow
 */
public class SWTUtils
{
    /**
     * This method will center the given shell.
     *
     * @param  dDisplay  The current display.
     * @param  sShell    The shell to center.
     */
    public static void centerShell(Display dDisplay, Shell sShell)
    {
        Monitor[] am = dDisplay.getMonitors();
        Monitor mCurrent = am[SWTUtils.getMonitorIndex(Display.getDefault(), sShell)];

        // Calculate the center
        Rectangle rBounds = mCurrent.getBounds();
        Rectangle rShell = sShell.getBounds();
        rShell.x = Math.abs(rBounds.width - rShell.width) / 2;
        rShell.y = Math.abs(rBounds.height - rShell.height) / 2;

        sShell.setBounds(rShell);
    }

    /**
     * This method returns on which monitor the given control is rendered.
     *
     * @param   dDisplay  The current display.
     * @param   cControl  The control to check.
     *
     * @return  The index of the monitor.
     */
    public static int getMonitorIndex(Display dDisplay, Control cControl)
    {
        int iReturn = 0;

        Monitor[] amMonitors = dDisplay.getMonitors();

        for (int iCount = 0; iCount < amMonitors.length; iCount++)
        {
            Monitor mMonitor = amMonitors[iCount];

            if (mMonitor.getBounds().intersects(cControl.getBounds()) == true)
            {
                iReturn = iCount;
                break;
            }
        }

        return iReturn;
    }
    
    /**
     * This method disposes the children of a composite.
     *
     * @param  composite  The composite to dispose the children of.
     */
    public static void disposeChildren(Composite composite)
    {
        if ((composite != null) && !composite.isDisposed())
        {
            Control[] childs = composite.getChildren();

            if (childs.length > 0)
            {
                for (int i = 0; i < childs.length; i++)
                {
                    childs[i].dispose();
                }
            }
        }
    }
}
