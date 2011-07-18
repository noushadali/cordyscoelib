/**
 * (c) 2006 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util.template;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.XMLException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * A byte array variable type.
 *
 * @author  mpoyhone
 */
public class ByteArrayVariable
    implements ITemplateVariable
{
    /**
     * Contains the variable data.
     */
    protected byte[] baBytes;

    /**
     * Constructor for ByteArrayVariable.
     *
     * @param  baBytes  Variable data.
     */
    public ByteArrayVariable(byte[] baBytes)
    {
        this.baBytes = baBytes;
    }

    /**
     * @see  com.cordys.coe.util.template.ITemplateVariable#getAsByteArray(String)
     */
    public byte[] getAsByteArray(String sStringEncoding)
    {
        if (baBytes == null)
        {
            throw new IllegalStateException("Byte array data is not set.");
        }

        return baBytes;
    }

    /**
     * Returns an input stream that can be used to read the content. The caller is responsible of
     * closing this input stream!
     *
     * @param   sStringEncoding  Optional enconding for string type variables.
     *
     * @return  Content input stream.
     *
     * @see     com.cordys.coe.util.template.ITemplateVariable#getAsInputStream(java.lang.String)
     */
    public InputStream getAsInputStream(String sStringEncoding)
    {
        return new ByteArrayInputStream(baBytes);
    }

    /**
     * @see  com.cordys.coe.util.template.ITemplateVariable#getAsNomXml(com.eibus.xml.nom.Document)
     */
    public int getAsNomXml(Document dDocument)
    {
        if (baBytes == null)
        {
            throw new IllegalStateException("Byte array data is not set.");
        }

        try
        {
            return dDocument.load(baBytes);
        }
        catch (XMLException e)
        {
            throw new IllegalStateException("Unable to parse bytes to XML.", e);
        }
    }

    /**
     * @see  com.cordys.coe.util.template.ITemplateVariable#getAsNomXml(com.eibus.xml.nom.Document,
     *       com.cordys.coe.util.template.ObjectCleanupList)
     */
    public int getAsNomXml(Document dDoc, ObjectCleanupList<ICleanupObject> oclList)
    {
        int xRes = getAsNomXml(dDoc);

        oclList.add(new NomXmlVariable(xRes));

        return xRes;
    }

    /**
     * @see  com.cordys.coe.util.template.ITemplateVariable#getAsString(String)
     */
    public String getAsString(String sEncoding)
    {
        if (baBytes == null)
        {
            throw new IllegalStateException("Byte array data is not set.");
        }

        try
        {
            return (sEncoding != null) ? new String(baBytes, sEncoding) : new String(baBytes);
        }
        catch (Exception e)
        {
            throw new IllegalStateException("Byte data is not in format of the given character set.",
                                            e);
        }
    }

    /**
     * @see  com.cordys.coe.util.template.ITemplateVariable#getVariableType()
     */
    public EVariableType getVariableType()
    {
        return EVariableType.STRING;
    }

    /**
     * @see  java.lang.Object#toString()
     */
    @Override public String toString()
    {
        return getAsString(null);
    }
}
