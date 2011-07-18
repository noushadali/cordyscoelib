package com.cordys.coe.tools.useradmin.swt;

import com.cordys.coe.tools.useradmin.LDAPItemEntry;
import com.cordys.coe.util.config.IConfiguration;
import com.cordys.coe.util.connection.CordysConnectionException;
import com.cordys.coe.util.connection.ICordysConnection;
import com.cordys.coe.util.general.ldap.LDAPUtils;
import com.cordys.coe.util.swt.BorderLayout;
import com.cordys.coe.util.swt.MessageBoxUtil;
import com.cordys.coe.util.swt.SWTResourceManager;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPModification;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TreeAdapter;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * This composite holds the UserAdmin application.
 *
 * @author  pgussow
 */
public class UserAdminComposite extends Composite
{
    /**
     * Holds the logger to use.
     */
    private static final Logger LOG = Logger.getLogger(UserAdminComposite.class);
    /**
     * DOCUMENTME.
     */
    private static final String IMG_AUTHENTICATED_USER = "authenticateduser";
    /**
     * Holds the image registry.
     */
    private static ImageRegistry s_irImages;
    /**
     * Holds the root item of the tree.
     */
    private AbstractCordysTreeNode actnRootItem;
    /**
     * Holds all the authenticated users.
     */
    private ListWithData lAuthenticatedUsers;
    /**
     * Holds the full DN of the selected entry.
     */
    private Text lblDetails;
    /**
     * Holds all the roles.
     */
    private ListWithData lRoles;
    /**
     * Holds the Cordys Connection to use for talking to Cordys.
     */
    private ICordysConnection m_ccConnection;
    /**
     * Holds the configuration for the connection.
     */
    private IConfiguration m_cConfig;
    /**
     * Holds the details of the object.
     */
    private Table tblDetails;
    /**
     * Holds the root item for the menu tree.
     */
    private TreeItem tiMenuRoot = null;
    /**
     * Holds all the menus.
     */
    private Tree tMenus;
    /**
     * Holds all the organizations.
     */
    private Tree tOrganizations;

    /**
     * Creates a new UserAdminComposite object.
     *
     * @param  cParent       The parent composite.
     * @param  iStyle        The style for this composite.
     * @param  ccConnection  DOCUMENTME
     */
    public UserAdminComposite(Composite cParent, int iStyle, ICordysConnection ccConnection)
    {
        super(cParent, iStyle);
        setLayout(new FillLayout());

        if (ccConnection != null)
        {
            m_ccConnection = ccConnection;
            m_cConfig = ccConnection.getConfiguration();
            initializeForConnection();
        }

        createContent();
    }

    /**
     * Gets all athenticated users from LDAP.
     */
    public void getAllAuthUsers()
    {
        if (lAuthenticatedUsers != null)
        {
            // Clear the list in case we'return refreshing it.
            lAuthenticatedUsers.removeAll();

            try
            {
                // We need to find all authenticated users in LDAP. An authenticated user has an
                // objectclass busauthenticationuser.
                LDAPEntry[] results = m_ccConnection.searchLDAP(m_ccConnection.getSearchRoot(),
                                                                LDAPConnection.SCOPE_SUB,
                                                                "(objectclass=busauthenticationuser)");

                for (int iCount = 0; iCount < results.length; iCount++)
                {
                    addAuthUser(new LDAPItemEntry(results[iCount]));
                }
            }
            catch (CordysConnectionException e)
            {
                MessageBoxUtil.showError(getShell(), "Error getting all the authenticated users.",
                                         e);
            }
        }
    }

    /**
     * This method gets the configuration of the connection.
     *
     * @return  The configuration of the connection.
     */
    public IConfiguration getConfiguration()
    {
        return m_cConfig;
    }

    /**
     * This method returns the cordys connection that is being used.
     *
     * @return  The cordys connection.
     */
    public ICordysConnection getCordysConnection()
    {
        return m_ccConnection;
    }

    /**
     * This method gets the image registry for this client.
     *
     * @return  The image registry for this client.
     */
    public ImageRegistry getOwnImageRegistry()
    {
        if (s_irImages == null)
        {
            registerImages();
        }
        return s_irImages;
    }

    /**
     * This method refreshes all the children of the passed on tree node.
     *
     * @param  ltnParent  the tree node to refresh.
     */
    public void refreshChildren(LDAPTreeNode ltnParent)
    {
        ltnParent.removeAll();
        ltnParent.setLoadedChildren(false);

        try
        {
            ltnParent.loadChildren();
        }
        catch (Exception e)
        {
            LOG.error("Error refreshing the tree node " + ltnParent.getEntry() + "\n", e);
            MessageBoxUtil.showError(getShell(),
                                     "Error refreshing the tree node " + ltnParent.getEntry(), e);
        }
    }

    /**
     * This method sets the Cordys connection to use.
     *
     * @param  ccConnection  The cordys connection.
     */
    public void setCordysConnection(ICordysConnection ccConnection)
    {
        if (ccConnection != null)
        {
            m_ccConnection = ccConnection;
            m_cConfig = ccConnection.getConfiguration();

            initializeForConnection();
        }
    }

    /**
     * This method shows the new authenticated user screen.
     */
    public void showNewAuthScreen()
    {
        NewAuthenticatedUser nauScreen = new NewAuthenticatedUser(getShell(),
                                                                  getCordysConnection());
        nauScreen.open();

        // Refresh the authenticated user's list
        getAllAuthUsers();
    }

    /**
     * This method deletes the authenticated user.
     */
    protected void deleteAuthenticatedUser()
    {
        int iSelected = lAuthenticatedUsers.getSelectionIndex();
        LDAPItemEntry lieEntry = lAuthenticatedUsers.getSelectedObject(iSelected);

        if (
            askConfirmation("Are you sure you want to delete the authenticated user\n" +
                                lieEntry.getEntry().getDN()))
        {
            // delete it.
            try
            {
                m_ccConnection.deleteLDAPEntry(lieEntry.getEntry(), true);
                lAuthenticatedUsers.remove(iSelected);
            }
            catch (CordysConnectionException e)
            {
                MessageBoxUtil.showError(getShell(),
                                         "Error deleting authenticated user." + e.getMessage(), e);
                LOG.error("Error deleting authenticated user.", e);
            }
        }
    }

