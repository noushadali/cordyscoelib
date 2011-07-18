/**
 * © 2003 Cordys R&D B.V. All rights reserved.     The computer program(s) is
 * the proprietary information of Cordys R&D B.V.     and provided under the
 * relevant License Agreement containing restrictions     on use and
 * disclosure. Use is subject to the License Agreement.
 */
package com.cordys.coe.tools.useradmin;

import com.cordys.coe.util.general.ldap.LDAPUtils;
import com.cordys.coe.util.ldap.LDAPUtil;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;

import java.util.HashMap;
import java.util.Iterator;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Object to display the entry's in the tree.
 */
public class CordysTreeNode extends DefaultMutableTreeNode
{
    /**
     * Holds the list of DN's that have to be updated.
     */
    private HashMap<String, String> hmDNsToBeUpdated;
    /**
     * Connection to the LDAP-server.
     */
    private LDAPConnection lLDAPCon;
    /**
     * Holds the LDAP-entry.
     */
    private LDAPItemEntry lLDAPItemEntry;

    /**
     * Creates a new instance of CordysTreeNode.
     *
     * @param  oObject           this is the LDAPItemEntry
     * @param  lLDAPCon          The LDAPconnection
     * @param  hmDNsToBeUpdated  DOCUMENTME
     */
    public CordysTreeNode(Object oObject, LDAPConnection lLDAPCon,
                          HashMap<String, String> hmDNsToBeUpdated)
    {
        super(oObject);
        this.lLDAPCon = lLDAPCon;
        this.hmDNsToBeUpdated = hmDNsToBeUpdated;

        if (oObject instanceof LDAPItemEntry)
        {
            lLDAPItemEntry = (LDAPItemEntry) oObject;

            /**
             * check if it is an organization
             */
            String sFilter = null;

            if (LDAPUtils.checkAttriValueExists(lLDAPItemEntry.getEntry(), "objectclass",
                                                    "groupOfNames"))
            {
                sFilter = "objectclass=organization";
            }

            if (
                LDAPUtils.checkAttriValueExists(lLDAPItemEntry.getEntry(), "objectclass",
                                                    "organization") ||
                    LDAPUtils.checkAttriValueExists(lLDAPItemEntry.getEntry(), "objectclass",
                                                        "organizationalunit"))
            {
                sFilter = "objectclass=organizationalunit";
            }

            if (sFilter != null)
            {
                try
                {
                    LDAPEntry[] results = LDAPUtils.searchLDAP(lLDAPCon,
                                                               lLDAPItemEntry.getEntry().getDN(),
                                                               LDAPConnection.SCOPE_SUB, sFilter);

                    for (int iCount = 0; iCount < results.length; iCount++)
                    {
                        String sCheck = results[iCount].getDN();

                        if (!sCheck.equals(lLDAPItemEntry.getEntry().getDN()))
                        {
                            CordysTreeNode ctnTreeNode = new CordysTreeNode(new LDAPItemEntry(results[iCount]),
                                                                            lLDAPCon,
                                                                            hmDNsToBeUpdated);
                            this.add(ctnTreeNode);
                        }
                    }
                }
                catch (LDAPException e)
                {
                    System.out.println("Error getting the organization schema: " + e);
                }
            }

            // get all users in the current context
            if (
                LDAPUtils.checkAttriValueExists(lLDAPItemEntry.getEntry(), "objectclass",
                                                    "organization") ||
                    LDAPUtils.checkAttriValueExists(lLDAPItemEntry.getEntry(), "objectclass",
                                                        "organizationalunit"))
            {
                try
                {
                    LDAPEntry[] results = LDAPUtils.searchLDAP(lLDAPCon,
                                                               lLDAPItemEntry.getEntry().getDN(),
                                                               LDAPConnection.SCOPE_SUB,
                                                               "objectclass=busorganizationaluser");

                    for (int iCount = 0; iCount < results.length; iCount++)
                    {
                        String sCheck = results[iCount].getDN();

                        if (!sCheck.equals(lLDAPItemEntry.getEntry().getDN()))
                        {
                            CordysTreeNode ctnTreeNode = new CordysTreeNode(new LDAPItemEntry(results[iCount]),
                                                                            lLDAPCon,
                                                                            hmDNsToBeUpdated);
                            this.add(ctnTreeNode);
                        }
                    }
                }
                catch (LDAPException e)
                {
                    System.out.println("Error getting the organization schema: " + e);
                }
            }

            // if this is an user get all roles
            if (LDAPUtils.checkAttriValueExists(lLDAPItemEntry.getEntry(), "objectclass",
                                                    "busorganizationaluser"))
            {
                LDAPEntry lLdapEntry = lLDAPItemEntry.getEntry();

                LDAPAttribute anAttr = lLdapEntry.getAttribute("role");

                if (anAttr != null)
                {
                    String[] saValues = anAttr.getStringValueArray();

                    for (int iCount = 0; iCount < saValues.length; iCount++)
                    {
                        try
                        {
                            // add the roles to the tree
                            CordysTreeNode ctnTreeNode = new CordysTreeNode(new LDAPItemEntry(lLDAPCon
                                                                                              .read(saValues[iCount])),
                                                                            lLDAPCon,
                                                                            hmDNsToBeUpdated);
                            this.add(ctnTreeNode);
                        }
                        catch (LDAPException ldape)
                        {
                        }
                    }
                }
            }
        }
    }

