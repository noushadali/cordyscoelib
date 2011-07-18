package com.cordys.coe.util.cgc.userinfo;

import com.cordys.coe.util.cgc.CordysGatewayClientException;
import com.cordys.coe.util.cgc.message.CGCMessages;
import com.cordys.coe.util.exceptions.XMLWrapperException;
import com.cordys.coe.util.xml.nom.NamespaceConstants;
import com.cordys.coe.util.xml.nom.XPathHelper;

import com.eibus.xml.nom.Node;

import javax.xml.transform.TransformerException;

/**
 * This class can parse the GetUserInfoResponse based on the DOM classes.
 *
 * @author  pgussow
 */
class UserInfoNOMParser
{
    /**
     * Holds the prefix to use.
     */
    private static final String PRE_LDAP = NamespaceConstants.registerPrefix("ldap11",
                                                                             "http://schemas.cordys.com/1.1/ldap");
    /**
     * Holds the root tag.
     */
    private int m_iGetUserDetailsResponse;

    /**
     * Constructor.
     *
     * @param  iGetUserDetailsResponse  The GetUserDetailsResponse tag.
     */
    private UserInfoNOMParser(int iGetUserDetailsResponse)
    {
        m_iGetUserDetailsResponse = iGetUserDetailsResponse;
    }

    /**
     * This method will do the actual parsing of the GetUserDetails.
     *
     * @param   iGetUserDetailsResponse  The tag pointing to the GetUserDetailsResponse/tuple
     *
     * @return  The parsed UserInfo.
     *
     * @throws  CordysGatewayClientException  In case of any exception.
     */
    public static IUserInfo parse(int iGetUserDetailsResponse)
                           throws CordysGatewayClientException
    {
        IUserInfo uiReturn = null;

        UserInfoNOMParser uidp = new UserInfoNOMParser(iGetUserDetailsResponse);
        uiReturn = uidp.parse();

        return uiReturn;
    }

    /**
     * This method parses the GetUserDetailsResponse..
     *
     * @return  The user info object.
     *
     * @throws  CordysGatewayClientException  In case of any exception.
     */
    public IUserInfo parse()
                    throws CordysGatewayClientException
    {
        IUserInfo uiReturn = new UserInfoImpl();

        try
        {
            int iUser = XPathHelper.selectSingleNode(m_iGetUserDetailsResponse,
                                                     "./" + PRE_LDAP + ":old/" + PRE_LDAP +
                                                     ":user");

            if (iUser == 0)
            {
                throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_USERINFO_XPATH,
                                                       "/old/user");
            }

            // Get the authenticated user.
            String sTemp = XPathHelper.getStringValue(iUser, "./" + PRE_LDAP + ":authuserdn/text()",
                                                      "");

            if (sTemp.length() == 0)
            {
                throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_USERINFO_XPATH,
                                                       "authuserdn/text()");
            }
            uiReturn.setAuthenticatedUser(sTemp);

            // Get the description.
            sTemp = XPathHelper.getStringValue(iUser, "./" + PRE_LDAP + ":description/text()", "");

