package com.cordys.coe.util.swt;

import com.cordys.coe.util.cgc.config.EAuthenticationType;
import com.cordys.coe.util.cgc.config.ETrustMode;
import com.cordys.coe.util.config.ConfigurationFactory;
import com.cordys.coe.util.config.ConfigurationManager;
import com.cordys.coe.util.config.ConfigurationManagerException;
import com.cordys.coe.util.config.IConfiguration;
import com.cordys.coe.util.config.IWebGatewayConfiguration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

/**
 * This dialog can be used to log on to any Cordys system. It will return a CordysGatewayClient
 * connection that can be used to send messages to the Cordys system.
 *
 * @author  pgussow
 */
public class CGCLoginForm extends Dialog
{
    /**
     * Identifies the authentication mode: client certificate.
     */
    private static final String AUTH_MODE_CLIENT_CERTIFICATE = "Client certificate";
    /**
     * Identifies the authentication mode: SSO.
     */
    private static final String AUTH_MODE_SSO = "SSO (C3)";
    /**
     * Identifies the authentication mode: Cordys custom.
     */
    private static final String AUTH_MODE_CORDYS_CUSTOM = "Cordys Custom (C2)";
    /**
     * Identifies the authentication mode: Basic.
     */
    private static final String AUTH_MODE_BASIC = "Basic";
    /**
     * Identifies the authentication mode: NTLM.
     */
    private static final String AUTH_MODE_NTLM = "Domain (NTLM)";
    /**
     * Identifies the SSL trust mode: use specific truststore.
     */
    private static final String TRUST_MODE_TRUSTSTORE = "Use specific truststore";
    /**
     * Identifies the SSL trust mode: All servers.
     */
    private static final String TRUST_MODE_ALL_SERVERS = "Trust all servers";
    /**
     * DOCUMENTME.
     */
    private Button m_bAcceptExpired;
    /**
     * DOCUMENTME.
     */
    private Button m_bAcceptInvalid;
    /**
     * DOCUMENTME.
     */
    private Button m_bBrowseCertificate;
    /**
     * DOCUMENTME.
     */
    private Button m_bBrowseTrust;
    /**
     * Holds whether or not all controls have been created.
     */
    private boolean m_bInitialized;
    /**
     * Indicates whether or not the configuration was ok.
     */
    private boolean m_bOk;
    /**
     * Holds whether or not the configuration should be saved.
     */
    private Button m_bSaveConfig;
    /**
     * Holds the authentication type to use for the Cordys server.
     */
    private Combo m_cAuthType;
    /**
     * Holds the keystore type for the certificate.
     */
    private Combo m_cCertificateType;
    /**
     * Holds the configuration of the web gateway.
     */
    private IWebGatewayConfiguration m_cConfig;
    /**
     * Holds the currently stored configurations.
     */
    private Combo m_cCurrentConfiguration;
    /**
     * Holds the configuration manager to manage locally stored sessions.
     */
    private ConfigurationManager m_cmManager;
    /**
     * Holds whether or not the server runs in SSL mode.
     */
    private Button m_cSSL;
    /**
     * Holds the trust mode to use for an SSL connection.
     */
    private Combo m_cTrustMode;
    /**
     * Holds the trust store type.
     */
    private Combo m_cTrustType;
    /**
     * Holds the list of all currently loaded configurations.
     */
    private LinkedHashMap<String, IWebGatewayConfiguration> m_lhmConfigurations;
    /**
     * Holds the dialog's shell.
     */
    private Shell m_sShell;
    /**
     * Holds the location of the certificate.
     */
    private Text m_tCertificateLocation;
    /**
     * Holds the certificate password.
     */
    private Text m_tCertificatePassword;
    /**
     * Holds the name for this configuration.
     */
    private Text m_tConfigName;
    /**
     * Holds the NTDomain to authenticate to.
     */
    private Text m_tDomain;
    /**
     * Holds the URL of the gateway.
     */
    private Text m_tGatewayURL;
    /**
     * Holds the network timeout.
     */
    private Text m_tNetworkTimeout;
    /**
     * Holds the password to use.
     */
    private Text m_tPassword;
    /**
     * Holds the port number to connect to.
     */
    private Text m_tPort;
    /**
     * The password for the proxy server.
     */
    private Text m_tProxyPassword;
    /**
     * The port for the proxy server.
     */
    private Text m_tProxyPort;
    /**
     * The name of the proxy server.
     */
    private Text m_tProxyServer;
    /**
     * The proxy username.
     */
    private Text m_tProxyUsername;
    /**
     * Holds teh request timeout.
     */
    private Text m_tRequestTimeout;
    /**
     * Holds the name of the server.
     */
    private Text m_tServer;
    /**
     * Holds the location of the trust store.
     */
    private Text m_tTrustLocation;
    /**
     * Holds the password for the trust store.
     */
    private Text m_tTrustPassword;
    /**
     * Holds the username to use.
     */
    private Text m_tUsername;

    /**
     * Creates a new CGCLoginForm object.
     *
     * @param  sParent  The parent shell.
     */
    public CGCLoginForm(Shell sParent)
    {
        super(sParent, SWT.APPLICATION_MODAL | SWT.RESIZE);
    }

