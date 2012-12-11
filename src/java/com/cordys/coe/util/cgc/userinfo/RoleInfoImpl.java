package com.cordys.coe.util.cgc.userinfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class wraps the Role information.
 * 
 * @author pgussow
 */
public class RoleInfoImpl implements IRoleInfo
{
    /** Holds all menus for this organization. */
    private List<String> m_lMenus = new ArrayList<String>();
    /** Holds all toolbars for this organization. */
    private List<String> m_lToolbars = new ArrayList<String>();
    /** Holds all roles for this organizations. */
    private Map<String, IRoleInfo> m_mRoles = new LinkedHashMap<String, IRoleInfo>();
    /** Holds the description of the oprganization. */
    private String m_sDescription;
    /** Holds the DN of the role. */
    private String m_sRoleDN;
    /** Holds the parent role Info object. */
    private IRoleInfo m_parent;

    /**
     * Instantiates a new role info impl.
     * 
     * @param parent the parent
     */
    RoleInfoImpl(IRoleInfo parent)
    {
        m_parent = parent;
    }
    
    /**
     * This method gets the parent role Info object.
     * 
     * @return The parent role Info object.
     */
    public IRoleInfo getParent()
    {
        return m_parent;
    }

    /**
     * This method sets the parent role Info object.
     * 
     * @param parent The parent role Info object.
     */
    public void setParent(IRoleInfo parent)
    {
        m_parent = parent;
    }

    /**
     * This method adds a new menu to this role.
     * 
     * @param sMenu The menu to add.
     * @see com.cordys.coe.util.cgc.userinfo.IRoleInfo#addMenu(java.lang.String)
     */
    public void addMenu(String sMenu)
    {
        m_lMenus.add(sMenu);
    }

    /**
     * This method adds a nested role to the current role.
     * 
     * @param riRole The role information.
     * @see com.cordys.coe.util.cgc.userinfo.IRoleInfo#addNestedRole(com.cordys.coe.util.cgc.userinfo.IRoleInfo)
     */
    public void addNestedRole(IRoleInfo riRole)
    {
        m_mRoles.put(riRole.getRoleDN(), riRole);
    }

    /**
     * This method adds a new toolbar to this role.
     * 
     * @param sToolbar The toolbar to add.
     * @see com.cordys.coe.util.cgc.userinfo.IRoleInfo#addToolbar(java.lang.String)
     */
    public void addToolbar(String sToolbar)
    {
        m_lToolbars.add(sToolbar);
    }

    /**
     * This method gets the description for the role.
     * 
     * @return The description for the role.
     * @see com.cordys.coe.util.cgc.userinfo.IRoleInfo#getDescription()
     */
    public String getDescription()
    {
        return m_sDescription;
    }

    /**
     * This method gets the list of menus assigned to this user.
     * 
     * @return The list of menus assigned to this user.
     * @see com.cordys.coe.util.cgc.userinfo.IRoleInfo#getMenus()
     */
    public List<String> getMenus()
    {
        // Always return a new collection because otherwise the callee
        // cannot iterate it in a concurrent-scenario.
        return new ArrayList<String>(m_lMenus);
    }

    /**
     * This method gets the list of nested roles for this role.
     * 
     * @return The list of nested roles for this role.
     * @see com.cordys.coe.util.cgc.userinfo.IRoleInfo#getNestedRoles()
     */
    public Map<String, IRoleInfo> getNestedRoles()
    {
        // Always return a new collection because otherwise the callee
        // cannot iterate it in a concurrent-scenario.
        return new LinkedHashMap<String, IRoleInfo>(m_mRoles);
    }

    /**
     * This method gets the DN of the current role.
     * 
     * @return The DN of the current role.
     * @see com.cordys.coe.util.cgc.userinfo.IRoleInfo#getRoleDN()
     */
    public String getRoleDN()
    {
        return m_sRoleDN;
    }

    /**
     * This method gets the list of toolsbars assigned to this user.
     * 
     * @return The list of toolsbars assigned to this user.
     * @see com.cordys.coe.util.cgc.userinfo.IRoleInfo#getToolbars()
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
     * @param sRoleDN The DN of the role.
     * @return true if the user has this role. Otherwise false.
     * @see com.cordys.coe.util.cgc.userinfo.IRoleInfo#hasRole(java.lang.String)
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
     * This method sets the description for the role.
     * 
     * @param sDescription The description for the role.
     * @see com.cordys.coe.util.cgc.userinfo.IRoleInfo#setDescription(java.lang.String)
     */
    public void setDescription(String sDescription)
    {
        m_sDescription = sDescription;
    }

    /**
     * This method sets the DN of the current role.
     * 
     * @param sRoleDN The DN of the current role.
     * @see com.cordys.coe.util.cgc.userinfo.IRoleInfo#setRoleDN(java.lang.String)
     */
    public void setRoleDN(String sRoleDN)
    {
        m_sRoleDN = sRoleDN;
    }

    /**
     * This method returns the string representation of the object.
     * 
     * @return The string representation of the object.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return toString(0);
    }

    /**
     * This method returns the string representation of the object.
     * 
     * @param iIdent The number of tab char to add after a new line.
     * @return The string representation of the object.
     * @see java.lang.Object#toString()
     */
    public String toString(int iIdent)
    {
        StringBuilder sbReturn = new StringBuilder();

        // Build the prefix
        StringBuilder sbPrefix = new StringBuilder();

        for (int iCount = 0; iCount < iIdent; iCount++)
        {
            sbPrefix.append("\t");
        }

        String sPrefix = sbPrefix.toString();

        // Dump the acual information
        sbReturn.append(sPrefix).append("DN: ").append(getRoleDN()).append("\n");
        sbReturn.append(sPrefix).append("Description: ").append(getDescription()).append("\n");

        // Do the menus.
        if (m_lMenus.size() > 0)
        {
            sbReturn.append(sPrefix).append("Menus: ");

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
            sbReturn.append(sPrefix).append("Toolbars: ");

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
            sbReturn.append(sPrefix).append("Nested roles:\n");

            for (Iterator<IRoleInfo> iRoles = m_mRoles.values().iterator(); iRoles.hasNext();)
            {
                IRoleInfo riRole = (IRoleInfo) iRoles.next();
                sbReturn.append(riRole.toString(iIdent + 1));
            }
        }

        return sbReturn.toString();
    }
}
