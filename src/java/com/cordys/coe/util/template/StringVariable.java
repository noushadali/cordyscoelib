/**
 * (c) 2006 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util.template;

import com.eibus.xml.nom.Document;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * A string variable type.
 *
 * @author  mpoyhone
 */
public class StringVariable
    implements ITemplateVariable
{
    /**
     * Contains the variable data.
     */
    private String sString;

    /**
     * Constructor for StringVariable.
     *
     * @param  sString  Message data.
     */
    public StringVariable(String sString)
    {
        this.sString = sString;
    }

    /**
     * @see  com.cordys.coe.util.template.ITemplateVariable#getAsByteArray(String)
     */
    public byte[] getAsByteArray(String sStringEncoding)
    {
        if (sString == null)
        {
            throw new IllegalStateException("String data is not set.");
        }

        try
        {
            return sString.getBytes((sStringEncoding != null) ? sStringEncoding : "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new IllegalStateException("UTF-8 encoding is not supported by this platform.", e);
        }
    }

    /**
     * @see  com.cordys.coe.util.template.ITemplateVariable#getAsInputStream(String)
     */
    public InputStream getAsInputStream(String sStringEncoding)
    {
        return new ByteArrayInputStream(getAsByteArray(sStringEncoding));
    }

    /**
     * @see  com.cordys.coe.util.template.ITemplateVariable#getAsNomXml(com.eibus.xml.nom.Document)
     */
    public int getAsNomXml(Document dDocument)
    {
        if (sString == null)
        {
            throw new IllegalStateException("String data is not set.");
        }

        try
        {
            return dDocument.parseString(sString);
        }
        catch (Exception e)
        {
            throw new IllegalStateException("Unable to parse string to XML.", e);
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
        if (sString == null)
        {
            throw new IllegalStateException("String data is not set.");
        }

        return sString;
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
        return (sString != null) ? sString : "null";
    }
}
