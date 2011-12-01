/**
 *       2003 Cordys R&D B.V. All rights reserved.
 *      The computer program(s) is the proprietary information of Cordys R&D B.V.
 *      and provided under the relevant License Agreement containing restrictions
 *      on use and disclosure. Use is subject to the License Agreement.
 */
package com.cordys.coe.util.general.ldap;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Find;
import com.eibus.xml.nom.Node;

import com.novell.ldap.*;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

/**
 * This class contains static methods for use on LDAPEntries.
 *
 * @author  pgussow
 */
public class LDAPUtils
{
    /**
     * vars to define the LDAP Commands.
     */
    public static final int LDAPCOMMAND_DELETE = 1;
    /**
     * DOCUMENTME.
     */
    public static final int LDAPCOMMAND_DELETE_RECURSIVE = 2;
    /**
     * DOCUMENTME.
     */
    public static final int LDAPCOMMAND_DELETE_ATTRIBUTE = 3;
    /**
     * DOCUMENTME.
     */
    public static final int LDAPCOMMAND_ADD = 4;
    /**
     * DOCUMENTME.
     */
    public static final int LDAPCOMMAND_ADD_ATTRIBUTE = 5;
    /**
     * DOCUMENTME.
     */
    public static final int LDAPCOMMAND_REPLACE_ATTRIBUTE = 6;

    /**
     * This method adds the given entry to LDAP.
     *
     * @param   lConn  The LDAPConnection to use for manipulating the entries
     * @param   entry  The LDAP-entry to added.
     *
     * @throws  LDAPException  if an error occured working with LDAP.
     */
    public static void addEntry(LDAPConnection lConn, LDAPEntry entry)
                         throws LDAPException
    {
        lConn.add(entry);
    } // addEntry

    /**
     * This method removes an attribute form a LDAP entry.
     *
     * @param   lConn      The LDAPConnection to use for manipulating the entries
     * @param   ldEntry    The LDAP-entry to added.
     * @param   ldapAttri  The LDAP attribute.
     * @param   iCommand   The command for LDAP.
     *
     * @throws  LDAPException  if an error occured working with LDAP.
     */
    public static void changeAttibute(LDAPConnection lConn, LDAPEntry ldEntry,
                                      LDAPAttribute ldapAttri, int iCommand)
                               throws LDAPException
    {
        LDAPModification ldapMod = new LDAPModification(iCommand, ldapAttri);
        lConn.modify(ldEntry.getDN(), ldapMod);
    } // addEntry

    /**
     * Call this function if no changes are made to attibutes.
     *
     * @param   ldapCon     DOCUMENTME
     * @param   lLDAPEntry  DOCUMENTME
     * @param   iCommand    DOCUMENTME
     *
     * @throws  LDAPException  DOCUMENTME
     */
    public static void changeLDAP(LDAPConnection ldapCon, LDAPEntry lLDAPEntry, int iCommand)
                           throws LDAPException
    {
        changeLDAP(ldapCon, lLDAPEntry, iCommand, null);
    }

    /**
     * make changes to the LDAP.
     *
     * @param   ldapCon     DOCUMENTME
     * @param   lLDAPEntry  DOCUMENTME
     * @param   iCommand    DOCUMENTME
     * @param   ldapAttri   DOCUMENTME
     *
     * @throws  LDAPException  DOCUMENTME
     */
    public static void changeLDAP(LDAPConnection ldapCon, LDAPEntry lLDAPEntry, int iCommand,
                                  LDAPAttribute ldapAttri)
                           throws LDAPException
    {
        // if delete is insite iCommand
        if (iCommand == LDAPCOMMAND_DELETE)
        {
            deleteEntry(ldapCon, lLDAPEntry, false);
        }

        // if delete_recursive is insite iCommand
        if (iCommand == LDAPCOMMAND_DELETE_RECURSIVE)
        {
            deleteEntry(ldapCon, lLDAPEntry, true);
        }

        // if add is insite iCommand
        if (iCommand == LDAPCOMMAND_ADD)
        {
            addEntry(ldapCon, lLDAPEntry);
        }

        // if add is insite iCommand
        if (iCommand == LDAPCOMMAND_DELETE_ATTRIBUTE)
        {
            changeAttibute(ldapCon, lLDAPEntry, ldapAttri, LDAPModification.DELETE);
        }

        if (iCommand == LDAPCOMMAND_ADD_ATTRIBUTE)
        {
            changeAttibute(ldapCon, lLDAPEntry, ldapAttri, LDAPModification.ADD);
        }

        if (iCommand == LDAPCOMMAND_REPLACE_ATTRIBUTE)
        {
            changeAttibute(ldapCon, lLDAPEntry, ldapAttri, LDAPModification.REPLACE);
        }
    } // changeLDAP

