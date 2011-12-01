/**
 *       2003 Cordys R&D B.V. All rights reserved.
 *      The computer program(s) is the proprietary information of Cordys R&D B.V.
 *      and provided under the relevant License Agreement containing restrictions
 *      on use and disclosure. Use is subject to the License Agreement.
 */
package com.cordys.coe.util.general.ldap;

import com.eibus.directory.soap.LDAPDirectory;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Find;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;

/**
 * Created by IntelliJ IDEA. User: pgussow Date: 14-aug-2003 Time: 16:46:11 To change this template
 * use Options | File Templates.
 */
public class LDAPImporter
{
    /**
     * Holds the connection to the LDAP-server.
     */
    private LDAPConnection lCon;

    /**
     * Constructor.
     *
     * @param  lCon  DOCUMENTME
     */
    public LDAPImporter(LDAPConnection lCon)
    {
        this.lCon = lCon;
    } // LDAPImporter

    /**
     * Main method.
     *
     * @param  args
     */
    public static void main(String[] args)
    {
        try
        {
            LDAPDirectory ldir = new LDAPDirectory("srv-nl-apps7", 389,
                                                   "cn=Directory Manager,o=vanenburg.com",
                                                   "aagmanager");
            LDAPImporter imp = new LDAPImporter(ldir.getConnection());
            Document dDoc = new Document();
            int iNode = dDoc.load("c:/temp/export.xml");
            imp.importContent("cn=Method Set Kanban Custom Repository ,cn=method sets,o=Kanban,cn=cordys,o=vanenburg.com",
                              iNode, true);
        }
        catch (Exception e)
        {
            if (e instanceof LDAPException)
            {
                LDAPException le = (LDAPException) e;
                System.out.println("LDAP Exception:\nErrorcode:" + le.getResultCode() +
                                   "\nMessage: " + le.getLDAPErrorMessage());
            }
            else
            {
                System.out.println("Error: " + e);
            }
        }
        System.exit(0);
    } // main

    /**
     * Imports the passed on XML.
     *
     * @param   sBaseDN     The DN under which the content should be put.
     * @param   iXML        The XML containing the entries.
     * @param   bRecursive  Whether or not to go recursively in the XML.
     *
     * @throws  LDAPException  DOCUMENTME
     */
    public void importContent(String sBaseDN, int iXML, boolean bRecursive)
                       throws LDAPException
    {
        int[] aiEntries = Find.match(iXML, "fChild<LDAPEntry>");
        LDAPEntry eRoot = lCon.read(sBaseDN);

        for (int iCount = 0; iCount < aiEntries.length; iCount++)
        {
            int iEntryNode = aiEntries[iCount];
            LDAPEntry eNewEntry = LDAPUtils.parseXML(eRoot, iEntryNode);

            try
            {
                lCon.add(eNewEntry);
            }
            catch (LDAPException e)
            {
                if (e.getResultCode() == LDAPException.ENTRY_ALREADY_EXISTS)
                {
                    System.out.println("Entry already exists.");
                    eNewEntry = lCon.read(eNewEntry.getDN());
                }
                else
                {
                    throw e;
                }
            }

            if (bRecursive == true)
            {
                importContent(eNewEntry.getDN(), iEntryNode, true);
            }
        }
    } // importContent
} // LDAPImporter
