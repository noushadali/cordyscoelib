/**
 * © 2003 Cordys R&D B.V. All rights reserved.     The computer program(s) is
 * the proprietary information of Cordys R&D B.V.     and provided under the
 * relevant License Agreement containing restrictions     on use and
 * disclosure. Use is subject to the License Agreement.
 */
package com.cordys.coe.tools.useradmin;

import com.cordys.coe.util.general.ldap.LDAPPublisher;
import com.cordys.coe.util.general.ldap.LDAPUtils;
import com.cordys.coe.util.swing.LDAPLogin;
import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.table.DefaultTableModel;

/**
 * Program to maintain users.
 */
public class UserAdmin extends JFrame
    implements ActionListener
{
    /**
     * Holds the roles.
     */
    private ButtonGroup btnGrpRoles;
    /**
     * Holds all authenticated users.
     */
    private LocalDNDList dndAuthusr;
    /**
     * Holds all roles.
     */
    private LocalDNDList dndRolesList;
    /**
     * Holds all organizations.
     */
    private DnDjTree dndTreeOganisations;
    /**
     * Holds the arraylist with strings of DN's that should be updated.
     */
    private HashMap<String, String> hmDNsToBeUpdated;
    /**
     * Total menubar.
     */
    private JMenuBar jMenuBar1;
    /**
     * Exit-menu-item.
     */
    private JMenuItem jmExit;
    /**
     * File-menuitem.
     */
    private JMenu jmFile;
    /**
     * Role-menu.
     */
    private JMenu jmRole;
    /**
     * Seperator.
     */
    private JSeparator jSeparator1;
    /**
     * The LDAP Directory.
     */
    private LDAPConnection lcConnection;
    /**
     * Menuitem to add a new organizational user.
     */
    private JMenuItem miNewAuthUser;
    /**
     * The contextmenu point.
     */
    private Point pContextMenuPoint;
    /**
     * Splitter.
     */
    private JSplitPane slpAuthMain;
    /**
     * This splitter splits all 3 splitters and the properties-pane on the bottom.
     */
    private JSplitPane slpMainProps;
    /**
     * Splitter.
     */
    private JSplitPane slpOrgRoles;
    /**
     * Scrollpane for the organizations.
     */
    private JScrollPane spOrganization;
    /**
     * Scrollpane for the users.
     */
    private JScrollPane spUsers;
    /**
     * Scrollpane for the roles.
     */
    private JScrollPane spUsers1;
    /**
     * The search-root.
     */
    private String sSearchRoot;
    /**
     * Holds the table with the properties of the selected node.
     */
    private JTable tblProperties;

    /**
     * Constructor.
     */
    public UserAdmin()
    {
        setIconImage(Toolkit.getDefaultToolkit().createImage(UserAdmin.class.getResource("organizationaluser.gif")));

        initComponents();

        hmDNsToBeUpdated = new HashMap<String, String>();
    }

    /**
     * the main startup method. Calls the ldap login class
     *
     * @param  args  the command line arguments
     */
    public static void main(String[] args)
    {
        UserAdmin uUsrAdmin = new UserAdmin();
        uUsrAdmin.setVisible(true);
    }

    /**
     * act on a click at the popupmenu replaces the default context attribute in the LDAP.
     *
     * @param  e  evt
     */
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() instanceof JMenuItemWithObject)
        {
            JMenuItemWithObject jmiOrg = (JMenuItemWithObject) e.getSource();
            int iLocation = dndAuthusr.locationToIndex(pContextMenuPoint);
            Object oLdapObject = dndAuthusr.getModel().getElementAt(iLocation);

            LDAPEntry lLdapEntry = null;
            String sDevContext = null;

            // selected user
            if (oLdapObject instanceof LDAPItemEntry)
            {
                LDAPItemEntry lLDAPItemEntry = (LDAPItemEntry) oLdapObject;
                lLdapEntry = lLDAPItemEntry.getEntry();
            }

            // selected org
            Object oObject = jmiOrg.getObject();

            if (oObject instanceof LDAPItemEntry)
            {
                LDAPEntry lLdapEntryOrg = ((LDAPItemEntry) oObject).getEntry();
                sDevContext = lLdapEntryOrg.getDN();
            }

            // create the arribute that has to be added
            LDAPAttribute attrDevCon = new LDAPAttribute("defaultcontext", sDevContext);

            try
            {
                LDAPUtils.changeLDAP(lcConnection, lLdapEntry,
                                     LDAPUtils.LDAPCOMMAND_REPLACE_ATTRIBUTE, attrDevCon);

                if (!hmDNsToBeUpdated.containsKey(lLdapEntry.getDN()))
                {
                    hmDNsToBeUpdated.put(lLdapEntry.getDN(), lLdapEntry.getDN());
                }
            }
            catch (LDAPException ldape)
            {
                System.out.println(ldape);
            }
        }
    }

    /**
     * This method returns an array containing the roles that should be added to the new
     * organizational user. It first checks if roles are selected. If not, the default role from the
     * menu-item is used.
     *
     * @return  retuns a string with the dn of the current selected role
     */
    @SuppressWarnings("deprecation")
    public LDAPEntry[] getCurrentRoles()
    {
        LDAPEntry[] lLDAPReturn = null;

        Object[] oaRoles = dndRolesList.getSelectedValues();

        if ((oaRoles != null) && (oaRoles.length > 0))
        {
            // Roles are selected, so use those.
            lLDAPReturn = new LDAPEntry[oaRoles.length];

            for (int iCount = 0; iCount < oaRoles.length; iCount++)
            {
                LDAPItemEntry lie = (LDAPItemEntry) oaRoles[iCount];
                lLDAPReturn[iCount] = lie.getEntry();
            }
        }
        else
        {
            // No roles selected, use the default one.
            for (Enumeration<?> e = btnGrpRoles.getElements(); e.hasMoreElements();)
            {
                Object oObject = e.nextElement();

                if (oObject instanceof JRBtnMItemWithObject)
                {
                    JRBtnMItemWithObject jrbRadio = (JRBtnMItemWithObject) oObject;

                    if (jrbRadio.isSelected())
                    {
                        if (jrbRadio.getObject() instanceof LDAPItemEntry)
                        {
                            LDAPItemEntry lLDAPItemEntry = (LDAPItemEntry) jrbRadio.getObject();
                            lLDAPReturn = new LDAPEntry[1];
                            lLDAPReturn[0] = lLDAPItemEntry.getEntry();
                            break;
                        }
                    }
                }
            }
        }
        return lLDAPReturn;
    }

    /**
     * Will set the LDAP Dir. Wil com from LDAPLoging
     *
     * @param  ldapConnection  reqires LDAPDirectory
     */
    public void setLDAPDir(LDAPConnection ldapConnection)
    {
        this.lcConnection = ldapConnection;
    }

    /**
     * This method sets the search-root to use for this tool.
     *
     * @param  sSearchRoot  The new searchroot for the LDAP-connection.
     */
    public void setLDAPSearchRoot(String sSearchRoot)
    {
        this.sSearchRoot = sSearchRoot;
    }

    /**
     * Changes the splitter location.
     */
    private void changeSplitterLocation()
    {
        slpOrgRoles.setDividerLocation(getWidth() - 250);
    }

    // changeSplitterLocation
    /**
     * this function creates the DNDTree and sets the default treeCellrender.
     */
    private void createDNDTree()
    {
        dndTreeOganisations = new DNDTree(getRootObject(), lcConnection);
        spOrganization.setViewportView(dndTreeOganisations);
        dndTreeOganisations.setTreeCellRenderer(new CordysUserRenderer());
        createOrgPopUpMenu();
    }

    // createDNDTree

    /**
     * DOCUMENTME.
     */
    private void createOrgPopUpMenu()
    {
        final JPopupMenu popupMenu = new JPopupMenu("test");
        JMenuItem jmiLabel = new JMenuItem();
        jmiLabel.setFont(new Font("Tahoma", 0, 10));
        jmiLabel.setBackground(new Color(10, 36, 106));
        jmiLabel.setForeground(new Color(255, 255, 255));
        jmiLabel.setText("Default Context :");
        jmiLabel.setEnabled(false);
        popupMenu.add(jmiLabel);
        popupMenu.setToolTipText("Change default context to ");
        popupMenu.addSeparator();

        try
        {
            LDAPEntry[] results = LDAPUtils.searchLDAP(lcConnection,
                                                       (getRootObject().getEntry()).getDN(),
                                                       LDAPConnection.SCOPE_SUB,
                                                       "(|(objectclass=organization)(objectclass=organizationalunit))");

            for (int iCount = 0; iCount < results.length; iCount++)
            {
                JMenuItemWithObject jmiOrg = new JMenuItemWithObject(new LDAPItemEntry(results[iCount]));

                jmiOrg.setFont(new Font("Tahoma", 0, 10));

                if (!LDAPUtils.getAttrValue(results[iCount], "o").equals(""))
                {
                    jmiOrg.setText(LDAPUtils.getAttrValue(results[iCount], "o"));
                }
                else
                {
                    jmiOrg.setText("   " + LDAPUtils.getAttrValue(results[iCount], "ou"));
                }
                jmiOrg.addActionListener(this);
                popupMenu.add(jmiOrg);
            }
        }
        catch (LDAPException e)
        {
            System.out.println("Error getting the organization schema: " + e);
        }

        dndAuthusr.addMouseListener(new MouseAdapter()
            {
                public void mouseClicked(MouseEvent me)
                {
                    if (SwingUtilities.isRightMouseButton(me) && !dndAuthusr.isSelectionEmpty() &&
                            (dndAuthusr.locationToIndex(me.getPoint()) ==
                                 dndAuthusr.getSelectedIndex()))
                    {
                        pContextMenuPoint = new Point(me.getX(), me.getY());
                        popupMenu.show(dndAuthusr, me.getX(), me.getY());
                    }
                }
            });
    }

    // createOrgPopUpMenu
    /**
     * creates the menuitems who contain the roles.
     *
     * @param  oObject  the LDAP entry to add to the JRBtnMItemWithObject
     */
    private void createRoleMenu(Object oObject)
    {
        if (oObject instanceof LDAPItemEntry)
        {
            LDAPItemEntry lLDAPItemEntry = (LDAPItemEntry) oObject;

            LDAPEntry lLDAPEntry = lLDAPItemEntry.getEntry();
            JRadioButtonMenuItem jmiRoleNew = new JRBtnMItemWithObject(oObject);
            jmiRoleNew.setFont(new Font("Tahoma", 0, 10));
            jmiRoleNew.setText(LDAPUtils.getAttrValue(lLDAPEntry, "cn"));
            jmiRoleNew.setToolTipText(lLDAPEntry.getDN());
            btnGrpRoles.add(jmiRoleNew);
            jmRole.add(jmiRoleNew);
        }
    }

    /**
     * This method deletes all the selected athenticated users from the LDAP-server. If an
     * authenticated user is removed all linked organizational users are removed too.
     *
     * @param  evt  The KeyReleased event.
     */
    @SuppressWarnings("deprecation")
    private void deleteAuthenticatedUser(KeyEvent evt)
    {
        // Check for the del-key
        if (evt.getKeyCode() == 127)
        {
            int iConfirm = JOptionPane.showConfirmDialog(this,
                                                         "Are you sure you want to delete the authenticated user?\nAttention: All organizational users linked to this user will also be deleted!!!",
                                                         "Delete authenticated user",
                                                         JOptionPane.YES_NO_OPTION,
                                                         JOptionPane.WARNING_MESSAGE);

            if (iConfirm == JOptionPane.YES_OPTION)
            {
                Object[] oSelected = dndAuthusr.getSelectedValues();

                for (int iCount = 0; iCount < oSelected.length; iCount++)
                {
                    LDAPItemEntry lieEntry = (LDAPItemEntry) oSelected[iCount];

                    try
                    {
                        // Delete all linked organizational users
                        LDAPEntry[] eaResults = LDAPUtils.searchLDAP(lcConnection, sSearchRoot,
                                                                     LDAPConnection.SCOPE_SUB,
                                                                     "(&(objectclass=busorganizationaluser)(authenticationuser=" +
                                                                     lieEntry.getEntry().getDN() +
                                                                     "))");

                        for (int iResCnt = 0; iResCnt < eaResults.length; iResCnt++)
                        {
                            LDAPEntry leTemp = new LDAPItemEntry(eaResults[iResCnt]).getEntry();

                            if (!hmDNsToBeUpdated.containsKey(leTemp.getDN()))
                            {
                                hmDNsToBeUpdated.put(leTemp.getDN(), leTemp.getDN());
                            }
                            LDAPUtils.changeLDAP(lcConnection, leTemp,
                                                 LDAPUtils.LDAPCOMMAND_DELETE);
                        }

                        // Delete itself.
                        LDAPEntry leTemp = ((LDAPItemEntry) oSelected[iCount]).getEntry();

                        if (!hmDNsToBeUpdated.containsKey(leTemp.getDN()))
                        {
                            hmDNsToBeUpdated.put(leTemp.getDN(), leTemp.getDN());
                        }
                        LDAPUtils.changeLDAP(lcConnection,
                                             ((LDAPItemEntry) oSelected[iCount]).getEntry(),
                                             LDAPUtils.LDAPCOMMAND_DELETE);
                    }
                    catch (Exception exc)
                    {
                        showMsg("Authenticated user " + lieEntry +
                                " not deleted because there was a problem deleting the linked organizational users.\n" +
                                exc);
                    }
                }

                // Refresh the current view.
                if (oSelected.length > 0)
                {
                    getAllAuthUsers();
                    createDNDTree();
                }
            }

            // End if confirmed
        }
    }

    /**
     * This method displays the attributes of the entry selected in the table.
     *
     * @param  leEntry  The LDAP-entry to display.
     */
    private void displayEntry(LDAPEntry leEntry)
    {
        DefaultTableModel dtm = (DefaultTableModel) tblProperties.getModel();

        while (dtm.getRowCount() > 0)
        {
            dtm.removeRow(0);
        }

        Iterator<?> iAttributes = leEntry.getAttributeSet().iterator();

        while (iAttributes.hasNext())
        {
            LDAPAttribute laAttr = (LDAPAttribute) iAttributes.next();
            String[] saValues = laAttr.getStringValueArray();

            for (int iCount = 0; iCount < saValues.length; iCount++)
            {
                dtm.addRow(new Object[] { laAttr.getName(), saValues[iCount] });
            }
        }
    }

    /**
     * Exit the Application.
     */
    private void exitForm()
    {
        // First send a message to the specific EventService that the LDAP's should be refreshed.
        LDAPPublisher lpPublisher = new LDAPPublisher(lcConnection);

        try
        {
            String[] saDNs = new String[hmDNsToBeUpdated.size()];
            System.out.println("Publishing " + saDNs.length + " DNs");

            Iterator<String> iDNs = hmDNsToBeUpdated.keySet().iterator();

            for (int iCount = 0; (iCount < hmDNsToBeUpdated.size()) && iDNs.hasNext(); iCount++)
            {
                saDNs[iCount] = iDNs.next();
            }
            lpPublisher.publishChange(saDNs);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        System.exit(0);
    }

    /**
     * Gets all athenticated users from LDAP.
     */
    private void getAllAuthUsers()
    {
        // Clear the list in case we'return refreshing it.
        dndAuthusr.removeAllElements();

        try
        {
            // We need to find all authenticated users in LDAP. An authenticated user has an
            // objectclass busauthenticationuser.
            LDAPEntry[] results = LDAPUtils.searchLDAP(lcConnection, sSearchRoot,
                                                       LDAPConnection.SCOPE_SUB,
                                                       "(objectclass=busauthenticationuser)");

            for (int iCount = 0; iCount < results.length; iCount++)
            {
                dndAuthusr.addElement(new LDAPItemEntry(results[iCount]));
            }
        }
        catch (LDAPException e)
        {
            showMsg("Error getting all the authenticated users: " + e);
        }
    }

    /**
     * This function will get all roles form the LDAP.
     */
    private void getAllRoles()
    {
        try
        {
            LDAPEntry[] results = LDAPUtils.searchLDAP(lcConnection, sSearchRoot,
                                                       LDAPConnection.SCOPE_SUB,
                                                       "(&(objectclass=busorganizationalrole))");

            for (int iCount = 0; iCount < results.length; iCount++)
            {
                createRoleMenu(new LDAPItemEntry(results[iCount]));
                dndRolesList.addElement(new LDAPItemEntry(results[iCount]));
            }

            if (btnGrpRoles.getButtonCount() != 0)
            {
                Enumeration<?> e = btnGrpRoles.getElements();
                Object oObject = e.nextElement();

                if (oObject instanceof JRBtnMItemWithObject)
                {
                    ((JRBtnMItemWithObject) oObject).setSelected(true);
                }
            }
        }
        catch (LDAPException e)
        {
            showMsg("Error creating roles-menu: " + e);
        }
    }

    /**
     * This function gets the root Object from LDAP and adds it to the tree Then it will call get
     * organization units. Then it will add all users to the
     *
     * @return  The root object.
     */
    private LDAPItemEntry getRootObject()
    {
        LDAPItemEntry lLDAPItemEntry = null;

        try
        {
            LDAPEntry[] results = LDAPUtils.searchLDAP(lcConnection, sSearchRoot,
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
        LDAPLogin lLDapLogin = new LDAPLogin(new JFrame(), true, true);

        lLDapLogin.setVisible(true);

        if (lLDapLogin.isOk())
        {
            setLDAPDir(lLDapLogin.getLDAPConnection());
            setLDAPSearchRoot(lLDapLogin.getSearchRoot());
        }
        else
        {
            System.exit(0);
        }

        btnGrpRoles = new ButtonGroup();
        slpAuthMain = new JSplitPane();
        spUsers = new JScrollPane();
        dndAuthusr = new LocalDNDList();
        slpOrgRoles = new JSplitPane();
        spOrganization = new JScrollPane();
        dndTreeOganisations = null;
        spUsers1 = new JScrollPane();
        dndRolesList = new LocalDNDList();
        jMenuBar1 = new JMenuBar();
        jmFile = new JMenu();
        miNewAuthUser = new JMenuItem();
        jSeparator1 = new JSeparator();
        jmExit = new JMenuItem();
        jmRole = new JMenu();

        setTitle("Cordys User Admin");
        setFont(new Font("Tahoma", 0, 10));
        setIconImage(getIconImage());
        addComponentListener(new ComponentAdapter()
            {
                public void componentResized(ComponentEvent evt)
                {
                    changeSplitterLocation();
                }
            });

        addWindowListener(new WindowAdapter()
            {
                public void windowOpened(WindowEvent evt)
                {
                    getAllAuthUsers();
                    getAllRoles();
                    createDNDTree();
                }

                public void windowClosing(WindowEvent evt)
                {
                    exitForm();
                }
            });

        slpAuthMain.setDividerLocation(100);
        slpAuthMain.setDividerSize(4);
        slpAuthMain.setLastDividerLocation(125);
        slpAuthMain.setAutoscrolls(true);
        spUsers.setFont(new Font("Tahoma", 0, 10));
        spUsers.setAutoscrolls(true);
        dndAuthusr.setFont(new Font("Tahoma", 0, 10));
        dndAuthusr.addKeyListener(new KeyAdapter()
            {
                public void keyReleased(KeyEvent evt)
                {
                    deleteAuthenticatedUser(evt);
                }
            });

        spUsers.setViewportView(dndAuthusr);

        slpAuthMain.setLeftComponent(spUsers);

        slpOrgRoles.setBorder(null);
        slpOrgRoles.setDividerLocation(285);
        slpOrgRoles.setDividerSize(4);
        slpOrgRoles.setLastDividerLocation(125);
        slpOrgRoles.setMinimumSize(new Dimension(0, 0));
        slpOrgRoles.setPreferredSize(new Dimension(0, 0));
        slpOrgRoles.setAutoscrolls(true);
        spOrganization.setMaximumSize(new Dimension(0, 0));
        spOrganization.setMinimumSize(new Dimension(300, 0));
        spOrganization.setPreferredSize(new Dimension(300, 0));
        spOrganization.setViewportView(dndTreeOganisations);

        slpOrgRoles.setLeftComponent(spOrganization);

        spUsers1.setFont(new Font("Tahoma", 0, 10));
        spUsers1.setMaximumSize(new Dimension(100, 0));
        spUsers1.setMinimumSize(new Dimension(100, 0));
        spUsers1.setPreferredSize(new Dimension(100, 0));
        spUsers1.setAutoscrolls(true);
        dndRolesList.setFont(new Font("Tahoma", 0, 10));
        spUsers1.setViewportView(dndRolesList);

        slpOrgRoles.setRightComponent(spUsers1);

        slpAuthMain.setRightComponent(slpOrgRoles);

        // Create the splitpanel for the main and properties.
        slpMainProps = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        slpMainProps.setDividerLocation(400);
        slpMainProps.setDividerSize(4);
        slpMainProps.setAutoscrolls(true);

        tblProperties = new JTable();

        JScrollPane spTemp = new JScrollPane(tblProperties);
        slpMainProps.setRightComponent(spTemp);
        slpMainProps.setLeftComponent(slpAuthMain);
        tblProperties.setFont(new Font("Tahoma", 0, 10));

        DefaultTableModel dtm = (DefaultTableModel) tblProperties.getModel();
        dtm.addColumn("Name");
        dtm.addColumn("Value");
        dtm.setRowCount(1);

        getContentPane().add(slpMainProps, BorderLayout.CENTER);

        jMenuBar1.setBorder(null);
        jMenuBar1.setFont(new Font("Tahoma", 0, 10));
        jmFile.setBorder(null);
        jmFile.setText("File");
        jmFile.setFont(new Font("Tahoma", 0, 10));
        miNewAuthUser.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.ALT_MASK));
        miNewAuthUser.setFont(new Font("Tahoma", 0, 10));
        miNewAuthUser.setMnemonic('n');
        miNewAuthUser.setText("New Authenticated user");
        miNewAuthUser.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent evt)
                {
                    showAddNewAuthUser();
                }
            });

        jmFile.add(miNewAuthUser);
        jmFile.add(jSeparator1);
        jmExit.setFont(new Font("Tahoma", 0, 10));
        jmExit.setText("Exit");
        jmExit.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent evt)
                {
                    jmExitActionPerformed();
                }
            });

        jmFile.add(jmExit);
        jMenuBar1.add(jmFile);
        jmRole.setBorder(null);
        jmRole.setText("Default Role");
        jmRole.setFont(new Font("Tahoma", 0, 10));
        jMenuBar1.add(jmRole);
        setJMenuBar(jMenuBar1);

        pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(new Dimension(800, 600));
        setLocation((screenSize.width - 800) / 2, (screenSize.height - 600) / 2);
    }

    /**
     * DOCUMENTME.
     */
    private void jmExitActionPerformed()
    {
        exitForm();
    }

    /**
     * This method shows the dialog in which the user can enter new authenticated users.
     */
    private void showAddNewAuthUser()
    {
        // Show the screen
        NewAuthUser newAuthUser = new NewAuthUser(this, true, lcConnection, sSearchRoot,
                                                  hmDNsToBeUpdated);
        newAuthUser.setVisible(true);

        // Refresh the listbox.
        getAllAuthUsers();
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
     * class used to do specific things for this app.
     */
    private class DNDTree extends DnDjTree
    {
        /**
         * Calls the super with the given string.
         *
         * @param  oObject  the root Object
         * @param  LDAPCon  The Connection to the LDAP
         */
        public DNDTree(Object oObject, LDAPConnection LDAPCon)
        {
            super(oObject, LDAPCon, hmDNsToBeUpdated);
        }

        /**
         * Handles selected.
         *
         * @param  tse  DOCUMENTME
         */
        @Override public void valueChanged(TreeSelectionEvent tse)
        {
            CordysTreeNode ctn = (CordysTreeNode) tse.getPath().getLastPathComponent();
            UserAdmin.this.displayEntry(ctn.getLDAPEntry());
        }

        /**
         * Function whitch can be used to get the current selcted role.
         *
         * @return  the current selected role in the menu
         */
        @Override protected LDAPEntry[] getSelectedRoles()
        {
            return getCurrentRoles();
        }
    }

    /**
     * Local class for handling the DnDLists.
     */
    private class LocalDNDList extends DnDjList
    {
        /**
         * Default constructor.
         */
        public LocalDNDList()
        {
            super();
        }

        /**
         * DOCUMENTME.
         *
         * @param  lse  DOCUMENTME
         */
        @Override public void valueChanged(ListSelectionEvent lse)
        {
            UserAdmin.this.displayEntry(((LDAPItemEntry) ((LocalDNDList) lse.getSource())
                                         .getSelectedValue()).getEntry());
        }
    }
}
