package com.cordys.coe.tools.orgmanager.log4j;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class wraps the parameter.
 *
 * @author  pgussow
 */
public class Parameter
{
    /**
     * Holds the name of the parameter.
     */
    private String m_sName;

    /**
     * Holds the value for the parameter.
     */
    private String m_sValue;

    /**
     * Creates a new Parameter object.
     */
    public Parameter()
    {
    }

    /**
     * Creates a new Parameter object.
     *
     * @param  eParameter  The source parameter.
     */
    public Parameter(Element eParameter)
    {
        m_sName = eParameter.getAttribute("name");
        m_sValue = eParameter.getAttribute("value");
    }
    
    /**
     * Creates a new parameters with the given name and value.
     * 
     * @param sName The name of the appender.
     * @param sValue The value for the appender.
     */
    public Parameter(String sName, String sValue)
    {
        m_sName = sName;
        m_sValue = sValue;
    }

    /**
     * This method gets the name of the parameter.
     *
     * @return  The name of the parameter.
     */
    public String getName()
    {
        return m_sName;
    }

    /**
     * This method gets the value for the parameter.
     *
     * @return  The value for the parameter.
     */
    public String getValue()
    {
        return m_sValue;
    }

    /**
     * This method sets the name of the parameter.
     *
     * @param  sName  The name of the parameter.
     */
    public void setName(String sName)
    {
        m_sName = sName;
    }

    /**
     * This method sets the value for the parameter.
     *
     * @param  sValue  The value for the parameter.
     */
    public void setValue(String sValue)
    {
        m_sValue = sValue;
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

        eReturn = dDoc.createElementNS("http://jakarta.apache.org/log4j/", "param");
        eReturn.setAttribute("name", getName());
        eReturn.setAttribute("value", getValue());
        
        eParent.appendChild(eReturn);

        return eReturn;
    }
}
