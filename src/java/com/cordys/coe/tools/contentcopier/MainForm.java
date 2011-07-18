/**
 * © 2003 Cordys R&D B.V. All rights reserved.     The computer program(s) is
 * the proprietary information of Cordys R&D B.V.     and provided under the
 * relevant License Agreement containing restrictions     on use and
 * disclosure. Use is subject to the License Agreement.
 */
package com.cordys.coe.tools.contentcopier;

import com.cordys.coe.exception.GeneralException;
import com.cordys.coe.util.general.Util;
import com.cordys.coe.util.swing.LDAPLogin;

import com.eibus.xml.nom.Document;

import com.novell.ldap.LDAPConnection;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Toolkit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import javax.swing.border.EtchedBorder;

/**
 * This program can be used to copy LDAP entries from one LDAP server to another.
 *
 * @author  pgussow
 */
public class MainForm extends JFrame
{
    /**
     * Holds it's own instance.
     */
    private static MainForm mfSelf;
    /**
     * DOCUMENTME.
     */
    private JMenu jMenu1;
    /**
     * DOCUMENTME.
     */
    private JMenu jMenu2;
    /**
     * DOCUMENTME.
     */
    private JMenuBar jMenuBar1;
    /**
     * Holds the LDAP-connection for the left-side.
     */
    private LDAPConnection lcLeft;
    /**
     * Holds the LDAP-connection for the right-side.
     */
    private LDAPConnection lcRight;
    /**
     * The left panel.
     */
    private LDAPPanel lpLeftPanel;
    /**
     * The right panel.
     */
    private LDAPPanel lpRightPanel;
    /**
     * DOCUMENTME.
     */
    private JMenuItem miLeftConnect;
    /**
     * DOCUMENTME.
     */
    private JMenuItem miRightConnect;
    /**
     * DOCUMENTME.
     */
    private JSplitPane spSplitPane;
    /**
     * Holds the statusbar.
     */
    private JTextField tfStatusBar;

    /**
     * Constructor.
     *
     * @throws  GeneralException  DOCUMENTME
     */
    public MainForm()
             throws GeneralException
    {
        try
        {
            if (mfSelf == null)
            {
                mfSelf = this;
            }

            if (Util.DEBUG)
            {
                System.out.println("Loading contentcopier.gif.");
            }
            setIconImage(Toolkit.getDefaultToolkit().createImage(MainForm.class.getResource("contentcopier.gif")));
            initComponents();

            miLeftConnectActionPerformed();
            miRightConnectActionPerformed();
        }
        catch (Exception e)
        {
            throw new GeneralException(e, "Error creating the form");
        }
    } // MainForm

    /**
     * Main method. Starts the application
     *
     * @param  args  the command line arguments
     */
    public static void main(String[] args)
    {
        try
        {
            // For some strange reason we have to create a document before the AWT-threads
            // are started, otherwise the NOM lib does not work properly.
            Document dDoc = new Document();
            dDoc.createElement("dummy");

            new MainForm().setVisible(true);
        }
        catch (Exception e)
        {
            System.out.println("Exception: " + e);
            e.printStackTrace(); // To change body of catch statement use Options | File Templates.
        }
    } // main

    /**
     * Alerts an message.
     *
     * @param  sMessage  The message to display.
     */
    private void alert(String sMessage)
    {
        JOptionPane.showMessageDialog(this, sMessage);
    } // alert

    /**
     * Exits the application.
     */
    private void exitForm()
    {
        System.exit(0);
    } // exitForm

    /**
     * This method is called from within the constructor to initialize the form.
     *
     * @throws  GeneralException  DOCUMENTME
     */
    private void initComponents()
                         throws GeneralException
    {
        spSplitPane = new JSplitPane();
        jMenuBar1 = new JMenuBar();
        jMenu1 = new JMenu();
        miLeftConnect = new JMenuItem();
        jMenu2 = new JMenu();
        miRightConnect = new JMenuItem();

        setTitle("LDAP Content Copier");
        setFont(new Font("Tahoma", 0, 10));
        addWindowListener(new WindowAdapter()
            {
                public void windowClosing(WindowEvent evt)
                {
                    exitForm();
                }
            });

        lpLeftPanel = new LDAPPanel();
        lpRightPanel = new LDAPPanel();
        spSplitPane.setLeftComponent(lpLeftPanel);
        spSplitPane.setRightComponent(lpRightPanel);

        getContentPane().add(spSplitPane, BorderLayout.CENTER);

        jMenuBar1.setFont(new Font("Tahoma", 0, 10));
        jMenu1.setText("Left side");
        jMenu1.setFont(new Font("Tahoma", 1, 10));
        jMenu1.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent evt)
                {
                    miLeftConnectActionPerformed();
                }
            });

        miLeftConnect.setFont(new Font("Tahoma", 0, 10));
        miLeftConnect.setText("Connect");
        miLeftConnect.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    miLeftConnectActionPerformed();
                }
            });
        jMenu1.add(miLeftConnect);
        jMenuBar1.add(jMenu1);
        jMenu2.setText("Right ride");
        jMenu2.setFont(new Font("Tahoma", 1, 10));
        miRightConnect.setFont(new Font("Tahoma", 0, 10));
        miRightConnect.setText("Connect");
        miRightConnect.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    miRightConnectActionPerformed();
                }
            });

        jMenu2.add(miRightConnect);
        jMenuBar1.add(jMenu2);
        setJMenuBar(jMenuBar1);

        // Statusbar.
        tfStatusBar = new JTextField("");
        tfStatusBar.setBorder(new EtchedBorder());
        tfStatusBar.setEditable(false);
        getContentPane().add(tfStatusBar, BorderLayout.SOUTH);

        pack();
    } // initComponents

    /**
     * This method connects the left side.
     */
    private void miLeftConnectActionPerformed()
    {
        LDAPLogin lLogin = new LDAPLogin(this, true, true);
        lLogin.setTitle("Enter left side LDAP Information");
        lLogin.setVisible(true);

        if (lLogin.isOk())
        {
            lcLeft = lLogin.getLDAPConnection();
            lpLeftPanel.setTitle("LDAP server: " + lcLeft.getHost() + ":" + lcLeft.getPort());

            try
            {
                lpLeftPanel.initTree(lcLeft, lLogin.getSearchRoot());
            }
            catch (Exception e)
            {
                alert("Error initializing the tree\n" + e);
            }
        }
    } // miLeftConnectActionPerformed

    /**
     * This method connects to the right LDAP.
     */
    private void miRightConnectActionPerformed()
    {
        LDAPLogin lLogin = new LDAPLogin(this, true, true);
        lLogin.setTitle("Enter right side LDAP Information");
        lLogin.setVisible(true);

        if (lLogin.isOk())
        {
            lcRight = lLogin.getLDAPConnection();
            lpRightPanel.setTitle("LDAP server: " + lcRight.getHost() + ":" + lcRight.getPort());

            try
            {
                lpRightPanel.initTree(lcRight, lLogin.getSearchRoot());
            }
            catch (Exception e)
            {
                alert("Error initializing the tree\n" + e);
            }
        }
    } // miRightConnectActionPerformed
} // MainForm
