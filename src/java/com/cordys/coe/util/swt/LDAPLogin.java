package com.cordys.coe.util.swt;

import com.eibus.directory.soap.LDAPDirectory;

import com.eibus.util.system.EIBProperties;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPException;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * This dialog can be used to log on to a certain Cordys installation.
 *
 * @author  pgussow
 */
public class LDAPLogin extends Dialog
{
    /**
     * The result for this dialog.
     */
    protected Object result;
    /**
     * The shell to use.
     */
    protected Shell shell;
    /**
     * Holds whether or not to access LDAP by LDAPConnection instead of LDAPDirectory.
     */
    private boolean bConnection = false;
    /**
     * Indicates whether or not OK was pressed.
     */
    private boolean bOk = false;
    /**
     * The checkbox for SSL.
     */
    private Button cbSSL;
    /**
     * The checkbox for whether or not the entered values should be saved to the wcp.properties,
     */
    private Button cbSystemDefault;
    /**
     * Holds the LDAPConnection.
     */
    private LDAPConnection lCon;
    /**
     * Holds the LDAP-drirectory.
     */
    private LDAPDirectory ldap;
    /**
     * Holds the searchroot.
     */
    private String sSearchRoot;
    /**
     * The text box for entering the servername.
     */
    private Text tfLDAPServer;
    /**
     * The text box for entering the password.
     */
    private Text tfPassword;
    /**
     * The text box for entering the port number.
     */
    private Text tfPort;
    /**
     * The text box for entering the search root.
     */
    private Text tfSearchRoot;
    /**
     * The text box for entering the username.
     */
    private Text tfUser;

    /**
     * Creates a new LDAPLogin object.
     *
     * @param  parent  DOCUMENTME
     */
    public LDAPLogin(Shell parent)
    {
        this(parent, SWT.NONE);
    }

    /**
     * Creates a new LDAPLogin object.
     *
     * @param  parent  The parent shell.
     * @param  style   The style.
     */
    public LDAPLogin(Shell parent, int style)
    {
        super(parent, style);
    }

    /**
     * Returns the LDAPConnection.
     *
     * @return  The LDAPConnection.
     */
    public LDAPConnection getLDAPConnection()
    {
        return lCon;
    }

    /**
     * Returns the LDAPDirectory.
     *
     * @return  The LDAPDirectory.
     */
    public LDAPDirectory getLDAPDirectory()
    {
        return ldap;
    }

    /**
     * This method returns the searchroot for the LDAPserver.
     *
     * @return  The searchroot for the LDAPserver.
     */
    public String getSearchRoot()
    {
        return sSearchRoot;
    }

    /**
     * Returns wether or not the logon was succesfull.
     *
     * @return  Wether or not the logon was succesfull.
     */
    public boolean isOk()
    {
        return bOk;
    }

    /**
     * This metehod shows the screen.
     *
     * @return  The result for this dialog.
     */
    public Object open()
    {
        Shell parent = getParent();
        shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        shell.setText(getText());

        // Initialize the dialog.
        createContents();

        readDefaults();

        // Your code goes here (widget creation, set result, etc).
        shell.open();
        shell.layout();

        Display display = parent.getDisplay();

        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
        return result;
    }

    /**
     * This method sets whether or not to use the LDAPConnection or LDAPDirectory. If the
     * bConnection is false then LDAPDirectory is used. Otherwise the LDAPConnection is used.
     *
     * @param  bConnection  Whether or not to use LDAPConnection.
     */
    public void setUseConnection(boolean bConnection)
    {
        this.bConnection = bConnection;
    }

