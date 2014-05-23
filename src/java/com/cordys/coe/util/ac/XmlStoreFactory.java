package com.cordys.coe.util.ac;

/**
 * A factory for creating XMLStore objects.
 */
public class XmlStoreFactory
{
    /**
     * This method creates the XML store object to use for communicating with the Xml Store.
     * 
     * @param connector The connector to use
     * @return The XML store object to use.
     */
    public static IXmlStore create(IConnector connector)
    {
        return new XmlStore(connector);
    }

}
