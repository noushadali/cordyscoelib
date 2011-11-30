/**
 * © 2003 Cordys R&D B.V. All rights reserved.     The computer program(s) is
 * the proprietary information of Cordys R&D B.V.     and provided under the
 * relevant License Agreement containing restrictions     on use and
 * disclosure. Use is subject to the License Agreement.
 */
package com.cordys.coe.util.xmlstore;

import com.cordys.coe.util.soap.ISOAPWrapper;
import com.cordys.coe.util.soap.SOAPException;

import com.eibus.directory.soap.DirectoryException;

import com.eibus.exception.ExceptionGroup;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Find;
import com.eibus.xml.nom.Node;

/**
 * This class will act as a wrapper around the XMLStore methods. This will internally call the
 * SOAPWrapper class for sending requests to BCP.All XXL Store methods are implemented by this class
 *
 * @author  snarayan
 * 
 * @deprecated This class uses the old Find library for XML searching. This is not namespace safe!
 */
public class XMLStoreWrapper
{
    /**
     * Method name for getting XML Object.
     */
    private static final String MTD_GETXMLOBJECT = "GetXMLObject";
    /**
     * Method name for getting Collection of XMLObjects.
     */
    private static final String MTD_GETCOLLECTION = "GetCollection";
    /**
     * Method name for updating XML Object.
     */
    private static final String MTD_UPDATEXMLOBJECT = "UpdateXMLObject";
    /**
     * Namespace for methodset XMLStore.
     */
    private static final String NSP_XMLSTORE = "http://schemas.cordys.com/1.0/xmlstore";
    /**
     * The tuple tag which need to be created.
     */
    private static final String S_TUPLE = "tuple";
    /**
     * The new tag which need to be created.
     */
    private static final String S_NEW = "new";
    /**
     * String for creating a folder node.
     */
    private static final String TAGNAME_FOLDER = "folder";
    /**
     * XMLStore separator character.
     */
    private static final String XMLSTORE_SEPARATOR = "/";
    /**
     * String for creating a key node.
     */
    private static final String TAGNAME_KEY = "key";
    /**
     * Holds the timeout to use for sending messages.
     */
    private long lTimeOut = 60000L;
    /**
     * The receiver name to which the request is being sent.
     */
    private String sReceiver;
    /**
     * Holds the SOAPWrapper Object.
     */
    private ISOAPWrapper swSoap;

    /**
     * Creates a new XMLStoreWrapper object. This will create a new connector instance and pass it
     * to the SOAPWrapper object
     *
     * @param   swSoap  This is the SOAPWrapper Object to send SOAP requests to BCP
     *
     * @throws  SOAPException       Indicates exceptions related to SOAP
     * @throws  DirectoryException  If error happened in the communication with the LDAP server.
     * @throws  ExceptionGroup      Contains all exceptions that occured during creation of a
     *                              connection to the underlying middleware
     */
    public XMLStoreWrapper(ISOAPWrapper swSoap)
                    throws SOAPException, DirectoryException, ExceptionGroup
    {
        if (swSoap != null)
        {
            this.swSoap = swSoap;
        }
        else
        {
            throw new SOAPException("Missing SOAPWrapper object");
        }
    }

    /**
     * Creates a new XMLStoreWrapper object.
     *
     * @param   swSoap     This is the SOAPWrapper Object to send SOAP requests to BCP
     * @param   sReciever  The recever name to which the request has to be sent
     *
     * @throws  DirectoryException  If error happened in the communication with the LDAP server.
     * @throws  ExceptionGroup      Contains all exceptions that occured during creation of a
     *                              connection
     * @throws  SOAPException       Indicates exceptions related to SOAP
     */
    public XMLStoreWrapper(ISOAPWrapper swSoap, String sReciever)
                    throws DirectoryException, ExceptionGroup, SOAPException
    {
        if (swSoap != null)
        {
            this.swSoap = swSoap;
        }
        else
        {
            throw new SOAPException("Missing SOAPWrapper object");
        }

        if ((sReceiver != null) && (sReceiver.length() > 0))
        {
            this.sReceiver = sReciever;
        }
        else
        {
            throw new SOAPException("Missing Receiver name");
        }
    }

    /**
     * This method checks the XML Store to see if the passed on foldername exists. If the folder
     * already exists, true is returned. Otherwise false is returned.
     *
     * @param   sFolderName  The name of the folder to check.
     *
     * @return  If the folder already exists, true is returned. Otherwise false is returned.
     *
     * @throws  SOAPException  DOCUMENTME
     */
    public boolean doesFolderExist(String sFolderName)
                            throws SOAPException
    {
        boolean bReturn = false;

        // Get the parent of the folder
        String sParent = XMLSTORE_SEPARATOR;

        if (sFolderName.lastIndexOf(XMLSTORE_SEPARATOR) > 0)
        {
            sParent = sFolderName.substring(0, sFolderName.lastIndexOf(XMLSTORE_SEPARATOR));
        }

        int iCollection = getCollection(sParent);

        int iFolder = Find.firstMatch(iCollection,
                                      "?<tuple key=\"" + sFolderName + "\" isFolder=\"true\">");

        if (iFolder != 0)
        {
            bReturn = true;
        }

        return bReturn;
    }

