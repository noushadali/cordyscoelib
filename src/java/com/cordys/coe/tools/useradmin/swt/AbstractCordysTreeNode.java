package com.cordys.coe.tools.useradmin.swt;

import com.cordys.coe.util.connection.ICordysConnection;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * DOCUMENTME.
 *
 * @author  $author$
 */
public abstract class AbstractCordysTreeNode extends TreeItem
{
    /**
     * Identifies the root node.
     */
    public static final int TYPE_ROOT = 0;
    /**
     * Identifies a organization node.
     */
    public static final int TYPE_ORGANIZATION = 1;
    /**
     * Identifies a organizational user.
     */
    public static final int TYPE_ORGANIZATIONAL_USER = 2;
    /**
     * Identifies a role.
     */
    public static final int TYPE_USER_ROLE = 3;
    /**
     * Identifies a menu.
     */
    public static final int TYPE_USER_MENU = 4;
    /**
     * Identifies a toolbar.
     */
    public static final int TYPE_USER_TOOLBAR = 5;
    /**
     * Identifies an organizational unit.
     */
    public static final int TYPE_ORGANIZATIONAL_UNIT = 6;
    /**
     * Indicates whether or not the children are loaded.
     */
    private boolean bLoadedChildren = false;
    /**
     * Holds the type of tree item.
     */
    private int iType = TYPE_ROOT;
    /**
     * Holds the datasource.
     */
    private ICordysConnection m_ccConnection;
    /**
     * Holds the entry for this treenode.
     */
    private Object oEntry;

    /**
     * Creates a new CordysTreeNode object.
     *
     * @param  tParent      the parent tree
     * @param  iStyle       The style for the item.
     * @param  oEntry       The corresponding LDAP entry.
     * @param  oDataSource  The LDAP connection.
     */
    public AbstractCordysTreeNode(Tree tParent, int iStyle, Object oEntry,
                                  ICordysConnection oDataSource)
    {
        super(tParent, iStyle);
        this.oEntry = oEntry;
        this.m_ccConnection = oDataSource;

        initialize();
    }

    /**
     * Creates a new CordysTreeNode object.
     *
     * @param  tiParent     The parent tree item.
     * @param  iStyle       The style for this item.
     * @param  oEntry       The corresponding LDAP entry.
     * @param  oDataSource  The LDAP connection.
     */
    public AbstractCordysTreeNode(TreeItem tiParent, int iStyle, Object oEntry,
                                  ICordysConnection oDataSource)
    {
        super(tiParent, iStyle);
        this.oEntry = oEntry;
        this.m_ccConnection = oDataSource;

        initialize();
    }

    /**
     * This method loads the children of this tree node.
     *
     * @throws  Exception  DOCUMENTME
     */
    public abstract void loadChildren()
                               throws Exception;

    /**
     * This method gets the connector for this tree item.
     *
     * @return  The connector for this tree item.
     */
    public ICordysConnection getCordysConnection()
    {
        return (ICordysConnection) getDataSource();
    }

    /**
     * This method gets the datasource for this tree item.
     *
     * @return  The datasource for this tree item.
     */
    public Object getDataSource()
    {
        return m_ccConnection;
    }

    /**
     * This method gets the entry for this node.
     *
     * @return  The entry for this node.
     */
    public Object getEntry()
    {
        return oEntry;
    }

    /**
     * This method gets the type for this tree node.
     *
     * @return  The type for this tree node.
     */
    public int getType()
    {
        return iType;
    }

    /**
     * This method gets whether or not the children of this node have been loaded.
     *
     * @return  Whether or not the children of this node have been loaded.
     */
    public boolean hasLoadedChildren()
    {
        return bLoadedChildren;
    }

    /**
     * This method sets wether or not the children of this node have been loaded.
     *
     * @param  bLoadedChildren  Whether or not the children of this node have been loaded.
     */
    public void setLoadedChildren(boolean bLoadedChildren)
    {
        this.bLoadedChildren = bLoadedChildren;
    }

    /**
     * This method sets the type for this tree node.
     *
     * @param  iType  The type for this tree node.
     */
    public void setType(int iType)
    {
        this.iType = iType;
    }

    /**
     * This method initializes the tree item. Based on the LDAP entry the type is determined and
     * initialized accordingly.
     */
    protected abstract void initialize();

    /**
     * Make sure this class can be subclassed.
     */
    @Override protected void checkSubclass()
    {
    }
}
