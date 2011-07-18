/**
 * (c) 2006 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util.template;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Node;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * A NOM XML variable type.
 *
 * @author  mpoyhone
 */
public class NomXmlVariable
    implements ITemplateVariable, ICleanupObject
{
    /**
     * Contains the variable data.
     */
    private int xRootNode;

    /**
     * Constructor for NomXmlVariable.
     *
     * @param  xRootNode  Message data.
     */
    public NomXmlVariable(int xRootNode)
    {
        this.xRootNode = xRootNode;
    }

    /**
     * @see  com.cordys.coe.util.template.ICleanupObject#cleanup()
     */
    public void cleanup()
    {
        deleteNode();
    }

    /**
     * Deletes the root node.
     */
    public void deleteNode()
    {
        if (xRootNode != 0)
        {
            Node.delete(xRootNode);
            xRootNode = 0;
        }
    }

    /**
     * @see  com.cordys.coe.util.template.ITemplateVariable#getAsByteArray(String)
     */
    public byte[] getAsByteArray(String sStringEncoding)
    {
        if (xRootNode == 0)
        {
            throw new IllegalStateException("NOM node is not set.");
        }

        return Node.write(xRootNode, false);
    }

    /**
     * @see  com.cordys.coe.util.template.ITemplateVariable#getAsInputStream(String)
     */
    public InputStream getAsInputStream(String sStringEncoding)
    {
        if (xRootNode == 0)
        {
            throw new IllegalStateException("NOM node is not set.");
        }

        ByteArrayInputStream baisInput;

        baisInput = new ByteArrayInputStream(getAsByteArray(null));

        return baisInput;
    }

    /**
     * @see  com.cordys.coe.util.template.ITemplateVariable#getAsNomXml(com.eibus.xml.nom.Document)
     */
    public int getAsNomXml(Document dDocument)
    {
        // Here we have the clone the root node because the caller must delete the returned node.
        if (xRootNode == 0)
        {
            throw new IllegalStateException("NOM node is not set.");
        }

        return Node.clone(xRootNode, true);
    }

    /**
     * @see  com.cordys.coe.util.template.ITemplateVariable#getAsNomXml(com.eibus.xml.nom.Document,
     *       com.cordys.coe.util.template.ObjectCleanupList)
     */
    public int getAsNomXml(Document dDoc, ObjectCleanupList<ICleanupObject> oclList)
    {
        if (xRootNode == 0)
        {
            throw new IllegalStateException("NOM node is not set.");
        }

        return xRootNode;
    }

    /**
     * @see  com.cordys.coe.util.template.ITemplateVariable#getAsString(String)
     */
    public String getAsString(String sEncoding)
    {
        if (xRootNode == 0)
        {
            throw new IllegalStateException("NOM node is not set.");
        }

        return Node.writeToString(xRootNode, false);
    }

    /**
     * @see  com.cordys.coe.util.template.ITemplateVariable#getVariableType()
     */
    public EVariableType getVariableType()
    {
        return EVariableType.XML;
    }

    /**
     * @see  java.lang.Object#toString()
     */
    @Override public String toString()
    {
        return Node.writeToString(xRootNode, true);
    }
}
