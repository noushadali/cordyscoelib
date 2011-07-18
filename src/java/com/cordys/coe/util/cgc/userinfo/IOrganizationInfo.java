package com.cordys.coe.util.cgc.userinfo;

import java.util.List;
import java.util.Map;

/**
 * This interface describes the information about an organization.
 *
 * @author  pgussow
 */
public interface IOrganizationInfo
{
    /**
     * This method adds a new menu to this role.
     *
     * @param  sMenu  The menu to add.
     */
    void addMenu(String sMenu);

    /**
     * This method adds a new role to this organization.
     *
     * @param  riRole  The toolbar to add.
     */
    void addRole(IRoleInfo riRole);

    /**
     * This method adds a new toolbar to this organization.
     *
     * @param  sToolbar  The toolbar to add.
     */
    void addToolbar(String sToolbar);

    /**
     * This method gets the description for this user.
     *
     * @return  The description for this user.
     */
    String getDescription();

    /**
     * This method gets the DN of the current organization.
     *
     * @return  The DN of the current organization.
     */
    String getDN();

    /**
     * This method gets the list of menus assigned to this user.
     *
     * @return  The list of menus assigned to this user.
     */
    List<String> getMenus();

    /**
     * This method gets the DN of the organizational user. Known limitation: it's possible that the
     * authenticated user has multiple organizational users in the same organization. For now this
     * is not supported.
     *
     * @return  The DN of the organizational user.
     */
    String getOrganizationalUser();

    /**
     * This method gets the roles for the current organzation. Note: the roles can also have nested
     * roles.
     *
     * @return  The roles for the current organzation. Note: the roles can also have nested roles.
     */
    Map<String, IRoleInfo> getRoles();

    /**
     * This method gets the list of toolsbars assigned to this user.
     *
     * @return  The list of toolsbars assigned to this user.
     */
    List<String> getToolbars();

    /**
     * This method returns if the user has the given role.
     *
     * @param   sRoleDN  The DN of the role.
     *
     * @return  true if the user has this role. Otherwise false.
     */
    boolean hasRole(String sRoleDN);

    /**
     * This method gets whether or not this organization is the default organization.
     *
     * @return  Whether or not this organization is the default organization.
     */
    boolean isDefaultOrganization();

    /**
     * This method sets wether or not this organization is the default organization.
     *
     * @param  bDefaultOrganization  Whether or not this organization is the default organization.
     */
    void setDefaultOrganization(boolean bDefaultOrganization);

    /**
     * This method sets the description for this user.
     *
     * @param  sDescription  The description for this user.
     */
    void setDescription(String sDescription);

    /**
     * This method sets the DN of the current organization.
     *
     * @param  sDN  The DN of the current organization.
     */
    void setDN(String sDN);

    /**
     * This method sets the DN of the organizational user. Known limitation: it's possible that the
     * authenticated user has multiple organizational users in the same organization. For now this
     * is not supported.
     *
     * @param  sOrganizationalUser  The DN of the organizational user.
     */
    void setOrganizationalUser(String sOrganizationalUser);
}