    /**
     * DOCUMENTME.
     */
    protected void createContents()
    {
        shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        shell.setLayout(new FillLayout());
        shell.setSize(345, 226);
        shell.setText("Login to LDAP");

        final Group gLDAPGroup = new Group(shell, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.marginRight = 5;
        gridLayout.marginLeft = 5;
        gridLayout.numColumns = 4;
        gLDAPGroup.setLayout(gridLayout);

        final Label ldapServerLabel = new Label(gLDAPGroup, SWT.NONE);
        ldapServerLabel.setText("LDAP Server:");

        tfLDAPServer = new Text(gLDAPGroup, SWT.BORDER);
        tfLDAPServer.setLayoutData(new GridData(162, SWT.DEFAULT));

        final Label label_1 = new Label(gLDAPGroup, SWT.NONE);
        label_1.setText(":");

        tfPort = new Text(gLDAPGroup, SWT.BORDER);
        tfPort.setLayoutData(new GridData(34, SWT.DEFAULT));

        final Label userLabel = new Label(gLDAPGroup, SWT.NONE);
        userLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        userLabel.setText("User:");

        tfUser = new Text(gLDAPGroup, SWT.BORDER);
        tfUser.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 3, 1));

        final Label passwordLabel = new Label(gLDAPGroup, SWT.NONE);
        passwordLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        passwordLabel.setText("Password:");

