package com.cordys.coe.tools.orgmanager.log4j;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class wraps the renderer definition.
 *
 * @author  pgussow
 */
public class Renderer
{
    /**
     * Holds the rendered class for this renderer.
     */
    private String m_sRenderedClass;

    /**
     * Holds the rendering Class for this renderer.
     */
    private String m_sRenderingClass;

    /**
     * Creates a new Renderer object.
     */
    public Renderer()
    {
    }

    /**
     * Creates a new Renderer object.
     *
     * @param  eRenderer  The source XML
     */
    public Renderer(Element eRenderer)
    {
        m_sRenderedClass = eRenderer.getAttribute("renderedClass");
        m_sRenderingClass = eRenderer.getAttribute("renderingClass");
    }

    /**
     * This method gets the rendered class for this renderer.
     *
     * @return  The rendered class for this renderer.
     */
    public String getRenderedClass()
    {
        return m_sRenderedClass;
    }

    /**
     * This method gets the rendering Class for this renderer.
     *
     * @return  The rendering Class for this renderer.
     */
    public String getRenderingClass()
    {
        return m_sRenderingClass;
    }

    /**
     * This method sets the rendered class for this renderer.
     *
     * @param  sRenderedClass  The rendered class for this renderer.
     */
    public void setRenderedClass(String sRenderedClass)
    {
        m_sRenderedClass = sRenderedClass;
    }

    /**
     * This method sets the rendering Class for this renderer.
     *
     * @param  sRenderingClass  The rendering Class for this renderer.
     */
    public void setRenderingClass(String sRenderingClass)
    {
        m_sRenderingClass = sRenderingClass;
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
        eReturn = dDoc.createElementNS("http://jakarta.apache.org/log4j/", "renderer");
        eReturn.setAttribute("renderedClass", getRenderedClass());
        eReturn.setAttribute("renderingClass", getRenderingClass());
        
        eParent.appendChild(eReturn);

        return eReturn;
    }
}
