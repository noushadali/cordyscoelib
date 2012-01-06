package com.cordys.coe.util.cgc.userinfo;

import com.cordys.coe.util.cgc.CordysGatewayClientException;
import com.cordys.coe.util.cgc.message.CGCMessages;
import com.cordys.coe.util.xml.dom.NamespaceConstants;
import com.cordys.coe.util.xml.dom.XMLHelper;
import com.cordys.coe.util.xml.dom.XPathHelper;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class can parse the GetUserInfoResponse based on the DOM classes.
 *
 * @author  pgussow
 */
class UserInfoDOMParser
{
    /**
     * Holds the prefix to use.
     */
    private static final String PRE_LDAP = NamespaceConstants.registerPrefix("ldap11",
                                                                             "http://schemas.cordys.com/1.1/ldap");
    /**
     * Holds the root tag.
     */
    private Element m_eGetUserDetailsResponse;

    /**
     * Constructor.
     *
     * @param  eGetUserDetailsResponse  The GetUserDetailsResponse tag.
     */
    private UserInfoDOMParser(Element eGetUserDetailsResponse)
    {
        m_eGetUserDetailsResponse = eGetUserDetailsResponse;
    }

    /**
     * This method will do the actual parsing of the GetUserDetails.
     *
     * @param   eGetUserDetailsResponse  The tag pointing to the GetUserDetailsResponse/tuple
     *
     * @return  The parsed UserInfo.
     *
     * @throws  CordysGatewayClientException  In case of any exceptions
     */
    public static IUserInfo parse(Element eGetUserDetailsResponse)
                           throws CordysGatewayClientException
    {
        IUserInfo uiReturn = null;

        UserInfoDOMParser uidp = new UserInfoDOMParser(eGetUserDetailsResponse);
        uiReturn = uidp.parse();

        return uiReturn;
    }

    /**
     * This method parses the GetUserDetailsResponse..
     *
     * @return  The user info object.
     *
     * @throws  CordysGatewayClientException  In case of any exceptions
     */
    public IUserInfo parse()
                    throws CordysGatewayClientException
    {
        IUserInfo uiReturn = new UserInfoImpl();

        try
        {
            Node nUser = XPathHelper.prSelectSingleNode(m_eGetUserDetailsResponse,
                                                        "./" + PRE_LDAP + ":old/" + PRE_LDAP + ":user");

            if (nUser == null)
            {
                throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_USERINFO_XPATH, "/old/user");
            }

            // Get the authenticated user.
            String sTemp = XMLHelper.prGetData(nUser, "./" + PRE_LDAP + ":authuserdn/text()", "");

            if (sTemp.length() == 0)
            {
                throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_USERINFO_XPATH, "authuserdn/text()");
            }
            uiReturn.setAuthenticatedUser(sTemp);

            // Get the description.
            sTemp = XMLHelper.prGetData(nUser, "./" + PRE_LDAP + ":description/text()", "");

