package com.cordys.coe.util.ldap;

import com.cordys.coe.util.general.ldap.LDAPUtils;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;

/**
 * DOCUMENTME.
 *
 * @author  $author$
 */
public class LDAPUtil
{
    /**
     * WCP version unknown.
     */
    public static final int WCP_VESION_UNKNOWN = 0;
    /**
     * WCP version 1.0.
     */
    public static final int WCP_VESION_1_0 = 1;
    /**
     * WCP version 1.1.
     */
    public static final int WCP_VESION_1_1 = 2;
    /**
     * WCP version 1.2.
     */
    public static final int WCP_VESION_1_2 = 3;
    /**
     * WCP version 1.3.
     */
    public static final int WCP_VESION_1_3 = 4;
    /**
     * WCP version 1.4.
     */
    public static final int WCP_VESION_1_4 = 5;
    /**
     * WCP version 1.5.
     */
    public static final int WCP_VESION_1_5 = 6;

    /**
     * This method returns the current version number of Cordys based on the ISV-packages in the
     * LDAP-server.
     *
     * @param   lConn  The LDAPConnection to the server to check.
     *
     * @return  An integer identifying the WCP-version.
     */
    public static int getWCPVersion(LDAPConnection lConn)
    {
        int iReturn = WCP_VESION_UNKNOWN;

        try
        {
            LDAPEntry[] results = LDAPUtils.searchLDAP(lConn, LDAPUtils.getRootDN(lConn),
                                                       LDAPConnection.SCOPE_SUB,
                                                       "(|(cn=Software Package Cordys 1.0)(cn=Cordys WCP 1.1)(cn=Cordys WCP 1.2)(cn=Cordys WCP 1.3)(cn=Cordys WCP 1.4))");

            for (int iCount = 0; iCount < results.length; iCount++)
            {
                if (LDAPUtils.checkAttriValueExists(results[iCount], "cn", "Cordys WCP 1.5"))
                {
                    if (iReturn < WCP_VESION_1_5)
                    {
                        iReturn = WCP_VESION_1_5;
                    }
                }
                else if (LDAPUtils.checkAttriValueExists(results[iCount], "cn", "Cordys WCP 1.4"))
                {
                    if (iReturn < WCP_VESION_1_4)
                    {
                        iReturn = WCP_VESION_1_4;
                    }
                }
                else if (LDAPUtils.checkAttriValueExists(results[iCount], "cn", "Cordys WCP 1.3"))
                {
                    if (iReturn < WCP_VESION_1_3)
                    {
                        iReturn = WCP_VESION_1_3;
                    }
                }
                else if (LDAPUtils.checkAttriValueExists(results[iCount], "cn", "Cordys WCP 1.2"))
                {
                    if (iReturn < WCP_VESION_1_2)
                    {
                        iReturn = WCP_VESION_1_2;
                    }
                }
                else if (LDAPUtils.checkAttriValueExists(results[iCount], "cn", "Cordys WCP 1.1"))
                {
                    if (iReturn < WCP_VESION_1_1)
                    {
                        iReturn = WCP_VESION_1_1;
                    }
                }
                else if (LDAPUtils.checkAttriValueExists(results[iCount], "cn",
                                                             "Software Package Cordys 1.0"))
                {
                    if (iReturn < WCP_VESION_1_0)
                    {
                        iReturn = WCP_VESION_1_0;
                    }
                }
            }
        }
        catch (LDAPException e)
        {
        }
        return iReturn;
    }
}