    /**
     * function to check a value exists insite the arrtibute insite the entry.
     *
     * @param   entry     the ldapentry to search in
     * @param   sAttr     the string attribute to search in
     * @param   sCompare  the value to search in
     *
     * @return  returns true if the value exists
     */
    public static boolean checkAttriValueExists(LDAPEntry entry, String sAttr, String sCompare)
    {
        boolean bReturn = false;
        LDAPAttribute attr = entry.getAttribute(sAttr);

        if (attr != null)
        {
            String[] saValues = attr.getStringValueArray();

            for (int iCount = 0; iCount < saValues.length; iCount++)
            {
                if (saValues[iCount].equalsIgnoreCase(sCompare))
                {
                    bReturn = true;
                    break;
                }
            }
        }
        return bReturn;
    }

    /**
     * This method deletes the given entry from LDAP. If bRecursive is true, also all it's subnodes
     * are deleted.
     *
     * @param   lConn       The LDAPConnection to use for manipulating the entries
     * @param   entry       The LDAP-entry to delete.
     * @param   bRecursive  Indicates wether of not to delete all it's children.
     *
     * @throws  LDAPException  if an error occured working with LDAP.
     */
    public static void deleteEntry(LDAPConnection lConn, LDAPEntry entry, boolean bRecursive)
                            throws LDAPException
    {
        if (bRecursive == true)
        {
            String sSearchFilter = "cn=*";
            LDAPSearchResults results = lConn.search(entry.getDN(), LDAPConnection.SCOPE_ONE,
                                                     sSearchFilter, null, true);

            // Itterate trough all its children.
            while (results.hasMore())
            {
                LDAPEntry tmpEntry = results.next();
                tmpEntry = lConn.read(tmpEntry.getDN());
                deleteEntry(lConn, tmpEntry, bRecursive);
            }
        }
        lConn.delete(entry.getDN());
    } // deleteEntry

    /**
     * This methodset returns the LDAP-entry as an XML-structure.
     *
     * @param   entry  The LDAP-entry to convert to XML
     * @param   dDoc   The XML-document to use for XML creation.
     *
     * @return  The XML-representation of the Entry.
     */
    public static int entryToXML(LDAPEntry entry, Document dDoc)
    {
        return entryToXML(entry, dDoc, "entry");
    } // entryToXML

    /**
     * This methodset returns the LDAP-entry as an XML-structure.
     *
     * @param   entry     The LDAP-entry to convert to XML
     * @param   dDoc      The XML-document to use for XML creation.
     * @param   sRootTag  The name of the tag that identifies an entry.
     *
     * @return  The XML-representation of the Entry.
     */
    public static int entryToXML(LDAPEntry entry, Document dDoc, String sRootTag)
    {
        int iReturn = dDoc.createElement(sRootTag);

        LDAPAttributeSet laSet = entry.getAttributeSet();
        Iterator<?> iAttributes = laSet.iterator();

        while (iAttributes.hasNext())
        {
            LDAPAttribute attr = (LDAPAttribute) iAttributes.next();
            int iTmpNode = dDoc.createElement(attr.getName(), iReturn);
            String[] saValues = attr.getStringValueArray();

            for (int iCount = 0; iCount < saValues.length; iCount++)
            {
                dDoc.createTextElement("string", saValues[iCount], iTmpNode);
            }
        }
        return iReturn;
    } // entryToXML

    /**
     * This method returns the value of the first attribute as string.
     *
     * @param   entry  The LDAPEntry.
     * @param   sAttr  The attribute to retrieve the data of.
     *
     * @return  A string containing the data of the attribute.
     */
    public static String getAttrValue(LDAPEntry entry, String sAttr)
    {
        String sReturn = "";
        LDAPAttribute attr = entry.getAttribute(sAttr);

        if (attr != null)
        {
            String[] saValues = attr.getStringValueArray();

            if (saValues.length > 0)
            {
                sReturn = saValues[0];
            }
        }
        return sReturn;
    } // getAttrValue

    /**
     * This returns the dn of the root of the LDAP.
     *
     * @param   lConn  The LDAPConnection to use for manipulating the entries
     *
     * @return  the root of the ldap as a string
     *
     * @throws  LDAPException  if an error occured working with LDAP.
     */
    public static String getRootDN(LDAPConnection lConn)
                            throws LDAPException
    {
        String sReturn = null;

        LDAPSearchResults res = lConn.search("", LDAPConnection.SCOPE_BASE, "(namingContexts=*)",
                                             new String[] { "namingContexts" }, false);

        /* There should be only one entry in the results (the root DSE). */
        while (res.hasMore())
        {
            LDAPEntry findEntry = res.next();
            /* Get the attributes of the root DSE. */
            LDAPAttributeSet findAttrs = findEntry.getAttributeSet();
            Iterator<?> iAttributes = findAttrs.iterator();

            // Iterate through each attribute.
            if (iAttributes.hasNext())
            {
                LDAPAttribute anAttr = (LDAPAttribute) iAttributes.next();
                Enumeration<?> enumVals = anAttr.getStringValues();

                if (enumVals != null)
                {
                    sReturn = (String) enumVals.nextElement();
                }
            }
        }
        return sReturn;
    } // getRootDN

