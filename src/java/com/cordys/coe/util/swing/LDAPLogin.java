/**
 *  2003 Cordys R&D B.V. All rights reserved.     The computer program(s) is
 * the proprietary information of Cordys R&D B.V.     and provided under the
 * relevant License Agreement containing restrictions     on use and
 * disclosure. Use is subject to the License Agreement.
 */
package com.cordys.coe.util.swing;

import com.eibus.directory.soap.LDAPDirectory;

import com.eibus.util.system.EIBProperties;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPException;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import javax.swing.border.EtchedBorder;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Form to login to LDAP.
 */
public class LDAPLogin extends JDialog
{
    /**
     * Holds whether or not to access LDAP by LDAPConnection instead of LDAPDirectory.
     */
    private boolean bConnection;
    /**
     * Indicates whether or not OK was pressed.
     */
    private boolean bOk = false;
    /**
     * DOCUMENTME.
     */
    private JCheckBox bSaveDefaultEIB;
    /**
     * DOCUMENTME.
     */
    private JButton jbCancel;
    /**
     * DOCUMENTME.
     */
    private JButton jbConnect1;
    /**
     * DOCUMENTME.
     */
    private JLabel jLabel1;
    /**
     * DOCUMENTME.
     */
    private JLabel jLabel11;
    /**
     * DOCUMENTME.
     */
    private JLabel jLabel12;
    /**
     * DOCUMENTME.
     */
    private JLabel jLabel13;
    /**
     * DOCUMENTME.
     */
    private JLabel jLabel132;
    /**
     * DOCUMENTME.
     */
    private JPanel jPanel1;
    /**
     * Holds the LDAPConnection.
     */
    private LDAPConnection lCon;
    /**
     * Holds the LDAP-drirectory.
     */
    private LDAPDirectory ldap;
    /**
     * DOCUMENTME.
     */
    private JLabel lusingDefault;
    /**
     * DOCUMENTME.
     */
    private JPasswordField tfLdapPass;
    /**
     * DOCUMENTME.
     */
    private JTextField tfPort;
    /**
     * DOCUMENTME.
     */
    private JTextField tfSearchRoot;
    /**
     * DOCUMENTME.
     */
    private JTextField tfServer1;
    /**
     * DOCUMENTME.
     */
    private JTextField tfUser;

    /**
     * Creates the login-form.
     *
     * @param  parent  The parent
     * @param  modal   Whether or not to display it as a modal dialog.
     */
    public LDAPLogin(Frame parent, boolean modal)
    {
        super(parent, modal);
        initComponents();
        readDefaults();
        bConnection = false;
    }
    // LDAPLogin

    /**
     * Creates the login-form.
     *
     * @param  parent       The parent
     * @param  modal        Whether or not to display it as a modal dialog.
     * @param  bConnection  Whether or not to access LDAP by LDAPConnection instead of LDAPDirectory
     */
    public LDAPLogin(Frame parent, boolean modal, boolean bConnection)
    {
        super(parent, modal);
        initComponents();
        readDefaults();
        this.bConnection = bConnection;
    }
    // LDAPLogin

    /**
     * Main method.
     *
     * @param  args  the command line arguments posible implementation : LdapLogin lLDapLogin = new
     *               LdapLogin(new JFrame(),true); lLDapLogin.show(); if (lLDapLogin.isOk()) {
     *               lDAPdir = lLDapLogin.getLDAPDir(); }
     */
    public static void main(String[] args)
    {
        new LDAPLogin(new JFrame(), true).setVisible(true);
    }
    // main

    /**
     * Returns the LDAPConnection.
     *
     * @return  The LDAPConnection.
     */
    public LDAPConnection getLDAPConnection()
    {
        return lCon;
    }
    // getLDAPConnection

    /**
     * Returns the LDAPDirectory.
     *
     * @return  The LDAPDirectory.
     */
    public LDAPDirectory getLDAPDirectory()
    {
        return ldap;
    }
    // getLDAPDirectory

    /**
     * This method returns the searchroot for the LDAPserver.
     *
     * @return  The searchroot for the LDAPserver.
     */
    public String getSearchRoot()
    {
        return tfSearchRoot.getText();
    }
    // getSearchRoot

    /**
     * Returns wether or not the logon was succesfull.
     *
     * @return  Wether or not the logon was succesfull.
     */
    public boolean isOk()
    {
        return bOk;
    }
    // isOk

    /**
     * Handles the Cancel-button.
     */
    private void CancelClick()
    {
        bOk = false;
        setVisible(false);
        dispose();
    }
    // CancelClick

    /**
     * This method closes the dialog.
     */
    private void closeDialog()
    {
        bOk = false;
        setVisible(false);
        dispose();
    }
    // closeDialog

