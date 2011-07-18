package com.cordys.coe.util.cgc.userinfo;

import java.util.List;
import java.util.Map;

/**
 * This interface describes the information.
 *
 * @author  $author$
 */
public interface IRoleInfo
{
    /**
     * This method adds a new menu to this role.
     *
     * @param  sMenu  The menu to add.
     */
    void addMenu(String sMenu);

    /**
     * This method adds a nested role to the current role.
     *
     * @param  riInfo  The role information.
     */
    void addNestedRole(IRoleInfo riInfo);

    /**
     * This method adds a new toolbar to this role.
     *
     * @param  sToolbar  The toolbar to add.
     */
    void addToolbar(String sToolbar);

    /**
     * This method gets the description for the role.
     *
     * @return  The description for the role.
     */
    String getDescription();

    /**
     * This method gets the list of menus assigned to this user.
     *
     * @return  The list of menus assigned to this user.
     */
    List<String> getMenus();

    /**
     * This method gets the list of nested roles for this role.
     *
     * @return  The list of nested roles for this role.
     */
    Map<String, IRoleInfo> getNestedRoles();

    /**
     * This method gets the DN of the current role.
     *
     * @return  The DN of the current role.
     */
    String getRoleDN();

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
     * This method sets the description for the role.
     *
     * @param  sDescription  The description for the role.
     */
    void setDescription(String sDescription);

    /**
     * This method sets the DN of the current role.
     *
     * @param  sRoleDN  The DN of the current role.
     */
    void setRoleDN(String sRoleDN);

    /**
     * This method returns the string representation of the object.
     *
     * @param   iIdent  The number of tab char to add after a new line.
     *
     * @return  The string representation of the object.
     *
     * @see     java.lang.Object#toString()
     */
    String toString(int iIdent);
}
