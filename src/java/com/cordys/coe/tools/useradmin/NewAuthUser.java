/**
 *  2003 Cordys R&D B.V. All rights reserved.     The computer program(s) is
 * the proprietary information of Cordys R&D B.V.     and provided under the
 * relevant License Agreement containing restrictions     on use and
 * disclosure. Use is subject to the License Agreement.
 */
package com.cordys.coe.tools.useradmin;

import com.cordys.coe.util.general.ldap.LDAPUtils;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;

import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import javax.swing.border.EtchedBorder;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * This form can add authenticated users to the LDAP-schema of Cordys.
 *
 * @author  pgussow
 */
public class NewAuthUser extends JDialog
    implements DocumentListener
{
    /**
     * DOCUMENTME.
     */
    private JComboBox cbOrganization;
    /**
     * Holds the list of DN's that have to be updated.
     */
    private HashMap<String, String> hmDNsToBeUpdated;
    /**
     * DOCUMENTME.
     */
    private JButton jbAdd;
    /**
     * DOCUMENTME.
     */
    private JButton jbCancel;
    /**
     * DOCUMENTME.
     */
    private JButton jbClose;
    /**
     * DOCUMENTME.
     */
    private JPanel jpMainPanel;
    /**
     * DOCUMENTME.
     */
    private JLabel lblFullName;
    /**
     * DOCUMENTME.
     */
    private JLabel lblOrganization;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    /**
     * DOCUMENTME.
     */
    private JLabel lblOSIdentity;
    /**
     * DOCUMENTME.
     */
    private JLabel lblUsername;
    /**
     * Holds the connection to LDAP.
     */
    private LDAPConnection ldapConnection;
    /**
     * Holds the DN of the place where authenticated users are stored.
     */
    private String sAuthUserDN;
    /**
     * Holds the seearchroot for the LDAP-operations.
     */
    private String sSearchRoot;
    /**
     * DOCUMENTME.
     */
    private JTextField tfFullName;
    /**
     * DOCUMENTME.
     */
    private JTextField tfName;
    /**
     * DOCUMENTME.
     */
    private JTextField tfOSIdentity;

    /**
     * Creates new form NewAuthUser.
     *
     * @param  parent            The parent for this form
     * @param  modal             Indicates wethor or not to show this form as a modal dialog
     * @param  ldapConnection    The LDAPDirectory which identifies the connection to LDAP.
     * @param  sSearchRoot       The LDAP-searchroot to use.
     * @param  hmDNsToBeUpdated  The hashmap with entries that should be updated.
     */
    public NewAuthUser(Frame parent, boolean modal, LDAPConnection ldapConnection,
                       String sSearchRoot, HashMap<String, String> hmDNsToBeUpdated)
    {
        super(parent, modal);

        // Set the ldapconnection to use.
        this.ldapConnection = ldapConnection;
        this.sSearchRoot = sSearchRoot;
        this.hmDNsToBeUpdated = hmDNsToBeUpdated;

        // Init the form
        initComponents();

        // Center screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension size = new Dimension(296, 175);
        setSize(size);
        setLocation((screenSize.width - size.width) / 2, (screenSize.height - size.height) / 2);

        // Set the font for each component
        Component[] acComps = jpMainPanel.getComponents();

        for (int iCount = 0; iCount < acComps.length; iCount++)
        {
            acComps[iCount].setFont(jpMainPanel.getFont());
        }

        // Find all the organizations in the LDAP-schema and fill the combobox.
        fillOrganizationComboBox();

        // Create the documentlistener for the textfield.
        tfName.getDocument().addDocumentListener(this);

        // Find the DN where the authenticated users are stored.
        try
        {
            sAuthUserDN = findAuthUserDN();
        }
        catch (Exception exc)
        {
            showMsg("Error retrieving the base-DN for authenticated users:\n" + exc);
            sAuthUserDN = null;
        }
    }

    // NewAuthUser
    /**
     * Part of the DocumentListener interface. Is called when the text of tfName is updated.
     *
     * @param  documentEvent  The event that occured.
     */
    public void changedUpdate(DocumentEvent documentEvent)
    {
        tfFullName.setText(tfName.getText());
        tfOSIdentity.setText(tfName.getText());
    }

    /**
     * Part of the DocumentListener interface. Is called when data from the the text of tfName is
     * inserted. When the text of tfName changes the text of tfFullName and tfOSIdentity is also
     * changed.
     *
     * @param  documentEvent  The event that occured.
     */
    public void insertUpdate(DocumentEvent documentEvent)
    {
        tfFullName.setText(tfName.getText());
        tfOSIdentity.setText(tfName.getText());
    }

    /**
     * Part of the DocumentListener interface. Is called when data from the the text of tfName is
     * removed. When the text of tfName changes the text of tfFullName and tfOSIdentity is also
     * changed.
     *
     * @param  documentEvent  The event that occured.
     */
    public void removeUpdate(DocumentEvent documentEvent)
    {
        tfFullName.setText(tfName.getText());
        tfOSIdentity.setText(tfName.getText());
    }

    /**
     * This method adds the authenticated user to the LDAP-directory,
     */
    private void addAthenticatedUser()
    {
        String sName = tfName.getText();
        String sFullName = tfFullName.getText();
        String sOSIdentity = tfOSIdentity.getText();
        int iSelItem = cbOrganization.getSelectedIndex();

        if ((sName == null) || sName.equals("") || (sFullName == null) || sFullName.equals("") ||
                (sOSIdentity == null) || sOSIdentity.equals("") || (iSelItem < 0))
        {
            showMsg("Not all fields are filled.");
        }
        else
        {
            // All data is valid, add the user.
            if ((sAuthUserDN != null) && !sAuthUserDN.equals(""))
            {
                LDAPAttributeSet asAttrs = new LDAPAttributeSet();
                LDAPAttribute laTemp = new LDAPAttribute("objectclass", "top");
                laTemp.addValue("busauthenticationuser");
                asAttrs.add(laTemp);

                asAttrs.add(new LDAPAttribute("cn", sName));
                asAttrs.add(new LDAPAttribute("description", sFullName));
                asAttrs.add(new LDAPAttribute("osidentity", sOSIdentity));
                asAttrs.add(new LDAPAttribute("defaultcontext",
                                              ((CBOrgEntry) cbOrganization.getItemAt(iSelItem))
                                              .getEntry().getDN()));

                LDAPEntry newEntry = new LDAPEntry("cn=" + sName + "," + sAuthUserDN, asAttrs);

                // Add the entry to LDAP
                try
                {
                    ldapConnection.add(newEntry);

                    if (!hmDNsToBeUpdated.containsKey(newEntry.getDN()))
                    {
                        hmDNsToBeUpdated.put(newEntry.getDN(), newEntry.getDN());
                    }
                    // Clear all input-fields
                    tfName.setText("");
                    // Set the focussed control to tfName
                    tfName.requestFocus();
                }
                catch (Exception exc)
                {
                    showMsg("Error adding the authenticated user to LDAP:\n" + exc);
                }
            }
            else
            {
                showMsg("Since no base-DN is available for storing the authenticated users the user could not be added.");
            }
        }
    }

    /**
     * Closes the dialog.
     */
    private void closeDialog()
    {
        setVisible(false);
        dispose();
    }

    /**
     * This method closes the form.
     */
    private void closeForm()
    {
        closeDialog();
    }

    /**
     * This method retrieves all the organizations from LDAP. It then places all the found
     * organizations in the combobox cbOrganization. The object added is an instance of the
     * CBEntry-class to the combobox.
     */
    private void fillOrganizationComboBox()
    {
        try
        {
            // Clear the current data in the combobox.
            cbOrganization.removeAllItems();

            LDAPEntry[] results = LDAPUtils.searchLDAP(ldapConnection,
                                                       getRootObject().getEntry().getDN(),
                                                       LDAPConnection.SCOPE_SUB,
                                                       "objectclass=organization");

            for (int iCount = 0; iCount < results.length; iCount++)
            {
                CBOrgEntry oeEntry = new CBOrgEntry(results[iCount]);
                cbOrganization.addItem(oeEntry);
            }

            // Set the selectedIndex.
            if (results.length > 0)
            {
                cbOrganization.setSelectedIndex(0);
            }
        }
        catch (Exception exc)
        {
            JOptionPane.showInternalMessageDialog(this, "Error:\n" + exc, "Error",
                                                  JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method searches LDAP for the base-DN to use to store authenticated users. The found DN
     * will be returned.
     *
     * @return  The base-DN where the authenticated users are stored.
     *
     * @throws  LDAPException  This exception is thrown when an error occured when communicating
     *                         with LDAP.
     */
    private String findAuthUserDN()
                           throws LDAPException
    {
        String sReturn = null;

        LDAPEntry[] aeResults = LDAPUtils.searchLDAP(ldapConnection, sSearchRoot,
                                                     LDAPConnection.SCOPE_SUB,
                                                     "(&(cn=authenticated users))");

        if (aeResults.length > 0)
        {
            sReturn = aeResults[0].getDN();
        }

        return sReturn;
    }

    /**
     * This function gets the root Object from LDAP and adds it to the tree Then it will call get
     * organization units. Then it will add all users to the
     *
     * @return  DOCUMENTME
     */
    private LDAPItemEntry getRootObject()
    {
        LDAPItemEntry lLDAPItemEntry = null;

        try
        {
            LDAPEntry[] results = LDAPUtils.searchLDAP(ldapConnection, sSearchRoot,
                                                       LDAPConnection.SCOPE_SUB,
                                                       "objectclass=groupOfNames");

            if (results.length != 0)
            {
                lLDAPItemEntry = new LDAPItemEntry(results[0]);
            }
        }
        catch (LDAPException e)
        {
            showMsg("Error getting the organization schema: " + e);
        }
        return lLDAPItemEntry;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    private void initComponents()
    {
        jpMainPanel = new JPanel();
        lblOSIdentity = new JLabel();
        lblUsername = new JLabel();
        lblFullName = new JLabel();
        lblOrganization = new JLabel();
        tfOSIdentity = new JTextField();
        tfFullName = new JTextField();
        tfName = new JTextField();
        jbClose = new JButton();
        jbAdd = new JButton();
        jbCancel = new JButton();
        cbOrganization = new JComboBox();

        getContentPane().setLayout(null);

        setTitle("Add a new authenticated user");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter()
            {
                public void windowClosing(java.awt.event.WindowEvent evt)
                {
                    closeDialog();
                }
            });

        jpMainPanel.setLayout(null);

        jpMainPanel.setBorder(new EtchedBorder());
        jpMainPanel.setFont(new Font("Tahoma", 0, 10));
        lblOSIdentity.setHorizontalAlignment(SwingConstants.RIGHT);
        lblOSIdentity.setText("OS identity:");
        jpMainPanel.add(lblOSIdentity);
        lblOSIdentity.setBounds(10, 62, 80, 16);

        lblUsername.setHorizontalAlignment(SwingConstants.RIGHT);
        lblUsername.setText("Name:");
        jpMainPanel.add(lblUsername);
        lblUsername.setBounds(10, 12, 80, 16);

        lblFullName.setHorizontalAlignment(SwingConstants.RIGHT);
        lblFullName.setText("Full name:");
        jpMainPanel.add(lblFullName);
        lblFullName.setBounds(10, 37, 80, 16);

        lblOrganization.setHorizontalAlignment(SwingConstants.RIGHT);
        lblOrganization.setText("Default org.:");
        jpMainPanel.add(lblOrganization);
        lblOrganization.setBounds(5, 85, 80, 16);

        jpMainPanel.add(tfOSIdentity);
        tfOSIdentity.setBounds(100, 60, 180, 20);

        jpMainPanel.add(tfFullName);
        tfFullName.setBounds(100, 35, 180, 20);

        jpMainPanel.add(tfName);
        tfName.setBounds(100, 10, 180, 20);

        jbClose.setFont(new java.awt.Font("Tahoma", 0, 10));
        jbClose.setMnemonic('C');
        jbClose.setText("Close");
        jbClose.setMaximumSize(new java.awt.Dimension(73, 26));
        jbClose.setMinimumSize(new java.awt.Dimension(73, 26));
        jbClose.setPreferredSize(new java.awt.Dimension(73, 26));
        jbClose.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    closeForm();
                }
            });

        jpMainPanel.add(jbClose);
        jbClose.setBounds(180, 120, 73, 20);

        jbAdd.setFont(new java.awt.Font("Tahoma", 0, 10));
        jbAdd.setMnemonic('A');
        jbAdd.setText("Add");
        jbAdd.setMaximumSize(new java.awt.Dimension(73, 26));
        jbAdd.setMinimumSize(new java.awt.Dimension(73, 26));
        jbAdd.setPreferredSize(new java.awt.Dimension(73, 26));
        jbAdd.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    addAthenticatedUser();
                }
            });

        jpMainPanel.add(jbAdd);
        jbAdd.setBounds(30, 120, 73, 20);

        jbCancel.setFont(new java.awt.Font("Tahoma", 0, 10));
        jbCancel.setMnemonic('n');
        jbCancel.setText("Cancel");
        jbCancel.setMaximumSize(new java.awt.Dimension(73, 26));
        jbCancel.setMinimumSize(new java.awt.Dimension(73, 26));
        jbCancel.setPreferredSize(new java.awt.Dimension(73, 26));
        jbCancel.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    closeForm();
                }
            });

        jpMainPanel.add(jbCancel);
        jbCancel.setBounds(105, 120, 73, 20);

        jpMainPanel.add(cbOrganization);
        cbOrganization.setBounds(100, 85, 180, 20);

        getContentPane().add(jpMainPanel);
        jpMainPanel.setBounds(0, 0, 290, 150);

        pack();
    }

    /**
     * This method shows a dialog with the passed on message.
     *
     * @param  sMSG  The message to display.
     */
    private void showMsg(String sMSG)
    {
        JOptionPane.showMessageDialog(this, sMSG);
    }

    /**
     * Innerclass CBOrgEntry. This class is used to store items in the combobox so the LDAPEntry can
     * be saved.
     */
    private class CBOrgEntry
    {
        /**
         * Holds the LDAPEntry for this obejct.
         */
        private LDAPEntry entry;

        /**
         * Constructor.
         *
         * @param  entry  The LDAPEntry to use for the instance.
         */
        public CBOrgEntry(LDAPEntry entry)
        {
            this.entry = entry;
        }

        /**
         * This method returns the LDAPEntry for this object.
         *
         * @return  The LDAPEntry for this object.
         */
        public LDAPEntry getEntry()
        {
            return entry;
        }

        /**
         * This method returns the String-representation of the organization-entry in LDAP.
         *
         * @return  The String-representation of the organization-entry in LDAP.
         */
        @Override public String toString()
        {
            return LDAPUtils.getAttrValue(entry, "description");
        }
    }
}
