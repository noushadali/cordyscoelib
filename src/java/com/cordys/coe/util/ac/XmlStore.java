package com.cordys.coe.util.ac;

import com.cordys.coe.util.StringUtils;
import com.cordys.coe.util.soap.SOAPWrapper;
import com.cordys.coe.util.soap.SoapFaultInfo;
import com.cordys.coe.util.xml.nom.XPathHelper;
import com.eibus.util.logger.CordysLogger;
import com.eibus.xml.nom.Node;
import com.eibus.xml.xpath.XPathMetaInfo;

/**
 * This class acts as a wrapper for XMLStore.
 */
class XmlStore implements IXmlStore
{
    /** Holds the logger to use. */
    private static final CordysLogger LOG = CordysLogger.getCordysLogger(XmlStore.class);
    /** Holds the Getxmlobject method name */
    public static final String METHOD_GETXMLOBJECT = "GetXMLObject";
    /** Holds the GetCollection method name */
    public static final String METHOD_GETCOLLECTION = "GetCollection";
    /** Holds the XMLSTORE namespace */
    public static final String NS_XMLSTORE = "http://schemas.cordys.com/1.0/xmlstore";
    /** Holds the XPath meta information */
    private static XPathMetaInfo xmi = new XPathMetaInfo();

    static
    {
        xmi.addNamespaceBinding("ns", NS_XMLSTORE);
    }

    /** Holds the default recursive value. */
    private static String DEFAULT_RECURSIVE_VALUE = "true";
    /** Holds the default detail value. */
    private static String DEFAULT_DETAIL_VALUE = "true";
    /** Holds the connector to use for getting the requests */
    private IConnector connector;

    /**
     * Instantiates a new xml store.
     * 
     * @param connector The connector
     */
    XmlStore(IConnector connector)
    {
        if (connector == null)
        {
            throw new IllegalArgumentException("connector cannot be null");
        }

        this.connector = connector;
    }

    /**
     * @see com.cordys.coe.util.ac.IXmlStore#getXMLObject(java.lang.String, java.lang.String)
     */
    @Override
    public int getXMLObject(String key, String version) throws ACHelperException
    {
        int envelope = 0;
        int response = 0;

        try
        {
            int method = connector.createSOAPMethod(NS_XMLSTORE, METHOD_GETXMLOBJECT);
            int keyNode = Node.createElementWithParentNS("key", key, method);
            if (StringUtils.isSet(version))
            {
                Node.setAttribute(keyNode, "version", version);
            }

            // Store the root of the request to avoid memory leaks
            envelope = Node.getRoot(method);

            if (LOG.isDebugEnabled())
            {
                LOG.debug("Going to retrieve the object " + key + " from the XML store.\nRequest:\n"
                        + Node.writeToString(envelope, false));
            }

            response = connector.sendAndWait(envelope, 30000L);

            if (LOG.isDebugEnabled())
            {
                LOG.debug("Response received:\n" + Node.writeToString(response, false));
            }

            // Check for the SOAP fault
            SoapFaultInfo sfi = SoapFaultInfo.findSoapFault(response);

            if (sfi != null)
            {
                throw new ACHelperException(ACMessages.ERROR_READING_XML_OBJECT_WITH_KEY, key, sfi.toString());
            }
        }
        catch (Exception e)
        {
            if (response != 0)
            {
                Node.delete(response);
            }

            if (e instanceof ACHelperException)
            {
                throw (ACHelperException) e;
            }
            throw new ACHelperException(e, ACMessages.ERROR_WHILE_READING_XML_STORE_FILE, key);
        }
        finally
        {
            if (envelope > 0)
            {
                Node.delete(envelope);
            }
        }

        return response;
    }

