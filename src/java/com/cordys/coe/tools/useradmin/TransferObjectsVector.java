/**
 *  2003 Cordys R&D B.V. All rights reserved.     The computer program(s) is
 * the proprietary information of Cordys R&D B.V.     and provided under the
 * relevant License Agreement containing restrictions     on use and
 * disclosure. Use is subject to the License Agreement.
 */
package com.cordys.coe.tools.useradmin;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import java.util.Vector;

/**
 * The drag-and-drop vector.
 *
 * @author  gjlubber
 */
public class TransferObjectsVector
    implements Transferable
{
    /**
     * DOCUMENTME.
     */
    protected Vector<Object> oTranferVector; // The data to be transferred.

    /**
     * Creates a new instance of TransferObjectArray.
     */
    public TransferObjectsVector()
    {
        oTranferVector = new Vector<Object>();
    }

    /**
     * DOCUMENTME.
     *
     * @param  oObject  DOCUMENTME
     */
    public void addTransferObjectToVector(Object oObject)
    {
        oTranferVector.addElement(oObject);
    }

    /**
     * DOCUMENTME.
     *
     * @param   dataFlavor  DOCUMENTME
     *
     * @return  DOCUMENTME
     *
     * @throws  java.awt.datatransfer.UnsupportedFlavorException  DOCUMENTME
     * @throws  java.io.IOException                               DOCUMENTME
     */
    public Object getTransferData(java.awt.datatransfer.DataFlavor dataFlavor)
                           throws java.awt.datatransfer.UnsupportedFlavorException,
                                  java.io.IOException
    {
        return oTranferVector;
    }

    /**
     * DOCUMENTME.
     *
     * @return  DOCUMENTME
     */
    public java.awt.datatransfer.DataFlavor[] getTransferDataFlavors()
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
     * DOCUMENTME.
     *
     * @param   dataFlavor  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    public boolean isDataFlavorSupported(java.awt.datatransfer.DataFlavor dataFlavor)
    {
        boolean bReturn = false;

        if (DataFlavor.javaJVMLocalObjectMimeType.equals(dataFlavor.getMimeType()))
        {
            bReturn = true;
        }
        return bReturn;
    }

    /**
     * DOCUMENTME.
     *
     * @return  DOCUMENTME
     */
    public int size()
    {
        return oTranferVector.size();
    }
}