    /**
     * This method deletes the selected item from the tree. It can delete menus, roles and
     * organizational users.
     *
     * @throws  Exception  DOCUMENTME
     */
    protected void deleteItemFromOrgTree()
                                  throws Exception
    {
        TreeItem[] atiSelected = tOrganizations.getSelection();

        for (int iCount = 0; iCount < atiSelected.length; iCount++)
        {
            TreeItem tiTemp = atiSelected[iCount];

            if (tiTemp instanceof LDAPTreeNode)
            {
                // It's an LDAP item
                LDAPTreeNode ltnToBeDeleted = (LDAPTreeNode) tiTemp;

                LDAPTreeNode ltnParentItem = (LDAPTreeNode) ltnToBeDeleted.getParentItem();

                if (ltnToBeDeleted.getType() == AbstractCordysTreeNode.TYPE_ORGANIZATIONAL_USER)
                {
                    m_ccConnection.deleteLDAPEntry(ltnToBeDeleted.getLDAPItemEntry().getEntry(),
                                                   true);
                }
                else if (ltnToBeDeleted.getType() == AbstractCordysTreeNode.TYPE_USER_ROLE)
                {
                    LDAPItemEntry lieToBeDeleted = ltnToBeDeleted.getLDAPItemEntry();

                    LDAPEntry leCurrentOrgUser = m_ccConnection.readLDAPEntry(ltnParentItem
                                                                              .getLDAPItemEntry()
                                                                              .getEntry().getDN());
                    LDAPAttribute laRole = leCurrentOrgUser.getAttribute("role");
                    laRole.removeValue(lieToBeDeleted.getEntry().getDN());

                    m_ccConnection.changeLDAPAttibute(leCurrentOrgUser, laRole,
                                                      LDAPModification.REPLACE);
                }
                else
                {
                    MessageBoxUtil.showError(getShell(), "Cannot delete this type.");
                }
                ltnParentItem.removeAll();
                ltnParentItem.setLoadedChildren(false);
                ltnParentItem.loadChildren();
            }
            else if (tiTemp instanceof XMLStoreTreeNode)
            {
                // Menu or toolbar.
                XMLStoreTreeNode xstnTobeDeleted = (XMLStoreTreeNode) tiTemp;
                XMLStoreItem xsiTemp = xstnTobeDeleted.getXMLStoreItem();
                LDAPTreeNode ltnParentItem = (LDAPTreeNode) xstnTobeDeleted.getParentItem();

                String sAttribute = "menu";

                if (xsiTemp.getType() == XMLStoreItem.TYPE_TOOLBAR)
                {
                    sAttribute = "toolbar";
                }

                String sTemp = ltnParentItem.getLDAPItemEntry().getEntry().getDN();
                LDAPEntry leCurrentOrgUser = m_ccConnection.readLDAPEntry(sTemp);
                LDAPAttribute laAttribute = leCurrentOrgUser.getAttribute(sAttribute);
                laAttribute.removeValue(xsiTemp.getKey());

                m_ccConnection.changeLDAPAttibute(leCurrentOrgUser, laAttribute,
                                                  LDAPModification.REPLACE);

                ltnParentItem.removeAll();
                ltnParentItem.setLoadedChildren(false);
                ltnParentItem.loadChildren();
            }
        }
    }

    /**
     * This method retrieves all the menus that are in Cordys. It retrieves all the menus on ISV and
     * on organizational level.
     */
    protected void getAllMenus()
    {
        if (m_ccConnection != null)
        {
            TreeItem[] atiOrgs = actnRootItem.getItems();

            tMenus.removeAll();

            // First we get the ISV level menus
            tiMenuRoot = new TreeItem(tMenus, SWT.NONE);
            tiMenuRoot.setText("Menus");
            tiMenuRoot.setImage(SWTResourceManager.getImage(UserAdmin.class, "menu.gif"));

            // Now get all the ISV levels.
            XMLStoreItem xsiISV = new XMLStoreItem("/Cordys/WCP/Menu");
            xsiISV.setLevel(XMLStoreItem.LEVEL_ISV);
            xsiISV.setDisplay("ISV");
            xsiISV.setFolder(true);

            TreeItem tiISV = new XMLStoreTreeNode(tiMenuRoot, SWT.NONE, xsiISV, m_ccConnection);
            tiISV.setText("ISV Level");
            tiISV.setImage(SWTResourceManager.getImage(UserAdmin.class, "isvobject.gif"));

            // Now we need it for each organization.
            TreeItem tiOrganizations = new TreeItem(tiMenuRoot, SWT.NONE);
            tiOrganizations.setText("Organizations");
            tiOrganizations.setImage(SWTResourceManager.getImage(UserAdmin.class,
                                                                 "organization.gif"));

            for (int iCount = 0; iCount < atiOrgs.length; iCount++)
            {
                TreeItem tiOrg = atiOrgs[iCount];

                if (tiOrg instanceof LDAPTreeNode)
                {
                    LDAPTreeNode ltnOrg = (LDAPTreeNode) tiOrg;
                    String sDN = ltnOrg.getLDAPItemEntry().getEntry().getDN();
                    String sDisplay = ltnOrg.getLDAPItemEntry().toString();

                    xsiISV = new XMLStoreItem("/Cordys/WCP/Menu");
                    xsiISV.setLevel(XMLStoreItem.LEVEL_ORGANIZATION);
                    xsiISV.setDisplay(sDisplay);
                    xsiISV.setOrganization(sDN);
                    xsiISV.setFolder(true);

                    XMLStoreTreeNode xstnNew = new XMLStoreTreeNode(tiOrganizations, SWT.NONE,
                                                                    xsiISV, m_ccConnection);
                    xstnNew.setText(sDisplay);
                    xstnNew.setImage(SWTResourceManager.getImage(UserAdmin.class, "orgobject.gif"));
                }
            }
        }
    }