    /**
     * Adds a child (new LDAPItemEntry(LDAPEntry)).
     *
     * @param   leUser  the LDAPEntry to add as a child
     *
     * @return  the Cordys added treeNode (if Added)
     */
    public CordysTreeNode addChild(LDAPEntry leUser)
    {
        CordysTreeNode ctnTreeNode = null;

        // if is user
        if (LDAPUtils.checkAttriValueExists(leUser, "objectclass", "busauthenticationuser"))
        {
            // get the current (this , it is the parent) Entry
            LDAPEntry leOrganization = ((LDAPItemEntry) this.getUserObject()).getEntry();

            // create a new dn for the lLDAPEntry
            String sNewDn = "cn=" + LDAPUtils.getAttrValue(leUser, "cn") +
                            ",cn=organizational users," + leOrganization.getDN();

            // Copy all attributes from the authenticated user to the organizational user.
            LDAPAttributeSet laOldAttributes = leUser.getAttributeSet();
            LDAPAttributeSet laNewAttributes = new LDAPAttributeSet();
            Iterator<?> iAttributes = laOldAttributes.iterator();

            while (iAttributes.hasNext())
            {
                Object oObject = iAttributes.next();

                if (oObject instanceof LDAPAttribute)
                {
                    LDAPAttribute lLDAPAttribute = (LDAPAttribute) oObject;
                    laNewAttributes.add(new LDAPAttribute(lLDAPAttribute.getName(),
                                                          lLDAPAttribute.getStringValueArray()));
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
            attAuthUsr.addValue(leUser.getDN());
            laNewAttributes.add(attAuthUsr);

            LDAPAttribute attRole = new LDAPAttribute("role");

            // If the current version is 1.5, also add the everyoneIn<OrgName> role to the user.
            if (LDAPUtil.getWCPVersion(lLDAPCon) >= LDAPUtil.WCP_VESION_1_5)
            {
                // cn=everyoneInDevelopment,cn=organizational
                // roles,o=Development,cn=cordys,o=vanenburg.com
                String sOrgName = leOrganization.getAttribute("o").getStringValue();
                attRole.addValue("cn=everyoneIn" + sOrgName + ",cn=organizational roles," +
                                 leOrganization.getDN());
            }

            // if wcp 1.2 add the everyone in organization
            if (LDAPUtil.getWCPVersion(lLDAPCon) >= LDAPUtil.WCP_VESION_1_2)
            {
                String newSearchRoot = sNewDn.substring((leUser.getDN().indexOf(",") + 1));
                newSearchRoot = newSearchRoot.substring((newSearchRoot.indexOf(",") + 1));

                try
                {
                    LDAPEntry[] results = LDAPUtils.searchLDAP(lLDAPCon, newSearchRoot,
                                                               LDAPConnection.SCOPE_SUB,
                                                               "role=cn=everyone,cn=Cordys WCP 1.2*");

                    if (results.length != 0)
                    {
                        attRole.addValue(results[0].getDN());
                        laNewAttributes.add(attRole);
                    }
                }
                catch (LDAPException ladpe)
                {
                    System.out.println(ladpe);
                }
            }

            if ((attRole.getStringValueArray() != null) &&
                    (attRole.getStringValueArray().length > 0))
            {
                laNewAttributes.add(attRole);
            }

            // creates new LDAP entry with the new DN and The attributes
            LDAPItemEntry newLDAP = new LDAPItemEntry(sNewDn, laNewAttributes);

            // first save it to the LDAP
            try
            {
                LDAPUtils.changeLDAP(lLDAPCon, newLDAP.getEntry(), LDAPUtils.LDAPCOMMAND_ADD);

                if (!hmDNsToBeUpdated.containsKey(sNewDn))
                {
                    hmDNsToBeUpdated.put(sNewDn, sNewDn);
                }
                ctnTreeNode = new CordysTreeNode(newLDAP, lLDAPCon, hmDNsToBeUpdated);
            }
            catch (LDAPException ldape)
            {
            }
        }

        // if is role
        if (LDAPUtils.checkAttriValueExists(leUser, "objectclass", "busorganizationalrole"))
        {
            // Create the attribute to be added :
            String attrValues = leUser.getDN();
            LDAPAttribute attrRole = new LDAPAttribute("role", attrValues);

            // get the entry to witch the attibute has to be added (the parrent) this
            LDAPItemEntry ParentItemEntry = (LDAPItemEntry) this.getUserObject();
            LDAPEntry leOrgUser = ParentItemEntry.getEntry();

            try
            {
                // Add the attribute to the LDAP
                LDAPUtils.changeLDAP(lLDAPCon, leOrgUser, LDAPUtils.LDAPCOMMAND_ADD_ATTRIBUTE,
                                     attrRole);

                if (!hmDNsToBeUpdated.containsKey(leOrgUser.getDN()))
                {
                    hmDNsToBeUpdated.put(leOrgUser.getDN(), leOrgUser.getDN());
                }
                ctnTreeNode = new CordysTreeNode(new LDAPItemEntry(lLDAPCon.read(leUser.getDN())),
                                                 lLDAPCon, hmDNsToBeUpdated);
            }
            catch (LDAPException ldape)
            {
            }
        }

        if (ctnTreeNode != null)
        {
            // add the treenode to this
            this.add(ctnTreeNode);
        }
        return ctnTreeNode;
    }

    // funtion to call if this tree has to eliminate itself
    /**
     * Deletes itself.
     */
    public void deleteThis()
    {
        if (isUser())
        {
            LDAPItemEntry ldapItemEntry = (LDAPItemEntry) this.getUserObject();

            try
            {
                LDAPEntry leOrgUser = ldapItemEntry.getEntry();

                if (!hmDNsToBeUpdated.containsKey(leOrgUser.getDN()))
                {
                    hmDNsToBeUpdated.put(leOrgUser.getDN(), leOrgUser.getDN());
                }
                LDAPUtils.changeLDAP(lLDAPCon, ldapItemEntry.getEntry(),
                                     LDAPUtils.LDAPCOMMAND_DELETE);
            }
            catch (LDAPException ldape)
            {
            }
        }

        if (isOrganizationRole())
        {
            // get the parent object
            CordysTreeNode crnNode = (CordysTreeNode) this.getParent();
            LDAPItemEntry parentItemEntry = (LDAPItemEntry) crnNode.getUserObject();
            LDAPEntry parentldapEntry = parentItemEntry.getEntry();

            // get the dn from the current object
            LDAPItemEntry ldapItemEntry = (LDAPItemEntry) this.getUserObject();
            LDAPEntry lldapEntry = ldapItemEntry.getEntry();

            // create the arribute that has to be deleted
            String attrValues = lldapEntry.getDN();
            LDAPAttribute attrRole = new LDAPAttribute("role", attrValues);

            try
            {
                if (!hmDNsToBeUpdated.containsKey(parentldapEntry.getDN()))
                {
                    hmDNsToBeUpdated.put(parentldapEntry.getDN(), parentldapEntry.getDN());
                }
                LDAPUtils.changeLDAP(lLDAPCon, parentldapEntry,
                                     LDAPUtils.LDAPCOMMAND_DELETE_ATTRIBUTE, attrRole);
            }
            catch (LDAPException ldape)
            {
            }
        }

        // remove the item from parent
        removeFromParent();
    }

    /**
     * This method returns the LDAPEntry for this node.
     *
     * @return  The LDAPEntry for this node.
     */
    public LDAPEntry getLDAPEntry()
    {
        return lLDAPItemEntry.getEntry();
    }

    /**
     * check if the current treenode is an organization.
     *
     * @return  returns true this is an organization
     */
    public boolean isOrganization()
    {
        boolean bReturn = false;

        if (LDAPUtils.checkAttriValueExists(((LDAPItemEntry) this.getUserObject()).getEntry(),
                                                "objectclass", "organization"))
        {
            bReturn = true;
        }
        return bReturn;
    }

    // gives true if the current treenode is an organizationRole
    /**
     * check if the current treenode is an organizationRole.
     *
     * @return  returns true this is an organizationrole
     */
    public boolean isOrganizationRole()
    {
        boolean bReturn = false;

        if (LDAPUtils.checkAttriValueExists(((LDAPItemEntry) this.getUserObject()).getEntry(),
                                                "objectclass", "busorganizationalrole"))
        {
            bReturn = true;
        }
        return bReturn;
    }

    // gives true if the current treenode is an organizationUnit
    /**
     * check if the current treenode is an organizationUnit.
     *
     * @return  returns true this is an organizationunit
     */
    public boolean isOrganizationUnit()
    {
        boolean bReturn = false;

        if (LDAPUtils.checkAttriValueExists(((LDAPItemEntry) this.getUserObject()).getEntry(),
                                                "objectclass", "organizationalunit"))
        {
            bReturn = true;
        }
        return bReturn;
    }

    // gives true if the current treenode is an user
    /**
     * check if the current treenode is an user.
     *
     * @return  returns true this is an user
     */
    public boolean isUser()
    {
        boolean bReturn = false;

        if (LDAPUtils.checkAttriValueExists(((LDAPItemEntry) this.getUserObject()).getEntry(),
                                                "objectclass", "busorganizationaluser"))
        {
            bReturn = true;
        }
        return bReturn;
    }
}
