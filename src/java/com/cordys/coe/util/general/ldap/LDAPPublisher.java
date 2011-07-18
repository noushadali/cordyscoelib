package com.cordys.coe.util.general.ldap;

import com.cordys.coe.util.swing.LDAPLogin;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;

import java.io.DataOutputStream;
import java.io.OutputStream;

import java.net.InetAddress;
import java.net.Socket;

/**
 * This class can send an UpdateLDAPCache method to a Cordys installation without being a user in
 * that organization.
 *
 * @author  pgussow
 */
public class LDAPPublisher
{
    /**
     * Holds the LDAP connection to use.
     */
    private LDAPConnection lcConnection;

    /**
     * Creates a new LDAPPublisher object.
     *
     * @param  lcConnection  The LDAP Connection to use.
     */
    public LDAPPublisher(LDAPConnection lcConnection)
    {
        this.lcConnection = lcConnection;
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
            LDAPLogin lLogin = new LDAPLogin(null, true, true);
            lLogin.setVisible(true);

            if (lLogin.isOk())
            {
                LDAPPublisher lp = new LDAPPublisher(lLogin.getLDAPConnection());
                lp.publishChange(new String[]
                                 {
                                     "cn=pgussow,cn=organizational users,o=system,cn=cordys,o=vanenburg.com"
                                 });
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        System.exit(0);
    }

    /**
     * This method publishes that the entries in saDNs have cahnged and that the local LDAP-caches
     * should be cleared.
     *
     * @param   saDNs  The DNs to publish the changes for.
     *
     * @throws  Exception  DOCUMENTME
     */
    public void publishChange(String[] saDNs)
                       throws Exception
    {
        LDAPEntry[] lae = getEventServices();

        for (int iCount = 0; iCount < lae.length; iCount++)
        {
            LDAPEntry leEventService = lae[iCount];

            sendPublishToES(saDNs, leEventService);
        }
    }

    /**
     * This method returns all the eventservices that are in the LDAP.
     *
     * @return  All eventservice soapnodes.
     *
     * @throws  LDAPException  DOCUMENTME
     */
    private LDAPEntry[] getEventServices()
                                  throws LDAPException
    {
        LDAPEntry[] saEventServices = LDAPUtils.searchLDAP(lcConnection,
                                                           LDAPUtils.getRootDN(lcConnection),
                                                           LDAPConnection.SCOPE_SUB,
                                                           "(&(objectclass=bussoapnode)(labeleduri=http://schemas.cordys.com/1.0/eventservice))");
        return saEventServices;
    }

    /**
     * This method returns all the connectionpoints for the event-service processors.
     *
     * @param   leEventService  The eventservice.
     *
     * @return  The URI found.
     *
     * @throws  LDAPException  DOCUMENTME
     */
    private String[] getURIForService(LDAPEntry leEventService)
                               throws LDAPException
    {
        LDAPEntry[] aleCPs = LDAPUtils.searchLDAP(lcConnection, leEventService.getDN(),
                                                  LDAPConnection.SCOPE_SUB,
                                                  "(&(objectclass=busconnectionpoint))");
        String[] saReturn = new String[aleCPs.length];

        for (int iCount = 0; iCount < aleCPs.length; iCount++)
        {
            LDAPEntry leCP = aleCPs[iCount];
            saReturn[iCount] = LDAPUtils.getAttrValue(leCP, "labeleduri");
        }

        return saReturn;
    }

    /**
     * This method sends the actual publish-method to the specific eventservice.
     *
     * @param   saDNs  The DN's to update.
     * @param   sESDN  The DN of the event-service.
     * @param   sURI   The URI of the eventservice.
     *
     * @throws  Exception  DOCUMENTME
     */
    private void send(String[] saDNs, String sESDN, String sURI)
               throws Exception
    {
        String sMessage = "<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                          "<SOAP:Header>" + "<header>" + "<receiver>" +
                          "<component>" + sESDN + "</component>" +
                          "</receiver>" + "<sender>" +
                          "<component>cn=LDAP Processor,cn=LDAP Service,cn=soap nodes,o=system,cn=cordys," +
                          LDAPUtils.getRootDN(lcConnection) + "</component>" +
                          "<reply-to>socket://127.0.0.1:18872/</reply-to>" +
                          "<user>cn=SYSTEM,cn=organizational users,o=system,cn=cordys," +
                          LDAPUtils.getRootDN(lcConnection) + "</user>" +
                          "</sender>" + "<msg-id>UpdateLDAPCache</msg-id>" +
                          "</header>" + "</SOAP:Header>" + "<SOAP:Body>" +
                          "<Publish xmlns=\"http://schemas.cordys.com/1.0/eventservice\">" +
                          "<subject>UpdateLDAPCache</subject>" +
                          "<synchronous>false</synchronous>" + "<event>" +
                          "<UpdateLDAPCache xmlns=\"http://schemas.cordys.com/wcp/system\">";

        for (int iCount = 0; iCount < saDNs.length; iCount++)
        {
            sMessage += "<dn>" + saDNs[iCount] + "</dn>";
        }
        sMessage += "</UpdateLDAPCache></event></Publish></SOAP:Body></SOAP:Envelope>";

        if (sURI.startsWith("socket://"))
        {
            String sTemp = sURI.substring(9);
            String sHost = sTemp.substring(0, sTemp.indexOf(":"));
            String sPort = sTemp.substring(sTemp.indexOf(":") + 1);
            Socket sES = new Socket(InetAddress.getByName(sHost), Integer.parseInt(sPort));
            OutputStream osSocket = sES.getOutputStream();
            DataOutputStream dos = new DataOutputStream(osSocket);
            byte[] baMessage = sMessage.getBytes();
            dos.write(5);
            dos.writeInt(baMessage.length);
            dos.write(baMessage);
            dos.flush();

            dos.close();
            osSocket.close();
            sES.close();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   saDNs
     * @param   leEventService
     *
     * @throws  Exception  DOCUMENTME
     */
    private void sendPublishToES(String[] saDNs, LDAPEntry leEventService)
                          throws Exception
    {
        String[] saURI = getURIForService(leEventService);

        for (int iCount = 0; iCount < saURI.length; iCount++)
        {
            String sURI = saURI[iCount];
            send(saDNs, leEventService.getDN(), sURI);
        }
    }
}