    /**
     * This method parses the given XML and returns an LDAPEntry-object.
     *
     * @param   eRoot       The root-entry.
     * @param   iEntryNode  The XML that describes the entry.
     *
     * @return  A new LDAPEntry.
     */
    public static LDAPEntry parseXML(LDAPEntry eRoot, int iEntryNode)
    {
        LDAPEntry eReturn = null;

        // Find the identification for this entry
        String sDN = eRoot.getDN();
        int iNode = Find.firstMatch(iEntryNode, "fChild<cn><string>");

        if ((iNode != 0) && (Node.getDataWithDefault(iNode, "").length() > 0))
        {
            sDN = "cn=" + Node.getData(iNode) + "," + sDN;
            eReturn = new LDAPEntry(sDN, new LDAPAttributeSet());

            // Itterate trough all the children.
            int iCurrentChild = Node.getFirstChild(iEntryNode);
            LDAPAttributeSet asAttr = eReturn.getAttributeSet();

            while (iCurrentChild != 0)
            {
                if (!(Node.getName(iCurrentChild).equals("LDAPEntry")) &&
                        !(Node.getName(iCurrentChild).equals("entry")))
                {
                    String sName = Node.getName(iCurrentChild);
                    int[] aiValues = Find.match(iCurrentChild, "fChild<string>");
                    String[] saValues = new String[aiValues.length];

                    for (int iCount = 0; iCount < aiValues.length; iCount++)
                    {
                        int iValueNode = aiValues[iCount];
                        saValues[iCount] = Node.getDataWithDefault(iValueNode, "");
                    }

                    LDAPAttribute aAttribute = new LDAPAttribute(sName, saValues);
                    asAttr.add(aAttribute);
                }
                iCurrentChild = Node.getNextSibling(iCurrentChild);
            }
        }

        return eReturn;
    } // parseXML

    /**
     * This method executes the search on LDAP. Before a search is done
     *
     * @param   lConn    DOCUMENTME
     * @param   sDN      The DN under which to search.
     * @param   iScope   The LDAP-scope.
     * @param   sFilter  The searchfilter.
     *
     * @return  An array with the results.
     *
     * @throws  LDAPException  DOCUMENTME
     */
    public static LDAPEntry[] searchLDAP(LDAPConnection lConn, String sDN, int iScope,
                                         String sFilter)
                                  throws LDAPException
    {
        LDAPSearchResults lsrResults = lConn.search(sDN, iScope, sFilter, null, false);
        Vector<LDAPEntry> vVector = new Vector<LDAPEntry>();

        try
        {
            while ((lsrResults != null) && lsrResults.hasMore())
            {
                try
                {
                    LDAPEntry ldapentry = lsrResults.next();

                    if (ldapentry != null)
                    {
                        vVector.addElement(ldapentry);
                    }
                }
                catch (LDAPException ldapexception)
                {
                    System.out.println("ldapexception" + ldapexception.getLDAPErrorMessage());
                }
                catch (Exception e)
                {
                    System.out.println("Exc" + e.getMessage());
                }
            }
        }
        catch (Exception e)
        {
            System.out.println(e + e.getMessage());
        }

        LDAPEntry[] aldapentry = new LDAPEntry[vVector.size()];
        vVector.copyInto(aldapentry);
        return aldapentry;
    } // searchLDAP

    /**
     * This method executes the search on LDAP. Before a search is done
     *
     * @param   lConn    DOCUMENTME
     * @param   sDN      The DN under which to search.
     * @param   iScope   The LDAP-scope.
     * @param   sFilter  The searchfilter.
     * @param   cons     DOCUMENTME
     *
     * @return  An array with the results.
     *
     * @throws  LDAPException  DOCUMENTME
     */
    public static LDAPEntry[] searchLDAP(LDAPConnection lConn, String sDN, int iScope,
                                         String sFilter, LDAPSearchConstraints cons)
                                  throws LDAPException
    {
        LDAPSearchResults lsrResults = lConn.search(sDN, iScope, sFilter, null, false, cons);

        Vector<LDAPEntry> vVector = new Vector<LDAPEntry>();

        while (lsrResults.hasMore())
        {
            try
            {
                LDAPEntry ldapentry = lsrResults.next();
                vVector.addElement(ldapentry);
            }
            catch (LDAPException ldapexception)
            {
            }
        }

        LDAPEntry[] aldapentry = new LDAPEntry[vVector.size()];
        vVector.copyInto(aldapentry);
        return aldapentry;
    } // searchLDAP
} // LDAPUtils
