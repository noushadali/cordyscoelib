package com.cordys.coe.util.swing;

import com.cordys.coe.util.cgc.config.EAuthenticationType;
import com.cordys.coe.util.cgc.config.ETrustMode;
import com.cordys.coe.util.config.ConfigurationFactory;
import com.cordys.coe.util.config.ConfigurationManager;
import com.cordys.coe.util.config.ConfigurationManagerException;
import com.cordys.coe.util.config.IConfiguration;
import com.cordys.coe.util.config.IWebGatewayConfiguration;

import java.io.File;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

/**
 * The login panel Swing based.
 *
 * @author  pgussow
 */
public class CGCLoginPanel extends javax.swing.JPanel
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
     * Holds the button to accept certificates when they are expired.
     */
    private javax.swing.JCheckBox m_bAcceptExpired;
    /**
     * Holds the button to accept certificates when they are invalid.
     */
    private javax.swing.JCheckBox m_bAcceptInvalid;
    /**
     * Holds the button which shows the file browser to browse for a client certificate.
     */
    private javax.swing.JButton m_bBrowseCertificate;
    /**
     * Holds the button which shows the file browser to browse for a trust store.
     */
    private javax.swing.JButton m_bBrowseTrust;
    /**
     * Variables declaration - do not modify.
     */
    private javax.swing.JButton m_bCancel;
    /**
     * Holds the connect button.
     */
    private javax.swing.JButton m_bConnect;
    /**
     * Holds whether or not the connect/cancel button should be shown.
     */
    private boolean m_bCreateButtons;
    /**
     * Holds whether or not the panel is initialized.
     */
    private boolean m_bInitialized;
    /**
     * Holds whether or not the connect button is pressed.
     */
    private boolean m_bOk;
    /**
     * Holds whether or not the configuration should be saved.
     */
    private javax.swing.JCheckBox m_bSaveConfig;
    /**
     * Holds the button to test the connection.
     */
    private JButton m_bTest;
    /**
     * Holds whether or not the connection manager should be used.
     */
    private boolean m_bUseConnectionManager;
    /**
     * Holds the drop down with the authentication types.
     */
    private javax.swing.JComboBox<String> m_cAuthType;
    /**
     * Holds the drop down for the certificate types.
     */
    private javax.swing.JComboBox<String> m_cCertificateType;
    /**
     * Holds the actual web gateway configuration to use.
     */
    private IWebGatewayConfiguration m_cConfig;
    /**
     * Holds the list of currently saved configurations.
     */
    private javax.swing.JComboBox<String> m_cCurrentConfiguration;
    /**
     * Holds the configuration manager to use.
     */
    private ConfigurationManager m_cmManager;
    /**
     * Holds whether or not the server is running in SSL mode.
     */
    private javax.swing.JCheckBox m_cSSL;
    /**
     * Holds the trust mode to use.
     */
    private javax.swing.JComboBox<String> m_cTrustMode;
    /**
     * Holds the trust store type.
     */
    private javax.swing.JComboBox<String> m_cTrustType;
    /**
     * Holds the currently selected configuration.
     */
    private javax.swing.JLabel m_lCurrentConfig;
    /**
     * Holds the list with all stored configurations.
     */
    private LinkedHashMap<String, IWebGatewayConfiguration> m_lhmConfigurations;
    /**
     * Holds the panel containing all the buttons.
     */
    private javax.swing.JPanel m_pButtonPanel;
    /**
     * Holds the panel which contains the configuration management controls.
     */
    private javax.swing.JPanel m_pConfigManagement;
    /**
     * Temporary variable for the server name-change-detection.
     */
    private String m_sBefore = "";
    /**
     * Temporary variable for the server name-change-detection.
     */
    private String m_sServer = "";
    /**
     * Holds the certificate location.
     */
    private javax.swing.JTextField m_tCertificateLocation;
    /**
     * Holds the certificate password.
     */
    private javax.swing.JPasswordField m_tCertificatePassword;
    /**
     * Holds the name of the configuration.
     */
    private javax.swing.JTextField m_tConfigName;
    /**
     * Holds the NT domain for authentication.
     */
    private javax.swing.JTextField m_tDomain;
    /**
     * Holds the URL of the gateway.
     */
    private javax.swing.JTextField m_tGatewayURL;
    /**
     * Holds the network timeout.
     */
    private javax.swing.JTextField m_tNetworkTimeout;
    /**
     * Holds the password.
     */
    private javax.swing.JPasswordField m_tPassword;
    /**
     * Holds the port to connect to.
     */
    private javax.swing.JTextField m_tPort;
    /**
     * Holds the password for the proxy server.
     */
    private javax.swing.JPasswordField m_tProxyPassword;
    /**
     * Holds the proxy port.
     */
    private javax.swing.JTextField m_tProxyPort;
    /**
     * Holds the proxy server name.
     */
    private javax.swing.JTextField m_tProxyServer;
    /**
     * Holds the proxy username.
     */
    private javax.swing.JTextField m_tProxyUsername;
    /**
     * Holds the timeout for the requests.
     */
    private javax.swing.JTextField m_tRequestTimeout;
    /**
     * Holds the name of the server.
     */
    private javax.swing.JTextField m_tServer;
    /**
     * Holds the trust store location.
     */
    private javax.swing.JTextField m_tTrustLocation;
    /**
     * Holds the truststore password.
     */
    private javax.swing.JPasswordField m_tTrustPassword;
    /**
     * Holds the username.
     */
    private javax.swing.JTextField m_tUsername;

    /**
     * Creates new form CordysLoginPanel.
     */
    public CGCLoginPanel()
    {
        this(true, true);
    }

    /**
     * Creates new form CordysLoginPanel.
     *
     * @param  bCreateButtons         Whether or not the connect/cancel button should be shown.
     * @param  bUseConnectionManager  Whether or not the connection manager should be used.
     */
    public CGCLoginPanel(boolean bCreateButtons, boolean bUseConnectionManager)
    {
        m_bCreateButtons = bCreateButtons;
        m_bUseConnectionManager = bUseConnectionManager;

        initComponents();

        m_bInitialized = false;
    }

    /**
     * Open the window.
     *
     * @return  The actual ICordysGatewayClient to use.
     */
    public IWebGatewayConfiguration getConfiguration()
    {
        if (isOk())
        {
            return m_cConfig;
        }

        return null;
    }

    /**
     * This method initializes the panel. It will read all configurations from the file and display
     * them.
     */
    public void initialize()
    {
        try
        {
            if (m_bUseConnectionManager)
            {
                m_cmManager = ConfigurationManager.getInstance();
                m_lhmConfigurations = m_cmManager.getWebGatewayConfigurations();
                m_cCurrentConfiguration.removeAllItems();

                // Fill the combobox.
                m_cCurrentConfiguration.addItem("<New>");

                for (Iterator<String> iTemp = m_lhmConfigurations.keySet().iterator();
                         iTemp.hasNext();)
                {
                    String sKey = iTemp.next();
                    m_cCurrentConfiguration.addItem(sKey);
                }

                m_cCurrentConfiguration.setSelectedIndex(0);
            }

            // Set the default selected
            m_cAuthType.setSelectedIndex(0);
            m_cTrustMode.setSelectedIndex(0);
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(this, "Error loading configuration", "Error",
                                          JOptionPane.ERROR_MESSAGE);
        }

        m_bInitialized = true;
    }

    /**
     * Returns whether or not the logon was succesfull.
     *
     * @return  Whether or not the logon was succesfull.
     */
    public boolean isOk()
    {
        return m_bOk;
    }

    /**
     * This method opens a file browser to pick the proper certificate file.
     *
     * @param  tLocation  The text control where the selected path should be put into.
     */
    protected void browseFile(JTextField tLocation)
    {
        JFileChooser fcFile = new JFileChooser(new File("."));
        int iReturn = fcFile.showOpenDialog(this);

        if (iReturn == JFileChooser.APPROVE_OPTION)
        {
            tLocation.setText(fcFile.getSelectedFile().getAbsolutePath());
        }
    }

    /**
     * This method enables the proper controls based on the selected values.
     */
    protected void enableProperControls()
    {
        if (m_bInitialized)
        {
            String sAuth = m_cAuthType.getSelectedItem().toString();

            // First check the authentication mode.
            if (AUTH_MODE_CLIENT_CERTIFICATE.equals(sAuth))
            {
                m_cSSL.setSelected(true);
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
            if (m_cSSL.isSelected() == true)
            {
                m_cTrustMode.setEnabled(true);

                String sSSLMode = m_cTrustMode.getSelectedItem().toString();

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
     * This method is called when the cancel button is pressed.
     */
    protected void handleCancelPressed()
    {
        m_bOk = false;
    }

    /**
     * This method handles the pressing of the conenct button. The only thing done here is make a
     * call to the makeConnection method.
     */
    protected void handleConnect()
    {
        makeConnection();
    }

    /**
     * This method is called when the test button is pressed.
     */
    protected void handleTestPressed()
    {
    }

    /**
     * This method first saves the configuration if the users wants to save it. Then it will create
     * the actual connection.
     */
    protected void makeConnection()
    {
        // First check if we need to update a configuration
        if ((m_bUseConnectionManager == true) && (m_cCurrentConfiguration.getSelectedIndex() > 0))
        {
            // Possible update
            m_cConfig = (IWebGatewayConfiguration) m_cmManager.getConfiguration(m_cCurrentConfiguration
                                                                                .getSelectedItem()
                                                                                .toString());

            if (m_cConfig != null)
            {
                m_cConfig.setName(m_tConfigName.getText());
                m_cConfig.setServername(m_tServer.getText());
                m_cConfig.setPort(Integer.parseInt(m_tPort.getText()));
                m_cConfig.setGatewayURL(m_tGatewayURL.getText());

                m_cConfig.setDomainUsername(m_tUsername.getText());
                m_cConfig.setDomainPassword(new String(m_tPassword.getPassword()));
                m_cConfig.setDomain(m_tDomain.getText());

                m_cConfig.setCertificateLocation(m_tCertificateLocation.getText());
                m_cConfig.setCertificatePassword(new String(m_tCertificatePassword.getPassword()));
                m_cConfig.setCertificateType(m_cCertificateType.getSelectedItem().toString());

                m_cConfig.setSSL(m_cSSL.isSelected());
                m_cConfig.setSSLTrustMode((m_cTrustMode.getSelectedIndex() == 0)
                                          ? ETrustMode.TRUST_EVERY_SERVER
                                          : ETrustMode.USE_TRUSTORE);
                m_cConfig.setTrustStoreLocation(m_tTrustLocation.getText());
                m_cConfig.setTrustStorePassword(new String(m_tTrustPassword.getPassword()));
                m_cConfig.setTrustStoreType(m_cTrustType.getSelectedItem().toString());

                m_cConfig.setAuthenticationType(getAuthenticationType());

                m_cConfig.setAcceptWhenExpired(m_bAcceptExpired.isSelected());
                m_cConfig.setAcceptWhenInvalid(m_bAcceptInvalid.isSelected());

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
                m_cConfig.setProxyPassword(new String(m_tProxyPassword.getPassword()));

                try
                {
                    m_cConfig.checkValidity();
                }
                catch (ConfigurationManagerException e)
                {
                    JOptionPane.showMessageDialog(this, e.getShortMessage(),
                                                  "Error: " + e.getErrorCode(),
                                                  JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        else
        {
            String sName = m_tServer.getText() + ":" + m_tPort.getText();

            if (m_bUseConnectionManager)
            {
                // We need to create a new configuration.
                if (m_tConfigName.getText().length() == 0)
                {
                    m_tConfigName.setText(m_tServer.getText() + ":" + m_tPort.getText());
                }
            }

            m_cConfig = ConfigurationFactory.createNewWebGatewayConfiguration(sName,
                                                                              m_tServer.getText(),
                                                                              Integer.parseInt(m_tPort
                                                                                               .getText()),
                                                                              m_tGatewayURL
                                                                              .getText(),
                                                                              m_tUsername.getText(),
                                                                              new String(m_tPassword
                                                                                         .getPassword()),
                                                                              m_tDomain.getText(),
                                                                              m_tCertificateLocation
                                                                              .getText(),
                                                                              new String(m_tCertificatePassword
                                                                                         .getPassword()),
                                                                              m_cCertificateType
                                                                              .getSelectedItem()
                                                                              .toString(),
                                                                              m_tTrustLocation
                                                                              .getText(),
                                                                              new String(m_tTrustPassword
                                                                                         .getPassword()),
                                                                              m_cTrustType
                                                                              .getSelectedItem()
                                                                              .toString());

            m_cConfig.setServername(m_tServer.getText());
            m_cConfig.setPort(Integer.parseInt(m_tPort.getText()));
            m_cConfig.setGatewayURL(m_tGatewayURL.getText());

            m_cConfig.setDomainUsername(m_tUsername.getText());
            m_cConfig.setDomainPassword(new String(m_tPassword.getPassword()));
            m_cConfig.setDomain(m_tDomain.getText());

            m_cConfig.setCertificateLocation(m_tCertificateLocation.getText());
            m_cConfig.setCertificatePassword(new String(m_tCertificatePassword.getPassword()));
            m_cConfig.setCertificateType(m_cCertificateType.getSelectedItem().toString());

            m_cConfig.setSSL(m_cSSL.isSelected());
            m_cConfig.setSSLTrustMode((m_cTrustMode.getSelectedIndex() == 0)
                                      ? ETrustMode.TRUST_EVERY_SERVER : ETrustMode.USE_TRUSTORE);
            m_cConfig.setTrustStoreLocation(m_tTrustLocation.getText());
            m_cConfig.setTrustStorePassword(new String(m_tTrustPassword.getPassword()));
            m_cConfig.setTrustStoreType(m_cTrustType.getSelectedItem().toString());

            m_cConfig.setAuthenticationType(getAuthenticationType());

            m_cConfig.setAcceptWhenExpired(m_bAcceptExpired.isSelected());
            m_cConfig.setAcceptWhenInvalid(m_bAcceptInvalid.isSelected());

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
            m_cConfig.setProxyPassword(new String(m_tProxyPassword.getPassword()));

            try
            {
                m_cConfig.checkValidity();

                if (m_bUseConnectionManager == true)
                {
                    m_cmManager.addConfiguration(m_cConfig);
                }
            }
            catch (ConfigurationManagerException e)
            {
                JOptionPane.showMessageDialog(this, e.getShortMessage(),
                                              "Error: " + e.getErrorCode(),
                                              JOptionPane.ERROR_MESSAGE);
            }
        }

        try
        {
            if ((m_cConfig != null) && m_cConfig.isValid())
            {
                if ((m_bUseConnectionManager == true) && (m_bSaveConfig.isSelected() == true))
                {
                    m_cmManager.saveConfigurations();
                }
            }
        }
        catch (ConfigurationManagerException e)
        {
            JOptionPane.showMessageDialog(this, e.getShortMessage(), "Error: " + e.getErrorCode(),
                                          JOptionPane.ERROR_MESSAGE);
        }

        // Now make the actual connection
        if (m_cConfig != null)
        {
            m_bOk = true;
        }
    }

    /**
     * This method sets the details based on the saved configuration.
     *
     * @param  cConfig  The configuration to display.
     */
    protected void setConfigDetails(IWebGatewayConfiguration cConfig)
    {
        m_tServer.setText(cConfig.getServername());
        m_tPort.setText(String.valueOf(cConfig.getPort()));
        m_tGatewayURL.setText(cConfig.getGatewayURL());
        m_tUsername.setText(cConfig.getDomainUsername());
        m_tPassword.setText(cConfig.getDomainPassword());
        m_tDomain.setText(cConfig.getDomain());

        m_tCertificateLocation.setText(cConfig.getCertificateLocation());
        m_tCertificatePassword.setText(cConfig.getCertificatePassword());
        m_cCertificateType.setSelectedItem(cConfig.getCertificateType());

        m_cSSL.setSelected(cConfig.getSSL());

        m_cTrustMode.setSelectedItem(cConfig.getSSLTrustMode().name());
        m_tTrustLocation.setText(cConfig.getTrustStoreLocation());
        m_tTrustPassword.setText(cConfig.getTrustStorePassword());
        m_cTrustType.setSelectedItem(cConfig.getTrustStoreType());

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
        m_cAuthType.setSelectedIndex(iIndex);
    }

    /**
     * This method is called when the test-button is pressed.
     */
    protected void testConnection()
    {
    }

    /**
     * This method updates the display with the loaded configuration details.
     */
    protected void updateConfigTable()
    {
        if (m_cCurrentConfiguration.getSelectedIndex() > 0)
        {
            String sConfigName = m_cCurrentConfiguration.getSelectedItem().toString();
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
            m_cCertificateType.setSelectedItem("");

            m_cSSL.setSelected(false);
            m_tTrustLocation.setText("");
            m_tTrustPassword.setText("");
            m_cTrustType.setSelectedItem("");

            m_tConfigName.setText("");

            m_cTrustMode.setSelectedIndex(0);
            m_cAuthType.setSelectedIndex(0);
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

        String sAuth = m_cAuthType.getSelectedItem().toString();

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
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    private void initComponents()
    {
        JLabel jLabel25 = new javax.swing.JLabel();
        JPanel jPanel1 = new javax.swing.JPanel();

        if (m_bUseConnectionManager)
        {
            m_lCurrentConfig = new javax.swing.JLabel();
            m_cCurrentConfiguration = new javax.swing.JComboBox<String>();
        }

        JLabel jLabel2 = new javax.swing.JLabel();
        JLabel jLabel3 = new javax.swing.JLabel();
        JLabel jLabel4 = new javax.swing.JLabel();
        JLabel jLabel5 = new javax.swing.JLabel();
        JLabel jLabel6 = new javax.swing.JLabel();
        m_cAuthType = new javax.swing.JComboBox<String>();
        m_tServer = new javax.swing.JTextField();

        JLabel jLabel7 = new javax.swing.JLabel();
        m_tPort = new javax.swing.JTextField();
        m_tUsername = new javax.swing.JTextField();
        m_tPassword = new javax.swing.JPasswordField();
        m_tDomain = new javax.swing.JTextField();

        if (m_bCreateButtons)
        {
            m_pButtonPanel = new javax.swing.JPanel();
            m_bConnect = new javax.swing.JButton();
            m_bCancel = new javax.swing.JButton();
            m_bTest = new javax.swing.JButton();
        }

        JTabbedPane jTabbedPane1 = new javax.swing.JTabbedPane();
        JPanel jPanel2 = new javax.swing.JPanel();
        m_cSSL = new javax.swing.JCheckBox();

        JLabel jLabel8 = new javax.swing.JLabel();
        JLabel jLabel9 = new javax.swing.JLabel();
        JLabel jLabel10 = new javax.swing.JLabel();
        m_tTrustLocation = new javax.swing.JTextField();
        m_tTrustPassword = new javax.swing.JPasswordField();
        m_cTrustMode = new javax.swing.JComboBox<String>();
        m_bBrowseTrust = new javax.swing.JButton();

        JLabel jLabel11 = new javax.swing.JLabel();
        m_cTrustType = new javax.swing.JComboBox<String>();
        m_bAcceptExpired = new javax.swing.JCheckBox();
        m_bAcceptInvalid = new javax.swing.JCheckBox();

        JPanel jPanel3 = new javax.swing.JPanel();
        JLabel jLabel24 = new javax.swing.JLabel();
        JLabel jLabel26 = new javax.swing.JLabel();
        m_tCertificateLocation = new javax.swing.JTextField();
        m_tCertificatePassword = new javax.swing.JPasswordField();
        m_bBrowseCertificate = new javax.swing.JButton();

        JLabel jLabel27 = new javax.swing.JLabel();
        m_cCertificateType = new javax.swing.JComboBox<String>();

        JPanel jPanel4 = new javax.swing.JPanel();
        JLabel jLabel28 = new javax.swing.JLabel();
        JLabel jLabel29 = new javax.swing.JLabel();
        JLabel jLabel30 = new javax.swing.JLabel();
        JLabel jLabel31 = new javax.swing.JLabel();
        JLabel jLabel32 = new javax.swing.JLabel();
        JLabel jLabel33 = new javax.swing.JLabel();
        JLabel jLabel34 = new javax.swing.JLabel();
        m_tGatewayURL = new javax.swing.JTextField();
        m_tProxyServer = new javax.swing.JTextField();
        m_tProxyPort = new javax.swing.JTextField();
        m_tProxyUsername = new javax.swing.JTextField();
        m_tProxyPassword = new javax.swing.JPasswordField();
        m_tRequestTimeout = new javax.swing.JTextField();
        m_tNetworkTimeout = new javax.swing.JTextField();

        if (m_bUseConnectionManager)
        {
            m_pConfigManagement = new javax.swing.JPanel();
        }

        JLabel jLabel35 = new javax.swing.JLabel();
        m_tConfigName = new javax.swing.JTextField();
        m_bSaveConfig = new javax.swing.JCheckBox();

        ResourceBundle resourceMap = ResourceBundle.getBundle(CGCLoginPanel.class.getName());
        jLabel25.setText(resourceMap.getString("jLabel25.text")); // NOI18N
        jLabel25.setName("jLabel25"); // NOI18N

        setName("Form"); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setName("jPanel1"); // NOI18N

        if (m_bUseConnectionManager)
        {
            m_lCurrentConfig.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            m_lCurrentConfig.setText(resourceMap.getString("jLabel1.text")); // NOI18N
            m_lCurrentConfig.setName("jLabel1"); // NOI18N

            m_cCurrentConfiguration.setModel(new javax.swing.DefaultComboBoxModel<String>(new String[]
                                                                                  {
                                                                                      "<New>"
                                                                                  }));
            m_cCurrentConfiguration.setName("m_cCurrentConfiguration"); // NOI18N
            m_cCurrentConfiguration.addActionListener(new java.awt.event.ActionListener()
                {
                    public void actionPerformed(java.awt.event.ActionEvent evt)
                    {
                        m_cCurrentConfigurationActionPerformed(evt);
                    }
                });
            m_cCurrentConfiguration.addKeyListener(new java.awt.event.KeyAdapter()
                {
                    public void keyPressed(java.awt.event.KeyEvent evt)
                    {
                        m_cCurrentConfigurationKeyPressed(evt);
                    }
                });
        }

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        m_cAuthType.setModel(new javax.swing.DefaultComboBoxModel<String>(new String[]
                                                                  {
                                                                      "Domain (NTLM)", "Basic",
                                                                      "Cordys Custom (C2)",
                                                                      "SSO (C3)",
                                                                      "Client certificate"
                                                                  }));
        m_cAuthType.setName("m_cAuthType"); // NOI18N
        m_cAuthType.addInputMethodListener(new java.awt.event.InputMethodListener()
            {
                public void caretPositionChanged(java.awt.event.InputMethodEvent evt)
                {
                }

                public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt)
                {
                    m_cAuthTypeInputMethodTextChanged(evt);
                }
            });

        m_tServer.setText(resourceMap.getString("m_tServer.text")); // NOI18N
        m_tServer.setName("m_tServer"); // NOI18N
        m_tServer.addFocusListener(new java.awt.event.FocusAdapter()
            {
                public void focusGained(java.awt.event.FocusEvent evt)
                {
                    m_tServerFocusGained(evt);
                }

                public void focusLost(java.awt.event.FocusEvent evt)
                {
                    m_tServerFocusLost(evt);
                }
            });

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        m_tPort.setText(resourceMap.getString("m_tPort.text")); // NOI18N
        m_tPort.setName("m_tPort"); // NOI18N

        m_tUsername.setText(resourceMap.getString("m_tUsername.text")); // NOI18N
        m_tUsername.setName("m_tUsername"); // NOI18N

        m_tPassword.setText(resourceMap.getString("m_tPassword.text")); // NOI18N
        m_tPassword.setName("m_tPassword"); // NOI18N

        m_tDomain.setText(resourceMap.getString("m_tDomain.text")); // NOI18N
        m_tDomain.setName("m_tDomain"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);

        if (m_bUseConnectionManager)
        {
            jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing
                                                                               .GroupLayout
                                                                               .Alignment.LEADING)
                                             .addGroup(jPanel1Layout.createSequentialGroup()
                                                       .addContainerGap().addGroup(jPanel1Layout
                                                                                   .createParallelGroup(javax
                                                                                                        .swing
                                                                                                        .GroupLayout
                                                                                                        .Alignment.LEADING)
                                                                                   .addGroup(jPanel1Layout
                                                                                             .createSequentialGroup()
                                                                                             .addComponent(m_lCurrentConfig,
                                                                                                           javax
                                                                                                           .swing
                                                                                                           .GroupLayout.PREFERRED_SIZE,
                                                                                                           96,
                                                                                                           javax
                                                                                                           .swing
                                                                                                           .GroupLayout.PREFERRED_SIZE)
                                                                                             .addPreferredGap(javax
                                                                                                              .swing
                                                                                                              .LayoutStyle
                                                                                                              .ComponentPlacement.RELATED)
                                                                                             .addComponent(m_cCurrentConfiguration,
                                                                                                           0,
                                                                                                           388,
                                                                                                           Short.MAX_VALUE))
                                                                                   .addGroup(jPanel1Layout
                                                                                             .createSequentialGroup()
                                                                                             .addComponent(jLabel4)
                                                                                             .addPreferredGap(javax
                                                                                                              .swing
                                                                                                              .LayoutStyle
                                                                                                              .ComponentPlacement.RELATED)
                                                                                             .addComponent(m_tUsername,
                                                                                                           javax
                                                                                                           .swing
                                                                                                           .GroupLayout.DEFAULT_SIZE,
                                                                                                           388,
                                                                                                           Short.MAX_VALUE))
                                                                                   .addGroup(jPanel1Layout
                                                                                             .createSequentialGroup()
                                                                                             .addComponent(jLabel5)
                                                                                             .addPreferredGap(javax
                                                                                                              .swing
                                                                                                              .LayoutStyle
                                                                                                              .ComponentPlacement.RELATED)
                                                                                             .addComponent(m_tPassword,
                                                                                                           javax
                                                                                                           .swing
                                                                                                           .GroupLayout.DEFAULT_SIZE,
                                                                                                           388,
                                                                                                           Short.MAX_VALUE))
                                                                                   .addGroup(jPanel1Layout
                                                                                             .createSequentialGroup()
                                                                                             .addComponent(jLabel6)
                                                                                             .addPreferredGap(javax
                                                                                                              .swing
                                                                                                              .LayoutStyle
                                                                                                              .ComponentPlacement.RELATED)
                                                                                             .addComponent(m_tDomain,
                                                                                                           javax
                                                                                                           .swing
                                                                                                           .GroupLayout.DEFAULT_SIZE,
                                                                                                           388,
                                                                                                           Short.MAX_VALUE))
                                                                                   .addGroup(jPanel1Layout
                                                                                             .createSequentialGroup()
                                                                                             .addGroup(jPanel1Layout
                                                                                                       .createParallelGroup(javax
                                                                                                                            .swing
                                                                                                                            .GroupLayout
                                                                                                                            .Alignment.TRAILING)
                                                                                                       .addGroup(javax
                                                                                                                 .swing
                                                                                                                 .GroupLayout
                                                                                                                 .Alignment.LEADING,
                                                                                                                 jPanel1Layout
                                                                                                                 .createSequentialGroup()
                                                                                                                 .addComponent(jLabel2)
                                                                                                                 .addPreferredGap(javax
                                                                                                                                  .swing
                                                                                                                                  .LayoutStyle
                                                                                                                                  .ComponentPlacement.RELATED)
                                                                                                                 .addComponent(m_cAuthType,
                                                                                                                               0,
                                                                                                                               270,
                                                                                                                               Short.MAX_VALUE))
                                                                                                       .addGroup(javax
                                                                                                                 .swing
                                                                                                                 .GroupLayout
                                                                                                                 .Alignment.LEADING,
                                                                                                                 jPanel1Layout
                                                                                                                 .createSequentialGroup()
                                                                                                                 .addComponent(jLabel3)
                                                                                                                 .addPreferredGap(javax
                                                                                                                                  .swing
                                                                                                                                  .LayoutStyle
                                                                                                                                  .ComponentPlacement.RELATED)
                                                                                                                 .addComponent(m_tServer,
                                                                                                                               javax
                                                                                                                               .swing
                                                                                                                               .GroupLayout.DEFAULT_SIZE,
                                                                                                                               270,
                                                                                                                               Short.MAX_VALUE)))
                                                                                             .addPreferredGap(javax
                                                                                                              .swing
                                                                                                              .LayoutStyle
                                                                                                              .ComponentPlacement.RELATED,
                                                                                                              18,
                                                                                                              javax
                                                                                                              .swing
                                                                                                              .GroupLayout.PREFERRED_SIZE)
                                                                                             .addComponent(jLabel7)
                                                                                             .addGap(18,
                                                                                                     18,
                                                                                                     18)
                                                                                             .addComponent(m_tPort,
                                                                                                           javax
                                                                                                           .swing
                                                                                                           .GroupLayout.PREFERRED_SIZE,
                                                                                                           58,
                                                                                                           javax
                                                                                                           .swing
                                                                                                           .GroupLayout.PREFERRED_SIZE)))
                                                       .addContainerGap()));

            jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL,
                                   new java.awt.Component[]
                                   {
                                       m_lCurrentConfig, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6
                                   });

            jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout
                                                                             .Alignment.LEADING)
                                           .addGroup(jPanel1Layout.createSequentialGroup()
                                                     .addContainerGap().addGroup(jPanel1Layout
                                                                                 .createParallelGroup(javax
                                                                                                      .swing
                                                                                                      .GroupLayout
                                                                                                      .Alignment.BASELINE)
                                                                                 .addComponent(m_lCurrentConfig)
                                                                                 .addComponent(m_cCurrentConfiguration,
                                                                                               javax
                                                                                               .swing
                                                                                               .GroupLayout.PREFERRED_SIZE,
                                                                                               javax
                                                                                               .swing
                                                                                               .GroupLayout.DEFAULT_SIZE,
                                                                                               javax
                                                                                               .swing
                                                                                               .GroupLayout.PREFERRED_SIZE))
                                                     .addPreferredGap(javax.swing.LayoutStyle
                                                                      .ComponentPlacement.RELATED)
                                                     .addGroup(jPanel1Layout.createParallelGroup(javax
                                                                                                 .swing
                                                                                                 .GroupLayout
                                                                                                 .Alignment.BASELINE)
                                                               .addComponent(jLabel2).addComponent(m_cAuthType,
                                                                                                   javax
                                                                                                   .swing
                                                                                                   .GroupLayout.PREFERRED_SIZE,
                                                                                                   javax
                                                                                                   .swing
                                                                                                   .GroupLayout.DEFAULT_SIZE,
                                                                                                   javax
                                                                                                   .swing
                                                                                                   .GroupLayout.PREFERRED_SIZE))
                                                     .addPreferredGap(javax.swing.LayoutStyle
                                                                      .ComponentPlacement.RELATED)
                                                     .addGroup(jPanel1Layout.createParallelGroup(javax
                                                                                                 .swing
                                                                                                 .GroupLayout
                                                                                                 .Alignment.BASELINE)
                                                               .addComponent(jLabel3).addComponent(m_tPort,
                                                                                                   javax
                                                                                                   .swing
                                                                                                   .GroupLayout.PREFERRED_SIZE,
                                                                                                   javax
                                                                                                   .swing
                                                                                                   .GroupLayout.DEFAULT_SIZE,
                                                                                                   javax
                                                                                                   .swing
                                                                                                   .GroupLayout.PREFERRED_SIZE)
                                                               .addComponent(jLabel7).addComponent(m_tServer,
                                                                                                   javax
                                                                                                   .swing
                                                                                                   .GroupLayout.PREFERRED_SIZE,
                                                                                                   javax
                                                                                                   .swing
                                                                                                   .GroupLayout.DEFAULT_SIZE,
                                                                                                   javax
                                                                                                   .swing
                                                                                                   .GroupLayout.PREFERRED_SIZE))
                                                     .addPreferredGap(javax.swing.LayoutStyle
                                                                      .ComponentPlacement.RELATED)
                                                     .addGroup(jPanel1Layout.createParallelGroup(javax
                                                                                                 .swing
                                                                                                 .GroupLayout
                                                                                                 .Alignment.BASELINE)
                                                               .addComponent(jLabel4).addComponent(m_tUsername,
                                                                                                   javax
                                                                                                   .swing
                                                                                                   .GroupLayout.PREFERRED_SIZE,
                                                                                                   javax
                                                                                                   .swing
                                                                                                   .GroupLayout.DEFAULT_SIZE,
                                                                                                   javax
                                                                                                   .swing
                                                                                                   .GroupLayout.PREFERRED_SIZE))
                                                     .addPreferredGap(javax.swing.LayoutStyle
                                                                      .ComponentPlacement.RELATED)
                                                     .addGroup(jPanel1Layout.createParallelGroup(javax
                                                                                                 .swing
                                                                                                 .GroupLayout
                                                                                                 .Alignment.BASELINE)
                                                               .addComponent(jLabel5).addComponent(m_tPassword,
                                                                                                   javax
                                                                                                   .swing
                                                                                                   .GroupLayout.PREFERRED_SIZE,
                                                                                                   javax
                                                                                                   .swing
                                                                                                   .GroupLayout.DEFAULT_SIZE,
                                                                                                   javax
                                                                                                   .swing
                                                                                                   .GroupLayout.PREFERRED_SIZE))
                                                     .addPreferredGap(javax.swing.LayoutStyle
                                                                      .ComponentPlacement.RELATED)
                                                     .addGroup(jPanel1Layout.createParallelGroup(javax
                                                                                                 .swing
                                                                                                 .GroupLayout
                                                                                                 .Alignment.BASELINE)
                                                               .addComponent(jLabel6).addComponent(m_tDomain,
                                                                                                   javax
                                                                                                   .swing
                                                                                                   .GroupLayout.PREFERRED_SIZE,
                                                                                                   javax
                                                                                                   .swing
                                                                                                   .GroupLayout.DEFAULT_SIZE,
                                                                                                   javax
                                                                                                   .swing
                                                                                                   .GroupLayout.PREFERRED_SIZE))
                                                     .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                      Short.MAX_VALUE)));
        }
        else
        {
            jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing
                                                                               .GroupLayout
                                                                               .Alignment.LEADING)
                                             .addGroup(jPanel1Layout.createSequentialGroup()
                                                       .addContainerGap().addGroup(jPanel1Layout
                                                                                   .createParallelGroup(javax
                                                                                                        .swing
                                                                                                        .GroupLayout
                                                                                                        .Alignment.LEADING)
                                                                                   .addGroup(jPanel1Layout
                                                                                             .createSequentialGroup()
                                                                                             .addComponent(jLabel4)
                                                                                             .addPreferredGap(javax
                                                                                                              .swing
                                                                                                              .LayoutStyle
                                                                                                              .ComponentPlacement.RELATED)
                                                                                             .addComponent(m_tUsername,
                                                                                                           javax
                                                                                                           .swing
                                                                                                           .GroupLayout.DEFAULT_SIZE,
                                                                                                           388,
                                                                                                           Short.MAX_VALUE))
                                                                                   .addGroup(jPanel1Layout
                                                                                             .createSequentialGroup()
                                                                                             .addComponent(jLabel5)
                                                                                             .addPreferredGap(javax
                                                                                                              .swing
                                                                                                              .LayoutStyle
                                                                                                              .ComponentPlacement.RELATED)
                                                                                             .addComponent(m_tPassword,
                                                                                                           javax
                                                                                                           .swing
                                                                                                           .GroupLayout.DEFAULT_SIZE,
                                                                                                           388,
                                                                                                           Short.MAX_VALUE))
                                                                                   .addGroup(jPanel1Layout
                                                                                             .createSequentialGroup()
                                                                                             .addComponent(jLabel6)
                                                                                             .addPreferredGap(javax
                                                                                                              .swing
                                                                                                              .LayoutStyle
                                                                                                              .ComponentPlacement.RELATED)
                                                                                             .addComponent(m_tDomain,
                                                                                                           javax
                                                                                                           .swing
                                                                                                           .GroupLayout.DEFAULT_SIZE,
                                                                                                           388,
                                                                                                           Short.MAX_VALUE))
                                                                                   .addGroup(jPanel1Layout
                                                                                             .createSequentialGroup()
                                                                                             .addGroup(jPanel1Layout
                                                                                                       .createParallelGroup(javax
                                                                                                                            .swing
                                                                                                                            .GroupLayout
                                                                                                                            .Alignment.TRAILING)
                                                                                                       .addGroup(javax
                                                                                                                 .swing
                                                                                                                 .GroupLayout
                                                                                                                 .Alignment.LEADING,
                                                                                                                 jPanel1Layout
                                                                                                                 .createSequentialGroup()
                                                                                                                 .addComponent(jLabel2)
                                                                                                                 .addPreferredGap(javax
                                                                                                                                  .swing
                                                                                                                                  .LayoutStyle
                                                                                                                                  .ComponentPlacement.RELATED)
                                                                                                                 .addComponent(m_cAuthType,
                                                                                                                               0,
                                                                                                                               270,
                                                                                                                               Short.MAX_VALUE))
                                                                                                       .addGroup(javax
                                                                                                                 .swing
                                                                                                                 .GroupLayout
                                                                                                                 .Alignment.LEADING,
                                                                                                                 jPanel1Layout
                                                                                                                 .createSequentialGroup()
                                                                                                                 .addComponent(jLabel3)
                                                                                                                 .addPreferredGap(javax
                                                                                                                                  .swing
                                                                                                                                  .LayoutStyle
                                                                                                                                  .ComponentPlacement.RELATED)
                                                                                                                 .addComponent(m_tServer,
                                                                                                                               javax
                                                                                                                               .swing
                                                                                                                               .GroupLayout.DEFAULT_SIZE,
                                                                                                                               270,
                                                                                                                               Short.MAX_VALUE)))
                                                                                             .addPreferredGap(javax
                                                                                                              .swing
                                                                                                              .LayoutStyle
                                                                                                              .ComponentPlacement.RELATED,
                                                                                                              18,
                                                                                                              javax
                                                                                                              .swing
                                                                                                              .GroupLayout.PREFERRED_SIZE)
                                                                                             .addComponent(jLabel7)
                                                                                             .addGap(18,
                                                                                                     18,
                                                                                                     18)
                                                                                             .addComponent(m_tPort,
                                                                                                           javax
                                                                                                           .swing
                                                                                                           .GroupLayout.PREFERRED_SIZE,
                                                                                                           58,
                                                                                                           javax
                                                                                                           .swing
                                                                                                           .GroupLayout.PREFERRED_SIZE)))
                                                       .addContainerGap()));

            jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL,
                                   new java.awt.Component[]
                                   {
                                       jLabel2, jLabel3, jLabel4, jLabel5, jLabel6
                                   });

            jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout
                                                                             .Alignment.LEADING)
                                           .addGroup(jPanel1Layout.createSequentialGroup()
                                                     .addContainerGap().addGroup(jPanel1Layout
                                                                                 .createParallelGroup(javax
                                                                                                      .swing
                                                                                                      .GroupLayout
                                                                                                      .Alignment.BASELINE)
                                                                                 .addComponent(jLabel2)
                                                                                 .addComponent(m_cAuthType,
                                                                                               javax
                                                                                               .swing
                                                                                               .GroupLayout.PREFERRED_SIZE,
                                                                                               javax
                                                                                               .swing
                                                                                               .GroupLayout.DEFAULT_SIZE,
                                                                                               javax
                                                                                               .swing
                                                                                               .GroupLayout.PREFERRED_SIZE))
                                                     .addPreferredGap(javax.swing.LayoutStyle
                                                                      .ComponentPlacement.RELATED)
                                                     .addGroup(jPanel1Layout.createParallelGroup(javax
                                                                                                 .swing
                                                                                                 .GroupLayout
                                                                                                 .Alignment.BASELINE)
                                                               .addComponent(jLabel3).addComponent(m_tPort,
                                                                                                   javax
                                                                                                   .swing
                                                                                                   .GroupLayout.PREFERRED_SIZE,
                                                                                                   javax
                                                                                                   .swing
                                                                                                   .GroupLayout.DEFAULT_SIZE,
                                                                                                   javax
                                                                                                   .swing
                                                                                                   .GroupLayout.PREFERRED_SIZE)
                                                               .addComponent(jLabel7).addComponent(m_tServer,
                                                                                                   javax
                                                                                                   .swing
                                                                                                   .GroupLayout.PREFERRED_SIZE,
                                                                                                   javax
                                                                                                   .swing
                                                                                                   .GroupLayout.DEFAULT_SIZE,
                                                                                                   javax
                                                                                                   .swing
                                                                                                   .GroupLayout.PREFERRED_SIZE))
                                                     .addPreferredGap(javax.swing.LayoutStyle
                                                                      .ComponentPlacement.RELATED)
                                                     .addGroup(jPanel1Layout.createParallelGroup(javax
                                                                                                 .swing
                                                                                                 .GroupLayout
                                                                                                 .Alignment.BASELINE)
                                                               .addComponent(jLabel4).addComponent(m_tUsername,
                                                                                                   javax
                                                                                                   .swing
                                                                                                   .GroupLayout.PREFERRED_SIZE,
                                                                                                   javax
                                                                                                   .swing
                                                                                                   .GroupLayout.DEFAULT_SIZE,
                                                                                                   javax
                                                                                                   .swing
                                                                                                   .GroupLayout.PREFERRED_SIZE))
                                                     .addPreferredGap(javax.swing.LayoutStyle
                                                                      .ComponentPlacement.RELATED)
                                                     .addGroup(jPanel1Layout.createParallelGroup(javax
                                                                                                 .swing
                                                                                                 .GroupLayout
                                                                                                 .Alignment.BASELINE)
                                                               .addComponent(jLabel5).addComponent(m_tPassword,
                                                                                                   javax
                                                                                                   .swing
                                                                                                   .GroupLayout.PREFERRED_SIZE,
                                                                                                   javax
                                                                                                   .swing
                                                                                                   .GroupLayout.DEFAULT_SIZE,
                                                                                                   javax
                                                                                                   .swing
                                                                                                   .GroupLayout.PREFERRED_SIZE))
                                                     .addPreferredGap(javax.swing.LayoutStyle
                                                                      .ComponentPlacement.RELATED)
                                                     .addGroup(jPanel1Layout.createParallelGroup(javax
                                                                                                 .swing
                                                                                                 .GroupLayout
                                                                                                 .Alignment.BASELINE)
                                                               .addComponent(jLabel6).addComponent(m_tDomain,
                                                                                                   javax
                                                                                                   .swing
                                                                                                   .GroupLayout.PREFERRED_SIZE,
                                                                                                   javax
                                                                                                   .swing
                                                                                                   .GroupLayout.DEFAULT_SIZE,
                                                                                                   javax
                                                                                                   .swing
                                                                                                   .GroupLayout.PREFERRED_SIZE))
                                                     .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                      Short.MAX_VALUE)));
        }

        if (m_bCreateButtons == true)
        {
            m_pButtonPanel.setName("jPanel6"); // NOI18N

            m_bConnect.setMnemonic('c');
            m_bConnect.setText(resourceMap.getString("bConnect.text")); // NOI18N
            m_bConnect.setName("bConnect"); // NOI18N
            m_bConnect.addActionListener(new java.awt.event.ActionListener()
                {
                    public void actionPerformed(java.awt.event.ActionEvent evt)
                    {
                        handleConnect();
                    }
                });

            m_bCancel.setMnemonic('a');
            m_bCancel.setText(resourceMap.getString("bCancel.text")); // NOI18N
            m_bCancel.setName("bCancel"); // NOI18N
            m_bCancel.addActionListener(new java.awt.event.ActionListener()
                {
                    public void actionPerformed(java.awt.event.ActionEvent evt)
                    {
                        handleCancelPressed();
                    }
                });

            m_bTest.setMnemonic('t');
            m_bTest.setText(resourceMap.getString("bTest.text")); // NOI18N
            m_bTest.setName("bTest"); // NOI18N
            m_bTest.addActionListener(new java.awt.event.ActionListener()
                {
                    public void actionPerformed(java.awt.event.ActionEvent evt)
                    {
                        handleTestPressed();
                    }
                });

            javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(m_pButtonPanel);
            m_pButtonPanel.setLayout(jPanel6Layout);
            jPanel6Layout.setHorizontalGroup(jPanel6Layout.createParallelGroup(javax.swing
                                                                               .GroupLayout
                                                                               .Alignment.LEADING)
                                             .addGroup(jPanel6Layout.createSequentialGroup().addGap(143,
                                                                                                    143,
                                                                                                    143)
                                                       .addComponent(m_bConnect).addGap(6, 6, 6)
                                                       .addComponent(m_bTest).addPreferredGap(javax
                                                                                              .swing
                                                                                              .LayoutStyle
                                                                                              .ComponentPlacement.RELATED)
                                                       .addComponent(m_bCancel).addContainerGap(135,
                                                                                                Short.MAX_VALUE)));
            jPanel6Layout.setVerticalGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout
                                                                             .Alignment.LEADING)
                                           .addGroup(jPanel6Layout.createSequentialGroup()
                                                     .addContainerGap().addGroup(jPanel6Layout
                                                                                 .createParallelGroup(javax
                                                                                                      .swing
                                                                                                      .GroupLayout
                                                                                                      .Alignment.BASELINE)
                                                                                 .addComponent(m_bConnect)
                                                                                 .addComponent(m_bCancel)
                                                                                 .addComponent(m_bTest))
                                                     .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                      Short.MAX_VALUE)));
            jPanel6Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL,
                                   new java.awt.Component[] { m_bCancel, m_bConnect, m_bTest });
        }

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jPanel2.setName("jPanel2"); // NOI18N

        m_cSSL.setText(resourceMap.getString("m_cSSL.text")); // NOI18N
        m_cSSL.setName("m_cSSL"); // NOI18N
        m_cSSL.addInputMethodListener(new java.awt.event.InputMethodListener()
            {
                public void caretPositionChanged(java.awt.event.InputMethodEvent evt)
                {
                }

                public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt)
                {
                    m_cSSLInputMethodTextChanged(evt);
                }
            });

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        m_tTrustLocation.setText(resourceMap.getString("m_tTrustLocation.text")); // NOI18N
        m_tTrustLocation.setName("m_tTrustLocation"); // NOI18N

        m_tTrustPassword.setText(resourceMap.getString("m_tTrustPassword.text")); // NOI18N
        m_tTrustPassword.setName("m_tTrustPassword"); // NOI18N

        m_cTrustMode.setModel(new javax.swing.DefaultComboBoxModel<String>(new String[]
                                                                   {
                                                                       "Trust all servers",
                                                                       "Use specific truststore"
                                                                   }));
        m_cTrustMode.setName("m_cTrustMode"); // NOI18N
        m_cTrustMode.addInputMethodListener(new java.awt.event.InputMethodListener()
            {
                public void caretPositionChanged(java.awt.event.InputMethodEvent evt)
                {
                }

                public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt)
                {
                    m_cTrustModeInputMethodTextChanged(evt);
                }
            });

        m_bBrowseTrust.setText(resourceMap.getString("m_bBrowseTrust.text")); // NOI18N
        m_bBrowseTrust.setName("m_bBrowseTrust"); // NOI18N
        m_bBrowseTrust.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    m_bBrowseTrustActionPerformed(evt);
                }
            });

        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N

        m_cTrustType.setModel(new javax.swing.DefaultComboBoxModel<String>(new String[]
                                                                   {
                                                                       "JKS", "PKCS12"
                                                                   }));
        m_cTrustType.setName("m_cTrustType"); // NOI18N

        m_bAcceptExpired.setText(resourceMap.getString("m_bAcceptExpired.text")); // NOI18N
        m_bAcceptExpired.setName("m_bAcceptExpired"); // NOI18N

        m_bAcceptInvalid.setText(resourceMap.getString("m_bAcceptInvalid.text")); // NOI18N
        m_bAcceptInvalid.setName("m_bAcceptInvalid"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout
                                                                           .Alignment.LEADING)
                                         .addGroup(jPanel2Layout.createSequentialGroup()
                                                   .addContainerGap().addGroup(jPanel2Layout
                                                                               .createParallelGroup(javax
                                                                                                    .swing
                                                                                                    .GroupLayout
                                                                                                    .Alignment.LEADING,
                                                                                                    false)
                                                                               .addComponent(jLabel8,
                                                                                             javax
                                                                                             .swing
                                                                                             .GroupLayout.DEFAULT_SIZE,
                                                                                             104,
                                                                                             Short.MAX_VALUE)
                                                                               .addComponent(jLabel9,
                                                                                             javax
                                                                                             .swing
                                                                                             .GroupLayout.DEFAULT_SIZE,
                                                                                             javax
                                                                                             .swing
                                                                                             .GroupLayout.DEFAULT_SIZE,
                                                                                             Short.MAX_VALUE)
                                                                               .addComponent(jLabel10,
                                                                                             javax
                                                                                             .swing
                                                                                             .GroupLayout.DEFAULT_SIZE,
                                                                                             javax
                                                                                             .swing
                                                                                             .GroupLayout.DEFAULT_SIZE,
                                                                                             Short.MAX_VALUE))
                                                   .addPreferredGap(javax.swing.LayoutStyle
                                                                    .ComponentPlacement.RELATED)
                                                   .addGroup(jPanel2Layout.createParallelGroup(javax
                                                                                               .swing
                                                                                               .GroupLayout
                                                                                               .Alignment.LEADING)
                                                             .addComponent(m_cSSL).addGroup(javax
                                                                                            .swing
                                                                                            .GroupLayout
                                                                                            .Alignment.TRAILING,
                                                                                            jPanel2Layout
                                                                                            .createSequentialGroup()
                                                                                            .addComponent(m_tTrustLocation,
                                                                                                          javax
                                                                                                          .swing
                                                                                                          .GroupLayout.DEFAULT_SIZE,
                                                                                                          356,
                                                                                                          Short.MAX_VALUE)
                                                                                            .addPreferredGap(javax
                                                                                                             .swing
                                                                                                             .LayoutStyle
                                                                                                             .ComponentPlacement.UNRELATED)
                                                                                            .addComponent(m_bBrowseTrust,
                                                                                                          javax
                                                                                                          .swing
                                                                                                          .GroupLayout.PREFERRED_SIZE,
                                                                                                          24,
                                                                                                          javax
                                                                                                          .swing
                                                                                                          .GroupLayout.PREFERRED_SIZE))
                                                             .addGroup(jPanel2Layout
                                                                       .createSequentialGroup()
                                                                       .addGroup(jPanel2Layout
                                                                                 .createParallelGroup(javax
                                                                                                      .swing
                                                                                                      .GroupLayout
                                                                                                      .Alignment.LEADING)
                                                                                 .addGroup(jPanel2Layout
                                                                                           .createSequentialGroup()
                                                                                           .addComponent(m_tTrustPassword,
                                                                                                         javax
                                                                                                         .swing
                                                                                                         .GroupLayout.PREFERRED_SIZE,
                                                                                                         112,
                                                                                                         javax
                                                                                                         .swing
                                                                                                         .GroupLayout.PREFERRED_SIZE)
                                                                                           .addPreferredGap(javax
                                                                                                            .swing
                                                                                                            .LayoutStyle
                                                                                                            .ComponentPlacement.RELATED)
                                                                                           .addComponent(jLabel11))
                                                                                 .addComponent(m_bAcceptExpired))
                                                                       .addPreferredGap(javax.swing
                                                                                        .LayoutStyle
                                                                                        .ComponentPlacement.RELATED)
                                                                       .addGroup(jPanel2Layout
                                                                                 .createParallelGroup(javax
                                                                                                      .swing
                                                                                                      .GroupLayout
                                                                                                      .Alignment.LEADING)
                                                                                 .addGroup(jPanel2Layout
                                                                                           .createSequentialGroup()
                                                                                           .addComponent(m_bAcceptInvalid)
                                                                                           .addPreferredGap(javax
                                                                                                            .swing
                                                                                                            .LayoutStyle
                                                                                                            .ComponentPlacement.RELATED,
                                                                                                            41,
                                                                                                            Short.MAX_VALUE))
                                                                                 .addComponent(m_cTrustType,
                                                                                               0,
                                                                                               188,
                                                                                               Short.MAX_VALUE)))
                                                             .addComponent(m_cTrustMode, 0, 390,
                                                                           Short.MAX_VALUE))
                                                   .addContainerGap()));
        jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout
                                                                         .Alignment.LEADING)
                                       .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                 jPanel2Layout.createSequentialGroup()
                                                 .addContainerGap(13, Short.MAX_VALUE).addComponent(m_cSSL)
                                                 .addPreferredGap(javax.swing.LayoutStyle
                                                                  .ComponentPlacement.RELATED)
                                                 .addGroup(jPanel2Layout.createParallelGroup(javax
                                                                                             .swing
                                                                                             .GroupLayout
                                                                                             .Alignment.BASELINE)
                                                           .addComponent(jLabel8).addComponent(m_cTrustMode,
                                                                                               javax
                                                                                               .swing
                                                                                               .GroupLayout.PREFERRED_SIZE,
                                                                                               javax
                                                                                               .swing
                                                                                               .GroupLayout.DEFAULT_SIZE,
                                                                                               javax
                                                                                               .swing
                                                                                               .GroupLayout.PREFERRED_SIZE))
                                                 .addPreferredGap(javax.swing.LayoutStyle
                                                                  .ComponentPlacement.RELATED)
                                                 .addGroup(jPanel2Layout.createParallelGroup(javax
                                                                                             .swing
                                                                                             .GroupLayout
                                                                                             .Alignment.BASELINE)
                                                           .addComponent(jLabel9).addComponent(m_bBrowseTrust)
                                                           .addComponent(m_tTrustLocation,
                                                                         javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                         javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                         javax.swing.GroupLayout.PREFERRED_SIZE))
                                                 .addPreferredGap(javax.swing.LayoutStyle
                                                                  .ComponentPlacement.RELATED)
                                                 .addGroup(jPanel2Layout.createParallelGroup(javax
                                                                                             .swing
                                                                                             .GroupLayout
                                                                                             .Alignment.BASELINE)
                                                           .addComponent(jLabel10).addComponent(m_tTrustPassword,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.PREFERRED_SIZE,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.DEFAULT_SIZE,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.PREFERRED_SIZE)
                                                           .addComponent(jLabel11).addComponent(m_cTrustType,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.PREFERRED_SIZE,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.DEFAULT_SIZE,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.PREFERRED_SIZE))
                                                 .addPreferredGap(javax.swing.LayoutStyle
                                                                  .ComponentPlacement.UNRELATED)
                                                 .addGroup(jPanel2Layout.createParallelGroup(javax
                                                                                             .swing
                                                                                             .GroupLayout
                                                                                             .Alignment.BASELINE)
                                                           .addComponent(m_bAcceptExpired)
                                                           .addComponent(m_bAcceptInvalid)).addGap(36,
                                                                                                   36,
                                                                                                   36)));

        jTabbedPane1.addTab(resourceMap.getString("jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

        jPanel3.setName("jPanel3"); // NOI18N

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel24.setText(resourceMap.getString("jLabel24.text")); // NOI18N
        jLabel24.setName("jLabel24"); // NOI18N

        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel26.setText(resourceMap.getString("jLabel26.text")); // NOI18N
        jLabel26.setName("jLabel26"); // NOI18N

        m_tCertificateLocation.setText(resourceMap.getString("m_tCertificateLocation.text")); // NOI18N
        m_tCertificateLocation.setName("m_tCertificateLocation"); // NOI18N

        m_tCertificatePassword.setText(resourceMap.getString("m_tCertificatePassword.text")); // NOI18N
        m_tCertificatePassword.setName("m_tCertificatePassword"); // NOI18N

        m_bBrowseCertificate.setText(resourceMap.getString("m_bBrowseCertificate.text")); // NOI18N
        m_bBrowseCertificate.setName("m_bBrowseCertificate"); // NOI18N
        m_bBrowseCertificate.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    m_bBrowseCertificateActionPerformed(evt);
                }
            });

        jLabel27.setText(resourceMap.getString("jLabel27.text")); // NOI18N
        jLabel27.setName("jLabel27"); // NOI18N

        m_cCertificateType.setModel(new javax.swing.DefaultComboBoxModel<String>(new String[]
                                                                         {
                                                                             "JKS", "PKCS12"
                                                                         }));
        m_cCertificateType.setName("m_cCertificateType"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout
                                                                           .Alignment.LEADING)
                                         .addGroup(jPanel3Layout.createSequentialGroup()
                                                   .addContainerGap().addGroup(jPanel3Layout
                                                                               .createParallelGroup(javax
                                                                                                    .swing
                                                                                                    .GroupLayout
                                                                                                    .Alignment.LEADING,
                                                                                                    false)
                                                                               .addComponent(jLabel24,
                                                                                             javax
                                                                                             .swing
                                                                                             .GroupLayout.DEFAULT_SIZE,
                                                                                             javax
                                                                                             .swing
                                                                                             .GroupLayout.DEFAULT_SIZE,
                                                                                             Short.MAX_VALUE)
                                                                               .addComponent(jLabel26,
                                                                                             javax
                                                                                             .swing
                                                                                             .GroupLayout.DEFAULT_SIZE,
                                                                                             javax
                                                                                             .swing
                                                                                             .GroupLayout.DEFAULT_SIZE,
                                                                                             Short.MAX_VALUE))
                                                   .addPreferredGap(javax.swing.LayoutStyle
                                                                    .ComponentPlacement.RELATED)
                                                   .addGroup(jPanel3Layout.createParallelGroup(javax
                                                                                               .swing
                                                                                               .GroupLayout
                                                                                               .Alignment.LEADING)
                                                             .addGroup(jPanel3Layout
                                                                       .createSequentialGroup()
                                                                       .addComponent(m_tCertificateLocation,
                                                                                     javax.swing
                                                                                     .GroupLayout.DEFAULT_SIZE,
                                                                                     356,
                                                                                     Short.MAX_VALUE)
                                                                       .addPreferredGap(javax.swing
                                                                                        .LayoutStyle
                                                                                        .ComponentPlacement.RELATED)
                                                                       .addComponent(m_bBrowseCertificate,
                                                                                     javax.swing
                                                                                     .GroupLayout.PREFERRED_SIZE,
                                                                                     21,
                                                                                     javax.swing
                                                                                     .GroupLayout.PREFERRED_SIZE))
                                                             .addGroup(jPanel3Layout
                                                                       .createSequentialGroup()
                                                                       .addPreferredGap(javax.swing
                                                                                        .LayoutStyle
                                                                                        .ComponentPlacement.RELATED)
                                                                       .addComponent(m_tCertificatePassword,
                                                                                     javax.swing
                                                                                     .GroupLayout.PREFERRED_SIZE,
                                                                                     168,
                                                                                     javax.swing
                                                                                     .GroupLayout.PREFERRED_SIZE)
                                                                       .addPreferredGap(javax.swing
                                                                                        .LayoutStyle
                                                                                        .ComponentPlacement.RELATED)
                                                                       .addComponent(jLabel27)
                                                                       .addPreferredGap(javax.swing
                                                                                        .LayoutStyle
                                                                                        .ComponentPlacement.RELATED)
                                                                       .addComponent(m_cCertificateType,
                                                                                     0, 128,
                                                                                     Short.MAX_VALUE)))
                                                   .addContainerGap()));
        jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout
                                                                         .Alignment.LEADING)
                                       .addGroup(jPanel3Layout.createSequentialGroup()
                                                 .addContainerGap().addGroup(jPanel3Layout
                                                                             .createParallelGroup(javax
                                                                                                  .swing
                                                                                                  .GroupLayout
                                                                                                  .Alignment.BASELINE)
                                                                             .addComponent(m_bBrowseCertificate)
                                                                             .addComponent(m_tCertificateLocation,
                                                                                           javax
                                                                                           .swing
                                                                                           .GroupLayout.PREFERRED_SIZE,
                                                                                           javax
                                                                                           .swing
                                                                                           .GroupLayout.DEFAULT_SIZE,
                                                                                           javax
                                                                                           .swing
                                                                                           .GroupLayout.PREFERRED_SIZE)
                                                                             .addComponent(jLabel24))
                                                 .addPreferredGap(javax.swing.LayoutStyle
                                                                  .ComponentPlacement.RELATED)
                                                 .addGroup(jPanel3Layout.createParallelGroup(javax
                                                                                             .swing
                                                                                             .GroupLayout
                                                                                             .Alignment.BASELINE)
                                                           .addComponent(jLabel26).addComponent(jLabel27)
                                                           .addComponent(m_tCertificatePassword,
                                                                         javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                         javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                         javax.swing.GroupLayout.PREFERRED_SIZE)
                                                           .addComponent(m_cCertificateType,
                                                                         javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                         javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                         javax.swing.GroupLayout.PREFERRED_SIZE))
                                                 .addContainerGap(121, Short.MAX_VALUE)));

        jTabbedPane1.addTab(resourceMap.getString("jPanel3.TabConstraints.tabTitle"), jPanel3); // NOI18N

        jPanel4.setName("jPanel4"); // NOI18N

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel28.setText(resourceMap.getString("jLabel28.text")); // NOI18N
        jLabel28.setName("jLabel28"); // NOI18N

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel29.setText(resourceMap.getString("jLabel29.text")); // NOI18N
        jLabel29.setName("jLabel29"); // NOI18N

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel30.setText(resourceMap.getString("jLabel30.text")); // NOI18N
        jLabel30.setName("jLabel30"); // NOI18N

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel31.setText(resourceMap.getString("jLabel31.text")); // NOI18N
        jLabel31.setName("jLabel31"); // NOI18N

        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel32.setText(resourceMap.getString("jLabel32.text")); // NOI18N
        jLabel32.setName("jLabel32"); // NOI18N

        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel33.setText(resourceMap.getString("jLabel33.text")); // NOI18N
        jLabel33.setName("jLabel33"); // NOI18N

        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel34.setText(resourceMap.getString("jLabel34.text")); // NOI18N
        jLabel34.setName("jLabel34"); // NOI18N

        m_tGatewayURL.setText(resourceMap.getString("m_tGatewayURL.text")); // NOI18N
        m_tGatewayURL.setName("m_tGatewayURL"); // NOI18N

        m_tProxyServer.setText(resourceMap.getString("m_tProxyServer.text")); // NOI18N
        m_tProxyServer.setName("m_tProxyServer"); // NOI18N

        m_tProxyPort.setText(resourceMap.getString("m_tProxyPort.text")); // NOI18N
        m_tProxyPort.setName("m_tProxyPort"); // NOI18N

        m_tProxyUsername.setText(resourceMap.getString("m_tProxyUsername.text")); // NOI18N
        m_tProxyUsername.setName("m_tProxyUsername"); // NOI18N

        m_tProxyPassword.setText(resourceMap.getString("m_tProxyPassword.text")); // NOI18N
        m_tProxyPassword.setName("m_tProxyPassword"); // NOI18N

        m_tRequestTimeout.setText(resourceMap.getString("m_tRequestTimeout.text")); // NOI18N
        m_tRequestTimeout.setName("m_tRequestTimeout"); // NOI18N

        m_tNetworkTimeout.setText(resourceMap.getString("m_tNetworkTimeout.text")); // NOI18N
        m_tNetworkTimeout.setName("m_tNetworkTimeout"); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout
                                                                           .Alignment.LEADING)
                                         .addGroup(jPanel4Layout.createSequentialGroup()
                                                   .addContainerGap().addGroup(jPanel4Layout
                                                                               .createParallelGroup(javax
                                                                                                    .swing
                                                                                                    .GroupLayout
                                                                                                    .Alignment.LEADING)
                                                                               .addGroup(jPanel4Layout
                                                                                         .createSequentialGroup()
                                                                                         .addComponent(jLabel28)
                                                                                         .addPreferredGap(javax
                                                                                                          .swing
                                                                                                          .LayoutStyle
                                                                                                          .ComponentPlacement.RELATED)
                                                                                         .addComponent(m_tGatewayURL,
                                                                                                       javax
                                                                                                       .swing
                                                                                                       .GroupLayout.DEFAULT_SIZE,
                                                                                                       403,
                                                                                                       Short.MAX_VALUE))
                                                                               .addGroup(jPanel4Layout
                                                                                         .createSequentialGroup()
                                                                                         .addComponent(jLabel29)
                                                                                         .addPreferredGap(javax
                                                                                                          .swing
                                                                                                          .LayoutStyle
                                                                                                          .ComponentPlacement.RELATED)
                                                                                         .addComponent(m_tProxyServer,
                                                                                                       javax
                                                                                                       .swing
                                                                                                       .GroupLayout.PREFERRED_SIZE,
                                                                                                       307,
                                                                                                       javax
                                                                                                       .swing
                                                                                                       .GroupLayout.PREFERRED_SIZE)
                                                                                         .addPreferredGap(javax
                                                                                                          .swing
                                                                                                          .LayoutStyle
                                                                                                          .ComponentPlacement.RELATED)
                                                                                         .addComponent(jLabel33)
                                                                                         .addPreferredGap(javax
                                                                                                          .swing
                                                                                                          .LayoutStyle
                                                                                                          .ComponentPlacement.RELATED)
                                                                                         .addComponent(m_tProxyPort,
                                                                                                       javax
                                                                                                       .swing
                                                                                                       .GroupLayout.DEFAULT_SIZE,
                                                                                                       33,
                                                                                                       Short.MAX_VALUE))
                                                                               .addGroup(jPanel4Layout
                                                                                         .createSequentialGroup()
                                                                                         .addGroup(jPanel4Layout
                                                                                                   .createParallelGroup(javax
                                                                                                                        .swing
                                                                                                                        .GroupLayout
                                                                                                                        .Alignment.LEADING)
                                                                                                   .addComponent(jLabel30)
                                                                                                   .addComponent(jLabel32))
                                                                                         .addPreferredGap(javax
                                                                                                          .swing
                                                                                                          .LayoutStyle
                                                                                                          .ComponentPlacement.RELATED)
                                                                                         .addGroup(jPanel4Layout
                                                                                                   .createParallelGroup(javax
                                                                                                                        .swing
                                                                                                                        .GroupLayout
                                                                                                                        .Alignment.LEADING)
                                                                                                   .addComponent(m_tProxyPassword,
                                                                                                                 javax
                                                                                                                 .swing
                                                                                                                 .GroupLayout.DEFAULT_SIZE,
                                                                                                                 403,
                                                                                                                 Short.MAX_VALUE)
                                                                                                   .addComponent(m_tProxyUsername,
                                                                                                                 javax
                                                                                                                 .swing
                                                                                                                 .GroupLayout.DEFAULT_SIZE,
                                                                                                                 403,
                                                                                                                 Short.MAX_VALUE)))
                                                                               .addGroup(jPanel4Layout
                                                                                         .createSequentialGroup()
                                                                                         .addGroup(jPanel4Layout
                                                                                                   .createParallelGroup(javax
                                                                                                                        .swing
                                                                                                                        .GroupLayout
                                                                                                                        .Alignment.LEADING)
                                                                                                   .addComponent(jLabel34)
                                                                                                   .addComponent(jLabel31))
                                                                                         .addPreferredGap(javax
                                                                                                          .swing
                                                                                                          .LayoutStyle
                                                                                                          .ComponentPlacement.RELATED)
                                                                                         .addGroup(jPanel4Layout
                                                                                                   .createParallelGroup(javax
                                                                                                                        .swing
                                                                                                                        .GroupLayout
                                                                                                                        .Alignment.LEADING)
                                                                                                   .addComponent(m_tNetworkTimeout,
                                                                                                                 javax
                                                                                                                 .swing
                                                                                                                 .GroupLayout.DEFAULT_SIZE,
                                                                                                                 403,
                                                                                                                 Short.MAX_VALUE)
                                                                                                   .addComponent(m_tRequestTimeout,
                                                                                                                 javax
                                                                                                                 .swing
                                                                                                                 .GroupLayout.DEFAULT_SIZE,
                                                                                                                 403,
                                                                                                                 Short.MAX_VALUE))))
                                                   .addContainerGap()));

        jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL,
                               new java.awt.Component[]
                               {
                                   jLabel28, jLabel29, jLabel30, jLabel31, jLabel32, jLabel34
                               });

        jPanel4Layout.setVerticalGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout
                                                                         .Alignment.LEADING)
                                       .addGroup(jPanel4Layout.createSequentialGroup()
                                                 .addContainerGap().addGroup(jPanel4Layout
                                                                             .createParallelGroup(javax
                                                                                                  .swing
                                                                                                  .GroupLayout
                                                                                                  .Alignment.BASELINE)
                                                                             .addComponent(jLabel28)
                                                                             .addComponent(m_tGatewayURL,
                                                                                           javax
                                                                                           .swing
                                                                                           .GroupLayout.PREFERRED_SIZE,
                                                                                           javax
                                                                                           .swing
                                                                                           .GroupLayout.DEFAULT_SIZE,
                                                                                           javax
                                                                                           .swing
                                                                                           .GroupLayout.PREFERRED_SIZE))
                                                 .addPreferredGap(javax.swing.LayoutStyle
                                                                  .ComponentPlacement.RELATED)
                                                 .addGroup(jPanel4Layout.createParallelGroup(javax
                                                                                             .swing
                                                                                             .GroupLayout
                                                                                             .Alignment.BASELINE)
                                                           .addComponent(jLabel29).addComponent(m_tProxyServer,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.PREFERRED_SIZE,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.DEFAULT_SIZE,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.PREFERRED_SIZE)
                                                           .addComponent(jLabel33).addComponent(m_tProxyPort,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.PREFERRED_SIZE,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.DEFAULT_SIZE,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.PREFERRED_SIZE))
                                                 .addPreferredGap(javax.swing.LayoutStyle
                                                                  .ComponentPlacement.RELATED)
                                                 .addGroup(jPanel4Layout.createParallelGroup(javax
                                                                                             .swing
                                                                                             .GroupLayout
                                                                                             .Alignment.BASELINE)
                                                           .addComponent(jLabel30).addComponent(m_tProxyUsername,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.PREFERRED_SIZE,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.DEFAULT_SIZE,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.PREFERRED_SIZE))
                                                 .addPreferredGap(javax.swing.LayoutStyle
                                                                  .ComponentPlacement.RELATED)
                                                 .addGroup(jPanel4Layout.createParallelGroup(javax
                                                                                             .swing
                                                                                             .GroupLayout
                                                                                             .Alignment.BASELINE)
                                                           .addComponent(jLabel32).addComponent(m_tProxyPassword,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.PREFERRED_SIZE,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.DEFAULT_SIZE,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.PREFERRED_SIZE))
                                                 .addPreferredGap(javax.swing.LayoutStyle
                                                                  .ComponentPlacement.RELATED)
                                                 .addGroup(jPanel4Layout.createParallelGroup(javax
                                                                                             .swing
                                                                                             .GroupLayout
                                                                                             .Alignment.BASELINE)
                                                           .addComponent(m_tRequestTimeout,
                                                                         javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                         javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                         javax.swing.GroupLayout.PREFERRED_SIZE)
                                                           .addComponent(jLabel31)).addPreferredGap(javax
                                                                                                    .swing
                                                                                                    .LayoutStyle
                                                                                                    .ComponentPlacement.RELATED)
                                                 .addGroup(jPanel4Layout.createParallelGroup(javax
                                                                                             .swing
                                                                                             .GroupLayout
                                                                                             .Alignment.BASELINE)
                                                           .addComponent(jLabel34).addComponent(m_tNetworkTimeout,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.PREFERRED_SIZE,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.DEFAULT_SIZE,
                                                                                                javax
                                                                                                .swing
                                                                                                .GroupLayout.PREFERRED_SIZE))
                                                 .addContainerGap(22, Short.MAX_VALUE)));

        jTabbedPane1.addTab(resourceMap.getString("jPanel4.TabConstraints.tabTitle"), jPanel4); // NOI18N

        if (m_bUseConnectionManager)
        {
            m_pConfigManagement.setName("jPanel5"); // NOI18N

            jLabel35.setText(resourceMap.getString("jLabel35.text")); // NOI18N
            jLabel35.setName("jLabel35"); // NOI18N

            m_tConfigName.setText(resourceMap.getString("m_tConfigName.text")); // NOI18N
            m_tConfigName.setName("m_tConfigName"); // NOI18N

            m_bSaveConfig.setText(resourceMap.getString("m_bSaveConfig.text")); // NOI18N
            m_bSaveConfig.setName("m_bSaveConfig"); // NOI18N

            javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(m_pConfigManagement);
            m_pConfigManagement.setLayout(jPanel5Layout);
            jPanel5Layout.setHorizontalGroup(jPanel5Layout.createParallelGroup(javax.swing
                                                                               .GroupLayout
                                                                               .Alignment.LEADING)
                                             .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                       jPanel5Layout.createSequentialGroup()
                                                       .addContainerGap().addComponent(jLabel35)
                                                       .addPreferredGap(javax.swing.LayoutStyle
                                                                        .ComponentPlacement.RELATED)
                                                       .addGroup(jPanel5Layout.createParallelGroup(javax
                                                                                                   .swing
                                                                                                   .GroupLayout
                                                                                                   .Alignment.TRAILING)
                                                                 .addComponent(m_bSaveConfig,
                                                                               javax.swing
                                                                               .GroupLayout
                                                                               .Alignment.LEADING,
                                                                               javax.swing
                                                                               .GroupLayout.DEFAULT_SIZE,
                                                                               388,
                                                                               Short.MAX_VALUE)
                                                                 .addComponent(m_tConfigName,
                                                                               javax.swing
                                                                               .GroupLayout
                                                                               .Alignment.LEADING,
                                                                               javax.swing
                                                                               .GroupLayout.DEFAULT_SIZE,
                                                                               388,
                                                                               Short.MAX_VALUE))
                                                       .addContainerGap()));
            jPanel5Layout.setVerticalGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout
                                                                             .Alignment.LEADING)
                                           .addGroup(jPanel5Layout.createSequentialGroup()
                                                     .addContainerGap().addGroup(jPanel5Layout
                                                                                 .createParallelGroup(javax
                                                                                                      .swing
                                                                                                      .GroupLayout
                                                                                                      .Alignment.BASELINE)
                                                                                 .addComponent(jLabel35)
                                                                                 .addComponent(m_tConfigName,
                                                                                               javax
                                                                                               .swing
                                                                                               .GroupLayout.PREFERRED_SIZE,
                                                                                               javax
                                                                                               .swing
                                                                                               .GroupLayout.DEFAULT_SIZE,
                                                                                               javax
                                                                                               .swing
                                                                                               .GroupLayout.PREFERRED_SIZE))
                                                     .addPreferredGap(javax.swing.LayoutStyle
                                                                      .ComponentPlacement.RELATED)
                                                     .addComponent(m_bSaveConfig).addContainerGap(127,
                                                                                                  Short.MAX_VALUE)));

            jTabbedPane1.addTab(resourceMap.getString("jPanel5.TabConstraints.tabTitle"),
                                m_pConfigManagement); // NOI18N
        }

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);

        if (m_bCreateButtons == true)
        {
            layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                layout.createSequentialGroup().addGroup(layout
                                                                                        .createParallelGroup(javax
                                                                                                             .swing
                                                                                                             .GroupLayout
                                                                                                             .Alignment.TRAILING)
                                                                                        .addComponent(jTabbedPane1,
                                                                                                      javax
                                                                                                      .swing
                                                                                                      .GroupLayout.DEFAULT_SIZE,
                                                                                                      515,
                                                                                                      Short.MAX_VALUE)
                                                                                        .addComponent(jPanel1,
                                                                                                      javax
                                                                                                      .swing
                                                                                                      .GroupLayout
                                                                                                      .Alignment.LEADING,
                                                                                                      javax
                                                                                                      .swing
                                                                                                      .GroupLayout.DEFAULT_SIZE,
                                                                                                      javax
                                                                                                      .swing
                                                                                                      .GroupLayout.DEFAULT_SIZE,
                                                                                                      Short.MAX_VALUE)
                                                                                        .addComponent(m_pButtonPanel,
                                                                                                      javax
                                                                                                      .swing
                                                                                                      .GroupLayout
                                                                                                      .Alignment.LEADING,
                                                                                                      javax
                                                                                                      .swing
                                                                                                      .GroupLayout.DEFAULT_SIZE,
                                                                                                      javax
                                                                                                      .swing
                                                                                                      .GroupLayout.DEFAULT_SIZE,
                                                                                                      Short.MAX_VALUE))
                                                .addContainerGap()));
        }
        else
        {
            layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                layout.createSequentialGroup().addGroup(layout
                                                                                        .createParallelGroup(javax
                                                                                                             .swing
                                                                                                             .GroupLayout
                                                                                                             .Alignment.TRAILING)
                                                                                        .addComponent(jTabbedPane1,
                                                                                                      javax
                                                                                                      .swing
                                                                                                      .GroupLayout.DEFAULT_SIZE,
                                                                                                      515,
                                                                                                      Short.MAX_VALUE)
                                                                                        .addComponent(jPanel1,
                                                                                                      javax
                                                                                                      .swing
                                                                                                      .GroupLayout
                                                                                                      .Alignment.LEADING,
                                                                                                      javax
                                                                                                      .swing
                                                                                                      .GroupLayout.DEFAULT_SIZE,
                                                                                                      javax
                                                                                                      .swing
                                                                                                      .GroupLayout.DEFAULT_SIZE,
                                                                                                      Short.MAX_VALUE))
                                                .addContainerGap()));
        }

        if (m_bCreateButtons == true)
        {
            layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup().addComponent(jPanel1,
                                                                                          javax
                                                                                          .swing
                                                                                          .GroupLayout.PREFERRED_SIZE,
                                                                                          javax
                                                                                          .swing
                                                                                          .GroupLayout.DEFAULT_SIZE,
                                                                                          javax
                                                                                          .swing
                                                                                          .GroupLayout.PREFERRED_SIZE)
                                              .addPreferredGap(javax.swing.LayoutStyle
                                                               .ComponentPlacement.RELATED)
                                              .addComponent(m_pButtonPanel,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE,
                                                            javax.swing.GroupLayout.DEFAULT_SIZE,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE)
                                              .addPreferredGap(javax.swing.LayoutStyle
                                                               .ComponentPlacement.RELATED)
                                              .addComponent(jTabbedPane1,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE,
                                                            javax.swing.GroupLayout.DEFAULT_SIZE,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE)
                                              .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
                                                               Short.MAX_VALUE)));
        }
        else
        {
            layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup().addComponent(jPanel1,
                                                                                          javax
                                                                                          .swing
                                                                                          .GroupLayout.PREFERRED_SIZE,
                                                                                          javax
                                                                                          .swing
                                                                                          .GroupLayout.DEFAULT_SIZE,
                                                                                          javax
                                                                                          .swing
                                                                                          .GroupLayout.PREFERRED_SIZE)
                                              .addPreferredGap(javax.swing.LayoutStyle
                                                               .ComponentPlacement.RELATED)
                                              .addComponent(jTabbedPane1,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE,
                                                            javax.swing.GroupLayout.DEFAULT_SIZE,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE)
                                              .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
                                                               Short.MAX_VALUE)));
        }
    } // </editor-fold>

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void m_bBrowseCertificateActionPerformed(java.awt.event.ActionEvent evt)
    {
        browseFile(m_tCertificateLocation);
    }

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void m_bBrowseTrustActionPerformed(java.awt.event.ActionEvent evt)
    {
        browseFile(m_tTrustLocation);
    }

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void m_cAuthTypeInputMethodTextChanged(java.awt.event.InputMethodEvent evt)
    {
        enableProperControls();
    }

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void m_cCurrentConfigurationActionPerformed(java.awt.event.ActionEvent evt)
    {
        updateConfigTable();
    }

    /**
     * DOCUMENTME.
     *
     * @param  evt  DOCUMENTME
     */
    private void m_cCurrentConfigurationKeyPressed(java.awt.event.KeyEvent evt)
    {
        if ((evt.getKeyCode() == 13) && !m_cCurrentConfiguration.getSelectedItem().equals("<New>"))
        {
            makeConnection();
        }
    }

    /**
     * This method is called when the SSL checkbox is changed. It sets some default values.
     *
     * @param  evt  The event that occurred.
     */
    private void m_cSSLInputMethodTextChanged(java.awt.event.InputMethodEvent evt)
    {
        if (m_cAuthType.getSelectedItem().toString().equals(AUTH_MODE_CLIENT_CERTIFICATE) &&
                (m_cSSL.isSelected() == false))
        {
            JOptionPane.showMessageDialog(this,
                                          "When using a client certificate it is automatically SSL",
                                          "Information", JOptionPane.INFORMATION_MESSAGE);
            m_cSSL.setSelected(true);
        }

        if ((m_cSSL.isSelected() == true) && m_tPort.getText().equals("80"))
        {
            m_tPort.setText("443");
        }
        else if ((m_cSSL.isSelected() == false) && m_tPort.getText().equals("443"))
        {
            m_tPort.setText("80");
        }

        enableProperControls();
    }

    /**
     * This method is called when the trustmode is changed. It will enable the proper controls.
     *
     * @param  evt  The InputMethodEvent.
     */
    private void m_cTrustModeInputMethodTextChanged(java.awt.event.InputMethodEvent evt)
    {
        enableProperControls();
    }

    /**
     * This method is called when the server name gains the focus. This is to determine any changes
     * in the name which could be reflected to the configuration name.
     *
     * @param  evt  The focus event.
     */
    private void m_tServerFocusGained(java.awt.event.FocusEvent evt)
    {
        m_sBefore = m_tConfigName.getText();
        m_sServer = m_tServer.getText();
    }

    /**
     * This method is called when the focus leaves the Server-field. It will determine the proper
     * configuration name.
     *
     * @param  evt  The focus event.
     */
    private void m_tServerFocusLost(java.awt.event.FocusEvent evt)
    {
        if ((m_sBefore == null) || (m_sBefore.length() == 0) ||
                ((m_sBefore.length() > 0) && (m_sServer.length() > 0) &&
                     m_sBefore.startsWith(m_sServer)))
        {
            // We'll change it
            if ((m_cCurrentConfiguration != null) &&
                    (m_cCurrentConfiguration.getSelectedIndex() == 0))
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
}
