package com.cordys.coe.util.swt;

import com.cordys.coe.util.config.ConfigurationFactory;
import com.cordys.coe.util.config.ConfigurationManager;
import com.cordys.coe.util.config.ConfigurationManagerException;
import com.cordys.coe.util.config.IConfiguration;
import com.cordys.coe.util.config.INativeConfiguration;
import com.cordys.coe.util.config.IWebGatewayConfiguration;
import com.cordys.coe.util.general.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

/**
 * Generic logon screen to log on to any Cordys installation. It right now supports only the
 * webgateway, but it should also support native connections.
 *
 * @author  pgussow
 */
public class CordysLogin extends Dialog
{
    /**
     * Indicates whether or not OK was pressed.
     */
    private boolean m_bOk = false;
    /**
     * DOCUMENTME.
     */
    private Button m_bSaveConfig;
    /**
     * Indicates whether or not the tab for native should be shown.
     */
    private boolean m_bShowNative;
    /**
     * DOCUMENTME.
     */
    private Button m_bUseCertAuth;
    /**
     * DOCUMENTME.
     */
    private Button m_cbSSL;
    /**
     * DOCUMENTME.
     */
    private Combo m_cCACertType;
    /**
     * DOCUMENTME.
     */
    private Combo m_cCATrustType;
    /**
     * Holds the configuration for this connection.
     */
    private IConfiguration m_cConfig = null;
    /**
     * DOCUMENTME.
     */
    private Combo m_cMethod;
    /**
     * DOCUMENTME.
     */
    private ConfigurationManager m_cmManager;
    /**
     * DOCUMENTME.
     */
    private Group m_gCertAuth;
    /**
     * DOCUMENTME.
     */
    private Group m_gDomainAuth;
    /**
     * DOCUMENTME.
     */
    private LinkedHashMap<String, IConfiguration> m_lhmConfigurations;
    /**
     * Holds the current shell.
     */
    private Shell m_sShell;
    /**
     * DOCUMENTME.
     */
    private Text m_tCACertLoc;
    /**
     * DOCUMENTME.
     */
    private Text m_tCACertPassword;
    /**
     * DOCUMENTME.
     */
    private Text m_tCATrustLoc;
    /**
     * DOCUMENTME.
     */
    private Text m_tCATrustPassword;
    /**
     * DOCUMENTME.
     */
    private Text m_tConfigName;
    /**
     * DOCUMENTME.
     */
    private Text m_tDADomain;
    /**
     * DOCUMENTME.
     */
    private Text m_tDAPassword;
    /**
     * DOCUMENTME.
     */
    private Text m_tDAUsername;
    /**
     * Holds the tab container.
     */
    private TabFolder m_tfTabs;
    /**
     * DOCUMENTME.
     */
    private Text m_tGatewayURL;
    /**
     * Holds teh tab for the native part.
     */
    private TabItem m_tiNative;
    /**
     * Holds the tab for the web gateway connection.
     */
    private TabItem m_tiWebGateway;
    /**
     * DOCUMENTME.
     */
    private Text m_tLDAPKeystore;
    /**
     * DOCUMENTME.
     */
    private Text m_tLDAPKeystorePassword;
    /**
     * DOCUMENTME.
     */
    private Text m_tLDAPPassword;
    /**
     * DOCUMENTME.
     */
    private Text m_tLDAPPort;
    /**
     * DOCUMENTME.
     */
    private Text m_tLDAPSearchRoot;
    /**
     * DOCUMENTME.
     */
    private Text m_tLDAPServer;
    /**
     * DOCUMENTME.
     */
    private Text m_tLDAPUsername;
    /**
     * DOCUMENTME.
     */
    private Text m_tPort;
    /**
     * DOCUMENTME.
     */
    private Text m_tServer;

    /**
     * Creates a new LDAPLogin object.
     *
     * @param  sParent  The parent shell.
     * @param  iStyle   The style.
     */
    public CordysLogin(Shell sParent, int iStyle)
    {
        this(sParent, iStyle, false);
    }