    /**
     * This method expands the current tree node. It makes sure that for the nodes that will be
     * shown the subnodes are loaded.
     *
     * @param  teEvent  The event that occurred.
     */
    protected void onExpandNode(TreeEvent teEvent)
    {
        TreeItem tiNode = (TreeItem) teEvent.item;
        TreeItem[] atiChildren = tiNode.getItems();

        for (int iCount = 0; iCount < atiChildren.length; iCount++)
        {
            TreeItem tiItem = atiChildren[iCount];

            if (tiItem instanceof AbstractCordysTreeNode)
            {
                AbstractCordysTreeNode ctnChild = (AbstractCordysTreeNode) tiItem;

                try
                {
                    ctnChild.loadChildren();
                }
                catch (Exception e)
                {
                    MessageBoxUtil.showError(getShell(),
                                             "Error loading children for " + ctnChild.getText(), e);
                    LOG.error("Error loading children for " + ctnChild.getText() + ":\n", e);
                }
            }
        }
    }

    /**
     * This method shows the details of the authenticated user in the details pane.
     */
    protected void showAuthUserDetails()
    {
        int[] aiIndices = lAuthenticatedUsers.getSelectionIndices();

        if (aiIndices.length > 0)
        {
            // We'll only see the first one.
            LDAPItemEntry lieAuthUser = lAuthenticatedUsers.getSelectedObject(aiIndices[0]);
            showLDAPDetails(lieAuthUser);
        }
    }

    /**
     * This method shows the details for the item clicked in the tree.
     *
     * @param  ctnNode
     */
    protected void showDetailsFromTree(AbstractCordysTreeNode ctnNode)
    {
        if (ctnNode instanceof LDAPTreeNode)
        {
            showLDAPDetails(((LDAPTreeNode) ctnNode).getLDAPItemEntry());
        }
        else if (ctnNode instanceof XMLStoreTreeNode)
        {
            showXMLStoreDetails(((XMLStoreTreeNode) ctnNode).getXMLStoreItem());
        }
    }

    /**
     * This method displays the details of the role in the details-pane.
     */
    protected void showRoleDetails()
    {
        int[] aiIndices = lRoles.getSelectionIndices();

        if (aiIndices.length > 0)
        {
            // We'll only see the first one.
            LDAPItemEntry lieRole = lRoles.getSelectedObject(aiIndices[0]);
            showLDAPDetails(lieRole);
        }
    }

    /**
     * This method adds the user to the list.
     *
     * @param  lieAuthUser  The user to add.
     */
    private void addAuthUser(LDAPItemEntry lieAuthUser)
    {
        lAuthenticatedUsers.add(lieAuthUser);
    }

    /**
     * This method adds the specified entry to the Roles-list.
     *
     * @param  lieEntry  The entry to add.
     */
    private void addRole(LDAPItemEntry lieEntry)
    {
        lRoles.add(lieEntry);
    }

    /**
     * This method shows a confirmation dialog.
     *
     * @param   sMessage  The message to display.
     *
     * @return  True if the user pressed yes. Otherwise false.
     */
    private boolean askConfirmation(String sMessage)
    {
        MessageBox mb = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
        mb.setMessage(sMessage);
        return (mb.open() == SWT.YES);
    }

