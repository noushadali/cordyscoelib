/**
 * (c) 2006 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util.template;

import com.eibus.xml.nom.Document;

import java.io.InputStream;

/**
 * Interface for defining variables for templates. This interface allows different types of data to
 * be passed transparently. The data type is determined by the enumeration value returned by this
 * interface.
 *
 * @author  mpoyhone
 */
public interface ITemplateVariable
{
    /**
     * Returns the variable data as a byte array. For string variables the UTF-8 encoding is used.
     *
     * @param   sStringEncoding  Optional enconding for string type variables.
     *
     * @return  Data as a byte array.
     */
    byte[] getAsByteArray(String sStringEncoding);

    /**
     * Returns an input stream that can be used to read the content. The caller is responsible of
     * closing this input stream!
     *
     * @param   sStringEncoding  Optional enconding for string type variables.
     *
     * @return  Content input stream.
     */
    InputStream getAsInputStream(String sStringEncoding);

    /**
     * Returns the variable data as NOM XML. The caller _must_ delete this data!
     *
     * @param   dDocument  Document used to create a new node (if necessary). If <code>null</code>
     *                     and the variable is not a native NOM variable, this method throws an
     *                     <code>IllegalArgumentException</code>.
     *
     * @return  NOM XML root node.
     */
    int getAsNomXml(Document dDocument);

    /**
     * Returns the variable data as NOM XML. If a new node is created, it is placed on the list,
     * otherwise an exising node is returned. The caller _must not_ delete this data directly, only
     * through the cleanup list!
     *
     * @param   dDoc     Document used to create a new node (if necessary).
     * @param   oclList  If a now node needs to be created it is placed in this list.
     *
     * @return  Create NOM XML root node.
     */
    int getAsNomXml(Document dDoc, ObjectCleanupList<ICleanupObject> oclList);

    /**
     * Returns the variable data as a plain string.
     *
     * @param   sEncoding  String encoding for variables that need to convert the data from binary
     *                     format.
     *
     * @return  Data as a string.
     */
    String getAsString(String sEncoding);

    /**
     * Returns root type of the variable.
     *
     * @return  Variable type.
     */
    EVariableType getVariableType();
}