    /**
     * Creates a new CordysLogin object.
     *
     * @param  sParent      DOCUMENTME
     * @param  iStyle       DOCUMENTME
     * @param  bShowNative  DOCUMENTME
     */
    public CordysLogin(Shell sParent, int iStyle, boolean bShowNative)
    {
        super(sParent, iStyle);
        m_bShowNative = bShowNative;
    }

    /**
     * This method gets the configuration for the connection.
     *
     * @return  The configuration for the connection.
     */
    public IConfiguration getConfiguration()
    {
        return m_cConfig;
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
     * @return  DOCUMENTME
     */
    public Object open()
    {
        Shell parent = getParent();
        m_sShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        m_sShell.setText(getText());

        createContents();

        // Load the configuration.
        try
        {
            m_cmManager = ConfigurationManager.getInstance();
            m_lhmConfigurations = m_cmManager.getConfigurations();

            // Fill the combobox.
            ArrayList<String> alTemp = new ArrayList<String>(m_lhmConfigurations.size() + 1);
            alTemp.add("<New>");

            for (Iterator<String> iTemp = m_lhmConfigurations.keySet().iterator(); iTemp.hasNext();)
            {
                String sKey = iTemp.next();
                alTemp.add(sKey);
            }

            String[] saItems = alTemp.toArray(new String[alTemp.size()]);
            m_cMethod.setItems(saItems);
            m_cMethod.select(0);

            m_sShell.open();
            m_sShell.layout();

            Display display = parent.getDisplay();

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
            showErrorMsg(Util.getStackTrace(e));
        }

        return m_cConfig;
    }

    /**
     * Create contents of the window.
     */
    protected void createContents()
    {
        m_sShell = new Shell();

        final GridLayout gridLayout_1 = new GridLayout();
        m_sShell.setLayout(gridLayout_1);
        m_sShell.setSize(575, 592);
        m_sShell.setText("SWT Application");

        final Group group = new Group(m_sShell, SWT.NONE);
        group.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        group.setLayout(gridLayout);

        final Label connectionMethodLabel = new Label(group, SWT.NONE);
        connectionMethodLabel.setText("Connections:");

        m_cMethod = new Combo(group, SWT.READ_ONLY);
        m_cMethod.setItems(new String[] { "<New>" });
        m_cMethod.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e)
                {
                    updateConfigTable();
                }
            });
        m_cMethod.select(0);
        m_cMethod.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

        m_tfTabs = new TabFolder(m_sShell, SWT.NONE);
        m_tfTabs.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));

        m_tiWebGateway = new TabItem(m_tfTabs, SWT.NONE);
        m_tiWebGateway.setText("Web Gateway");

        final Composite composite_1 = new Composite(m_tfTabs, SWT.NONE);
        composite_1.setLayout(new GridLayout());
        m_tiWebGateway.setControl(composite_1);

        final Group webGatewayGroup = new Group(composite_1, SWT.NONE);
        webGatewayGroup.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

        final GridLayout gridLayout_2 = new GridLayout();
        gridLayout_2.numColumns = 4;
        webGatewayGroup.setLayout(gridLayout_2);
        webGatewayGroup.setText(" Web Gateway ");

        final Label servernameLabel = new Label(webGatewayGroup, SWT.NONE);
        servernameLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        servernameLabel.setText("Servername:");

        m_tServer = new Text(webGatewayGroup, SWT.BORDER);
        m_tServer.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

        final Label portLabel = new Label(webGatewayGroup, SWT.NONE);
        portLabel.setText("Port:");

        m_tPort = new Text(webGatewayGroup, SWT.BORDER);
        m_tPort.setText("80");
        m_tPort.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

        final Label gatewayUrlLabel = new Label(webGatewayGroup, SWT.NONE);
        gatewayUrlLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        gatewayUrlLabel.setText("Gateway URL:");

        m_tGatewayURL = new Text(webGatewayGroup, SWT.BORDER);
        m_tGatewayURL.setText("/cordys/com.eibus.web.soap.Gateway.wcp");
        m_tGatewayURL.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3,
                                                 1));
        new Label(webGatewayGroup, SWT.NONE);

        m_bUseCertAuth = new Button(webGatewayGroup, SWT.CHECK);
        m_bUseCertAuth.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e)
                {
                    enableDisableProperGroup();
                }
            });
        m_bUseCertAuth.setText("Use certificate authentication");
        new Label(webGatewayGroup, SWT.NONE);
        new Label(webGatewayGroup, SWT.NONE);

        m_gDomainAuth = new Group(webGatewayGroup, SWT.NONE);
        m_gDomainAuth.setText(" Domain Authentication ");

        final GridLayout gridLayout_3 = new GridLayout();
        gridLayout_3.numColumns = 2;
        m_gDomainAuth.setLayout(gridLayout_3);
        m_gDomainAuth.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 4,
                                                 1));

        final Label usernameLabel = new Label(m_gDomainAuth, SWT.NONE);
        usernameLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        usernameLabel.setText("Username:");

        m_tDAUsername = new Text(m_gDomainAuth, SWT.BORDER);

        final GridData gridData_2 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
        gridData_2.widthHint = 128;
        m_tDAUsername.setLayoutData(gridData_2);

        final Label passwordLabel = new Label(m_gDomainAuth, SWT.NONE);
        passwordLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        passwordLabel.setText("Password:");

        m_tDAPassword = new Text(m_gDomainAuth, SWT.BORDER);
        m_tDAPassword.setEchoChar('*');

        final GridData gridData_1 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
        gridData_1.widthHint = 161;
        m_tDAPassword.setLayoutData(gridData_1);

        final Label domainLabel = new Label(m_gDomainAuth, SWT.NONE);
        domainLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        domainLabel.setText("Domain");

        m_tDADomain = new Text(m_gDomainAuth, SWT.BORDER);

        final GridData gridData = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
        gridData.widthHint = 124;
        m_tDADomain.setLayoutData(gridData);

        m_gCertAuth = new Group(webGatewayGroup, SWT.NONE);
        m_gCertAuth.setEnabled(false);
        m_gCertAuth.setText(" Certificate Authentication ");
        m_gCertAuth.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 4, 1));

        final GridLayout gridLayout_4 = new GridLayout();
        gridLayout_4.numColumns = 2;
        m_gCertAuth.setLayout(gridLayout_4);

        final Label certificateLocationLabel = new Label(m_gCertAuth, SWT.NONE);
        certificateLocationLabel.setEnabled(false);
        certificateLocationLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false,
                                                            false));
        certificateLocationLabel.setText("Certificate location:");

        m_tCACertLoc = new Text(m_gCertAuth, SWT.BORDER);
        m_tCACertLoc.setEnabled(false);
        m_tCACertLoc.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

        final Label certificatePasswordLabel = new Label(m_gCertAuth, SWT.NONE);
        certificatePasswordLabel.setEnabled(false);
        certificatePasswordLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false,
                                                            false));
        certificatePasswordLabel.setText("Certificate password:");

        m_tCACertPassword = new Text(m_gCertAuth, SWT.BORDER);
        m_tCACertPassword.setEnabled(false);
        m_tCACertPassword.setEchoChar('*');

        final GridData gridData_3 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
        gridData_3.widthHint = 155;
        m_tCACertPassword.setLayoutData(gridData_3);

        final Label certificateTypeLabel = new Label(m_gCertAuth, SWT.NONE);
        certificateTypeLabel.setEnabled(false);
        certificateTypeLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false,
                                                        false));
        certificateTypeLabel.setAlignment(SWT.RIGHT);
        certificateTypeLabel.setText("Certificate Type:");

        m_cCACertType = new Combo(m_gCertAuth, SWT.NONE);
        m_cCACertType.setItems(new String[] { "JKS", "PKCS12" });
        m_cCACertType.setEnabled(false);

        final GridData gridData_5 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
        gridData_5.widthHint = 181;
        m_cCACertType.setLayoutData(gridData_5);

        final Label trustStoreLabel = new Label(m_gCertAuth, SWT.NONE);
        trustStoreLabel.setEnabled(false);
        trustStoreLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        trustStoreLabel.setText("Trust store:");

        m_tCATrustLoc = new Text(m_gCertAuth, SWT.BORDER);
        m_tCATrustLoc.setEnabled(false);
        m_tCATrustLoc.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

        final Label trustPasswordLabel = new Label(m_gCertAuth, SWT.NONE);
        trustPasswordLabel.setEnabled(false);
        trustPasswordLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        trustPasswordLabel.setText("Trust password:");

        m_tCATrustPassword = new Text(m_gCertAuth, SWT.BORDER);
        m_tCATrustPassword.setEnabled(false);
        m_tCATrustPassword.setEchoChar('*');

        final GridData gridData_4 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
        gridData_4.widthHint = 177;
        m_tCATrustPassword.setLayoutData(gridData_4);

        final Label trustStoreTypeLabel = new Label(m_gCertAuth, SWT.NONE);
        trustStoreTypeLabel.setEnabled(false);
        trustStoreTypeLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false,
                                                       false));
        trustStoreTypeLabel.setText("Trust store type:");

        m_cCATrustType = new Combo(m_gCertAuth, SWT.NONE);
        m_cCATrustType.setItems(new String[] { "JKS", "PKCS12" });
        m_cCATrustType.setEnabled(false);

        final GridData gridData_6 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
        gridData_6.widthHint = 205;
        m_cCATrustType.setLayoutData(gridData_6);

        m_tiNative = new TabItem(m_tfTabs, SWT.NONE);
        m_tiNative.setText("Native");

        final Composite ctiNative = new Composite(m_tfTabs, SWT.NONE);
        ctiNative.setLayout(new GridLayout());
        m_tiNative.setControl(ctiNative);

        final Group gLDAPGroup = new Group(ctiNative, SWT.NONE);
        gLDAPGroup.setText(" LDAP Details ");
        gLDAPGroup.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

        GridLayout gridLayout_16 = new GridLayout();
        gridLayout_16.marginRight = 5;
        gridLayout_16.marginLeft = 5;
        gridLayout_16.numColumns = 4;
        gLDAPGroup.setLayout(gridLayout_16);

        final Label ldapServerLabel = new Label(gLDAPGroup, SWT.NONE);
        ldapServerLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        ldapServerLabel.setText("LDAP Server:");

        m_tLDAPServer = new Text(gLDAPGroup, SWT.BORDER);

        final GridData gridData_8 = new GridData(SWT.FILL, SWT.CENTER, false, false);
        gridData_8.widthHint = 162;
        m_tLDAPServer.setLayoutData(gridData_8);

        final Label label_1 = new Label(gLDAPGroup, SWT.NONE);
        label_1.setText(":");

        m_tLDAPPort = new Text(gLDAPGroup, SWT.BORDER);
        m_tLDAPPort.setLayoutData(new GridData(34, SWT.DEFAULT));

        final Label userLabel = new Label(gLDAPGroup, SWT.NONE);
        userLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        userLabel.setText("User:");

        m_tLDAPUsername = new Text(gLDAPGroup, SWT.BORDER);
        m_tLDAPUsername.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 3,
                                                   1));

        Label passwordLabel_16 = new Label(gLDAPGroup, SWT.NONE);
        passwordLabel_16.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        passwordLabel_16.setText("Password:");

        m_tLDAPPassword = new Text(gLDAPGroup, SWT.BORDER);
        m_tLDAPPassword.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 3,
                                                   1));
        m_tLDAPPassword.setEchoChar('*');

        final Label searchRootLabel_1 = new Label(gLDAPGroup, SWT.NONE);
        searchRootLabel_1.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        searchRootLabel_1.setText("Search root:");

        m_tLDAPSearchRoot = new Text(gLDAPGroup, SWT.BORDER);
        m_tLDAPSearchRoot.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false,
                                                     3, 1));

        final Label keyStoreLabel = new Label(gLDAPGroup, SWT.NONE);
        keyStoreLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        keyStoreLabel.setText("Key store:");

        m_tLDAPKeystore = new Text(gLDAPGroup, SWT.BORDER);
        m_tLDAPKeystore.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

        final Label keyStorePasswordLabel = new Label(gLDAPGroup, SWT.NONE);
        keyStorePasswordLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        keyStorePasswordLabel.setText("Key store password:");

        m_tLDAPKeystorePassword = new Text(gLDAPGroup, SWT.BORDER);
        m_tLDAPKeystorePassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3,
                                                           1));

        final Label sslLabel = new Label(gLDAPGroup, SWT.NONE);
        sslLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
        sslLabel.setText("SSL:");

        m_cbSSL = new Button(gLDAPGroup, SWT.CHECK);
        m_cbSSL.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 3,
                                           1));

        final Group group_1 = new Group(m_sShell, SWT.NONE);
        group_1.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));

        final GridLayout gridLayout_6 = new GridLayout();
        gridLayout_6.numColumns = 2;
        group_1.setLayout(gridLayout_6);
        new Label(group_1, SWT.NONE);

        m_bSaveConfig = new Button(group_1, SWT.CHECK);
        m_bSaveConfig.setLayoutData(new GridData());
        m_bSaveConfig.setSelection(true);
        m_bSaveConfig.setText("Save configuration");

        final Label configurationNameLabel = new Label(group_1, SWT.NONE);
        configurationNameLabel.setLayoutData(new GridData());
        configurationNameLabel.setText("Configuration name:");

        m_tConfigName = new Text(group_1, SWT.BORDER);
        m_tConfigName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));

        final Composite composite = new Composite(group_1, SWT.NONE);
        final GridData gridData_7 = new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1);
        gridData_7.widthHint = 379;
        composite.setLayoutData(gridData_7);

        final GridLayout gridLayout_5 = new GridLayout();
        gridLayout_5.numColumns = 4;
        composite.setLayout(gridLayout_5);

        final Label label = new Label(composite, SWT.NONE);
        label.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));

        final Button bConnect = new Button(composite, SWT.NONE);
        bConnect.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e)
                {
                    makeConnection();
                    m_sShell.close();
                }
            });
        bConnect.setText("C&onnect");

        final Button bCancel = new Button(composite, SWT.NONE);
        bCancel.setLayoutData(new GridData());
        bCancel.setText("&Cancel");
        bCancel.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e)
                {
                    m_sShell.close();
                }
            });

        Label llabel_1 = new Label(composite, SWT.NONE);
        llabel_1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));

        //
        if (m_bShowNative)
        {
            m_tfTabs.setSelection(m_tiNative);
        }
    }

    /**
     * This method enables disables the groups.
     */
    protected void enableDisableProperGroup()
    {
        if (m_bUseCertAuth.getSelection())
        {
            // It's certificate authnetication
            setEnableForGroup(true, m_gCertAuth);
            setEnableForGroup(false, m_gDomainAuth);
        }
        else
        {
            // It's NTLM authentication
            setEnableForGroup(false, m_gCertAuth);
            setEnableForGroup(true, m_gDomainAuth);
        }
    }

    /**
     * This method first saves the configuration if the users wants to save it. Then it will create
     * the actual connection.
     */
    protected void makeConnection()
    {
        // First check if we need to update a configuration
        if (m_cMethod.getSelectionIndex() > 0)
        {
            // Possible update
            m_cConfig = m_cmManager.getConfiguration(m_cMethod.getText());

            if (m_cConfig != null)
            {
                if (m_cConfig.getType() == IConfiguration.TYPE_WEBGATEWAY)
                {
                    IWebGatewayConfiguration wgc = (IWebGatewayConfiguration) m_cConfig;
                    wgc.setName(m_tConfigName.getText());
                    wgc.setServername(m_tServer.getText());
                    wgc.setPort(Integer.parseInt(m_tPort.getText()));
                    wgc.setGatewayURL(m_tGatewayURL.getText());
                    wgc.setDomainUsername(m_tDAUsername.getText());
                    wgc.setDomainPassword(m_tDAPassword.getText());
                    wgc.setDomain(m_tDADomain.getText());
                    wgc.setCertificateLocation(m_tCACertLoc.getText());
                    wgc.setCertificatePassword(m_tCACertPassword.getText());
                    wgc.setCertificateType(m_cCACertType.getText());
                    wgc.setTrustStoreLocation(m_tCATrustLoc.getText());
                    wgc.setTrustStorePassword(m_tCATrustPassword.getText());
                    wgc.setTrustStoreType(m_cCATrustType.getText());
                }
                else if (m_cConfig.getType() == IConfiguration.TYPE_NATIVE)
                {
                    INativeConfiguration nc = (INativeConfiguration) m_cConfig;

                    nc.setServername(m_tLDAPServer.getText());
                    nc.setLDAPUsername(m_tLDAPUsername.getText());
                    nc.setLDAPPassword(m_tLDAPPassword.getText());
                    nc.setLDAPSearchRoot(m_tLDAPSearchRoot.getText());
                    nc.setPort(Integer.parseInt(m_tLDAPPort.getText()));
                    nc.setSSL(m_cbSSL.getSelection());
                }
            }
        }
        else
        {
            // We need to create a new configuration.
            if (m_tConfigName.getText().length() == 0)
            {
                m_tConfigName.setText(m_tServer.getText() + ":" + m_tPort.getText());
            }

            if (m_tfTabs.getSelectionIndex() == 0)
            {
                m_cConfig = ConfigurationFactory.createNewWebGatewayConfiguration(m_tConfigName
                                                                                  .getText(),
                                                                                  m_tServer
                                                                                  .getText(),
                                                                                  Integer.parseInt(m_tPort
                                                                                                   .getText()),
                                                                                  m_tGatewayURL
                                                                                  .getText(),
                                                                                  m_tDAUsername
                                                                                  .getText(),
                                                                                  m_tDAPassword
                                                                                  .getText(),
                                                                                  m_tDADomain
                                                                                  .getText(),
                                                                                  m_tCACertLoc
                                                                                  .getText(),
                                                                                  m_tCACertPassword
                                                                                  .getText(),
                                                                                  m_cCACertType
                                                                                  .getText(),
                                                                                  m_tCATrustLoc
                                                                                  .getText(),
                                                                                  m_tCATrustPassword
                                                                                  .getText(),
                                                                                  m_cCATrustType
                                                                                  .getText());
            }
            else
            {
                m_cConfig = ConfigurationFactory.createNewNativeConfiguration(m_tConfigName
                                                                              .getText(),
                                                                              m_tLDAPServer
                                                                              .getText(),
                                                                              Integer.parseInt(m_tLDAPPort
                                                                                               .getText()),
                                                                              m_tLDAPUsername
                                                                              .getText(),
                                                                              m_tLDAPPassword
                                                                              .getText(),
                                                                              m_tLDAPSearchRoot
                                                                              .getText(),
                                                                              m_cbSSL
                                                                              .getSelection());
            }

            try
            {
                m_cmManager.addConfiguration(m_cConfig);
            }
            catch (ConfigurationManagerException e)
            {
                showErrorMsg("Error adding the configuration.\n" + Util.getStackTrace(e));
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
            showErrorMsg("Error saving the configuration file.\n" + Util.getStackTrace(e));
        }

        // Now make the actual connection
        if (m_cConfig != null)
        {
            m_bOk = true;
        }
    }

    /**
     * This method updates the details based on the selected configuration.
     */
    protected void updateConfigTable()
    {
        if (m_cMethod.getSelectionIndex() > 0)
        {
            String sConfigName = m_cMethod.getText();
            IConfiguration cConfig = m_cmManager.getConfiguration(sConfigName);

            if (cConfig != null)
            {
                setConfigDetails(cConfig);
            }
        }
        else
        {
            // Clean it.
            m_tServer.setText("");
            m_tPort.setText("80");
            m_tGatewayURL.setText("/cordys/com.eibus.web.soap.Gateway.wcp");
            m_tDAUsername.setText("");
            m_tDAPassword.setText("");
            m_tDADomain.setText("");
            m_tCACertLoc.setText("");
            m_tCACertPassword.setText("");
            m_cCACertType.setText("");
            m_tCATrustLoc.setText("");
            m_tCATrustPassword.setText("");
            m_cCATrustType.setText("");
            m_tConfigName.setText("");
        }
    }

    /**
     * This method fills the screen with the proper configuration details.
     *
     * @param  cConfig  the configuration to show.
     */
    private void setConfigDetails(IConfiguration cConfig)
    {
        m_tConfigName.setText(cConfig.getName());

        if (cConfig instanceof IWebGatewayConfiguration)
        {
            IWebGatewayConfiguration wgc = (IWebGatewayConfiguration) cConfig;

            m_tServer.setText(wgc.getServername());
            m_tPort.setText(String.valueOf(wgc.getPort()));
            m_tGatewayURL.setText(wgc.getGatewayURL());
            m_tDAUsername.setText(wgc.getDomainUsername());
            m_tDAPassword.setText(wgc.getDomainPassword());
            m_tDADomain.setText(wgc.getDomain());
            m_tCACertLoc.setText(wgc.getCertificateLocation());
            m_tCACertPassword.setText(wgc.getCertificatePassword());
            m_cCACertType.setText(wgc.getCertificateType());
            m_tCATrustLoc.setText(wgc.getTrustStoreLocation());
            m_tCATrustPassword.setText(wgc.getTrustStorePassword());
            m_cCATrustType.setText(wgc.getTrustStoreType());

            // Select the webgateway tab.
            m_tfTabs.setSelection(new TabItem[] { m_tiWebGateway });
        }
        else if (cConfig instanceof INativeConfiguration)
        {
            INativeConfiguration ncConfig = (INativeConfiguration) cConfig;

            m_tLDAPServer.setText(ncConfig.getServername());
            m_tLDAPUsername.setText(ncConfig.getLDAPUsername());
            m_tLDAPPort.setText(String.valueOf(ncConfig.getPort()));
            m_tLDAPSearchRoot.setText(ncConfig.getLDAPSearchRoot());
            m_cbSSL.setSelection(ncConfig.isSSL());
            m_tLDAPPassword.setText(ncConfig.getLDAPPassword());

            // Select the native tab
            m_tfTabs.setSelection(new TabItem[] { m_tiNative });
        }
    }

    /**
     * This method sets the enabled for a group and all it's children.
     *
     * @param  bEnable  Whether or not to enable the controls.
     * @param  gGroup   The group to enable/disable.
     */
    private void setEnableForGroup(boolean bEnable, Group gGroup)
    {
        Control[] acChildren = gGroup.getChildren();

        for (int iCount = 0; iCount < acChildren.length; iCount++)
        {
            Control cControl = acChildren[iCount];

            if ((cControl instanceof Label) || (cControl instanceof Text) ||
                    (cControl instanceof Combo))
            {
                cControl.setEnabled(bEnable);
            }
        }
        gGroup.setEnabled(bEnable);
    }

    /**
     * Displatys an alert with the message.
     *
     * @param  sMSG  The message to display.
     */
    private void showErrorMsg(String sMSG)
    {
        MessageBox mb = new MessageBox(m_sShell, SWT.ICON_ERROR | SWT.OK);
        mb.setMessage(sMSG);
        mb.open();
    }
}
