/**
 *      © 2003 Cordys R&D B.V. All rights reserved.
 *      The computer program(s) is the proprietary information of Cordys R&D B.V.
 *      and provided under the relevant License Agreement containing restrictions
 *      on use and disclosure. Use is subject to the License Agreement.
 */
package com.cordys.coe.util.general.ldap;

import com.cordys.coe.exception.GeneralException;

import com.eibus.directory.soap.LDAPDirectory;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Node;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;

/**
 * This class exports all entries in the LDAP server to an XML-file. This class will be used to back
 * up the entire LDAP.
 *
 * @author  pgussow
 */
public class LDAPExporter
{
    /**
     * Holds the connection to the LDAP-server.
     */
    private LDAPConnection lConnection;
    /**
     * Holds the root of the LDAP.
     */
    private String sRootDN;

    /**
     * Constructor.
     *
     * @param   lConnection  DOCUMENTME
     *
     * @throws  LDAPException  DOCUMENTME
     */
    public LDAPExporter(LDAPConnection lConnection)
                 throws LDAPException
    {
        this.lConnection = lConnection;

        if (lConnection != null)
        {
            sRootDN = LDAPUtils.getRootDN(lConnection);
        }
    }

    /**
     * Main method.
     *
     * @param  args  The parameters
     */
    public static void main(String[] args)
    {
        try
        {
            LDAPDirectory ldir = new LDAPDirectory("srv-nl-apps7", 389,
                                                   "cn=Directory Manager,o=vanenburg.com",
                                                   "aagmanager");

            LDAPExporter exp = new LDAPExporter(ldir.getConnection());
            Document dDoc = new Document();
            int iRootNode = dDoc.load("<root/>".getBytes());
            String sDN = "cn=cordys,o=vanenburg.com";
            exp.exportDN(sDN, true, false, iRootNode);
            Node.writeToFile(iRootNode, iRootNode, "c:/temp/srv_nl_apps7.xml", Node.WRITE_PRETTY);
        }
        catch (Exception e)
        {
            System.out.println("Error:" + e);
        }
        System.exit(0);
    }

    /**
     * This method exports the given DN. If bParent is true, then the root-node will be exported
     * too. If bRecursive is true, the entire subtree will also be exported.
     *
     * @param   sDN          The DN of the entry to export
     * @param   bRecursive   Whether or not the method should export the entire tree
     * @param   bParent      Whether or not to include the parent-entry
     * @param   iParentNode  The XMLNode to append the generated XML
     *
     * @throws  Exception
     */
    public void exportDN(String sDN, boolean bRecursive, boolean bParent, int iParentNode)
                  throws Exception
    {
        this.exportDN(new String[] { sDN }, bRecursive, bParent, iParentNode);
    }

    /**
     * This method exports the given DN. If bParent is true, then the root-node will be exported
     * too. If bRecursive is true, the entire subtree will also be exported.
     *
     * @param   saDN         The array containing the DN's to export
     * @param   bRecursive   Whether or not the method should export the entire tree
     * @param   bParent      Whether or not to include the parent-entry
     * @param   iParentNode  The XMLNode to append the generated XML
     *
     * @throws  Exception
     */
    public void exportDN(String[] saDN, boolean bRecursive, boolean bParent, int iParentNode)
                  throws Exception
    {
        LDAPEntry entry = lConnection.read(sRootDN);

        for (int iDNCount = 0; iDNCount < saDN.length; iDNCount++)
        {
            String sDN = saDN[iDNCount];

            if ((sDN != null) && (sDN.length() > 0))
            {
                entry = lConnection.read(sDN);
            }

            exportEntry(entry, bRecursive, bParent, iParentNode);
        }
    }

    /**
     * This method exports the given DN. If bParent is true, then the root-node will be exported
     * too. If bRecursive is true, the entire subtree will also be exported.
     *
     * @param   alEntries    The array containing the DN's to export
     * @param   bRecursive   Whether or not the method should export the entire tree
     * @param   bParent      Whether or not to include the parent-entry
     * @param   iParentNode  The XMLNode to append the generated XML
     *
     * @throws  GeneralException  DOCUMENTME
     */
    public void exportDN(LDAPEntry[] alEntries, boolean bRecursive, boolean bParent,
                         int iParentNode)
                  throws GeneralException
    {
        try
        {
            for (int iCount = 0; iCount < alEntries.length; iCount++)
            {
                LDAPEntry alEntry = alEntries[iCount];
                exportEntry(alEntry, bRecursive, bParent, iParentNode);
            }
        }
        catch (Exception e)
        {
            throw new GeneralException(e, "Error exporting entries.");
        }
    }

    /**
     * This method exports the given DN. If bParent is true, then the root-node will be exported
     * too. If bRecursive is true, the entire subtree will also be exported.
     *
     * @param   entry        The array containing the DN's to export
     * @param   bRecursive   Whether or not the method should export the entire tree
     * @param   bParent      Whether or not to include the parent-entry
     * @param   iParentNode  The XMLNode to append the generated XML
     *
     * @throws  Exception  DOCUMENTME
     */
    private void exportEntry(LDAPEntry entry, boolean bRecursive, boolean bParent, int iParentNode)
                      throws Exception
    {
        Document dDoc = Node.getDocument(iParentNode);
        int iCurrentNode = iParentNode;

        if (bParent == true)
        {
            iCurrentNode = LDAPUtils.entryToXML(entry, dDoc, "LDAPEntry");
            Node.appendToChildren(iCurrentNode, iParentNode);
        }

        LDAPEntry[] laChildren = LDAPUtils.searchLDAP(lConnection, entry.getDN(),
                                                      LDAPConnection.SCOPE_ONE, "(objectclass=*)");

        for (int iCount = 0; iCount < laChildren.length; iCount++)
        {
            LDAPEntry ldapEntry = laChildren[iCount];
            int iEntryNode = LDAPUtils.entryToXML(ldapEntry, dDoc, "LDAPEntry");
            Node.appendToChildren(iEntryNode, iCurrentNode);

            if (bRecursive == true)
            {
                exportEntry(ldapEntry, true, false, iEntryNode);
            }
        }
    }
}
