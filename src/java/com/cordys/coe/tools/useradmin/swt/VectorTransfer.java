package com.cordys.coe.tools.useradmin.swt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.Vector;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * This class can be used to drag and drop a vector with objects.
 *
 * @author  pgussow
 */
public class VectorTransfer extends ByteArrayTransfer
{
    /**
     * Identifies the drag-type.
     */
    private static final String TYPENAME_VECTOR = "vector_type";
    /**
     * Registers the type as draggable.
     */
    private static final int TYPEID_VECTOR = registerType(TYPENAME_VECTOR);
    /**
     * Singleton instance.
     */
    private static VectorTransfer vtInstance = new VectorTransfer();

    /**
     * Creates a new VectorTransfer object.
     */
    private VectorTransfer()
    {
    }

    /**
     * This method returns an instance of this class.
     *
     * @return  The instance of this object.
     */
    public static VectorTransfer getInstance()
    {
        return vtInstance;
    }

    /**
     * This method converts the Java objects to a native stream.
     *
     * @param  otoBeDragged  The object that should be dragged.
     * @param  tdData        The transfer data.
     */
    @Override public void javaToNative(Object otoBeDragged, TransferData tdData)
    {
        if ((otoBeDragged == null) || !(otoBeDragged instanceof Vector<?>))
        {
            return;
        }

        if (isSupportedType(tdData))
        {
            Vector<?> vVector = (Vector<?>) otoBeDragged;

            try
            {
                // write data to a byte array and then ask super to convert to pMedium
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ObjectOutputStream writeOut = new ObjectOutputStream(out);

                writeOut.writeObject(vVector);

                byte[] buffer = out.toByteArray();
                writeOut.close();

                super.javaToNative(buffer, tdData);
            }
            catch (Exception e)
            {
                System.out.println("DragException: " + e);
            }
        }
    }

    /**
     * DOCUMENTME.
     *
     * @param   transferData  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    @Override public Object nativeToJava(TransferData transferData)
    {
        Vector<?> vReturn = null;

        if (isSupportedType(transferData))
        {
            byte[] buffer = (byte[]) super.nativeToJava(transferData);

            if (buffer == null)
            {
                return null;
            }

            try
            {
                ByteArrayInputStream in = new ByteArrayInputStream(buffer);
                ObjectInputStream readIn = new ObjectInputStream(in);

                Object oTemp = readIn.readObject();

                if (oTemp instanceof Vector<?>)
                {
                    vReturn = (Vector<?>) oTemp;
                }
                else
                {
                    System.out.println("Unknown object: " + oTemp.getClass().getName());
                }
                readIn.close();
            }
            catch (Exception ex)
            {
                System.out.println("Exception reading object: " + ex);
            }
        }

        return vReturn;
    }

    /**
     * This method returns the type IDs.
     *
     * @return  The type IDs
     */
    @Override protected int[] getTypeIds()
    {
        return new int[] { TYPEID_VECTOR };
    }

    /**
     * This method returns the type names.
     *
     * @return  the type names.
     */
    @Override protected String[] getTypeNames()
    {
        return new String[] { TYPENAME_VECTOR };
    }
}