            if (sTemp.length() == 0)
            {
                throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_USERINFO_XPATH, "description/text()");
            }
            uiReturn.setDescription(sTemp);

            // Now parse the organizations.
            NodeList nlOrganizations = XPathHelper.prSelectNodeList(nUser, "./" + PRE_LDAP + ":organization");

            for (int iCount = 0; iCount < nlOrganizations.getLength(); iCount++)
            {
                Element eOrganization = (Element) nlOrganizations.item(iCount);

                IOrganizationInfo oiOrgInfo = parseOrganization(eOrganization);

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
     * @throws  Exception  In case of any exceptions
     */
    private IOrganizationInfo parseOrganization(Element eOrganization)
                                         throws Exception
    {
        IOrganizationInfo oiReturn = new OrganizationInfoImpl();

        // Check for the default organization.
        if (eOrganization.hasAttribute("default") && "true".equals(eOrganization.getAttribute("default")))
        {
            oiReturn.setDefaultOrganization(true);
        }

        // Get the DN of the organization
        String sTemp = XMLHelper.prGetData(eOrganization, "./" + PRE_LDAP + ":dn/text()", "");

        if (sTemp.length() == 0)
        {
            throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_USERINFO_XPATH, "organization/dn/text()");
        }

        oiReturn.setDN(sTemp);

        // Get the description
        sTemp = XMLHelper.prGetData(eOrganization, "./" + PRE_LDAP + ":description/text()", "");

        if (sTemp.length() == 0)
        {
            throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_USERINFO_XPATH,
                                                   "organization/description/text()");
        }

        oiReturn.setDescription(sTemp);

        // Get the DN of the organizational user.
        sTemp = XMLHelper.prGetData(eOrganization, "./" + PRE_LDAP + ":organizationaluser/" + PRE_LDAP + ":dn/text()",
                                    "");

        if (sTemp.length() == 0)
        {
            throw new CordysGatewayClientException(CGCMessages.CGC_ERROR_USERINFO_XPATH,
                                                   "organization/organizationaluser/dn/text()");
        }

        oiReturn.setOrganizationalUser(sTemp);

        // Parse the menus
        NodeList nlMenus = XPathHelper.prSelectNodeList(eOrganization, "./" + PRE_LDAP + ":menu");

        for (int iCount = 0; iCount < nlMenus.getLength(); iCount++)
        {
            sTemp = XMLHelper.prGetData(nlMenus.item(iCount), "./text()", "");

            if (sTemp.length() > 0)
            {
                oiReturn.addMenu(sTemp);
            }
        }

        // Parse the toolbars
        NodeList nlToolbars = XPathHelper.prSelectNodeList(eOrganization, "./" + PRE_LDAP + ":toolbar");

        for (int iCount = 0; iCount < nlToolbars.getLength(); iCount++)
        {
            sTemp = XMLHelper.prGetData(nlToolbars.item(iCount), "./text()", "");

            if (sTemp.length() > 0)
            {
                oiReturn.addToolbar(sTemp);
            }
        }

        // Parse the roles.
        NodeList nlRoles = XPathHelper.prSelectNodeList(eOrganization, "./" + PRE_LDAP + ":role");

        for (int iCount = 0; iCount < nlRoles.getLength(); iCount++)
        {
            Element eRole = (Element) nlRoles.item(iCount);

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
     * @param   eRole  The role element.
     *
     * @return  The role info.
     *
     * @throws  Exception  In case of any exceptions
     */
    private IRoleInfo parseRole(Element eRole)
                         throws Exception
    {
        IRoleInfo riReturn = new RoleInfoImpl();

        // Get the DN of the role.
        riReturn.setRoleDN(eRole.getAttribute("id"));

        // Get the description
        String sTemp = XMLHelper.prGetData(eRole, "./" + PRE_LDAP + ":description/text()", "");

        if (sTemp.length() == 0)
        {
            // We'll use the cn as description
            sTemp = riReturn.getRoleDN().substring(3, riReturn.getRoleDN().indexOf(',', 4) - 1);
        }

        riReturn.setDescription(sTemp);

        // Parse the optional menus, toolsbars and nested roles.
        // Parse the menus
        NodeList nlMenus = XPathHelper.prSelectNodeList(eRole, "./" + PRE_LDAP + ":menu");

        for (int iCount = 0; iCount < nlMenus.getLength(); iCount++)
        {
            sTemp = XMLHelper.prGetData(nlMenus.item(iCount), "./text()", "");

            if (sTemp.length() > 0)
            {
                riReturn.addMenu(sTemp);
            }
        }

        // Parse the toolbars
        NodeList nlToolbars = XPathHelper.prSelectNodeList(eRole, "./" + PRE_LDAP + ":toolbar");

        for (int iCount = 0; iCount < nlToolbars.getLength(); iCount++)
        {
            sTemp = XMLHelper.prGetData(nlToolbars.item(iCount), "./text()", "");

            if (sTemp.length() > 0)
            {
                riReturn.addToolbar(sTemp);
            }
        }

        // Parse the roles.
        NodeList nlRoles = XPathHelper.prSelectNodeList(eRole, "./" + PRE_LDAP + ":role");

        for (int iCount = 0; iCount < nlRoles.getLength(); iCount++)
        {
            Element eNestedRole = (Element) nlRoles.item(iCount);

            IRoleInfo riRole = parseRole(eNestedRole);

            if (riRole != null)
            {
                riReturn.addNestedRole(riRole);
            }
        }

        return riReturn;
    }
}
