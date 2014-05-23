package com.cordys.coe.util.ac;

import com.cordys.coe.util.soap.SOAPWrapper;

/**
 * Holds the Interface IConnector.
 */
public interface IConnector
{
    /**
     * This method gets the deafult organization to use for sending a message.
     * 
     * @return The deafult organization to use for sending a message.
     */
    String getDefaultOrganization();

    /**
     * This method sets the deafult organization to use for sending a message.
     * 
     * @param defaultOrganization The deafult organization to use for sending a message.
     */
    void setDefaultOrganization(String defaultOrganization);

    /**
     * This method gets the default username to use for sending requests.
     * 
     * @return The default username to use for sending requests.
     */
    String getDefaultUser();

    /**
     * This method sets the default username to use for sending requests.
     * 
     * @param defaultuser The default username to use for sending requests.
     */
    void setDefaultUser(String defaultuser);

    /**
     * This method creates the soap method based on the given operation and namespace.
     * 
     * @param namespace The namespace of the operation/
     * @param operation The operation name.
     * @return The int pointing to the operation XML.
     * @throws ACHelperException In case of any exceptions.
     */
    int createSOAPMethod(String namespace, String operation) throws ACHelperException;

    /**
     * This method creates the soap method based on the given operation and namespace.
     * 
     * @param userDN The DN of the user to use.
     * @param orgDN The DN of the organization.
     * @param namespace The namespace of the operation/
     * @param operation The operation name.
     * @return The int pointing to the operation XML.
     * @throws ACHelperException In case of any exceptions.
     */
    int createSOAPMethod(String userDN, String orgDN, String namespace, String operation) throws ACHelperException;

    /**
     * This method sends the given envelope using the given timeout.
     * 
     * @param envelope The envelope to send.
     * @param timeout The timeout to use.
     * @return The response XML.
     * @throws ACHelperException In case of any exceptions.
     */
    int sendAndWait(int envelope, long timeout) throws ACHelperException;

    /**
     * This method sends the given envelope using the given timeout.
     * 
     * @param envelope The envelope to send.
     * @param timeout The timeout to use.
     * @param checkSoapFault Whether or not to check the response for a SOAP fault.
     * @return The response XML.
     * @throws ACHelperException In case of any exceptions.
     */
    int sendAndWait(int envelope, long timeout, boolean checkSoapFault) throws ACHelperException;

    /**
     * This method creates a new SOAP wrapper around this connector.
     * 
     * @return The soap wrapper.
     */
    SOAPWrapper createSoapWrapper();
}
