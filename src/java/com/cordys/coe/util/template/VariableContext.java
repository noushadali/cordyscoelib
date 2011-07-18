/**
 * (c) 2006 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util.template;

import com.eibus.xml.nom.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * A class that wraps all the needed variable information.
 *
 * @author  mpoyhone
 */
public class VariableContext
{
    /**
     * Document used to create the result XML parameters.
     */
    private Document dDoc;
    /**
     * Map containing mapping from variable names to variable values.
     */
    private Map<String, ITemplateVariable> mValueMap;
    /**
     * Optional string encoding to be used when converting binary values to a string.
     */
    private String sStringEncoding;
    /**
     * Optional parent context, for overriding values.
     */
    private VariableContext vcParentContext;

    /**
     * Constructor for VariableContext. Uses UTF-8 string encoding.
     *
     * @param  dDoc  Document used to create the result XML parameters.
     */
    public VariableContext(Document dDoc)
    {
        this(new HashMap<String, ITemplateVariable>(), dDoc, "UTF-8");
    }

    /**
     * Constructor for VariableContext.
     *
     * @param  vcParentContext  Parent context.
     */
    public VariableContext(VariableContext vcParentContext)
    {
        this.vcParentContext = vcParentContext;
    }

    /**
     * Constructor for VariableContext.
     *
     * @param  dDoc             Document used to create the result XML parameters.
     * @param  sStringEncoding  Optional string encoding to be used when converting binary values to
     *                          a string.
     */
    public VariableContext(Document dDoc, String sStringEncoding)
    {
        this(new HashMap<String, ITemplateVariable>(), dDoc, sStringEncoding);
    }

    /**
     * Constructor for VariableContext.
     *
     * @param  mValueMap        Map containing mapping from variable names to variable values.
     * @param  dDoc             Document used to create the result XML parameters.
     * @param  sStringEncoding  Optional string encoding to be used when converting binary values to
     *                          a string.
     */
    public VariableContext(Map<String, ITemplateVariable> mValueMap, Document dDoc,
                           String sStringEncoding)
    {
        this.mValueMap = mValueMap;
        this.dDoc = dDoc;
        this.sStringEncoding = sStringEncoding;
    }

    /**
     * Returns the doc.
     *
     * @return  Returns the doc.
     */
    public Document getDoc()
    {
        return (dDoc != null) ? dDoc
                              : ((vcParentContext != null) ? vcParentContext.getDoc() : null);
    }

    /**
     * Returns the stringEncoding.
     *
     * @return  Returns the stringEncoding.
     */
    public String getStringEncoding()
    {
        return (sStringEncoding != null)
               ? sStringEncoding
               : ((vcParentContext != null) ? vcParentContext.getStringEncoding() : null);
    }

    /**
     * Returns the valueMap.
     *
     * @return  Returns the valueMap.
     */
    public Map<String, ITemplateVariable> getValueMap()
    {
        return (mValueMap != null)
               ? mValueMap : ((vcParentContext != null) ? vcParentContext.getValueMap() : null);
    }

    /**
     * Returns a variable value.
     *
     * @param   sName  Variable name.
     *
     * @return  Variable value or <code>null</code> if it is not set.
     */
    public ITemplateVariable getVariable(String sName)
    {
        if (mValueMap == null)
        {
            return (vcParentContext != null) ? vcParentContext.getVariable(sName) : null;
        }

        return mValueMap.get(sName);
    }

    /**
     * The doc to set.
     *
     * @param  aDoc  The doc to set.
     */
    public void setDoc(Document aDoc)
    {
        dDoc = aDoc;
    }

    /**
     * The stringEncoding to set.
     *
     * @param  aStringEncoding  The stringEncoding to set.
     */
    public void setStringEncoding(String aStringEncoding)
    {
        sStringEncoding = aStringEncoding;
    }

    /**
     * The valueMap to set.
     *
     * @param  aValueMap  The valueMap to set.
     */
    public void setValueMap(Map<String, ITemplateVariable> aValueMap)
    {
        mValueMap = aValueMap;
    }

    /**
     * Sets a variable.
     *
     * @param  sName    Variable name.
     * @param  emValue  Variable value.
     */
    public void setVariable(String sName, ITemplateVariable emValue)
    {
        if (mValueMap == null)
        {
            throw new IllegalStateException("Variable map is not initialized.");
        }

        mValueMap.put(sName, emValue);
    }

    /**
     * Sets a string variable.
     *
     * @param  sName   Variable name.
     * @param  sValue  Variable value.
     */
    public void setVariable(String sName, String sValue)
    {
        if (mValueMap == null)
        {
            throw new IllegalStateException("Variable map is not initialized.");
        }

        if (sValue == null)
        {
            return;
        }

        mValueMap.put(sName, new StringVariable(sValue));
    }

    /**
     * Sets a NOM XML variable.
     *
     * @param  sName   Variable name.
     * @param  xValue  Variable value.
     */
    public void setVariable(String sName, int xValue)
    {
        if (mValueMap == null)
        {
            throw new IllegalStateException("Variable map is not initialized.");
        }

        if (xValue == 0)
        {
            return;
        }

        mValueMap.put(sName, new NomXmlVariable(xValue));
    }
}
