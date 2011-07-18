package com.cordys.coe.util.connection;

import com.eibus.directory.soap.LDAPDirectory;

/**
 * This interface wraps around native connections to the Cordys bus.
 *
 * @author  pgussow
 */
public interface INativeConnection extends ICordysConnection
{
    /**
     * This method gets the used LDAP directory.
     *
     * @return  The used LDAP directory.
     */
    LDAPDirectory getLDAPDirectory();
}
