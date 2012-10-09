package com.cordys.coe.tools.log4j;

import com.cordys.coe.util.swt.MessageBoxUtil;
import com.cordys.coe.util.swt.SWTResourceManager;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * This dialog can add a new listener to the viewer.
 * 
 * @author pgussow
 */
public class NewListenerDlg extends Dialog
{
    /**
     * Holds the list of existing panels.
     */
    private Combo m_cbPanels;
    /**
     * Holds the default port number.
     */
    private int m_iDefaultPortNumber;
    /**
     * Holds the actual port number.
     */
    private int m_iPortnumber;
    /**
     * Holds the list of panels to be used.
     */
    private String[] m_saPanels;
    /**
     * Holds the name of the listener to use.
     */
    private String m_sListenerName;
    /**
     * Holds the name for the logger tab.
     */
    private String m_sLogName;
    /**
     * The name for the listener.
     */
    private Text m_tName;
    /**
     * The portnumber to listen on.
     */
    private Text m_tPortNumber;
    /**
     * Holds the checkbox for determining whether or not to use the SocketHUBAppender.
     */
    private Button m_cUseHUB;
    /**
     * Holds the text box for the connection server if a HUB appender is used.
     */
    private Text m_tHostName;
    /**
     * Holds whether or not to use the SocketHUBAppender..
     */
    private boolean m_useHUB;
    /**
     * Holds the connection server if a HUB appender is used.
     */
    private String m_hostname;
    /**
     * Holds the label for the hostname.
     */
    private Label m_lblHostName;

    /**
     * Create the dialog.
     * 
     * @param parentShell
     */
    public NewListenerDlg(Shell parentShell)
    {
        super(parentShell);
    }

    /**
     * This method gets the name for the listener.
     * 
     * @return The name for the listener.
     */
    public String getListenerName()
    {
        return m_sListenerName;
    }

    /**
     * This method gets the name of the panel in which the events should be shown.
     * 
     * @return The name of the panel in which the events should be shown.
     */
    public String getLogName()
    {
        return m_sLogName;
    }

    /**
     * This method gets the entered port number.
     * 
     * @return The entered port number.
     */
    public int getPortnumber()
    {
        return m_iPortnumber;
    }

    /**
     * This method gets the name of the server in case a HUB appender should be used.
     * 
     * @return The name of the server in case a HUB appender should be used.
     */
    public String getHostName()
    {
        return m_hostname;
    }

    /**
     * This method gets whether or not a HUB appender should be used.
     * 
     * @return Whether or not a HUB appender should be used.
     */
    public boolean useHUBAppender()
    {
        return m_useHUB;
    }

    /**
     * This method sets the default value for the port number.
     * 
     * @param iPortnumber The default portnumber.
     */
    public void setDefaultPortNumber(int iPortnumber)
    {
        m_iDefaultPortNumber = iPortnumber;
    }

    /**
     * This method sets the panel options.
     * 
     * @param saPanels The list of current panels.
     */
    public void setPanelOptions(String[] saPanels)
    {
        m_saPanels = saPanels;
    }

    /**
     * This method configures the dialog.
     * 
     * @param sNewShell The new shell.
     */
    @Override
    protected void configureShell(Shell sNewShell)
    {
        super.configureShell(sNewShell);
        sNewShell.setText("Add a new Log4J listener");
        sNewShell.setImage(SWTResourceManager.getImage(NewListenerDlg.class, "filter/icons/newlistener.gif"));
    }

    /**
     * Create contents of the button bar.
     * 
     * @param cParent The parent composite.
     */
    @Override
    protected void createButtonsForButtonBar(Composite cParent)
    {
        createButton(cParent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(cParent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    /**
     * Create contents of the dialog.
     * 
     * @param cParent The parent composite.
     * @return The control for this dialog.
     */
    @Override
    protected Control createDialogArea(Composite cParent)
    {
        Composite container = (Composite) super.createDialogArea(cParent);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Group m_gDetails = new Group(container, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        m_gDetails.setLayout(gridLayout);
        m_gDetails.setText(" Enter the details for the new listener ");

        final GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, true);
        gridData.widthHint = 373;
        m_gDetails.setLayoutData(gridData);

        m_lblHostName = new Label(m_gDetails, SWT.NONE);
        m_lblHostName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        m_lblHostName.setText("Host name:");

        m_tHostName = new Text(m_gDetails, SWT.BORDER);
        m_tHostName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        final Label portNumberLabel = new Label(m_gDetails, SWT.NONE);
        portNumberLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        portNumberLabel.setText("Port number:");

        m_tPortNumber = new Text(m_gDetails, SWT.BORDER);
        m_tPortNumber.addModifyListener(new ModifyListener() {
            public void modifyText(final ModifyEvent e)
            {
                if (m_tName != null)
                {
                    m_tName.setText(m_tPortNumber.getText());
                }
            }
        });
        m_tPortNumber.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Label nameLabel = new Label(m_gDetails, SWT.NONE);
        nameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        nameLabel.setText("Name:");

        m_tName = new Text(m_gDetails, SWT.BORDER);
        m_tName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Label addEventsToLabel = new Label(m_gDetails, SWT.NONE);
        addEventsToLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        addEventsToLabel.setText("Add events to panel:");

        //
        m_tPortNumber.setText("" + m_iDefaultPortNumber);
        m_tPortNumber.forceFocus();

        m_cbPanels = new Combo(m_gDetails, SWT.NONE);
        m_cbPanels.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        m_cbPanels.setItems(m_saPanels);
        m_cbPanels.add("<New Panel>");
        m_cbPanels.select(0);
        new Label(m_gDetails, SWT.NONE);

        m_cUseHUB = new Button(m_gDetails, SWT.CHECK);
        m_cUseHUB.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                if (m_cUseHUB.getSelection())
                {
                    m_tHostName.setVisible(true);
                    m_lblHostName.setVisible(true);
                }
                else
                {
                    m_tHostName.setVisible(false);
                    m_lblHostName.setVisible(false);
                }
            }
        });
        m_cUseHUB.setText("Use HUB appender");
        m_cUseHUB.setSelection(false);

        return container;
    }

    /**
     * Return the initial size of the dialog.
     * 
     * @return The initial size for the dialog.
     */
    @Override
    protected Point getInitialSize()
    {
        return new Point(440, 318);
    }

    /**
     * This method stores the values in the local member variables.
     * 
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    @Override
    protected void okPressed()
    {
        // Store the values in a local member
        try
        {
            m_iPortnumber = Integer.parseInt(m_tPortNumber.getText());
            m_useHUB = m_cUseHUB.getSelection();
            m_hostname = m_tHostName.getText();
        }
        catch (NumberFormatException nfe)
        {
            MessageBoxUtil.showError(getShell(), "The entered value is not a valid integer.");
        }

        m_sListenerName = m_tName.getText();
        m_sLogName = m_cbPanels.getText();

        super.okPressed();
    }
}
