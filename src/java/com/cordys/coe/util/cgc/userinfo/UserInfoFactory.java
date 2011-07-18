package com.cordys.coe.util.cgc.userinfo;

import com.cordys.coe.util.cgc.CordysGatewayClientException;

import org.w3c.dom.Element;

/**
 * This factory class can create the user info based on either NOM or DOM.
 *
 * @author  pgussow
 */
public class UserInfoFactory
{
    /**
     * This method creates the user info object based on the GetUserDetailsResponse XML.
     *
     * @param   eGetUserDetailsResponse  The tag pointing to the GetUserDetailsResponse/tuple
     *                                   element.
     *
     * @return  The UserInfo object that can be used.
     *
     * @throws  CordysGatewayClientException  In case of any exceptions.
     */
    public static IUserInfo createUserInfo(Element eGetUserDetailsResponse)
                                    throws CordysGatewayClientException
    {
        IUserInfo uiReturn = null;

        uiReturn = UserInfoDOMParser.parse(eGetUserDetailsResponse);

        return uiReturn;
    }

    /**
     * This method creates the user info object based on the GetUserDetailsResponse XML.
     *
     * @param   iGetUserDetailsResponse  The tag pointing to the GetUserDetailsResponse element.
     *
     * @return  The UserInfo object that can be used.
     *
     * @throws  CordysGatewayClientException  In case of any exceptions.
     */
    public static IUserInfo createUserInfo(int iGetUserDetailsResponse)
                                    throws CordysGatewayClientException
    {
        IUserInfo uiReturn = null;

        uiReturn = UserInfoNOMParser.parse(iGetUserDetailsResponse);

        return uiReturn;
    }
}
