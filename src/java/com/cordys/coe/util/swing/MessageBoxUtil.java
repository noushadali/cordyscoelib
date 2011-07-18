package com.cordys.coe.util.swing;

import com.cordys.coe.util.general.ExceptionUtil;

import java.awt.Frame;

import javax.swing.JOptionPane;

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
     * @param   fParent    The shell.
     * @param   sQuestion  The question to display.
     *
     * @return  true if the answer was yes. Otherwise false.
     */
    public static boolean showConfirmation(Frame fParent, String sQuestion)
    {
        boolean bReturn = false;

        if (fParent == null)
        {
            fParent = new Frame();
        }

        MessageDialog mb = new MessageDialog(fParent, "Confirmation", sQuestion, null,
                                             JOptionPane.QUESTION_MESSAGE);

        mb.setVisible(true);

        if (mb.getResult() == JOptionPane.OK_OPTION)
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
        showError((Frame) null, sMessage);
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
     * @param  fParent   The parent shell.
     * @param  sMessage  the message to display.
     */
    public static void showError(Frame fParent, String sMessage)
    {
        if (fParent == null)
        {
            fParent = new Frame();
        }

        MessageDialog mb = new MessageDialog(fParent, "Error", sMessage, null,
                                             JOptionPane.ERROR_MESSAGE);
        mb.setVisible(true);
    }

    /**
     * This method shows an error dialog to the end user.
     *
     * @param  fParent     The parent shell.
     * @param  sMessage    the message to display.
     * @param  tException  The exception to show.
     */
    public static void showError(Frame fParent, String sMessage, Throwable tException)
    {
        showError(fParent, sMessage,
                  (tException != null) ? ExceptionUtil.getStackTrace(tException) : null);
    }

    /**
     * This method shows an error dialog to the end user.
     *
     * @param  fParent   The parent shell.
     * @param  sMessage  the message to display.
     * @param  sDetail   The detail information.
     */
    public static void showError(Frame fParent, String sMessage, String sDetail)
    {
        if (fParent == null)
        {
            fParent = new Frame();
        }

        MessageDialog mb = new MessageDialog(fParent, "Error", sMessage, sDetail,
                                             JOptionPane.ERROR_MESSAGE);
        mb.setVisible(true);
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
     * @param  fParent   The parent shell.
     * @param  sMessage  The message to display.
     */
    public static void showInformation(Frame fParent, String sMessage)
    {
        if (fParent == null)
        {
            fParent = new Frame();
        }

        MessageDialog mb = new MessageDialog(fParent, "Information", sMessage, null,
                                             JOptionPane.INFORMATION_MESSAGE);
        mb.setVisible(true);
    }
}
