package com.cordys.coe.util.swt;

import com.cordys.coe.util.general.ExceptionUtil;

import org.eclipse.jface.dialogs.IDialogConstants;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

/**
 * SWT Message Box util functions.
 *
 * @author  pgussow
 */
public class MessageBoxUtil
{
    /**
     * This method shows a confirmation dialog.
     *
     * @param   sQuestion  The question to display.
     *
     * @return  true if the answer was yes. Otherwise false.
     */
    public static boolean showConfirmation(String sQuestion)
    {
        return showConfirmation(null, sQuestion);
    }

    /**
     * This method shows a confirmation dialog.
     *
     * @param   sShell     The shell.
     * @param   sQuestion  The question to display.
     *
     * @return  true if the answer was yes. Otherwise false.
     */
    public static boolean showConfirmation(Shell sShell, String sQuestion)
    {
        boolean bReturn = false;

        if (sShell == null)
        {
            sShell = new Shell();
        }

        MessageDialog mb = new MessageDialog(sShell, "Confirmation", sQuestion, null,
                                             SWT.ICON_QUESTION);

        if (mb.open() == IDialogConstants.OK_ID)
        {
            bReturn = true;
        }

        return bReturn;
    }

    /**
     * This method shows an error dialog to the end user.
     *
     * @param  sMessage  the message to display.
     */
    public static void showError(String sMessage)
    {
        showError((Shell) null, sMessage);
    }

    /**
     * This method shows an error dialog to the end user.
     *
     * @param  sMessage    the message to display.
     * @param  tException  The exception to show.
     */
    public static void showError(String sMessage, Throwable tException)
    {
        showError(null, sMessage, tException);
    }

    /**
     * This method shows an error dialog to the end user.
     *
     * @param  sMessage  the message to display.
     * @param  sDetail   The detail information.
     */
    public static void showError(String sMessage, String sDetail)
    {
        showError(null, sMessage, sDetail);
    }

    /**
     * This method shows an error dialog to the end user.
     *
     * @param  sShell    The parent shell.
     * @param  sMessage  the message to display.
     */
    public static void showError(Shell sShell, String sMessage)
    {
        if (sShell == null)
        {
            sShell = new Shell();
        }

        MessageDialog mb = new MessageDialog(sShell, "Error", sMessage, null, SWT.ICON_ERROR);
        mb.open();
    }

    /**
     * This method shows an error dialog to the end user.
     *
     * @param  sShell      The parent shell.
     * @param  sMessage    the message to display.
     * @param  tException  The exception to show.
     */
    public static void showError(Shell sShell, String sMessage, Throwable tException)
    {
        showError(sShell, sMessage,
                  (tException != null) ? ExceptionUtil.getStackTrace(tException) : null);
    }

    /**
     * This method shows an error dialog to the end user.
     *
     * @param  sShell    The parent shell.
     * @param  sMessage  the message to display.
     * @param  sDetail   The detail information.
     */
    public static void showError(Shell sShell, String sMessage, String sDetail)
    {
        if (sShell == null)
        {
            sShell = new Shell();
        }

        MessageDialog mb = new MessageDialog(sShell, "Error", sMessage, sDetail, SWT.ICON_ERROR);
        mb.open();
    }

    /**
     * This method shows an information dialog to the end user.
     *
     * @param  sMessage  The message to display.
     */
    public static void showInformation(String sMessage)
    {
        showInformation(null, sMessage);
    }

    /**
     * This method shows an information dialog to the end user.
     *
     * @param  sShell    The parent shell.
     * @param  sMessage  The message to display.
     */
    public static void showInformation(Shell sShell, String sMessage)
    {
        if (sShell == null)
        {
            sShell = new Shell();
        }

        MessageDialog mb = new MessageDialog(sShell, "Information", sMessage, null,
                                             SWT.ICON_INFORMATION);
        mb.open();
    }
}
