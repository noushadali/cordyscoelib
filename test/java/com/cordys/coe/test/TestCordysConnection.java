package com.cordys.coe.test;

import com.cordys.coe.util.config.ConfigurationFactory;
import com.cordys.coe.util.config.IConfiguration;
import com.cordys.coe.util.connection.CordysConnectionFactory;
import com.cordys.coe.util.connection.ICordysConnection;
import com.cordys.coe.util.xml.dom.NiceDOMWriter;

import org.w3c.dom.Node;

/**
 * DOCUMENTME.
 *
 * @author  $author$
 */
public class TestCordysConnection
{
    /**
     * Main method.
     *
     * @param  saArguments  The commandline arguments.
     */
    public static void main(String[] saArguments)
    {
        try
        {
            /*
             * IConfiguration cConfig =
             * ConfigurationFactory.createNewNativeConfiguration("srv-nl-ces20",
             *                                                       "srv-nl-ces20",
             *                                                           3899,
             *                                                     "cn=Directory
             * Manager,o=vanenburg.com",
             *               "ldap123",
             *              "cn=cordys,o=vanenburg.com",
             *                               false);/*/
            IConfiguration cConfig = ConfigurationFactory.createNewWebGatewayConfiguration("srv-nl-ces20_wg",
                                                                                           "srv-nl-ces20",
                                                                                           80,
                                                                                           "/cordys/com.eibus.web.soap.Gateway.wcp",
                                                                                           "pgussow",
                                                                                           "unknown",
                                                                                           "NTDOM");

            // */
            ICordysConnection ccConn = CordysConnectionFactory.createCordysConnection(cConfig,
                                                                                      "o=system,cn=cordys,o=vanenburg.com",
                                                                                      true);
            Node nMethod = ccConn.createSoapMethod("List", "http://schemas.cordys.com/1.0/monitor");

            // Get the Envelope.
            Node eEnvelope = ccConn.getEnvelope(nMethod);

            System.out.println("Request:\n" + NiceDOMWriter.write(eEnvelope));

            Node eResponse = ccConn.sendAndWait(nMethod);

            System.out.println("Response:\n" + NiceDOMWriter.write(eResponse));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
