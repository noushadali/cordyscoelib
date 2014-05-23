package com.cordys.coe.util.ac;

/**
 * Interface to wrap around XML store calls.
 * 
 * @author pgussow
 */
public interface IXmlStore
{
    /**
     * This method fires the GetXMLObject soap request for the key specified.
     * 
     * @param key The key
     * @param version The version
     * @param connector The connector to use for sending the message.
     * @return int contains the GetXMLObject response xml
     * @throws ACHelperException In case of any exceptions
     */
    int getXMLObject(String key, String version) throws ACHelperException;

    /**
     * This method fires the GetCollection soap request for the location.
     * 
     * @param location The location
     * @param recursive The recursive
     * @param version The version
     * @param connector The connector to use for sending the message.
     * @return int contains the GetCollection response xml
     * @throws ACHelperException In case of any exceptions
     */
    int getCollectionObjectsWithDetail(String location, Boolean recursive, String version) throws ACHelperException;

    /**
     * This method checks whether or not the given location is a folder in the XML store or that it is an actual file.
     * 
     * @param location The location to check.
     * @return true, if the location represents a folder
     * @throws ACHelperException In case of any exceptions.
     */
    boolean isFolder(String location) throws ACHelperException;
}
