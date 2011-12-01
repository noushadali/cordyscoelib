/**
 *  2003 Cordys R&D B.V. All rights reserved.     The computer program(s) is
 * the proprietary information of Cordys R&D B.V.     and provided under the
 * relevant License Agreement containing restrictions     on use and
 * disclosure. Use is subject to the License Agreement.
 */
package com.cordys.coe.util.general.procstop;

import com.eibus.connector.nom.Connector;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Node;

/**
 * This class only contains a main method. Call this program with 1 argument. The argument must
 * contain the LDAP-DN of the processor you wish to stop.
 */
public class StopProcessor
{
    /**
     * Namespace for the Monitor methodset.
     */
    private static final String NAMESPACE_MONITOR = "http://schemas.cordys.com/1.0/monitor";
    /**
     * Name of the Stop-method.
     */
    private static final String STOP_METHOD = "Stop";
    /**
     * Name of the List-method.
     */
    private static final String LIST_METHOD = "List";

    /**
     * args must contain 1 parameter. The parameter is the DN of the SOAPProcessor that needs to be
     * stopped.
     *
     * @param  args  The parameters of the application.
     */
    public static void main(String[] args)
    {
        if (args.length != 1)
        {
            System.out.println("Usage: java com.cordys.coe.util.general.procstop.StopProcessor <dn>");
            System.exit(-1);
        }
        else
        {
            try
            {
                Connector cCon = Connector.getInstance("StopProcessor");

                if (!cCon.isOpen())
                {
                    cCon.open();
                }

                int iNode = createStopRequest(cCon, args[0]);

                cCon.sendAndWait(iNode, 5000);
            }
            catch (Exception e)
            {
                System.out.println("Exception:\n" + e);
                System.exit(-2);
            }
        }
        System.exit(0);
    }

    /**
     * This method creates the XML-message that will stop the SOAP-processor identified by sDN.
     *
     * @param   cCon  The connector to use.
     * @param   sDN   The LDAP-DN of the SOAP-processor
     *
     * @return  A node that contains the request.
     *
     * @throws  Exception  DOCUMENTME
     */
    private static int createStopRequest(Connector cCon, String sDN)
                                  throws Exception
    {
        int iReturn = 0;

        iReturn = cCon.createSOAPMethod(NAMESPACE_MONITOR, STOP_METHOD);

        Document dDoc = Node.getDocument(iReturn);
        dDoc.createText(sDN, iReturn);
        iReturn = Node.getParent(iReturn);

        // To make sure we get a decent reply, add the list-method to the request
        int iTmpNode = dDoc.createElement(LIST_METHOD, iReturn);
        Node.setAttribute(iTmpNode, "xmlns", NAMESPACE_MONITOR);

        return iReturn;
    }
}
