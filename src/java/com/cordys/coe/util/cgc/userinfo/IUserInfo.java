package com.cordys.coe.util.cgc.userinfo;

import java.util.List;

/**
 * This interface describes the available user information.
 *
 * @author  pgussow
 */
public interface IUserInfo
{
    /**
     * This method adds the given organization to the list.
     *
     * @param  oiOrgInfo  The organization info.
     */
    void addOrganization(IOrganizationInfo oiOrgInfo);

    /**
     * This method gets the DN of the authenticated user.
     *
     * @return  The DN of the authenticated user.
     */
    String getAuthenticatedUser();

    /**
     * This method gets the default organization.
     *
     * @return  The default organization.
     */
    IOrganizationInfo getDefaultOrganization();

    /**
     * This method gets the description for this user.
     *
     * @return  The description for this user.
     */
    String getDescription();

    /**
     * This method gets the list of organizations this user is part of.
     *
     * @return  The list of organizations this user is part of.
     */
    List<IOrganizationInfo> getOrganizations();

    /**
     * This method returns if the user somehow has the given role.
     *
     * @param   sOrganization  The current organization.
     * @param   sRoleDN        The DN of the role.
     *
     * @return  true if the user has this role. Otherwise false.
     */
    boolean hasRole(String sOrganization, String sRoleDN);

    /**
     * This method sets the DN of the authenticated user.
     *
     * @param  sAuthenticatedUser  The DN of the authenticated user.
     */
    void setAuthenticatedUser(String sAuthenticatedUser);

    /**
     * This method sets the description for this user.
     *
     * @param  sDescription  The description for this user.
     */
    void setDescription(String sDescription);
}
