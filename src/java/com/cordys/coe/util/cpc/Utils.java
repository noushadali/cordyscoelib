/**
 *  2003 Cordys R&D B.V. All rights reserved.     The computer program(s) is
 * the proprietary information of Cordys R&D B.V.     and provided under the
 * relevant License Agreement containing restrictions     on use and
 * disclosure. Use is subject to the License Agreement.
 */
package com.cordys.coe.util.cpc;

import com.cordys.coe.util.soap.SOAPWrapper;

import com.eibus.connector.nom.Connector;

import com.eibus.directory.soap.DirectoryException;
import com.eibus.directory.soap.LDAPDirectory;

import com.eibus.exception.ExceptionGroup;
import com.eibus.exception.TimeoutException;

import com.eibus.util.spy.Spy;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Find;
import com.eibus.xml.nom.Node;

import com.novell.ldap.LDAPException;

/**
 * This class contains some utility-methods for CPC.
 *
 * @author  pgussow
 * @author  hvdvlier
 */
public class Utils
{
    /**
     * Holds the name of the connector to use.
     */
    private static final String CONNECTOR_NAME = "CPCUtils";
    /**
     * The name of the GetObjectsByTemplate-method.
     */
    private static final String GETOBJECTSBYTEMPLATE = "GetObjectsByTemplate";
    /**
     * The namespace of the GetObjectsByTemplate-method.
     */
    private static final String GETOBJECTSBYTEMPLATE_NS = "http://schemas.cordys.com/1.0/coboc";
    /**
     * The name of the DeleteObject-method.
     */
    private static final String DELETEOBJECT = "DeleteObject";
    /**
     * The namespace of the DeleteObject-method.
     */
    private static final String DELETEOBJECT_NS = "http://schemas.cordys.com/1.0/coboc";
    /**
     * Ststic reference to the connector object.
     */
    private static Connector cConnector = null;
    /**
     * Category used for sending spy messages.
     */
    private static final String SPY_CATEGORY = "ORC_UTILS_DEBUG";

    /**
     * This method deletes all objects based on a template-id.
     *
     * @param   sTemplateID  The ID of the template.
     *
     * @throws  LDAPException     DOCUMENTME
     * @throws  ExceptionGroup    DOCUMENTME
     * @throws  TimeoutException  DOCUMENTME
     */
    public static void deleteObjectsByTemplateID(String sTemplateID)
                                          throws LDAPException, ExceptionGroup, TimeoutException
    {
        deleteObjectsByTemplateID(sTemplateID, null, null);
    }

    /**
     * This method deletes all objects based on a template-id.
     *
     * @param   sTemplateID    The ID of the template.
     * @param   sOrganization  The organization to use.
     * @param   sUser          The organizational user to use.
     *
     * @throws  LDAPException     DOCUMENTME
     * @throws  ExceptionGroup    DOCUMENTME
     * @throws  TimeoutException  DOCUMENTME
     */
    public static void deleteObjectsByTemplateID(String sTemplateID, String sOrganization,
                                                 String sUser)
                                          throws LDAPException, ExceptionGroup, TimeoutException
    {
        deleteObjectsByTemplate("template_id", sTemplateID, null, null);
    }

    /**
     * This method deletes all objects based on a template-name.
     *
     * @param   sTemplateName  The name of the template.
     *
     * @throws  LDAPException     DOCUMENTME
     * @throws  ExceptionGroup    DOCUMENTME
     * @throws  TimeoutException  DOCUMENTME
     */
    public static void deleteObjectsByTemplateName(String sTemplateName)
                                            throws LDAPException, ExceptionGroup, TimeoutException
    {
        deleteObjectsByTemplateName(sTemplateName, null, null);
    }

    /**
     * This method deletes all objects based on a templatename.
     *
     * @param   sTemplateName  The name of the template.
     * @param   sOrganization  The organization to use.
     * @param   sUser          The organizational user to use.
     *
     * @throws  LDAPException     DOCUMENTME
     * @throws  ExceptionGroup    DOCUMENTME
     * @throws  TimeoutException  DOCUMENTME
     */
    public static void deleteObjectsByTemplateName(String sTemplateName, String sOrganization,
                                                   String sUser)
                                            throws LDAPException, ExceptionGroup, TimeoutException
    {
        deleteObjectsByTemplate("template_name", sTemplateName, null, null);
    }

    /**
     * DOCUMENTME.
     *
     * @param  args  DOCUMENTME
     */
    public static void main(String[] args)
    {
        try
        {
            // deleteObjectsByTemplateID("10430714051447");
            deleteObjectsByTemplateName("/Interpay/PubliceerAcquirer");
            // deleteObjectsByTemplateID("10426393670638" ,
            // "o=CPCSelfStudyCase,cn=cordys,o=vanenburg.com" , "cn=fwiering,cn=organizational
            // users,o=CPCSelfStudyCase,cn=cordys,o=vanenburg.com");
        }
        catch (Exception e)
        {
            System.out.println("Error:\n" + e);
        }
    }