    /**
     * @see com.cordys.coe.util.ac.IXmlStore#getCollectionObjectsWithDetail(java.lang.String, java.lang.Boolean, java.lang.String)
     */
    @Override
    public int getCollectionObjectsWithDetail(String location, Boolean recursive, String version) throws ACHelperException
    {
        int envelope = 0;
        int response = 0;

        try
        {
            int method = connector.createSOAPMethod(NS_XMLSTORE, METHOD_GETCOLLECTION);
            int folderNode = Node.createElementWithParentNS("folder", location, method);
            Node.setAttribute(folderNode, "recursive", (recursive != null ? recursive.toString().toLowerCase()
                    : DEFAULT_RECURSIVE_VALUE));
            Node.setAttribute(folderNode, "detail", DEFAULT_DETAIL_VALUE);
            if (StringUtils.isSet(version))
            {
                Node.setAttribute(folderNode, "version", version);
            }

            envelope = Node.getRoot(method);

            if (LOG.isDebugEnabled())
            {
                LOG.debug("Going to get the collection for location " + location + " from the XML store.\nRequest:\n"
                        + Node.writeToString(envelope, false));
            }

            response = connector.sendAndWait(envelope, 30000L);

            if (LOG.isDebugEnabled())
            {
                LOG.debug("Response received:\n" + Node.writeToString(response, false));
            }

            // Check for the SOAP fault
            SoapFaultInfo sfi = SoapFaultInfo.findSoapFault(response);

            if (sfi != null)
            {
                throw new ACHelperException(ACMessages.ERROR_READING_XML_STORE_COLLECTION, location, sfi.toString());
            }
        }
        catch (Exception e)
        {
            if (response != 0)
            {
                Node.delete(response);
            }

            if (e instanceof ACHelperException)
            {
                throw (ACHelperException) e;
            }
            throw new ACHelperException(e, ACMessages.ERROR_WHILE_READING_XML_STORE_COLLECTION, location);
        }
        finally
        {
            if (envelope > 0)
            {
                Node.delete(envelope);
            }
        }

        return response;
    }

    /**
     * @see com.cordys.coe.util.ac.IXmlStore#isFolder(java.lang.String)
     */
    @Override
    public boolean isFolder(String location) throws ACHelperException
    {
        boolean retVal = false;

        // Step 1: The XML store works as a fodler structure. In order to get the details of a folder we need to execute the
        // GetCollection web service on it's parent
        if (location.endsWith("/"))
        {
            location = location.substring(0, location.length() - 1);
        }

        String parent = "/";
        String filename = location;
        if (location.lastIndexOf('/') > 0)
        {
            parent = location.substring(0, location.lastIndexOf('/'));
            filename = location.substring(location.lastIndexOf('/') + 1);
        }
        else if (location.startsWith("/"))
        {
            filename = location.substring(1);
        }

        // Step 2: get the collection information.
        SOAPWrapper sw = connector.createSoapWrapper();
        try
        {
            int r = sw.createSoapMethod(METHOD_GETCOLLECTION, NS_XMLSTORE);

            int f = Node.createElementWithParentNS("folder", parent, r);
            Node.setAttribute(f, "recursive", "false");
            Node.setAttribute(f, "detail", "false");

            int response = sw.sendAndWait(r);

            // Step 3: find the filename in the response and check if the folder is an attribute
            int tuple = XPathHelper.selectSingleNodeDynamic(response, ".//ns:tuple[@name='" + filename + "']", xmi);
            if (tuple != 0)
            {
                retVal = Node.getAttribute(tuple, "isFolder", "false").equalsIgnoreCase("true");
            }
            else if (LOG.isDebugEnabled())
            {
                LOG.debug("Could not find " + filename + " in the response.");
            }
        }
        catch (Exception e)
        {
            if (e instanceof ACHelperException)
            {
                throw (ACHelperException) e;
            }

            throw new ACHelperException(ACMessages.ERROR_CHECKING_IF_LOCATION_IS_A_FOLDER, location);
        }
        finally
        {
            sw.freeXMLNodes();
        }

        return retVal;
    }
}
