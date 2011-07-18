package com.cordys.coe.util.cgc.userinfo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is the implementation of the UserInfo interface.
 *
 * @author  pgussow
 */
class UserInfoImpl
    implements IUserInfo
{
    /**
     * Holds all organziation to which this user has access.
     */
    private Map<String, IOrganizationInfo> m_hmOrganizations = new LinkedHashMap<String, IOrganizationInfo>();
    /**
     * Holds the default organization.
     */
    private IOrganizationInfo m_oiDefaultOrg = null;
    /**
     * Holds the DN of the authenticated user.
     */
    private String m_sAuthenticatedUser;
    /**
     * Holds the description for this user.
     */
    private String m_sDescription;

    /**
     * This method adds the given organization to the list.
     *
     * @param  oiOrgInfo  The organization info.
     *
     * @see    com.cordys.coe.util.cgc.userinfo.IUserInfo#addOrganization(com.cordys.coe.util.cgc.userinfo.IOrganizationInfo)
     */
    public void addOrganization(IOrganizationInfo oiOrgInfo)
    {
        m_hmOrganizations.put(oiOrgInfo.getDN(), oiOrgInfo);

        if ((m_oiDefaultOrg == null) || oiOrgInfo.isDefaultOrganization())
        {
            m_oiDefaultOrg = oiOrgInfo;
        }
    }

    /**
     * This method gets the DN of the authenticated user.
     *
     * @return  The DN of the authenticated user.
     *
     * @see     com.cordys.coe.util.cgc.userinfo.IUserInfo#getAuthenticatedUser()
     */
    public String getAuthenticatedUser()
    {
        return m_sAuthenticatedUser;
    }

    /**
     * This method gets the default organization.
     *
     * @return  The default organization.
     *
     * @see     com.cordys.coe.util.cgc.userinfo.IUserInfo#getDefaultOrganization()
     */
    public IOrganizationInfo getDefaultOrganization()
    {
        return m_oiDefaultOrg;
    }

    /**
     * This method gets the description for this user.
     *
     * @return  The description for this user.
     *
     * @see     com.cordys.coe.util.cgc.userinfo.IUserInfo#getDescription()
     */
    public String getDescription()
    {
        return m_sDescription;
    }

    /**
     * This method gets the list of organizations this user is part of.
     *
     * @return  The list of organizations this user is part of.
     *
     * @see     com.cordys.coe.util.cgc.userinfo.IUserInfo#getOrganizations()
     */
    public List<IOrganizationInfo> getOrganizations()
    {
        // Always return a new collection because otherwise the callee
        // cannot iterate it in a concurrent-scenario.
        return new ArrayList<IOrganizationInfo>(m_hmOrganizations.values());
    }

    /**
     * This method returns if the user somehow has the given role.
     *
     * @param   sOrganization  The current organization.
     * @param   sRoleDN        The DN of the role.
     *
     * @return  true if the user has this role. Otherwise false.
     *
     * @see     com.cordys.coe.util.cgc.userinfo.IUserInfo#hasRole(String, String)
     */
    public boolean hasRole(String sOrganization, String sRoleDN)
    {
        boolean bReturn = false;

        if (m_hmOrganizations.containsKey(sOrganization))
        {
            IOrganizationInfo oiOrg = m_hmOrganizations.get(sOrganization);

            bReturn = oiOrg.hasRole(sRoleDN);
        }

        return bReturn;
    }

    /**
     * This method sets the DN of the authenticated user.
     *
     * @param  sAuthenticatedUser  The DN of the authenticated user.
     *
     * @see    com.cordys.coe.util.cgc.userinfo.IUserInfo#setAuthenticatedUser(java.lang.String)
     */
    public void setAuthenticatedUser(String sAuthenticatedUser)
    {
        m_sAuthenticatedUser = sAuthenticatedUser;
    }

    /**
     * This method sets the description for this user.
     *
     * @param  sDescription  The description for this user.
     *
     * @see    com.cordys.coe.util.cgc.userinfo.IUserInfo#setDescription(java.lang.String)
     */
    public void setDescription(String sDescription)
    {
        m_sDescription = sDescription;
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

        sbReturn.append("Authenticated user: ").append(getAuthenticatedUser()).append("\n");
        sbReturn.append("Description: ").append(getDescription()).append("\n");
        sbReturn.append("Default organization: ").append(getDefaultOrganization().getDN()).append("\n");
        sbReturn.append("-------------------------------").append("\n");
        sbReturn.append("-         Organizations       -").append("\n");
        sbReturn.append("-------------------------------").append("\n");

        for (IOrganizationInfo oiOrg : m_hmOrganizations.values())
        {
            sbReturn.append(oiOrg.toString()).append("\n");
        }

        sbReturn.append("-------------------------------").append("\n");

        return sbReturn.toString();
    }
}
