package com.cordys.coe.util.cgc;

/**
 * Interface for creating a cordys gateway client.
 *
 * @author  jverhaar
 */
public interface ICordysGatewayClientFactory
{
    /**
     * Return a client (the factory is also responsable for creating the gateway).
     *
     * @return  A cordys gateway client.
     */
    ICordysGatewayClient getGatewayClientInstance();
}
