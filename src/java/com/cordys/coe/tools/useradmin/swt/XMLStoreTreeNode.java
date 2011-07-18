package com.cordys.coe.tools.useradmin.swt;

import com.cordys.coe.util.connection.ICordysConnection;
import com.cordys.coe.util.connection.INativeConnection;
import com.cordys.coe.util.general.ldap.LDAPUtils;
import com.cordys.coe.util.swt.MessageBoxUtil;
import com.cordys.coe.util.swt.SWTResourceManager;
import com.cordys.coe.util.xml.dom.XMLHelper;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This treenode wraps around a XML store entry.
 *
 * @author  pgussow
 */
public class XMLStoreTreeNode extends AbstractCordysTreeNode
{
    /**
     * Creates a new CordysTreeNode object.
     *
     * @param  tParent       the parent tree
     * @param  iStyle        The style for the item.
     * @param  xsiItem       The corresponding LDAP entry.
     * @param  ccConnection  The LDAP connection.
     */
    public XMLStoreTreeNode(Tree tParent, int iStyle, XMLStoreItem xsiItem,
                            ICordysConnection ccConnection)
    {
        super(tParent, iStyle, xsiItem, ccConnection);
    }

    /**
     * Creates a new CordysTreeNode object.
     *
     * @param  tiParent      The parent tree item.
     * @param  iStyle        The style for this item.
     * @param  xsiItem       The corresponding LDAP entry.
     * @param  ccConnection  The LDAP connection.
     */
    public XMLStoreTreeNode(TreeItem tiParent, int iStyle, XMLStoreItem xsiItem,
                            ICordysConnection ccConnection)
    {
        super(tiParent, iStyle, xsiItem, ccConnection);
    }

    /**
     * This method gets the XML Store item for this node.
     *
     * @return  The XML Store item for this node.
     */
    public XMLStoreItem getXMLStoreItem()
    {
        return (XMLStoreItem) getEntry();
    }

    /**
     * This method loads the children of this tree node.
     *
     * @throws  Exception  DOCUMENTME
     */
    @Override public void loadChildren()
                                throws Exception
    {
        // This node has no children.
        ICordysConnection cConnector = getCordysConnection();

        if (cConnector != null)
        {
            // Use the SYSTEM user if it's ISV level. If it's organizational version we need to
            // pick a user from the specific organization.
            String sUser = getNeededUser();

            XMLStoreItem xsiTemp = getXMLStoreItem();
            Node iMethod = cConnector.createSoapMethod(null, null, "GetCollection",
                                                       "http://schemas.cordys.com/1.0/xmlstore",
                                                       sUser);
            Node nEnvelope = cConnector.getEnvelope(iMethod);

            Element iFolder = (Element) XMLHelper.createTextElement("folder", xsiTemp.getKey(),
                                                                    iMethod);
            iFolder.setAttribute("version", xsiTemp.getLevelDesc());

            Node nResponse = cConnector.sendAndWait(nEnvelope);

            Node nMethodResponse = XMLHelper.getMethodResponse(nResponse, "pre");
            NodeList nlTuples = XMLHelper.getNodeList(nMethodResponse, "./:tuple");

            for (int iCount = 0; iCount < nlTuples.getLength(); iCount++)
            {
                Element iEntryNode = (Element) nlTuples.item(iCount);

                XMLStoreItem xsiNew = new XMLStoreItem(iEntryNode.getAttribute("key"));
                xsiNew.setLevel(iEntryNode.getAttribute("level"));

                if (iEntryNode.getAttribute("isFolder").equals("true"))
                {
                    xsiNew.setFolder(true);
                }
                else
                {
                    xsiNew.setFolder(false);
                }

                XMLStoreTreeNode xstn = new XMLStoreTreeNode(this, SWT.NONE, xsiNew, cConnector);

                if (xsiNew.isFolder())
                {
                    xstn.setImage(SWTResourceManager.getImage(UserAdmin.class, "folder.gif"));
                }

                // If the level is organization, we need to store the organization as well.
                if (xsiNew.getLevel() == XMLStoreItem.LEVEL_ORGANIZATION)
                {
                    xsiNew.setOrganization(xsiTemp.getOrganization());
                }
            }
        }
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
        if ((getType() == TYPE_USER_MENU) || (getType() == TYPE_USER_TOOLBAR))
        {
            setText(getXMLStoreItem().getDisplay());
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
        if (getType() == TYPE_USER_MENU)
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
        String sKey = getXMLStoreItem().getKey();

        if (sKey.indexOf("Cordys/WCP/Menu") > -1)
        {
            setType(TYPE_USER_MENU);
        }
        else if (sKey.indexOf("Cordys/WCP/Toolbar") > -1)
        {
            setType(TYPE_USER_TOOLBAR);
        }
    }

    /**
     * This method returns the user to get the collection with. If the level is ISV then trhe user
     * SYSTEM is used. Otherwise the first organizational user is used.
     *
     * @return  The organizational user to use.
     */
    private String getNeededUser()
    {
        String sReturn = null;

        if (getCordysConnection() instanceof INativeConnection)
        {
            INativeConnection ncNative = (INativeConnection) getCordysConnection();
            sReturn = getCordysConnection().getOrganizationalUser();

            if (getXMLStoreItem().getLevel() == XMLStoreItem.LEVEL_ISV)
            {
                sReturn = "cn=SYSTEM,cn=organizational users,o=system," +
                          ncNative.getLDAPDirectory().getDirectorySearchRoot();
            }
            else if (getXMLStoreItem().getLevel() == XMLStoreItem.LEVEL_ORGANIZATION)
            {
                String sSearchRoot = "cn=organizational users," +
                                     getXMLStoreItem().getOrganization();

                try
                {
                    LDAPEntry[] aleResults = LDAPUtils.searchLDAP(ncNative.getLDAPDirectory()
                                                                  .getConnection(), sSearchRoot,
                                                                  LDAPConnection.SCOPE_SUB,
                                                                  "objectclass=busorganizationaluser");

                    if (aleResults.length > 0)
                    {
                        sReturn = aleResults[1].getDN();
                    }
                }
                catch (LDAPException e)
                {
                    MessageBoxUtil.showError(getParent().getShell(),
                                             "Error getting the proper organizational user.", e);
                }
            }
        }

        return sReturn;
    }
}
