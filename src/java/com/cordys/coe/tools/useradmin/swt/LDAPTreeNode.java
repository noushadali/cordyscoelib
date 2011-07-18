package com.cordys.coe.tools.useradmin.swt;

import com.cordys.coe.tools.useradmin.LDAPItemEntry;
import com.cordys.coe.util.connection.CordysConnectionException;
import com.cordys.coe.util.connection.ICordysConnection;
import com.cordys.coe.util.general.ldap.LDAPUtils;
import com.cordys.coe.util.swt.SWTResourceManager;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * TreeItem to wrap around a LDAP entry.
 *
 * @author  pgussow
 */
public class LDAPTreeNode extends AbstractCordysTreeNode
{
    /**
     * Creates a new CordysTreeNode object.
     *
     * @param  tParent   the parent tree
     * @param  iStyle    The style for the item.
     * @param  lieEntry  The corresponding LDAP entry.
     * @param  lcConn    The LDAP connection.
     */
    public LDAPTreeNode(Tree tParent, int iStyle, LDAPItemEntry lieEntry, ICordysConnection lcConn)
    {
        super(tParent, iStyle, lieEntry, lcConn);
    }

    /**
     * Creates a new CordysTreeNode object.
     *
     * @param  tiParent  The parent tree item.
     * @param  iStyle    The style for this item.
     * @param  lieEntry  The corresponding LDAP entry.
     * @param  lcConn    The LDAP connection.
     */
    public LDAPTreeNode(TreeItem tiParent, int iStyle, LDAPItemEntry lieEntry,
                        ICordysConnection lcConn)
    {
        super(tiParent, iStyle, lieEntry, lcConn);
    }

    /**
     * This method gets the LDAP entry for this node.
     *
     * @return  The LDAP entry for this node.
     */
    public LDAPItemEntry getLDAPItemEntry()
    {
        return (LDAPItemEntry) getEntry();
    }

    /**
     * This method loads the children of this tree node.
     *
     * @throws  Exception  DOCUMENTME
     */
    @Override public void loadChildren()
                                throws Exception
    {
        if (hasLoadedChildren() == false)
        {
            if (getType() == TYPE_ROOT)
            {
                loadOrganizations();
            }
            else if ((getType() == TYPE_ORGANIZATION) || (getType() == TYPE_ORGANIZATIONAL_UNIT))
            {
                loadOrgContent();
            }
            else if (getType() == TYPE_ORGANIZATIONAL_USER)
            {
                loadUserDetails();
            }
        }
        else
        {
            // Do a refresh
        }
        setLoadedChildren(true);
    }

    /**
     * This method initializes the tree item. Based on the LDAP entry the type is determined and
     * initialized accordingly.
     */
    @Override protected void initialize()
    {
        // First we need to determine the type for this tree node.
        determineType();

        // Now set the proper image
        determineImage();

        // Set the caption
        determineCaption();
    }

    /**
     * This method determines the caption for this tree item.
     */
    private void determineCaption()
    {
        if ((getType() == TYPE_ROOT))
        {
            setText(getLDAPItemEntry().getEntry().getDN());
        }
        else if ((getType() == TYPE_ORGANIZATION) || (getType() == TYPE_ORGANIZATIONAL_UNIT) ||
                     (getType() == TYPE_ORGANIZATIONAL_USER) || (getType() == TYPE_USER_ROLE))
        {
            String sDescription = LDAPUtils.getAttrValue(getLDAPItemEntry().getEntry(),
                                                         "description");

            if ((sDescription == null) || (sDescription.length() == 0))
            {
                sDescription = LDAPUtils.getAttrValue(getLDAPItemEntry().getEntry(), "cn");
            }
            setText(sDescription);
        }
        else
        {
            setText("UNKNOWN");
        }
    }

    /**
     * This method determines the image that will be shown.
     */
    private void determineImage()
    {
        if (getType() == TYPE_ROOT)
        {
            setImage(SWTResourceManager.getImage(UserAdmin.class, "useradmin.gif"));
        }
        else if ((getType() == TYPE_ORGANIZATION) || (getType() == TYPE_ORGANIZATIONAL_UNIT))
        {
            setImage(SWTResourceManager.getImage(UserAdmin.class, "organization.gif"));
        }
        else if (getType() == TYPE_ORGANIZATIONAL_USER)
        {
            setImage(SWTResourceManager.getImage(UserAdmin.class, "organizationaluser.gif"));
        }
        else if (getType() == TYPE_USER_ROLE)
        {
            setImage(SWTResourceManager.getImage(UserAdmin.class, "organizationalrole.gif"));
        }
        else if (getType() == TYPE_USER_MENU)
        {
            setImage(SWTResourceManager.getImage(UserAdmin.class, "menu.gif"));
        }
        else if (getType() == TYPE_USER_TOOLBAR)
        {
            setImage(SWTResourceManager.getImage(UserAdmin.class, "toolbar.gif"));
        }
    }