        tfPassword = new Text(gLDAPGroup, SWT.BORDER);
        tfPassword.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 3, 1));
        tfPassword.setEchoChar('*');

        final Label searchRootLabel_1 = new Label(gLDAPGroup, SWT.NONE);
        searchRootLabel_1.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        searchRootLabel_1.setText("Search root:");

        tfSearchRoot = new Text(gLDAPGroup, SWT.BORDER);
        tfSearchRoot.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 3,
                                                1));

        final Label searchRootLabel = new Label(gLDAPGroup, SWT.NONE);
        searchRootLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        searchRootLabel.setText("System default:");

        cbSystemDefault = new Button(gLDAPGroup, SWT.CHECK);
        cbSystemDefault.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false,
                                                   false, 3, 1));

        final Label sslLabel = new Label(gLDAPGroup, SWT.NONE);
        sslLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        sslLabel.setText("SSL:");

        cbSSL = new Button(gLDAPGroup, SWT.CHECK);
        cbSSL.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 3, 1));

        final Composite composite = new Composite(gLDAPGroup, SWT.NONE);
        final GridLayout gridLayout_1 = new GridLayout();
        gridLayout_1.makeColumnsEqualWidth = true;
        gridLayout_1.numColumns = 3;
        composite.setLayout(gridLayout_1);

        final GridData gridData_1 = new GridData(GridData.FILL, GridData.CENTER, false, false, 4,
                                                 1);
        gridData_1.widthHint = 315;
        composite.setLayoutData(gridData_1);

        final Label label = new Label(composite, SWT.NONE);
        label.setText("");

        final Button bConnect = new Button(composite, SWT.NONE);
        bConnect.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e)
                {
                    connectToLDAP();
                }
            });
        bConnect.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
        bConnect.setText("&Connect");

        final Button bCancel = new Button(composite, SWT.NONE);
        bCancel.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e)
                {
                    shell.close();
                }
            });

        final GridData gridData = new GridData(GridData.END, GridData.CENTER, false, false);
        gridData.widthHint = 52;
        bCancel.setLayoutData(gridData);
        bCancel.setText("C&ancel");
        //
    }

    /**
     * This method connects to LDAP. Depending on the bConnection-variable this is done either
     * trough the LDAPDirectory or trough the LDAPConnection-class.
     */
    private void connectToLDAP()
    {
        try
        {
            String sServer = tfLDAPServer.getText();
            int iPort = Integer.parseInt(tfPort.getText());
            String sUser = tfUser.getText();
            String sPassword = new String(tfPassword.getText());

            if (bConnection == false)
            {
                ldap = new LDAPDirectory(sServer, iPort, sUser, sPassword);
            }
            else
            {
                lCon = new LDAPConnection();
                lCon.connect(tfLDAPServer.getText(), Integer.parseInt(tfPort.getText()));
                lCon.bind(LDAPConnection.LDAP_V3, tfUser.getText(), new String(tfPassword.getText())
                          .getBytes());
            }

            if (cbSystemDefault.getSelection())
            {
                try
                {
                    EIBProperties.setLDAPServer(tfLDAPServer.getText() + ":" + tfPort.getText());
                    EIBProperties.saveProperty("bus.administrator.user", tfUser.getText());
                    EIBProperties.saveProperty("bus.administrator.pwd",
                                               encode(new String(tfPassword.getText())));
                    EIBProperties.saveProperty("ldap.root", tfSearchRoot.getText());
                }
                catch (Exception e)
                {
                    showMsg("Error saving defaults");
                }
            }
            bOk = true;

            // Set the searchroot.
            sSearchRoot = tfSearchRoot.getText();

            // Close the dialog.
            shell.close();
        }
        catch (Exception e)
        {
            if (e instanceof LDAPException)
            {
                LDAPException le = (LDAPException) e;

                if (le.getResultCode() == LDAPException.CONNECT_ERROR)
                {
                    showMsg("LDAP server could not be contacted");
                }
                else if (le.getResultCode() == LDAPException.INVALID_CREDENTIALS)
                {
                    showMsg("You are not authorised to connect to LDAP server");
                }
            }
            else
            {
                showMsg("Unknown error\n" + e);
            }
        }
    }

    /**
     * This method decodes the password.
     *
     * @param   sPassword  The password to decode.
     *
     * @return  The decoded password.
     */
    private String decode(String sPassword)
    {
        try
        {
            BASE64Decoder base64decoder = new BASE64Decoder();
            byte[] abyte0 = base64decoder.decodeBuffer(sPassword);
            return new String(abyte0, "UTF8");
        }
        catch (Exception exception)
        {
            return sPassword;
        }
    }

    /**
     * Encodes the LDAP-password.
     *
     * @param   sPassword  The password to encode.
     *
     * @return  The encoded password.
     */
    private String encode(String sPassword)
    {
        try
        {
            BASE64Encoder base64encoder = new BASE64Encoder();
            return base64encoder.encodeBuffer(sPassword.getBytes("UTF8"));
        }
        catch (Exception exception)
        {
            return sPassword;
        }
    }

    /**
     * Function to read data from local eibus property.
     */
    private void readDefaults()
    {
        String sUsername = EIBProperties.getProperty("bus.administrator.user");

        if (sUsername == null)
        {
            sUsername = "";
        }

        String sEncodedPassword = EIBProperties.getProperty("bus.administrator.pwd");
        String sRealLDAPServer = "";
        String sRealPortNumber = "3899";
        String sRealPassword = "";
        String sEIBLDAPServer = EIBProperties.getProperty(EIBProperties.LDAP_SERVER);

        if ((sEIBLDAPServer != null) && (sEIBLDAPServer.length() > 0))
        {
            sRealLDAPServer = sEIBLDAPServer.substring(0, sEIBLDAPServer.indexOf(":"));
            sRealPortNumber = sEIBLDAPServer.substring((sEIBLDAPServer.indexOf(":") + 1));
        }

        if ((sEncodedPassword != null) && (sEncodedPassword.length() > 0))
        {
            sRealPassword = decode(sEncodedPassword);
        }

        tfUser.setText(sUsername);
        tfPassword.setText(sRealPassword);
        tfLDAPServer.setText(sRealLDAPServer);
        tfPort.setText(sRealPortNumber);

        if (EIBProperties.getProperty("ldap.root") == null)
        {
            try
            {
                InetAddress.getLocalHost();

                InetAddress[] ainetaddress = InetAddress.getAllByName(InetAddress.getLocalHost()
                                                                      .getHostAddress());
                String s = "";

                for (int i = 0; i < ainetaddress.length; i++)
                {
                    s = s + ainetaddress[i].getHostName();
                }
                tfSearchRoot.setText("o=" + s.substring(s.indexOf(".") + 1, s.length()));
            }
            catch (UnknownHostException unknownhostexception)
            {
                tfSearchRoot.setText("");
            }
        }
        else
        {
            tfSearchRoot.setText(EIBProperties.getProperty("ldap.root"));
        }
    }

    /**
     * Displatys an alert with the message.
     *
     * @param  sMSG  The message to display.
     */
    private void showMsg(String sMSG)
    {
        MessageBox mb = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
        mb.setMessage(sMSG);
        mb.open();
    }
}
