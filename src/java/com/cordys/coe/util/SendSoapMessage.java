package com.cordys.coe.util;

import com.cordys.coe.util.soap.ISOAPWrapper;
import com.cordys.coe.util.soap.SOAPWrapper;
import com.cordys.coe.util.ssm.ISendSoapMessageOutputHandler;
import com.cordys.coe.util.xml.nom.XPathHelper;

import com.eibus.connector.nom.Connector;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Find;
import com.eibus.xml.nom.Node;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import java.text.DateFormat;

import java.util.Date;

/**
 * This class can be used to send soap messages from the command prompt.
 *
 * @author  tveldhui, pgussow
 * 
 * @deprecated This class uses the old Find pattern instead of proper XPaths
 */
public class SendSoapMessage
{
    /**
     * Creates a new SendSoapMessage object.
     */
    public SendSoapMessage()
    {
    }

    /**
     * Main method. This method reads the inputfile and
     *
     * @param  saArgs  The commandline arguments
     */
    public static void main(String[] saArgs)
    {
        boolean bKeepLog = false;
        String sLogPath = "";
        int iSoapMessage = 0;
        int iResponse = 0;

        if ((saArgs.length < 1) || (saArgs.length > 2))
        {
            System.exit(-1);
        }

        try
        {
            if (saArgs.length == 2)
            {
                bKeepLog = true;
                sLogPath = saArgs[1];
            }

            // Open the client connector
            Connector cCon = Connector.getInstance("SendSoapMessage Connector");

            if (!cCon.isOpen())
            {
                cCon.open();
            }

            ISOAPWrapper swSoap = new SOAPWrapper(cCon);

            // Load the configuration-file
            Document dDoc = new Document();
            int iFileNode = dDoc.load(saArgs[0]);

            // Find all the messages to execute
            int[] iaExecuteNode = Find.match(iFileNode, "fChild<execute>");
            System.out.println("# of methods to execute : " + iaExecuteNode.length);

            for (int iCount = 0; iCount < iaExecuteNode.length; iCount++)
            {
                // Read the configuration data
                long lTimeOut = Long.parseLong(Node.getAttribute(iaExecuteNode[iCount], "timeout",
                                                                 "30000"));
                int iUserContextNode = Find.firstMatch(iaExecuteNode[iCount],
                                                       "fChild<usercontext>");
                String sUserContext = null;

                if (iUserContextNode != 0)
                {
                    sUserContext = Node.getDataWithDefault(iUserContextNode, null);
                }

                int iOrgContextNode = Find.firstMatch(iaExecuteNode[iCount], "fChild<orgcontext>");
                String sOrgContext = null;

                if (iOrgContextNode != 0)
                {
                    sOrgContext = Node.getDataWithDefault(iOrgContextNode, null);
                }
                else
                {
                    throw new Exception("Organizational context must be filled.");
                }

                int iMethodNode = Find.firstMatch(iaExecuteNode[iCount], "?<method>");
                String sMethod = null;

                if (iMethodNode != 0)
                {
                    sMethod = Node.getDataWithDefault(iMethodNode, null);
                }
                else
                {
                    throw new Exception("Method must be filled.");
                }

                int iNsNode = Find.firstMatch(iaExecuteNode[iCount], "fChild<ns>");
                String sNamespace = null;

                if (iNsNode != 0)
                {
                    sNamespace = Node.getDataWithDefault(iNsNode, null);
                }
                else
                {
                    throw new Exception("Namespace must be filled.");
                }

                int iDNNode = Find.firstMatch(iaExecuteNode[iCount], "fChild<dn>");
                String sSoapProcessorDN = null;

                if (iDNNode != 0)
                {
                    sSoapProcessorDN = Node.getDataWithDefault(iDNNode, null);
                }

                int iBodyNode = Find.firstMatch(iaExecuteNode[iCount], "fChild<body>");

                // Initialize the output handler if applicable
                ISendSoapMessageOutputHandler ssmohOutputHandler = null;
                String sClass = XPathHelper.getStringValue(iaExecuteNode[iCount],
                                                           "./outputhandler/class/text()", "");

                if ((sClass != null) && (sClass.length() > 0))
                {
                    ssmohOutputHandler = (ISendSoapMessageOutputHandler) Class.forName(sClass)
                                                                              .newInstance();

                    int iOHParams = XPathHelper.selectSingleNode(iaExecuteNode[iCount],
                                                                 "./outputhandler/parameters");
                    ssmohOutputHandler.configure(iOHParams, new File(sLogPath));
                }

                if ((sNamespace != null) && (sMethod != null))
                {
                    if (sOrgContext != null)
                    {
                        cCon.getMiddleware().getDirectory().setOrganization(sOrgContext);
                    }

                    try
                    {
                        iSoapMessage = swSoap.createSoapMethod(sSoapProcessorDN, sMethod,
                                                               sNamespace);

                        // Set the attributes that are set on the method.
                        int iNrOfAttributes = Node.getNumAttributes(iMethodNode);

                        for (int iAttrCount = 0; iAttrCount < iNrOfAttributes; iAttrCount++)
                        {
                            String sAttrName = Node.getAttributeName(iMethodNode, iAttrCount);
                            Node.setAttribute(iSoapMessage, sAttrName,
                                              Node.getAttribute(iMethodNode, sAttrName));
                        }

                        int iCurrentNode = Node.getFirstChild(iBodyNode);

                        while (iCurrentNode != 0)
                        {
                            Node.appendToChildren(Node.duplicate(iCurrentNode), iSoapMessage);
                            iCurrentNode = Node.getNextSibling(iCurrentNode);
                        }

                        if (sUserContext != null)
                        {
                            SOAPWrapper.setRequestUser(Node.getRoot(iSoapMessage), sUserContext);
                        }
                        swSoap.setTimeOut(lTimeOut);
                        iResponse = swSoap.sendAndWait(iSoapMessage);

                        if (ssmohOutputHandler != null)
                        {
                            if (!ssmohOutputHandler.processMethodResponse(iResponse))
                            {
                                if (bKeepLog)
                                {
                                    writeLogFile(iSoapMessage, iResponse, sLogPath);
                                }
                            }
                        }
                        else if (bKeepLog)
                        {
                            writeLogFile(iSoapMessage, iResponse, sLogPath);
                        }
                    }
                    catch (Exception e)
                    {
                        if (bKeepLog)
                        {
                            writeLogFile(iSoapMessage, iResponse, sLogPath, e);
                        }
                    }
                    finally
                    {
                        swSoap.freeXMLNodes();
                    }
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("Error in input file");
            e.printStackTrace();
        }
        System.exit(0);
    }

    /**
     * DOCUMENTME.
     *
     * @param  iSOAPEnvelope  DOCUMENTME
     * @param  sUser          DOCUMENTME
     */
    public static void setRequestUser(int iSOAPEnvelope, String sUser)
    {
        int iNewHeader = Find.firstMatch(iSOAPEnvelope, "?<SOAP:Header><header><sender>");

        if (iNewHeader != 0)
        {
            int iTmp = Find.firstMatch(iNewHeader, "?<user>");

            if (iTmp != 0)
            {
                Node.unlink(iTmp);
                Node.delete(iTmp);
            }
            Node.getDocument(iNewHeader).createTextElement("user", sUser, iNewHeader);
        }
    }

    /**
     * This method writes the request and response messages to the logfile.
     *
     * @param  iRequest   The request XML.
     * @param  iResponse  The response XML.
     * @param  sLogPath   The log-directory.
     */
    public static void writeLogFile(int iRequest, int iResponse, String sLogPath)
    {
        writeLogFile(iRequest, iResponse, sLogPath, null);
    }

    /**
     * This method writes the request andresponse messages to the logfile and also the passed on
     * exception.
     *
     * @param  iRequest    The request XML.
     * @param  iResponse   The response XML.
     * @param  sLogPath    The log-directory.
     * @param  eException  The exception to log.
     */
    public static void writeLogFile(int iRequest, int iResponse, String sLogPath,
                                    Exception eException)
    {
        try
        {
            Date dDatum = new Date();
            DateFormat dfDate = DateFormat.getDateInstance();
            String sDate = dfDate.format(dDatum);
            PrintWriter out = new PrintWriter(new FileWriter(sLogPath + File.separator + sDate +
                                                             ".log", true));
            out.println();
            out.println(" ****************************************************************************");
            out.println(" " + dDatum.toString());
            out.println(" ****************************************************************************");
            out.println();
            out.println(" Request send: ");
            out.println(" " + Node.writeToString(iRequest, false));
            out.println();
            out.println(" Response received ");

            if (eException != null)
            {
                out.println(" SoapRequest returned an SoapFault:");
                eException.printStackTrace(out);
            }
            else
            {
                out.println(" " + Node.writeToString(iResponse, false));
            }
            out.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