    /**
     * This method deletes all objects based on a template.
     *
     * @param   sTemplateTag    The templatetag (template_id or template_name).
     * @param   sTemplateValue  The value of templatetag.
     * @param   sOrganization   The organization to use.
     * @param   sUser           The organizational user to use.
     *
     * @throws  LDAPException     DOCUMENTME
     * @throws  ExceptionGroup    DOCUMENTME
     * @throws  TimeoutException  DOCUMENTME
     */
    private static void deleteObjectsByTemplate(String sTemplateTag, String sTemplateValue,
                                                String sOrganization, String sUser)
                                         throws LDAPException, ExceptionGroup, TimeoutException
    {
        if (Spy.active)
        {
            Spy.send(SPY_CATEGORY, "Opening connector...");
        }

        Connector cCon = getConnector();

        LDAPDirectory ldir = cCon.getMiddleware().getDirectory();

        if (sOrganization != null)
        {
            if (Spy.active)
            {
                Spy.send(SPY_CATEGORY, "Setting organization to " + sOrganization);
            }
            ldir.setOrganization(sOrganization);
        }

        int iEnvelope = 0;
        int iResponse = 0;

        try
        {
            // Create the method
            int iMethodNode = cCon.createSOAPMethod(GETOBJECTSBYTEMPLATE_NS, GETOBJECTSBYTEMPLATE);
            iEnvelope = Node.getRoot(iMethodNode);

            Document dDoc = Node.getDocument(iEnvelope);

            // Set the request user.
            if (sUser != null)
            {
                if (Spy.active)
                {
                    Spy.send(SPY_CATEGORY, "Replacing user to " + sUser);
                }
                SOAPWrapper.setRequestUser(iEnvelope, sUser);
            }

            // Fill up the parameters
            dDoc.createTextElement(sTemplateTag, sTemplateValue, iMethodNode);

            // Get the objects
            iResponse = cCon.sendAndWait(iEnvelope, 60000 * 5);

            int[] aiTuples = Find.match(iResponse,
                                        "?<" + GETOBJECTSBYTEMPLATE + "Response><tuple>");

            if (Spy.active)
            {
                Spy.send(SPY_CATEGORY, "Tuplecount: " + aiTuples.length);
            }

            // Create the delete-messages
            for (int iCount = 0; iCount < aiTuples.length; iCount++)
            {
                // Find the object id of the template.
                int iObjectNode = Find.firstMatch(aiTuples[iCount], "?<metadata><object_id>");

                if (iObjectNode != 0)
                {
                    int iDelEnvelope = 0;
                    int iDelResponse = 0;

                    try
                    {
                        int iDelMethodNode = cCon.createSOAPMethod(DELETEOBJECT_NS, DELETEOBJECT);
                        iDelEnvelope = Node.getRoot(iDelMethodNode);

                        // Set the user for the request
                        if (sUser != null)
                        {
                            SOAPWrapper.setRequestUser(iDelEnvelope, sUser);
                        }

                        // Add the tuple to the request.
                        Node.appendToChildren(Node.duplicate(aiTuples[iCount]), iDelMethodNode);

                        iDelResponse = cCon.sendAndWait(iDelEnvelope);

                        if (Spy.active)
                        {
                            Spy.send(SPY_CATEGORY,
                                     "Response from CoBOC:\n" +
                                     Node.writeToString(iDelResponse, false));
                        }
                    }
                    finally
                    {
                        if (iDelEnvelope != 0)
                        {
                            Node.delete(iDelEnvelope);
                        }

                        if (iDelResponse != 0)
                        {
                            Node.delete(iDelResponse);
                        }
                    }
                }
            }
        }
        finally
        {
            if (iEnvelope != 0)
            {
                Node.delete(iEnvelope);
            }

            if (iResponse != 0)
            {
                Node.delete(iResponse);
            }
        }
    } // deleteObjectsByTemplate

    /**
     * This method returns the connector that can be used to send messages to Cordys.
     *
     * @return  The connector to use.
     *
     * @throws  ExceptionGroup      DOCUMENTME
     * @throws  DirectoryException  DOCUMENTME
     */
    private static Connector getConnector()
                                   throws ExceptionGroup, DirectoryException
    {
        Connector cReturn = null;

        if (cConnector == null)
        {
            cConnector = Connector.getInstance(CONNECTOR_NAME);

            if (!cConnector.isOpen())
            {
                cConnector.open();
            }
        }
        cReturn = cConnector;

        return cReturn;
    }
} // Utils
