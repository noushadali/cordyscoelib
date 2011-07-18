/**
 *      © 2003 Cordys R&D B.V. All rights reserved.
 *      The computer program(s) is the proprietary information of Cordys R&D B.V.
 *      and provided under the relevant License Agreement containing restrictions
 *      on use and disclosure. Use is subject to the License Agreement.
 */
package com.cordys.coe.tools.contentcopier;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import java.io.IOException;

import java.util.Vector;

/**
 * Object used to hold the the LDAP items and it's connection that should be transferred.
 */
public class DataTransferVector
    implements Transferable
{
    /**
     * Holds the human-readable form of the dataflavor.
     */
    public static final String DND_LDAPENRIES = "LDAPEntries";
    /**
     * Holds the connection to LDAP.
     */
    private LDAPConnection lCon;
    /**
     * Holds all the LDAP entries.
     */
    private Vector<LDAPEntry> vEntries;

    /**
     * Default constructor.
     *
     * @param  lCon  The LDAP-connection for which the objects are valid.
     */
    public DataTransferVector(LDAPConnection lCon)
    {
        this.lCon = lCon;
        vEntries = new Vector<LDAPEntry>();
    } // DataTransferVector

    /**
     * Returns whether or not the dataflavor passed is the dataflavor we're looking for.
     *
     * @param   dataFlavor  The dataflavor to compare.
     *
     * @return  true if the dataflavor matches the wanted dataflavor.
     */
    public static boolean isFlavor(DataFlavor dataFlavor)
    {
        boolean bReturn = false;

        if (dataFlavor != null)
        {
            if (DataTransferVector.class.getName().equals(dataFlavor.getRepresentationClass()
                                                              .getName()))
            {
                bReturn = true;
            }
        }
        return bReturn;
    } // isFlavor

    /**
     * This method adds a LDAPEntry to the vector.
     *
     * @param  entry  The LDAP-entry.
     */
    public void addEntry(LDAPEntry entry)
    {
        vEntries.add(entry);
    } // addEntry

    /**
     * Returns the LDAP-connection for these entries.
     *
     * @return  The LDAP-connection for these entries.
     */
    public LDAPConnection getConnection()
    {
        return lCon;
    } // getConnection

    /**
     * Returns an array containing all the LDAP-entries.
     *
     * @return  An array containing all the LDAP-entries.
     */
    public LDAPEntry[] getEntries()
    {
        LDAPEntry[] laReturn = new LDAPEntry[vEntries.size()];
        vEntries.copyInto(laReturn);
        return laReturn;
    } // getEntries

    /**
     * Returns an object which represents the data to be transferred. The class of the object
     * returned is defined by the representation class of the flavor.
     *
     * @param      flavor  the requested flavor for the data
     *
     * @return     The object itself.
     *
     * @exception  UnsupportedFlavorException  if the requested data flavor is not supported.
     * @exception  IOException                 if the data is no longer available in the requested
     *                                         flavor.
     */
    public Object getTransferData(DataFlavor flavor)
                           throws UnsupportedFlavorException, IOException
    {
        Object oReturn = null;

        if (isDataFlavorSupported(flavor))
        {
            oReturn = this;
        }
        return oReturn;
    } // getTransferData

    /**
     * Returns an array of DataFlavor objects indicating the flavors the data can be provided in.
     * The array should be ordered according to preference for providing the data (from most richly
     * descriptive to least descriptive).
     *
     * @return  an array of data flavors in which this data can be transferred
     */
    public DataFlavor[] getTransferDataFlavors()
    {
        DataFlavor[] dfDataFlavor = new DataFlavor[1];
        dfDataFlavor[0] = new DataFlavor(this.getClass(), DND_LDAPENRIES);
        return dfDataFlavor;
    } // getTransferDataFlavors

    /**
     * Returns whether or not the specified data flavor is supported for this object.
     *
     * @param   flavor  the requested flavor for the data
     *
     * @return  boolean indicating whether or not the data flavor is supported
     */
    public boolean isDataFlavorSupported(DataFlavor flavor)
    {
        boolean bReturn = false;

        if ((this.getClass().getName().equals(flavor.getRepresentationClass().getName())))
        {
            bReturn = true;
        }
        return bReturn;
    } // isDataFlavorSupported
} // DataTransferVector
