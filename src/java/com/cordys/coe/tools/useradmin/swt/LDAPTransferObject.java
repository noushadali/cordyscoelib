package com.cordys.coe.tools.useradmin.swt;

import java.io.Serializable;

/**
 * This class wraps LDAP objects being dragged and dropped. It wraps around a DN and can identify
 * several types.
 *
 * @author  pgussow
 */
public class LDAPTransferObject
    implements Serializable
{
    /**
     * Identifies an authenticated user.
     */
    public static final int TYPE_AUTHENTICATED_USER = 0;
    /**
     * Identifies a role.
     */
    public static final int TYPE_ROLE = 1;
    /**
     * Identifies an organization.
     */
    public static final int TYPE_ORGANIZATION = 2;
    /**
     * Holds the type for this LDAP entry.
     */
    private int iType;
    /**
     * Holds the DN of the LDAP entry.
     */
    private String sDN;

    /**
     * Creates a new LDAPTransferObject object.
     *
     * @param  sDN    The DN of the LDAP entry.
     * @param  iType  The type of object.
     */
    public LDAPTransferObject(String sDN, int iType)
    {
        this.sDN = sDN;
        this.iType = iType;
    }

    /**
     * This method gets the DN of the LDAP entry.
     *
     * @return  The DN of the LDAP entry.
     */
    public String getDN()
    {
        return sDN;
    }

    /**
     * This method gets the type for this LDAP entry.
     *
     * @return  The type for this LDAP entry.
     */
    public int getType()
    {
        return iType;
    }

    /**
     * This method sets the DN of the LDAP entry.
     *
     * @param  sDN  The DN of the LDAP entry.
     */
    public void setDN(String sDN)
    {
        this.sDN = sDN;
    }

    /**
     * This method sets the type for this LDAP entry.
     *
     * @param  iType  The type for this LDAP entry.
     */
    public void setType(int iType)
    {
        this.iType = iType;
    }
}