    /**
     * This method is used to retrieve all XML Objects available in the XML Store, for a specified
     * key.
     *
     * @param   sFolder  The name of the folder under which the collection of XMLObjects resides.
     *
     * @return  The response node for GetCollecton Method
     *
     * @throws  SOAPException  Indicates exceptions related to SOAP
     */
    public int getCollection(String sFolder)
                      throws SOAPException
    {
        String sRequestUser = swSoap.getUser();

        int iMethod = swSoap.createSoapMethod(null, MTD_GETCOLLECTION, NSP_XMLSTORE, sRequestUser);
        Node.setDataElement(iMethod, TAGNAME_FOLDER, sFolder);

        int iResponse = swSoap.sendAndWait(iMethod, lTimeOut, true);
        return iResponse;
    }

    /**
     * This method will check whether the given object is in the XMLStore. If the object is found it
     * will return the lastmodified time,other wise it will return null
     *
     * @param   sKey  The XMLStroe object key
     *
     * @return  The lastmodified time
     *
     * @throws  SOAPException  Indicates exceptions related to SOAP
     */
    public String getLastModified(String sKey)
                           throws SOAPException
    {
        String sLastModified = null;
        String sRequestUser = swSoap.getUser();

        int iMethod = swSoap.createSoapMethod(null, MTD_GETXMLOBJECT, NSP_XMLSTORE, sRequestUser);

        Node.setDataElement(iMethod, TAGNAME_KEY, sKey);

        int iResponse = swSoap.sendAndWait(iMethod, lTimeOut, true);

        int iTuple = Find.firstMatch(iResponse, "?<tuple>");

        if (iTuple > 0)
        {
            sLastModified = Node.getAttribute(iTuple, "lastModified");
        }
        return sLastModified;
    }

    /**
     * Returns the SOAPWrapper object that is used to send SOAP messages.
     *
     * @return  The SOAPWrapper object used by this XMLStoreWrapper.
     */
    public ISOAPWrapper getSoapWrapper()
    {
        return swSoap;
    }

    /**
     * This method is used to retrieve a specific object from the XML Store, based on a key.
     *
     * @param   sKey  The key based on which the object from the XML Store has to be retrieved. This
     *                should be relative path from the collection folder
     *
     * @return  The response node for the XMLObject
     *
     * @throws  SOAPException  Indicates exceptions related to SOAP
     */
    public int getXMLObject(String sKey)
                     throws SOAPException
    {
        String sRequestUser = swSoap.getUser();

        int iMethod = swSoap.createSoapMethod(null, MTD_GETXMLOBJECT, NSP_XMLSTORE, sRequestUser);

        Node.setDataElement(iMethod, TAGNAME_KEY, sKey);

        int iResponse = swSoap.sendAndWait(iMethod, lTimeOut, true);

        return iResponse;
    }

    /**
     * This method is used to add new objects, update objects and delete objects from the XML Store.
     *
     * @param   iNode          The iNode to be saved in the XMLStore
     * @param   sFolder        The folder names underwhich it is to be stored
     * @param   sKey           The key string for the unique identification of the iNode
     * @param   sLastModified  The last modified time
     *
     * @return  Returns the key for the updated object
     *
     * @throws  SOAPException  Indicates exceptions related to SOAP
     */
    public String updateXMLObject(int iNode, String sFolder, String sKey, String sLastModified)
                           throws SOAPException
    {
        String sReturn = null;
        String sRequestUser = swSoap.getUser();

        int iMethod = swSoap.createSoapMethod(null, MTD_UPDATEXMLOBJECT, NSP_XMLSTORE,
                                              sRequestUser);
        Document dDocument = Node.getDocument(iMethod);
        int iTuple = dDocument.createElement(S_TUPLE);

        Node.setAttribute(iTuple, "key", sFolder + XMLSTORE_SEPARATOR + sKey);
        Node.setAttribute(iTuple, "folder", sFolder);
        Node.appendToChildren(iTuple, iMethod);

        if (sLastModified != null)
        {
            Node.setAttribute(iTuple, "lastModified", sLastModified);
        }

        int iNew = dDocument.createElement(S_NEW);

        Node.appendToChildren(iNode, iNew);
        Node.appendToChildren(iNew, iTuple);

        int iResponse = swSoap.sendAndWait(iMethod, lTimeOut, true);

        if (iResponse > 0)
        {
            sReturn = sFolder + XMLSTORE_SEPARATOR + sKey;
        }

        return sReturn;
    }
}