    /**
     * This method creates the full screen.
     */
    private void createContent()
    {
        Composite cMain = new Composite(this, SWT.NONE);
        cMain.setLayout(new BorderLayout());

        // Create the toolbar
        final CoolBar cbCoolBar = new CoolBar(cMain, SWT.NONE);
        cbCoolBar.setLayoutData(BorderLayout.NORTH);

        final ToolBar tbToolbar = new ToolBar(cbCoolBar, SWT.FLAT);

        // The new Authenticated user button.
        ToolItem tiItem = new ToolItem(tbToolbar, SWT.NULL);
        tiItem.setImage(getOwnImageRegistry().get(IMG_AUTHENTICATED_USER));
        tiItem.setToolTipText("Create a new authenticated user");

        tiItem.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent arg0)
                {
                    showNewAuthScreen();
                }
            });

        // Add a coolItem to a coolBar
        CoolItem ciCoolItem = new CoolItem(cbCoolBar, SWT.NULL);
        ciCoolItem.setControl(tbToolbar);

        Point size = tbToolbar.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        Point pSize = ciCoolItem.computeSize(size.x, size.y);
        ciCoolItem.setSize(pSize);

        final SashForm sfTopBottom = new SashForm(cMain, SWT.VERTICAL);
        sfTopBottom.setLayoutData(BorderLayout.CENTER);

        final Composite composite_2 = new Composite(sfTopBottom, SWT.NONE);
        composite_2.setLayout(new FillLayout());

        final SashForm sfMain = new SashForm(composite_2, SWT.NONE);

        final Composite cAuthenticatedUsers = new Composite(sfMain, SWT.BORDER);
        cAuthenticatedUsers.setLayout(new FillLayout());

        lAuthenticatedUsers = new ListWithData(cAuthenticatedUsers,
                                               SWT.V_SCROLL | SWT.MULTI | SWT.BORDER |
                                               SWT.H_SCROLL);

        final Composite cUsers = new Composite(sfMain, SWT.BORDER);
        cUsers.setLayout(new FillLayout());

        tOrganizations = new Tree(cUsers, SWT.BORDER | SWT.MULTI);

        final SashForm sfRolesMenus = new SashForm(sfMain, SWT.VERTICAL);

        final Composite cRoles = new Composite(sfRolesMenus, SWT.BORDER);
        cRoles.setLayout(new FillLayout());

        lRoles = new ListWithData(cRoles, SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.H_SCROLL);

        final Composite cMenus = new Composite(sfRolesMenus, SWT.BORDER);
        cMenus.setLayout(new BorderLayout(0, 0));

        tMenus = new Tree(cMenus, SWT.BORDER | SWT.MULTI);

        final Button btnRetrieveMenus = new Button(cMenus, SWT.NONE);

        btnRetrieveMenus.setText("Retrieve menus");
        btnRetrieveMenus.setLayoutData(BorderLayout.NORTH);
        sfRolesMenus.setWeights(new int[] { 1, 1 });
        sfMain.setWeights(new int[] { 1, 3, 1 });

        final Composite cDetails = new Composite(sfTopBottom, SWT.BORDER);
        cDetails.setLayout(new BorderLayout(0, 0));

        lblDetails = new Text(cDetails, SWT.BORDER);
        lblDetails.setLayoutData(BorderLayout.SOUTH);
        lblDetails.setText("");
        lblDetails.setEditable(false);

        tblDetails = new Table(cDetails, SWT.BORDER);
        tblDetails.setLinesVisible(true);
        tblDetails.setHeaderVisible(true);

        final TableColumn tcName = new TableColumn(tblDetails, SWT.NONE);
        tcName.setWidth(200);
        tcName.setText("Name");

        final TableColumn tcValue = new TableColumn(tblDetails, SWT.NONE);
        tcValue.setWidth(350);
        tcValue.setText("Value");

        sfTopBottom.setWeights(new int[] { 2, 1 });

        // Add the listeners to the needed controls
        lAuthenticatedUsers.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e)
                {
                    showAuthUserDetails();
                }
            });
        btnRetrieveMenus.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e)
                {
                    getAllMenus();
                }
            });
        tOrganizations.addTreeListener(new TreeAdapter()
            {
                public void treeExpanded(TreeEvent e)
                {
                    onExpandNode(e);
                }
            });
        lRoles.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e)
                {
                    showRoleDetails();
                }
            });
        tOrganizations.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e)
                {
                    showDetailsFromTree((AbstractCordysTreeNode) e.item);
                }
            });
        tMenus.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e)
                {
                    if (e.item instanceof AbstractCordysTreeNode)
                    {
                        showDetailsFromTree((AbstractCordysTreeNode) e.item);
                    }
                }
            });
        tMenus.addTreeListener(new TreeAdapter()
            {
                public void treeExpanded(TreeEvent e)
                {
                    onExpandNode(e);
                }
            });
        tOrganizations.addKeyListener(new KeyAdapter()
            {
                public void keyPressed(KeyEvent keEvent)
                {
                    if (keEvent.keyCode == SWT.DEL)
                    {
                        try
                        {
                            deleteItemFromOrgTree();
                        }
                        catch (Exception e)
                        {
                            MessageBoxUtil.showError(getShell(), "Error deleting item.", e);
                        }
                    }
                }
            });
        lAuthenticatedUsers.addKeyListener(new KeyAdapter()
            {
                public void keyPressed(KeyEvent keEvent)
                {
                    if (keEvent.keyCode == SWT.DEL)
                    {
                        try
                        {
                            deleteAuthenticatedUser();
                        }
                        catch (Exception e)
                        {
                            MessageBoxUtil.showError(getShell(), "Error deleting item.", e);
                        }
                    }
                }
            });

        // Set up the drag and drop.
        // Roles.
        DragSource dsDragSource = new DragSource(lRoles, DND.DROP_MOVE | DND.DROP_COPY);
        dsDragSource.setTransfer(new Transfer[] { VectorTransfer.getInstance() });
        dsDragSource.addDragListener(new ListDragSourceListener(lRoles,
                                                                LDAPTransferObject.TYPE_ROLE));

        // Authenticated users
        dsDragSource = new DragSource(lAuthenticatedUsers, DND.DROP_MOVE | DND.DROP_COPY);
        dsDragSource.setTransfer(new Transfer[] { VectorTransfer.getInstance() });
        dsDragSource.addDragListener(new ListDragSourceListener(lAuthenticatedUsers,
                                                                LDAPTransferObject.TYPE_AUTHENTICATED_USER));

        // Menus
        dsDragSource = new DragSource(tMenus, DND.DROP_MOVE | DND.DROP_COPY);
        dsDragSource.setTransfer(new Transfer[] { VectorTransfer.getInstance() });
        dsDragSource.addDragListener(new TreeDragSourceListener(tMenus));

        // The organization drop target.
        DropTarget dtDropTarget = new DropTarget(tOrganizations,
                                                 DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_DEFAULT);
        dtDropTarget.setTransfer(new Transfer[] { VectorTransfer.getInstance() });
        dtDropTarget.addDropListener(new TreeDropTargetListener());
    }

    /**
     * This function will get all roles form the LDAP.
     */
    private void getAllRoles()
    {
        // Clear the list in case we'return refreshing it.
        lRoles.removeAll();

        try
        {
            LDAPEntry[] results = m_ccConnection.searchLDAP(m_ccConnection.getSearchRoot(),
                                                            LDAPConnection.SCOPE_SUB,
                                                            "(&(objectclass=busorganizationalrole))");

            for (int iCount = 0; iCount < results.length; iCount++)
            {
                addRole(new LDAPItemEntry(results[iCount]));
            }
        }
        catch (CordysConnectionException e)
        {
            MessageBoxUtil.showError(getShell(), "Error creating roles-menu.", e);
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
            LDAPEntry[] results = m_ccConnection.searchLDAP(m_ccConnection.getSearchRoot(),
                                                            LDAPConnection.SCOPE_SUB,
                                                            "objectclass=groupOfNames");

            if (results.length != 0)
            {
                lLDAPItemEntry = new LDAPItemEntry(results[0]);
            }
        }
        catch (CordysConnectionException e)
        {
            MessageBoxUtil.showError(getShell(), "Error getting the organization schema.", e);
        }
        return lLDAPItemEntry;
    }

    /**
     * This method initializes the views for the current connection.
     */
    private void initializeForConnection()
    {
        // Initialize the data
        getAllAuthUsers();
        getAllRoles();
        initializeTree();
    }

    /**
     * This method initializes the tree.
     */
    private void initializeTree()
    {
        LDAPItemEntry lieEntry = getRootObject();

        actnRootItem = new LDAPTreeNode(tOrganizations, SWT.NONE, lieEntry, m_ccConnection);

        try
        {
            actnRootItem.loadChildren();
        }
        catch (Exception e)
        {
            MessageBoxUtil.showError(getShell(),
                                     "Error loading children for " + actnRootItem.getText(), e);
        }
    }

    /**
     * This method registers all images that could be needed by this application.
     */
    private void registerImages()
    {
        s_irImages = new ImageRegistry();

        String iconPath = "";
        s_irImages.put(IMG_AUTHENTICATED_USER,
                       ImageDescriptor.createFromFile(UserAdmin.class,
                                                      iconPath + IMG_AUTHENTICATED_USER + ".gif"));
    }

    /**
     * This method shows the details of the LDAP entry in the details screen.
     *
     * @param  lieEntry  The entry to display.
     */
    private void showLDAPDetails(LDAPItemEntry lieEntry)
    {
        if (lieEntry != null)
        {
            tblDetails.removeAll();

            LDAPEntry leEntry = lieEntry.getEntry();
            LDAPAttributeSet lasAttributes = leEntry.getAttributeSet();

            for (Iterator<?> iAttributes = lasAttributes.iterator(); iAttributes.hasNext();)
            {
                LDAPAttribute laAttr = (LDAPAttribute) iAttributes.next();
                String sName = laAttr.getName();
                String[] saValues = laAttr.getStringValueArray();

                for (int iCount = 0; iCount < saValues.length; iCount++)
                {
                    TableItem tiNew = new TableItem(tblDetails, SWT.NONE);
                    tiNew.setText(new String[] { sName, saValues[iCount] });
                    tiNew.setData(laAttr);
                }
            }

            lblDetails.setText(leEntry.getDN());
        }
    }

    /**
     * This method shows the details for the XML store item.
     *
     * @param  csStoreItem  The XML store item to display.
     */
    private void showXMLStoreDetails(XMLStoreItem csStoreItem)
    {
        tblDetails.removeAll();

        TableItem tiNew = new TableItem(tblDetails, SWT.NONE);
        tiNew.setText(new String[] { "key", csStoreItem.getKey() });
        tiNew.setData(csStoreItem);

        tiNew = new TableItem(tblDetails, SWT.NONE);
        tiNew.setText(new String[] { "display", csStoreItem.getDisplay() });
        tiNew.setData(csStoreItem);

        tiNew = new TableItem(tblDetails, SWT.NONE);
        tiNew.setText(new String[] { "organization", csStoreItem.getOrganization() });
        tiNew.setData(csStoreItem);

        tiNew = new TableItem(tblDetails, SWT.NONE);

        String sTemp = "";
        int iLevel = csStoreItem.getLevel();

        if (iLevel == XMLStoreItem.LEVEL_ISV)
        {
            sTemp = "isv";
        }
        else if (iLevel == XMLStoreItem.LEVEL_ORGANIZATION)
        {
            sTemp = "organization";
        }
        else if (iLevel == XMLStoreItem.LEVEL_USER)
        {
            sTemp = "user";
        }
        tiNew.setText(new String[] { "level", sTemp });
        tiNew.setData(csStoreItem);
    }

    /**
     * This class handles the dragsource from the list menu.
     *
     * @author  pgussow
     */
    private final class ListDragSourceListener
        implements DragSourceListener
    {
        /**
         * Holds the type of the object (LDAPTransferObject).
         */
        private int iType;
        /**
         * The source list.
         */
        private ListWithData lList = null;

        /**
         * Creates a new ListDragSourceListener object.
         *
         * @param  lList  The source list.
         * @param  iType  DOCUMENTME
         */
        public ListDragSourceListener(ListWithData lList, int iType)
        {
            this.lList = lList;
            this.iType = iType;
        }

        /**
         * This method is fired when the drag has finished.
         *
         * @param  dseEvent  The drag event.
         */
        public void dragFinished(DragSourceEvent dseEvent)
        {
            // If a move operation has been performed, remove the data
            // from the source
            if (dseEvent.detail == DND.DROP_MOVE)
            {
                // Do nothing. Move is the same as copy.
            }
        }

        /**
         * This method sets the data for the transfer.
         *
         * @param  dseEvent  The drag event.
         */
        public void dragSetData(DragSourceEvent dseEvent)
        {
            // Provide the data of the requested type.
            if (VectorTransfer.getInstance().isSupportedType(dseEvent.dataType))
            {
                Vector<Object> vData = new Vector<Object>();

                int[] aiIndices = lList.getSelectionIndices();

                for (int iCount = 0; iCount < aiIndices.length; iCount++)
                {
                    // Since LDAPEntry is not serializeable, we'll pass on strings with the DN.
                    Object oTemp = lList.getSelectedObject(aiIndices[iCount]);

                    if (oTemp != null)
                    {
                        Object oData = null;

                        if (oTemp instanceof LDAPItemEntry)
                        {
                            LDAPItemEntry lieEntry = (LDAPItemEntry) oTemp;
                            oData = new LDAPTransferObject(lieEntry.getEntry().getDN(), iType);
                        }
                        else if (oTemp instanceof XMLStoreItem)
                        {
                            oData = oTemp;
                        }
                        else
                        {
                            System.out.println("Must be a lDAPItemEntry or XMLStoreItem");
                        }
                        vData.add(oData);
                    }
                }
                dseEvent.data = vData;
            }
        }

        /**
         * This method determines wether a drag is even possible.
         *
         * @param  dseEvent  The event.
         */
        public void dragStart(DragSourceEvent dseEvent)
        {
            int[] aiIndices = lList.getSelectionIndices();

            if (aiIndices.length == 0)
            {
                dseEvent.doit = false;
            }
        }
    }

    /**
     * This class handles the dragsource from the list menu.
     *
     * @author  pgussow
     */
    private final class TreeDragSourceListener
        implements DragSourceListener
    {
        /**
         * The source list.
         */
        private Tree tTree = null;

        /**
         * Creates a new TreeDragSourceListener object.
         *
         * @param  lList  The source list.
         */
        public TreeDragSourceListener(Tree lList)
        {
            this.tTree = lList;
        }

        /**
         * This method is fired when the drag has finished.
         *
         * @param  dseEvent  The drag event.
         */
        public void dragFinished(DragSourceEvent dseEvent)
        {
            // If a move operation has been performed, remove the data
            // from the source
            if (dseEvent.detail == DND.DROP_MOVE)
            {
                // Do nothing. Move is the same as copy.
            }
        }

        /**
         * This method sets the data for the transfer.
         *
         * @param  dseEvent  The drag event.
         */
        public void dragSetData(DragSourceEvent dseEvent)
        {
            // Provide the data of the requested type.
            if (VectorTransfer.getInstance().isSupportedType(dseEvent.dataType))
            {
                Vector<Object> vData = new Vector<Object>();

                TreeItem[] aiTreeItems = tTree.getSelection();

                for (int iCount = 0; iCount < aiTreeItems.length; iCount++)
                {
                    // Since LDAPEntry is not serializeable, we'll pass on strings with the DN.
                    Object oTemp = aiTreeItems[iCount];

                    if (oTemp instanceof AbstractCordysTreeNode)
                    {
                        oTemp = ((AbstractCordysTreeNode) oTemp).getEntry();

                        if (oTemp != null)
                        {
                            Object oData = null;

                            if (oTemp instanceof XMLStoreItem)
                            {
                                XMLStoreItem xsiItem = (XMLStoreItem) oTemp;

                                if (!xsiItem.isFolder())
                                {
                                    oData = oTemp;
                                }
                            }
                            else
                            {
                                System.out.println("Must be a lDAPItemEntry or XMLStoreItem");
                            }

                            if (oData != null)
                            {
                                vData.add(oData);
                            }
                        }
                    }
                }
                dseEvent.data = vData;
            }
        }

        /**
         * This method determines wether a drag is even possible.
         *
         * @param  dseEvent  The event.
         */
        public void dragStart(DragSourceEvent dseEvent)
        {
            TreeItem[] aiIndices = tTree.getSelection();

            if (aiIndices.length == 0)
            {
                dseEvent.doit = false;
            }
        }
    }

    /**
     * This class handles the drop on the tree control.
     *
     * @author  pgussoe
     */
    private final class TreeDropTargetListener
        implements DropTargetListener
    {
        /**
         * This method is fired when the drag enters the tree.
         *
         * @param  dteEvent  The event.
         */
        public void dragEnter(DropTargetEvent dteEvent)
        {
            if (dteEvent.detail == DND.DROP_DEFAULT)
            {
                if ((dteEvent.operations & DND.DROP_COPY) != 0)
                {
                    dteEvent.detail = DND.DROP_COPY;
                }
                else
                {
                    dteEvent.detail = DND.DROP_NONE;
                }
            }

            // will accept text but prefer to have files dropped
            for (int iCount = 0; iCount < dteEvent.dataTypes.length; iCount++)
            {
                if (VectorTransfer.getInstance().isSupportedType(dteEvent.dataTypes[iCount]))
                {
                    dteEvent.currentDataType = dteEvent.dataTypes[iCount];

                    // files should only be copied
                    if (dteEvent.detail != DND.DROP_COPY)
                    {
                        dteEvent.detail = DND.DROP_NONE;
                    }
                    break;
                }
            }
        }

        /**
         * This methos is fired when the drag leaves the control.
         *
         * @param  dteEvent  The event.
         */
        public void dragLeave(DropTargetEvent dteEvent)
        {
        }

        /**
         * This method is fired when the drag operation changed.
         *
         * @param  dteEvent  The event.
         */
        public void dragOperationChanged(DropTargetEvent dteEvent)
        {
            if (dteEvent.detail == DND.DROP_DEFAULT)
            {
                dteEvent.detail = (dteEvent.operations & DND.DROP_COPY);

                if (dteEvent.detail != 0)
                {
                    dteEvent.detail = DND.DROP_COPY;
                }
                else
                {
                    dteEvent.detail = DND.DROP_NONE;
                }
            }

            if (VectorTransfer.getInstance().isSupportedType(dteEvent.currentDataType))
            {
                if (dteEvent.detail != DND.DROP_COPY)
                {
                    dteEvent.detail = DND.DROP_NONE;
                }
            }
        }

        /**
         * This method is fired when the drag goes over the control.
         *
         * @param  dteEvent  The event.
         */
        public void dragOver(DropTargetEvent dteEvent)
        {
            dteEvent.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;

            // We need to check if this thing can be dropped here. If it is a authenticated
            // user it can only be dropped on organizations. If it's a role, menu or toolbar it
            // can only be dropped on an organizational user.
            TreeItem tiTemp = (TreeItem) dteEvent.item;

            if (tiTemp != null)
            {
                if (VectorTransfer.getInstance().isSupportedType(dteEvent.currentDataType))
                {
                    // NOTE: on unsupported platforms this will return null
                    Vector<?> vData = (Vector<?>) (VectorTransfer.getInstance().nativeToJava(dteEvent.currentDataType));

                    if (vData == null)
                    {
                        System.out.println("Not supported on this platform.");
                    }
                }
            }
            else
            {
                // It cannot be dropped on the 'void'.
                dteEvent.operations = DND.DROP_NONE;
            }
        }

        /**
         * This method is fired when the data is dropped.
         *
         * @param  dteEvent  the event
         */
        public void drop(DropTargetEvent dteEvent)
        {
            if (VectorTransfer.getInstance().isSupportedType(dteEvent.currentDataType) &&
                    (dteEvent.item != null) && (dteEvent.item instanceof TreeItem))
            {
                // We first need to check if the desired drop is allowed.
                Vector<?> vData = (Vector<?>) dteEvent.data;

                if ((vData != null) && (vData.size() > 0))
                {
                    Object oTemp = vData.get(0);

                    if (oTemp instanceof LDAPTransferObject)
                    {
                        handleLDAPDrops((LDAPTreeNode) dteEvent.item, vData,
                                        ((LDAPTransferObject) oTemp).getType());
                    }
                    else if (oTemp instanceof XMLStoreItem)
                    {
                        handleXMLStoreDrops((LDAPTreeNode) dteEvent.item, vData,
                                            ((XMLStoreItem) oTemp).getType());
                    }
                }
            }
        }

        /**
         * This method is fired to determine whether or not to accept the drag.
         *
         * @param  dteEvent  The event.
         */
        public void dropAccept(DropTargetEvent dteEvent)
        {
        }

        /**
         * This method returns the string values for the attribute in a hashmap.
         *
         * @param   leEntry     The LDAP entry.
         * @param   sAttribute  The name of the attribute.
         *
         * @return  A hashmap with all the entries.
         */
        private ArrayList<String> getLDAPAttributeValues(LDAPEntry leEntry, String sAttribute)
        {
            ArrayList<String> alReturn = new ArrayList<String>();

            LDAPAttribute laRole = leEntry.getAttribute("role");
            String[] saRoles = laRole.getStringValueArray();

            for (int iCount = 0; iCount < saRoles.length; iCount++)
            {
                alReturn.add(saRoles[iCount]);
            }

            return alReturn;
        }

        /**
         * This method handles drops of roles and authenticated users.
         *
         * @param  ltnParent  The TreeItem it was dropped upon.
         * @param  data       The data that is being dropped.
         * @param  iType      The type that is being dragged.
         */
        private void handleLDAPDrops(LDAPTreeNode ltnParent, Vector<?> data, int iType)
        {
            if (iType == LDAPTransferObject.TYPE_ROLE)
            {
                if (validateDropTarget(ltnParent, AbstractCordysTreeNode.TYPE_ORGANIZATIONAL_USER))
                {
                    // We should parse the current roles to see if the dropped roles are
                    // already there. If they are not we should add them.
                    LDAPEntry leOrgUser = ltnParent.getLDAPItemEntry().getEntry();

                    ArrayList<String> alRoles = getLDAPAttributeValues(leOrgUser, "role");
                    LDAPAttribute laRoles = leOrgUser.getAttribute("role");

                    for (Iterator<?> iDroppedRoles = data.iterator(); iDroppedRoles.hasNext();)
                    {
                        LDAPTransferObject ltoRole = (LDAPTransferObject) iDroppedRoles.next();

                        if (!alRoles.contains(ltoRole.getDN()))
                        {
                            // This role should be added.
                            laRoles.addValue(ltoRole.getDN());
                        }
                    }

                    // Update the LDAP object.
                    try
                    {
                        m_ccConnection.changeLDAPAttibute(leOrgUser, laRoles,
                                                          LDAPModification.REPLACE);
                    }
                    catch (CordysConnectionException e)
                    {
                        MessageBoxUtil.showError(getShell(), "Error adding roles.", e);
                    }

                    refreshChildren(ltnParent);
                }
                else
                {
                    MessageBoxUtil.showError(getShell(),
                                             "A role can only be dropped on an organizational user.");
                }
            }
            else if (iType == LDAPTransferObject.TYPE_AUTHENTICATED_USER)
            {
                if (validateDropTarget(ltnParent, AbstractCordysTreeNode.TYPE_ORGANIZATION))
                {
                    // We have to check if there is already an organizational user for this
                    // authenticated user.
                    for (Iterator<?> iAuthUsers = data.iterator(); iAuthUsers.hasNext();)
                    {
                        try
                        {
                            LDAPTransferObject ltoAuthUser = (LDAPTransferObject) iAuthUsers.next();
                            LDAPEntry leAuthUser = ltnParent.getCordysConnection().readLDAPEntry(ltoAuthUser
                                                                                                 .getDN());
                            LDAPEntry leOrganization = ltnParent.getLDAPItemEntry().getEntry();

                            // The LTN is the organization, so we search within that organization is
                            // there already is a user based on this authenticated user.
                            String sFilter = "authenticationuser=" + leAuthUser.getDN();

                            LDAPEntry[] aleResult = m_ccConnection.searchLDAP("cn=organizational users," +
                                                                              ltnParent
                                                                              .getLDAPItemEntry()
                                                                              .getEntry().getDN(),
                                                                              LDAPConnection.SCOPE_SUB,
                                                                              sFilter);

                            if (aleResult.length != 0)
                            {
                                MessageBoxUtil.showError(getShell(),
                                                         "There is already an organizational user for this authenticated user.");
                            }
                            else if ((lRoles.getSelectionCount() == 0) &&
                                         (tMenus.getSelectionCount() == 0))
                            {
                                MessageBoxUtil.showError(getShell(),
                                                         "You need to select either 1 menu or 1 role");
                            }
                            else
                            {
                                // Now we should create it with the currently selected roles and
                                // menus.
                                String sNewDn = "cn=" + LDAPUtils.getAttrValue(leAuthUser, "cn") +
                                                ",cn=organizational users," +
                                                ltnParent.getLDAPItemEntry().getEntry().getDN();

                                // Copy all attributes from the authenticated user to the
                                // organizational user.
                                LDAPAttributeSet laOldAttributes = leAuthUser.getAttributeSet();
                                LDAPAttributeSet laNewAttributes = new LDAPAttributeSet();
                                Iterator<?> iAttributes = laOldAttributes.iterator();

                                while (iAttributes.hasNext())
                                {
                                    Object oObject = iAttributes.next();

                                    if (oObject instanceof LDAPAttribute)
                                    {
                                        LDAPAttribute lLDAPAttribute = (LDAPAttribute) oObject;
                                        laNewAttributes.add(new LDAPAttribute(lLDAPAttribute
                                                                              .getName(),
                                                                              lLDAPAttribute
                                                                              .getStringValueArray()));
                                    }
                                }

                                // Remove and change all unneeded attributes.
                                LDAPAttribute attrObjectClass = laNewAttributes.getAttribute("objectclass");
                                attrObjectClass.removeValue("busauthenticationuser");
                                attrObjectClass.addValue("busorganizationaluser");
                                attrObjectClass.addValue("busorganizationalobject");
                                laNewAttributes.remove("defaultcontext");
                                laNewAttributes.remove("osidentity");

                                LDAPAttribute attAuthUsr = new LDAPAttribute("authenticationuser");
                                attAuthUsr.addValue(leAuthUser.getDN());
                                laNewAttributes.add(attAuthUsr);

                                LDAPAttribute laNewRoles = new LDAPAttribute("role");

                                // cn=everyoneInDevelopment,cn=organizational
                                // roles,o=Development,cn=cordys,o=vanenburg.com
                                String sOrgName = leOrganization.getAttribute("o").getStringValue();
                                laNewRoles.addValue("cn=everyoneIn" + sOrgName +
                                                    ",cn=organizational roles," +
                                                    leOrganization.getDN());

                                String newSearchRoot = sNewDn.substring((leAuthUser.getDN().indexOf(",") +
                                                                         1));
                                newSearchRoot = newSearchRoot.substring((newSearchRoot.indexOf(",") +
                                                                         1));

                                try
                                {
                                    LDAPEntry[] results = m_ccConnection.searchLDAP(newSearchRoot,
                                                                                    LDAPConnection.SCOPE_SUB,
                                                                                    "role=cn=everyone,cn=Cordys WCP 1.2*");

                                    if (results.length != 0)
                                    {
                                        laNewRoles.addValue(results[0].getDN());
                                        laNewAttributes.add(laNewRoles);
                                    }
                                }
                                catch (CordysConnectionException le)
                                {
                                    MessageBoxUtil.showError(getShell(), "Error searching LDAP.",
                                                             le);
                                }

                                // Add the default selected roles.
                                int[] aiIndices = lRoles.getSelectionIndices();

                                for (int iCount = 0; iCount < aiIndices.length; iCount++)
                                {
                                    LDAPItemEntry lieTemp = lRoles.getSelectedObject(aiIndices[iCount]);
                                    laNewRoles.addValue(lieTemp.getEntry().getDN());
                                }

                                if ((laNewRoles.getStringValueArray() != null) &&
                                        (laNewRoles.getStringValueArray().length > 0))
                                {
                                    laNewAttributes.add(laNewRoles);
                                }

                                // Add the selected menus.
                                LDAPAttribute laNewMenus = new LDAPAttribute("menu");
                                TreeItem[] atiMenus = tMenus.getSelection();

                                for (int iCount = 0; iCount < atiMenus.length; iCount++)
                                {
                                    TreeItem tiTemp = atiMenus[iCount];

                                    if (tiTemp instanceof XMLStoreTreeNode)
                                    {
                                        XMLStoreTreeNode xstn = (XMLStoreTreeNode) tiTemp;

                                        if ((xstn.getXMLStoreItem().getType() ==
                                                 XMLStoreItem.TYPE_MENU) &&
                                                !xstn.getXMLStoreItem().isFolder())
                                        {
                                            laNewMenus.addValue(xstn.getXMLStoreItem().getKey());
                                        }
                                    }
                                }

                                if ((laNewMenus.getStringValueArray() != null) &&
                                        (laNewMenus.getStringValueArray().length > 0))
                                {
                                    laNewAttributes.add(laNewMenus);
                                }

                                // creates new LDAP entry with the new DN and The attributes
                                LDAPItemEntry lieNewOrgUser = new LDAPItemEntry(sNewDn,
                                                                                laNewAttributes);

                                // first save it to the LDAP
                                try
                                {
                                    m_ccConnection.changeLDAPEntry(lieNewOrgUser.getEntry(),
                                                                   LDAPModification.ADD);

                                    // Now add the child node for this entry
                                    LDAPTreeNode ltnNew = new LDAPTreeNode(ltnParent, SWT.NONE,
                                                                           lieNewOrgUser,
                                                                           ltnParent
                                                                           .getCordysConnection());
                                    ltnNew.loadChildren();
                                }
                                catch (Exception e)
                                {
                                    MessageBoxUtil.showError(getShell(),
                                                             "Error adding organizational user.",
                                                             e);
                                }
                            }
                        }
                        catch (CordysConnectionException e)
                        {
                            MessageBoxUtil.showError(getShell(),
                                                     "Error adding org user for authenticated user ",
                                                     e);
                        }
                    }
                }
                else
                {
                    MessageBoxUtil.showError(getShell(),
                                             "An authenticated user can only be dropped on an organization.");
                }
            }
        }

        /**
         * This method handles drops of menus and toolbars.
         *
         * @param  ltnParent  The TreeItem it was dropped upon.
         * @param  vData      The data that is being dropped.
         * @param  iType      The type of content in the vector.
         */
        private void handleXMLStoreDrops(LDAPTreeNode ltnParent, Vector<?> vData, int iType)
        {
            if ((iType == XMLStoreItem.TYPE_MENU) || (iType == XMLStoreItem.TYPE_TOOLBAR))
            {
                if (validateDropTarget(ltnParent, AbstractCordysTreeNode.TYPE_ORGANIZATIONAL_USER))
                {
                    // We should parse the current roles to see if the dropped roles are
                    // already there. If they are not we should add them.
                    LDAPEntry leOrgUser = ltnParent.getLDAPItemEntry().getEntry();

                    String sAttributeName = "menu";

                    if (iType == XMLStoreItem.TYPE_TOOLBAR)
                    {
                        sAttributeName = "toolbar";
                    }

                    ArrayList<String> alCurrentItems = getLDAPAttributeValues(leOrgUser,
                                                                              sAttributeName);
                    LDAPAttribute laAttr = leOrgUser.getAttribute(sAttributeName);

                    if (laAttr == null)
                    {
                        laAttr = new LDAPAttribute(sAttributeName);
                        leOrgUser.getAttributeSet().add(laAttr);
                    }

                    for (Iterator<?> iDroppedRoles = vData.iterator(); iDroppedRoles.hasNext();)
                    {
                        XMLStoreItem xsiItem = (XMLStoreItem) iDroppedRoles.next();

                        if (!alCurrentItems.contains(xsiItem.getKey()))
                        {
                            // This role should be added.
                            laAttr.addValue(xsiItem.getKey());
                        }
                    }

                    // Update the LDAP object.
                    try
                    {
                        m_ccConnection.changeLDAPAttibute(leOrgUser, laAttr,
                                                          LDAPModification.REPLACE);
                    }
                    catch (CordysConnectionException e)
                    {
                        MessageBoxUtil.showError(getShell(), "Error adding " + sAttributeName, e);
                    }

                    refreshChildren(ltnParent);
                }
                else
                {
                    MessageBoxUtil.showError(getShell(),
                                             "A menu or toolbar can only be dropped on an organizational user.");
                }
            }
        }

        /**
         * This method validates the drop target for.
         *
         * @param   tiParent      The tree item.
         * @param   iDesiredType  the desired type.
         *
         * @return  true if the parent is valid. Otherwise false.
         */
        private boolean validateDropTarget(TreeItem tiParent, int iDesiredType)
        {
            boolean bReturn = false;

            if (tiParent instanceof AbstractCordysTreeNode)
            {
                AbstractCordysTreeNode actnParent = (AbstractCordysTreeNode) tiParent;

                if (actnParent.getType() == iDesiredType)
                {
                    bReturn = true;
                }
            }
            return bReturn;
        }
    }
}
