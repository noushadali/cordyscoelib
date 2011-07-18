/**
 *      © 2003 Cordys R&D B.V. All rights reserved.
 *      The computer program(s) is the proprietary information of Cordys R&D B.V.
 *      and provided under the relevant License Agreement containing restrictions
 *      on use and disclosure. Use is subject to the License Agreement.
 */
package com.cordys.coe.tools.contentcopier;

import com.cordys.coe.util.general.ldap.LDAPUtils;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSearchConstraints;
import com.novell.ldap.controls.LDAPSortControl;
import com.novell.ldap.controls.LDAPSortKey;

import javax.swing.event.TreeExpansionEvent;

/**
 * Created by IntelliJ IDEA. User: pgussow Date: 15-aug-2003 Time: 16:41:39 To change this template
 * use Options | File Templates.
 */
public class ContentCopierTreeNode extends LDAPTreeNode
{
    /**
     * Creates a new ContentCopierTreeNode object.
     *
     * @param  lCon
     * @param  leEntry
     */
    public ContentCopierTreeNode(LDAPConnection lCon, LDAPEntry leEntry)
    {
        super(lCon, leEntry);
    } // LDAPTreeNode

    /**
     * Method gets called when the node expands.
     *
     * @param  teeEvent  The event that occured.
     */
    @Override protected void onExpand(TreeExpansionEvent teeEvent)
    {
        if (!isLoaded())
        {
            setLoaded(true);

            // Now we should find all the children for this ldap-entry
            try
            {
                // Make sure the server returns the results sorted.
                // Create a sort key that specifies the sort order.
                LDAPSortKey sortOrder = new LDAPSortKey("cn");

                // Create a "critical" server control using that sort key.
                LDAPSortControl sortCtrl = new LDAPSortControl(sortOrder, false);

                // Create search constraints to use that control.
                LDAPSearchConstraints cons = new LDAPSearchConstraints();
                cons.setControls(sortCtrl);

                LDAPEntry[] laChildren = LDAPUtils.searchLDAP(getLDAPCon(), getLDAPEntry().getDN(),
                                                              LDAPConnection.SCOPE_ONE, null, cons);

                for (int iCount = 0; iCount < laChildren.length; iCount++)
                {
                    LDAPEntry ldapEntry = laChildren[iCount];
                    ContentCopierTreeNode cctn = new ContentCopierTreeNode(getLDAPCon(), ldapEntry);
                    add(cctn);
                }
            }
            catch (LDAPException e)
            {
                // Ignore exception
                System.out.println("Error loading child: " + e);
            }
        }
    } // onExpand
} // ContentCopierTreeNode
