package com.cordys.coe.util.cgc.config;

import org.w3c.dom.Node;

/**
 * This interface describes the methods needed for SSO based authentication.
 *
 * @author  pgussow
 */
public interface ISSOAuthentication extends IUsernamePasswordAuthentication
{
    /**
     * This method gets the SAML token for the given user.
     *
     * @return  The SAML token for the given user.
     */
    Node getSAMLToken();

    /**
     * This method sets the SAML token for the given user.
     *
     * @param  nSAMLToken  The SAML token for the given user.
     */
    void setSAMLToken(Node nSAMLToken);
}