            if (sTemp.length() == 0)
            {
                throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_USERINFO_XPATH,
                                                       "description/text()");
            }
            uiReturn.setDescription(sTemp);

            // Now parse the organizations.
            int[] aiOrganizations = XPathHelper.selectNodes(iUser,
                                                            "./" + PRE_LDAP + ":organization");

            for (int iCount = 0; iCount < aiOrganizations.length; iCount++)
            {
                int iOrganization = aiOrganizations[iCount];

                IOrganizationInfo oiOrgInfo = parseOrganization(iOrganization);

                uiReturn.addOrganization(oiOrgInfo);
            }
        }
        catch (CordysGatewayClientException cgce)
        {
            throw cgce;
        }
        catch (Exception e)
        {
            throw new CordysGatewayClientException(e, CGCMessages.CGC_ERROR_USERINFO_XML);
        }

        return uiReturn;
    }

    /**
     * This method parses the given organization.
     *
     * @param   eOrganization  The given organziation.
     *
     * @return  The wrapper around the organizational information.
     *
     * @throws  XMLWrapperException           In case of XML related exceptions.
     * @throws  CordysGatewayClientException  In case of general exceptions.
     * @throws  TransformerException          In case of XML related exceptions.
     */
    private IOrganizationInfo parseOrganization(int eOrganization)
                                         throws XMLWrapperException, CordysGatewayClientException,
                                                TransformerException
    {
        IOrganizationInfo oiReturn = new OrganizationInfoImpl();

        // Check for the default organization.
        if ("true".equals(Node.getAttribute(eOrganization, "default", "false")))
        {
            oiReturn.setDefaultOrganization(true);
        }

        // Get the DN of the organization
        String sTemp = XPathHelper.getStringValue(eOrganization, "./" + PRE_LDAP + ":dn/text()",
                                                  "");

        if (sTemp.length() == 0)
        {
            throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_USERINFO_XPATH,
                                                   "organization/dn/text()");
        }

        oiReturn.setDN(sTemp);

        // Get the description
        sTemp = XPathHelper.getStringValue(eOrganization, "./" + PRE_LDAP + ":description/text()",
                                           "");

        if (sTemp.length() == 0)
        {
            throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_USERINFO_XPATH,
                                                   "organization/description/text()");
        }

        oiReturn.setDescription(sTemp);

        // Get the DN of the organizational user.
        sTemp = XPathHelper.getStringValue(eOrganization,
                                           "./" + PRE_LDAP + ":organizationaluser/" + PRE_LDAP +
                                           ":dn/text()", "");

        if (sTemp.length() == 0)
        {
            throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_USERINFO_XPATH,
                                                   "organization/organizationaluser/dn/text()");
        }

        oiReturn.setOrganizationalUser(sTemp);

        // Parse the menus
        int[] nlMenus = XPathHelper.selectNodes(eOrganization, "./" + PRE_LDAP + ":menu");

        for (int iCount = 0; iCount < nlMenus.length; iCount++)
        {
            sTemp = XPathHelper.getStringValue(nlMenus[iCount], "./text()", "");

            if (sTemp.length() > 0)
            {
                oiReturn.addMenu(sTemp);
            }
        }

        // Parse the toolbars
        int[] nlToolbars = XPathHelper.selectNodes(eOrganization, "./" + PRE_LDAP + ":toolbar");

        for (int iCount = 0; iCount < nlToolbars.length; iCount++)
        {
            sTemp = XPathHelper.getStringValue(nlToolbars[iCount], "./text()", "");

            if (sTemp.length() > 0)
            {
                oiReturn.addToolbar(sTemp);
            }
        }

        // Parse the roles.
        int[] nlRoles = XPathHelper.selectNodes(eOrganization, "./" + PRE_LDAP + ":role");

        for (int iCount = 0; iCount < nlRoles.length; iCount++)
        {
            int eRole = nlRoles[iCount];

            IRoleInfo riRole = parseRole(eRole);

            if (riRole != null)
            {
                oiReturn.addRole(riRole);
            }
        }

        return oiReturn;
    }

    /**
     * This method parses the role tag. It will return a filled RoleInfo object.
     *
     * @param   iRole  The role element.
     *
     * @return  The role info.
     *
     * @throws  XMLWrapperException           In case of any XML exception.
     * @throws  CordysGatewayClientException  In case of any exception.
     * @throws  TransformerException          In case of any XML exception.
     */
    private IRoleInfo parseRole(int iRole)
                         throws XMLWrapperException, CordysGatewayClientException,
                                TransformerException
    {
        IRoleInfo riReturn = new RoleInfoImpl();

        // Get the DN of the role.
        riReturn.setRoleDN(Node.getAttribute(iRole, "id"));

        // Get the description
        String sTemp = XPathHelper.getStringValue(iRole, "./" + PRE_LDAP + ":description/text()",
                                                  "");

        if (sTemp.length() == 0)
        {
            throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_USERINFO_XPATH,
                                                   "organization/role/description/text()");
        }

        riReturn.setDescription(sTemp);

        // Parse the optional menus, toolsbars and nested roles.
        // Parse the menus
        int[] aiMenus = XPathHelper.selectNodes(iRole, "./" + PRE_LDAP + ":menu");

        for (int iCount = 0; iCount < aiMenus.length; iCount++)
        {
            sTemp = XPathHelper.getStringValue(aiMenus[iCount], "./text()", "");

            if (sTemp.length() > 0)
            {
                riReturn.addMenu(sTemp);
            }
        }

        // Parse the toolbars
        int[] aiToolbars = XPathHelper.selectNodes(iRole, "./" + PRE_LDAP + ":toolbar");

        for (int iCount = 0; iCount < aiToolbars.length; iCount++)
        {
            sTemp = XPathHelper.getStringValue(aiToolbars[iCount], "./text()", "");

            if (sTemp.length() > 0)
            {
                riReturn.addToolbar(sTemp);
            }
        }

        // Parse the roles.
        int[] aiRoles = XPathHelper.selectNodes(iRole, "./" + PRE_LDAP + ":role");

        for (int iCount = 0; iCount < aiRoles.length; iCount++)
        {
            int iNestedRole = aiRoles[iCount];

            IRoleInfo riRole = parseRole(iNestedRole);

            if (riRole != null)
            {
                riReturn.addNestedRole(riRole);
            }
        }

        return riReturn;
    }
}