    /**
     * This method determines the type for this node.
     */
    private void determineType()
    {
        LDAPEntry leEntry = getLDAPItemEntry().getEntry();

        if (LDAPUtils.checkAttriValueExists(leEntry, "objectclass", "groupOfNames"))
        {
            setType(TYPE_ROOT);
        }
        else if (LDAPUtils.checkAttriValueExists(leEntry, "objectclass", "organization"))
        {
            setType(TYPE_ORGANIZATION);
        }
        else if (LDAPUtils.checkAttriValueExists(leEntry, "objectclass", "busorganizationaluser"))
        {
            setType(TYPE_ORGANIZATIONAL_USER);
        }
        else if (LDAPUtils.checkAttriValueExists(leEntry, "objectclass", "busorganizationalrole"))
        {
            setType(TYPE_USER_ROLE);
        }
        else if (LDAPUtils.checkAttriValueExists(leEntry, "objectclass", "organizationalunit"))
        {
            setType(TYPE_ORGANIZATIONAL_UNIT);
        }
    }

    /**
     * This method retrieves all the organizations from LDAP.
     *
     * @throws  CordysConnectionException  DOCUMENTME
     */
    private void loadOrganizations()
                            throws CordysConnectionException
    {
        LDAPEntry[] results = getCordysConnection().searchLDAP(getLDAPItemEntry().getEntry()
                                                               .getDN(), LDAPConnection.SCOPE_SUB,
                                                               "objectclass=organization");

        for (int iCount = 0; iCount < results.length; iCount++)
        {
            String sCheck = results[iCount].getDN();

            if (!sCheck.equals(getLDAPItemEntry().getEntry().getDN()))
            {
                new LDAPTreeNode(this, SWT.NONE, new LDAPItemEntry(results[iCount]),
                                 getCordysConnection());
            }
        }
    }

    /**
     * This method loads the organizational users within the current organization. It also loads the
     * organizational units.
     *
     * @throws  CordysConnectionException  DOCUMENTME
     */
    private void loadOrgContent()
                         throws CordysConnectionException
    {
        // Load organizational units.
        LDAPEntry[] aleResults = getCordysConnection().searchLDAP(getLDAPItemEntry().getEntry()
                                                                  .getDN(),
                                                                  LDAPConnection.SCOPE_SUB,
                                                                  "objectclass=organizationalunit");

        for (int iCount = 0; iCount < aleResults.length; iCount++)
        {
            String sCheck = aleResults[iCount].getDN();

            if (!sCheck.equals(getLDAPItemEntry().getEntry().getDN()))
            {
                new LDAPTreeNode(this, SWT.NONE, new LDAPItemEntry(aleResults[iCount]),
                                 getCordysConnection());
            }
        }

        // Load the organizational users.
        aleResults = getCordysConnection().searchLDAP(getLDAPItemEntry().getEntry().getDN(),
                                                      LDAPConnection.SCOPE_SUB,
                                                      "objectclass=busorganizationaluser");

        for (int iCount = 0; iCount < aleResults.length; iCount++)
        {
            String sCheck = aleResults[iCount].getDN();

            if (!sCheck.equals(getLDAPItemEntry().getEntry().getDN()))
            {
                new LDAPTreeNode(this, SWT.NONE, new LDAPItemEntry(aleResults[iCount]),
                                 getCordysConnection());
            }
        }
    }

    /**
     * This method loads all the details of the current organizational user. This means roles, menus
     * and toolbars.
     *
     * @throws  CordysConnectionException
     */
    private void loadUserDetails()
                          throws CordysConnectionException
    {
        // Load the roles
        LDAPEntry leEntry = getLDAPItemEntry().getEntry();

        LDAPAttribute laAttr = leEntry.getAttribute("role");

        if (laAttr != null)
        {
            String[] saValues = laAttr.getStringValueArray();

            for (int iCount = 0; iCount < saValues.length; iCount++)
            {
                LDAPEntry leTemp = getCordysConnection().readLDAPEntry(saValues[iCount]);

                if (leTemp != null)
                {
                    // add the roles to the tree
                    new LDAPTreeNode(this, SWT.NONE, new LDAPItemEntry(leTemp),
                                     getCordysConnection());
                }
            }
        }

        // Load the menus.
        laAttr = leEntry.getAttribute("menu");

        if (laAttr != null)
        {
            String[] saValues = laAttr.getStringValueArray();

            for (int iCount = 0; iCount < saValues.length; iCount++)
            {
                // Now we have the key of the menu. So we need to create the menu object.
                new XMLStoreTreeNode(this, SWT.NONE, new XMLStoreItem(saValues[iCount]), null);
            }
        }

        // Load the toolbars
        // Load the menus.
        laAttr = leEntry.getAttribute("toolbar");

        if (laAttr != null)
        {
            String[] saValues = laAttr.getStringValueArray();

            for (int iCount = 0; iCount < saValues.length; iCount++)
            {
                // Now we have the key of the toolbar. So we need to create the menu object.
                new XMLStoreTreeNode(this, SWT.NONE, new XMLStoreItem(saValues[iCount]), null);
            }
        }
    }
}
