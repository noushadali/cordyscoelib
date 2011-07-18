package com.cordys.coe.tools.testtool.methods;

import com.cordys.coe.util.cgc.ICordysGatewayClient;

/**
 * Holds the information about a method.
 *
 * @author  pgussow
 */
public interface IMethodInfo
{
    /**
     * This method composes a new request. This could be done based on the WSDL.
     *
     * @param   cordysGatewayClient  The Cordys gateway client that is used.
     * @param   organizationContext  The organizational context.
     *
     * @return  The request template.
     */
    String composeNewRequest(ICordysGatewayClient cordysGatewayClient, String organizationContext);

    /**
     * This method gets the implementation type for the method.
     *
     * @return  The implementation type for the method.
     */
    String getImplementationType();

    /**
     * This method gets the method set DN for the method.
     *
     * @return  The method set DN for the method.
     */
    String getMethodSetDN();

    /**
     * This method gets the name of the method.
     *
     * @return  The name of the method.
     */
    String getName();

    /**
     * This method gets the namespace for this method.
     *
     * @return  The namespace for this method.
     */
    String getNamespace();

    /**
     * This method sets the implementation type for the method.
     *
     * @param  sImplementationType  The implementation type for the method.
     */
    void setImplementationType(String sImplementationType);

    /**
     * This method sets the method set DN for the method.
     *
     * @param  methodSetDN  The method set DN for the method.
     */
    void setMethodSetDN(String methodSetDN);

    /**
     * This method sets the name of the method.
     *
     * @param  sName  The name of the method.
     */
    void setName(String sName);

    /**
     * This method sets the namespace for this method.
     *
     * @param  sNamespace  The namespace for this method.
     */
    void setNamespace(String sNamespace);
}
