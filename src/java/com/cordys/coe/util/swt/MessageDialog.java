package com.cordys.coe.util.swt;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * Generic message dialog to show messages including detailed message.
 *
 * @author  pgussow
 */
public class MessageDialog extends Dialog
{
    /**
     * Holds the type of message.
     */
    private int m_iType;
    /**
     * Holds the caption.
     */
    private String m_sCaption;
    /**
     * Holds the details.
     */
    private String m_sDetails;
    /**
     * Holds the icon to show.
     */
    private String m_sIconName;
    /**
     * Holds the short message.
     */
    private String m_sShortMessage;
    /**
     * Holds the details of the message.
     */
    private StyledText m_stDetails;

    /**
     * Creates a new MessageDialog object.
     *
     * @param  sParentShell   The parent shell.
     * @param  sCaption       The caption for the dialog.
     * @param  sShortMessage  The short message.
     * @param  sDetails       The detailed message.
     * @param  iType          The type. SWT.ICON_ERROR, SWT.ICON_WARNING, SWT.ICON_INFORMATION and
     *                        SWT.ICON_QUESTION are supported.
     */
    public MessageDialog(Shell sParentShell, String sCaption, String sShortMessage, String sDetails,
                         int iType)
    {
        super(sParentShell);

        m_sShortMessage = sShortMessage;
        m_sDetails = sDetails;
        m_sCaption = sCaption;
        m_iType = iType;

        if (iType == SWT.ICON_ERROR)
        {
            m_sIconName = "error32.gif";
        }
        else if (iType == SWT.ICON_WARNING)
        {
            m_sIconName = "warning32.gif";
        }
        else if (iType == SWT.ICON_INFORMATION)
        {
            m_sIconName = "info32.gif";
        }
        else if (iType == SWT.ICON_QUESTION)
        {
            m_sIconName = "question32.gif";
        }

        setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
    }

    /**
     * This method configures the shell with the proper caption.
     *
     * @param  newShell  The new shell.
     */
    @Override protected void configureShell(Shell newShell)
    {
        super.configureShell(newShell);
        newShell.setText(m_sCaption);
    }

    /**
     * Create contents of the button bar.
     *
     * @param  parent  The parent composite.
     */
    @Override protected void createButtonsForButtonBar(Composite parent)
    {
        if (m_iType == SWT.ICON_INFORMATION)
        {
            createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        }
        else if ((m_iType == SWT.ICON_ERROR) || (m_iType == SWT.ICON_WARNING))
        {
            createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);

            createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
        }
        else
        {
            // It's a question
            createButton(parent, IDialogConstants.OK_ID, IDialogConstants.YES_LABEL, true);

            createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.NO_LABEL, false);
        }
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

        final Label label = new Label(container, SWT.WRAP);
        label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

        label.setImage(SWTResourceManager.getImage(MessageDialog.class, m_sIconName));

        final Label m_lShortMessage = new Label(container, SWT.WRAP);
        final GridData gdShort = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gdShort.heightHint = 40;
        gdShort.widthHint = 400;
        m_lShortMessage.setLayoutData(gdShort);
        m_lShortMessage.setText(m_sShortMessage);

        if ((m_sDetails != null) && (m_sDetails.length() > 0))
        {
            m_stDetails = new StyledText(container,
                                         SWT.V_SCROLL | SWT.READ_ONLY | SWT.BORDER | SWT.H_SCROLL);
            m_stDetails.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
            m_stDetails.setEditable(false);

            final GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
            gridData.heightHint = 115;
            gridData.widthHint = 414;
            m_stDetails.setLayoutData(gridData);

            m_stDetails.setText(m_sDetails);
        }

        return container;
    }
}