    /**
     * This method connects to LDAP. Depending on the bConnection-variable this is done either
     * trough the LDAPDirectory or trough the LDAPConnection-class.
     */
    private void connectToLDAP()
    {
        try
        {
            String sServer = tfServer1.getText();
            int iPort = Integer.parseInt(tfPort.getText());
            String sUser = tfUser.getText();
            String sPassword = new String(tfLdapPass.getPassword());

            if (bConnection == false)
            {
                ldap = new LDAPDirectory(sServer, iPort, sUser, sPassword);
            }
            else
            {
                lCon = new LDAPConnection();
                lCon.connect(tfServer1.getText(), Integer.parseInt(tfPort.getText()));
                lCon.bind(LDAPConnection.LDAP_V3, tfUser.getText(),
                          new String(tfLdapPass.getPassword()).getBytes());
            }

            if (bSaveDefaultEIB.isSelected())
            {
                try
                {
                    EIBProperties.setLDAPServer(tfServer1.getText() + ":" + tfPort.getText());
                    EIBProperties.saveProperty("bus.administrator.user", tfUser.getText());
                    EIBProperties.saveProperty("bus.administrator.pwd",
                                               encode(new String(tfLdapPass.getPassword())));
                    EIBProperties.saveProperty("ldap.root", tfSearchRoot.getText());
                }
                catch (Exception e)
                {
                    showMsg("Error saving defaults");
                }
            }
            bOk = true;
            setVisible(false);
            dispose();
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
    // connectToLDAP

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
    // decode

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
    // encode

    /**
     * Checks whether or not the enter-key was pressed.
     *
     * @param  evt  The event that occured.
     */
    private void formKeyReleased(KeyEvent evt)
    {
        if (evt.getKeyCode() == 10)
        {
            connectToLDAP();
        }
    }
    // formKeyReleased

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    private void initComponents()
    {
        jPanel1 = new JPanel();
        jLabel12 = new JLabel();
        jLabel13 = new JLabel();
        tfSearchRoot = new JTextField();
        tfServer1 = new JTextField();
        tfUser = new JTextField();
        jbCancel = new JButton();
        tfPort = new JTextField();
        jLabel132 = new JLabel();
        jbConnect1 = new JButton();
        tfLdapPass = new JPasswordField();
        jLabel1 = new JLabel();
        lusingDefault = new JLabel();
        jLabel11 = new JLabel();
        bSaveDefaultEIB = new JCheckBox();

        getContentPane().setLayout(null);

        setTitle("Login to LDAP");
        addWindowListener(new WindowAdapter()
            {
                /**
                 * Close.
                 *
                 * @param  evt  event.
                 */
                public void windowClosing(WindowEvent evt)
                {
                    closeDialog();
                }
            });

        jPanel1.setLayout(null);

        jPanel1.setBorder(new EtchedBorder());
        jPanel1.addKeyListener(new KeyAdapter()
            {
                /**
                 * Key.
                 *
                 * @param  evt  event.
                 */
                public void keyTyped(KeyEvent evt)
                {
                    formKeyReleased(evt);
                }
            });

        jLabel12.setFont(new Font("Tahoma", 0, 10));
        jLabel12.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel12.setText("User:");
        jPanel1.add(jLabel12);
        jLabel12.setBounds(10, 28, 110, 13);

        jLabel13.setFont(new Font("Tahoma", 0, 10));
        jLabel13.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel13.setText("Search root:");
        jPanel1.add(jLabel13);
        jLabel13.setBounds(10, 64, 110, 13);

        tfSearchRoot.setFont(new Font("Tahoma", 0, 10));
        tfSearchRoot.setName("tfServer");
        tfSearchRoot.addKeyListener(new KeyAdapter()
            {
                /**
                 * Key.
                 *
                 * @param  evt  event.
                 */
                public void keyReleased(KeyEvent evt)
                {
                    formKeyReleased(evt);
                }
            });

        jPanel1.add(tfSearchRoot);
        tfSearchRoot.setBounds(130, 64, 230, 17);

        tfServer1.setFont(new Font("Tahoma", 0, 10));
        tfServer1.setName("tfServer");
        tfServer1.addKeyListener(new KeyAdapter()
            {
                /**
                 * Key.
                 *
                 * @param  evt  event.
                 */
                public void keyReleased(KeyEvent evt)
                {
                    formKeyReleased(evt);
                }
            });

        jPanel1.add(tfServer1);
        tfServer1.setBounds(130, 10, 190, 17);

        tfUser.setFont(new Font("Tahoma", 0, 10));
        tfUser.setName("tfServer");
        tfUser.addKeyListener(new KeyAdapter()
            {
                /**
                 * Key.
                 *
                 * @param  evt  event.
                 */
                public void keyReleased(KeyEvent evt)
                {
                    formKeyReleased(evt);
                }
            });

        jPanel1.add(tfUser);
        tfUser.setBounds(130, 28, 230, 17);

        jbCancel.setFont(new Font("Tahoma", 0, 10));
        jbCancel.setText("Cancel");
        jbCancel.addActionListener(new ActionListener()
            {
                /**
                 * Key.
                 *
                 * @param  evt  event.
                 */
                public void actionPerformed(ActionEvent evt)
                {
                    CancelClick();
                }
            });

        jbCancel.addKeyListener(new KeyAdapter()
            {
                /**
                 * Key.
                 *
                 * @param  evt  event.
                 */
                public void keyReleased(KeyEvent evt)
                {
                    formKeyReleased(evt);
                }
            });

        jPanel1.add(jbCancel);
        jbCancel.setBounds(204, 84, 64, 20);

        tfPort.setFont(new Font("Tahoma", 0, 10));
        tfPort.setName("tfServer");
        tfPort.addKeyListener(new KeyAdapter()
            {
                /**
                 * Key.
                 *
                 * @param  evt  event.
                 */
                public void keyReleased(KeyEvent evt)
                {
                    formKeyReleased(evt);
                }
            });

        jPanel1.add(tfPort);
        tfPort.setBounds(330, 10, 30, 17);

        jLabel132.setFont(new Font("Tahoma", 0, 10));
        jLabel132.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel132.setText("Password:");
        jPanel1.add(jLabel132);
        jLabel132.setBounds(10, 46, 110, 13);

        jbConnect1.setFont(new Font("Tahoma", 0, 10));
        jbConnect1.setText("Connect");
        jbConnect1.addActionListener(new ActionListener()
            {
                /**
                 * Key.
                 *
                 * @param  evt  event.
                 */
                public void actionPerformed(ActionEvent evt)
                {
                    connectToLDAP();
                }
            });

        jbConnect1.addKeyListener(new KeyAdapter()
            {
                /**
                 * Key.
                 *
                 * @param  evt  event.
                 */
                public void keyReleased(KeyEvent evt)
                {
                    formKeyReleased(evt);
                }
            });

        jPanel1.add(jbConnect1);
        jbConnect1.setBounds(130, 84, 72, 20);

        tfLdapPass.setFont(new Font("Tahoma", 0, 10));
        tfLdapPass.addKeyListener(new KeyAdapter()
            {
                /**
                 * Key.
                 *
                 * @param  evt  event.
                 */
                public void keyReleased(KeyEvent evt)
                {
                    formKeyReleased(evt);
                }
            });

        jPanel1.add(tfLdapPass);
        tfLdapPass.setBounds(130, 46, 230, 17);

        jLabel1.setFont(new Font("Tahoma", 0, 10));
        jLabel1.setText(":");
        jPanel1.add(jLabel1);
        jLabel1.setBounds(324, 10, 4, 13);

        lusingDefault.setFont(new Font("Tahoma", 0, 10));
        lusingDefault.setHorizontalAlignment(SwingConstants.CENTER);
        lusingDefault.setText("Connecting to LDAP using defaults....");
        jPanel1.add(lusingDefault);
        lusingDefault.setBounds(-84, -50, 200, 13);

        jLabel11.setFont(new Font("Tahoma", 0, 10));
        jLabel11.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel11.setText("LDAP Server:");
        jPanel1.add(jLabel11);
        jLabel11.setBounds(10, 10, 110, 13);

        bSaveDefaultEIB.setFont(new Font("Tahoma", 0, 10));
        bSaveDefaultEIB.setText("System default");
        bSaveDefaultEIB.addKeyListener(new KeyAdapter()
            {
                /**
                 * Key.
                 *
                 * @param  evt  event.
                 */
                public void keyReleased(KeyEvent evt)
                {
                    formKeyReleased(evt);
                }
            });

        jPanel1.add(bSaveDefaultEIB);
        bSaveDefaultEIB.setBounds(278, 84, 84, 21);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(8, 8, 380, 110);

        pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(new Dimension(400, 150));
        setLocation((screenSize.width - 400) / 2, (screenSize.height - 150) / 2);
    }
    // initComponents

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
        tfLdapPass.setText(sRealPassword);
        tfServer1.setText(sRealLDAPServer);
        tfPort.setText(sRealPortNumber);

        // tfServer1.setText(EIBProperties.getProperty(EIBProperties.LDAP_SERVER));
        // tfPort.setText(EIBProperties.getProperty(EIBProperties.LDAP_PORT));
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
        JOptionPane.showMessageDialog(this, sMSG);
    }
}
