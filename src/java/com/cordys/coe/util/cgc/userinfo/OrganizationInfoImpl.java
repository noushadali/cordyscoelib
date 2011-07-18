package com.cordys.coe.util.cgc.userinfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class wraps the organizational information.
 *
 * @author  pgussow
 */
class OrganizationInfoImpl
    implements IOrganizationInfo
{
    /**
     * Holds whether or not this is the default organization for the user.
     */
    private boolean m_bDefaultOrganization;
    /**
     * Holds all menus for this organization.
     */
    private List<String> m_lMenus = new ArrayList<String>();
    /**
     * Holds all toolbars for this organization.
     */
    private List<String> m_lToolbars = new ArrayList<String>();
    /**
     * Holds all roles for this organizations.
     */
    private Map<String, IRoleInfo> m_mRoles = new LinkedHashMap<String, IRoleInfo>();
    /**
     * Holds the description of the oprganization.
     */
    private String m_sDescription;
    /**
     * Holds teh DN of the organization.
     */
    private String m_sDN;
    /**
     * Holds the organizational user.
     */
    private String m_sOrganizationalUser;

    /**
     * This method adds a new menu to this role.
     *
     * @param  sMenu  The menu to add.
     *
     * @see    com.cordys.coe.util.cgc.userinfo.IOrganizationInfo#addMenu(java.lang.String)
     */
    public void addMenu(String sMenu)
    {
        m_lMenus.add(sMenu);
    }

    /**
     * This method adds a new role to this organization.
     *
     * @param  riRole  The toolbar to add.
     *
     * @see    com.cordys.coe.util.cgc.userinfo.IOrganizationInfo#addRole(com.cordys.coe.util.cgc.userinfo.IRoleInfo)
     */
    public void addRole(IRoleInfo riRole)
    {
        m_mRoles.put(riRole.getRoleDN(), riRole);
    }

    /**
     * This method adds a new toolbar to this organization.
     *
     * @param  sToolbar  The toolbar to add.
     *
     * @see    com.cordys.coe.util.cgc.userinfo.IOrganizationInfo#addToolbar(java.lang.String)
     */
    public void addToolbar(String sToolbar)
    {
        m_lToolbars.add(sToolbar);
    }

    /**
     * This method gets the description for this user.
     *
     * @return  The description for this user.
     *
     * @see     com.cordys.coe.util.cgc.userinfo.IOrganizationInfo#getDescription()
     */
    public String getDescription()
    {
        return m_sDescription;
    }

    /**
     * This method gets the DN of the current organization.
     *
     * @return  The DN of the current organization.
     *
     * @see     com.cordys.coe.util.cgc.userinfo.IOrganizationInfo#getDN()
     */
    public String getDN()
    {
        return m_sDN;
    }

    /**
     * This method gets the list of menus assigned to this user.
     *
     * @return  The list of menus assigned to this user.
     *
     * @see     com.cordys.coe.util.cgc.userinfo.IOrganizationInfo#getMenus()
     */
    public List<String> getMenus()
    {
        // Always return a new collection because otherwise the callee
        // cannot iterate it in a concurrent-scenario.
        return new ArrayList<String>(m_lMenus);
    }

    /**
     * This method gets the DN of the organizational user. Known limitation: it's possible that the
     * authenticated user has multiple organizational users in the same organization. For now this
     * is not supported.
     *
     * @return  The DN of the organizational user.
     *
     * @see     com.cordys.coe.util.cgc.userinfo.IOrganizationInfo#getOrganizationalUser()
     */
    public String getOrganizationalUser()
    {
        return m_sOrganizationalUser;
    }

    /**
     * This method gets the roles for the current organzation. Note: the roles can also have nested
     * roles.
     *
     * @return  The roles for the current organzation. Note: the roles can also have nested roles.
     *
     * @see     com.cordys.coe.util.cgc.userinfo.IOrganizationInfo#getRoles()
     */
    public Map<String, IRoleInfo> getRoles()
    {
        // Always return a new collection because otherwise the callee
        // cannot iterate it in a concurrent-scenario.
        return new LinkedHashMap<String, IRoleInfo>(m_mRoles);
    }

    /**
     * This method gets the list of toolsbars assigned to this user.
     *
     * @return  The list of toolsbars assigned to this user.
     *
     * @see     com.cordys.coe.util.cgc.userinfo.IOrganizationInfo#getToolbars()
     */
    public List<String> getToolbars()
    {
        // Always return a new collection because otherwise the callee
        // cannot iterate it in a concurrent-scenario.
        return new ArrayList<String>(m_lToolbars);
    }

    /**
     * This method returns if the user has the given role.
     *
     * @param   sRoleDN  The DN of the role.
     *
     * @return  true if the user has this role. Otherwise false.
     *
     * @see     com.cordys.coe.util.cgc.userinfo.IOrganizationInfo#hasRole(java.lang.String)
     */
    public boolean hasRole(String sRoleDN)
    {
        boolean bReturn = false;

        if (m_mRoles.containsKey(sRoleDN))
        {
            bReturn = true;
        }
        else
        {
            // It could be possible that a sub role has this role.
            for (IRoleInfo rRole : m_mRoles.values())
            {
                if (rRole.hasRole(sRoleDN))
                {
                    bReturn = true;
                    break;
                }
            }
        }
        return bReturn;
    }

    /**
     * This method gets whether or not this organization is the default organization.
     *
     * @return  Whether or not this organization is the default organization.
     *
     * @see     com.cordys.coe.util.cgc.userinfo.IOrganizationInfo#isDefaultOrganization()
     */
    public boolean isDefaultOrganization()
    {
        return m_bDefaultOrganization;
    }

    /**
     * This method sets wether or not this organization is the default organization.
     *
     * @param  bDefaultOrganization  Whether or not this organization is the default organization.
     *
     * @see    com.cordys.coe.util.cgc.userinfo.IOrganizationInfo#setDefaultOrganization(boolean)
     */
    public void setDefaultOrganization(boolean bDefaultOrganization)
    {
        m_bDefaultOrganization = bDefaultOrganization;
    }

    /**
     * This method sets the description for this user.
     *
     * @param  sDescription  The description for this user.
     *
     * @see    com.cordys.coe.util.cgc.userinfo.IOrganizationInfo#setDescription(java.lang.String)
     */
    public void setDescription(String sDescription)
    {
        m_sDescription = sDescription;
    }

    /**
     * This method sets the DN of the current organization.
     *
     * @param  sDN  The DN of the current organization.
     *
     * @see    com.cordys.coe.util.cgc.userinfo.IOrganizationInfo#setDN(java.lang.String)
     */
    public void setDN(String sDN)
    {
        m_sDN = sDN;
    }

    /**
     * This method sets the DN of the organizational user. Known limitation: it's possible that the
     * authenticated user has multiple organizational users in the same organization. For now this
     * is not supported.
     *
     * @param  sOrganizationalUser  The DN of the organizational user.
     *
     * @see    com.cordys.coe.util.cgc.userinfo.IOrganizationInfo#setOrganizationalUser(java.lang.String)
     */
    public void setOrganizationalUser(String sOrganizationalUser)
    {
        m_sOrganizationalUser = sOrganizationalUser;
    }

    /**
     * This method returns the string representation of the object.
     *
     * @return  The string representation of the object.
     *
     * @see     java.lang.Object#toString()
     */
    @Override public String toString()
    {
        StringBuilder sbReturn = new StringBuilder();

        sbReturn.append("DN: ").append(getDN()).append("\n");
        sbReturn.append("Description: ").append(getDescription()).append("\n");
        sbReturn.append("Organizational user: ").append(getOrganizationalUser()).append("\n");
        sbReturn.append("Default org: ").append(isDefaultOrganization()).append("\n");

        // Do the menus.
        if (m_lMenus.size() > 0)
        {
            sbReturn.append("Menus: ");

            for (Iterator<String> iMenus = m_lMenus.iterator(); iMenus.hasNext();)
            {
                String sMenu = (String) iMenus.next();
                sbReturn.append(sMenu);

                if (iMenus.hasNext())
                {
                    sbReturn.append(", ");
                }
            }
            sbReturn.append("\n");
        }

        // Do the toolbars.
        if (m_lToolbars.size() > 0)
        {
            sbReturn.append("Toolbars: ");

            for (Iterator<String> iToolbars = m_lToolbars.iterator(); iToolbars.hasNext();)
            {
                String sToolbar = (String) iToolbars.next();
                sbReturn.append(sToolbar);

                if (iToolbars.hasNext())
                {
                    sbReturn.append(", ");
                }
            }
            sbReturn.append("\n");
        }

        // Do the roles.
        if (m_mRoles.size() > 0)
        {
            sbReturn.append("Roles:\n");

            for (Iterator<IRoleInfo> iRoles = m_mRoles.values().iterator(); iRoles.hasNext();)
            {
                IRoleInfo riRole = (IRoleInfo) iRoles.next();
                sbReturn.append(riRole.toString(1));
            }
        }

        return sbReturn.toString();
    }
}
