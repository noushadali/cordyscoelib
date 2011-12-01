/**
 *  2003 Cordys R&D B.V. All rights reserved.     The computer program(s) is
 * the proprietary information of Cordys R&D B.V.     and provided under the
 * relevant License Agreement containing restrictions     on use and
 * disclosure. Use is subject to the License Agreement.
 */
package com.cordys.coe.tools.useradmin;

import com.cordys.coe.util.general.ldap.LDAPUtils;

import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPEntry;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import java.io.IOException;
import java.io.Serializable;

/**
 * This class enables to add a LDAP entry in a list or tree.
 *
 * @author  gjlubber
 */
public class LDAPItemEntry
    implements Transferable, Cloneable, Serializable
{
    /**
     * the LDAP entry.
     */
    private LDAPEntry entry;
    /**
     * sDisplay will have the first attribute.
     */
    private String sDisplay;

    /**
     * Creates a new LDAP entry. based on the param entry
     *
     * @param  entry  the LDAP entry
     */
    public LDAPItemEntry(LDAPEntry entry)
    {
        this.entry = entry;
        sDisplay = entry.getDN().substring(0, entry.getDN().indexOf("="));
    }

    /**
     * Creates a new LDAP entry. based on the DN and the attrs
     *
     * @param  sDN    string DN
     * @param  attrs  the attibutes the new LDAP entry should contain
     */
    public LDAPItemEntry(String sDN, LDAPAttributeSet attrs)
    {
        this.entry = new LDAPEntry(sDN, attrs);
        sDisplay = entry.getDN().substring(0, entry.getDN().indexOf("="));
    }

    /**
     * Check if his own autenticationuser is the same as the one from oObject.
     *
     * @param   oObject  an instance of LDAPItemEntry
     *
     * @return  Returns true is they are the same
     */
    @Override public boolean equals(Object oObject)
    {
        boolean bReturn = false;
        String sOwnAttribute = null;
        String sCompareAttribute = null;

        if (oObject instanceof LDAPItemEntry)
        {
            // get the authenticationuser attribute of this
            sOwnAttribute = LDAPUtils.getAttrValue(getEntry(), "authenticationuser");
            // get the authenticationuser attribute of oObject
            sCompareAttribute = LDAPUtils.getAttrValue(((LDAPItemEntry) oObject).getEntry(),
                                                       "authenticationuser");

            if (sOwnAttribute.equals(sCompareAttribute))
            {
                bReturn = true;
            }
        }
        return bReturn;
    }

    /**
     * Will return LDAP entry.
     *
     * @return  LDAPentrt
     */
    public LDAPEntry getEntry()
    {
        return entry;
    }

    /**
     * function who returns the transfereble data object After is has been modified.
     *
     * @param   dataflavor  the dataflavor who should be used
     *
     * @return  returns the tranferteble object
     *
     * @throws  UnsupportedFlavorException  UnsupportedFlavorException
     * @throws  IOException                 IOException
     */
    public Object getTransferData(DataFlavor dataflavor)
                           throws UnsupportedFlavorException, IOException
    {
        return this;
    }

    /**
     * returns a arrys of DataFlavors it supports.
     *
     * @return  returns a arrys of DataFlavors it supports
     */
    public DataFlavor[] getTransferDataFlavors()
    {
        DataFlavor[] dfDataFlavor = new DataFlavor[1];

        try
        {
            dfDataFlavor[0] = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType);
        }
        catch (ClassNotFoundException eException)
        {
            eException.printStackTrace();
            System.err.println("Exception" + eException.getMessage());
        }
        return dfDataFlavor;
    }

    /**
     * Check if the dataFlavor is supported.
     *
     * @param   dataFlavor  the dataflavor to check
     *
     * @return  returns true if the dataFlavor is supported otherwise it returns false
     */
    public boolean isDataFlavorSupported(DataFlavor dataFlavor)
    {
        boolean bReturn = false;

        if (DataFlavor.javaJVMLocalObjectMimeType.equals(dataFlavor.getMimeType()))
        {
            bReturn = true;
        }
        return bReturn;
    }

    /**
     * for display the ldap entry.
     *
     * @return  returns the string atribute of the item after the first "="
     */
    @Override public String toString()
    {
        String sReturn = LDAPUtils.getAttrValue(entry, sDisplay);

        if (sReturn == null)
        {
            sReturn = "";
        }
        return sReturn;
    }
}
