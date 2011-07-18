package com.cordys.coe.util.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

/**
 * This class shows a progress bar which in increased every 3 seconds.
 *
 * @author  pgussow
 */
public class InProgressDialog extends Dialog
{
    /**
     * DOCUMENTME.
     */
    private Label lDetailLabel;
    /**
     * Holds the actual progress bar.
     */
    private ProgressBar m_pbProgressBar;
    /**
     * Hold sthe dialog shell.
     */
    private Shell m_sShell;
    /**
     * Holds the detailed text.
     */
    private String m_sText;
    /**
     * Holds the title for the dialog.
     */
    private String m_sTitle;
    /**
     * Holds the updater thread.
     */
    private PBUpdater m_tPBUpdater;

    /**
     * Creates a new InProgressDialog object.
     *
     * @param  sParent  The parent shell.
     * @param  sTitle   The title for the dialog.
     * @param  sText    The detailed text.
     */
    public InProgressDialog(Shell sParent, String sTitle, String sText)
    {
        super(sParent);

        m_sTitle = sTitle;
        m_sText = sText;
    }

    /**
     * Main method.
     *
     * @param  saArguments  The commandline arguments.
     */
    public static void main(String[] saArguments)
    {
        try
        {
            Shell sShell = new Shell();
            InProgressDialog clf = new InProgressDialog(sShell, "Please wait",
                                                        "Making connection to cordys...\nPlease wait");
            clf.open();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * DOCUMENTME.
     */
    public void closeDialog()
    {
        m_sShell.close();

        // Stop the updater thread.
        m_tPBUpdater.interrupt();

        try
        {
            m_tPBUpdater.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * This method is called to open the dialog.
     *
     * @return  Always null.
     */
    public Object open()
    {
        createContents();

        m_sShell.pack();
        m_sShell.layout();

        // First figure out on which monitor I will be we started and which dimensions
        Display display = getParent().getDisplay();

        SWTUtils.centerShell(display, m_sShell);
        m_tPBUpdater = new PBUpdater(display);
        m_tPBUpdater.start();

        m_sShell.open();

        return null;
    }

    /**
     * This method sets the detail text.
     *
     * @param  sDetail  The detail text.
     */
    public void setDetail(String sDetail)
    {
        lDetailLabel.setText(m_sText);
    }

    /**
     * This method creates the components.
     */
    protected void createContents()
    {
        Shell parent = getParent();
        m_sShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
        m_sShell.setLayout(new GridLayout());
        m_sShell.setText(getText());
        m_sShell.setText(m_sTitle);
        m_sShell.setImage(SWTResourceManager.getImage(InProgressDialog.class, "cordys.gif"));

        final Composite composite = new Composite(m_sShell, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
        composite.setLayout(new GridLayout());

        lDetailLabel = new Label(composite, SWT.CENTER | SWT.WRAP);
        lDetailLabel.setText(m_sText);
        lDetailLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        m_pbProgressBar = new ProgressBar(composite, SWT.SMOOTH);

        final GridData gd_progressBar = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gd_progressBar.widthHint = 300;
        m_pbProgressBar.setLayoutData(gd_progressBar);
        m_pbProgressBar.setMaximum(60);
        //
    }

    /**
     * This class will update the progressbar every 500 milliseconds.
     *
     * @author  pgussow
     */
    private class PBUpdater extends Thread
    {
        /**
         * Holds the display to use.
         */
        private Display m_dDisplay = null;

        /**
         * Creates a new PBUpdater object.
         *
         * @param  dDisplay  The current display.
         */
        public PBUpdater(Display dDisplay)
        {
            super("PBUpdater");
            m_dDisplay = dDisplay;
        }

        /**
         * @see  java.lang.Thread#run()
         */
        @Override public void run()
        {
            while (true)
            {
                m_dDisplay.asyncExec(new Runnable()
                    {
                        public void run()
                        {
                            if (m_pbProgressBar.isDisposed())
                            {
                                return;
                            }

                            if (m_pbProgressBar.getMaximum() == m_pbProgressBar.getSelection())
                            {
                                m_pbProgressBar.setSelection(0);
                            }
                            else
                            {
                                m_pbProgressBar.setSelection(m_pbProgressBar.getSelection() + 1);
                            }
                        }
                    });

                try
                {
                    Thread.sleep(500);
                }
                catch (Exception e)
                {
                    break;
                }
            }
        }
    }
}