    /**
     * Creates a new CGCLoginForm object.
     *
     * @param  sParent  The parent shell.
     * @param  iStyle   The SWT style.
     */
    public CGCLoginForm(Shell sParent, int iStyle)
    {
        super(sParent, iStyle);
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
            CGCLoginForm clf = new CGCLoginForm(sShell);
            clf.open();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Returns wether or not the logon was succesfull.
     *
     * @return  Wether or not the logon was succesfull.
     */
    public boolean isOk()
    {
        return m_bOk;
    }

    /**
     * Open the window.
     *
     * @return  The actual ICordysGatewayClient to use.
     */
    public IWebGatewayConfiguration open()
    {
        createContents();
        m_bInitialized = true;

        // Load the configuration.
        try
        {
            m_cmManager = ConfigurationManager.getInstance();
            m_lhmConfigurations = m_cmManager.getWebGatewayConfigurations();

            // Fill the combobox.
            ArrayList<String> alTemp = new ArrayList<String>(m_lhmConfigurations.size() + 1);
            alTemp.add("<New>");

            for (Iterator<String> iTemp = m_lhmConfigurations.keySet().iterator(); iTemp.hasNext();)
            {
                String sKey = iTemp.next();
                alTemp.add(sKey);
            }

            String[] saItems = alTemp.toArray(new String[alTemp.size()]);
            m_cCurrentConfiguration.setItems(saItems);
            m_cCurrentConfiguration.select(0);

            // Set the default selected
            m_cAuthType.select(0);
            m_cTrustMode.select(0);

            Display display = getParent().getDisplay();

            SWTUtils.centerShell(display, m_sShell);
            m_sShell.layout();

            m_sShell.open();

            while (!m_sShell.isDisposed())
            {
                if (!display.readAndDispatch())
                {
                    display.sleep();
                }
            }
        }
        catch (Exception e)
        {
            MessageBoxUtil.showError(m_sShell, "Error connecting to Cordys", e);
        }

        if (isOk())
        {
            return m_cConfig;
        }

        return null;
    }

    /**
     * This method opens a file browser to pick the proper certificate file.
     *
     * @param  tLocation  The text control where the selected path should be put into.
     */
    protected void browseFile(Text tLocation)
    {
        FileDialog fcFile = new FileDialog(getParent(), SWT.OPEN);
        String sFilename = fcFile.open();

        if ((sFilename != null) && (sFilename.length() > 0))
        {
            tLocation.setText(sFilename);
        }
    }

    /**
     * Create contents of the window.
     */
    protected void createContents()
    {
        Shell parent = getParent();
        m_sShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
        m_sShell.setLayout(new GridLayout());
        m_sShell.setText(getText());
        m_sShell.setSize(500, 453);
        m_sShell.setText("Connecting to a Cordys environment");
        m_sShell.setImage(SWTResourceManager.getImage(CGCLoginForm.class, "cordys.gif"));

        final Group serverDetailsGroup = new Group(m_sShell, SWT.NO_RADIO_GROUP);
        final GridLayout gridLayout_1 = new GridLayout();
        gridLayout_1.numColumns = 4;
        serverDetailsGroup.setLayout(gridLayout_1);
        serverDetailsGroup.setText(" Server details ");

        final GridData gd_serverDetailsGroup = new GridData(SWT.FILL, SWT.CENTER, true, false);
        serverDetailsGroup.setLayoutData(gd_serverDetailsGroup);

        final Label connectionLabel = new Label(serverDetailsGroup, SWT.NONE);
        connectionLabel.setAlignment(SWT.RIGHT);
        connectionLabel.setLayoutData(new GridData(110, SWT.DEFAULT));
        connectionLabel.setText("Connection:");

        m_cCurrentConfiguration = new Combo(serverDetailsGroup, SWT.READ_ONLY);
        m_cCurrentConfiguration.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3,
                                                           1));
        m_cCurrentConfiguration.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e)
                {
                    updateConfigTable();
                }
            });
        m_cCurrentConfiguration.addKeyListener(new KeyAdapter() {
            public void keyPressed(final KeyEvent keyevent)
            {
                if (keyevent.character == 13 && !m_cCurrentConfiguration.getText().equals("<New>"))
                {
                    makeConnection();
                }
            }
        });
        m_cCurrentConfiguration.select(0);

        final Label authenticationTypeLabel = new Label(serverDetailsGroup, SWT.NONE);
        authenticationTypeLabel.setAlignment(SWT.RIGHT);

        final GridData gd_authenticationTypeLabel = new GridData(SWT.RIGHT, SWT.CENTER, false,
                                                                 false);
        gd_authenticationTypeLabel.widthHint = 110;
        authenticationTypeLabel.setLayoutData(gd_authenticationTypeLabel);
        authenticationTypeLabel.setText("Authentication type:");

        m_cAuthType = new Combo(serverDetailsGroup, SWT.READ_ONLY);
        m_cAuthType.addModifyListener(new ModifyListener()
            {
                public void modifyText(final ModifyEvent modifyevent)
                {
                    enableProperControls();
                }
            });
        m_cAuthType.setItems(new String[]
                             {
                                 AUTH_MODE_NTLM, AUTH_MODE_BASIC, AUTH_MODE_CORDYS_CUSTOM,
                                 AUTH_MODE_SSO, AUTH_MODE_CLIENT_CERTIFICATE
                             });

        final GridData gd_m_cAuthType = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_cAuthType.setLayoutData(gd_m_cAuthType);
        new Label(serverDetailsGroup, SWT.NONE);
        new Label(serverDetailsGroup, SWT.NONE);

        final Label servernameLabel = new Label(serverDetailsGroup, SWT.NONE);
        servernameLabel.setAlignment(SWT.RIGHT);

        final GridData gd_servernameLabel = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        gd_servernameLabel.widthHint = 110;
        servernameLabel.setLayoutData(gd_servernameLabel);
        servernameLabel.setText("Servername:");

        m_tServer = new Text(serverDetailsGroup, SWT.BORDER);
        m_tServer.addFocusListener(new FocusListener()
            {
                private String m_sBefore = "";
                private String m_sServer = "";

                public void focusGained(FocusEvent fe)
                {
                    m_sBefore = m_tConfigName.getText();
                    m_sServer = m_tServer.getText();
                }

                public void focusLost(FocusEvent fe)
                {
                    if ((m_sBefore == null) || (m_sBefore.length() == 0) ||
                            ((m_sBefore.length() > 0) && (m_sServer.length() > 0) &&
                                 m_sBefore.startsWith(m_sServer)))
                    {
                        // We'll change it
                        if (m_cCurrentConfiguration.getSelectionIndex() == 0)
                        {
                            // Now we need to get a proper name.
                            String sConfigName = m_tServer.getText();

                            if (m_cmManager.getConfiguration(sConfigName) != null)
                            {
                                // Name already exists
                                int iCount = 1;
                                boolean bFound = false;
                                String sFinal = sConfigName + "_" + iCount;

                                while (!bFound)
                                {
                                    sFinal = sConfigName + "_" + iCount;

                                    if (m_cmManager.getConfiguration(sFinal) == null)
                                    {
                                        bFound = true;
                                    }
                                    iCount++;
                                }
                                sConfigName = sFinal;
                            }

                            m_tConfigName.setText(sConfigName);
                        }
                    }
                }
            });

        final GridData gd_m_tserverText = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_tServer.setLayoutData(gd_m_tserverText);

        final Label portLabel = new Label(serverDetailsGroup, SWT.NONE);
        portLabel.setText("Port:");

        m_tPort = new Text(serverDetailsGroup, SWT.BORDER);
        m_tPort.setText("80");

        final GridData gd_m_tPort = new GridData(SWT.FILL, SWT.CENTER, false, false);
        gd_m_tPort.widthHint = 83;
        m_tPort.setLayoutData(gd_m_tPort);

        final Label usernameLabel = new Label(serverDetailsGroup, SWT.NONE);
        final GridData gd_usernameLabel = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        gd_usernameLabel.widthHint = 110;
        usernameLabel.setLayoutData(gd_usernameLabel);
        usernameLabel.setAlignment(SWT.RIGHT);
        usernameLabel.setText("Username:");

        m_tUsername = new Text(serverDetailsGroup, SWT.BORDER);
        m_tUsername.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));

        final Label passwordLabel = new Label(serverDetailsGroup, SWT.NONE);
        final GridData gd_passwordLabel = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        gd_passwordLabel.widthHint = 110;
        passwordLabel.setLayoutData(gd_passwordLabel);
        passwordLabel.setAlignment(SWT.RIGHT);
        passwordLabel.setText("Password:");

        m_tPassword = new Text(serverDetailsGroup, SWT.BORDER | SWT.PASSWORD);
        m_tPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
        m_tPassword.setEchoChar('*');

        final Label domainLabel = new Label(serverDetailsGroup, SWT.NONE);
        domainLabel.setLayoutData(new GridData(110, SWT.DEFAULT));
        domainLabel.setAlignment(SWT.RIGHT);
        domainLabel.setText("Domain");

        m_tDomain = new Text(serverDetailsGroup, SWT.BORDER);
        m_tDomain.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));

        final Composite composite_2 = new Composite(m_sShell, SWT.NONE);
        composite_2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));

        final GridLayout gridLayout_8 = new GridLayout();
        gridLayout_8.verticalSpacing = 0;
        gridLayout_8.marginWidth = 0;
        gridLayout_8.marginHeight = 0;
        gridLayout_8.horizontalSpacing = 0;
        gridLayout_8.numColumns = 2;
        composite_2.setLayout(gridLayout_8);

        final Button bConnect = new Button(composite_2, SWT.NONE);
        bConnect.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    makeConnection();
                }
            });

        final GridData gd_bConnect = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        gd_bConnect.widthHint = 60;
        bConnect.setLayoutData(gd_bConnect);
        bConnect.setText("C&onnect");

        final Button bCancel = new Button(composite_2, SWT.NONE);
        bCancel.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent e)
                {
                    m_sShell.close();
                }
            });

        final GridData gd_bCancel = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        gd_bCancel.widthHint = 60;
        bCancel.setLayoutData(gd_bCancel);
        bCancel.setText("&Cancel");

        final TabFolder tabFolder = new TabFolder(m_sShell, SWT.NONE);
        tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final TabItem basicSslTabItem = new TabItem(tabFolder, SWT.NONE);
        basicSslTabItem.setText("Basic SSL");

        final Composite composite_3 = new Composite(tabFolder, SWT.NONE);
        composite_3.setLayout(new GridLayout());
        basicSslTabItem.setControl(composite_3);

        final Group sslDetailsGroup = new Group(composite_3, SWT.NONE);
        sslDetailsGroup.setLayoutData(new GridData(SWT.DEFAULT, 125));
        sslDetailsGroup.setText(" SSL Details ");

        final GridLayout gridLayout_3 = new GridLayout();
        gridLayout_3.numColumns = 4;
        sslDetailsGroup.setLayout(gridLayout_3);
        new Label(sslDetailsGroup, SWT.NONE);

        m_cSSL = new Button(sslDetailsGroup, SWT.CHECK);
        m_cSSL.setLayoutData(new GridData());
        m_cSSL.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent selectionevent)
                {
                    if (m_cAuthType.getText().equals(AUTH_MODE_CLIENT_CERTIFICATE) &&
                            (m_cSSL.getSelection() == false))
                    {
                        MessageBoxUtil.showError("When using a client certificate it is automatically SSL");
                        m_cSSL.setSelection(true);
                    }

                    if ((m_cSSL.getSelection() == true) && m_tPort.getText().equals("80"))
                    {
                        m_tPort.setText("443");
                    }
                    else if ((m_cSSL.getSelection() == false) && m_tPort.getText().equals("443"))
                    {
                        m_tPort.setText("80");
                    }

                    enableProperControls();
                }
            });
        m_cSSL.setText("Server runs in SSL");
        new Label(sslDetailsGroup, SWT.NONE);
        new Label(sslDetailsGroup, SWT.NONE);

        final Label trustModeLabel = new Label(sslDetailsGroup, SWT.NONE);
        trustModeLabel.setAlignment(SWT.RIGHT);

        final GridData gd_trustModeLabel = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        gd_trustModeLabel.widthHint = 110;
        trustModeLabel.setLayoutData(gd_trustModeLabel);
        trustModeLabel.setText("Trust mode:");

        m_cTrustMode = new Combo(sslDetailsGroup, SWT.READ_ONLY);
        m_cTrustMode.addModifyListener(new ModifyListener()
            {
                public void modifyText(final ModifyEvent arg0)
                {
                    enableProperControls();
                }
            });
        m_cTrustMode.setItems(new String[] { TRUST_MODE_ALL_SERVERS, TRUST_MODE_TRUSTSTORE });

        final GridData gd_m_cTrustMode = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
        gd_m_cTrustMode.widthHint = 310;
        m_cTrustMode.setLayoutData(gd_m_cTrustMode);

        final Label trustStoreLocationLabel = new Label(sslDetailsGroup, SWT.NONE);
        trustStoreLocationLabel.setAlignment(SWT.RIGHT);

        final GridData gd_trustStoreLocationLabel = new GridData(SWT.RIGHT, SWT.CENTER, false,
                                                                 false);
        gd_trustStoreLocationLabel.widthHint = 110;
        trustStoreLocationLabel.setLayoutData(gd_trustStoreLocationLabel);
        trustStoreLocationLabel.setText("Trust store location:");

        final Composite composite_1_1 = new Composite(sslDetailsGroup, SWT.NONE);
        composite_1_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

        final GridLayout gridLayout_6 = new GridLayout();
        gridLayout_6.numColumns = 2;
        gridLayout_6.verticalSpacing = 0;
        gridLayout_6.marginWidth = 0;
        gridLayout_6.marginHeight = 0;
        gridLayout_6.horizontalSpacing = 0;
        composite_1_1.setLayout(gridLayout_6);

        m_tTrustLocation = new Text(composite_1_1, SWT.BORDER);

        final GridData gd_m_tTrustLocation = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_tTrustLocation.setLayoutData(gd_m_tTrustLocation);

        m_bBrowseTrust = new Button(composite_1_1, SWT.NONE);
        m_bBrowseTrust.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent selectionevent)
                {
                    browseFile(m_tTrustLocation);
                }
            });

        final GridData gd_m_bBrowseTrust = new GridData();
        m_bBrowseTrust.setLayoutData(gd_m_bBrowseTrust);
        m_bBrowseTrust.setText("...");

        final Label trustStorePasswordLabel = new Label(sslDetailsGroup, SWT.NONE);
        trustStorePasswordLabel.setAlignment(SWT.RIGHT);

        final GridData gd_trustStorePasswordLabel = new GridData(SWT.RIGHT, SWT.CENTER, false,
                                                                 false);
        gd_trustStorePasswordLabel.widthHint = 110;
        trustStorePasswordLabel.setLayoutData(gd_trustStorePasswordLabel);
        trustStorePasswordLabel.setText("Trust store password:");

        m_tTrustPassword = new Text(sslDetailsGroup, SWT.BORDER | SWT.PASSWORD);

        final GridData gd_m_tTrustPassword = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_tTrustPassword.setLayoutData(gd_m_tTrustPassword);

        final Label trustStoreTypeLabel = new Label(sslDetailsGroup, SWT.NONE);
        trustStoreTypeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        trustStoreTypeLabel.setText("Trust store type:");

        m_cTrustType = new Combo(sslDetailsGroup, SWT.READ_ONLY);
        m_cTrustType.setItems(new String[] { "JKS", "PKCS12" });

        final GridData gd_m_cTrustType = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_cTrustType.setLayoutData(gd_m_cTrustType);

        m_bAcceptExpired = new Button(sslDetailsGroup, SWT.CHECK);

        final GridData gd_m_cAcceptExpired = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 2,
                                                          1);
        m_bAcceptExpired.setLayoutData(gd_m_cAcceptExpired);
        m_bAcceptExpired.setText("Accept expired certificates");

        m_bAcceptInvalid = new Button(sslDetailsGroup, SWT.CHECK);

        final GridData gd_m_cAcceptInvalid = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
        m_bAcceptInvalid.setLayoutData(gd_m_cAcceptInvalid);
        m_bAcceptInvalid.setText("Accept invalid certificates");
        new Label(sslDetailsGroup, SWT.NONE);
        new Label(sslDetailsGroup, SWT.NONE);

        final TabItem clientCertificateTabItem = new TabItem(tabFolder, SWT.NONE);
        clientCertificateTabItem.setText("Client certificate");

        final Composite composite_5 = new Composite(tabFolder, SWT.NONE);
        composite_5.setLayout(new GridLayout());
        clientCertificateTabItem.setControl(composite_5);

        final Group clientCertificateDetailsGroup = new Group(composite_5, SWT.NONE);
        clientCertificateDetailsGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
                                                                 false));
        clientCertificateDetailsGroup.setText(" Client certificate details ");

        final GridLayout gridLayout_4 = new GridLayout();
        gridLayout_4.numColumns = 4;
        clientCertificateDetailsGroup.setLayout(gridLayout_4);

        final Label certificateLocationLabel = new Label(clientCertificateDetailsGroup, SWT.NONE);
        certificateLocationLabel.setAlignment(SWT.RIGHT);

        final GridData gd_certificateLocationLabel = new GridData(SWT.RIGHT, SWT.CENTER, false,
                                                                  false);
        gd_certificateLocationLabel.widthHint = 110;
        certificateLocationLabel.setLayoutData(gd_certificateLocationLabel);
        certificateLocationLabel.setText("Certificate location:");

        final Composite composite_1 = new Composite(clientCertificateDetailsGroup, SWT.NONE);
        final GridLayout gridLayout_5 = new GridLayout();
        gridLayout_5.numColumns = 2;
        gridLayout_5.verticalSpacing = 0;
        gridLayout_5.marginWidth = 0;
        gridLayout_5.horizontalSpacing = 0;
        gridLayout_5.marginHeight = 0;
        composite_1.setLayout(gridLayout_5);
        composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

        m_tCertificateLocation = new Text(composite_1, SWT.BORDER);

        final GridData gd_m_tCertificateLocation = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_tCertificateLocation.setLayoutData(gd_m_tCertificateLocation);

        m_bBrowseCertificate = new Button(composite_1, SWT.PUSH);
        m_bBrowseCertificate.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(final SelectionEvent selectionevent)
                {
                    browseFile(m_tCertificateLocation);
                }
            });

        final GridData gd_m_bBrowseCertificate = new GridData();
        m_bBrowseCertificate.setLayoutData(gd_m_bBrowseCertificate);
        m_bBrowseCertificate.setText("...");

        final Label certificatePasswordLabel = new Label(clientCertificateDetailsGroup, SWT.NONE);
        certificatePasswordLabel.setAlignment(SWT.RIGHT);

        final GridData gd_certificatePasswordLabel = new GridData(SWT.RIGHT, SWT.CENTER, false,
                                                                  false);
        gd_certificatePasswordLabel.widthHint = 110;
        certificatePasswordLabel.setLayoutData(gd_certificatePasswordLabel);
        certificatePasswordLabel.setText("Certificate password:");

        m_tCertificatePassword = new Text(clientCertificateDetailsGroup, SWT.BORDER | SWT.PASSWORD);

        final GridData gd_m_tCertificatePassword = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_tCertificatePassword.setLayoutData(gd_m_tCertificatePassword);

        final Label keystoreTypeLabel = new Label(clientCertificateDetailsGroup, SWT.NONE);
        keystoreTypeLabel.setText("Certificate type:");

        m_cCertificateType = new Combo(clientCertificateDetailsGroup, SWT.READ_ONLY);
        m_cCertificateType.setItems(new String[] { "JKS", "PKCS12" });

        final GridData gd_m_cCertificateType = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_cCertificateType.setLayoutData(gd_m_cCertificateType);

        final TabItem advancedTabItem = new TabItem(tabFolder, SWT.NONE);
        advancedTabItem.setText("Advanced");

        final TabItem configurationManagementTabItem = new TabItem(tabFolder, SWT.NONE);
        configurationManagementTabItem.setText("Configuration management");

        final Composite composite_6 = new Composite(tabFolder, SWT.NONE);
        composite_6.setLayout(new GridLayout());
        configurationManagementTabItem.setControl(composite_6);

        final Group configurationStorageGroup = new Group(composite_6, SWT.NONE);
        final GridData gd_configurationStorageGroup = new GridData(SWT.FILL, SWT.CENTER, true,
                                                                   false);
        configurationStorageGroup.setLayoutData(gd_configurationStorageGroup);

        final GridLayout gridLayout_7 = new GridLayout();
        gridLayout_7.numColumns = 2;
        configurationStorageGroup.setLayout(gridLayout_7);

        final Label configurationNameLabel = new Label(configurationStorageGroup, SWT.NONE);
        configurationNameLabel.setAlignment(SWT.RIGHT);
        configurationNameLabel.setLayoutData(new GridData(110, SWT.DEFAULT));
        configurationNameLabel.setText("Configuration name:");

        m_tConfigName = new Text(configurationStorageGroup, SWT.BORDER);

        final GridData gd_m_tConfigName = new GridData(SWT.FILL, SWT.CENTER, true, false);
        m_tConfigName.setLayoutData(gd_m_tConfigName);
        new Label(configurationStorageGroup, SWT.NONE);

        m_bSaveConfig = new Button(configurationStorageGroup, SWT.CHECK);
        m_bSaveConfig.setLayoutData(new GridData());
        m_bSaveConfig.setSelection(true);
        m_bSaveConfig.setText("Save configuration");

        final Composite composite_4 = new Composite(tabFolder, SWT.NONE);
        final GridLayout gridLayout_2 = new GridLayout();
        composite_4.setLayout(gridLayout_2);
        advancedTabItem.setControl(composite_4);

        final Group advancedConfigurationGroup = new Group(composite_4, SWT.NONE);
        advancedConfigurationGroup.setText(" Advanced configuration ");

        final GridData gd_advancedConfigurationGroup = new GridData(SWT.FILL, SWT.CENTER, true,
                                                                    false);
        advancedConfigurationGroup.setLayoutData(gd_advancedConfigurationGroup);

        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 4;
        advancedConfigurationGroup.setLayout(gridLayout);

        final Label gatewayUrlLabel = new Label(advancedConfigurationGroup, SWT.NONE);
        gatewayUrlLabel.setAlignment(SWT.RIGHT);
        gatewayUrlLabel.setText("Gateway URL:");

        m_tGatewayURL = new Text(advancedConfigurationGroup, SWT.BORDER);
        m_tGatewayURL.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
        m_tGatewayURL.setText("/cordys/com.eibus.web.soap.Gateway.wcp");

        final Label gatewayUrlLabel_1 = new Label(advancedConfigurationGroup, SWT.NONE);
        gatewayUrlLabel_1.setAlignment(SWT.RIGHT);
        gatewayUrlLabel_1.setText("Proxy server:");

        m_tProxyServer = new Text(advancedConfigurationGroup, SWT.BORDER);
        m_tProxyServer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Label gatewayUrlLabel_2 = new Label(advancedConfigurationGroup, SWT.NONE);
        gatewayUrlLabel_2.setAlignment(SWT.RIGHT);
        gatewayUrlLabel_2.setText("Proxy port:");

        m_tProxyPort = new Text(advancedConfigurationGroup, SWT.BORDER);
        m_tProxyPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

        final Label gatewayUrlLabel_3 = new Label(advancedConfigurationGroup, SWT.NONE);
        gatewayUrlLabel_3.setAlignment(SWT.RIGHT);
        gatewayUrlLabel_3.setText("Proxy user:");

        m_tProxyUsername = new Text(advancedConfigurationGroup, SWT.BORDER);
        m_tProxyUsername.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

        final Label gatewayUrlLabel_4 = new Label(advancedConfigurationGroup, SWT.NONE);
        gatewayUrlLabel_4.setAlignment(SWT.RIGHT);
        gatewayUrlLabel_4.setText("Proxy password:");

        m_tProxyPassword = new Text(advancedConfigurationGroup, SWT.BORDER | SWT.PASSWORD);
        m_tProxyPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

        final Label gatewayUrlLabel_4_1 = new Label(advancedConfigurationGroup, SWT.NONE);
        gatewayUrlLabel_4_1.setAlignment(SWT.RIGHT);
        gatewayUrlLabel_4_1.setText("Request timeout:");

        m_tRequestTimeout = new Text(advancedConfigurationGroup, SWT.BORDER);

        final GridData gd_m_tRequestTimeout = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
        m_tRequestTimeout.setLayoutData(gd_m_tRequestTimeout);

        final Label gatewayUrlLabel_4_2 = new Label(advancedConfigurationGroup, SWT.NONE);
        gatewayUrlLabel_4_2.setLayoutData(new GridData());
        gatewayUrlLabel_4_2.setAlignment(SWT.RIGHT);
        gatewayUrlLabel_4_2.setText("Network timeout:");

        m_tNetworkTimeout = new Text(advancedConfigurationGroup, SWT.BORDER);

        final GridData gd_m_tNetworkTimeout = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
        m_tNetworkTimeout.setLayoutData(gd_m_tNetworkTimeout);
        //
    }

    /**
     * This method enables the proper controls based on the selected values.
     */
    protected void enableProperControls()
    {
        if (m_bInitialized)
        {
            String sAuth = m_cAuthType.getText();

            // First check the authentication mode.
            if (AUTH_MODE_CLIENT_CERTIFICATE.equals(sAuth))
            {
                m_cSSL.setSelection(true);
                m_tCertificateLocation.setEnabled(true);
                m_tCertificatePassword.setEnabled(true);
                m_cCertificateType.setEnabled(true);
                m_bBrowseCertificate.setEnabled(true);

                m_tUsername.setEnabled(false);
                m_tPassword.setEnabled(false);
                m_tDomain.setEnabled(false);
            }
            else
            {
                m_tCertificateLocation.setEnabled(false);
                m_tCertificatePassword.setEnabled(false);
                m_cCertificateType.setEnabled(false);
                m_bBrowseCertificate.setEnabled(false);

                m_tUsername.setEnabled(true);
                m_tPassword.setEnabled(true);

                if (AUTH_MODE_NTLM.equals(sAuth))
                {
                    m_tDomain.setEnabled(true);
                }
                else if (AUTH_MODE_BASIC.equals(sAuth))
                {
                    m_tDomain.setEnabled(true);
                }
                else if (AUTH_MODE_CORDYS_CUSTOM.equals(sAuth))
                {
                    m_tDomain.setEnabled(false);
                }
                else if (AUTH_MODE_SSO.equals(sAuth))
                {
                    m_tDomain.setEnabled(false);
                }
            }

            // Now check the SSL mode.
            if (m_cSSL.getSelection() == true)
            {
                m_cTrustMode.setEnabled(true);

                String sSSLMode = m_cTrustMode.getText();

                if (TRUST_MODE_ALL_SERVERS.equals(sSSLMode))
                {
                    m_tTrustLocation.setEnabled(false);
                    m_tTrustPassword.setEnabled(false);
                    m_cTrustType.setEnabled(false);
                    m_bBrowseTrust.setEnabled(false);
                    m_bAcceptExpired.setEnabled(false);
                    m_bAcceptInvalid.setEnabled(false);
                }
                else if (TRUST_MODE_TRUSTSTORE.equals(sSSLMode))
                {
                    m_tTrustLocation.setEnabled(true);
                    m_tTrustPassword.setEnabled(true);
                    m_cTrustType.setEnabled(true);
                    m_bBrowseTrust.setEnabled(true);
                    m_bAcceptExpired.setEnabled(true);
                    m_bAcceptInvalid.setEnabled(true);
                }
            }
            else
            {
                // No SSL
                m_cTrustMode.setEnabled(false);
                m_tTrustLocation.setEnabled(false);
                m_tTrustPassword.setEnabled(false);
                m_cTrustType.setEnabled(false);
                m_bBrowseTrust.setEnabled(false);
                m_bAcceptExpired.setEnabled(false);
                m_bAcceptInvalid.setEnabled(false);
            }
        }
    }

    /**
     * This method first saves the configuration if the users wants to save it. Then it will create
     * the actual connection.
     */
    protected void makeConnection()
    {
        // First check if we need to update a configuration
        if (m_cCurrentConfiguration.getSelectionIndex() > 0)
        {
            // Possible update
            m_cConfig = (IWebGatewayConfiguration) m_cmManager.getConfiguration(m_cCurrentConfiguration
                                                                                .getText());

            if (m_cConfig != null)
            {
                m_cConfig.setName(m_tConfigName.getText());
                m_cConfig.setServername(m_tServer.getText());
                m_cConfig.setPort(Integer.parseInt(m_tPort.getText()));
                m_cConfig.setGatewayURL(m_tGatewayURL.getText());

                m_cConfig.setDomainUsername(m_tUsername.getText());
                m_cConfig.setDomainPassword(m_tPassword.getText());
                m_cConfig.setDomain(m_tDomain.getText());

                m_cConfig.setCertificateLocation(m_tCertificateLocation.getText());
                m_cConfig.setCertificatePassword(m_tCertificatePassword.getText());
                m_cConfig.setCertificateType(m_cCertificateType.getText());

                m_cConfig.setSSL(m_cSSL.getSelection());
                m_cConfig.setSSLTrustMode((m_cTrustMode.getSelectionIndex() == 0)
                                          ? ETrustMode.TRUST_EVERY_SERVER
                                          : ETrustMode.USE_TRUSTORE);
                m_cConfig.setTrustStoreLocation(m_tTrustLocation.getText());
                m_cConfig.setTrustStorePassword(m_tTrustPassword.getText());
                m_cConfig.setTrustStoreType(m_cTrustType.getText());

                m_cConfig.setAuthenticationType(getAuthenticationType());

                m_cConfig.setAcceptWhenExpired(m_bAcceptExpired.getSelection());
                m_cConfig.setAcceptWhenInvalid(m_bAcceptInvalid.getSelection());

                if (m_tRequestTimeout.getText().length() > 0)
                {
                    m_cConfig.setTimeout(Long.parseLong(m_tRequestTimeout.getText()));
                }

                if (m_tNetworkTimeout.getText().length() > 0)
                {
                    m_cConfig.setNetworkTimeout(Long.parseLong(m_tNetworkTimeout.getText()));
                }
                m_cConfig.setProxyHost(m_tProxyServer.getText());

                if (m_tProxyPort.getText().length() > 0)
                {
                    m_cConfig.setProxyPort(Integer.parseInt(m_tProxyPort.getText()));
                }
                m_cConfig.setProxyUsername(m_tProxyUsername.getText());
                m_cConfig.setProxyPassword(m_tProxyPassword.getText());
            }
        }
        else
        {
            // We need to create a new configuration.
            if (m_tConfigName.getText().length() == 0)
            {
                m_tConfigName.setText(m_tServer.getText() + ":" + m_tPort.getText());
            }

            m_cConfig = ConfigurationFactory.createNewWebGatewayConfiguration(m_tConfigName
                                                                              .getText(),
                                                                              m_tServer.getText(),
                                                                              Integer.parseInt(m_tPort
                                                                                               .getText()),
                                                                              m_tGatewayURL
                                                                              .getText(),
                                                                              m_tUsername.getText(),
                                                                              m_tPassword.getText(),
                                                                              m_tDomain.getText(),
                                                                              m_tCertificateLocation
                                                                              .getText(),
                                                                              m_tCertificatePassword
                                                                              .getText(),
                                                                              m_cCertificateType
                                                                              .getText(),
                                                                              m_tTrustLocation
                                                                              .getText(),
                                                                              m_tTrustPassword
                                                                              .getText(),
                                                                              m_cTrustType
                                                                              .getText());

            m_cConfig.setSSL(m_cSSL.getSelection());
            m_cConfig.setSSLTrustMode((m_cTrustMode.getSelectionIndex() == 0)
                                      ? ETrustMode.TRUST_EVERY_SERVER : ETrustMode.USE_TRUSTORE);
            m_cConfig.setAuthenticationType(getAuthenticationType());

            try
            {
                m_cmManager.addConfiguration(m_cConfig);
            }
            catch (ConfigurationManagerException e)
            {
                MessageBoxUtil.showError(m_sShell, "Error adding the configuration.", e);
            }
        }

        try
        {
            if (m_bSaveConfig.getSelection() == true)
            {
                m_cmManager.saveConfigurations();
            }
        }
        catch (ConfigurationManagerException e)
        {
            MessageBoxUtil.showError(m_sShell, "Error saving the configuration file.", e);
        }

        // Now make the actual connection
        if (m_cConfig != null)
        {
            m_bOk = true;
        }

        m_sShell.close();
    }

    /**
     * This method updates the display with the loaded configuration details.
     */
    protected void updateConfigTable()
    {
        if (m_cCurrentConfiguration.getSelectionIndex() > 0)
        {
            String sConfigName = m_cCurrentConfiguration.getText();
            IConfiguration cConfig = m_cmManager.getConfiguration(sConfigName);

            if (cConfig != null)
            {
                setConfigDetails((IWebGatewayConfiguration) cConfig);
            }
        }
        else
        {
            // Clean it.
            m_tServer.setText("");
            m_tPort.setText("80");
            m_tGatewayURL.setText("/cordys/com.eibus.web.soap.Gateway.wcp");
            m_tUsername.setText("");
            m_tPassword.setText("");
            m_tDomain.setText("");

            m_tCertificateLocation.setText("");
            m_tCertificatePassword.setText("");
            m_cCertificateType.setText("");

            m_cSSL.setSelection(false);
            m_tTrustLocation.setText("");
            m_tTrustPassword.setText("");
            m_cTrustType.setText("");

            m_tConfigName.setText("");

            m_cTrustMode.select(0);
            m_cAuthType.select(0);
        }
    }

    /**
     * This method returns the proper authentication mode for the selected one.
     *
     * @return  The proper authentication mode
     */
    private EAuthenticationType getAuthenticationType()
    {
        EAuthenticationType etReturn = EAuthenticationType.NTLM;

        String sAuth = m_cAuthType.getText();

        if (AUTH_MODE_CLIENT_CERTIFICATE.equals(sAuth))
        {
            etReturn = EAuthenticationType.CLIENT_CERTIFICATE;
        }
        else if (AUTH_MODE_NTLM.equals(sAuth))
        {
            etReturn = EAuthenticationType.NTLM;
        }
        else if (AUTH_MODE_BASIC.equals(sAuth))
        {
            etReturn = EAuthenticationType.BASIC;
        }
        else if (AUTH_MODE_CORDYS_CUSTOM.equals(sAuth))
        {
            etReturn = EAuthenticationType.CORDYS_CUSTOM;
        }
        else if (AUTH_MODE_SSO.equals(sAuth))
        {
            etReturn = EAuthenticationType.SSO;
        }

        return etReturn;
    }

    /**
     * This method sets the details based on the saved configuration.
     *
     * @param  cConfig  The configuration to display.
     */
    private void setConfigDetails(IWebGatewayConfiguration cConfig)
    {
        m_tServer.setText(cConfig.getServername());
        m_tPort.setText(String.valueOf(cConfig.getPort()));
        m_tGatewayURL.setText(cConfig.getGatewayURL());
        m_tUsername.setText(cConfig.getDomainUsername());
        m_tPassword.setText(cConfig.getDomainPassword());
        m_tDomain.setText(cConfig.getDomain());

        m_tCertificateLocation.setText(cConfig.getCertificateLocation());
        m_tCertificatePassword.setText(cConfig.getCertificatePassword());
        m_cCertificateType.setText(cConfig.getCertificateType());

        m_cSSL.setSelection(cConfig.getSSL());

        m_cTrustMode.setText(cConfig.getSSLTrustMode().name());
        m_tTrustLocation.setText(cConfig.getTrustStoreLocation());
        m_tTrustPassword.setText(cConfig.getTrustStorePassword());
        m_cTrustType.setText(cConfig.getTrustStoreType());

        m_tConfigName.setText(cConfig.getName());

        int iIndex = 0;

        // Current order: AUTH_MODE_NTLM, AUTH_MODE_BASIC, AUTH_MODE_CORDYS_CUSTOM, AUTH_MODE_SSO,
        // AUTH_MODE_CLIENT_CERTIFICATE
        switch (cConfig.getAuthenticationType())
        {
            case BASIC:
                iIndex = 1;
                break;

            case CLIENT_CERTIFICATE:
                iIndex = 4;
                break;

            case CORDYS_CUSTOM:
                iIndex = 2;
                break;

            case NTLM:
                iIndex = 0;
                break;

            case SSO:
                iIndex = 3;
                break;
        }
        m_cAuthType.select(iIndex);
    }
}
