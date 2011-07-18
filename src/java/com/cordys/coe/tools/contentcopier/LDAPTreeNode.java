/**
 *      © 2003 Cordys R&D B.V. All rights reserved.
 *      The computer program(s) is the proprietary information of Cordys R&D B.V.
 *      and provided under the relevant License Agreement containing restrictions
 *      on use and disclosure. Use is subject to the License Agreement.
 */
package com.cordys.coe.tools.contentcopier;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;

import javax.swing.*;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * LDAP Tree node. Abstract class to represent a LDAP-entry as treenode.
 */
public abstract class LDAPTreeNode extends DefaultMutableTreeNode
{
    /**
     * Indicates whether or not the item is loaded.
     */
    private boolean isLoaded;
    /**
     * Holds ther connection to LDAP.
     */
    private LDAPConnection lCon;
    /**
     * Holds the LDAP-Entry it should display.
     */
    private LDAPEntry leEntry;

    /**
     * Constructor.
     *
     * @param  lCon     The connection to the LDAP server.
     * @param  leEntry  The actual LDAPentry.
     */
    public LDAPTreeNode(LDAPConnection lCon, LDAPEntry leEntry)
    {
        super("", true);

        String sDisplayName = leEntry.getDN();
        int iPos = sDisplayName.indexOf(",");

        if (iPos > -1)
        {
            sDisplayName = sDisplayName.substring(0, iPos);
        }
        setUserObject(sDisplayName);

        setAllowsChildren(true);
        this.lCon = lCon;
        this.leEntry = leEntry;
        isLoaded = false;
    } // LDAPTreeNode

    /**
     * Returns the LDAP-connection.
     *
     * @return  The LDAP-connection.
     */
    public LDAPConnection getLDAPCon()
    {
        return lCon;
    } // getLDAPCon

    /**
     * Returns the LDAP-entry.
     *
     * @return  The LDAP-entry.
     */
    public LDAPEntry getLDAPEntry()
    {
        return leEntry;
    } // getLDAPEntry

    /**
     * Returns it isn't a leaf.
     *
     * @return  false.
     */
    @Override public boolean isLeaf()
    {
        return false;
    } // isLeaf

    /**
     * Returns if it's loaded.
     *
     * @return  true if the node is loaded. Otherwise false.
     */
    public boolean isLoaded()
    {
        return isLoaded;
    } // isLoaded

    /**
     * Method gets called when the node collapses.
     *
     * @param  teeEvent  The event that occured.
     */
    public void onCollapse(TreeExpansionEvent teeEvent)
    {
    } // onCollapse

    /**
     * This method gets called when a treeitem is contexted.
     *
     * @param  pmMenu  The popup-menu that gets displayed.
     */
    public void onContext(JPopupMenu pmMenu)
    {
    } // onContext

    /**
     * MEthod gets called when the nod is selected.
     *
     * @param  tse  The event that occured.
     */
    public void onSelect(TreeSelectionEvent tse)
    {
    } // onSelect

    /**
     * Sets the object is loaded.
     *
     * @param  flag  Whether or not the node is loaded.
     */
    public void setLoaded(boolean flag)
    {
        isLoaded = flag;

        if (flag == false)
        {
            onCollapse(null);
            removeAllChildren();
        }
    } // setLoaded

    // ***********************************************//
    // ********* ADAPTER METHODS *********************//
    // ***********************************************//
    /**
     * Method gets called when the node expands.
     *
     * @param  teeEvent  The event that occured.
     */
    protected void onExpand(TreeExpansionEvent teeEvent)
    {
    } // onExpand
} // LDAPTreeNode
