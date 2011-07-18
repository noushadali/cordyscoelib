package com.cordys.coe.util.soap;

import com.eibus.connector.nom.Connector;

/**
 * This class can create new SOAP wrappers.
 *
 * @author  pgussow
 */
public class SOAPWrapperFactory
{
    /**
     * This method creates a new SOAP wrapper based on the given NOM connector.
     *
     * @param   connector  The connector to use.
     *
     * @return  The created wrapper.
     */
    public static ISOAPWrapper createSOAPWrapper(Connector connector)
    {
        return new SOAPWrapper(connector);
    }
}
