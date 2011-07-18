package com.cordys.coe.tools.orgmanager.log4j;

import com.cordys.coe.util.xml.dom.XPathHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This base class can be used for all objects the use the param name/value way.
 *
 * @author  pgussow
 */
public abstract class BaseWithParam
{
    /**
     * Holds all parameters for this object.
     */
    private LinkedHashMap<String, Parameter> m_lhmParameters = new LinkedHashMap<String, Parameter>();

    /**
     * Holds the class name.
     */
    private String m_sClass;
    /**
     * Holds the name of the tag to create.
     */
    private String m_sTagName;

    /**
     * Creates a new BaseWithParam object.
     *
     * @param  sTagName  DOCUMENTME
     */
    public BaseWithParam(String sTagName)
    {
        m_sTagName = sTagName;
    }

    /**
     * Creates a new BaseWithParam object.
     *
     * @param  eParent   The parent element.
     * @param  sTagName  The name of the parent tag.
     */
    public BaseWithParam(Element eParent, String sTagName)
    {
        m_sTagName = sTagName;
        
        m_sClass = eParent.getAttribute("class");

        try
        {
            NodeList nl = XPathHelper.selectNodeList(eParent, "./param");

            for (int iCount = 0; iCount < nl.getLength(); iCount++)
            {
                Parameter p = new Parameter((Element) nl.item(iCount));
                addParameter(p);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error parsing parameters file", e);
        }
    }

    /**
     * This method adds a new parameter.
     *
     * @param  p  The parameter to add.
     */
    public void addParameter(Parameter p)
    {
        m_lhmParameters.put(p.getName(), p);
    }
    
    /**
     * This method removes all current parameters.
     */
    public void clearParameters() 
    {
        m_lhmParameters.clear();
    }

    /**
     * This method gets the class name.
     *
     * @return  The class name.
     */
    public String getClassName()
    {
        return m_sClass;
    }

    /**
     * This method gets the parameters.
     *
     * @return  The parameters.
     */
    public ArrayList<Parameter> getParameters()
    {
        return new ArrayList<Parameter>(m_lhmParameters.values());
    }

    /**
     * This method sets the class name.
     *
     * @param  sClass  The class name.
     */
    public void setClassName(String sClass)
    {
        m_sClass = sClass;
    }

    /**
     * This method writes the current configuration back to XML.
     *
     * @param   eParent  The parent node.
     *
     * @return  The creates element.
     */
    public Element toXML(Element eParent)
    {
        Element eReturn = null;

        Document dDoc = eParent.getOwnerDocument();
        eReturn = dDoc.createElementNS("http://jakarta.apache.org/log4j/", m_sTagName);
        eParent.appendChild(eReturn);

        onAfterTagCreation(eReturn);

        if (getClassName() != null && getClassName().length() > 0)
    	{
        	eReturn.setAttribute("class", getClassName());
    	}

        onAfterAttributeCreation(eReturn);

        for (Parameter p : m_lhmParameters.values())
        {
            p.toXML(eReturn);
        }

        onAfterParameterCreation(eReturn);

        return eReturn;
    }

    /**
     * Adapter method. Is called when the attributes have been created.
     *
     * @param  eElement  The created element.
     */
    protected void onAfterAttributeCreation(Element eElement)
    {
    }

    /**
     * Adapter method. Is called when the parameters have been created.
     *
     * @param  eElement  The created element.
     */
    protected void onAfterParameterCreation(Element eElement)
    {
    }

    /**
     * Empty adapter method. Is called when the tag has been created.
     *
     * @param  eCreated  The created tag.
     */
    protected void onAfterTagCreation(Element eCreated)
    {
    }
}
