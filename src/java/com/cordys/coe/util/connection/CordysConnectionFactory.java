package com.cordys.coe.util.connection;

import com.cordys.coe.util.config.IConfiguration;
import com.cordys.coe.util.config.IWebGatewayConfiguration;
import com.cordys.coe.util.connection.impl.WebGatewayConnection;

/**
 * This factory can be used to create Cordys connections based on a certain configuration.
 *
 * @author  pgussow
 */
public class CordysConnectionFactory
{
    /**
     * This method creates a Cordys connection based on the given configuration.
     *
     * @param   cConfig          The configuration.
     * @param   sOrganization    The organization to connect with.
     * @param   bCheckSoapFault  Whether or not to check for soap faults.
     *
     * @return  The new connection.
     *
     * @throws  CordysConnectionException  Thrown if the connection could not be created.
     */
    public static ICordysConnection createCordysConnection(IConfiguration cConfig,
                                                           String sOrganization,
                                                           boolean bCheckSoapFault)
                                                    throws CordysConnectionException
    {
        return createCordysConnection(cConfig, sOrganization, bCheckSoapFault, true);
    }
    
    /**
     * This method creates a Cordys connection based on the given configuration.
     *
     * @param   cConfig          The configuration.
     * @param   sOrganization    The organization to connect with.
     * @param   bCheckSoapFault  Whether or not to check for soap faults.
     * @param   bConnect         If <code>true</code> automatically connect to the server.
     *
     * @return  The new connection.
     *
     * @throws  CordysConnectionException  Thrown if the connection could not be created.
     */
    public static ICordysConnection createCordysConnection(IConfiguration cConfig,
                                                           String sOrganization,
                                                           boolean bCheckSoapFault,
                                                           boolean bConnect)
                                                    throws CordysConnectionException
    {    
        ICordysConnection ccReturn = null;

        if (cConfig instanceof IWebGatewayConfiguration)
        {
            IWebGatewayConfiguration wgc = (IWebGatewayConfiguration) cConfig;
            ccReturn = new WebGatewayConnection(wgc, bCheckSoapFault, bConnect);
        }
        else
        {
            throw new CordysConnectionException(CordysConnectionException.EC_UNSUPPORTED_CONFIGURATION,
                                                "Unsupported cordys connection.");
        }
        ccReturn.setOrganization(sOrganization);

        return ccReturn;
    }
}
