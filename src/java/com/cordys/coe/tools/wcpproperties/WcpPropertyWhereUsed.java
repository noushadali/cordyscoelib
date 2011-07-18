package com.cordys.coe.tools.wcpproperties;

import com.cordys.coe.util.xml.dom.XPathHelper;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Wrapper around the where-used clause in the wcpproperties file.
 *
 * @author  pgussow
 */
public class WcpPropertyWhereUsed
{
    /**
     * The Xpath for getting the class name.
     */
    private static final String XPATH_TEXT = "./text()";
    /**
     * The name of the attribute holding the component name.
     */
    private static final String ATTR_COMPONENT = "component";
    /**
     * The name of the class.
     */
    private String m_sClassName;
    /**
     * The component (Integrator, Orchestrator, Studio or Portal).
     */
    private String m_sComponent;

    /**
     * Creates a new WcpPropertyWhereUsed object.
     *
     * @param   eClass  The element for the class.
     *
     * @throws  TransformerException  DOCUMENTME
     */
    public WcpPropertyWhereUsed(Element eClass)
                         throws TransformerException
    {
        Node nTemp = XPathHelper.selectSingleNode(eClass, XPATH_TEXT);

        if (nTemp != null)
        {
            m_sClassName = nTemp.getNodeValue();
        }
        m_sComponent = eClass.getAttribute(ATTR_COMPONENT);
    }

    /**
     * This method gets the classname.
     *
     * @return  The classname.
     */
    public String getClassName()
    {
        return m_sClassName;
    }

    /**
     * This method gets the component name.
     *
     * @return  The component name.
     */
    public String getComponent()
    {
        return m_sComponent;
    }
}
