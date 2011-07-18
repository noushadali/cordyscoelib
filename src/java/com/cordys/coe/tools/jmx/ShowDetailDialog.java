package com.cordys.coe.tools.jmx;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog to show the details of a text element.
 *
 * @author  pgussow
 */
public class ShowDetailDialog extends Dialog
{
    /**
     * Holds whether or not editing is enabled.
     */
    private boolean m_bEnabled = true;
    /**
     * Holds the current value for the text.
     */
    private String m_sCurrentText = "";
    /**
     * Holds the text area holding the text to be edited.
     */
    private Text m_tDetails;

    /**
     * Creates a new ShowDetailDialog object.
     *
     * @param  sParent  The parent shell.
     */
    public ShowDetailDialog(Shell sParent)
    {
        super(sParent);
    }

    /**
     * This method gets the current text.
     *
     * @return  The current text.
     */
    public String getCurrentText()
    {
        return m_sCurrentText;
    }

    /**
     * This method gets whether or not the edit should be enabled.
     *
     * @return  Whether or not the edit should be enabled.
     */
    public boolean getEnabled()
    {
        return m_bEnabled;
    }

    /**
     * This method sets the current text.
     *
     * @param  sCurrentText  The current text.
     */
    public void setCurrentText(String sCurrentText)
    {
        m_sCurrentText = sCurrentText;

        if (m_tDetails != null)
        {
            m_tDetails.setText(sCurrentText);
        }
    }

    /**
     * This method sets wether or not the edit should be enabled.
     *
     * @param  bEnabled  Whether or not the edit should be enabled.
     */
    public void setEnabled(boolean bEnabled)
    {
        m_bEnabled = bEnabled;

        if (m_tDetails != null)
        {
            m_tDetails.setEditable(bEnabled);
        }
    }

    /**
     * @see  org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override protected void configureShell(Shell newShell)
    {
        super.configureShell(newShell);
        newShell.setText("Details");
    }

    /**
     * Create contents of the button bar.
     *
     * @param  parent  The parent composite.
     */
    @Override protected void createButtonsForButtonBar(Composite parent)
    {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);

        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    /**
     * Create contents of the dialog. It creates the image and the label and if needed the detail
     * part as well.
     *
     * @param   parent  The parent composite.
     *
     * @return  The control for this message dialog.
     */
    @Override protected Control createDialogArea(Composite parent)
    {
        final Composite container = (Composite) super.createDialogArea(parent);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        container.setLayout(gridLayout);

        m_tDetails = new Text(container, SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.H_SCROLL);

        final GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        gridData.heightHint = 480;
        gridData.widthHint = 600;
        m_tDetails.setLayoutData(gridData);

        m_tDetails.addModifyListener(new ModifyListener()
            {
                public void modifyText(ModifyEvent arg0)
                {
                    m_sCurrentText = m_tDetails.getText();
                }
            });

        m_tDetails.setText(getCurrentText());
        m_tDetails.setEditable(getEnabled());

        return container;
    }

    /**
     * Return the initial size of the dialog.
     *
     * @return  DOCUMENTME
     */
    @Override protected Point getInitialSize()
    {
        return new Point(500, 375);
    }
}
