package com.cordys.coe.util.wcpproperties;

import com.cordys.coe.util.xml.dom.XMLHelper;

import java.io.File;

import java.util.Iterator;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;

/**
 * This class wraps around static proeprties and it's information.
 *
 * @author  pgussow
 */
public class StaticPropertyInfo extends PropertyInfo
{
    /**
     * Holds the caption for the property.
     */
    private String m_sCaption;
    /**
     * Holds the component name where it's defined.
     */
    private String m_sComponent;
    /**
     * Holds the description of this property.
     */
    private String m_sDescription;
    /**
     * Holds whether or not the property is mandatory.
     */
    private String m_sMandatory;

    /**
     * Creates a new StaticPropertyInfo object.
     *
     * @param  sName            The name of the property.
     * @param  sValue           The default value of the property.
     * @param  sComponent       The name of the component.
     * @param  sWhereUsedClass  The class where this property is used.
     */
    public StaticPropertyInfo(String sName, String sValue, String sComponent,
                              String sWhereUsedClass)
    {
        super(sName, sValue, null);
        setMandatory("false");
        setDescription(sName);
        setCaption(sName);
        setComponent(sComponent);

        addClass(new ClassInfo(sWhereUsedClass, new SourcePath(sComponent, new File("."))));
    }

    /**
     * Creates a new StaticPropertyInfo object.
     *
     * @param  sName         The name of the property.
     * @param  sCaption      The caption for the property.
     * @param  sValue        The default value of the property.
     * @param  sComponent    The name of the component.
     * @param  sMandatory    sWhereUsedClass The class where this property is used.
     * @param  sDescription  The description of the property.
     */
    public StaticPropertyInfo(String sName, String sCaption, String sValue, String sComponent,
                              String sMandatory, String sDescription)
    {
        super(sName, sValue, null);
        setMandatory(sMandatory);
        setDescription(sDescription);
        setCaption(sCaption);
        setComponent(sComponent);
    }

    /**
     * This method adds the given class to the where-used.
     *
     * @param  sClassname  The name of the class.
     * @param  sComponent  The name of the component.
     */
    public void addClass(String sClassname, String sComponent)
    {
        addClass(new ClassInfo(sClassname, new SourcePath(sComponent, new File("."))));
    }

    /**
     * This method gets the caption for this static property.
     *
     * @return  The caption for this static property.
     */
    public String getCaption()
    {
        return m_sCaption;
    }

    /**
     * This method gets the name of the component where it comes from.
     *
     * @return  The name of the component where it comes from.
     */
    public String getComponent()
    {
        return m_sComponent;
    }

    /**
     * This method gets the description for this static property.
     *
     * @return  The description for this static property.
     */
    public String getDescription()
    {
        return m_sDescription;
    }

    /**
     * This method gets whether or not the property is mandatory.
     *
     * @return  Whether or not the property is mandatory.
     */
    public String getMandatory()
    {
        return m_sMandatory;
    }

    /**
     * This method sets the caption for this static property.
     *
     * @param  sCaption  The caption for this static property.
     */
    public void setCaption(String sCaption)
    {
        m_sCaption = sCaption;
    }

    /**
     * This method sets the name of the component where it comes from.
     *
     * @param  sComponent  The name of the component where it comes from.
     */
    public void setComponent(String sComponent)
    {
        m_sComponent = sComponent;
    }

    /**
     * This method sets the description for this static property.
     *
     * @param  sDescription  The description for this static property.
     */
    public void setDescription(String sDescription)
    {
        m_sDescription = sDescription;
    }

    /**
     * This method sets wether or not the property is mandatory.
     *
     * @param  sMandatory  Whether or not the property is mandatory.
     */
    public void setMandatory(String sMandatory)
    {
        m_sMandatory = sMandatory;
    }

    /**
     * This method writes the content for this property to XML. It uses the previous version to fill
     * in missing blanks.
     *
     * @param   eParent           The parent element.
     * @param   ePreviousVersion  The element pointing to the previous version.
     *
     * @throws  TransformerException  DOCUMENTME
     *
     * @see     com.cordys.coe.util.wcpproperties.PropertyInfo#toXML(org.w3c.dom.Element,
     *          org.w3c.dom.Element)
     */
    @Override public void toXML(Element eParent, Element ePreviousVersion)
                         throws TransformerException
    {
        Element eProperty = XMLHelper.createElement(TAG_PROPERTY, eParent);

        XMLHelper.createTextElement(TAG_NAME, getName(), eProperty);
        XMLHelper.createTextElement(TAG_CAPTION, getCaption(), eProperty);
        XMLHelper.createTextElement(TAG_DESCRIPTION, getDescription(), eProperty);
        XMLHelper.createTextElement(TAG_DEFAULT_VALUE, getDefaultValue(), eProperty);
        XMLHelper.createTextElement(TAG_COMPONENT, getComponent(), eProperty);
        XMLHelper.createTextElement(TAG_MANDATORY, getMandatory(), eProperty);

        // Now do the where-used.
        Element eWhereUsed = XMLHelper.createElement(TAG_WHEREUSED, eProperty);

        for (Iterator<ClassInfo> iClasses = getWhereUsedClasses().iterator(); iClasses.hasNext();)
        {
            ClassInfo ciInfo = (ClassInfo) iClasses.next();
            Element eClass = (Element) XMLHelper.createTextElement(TAG_CLASS, ciInfo.getClassName(),
                                                                   eWhereUsed);
            eClass.setAttribute(ATTR_COMPONENT, ciInfo.getSourcepath().getComponentName());
        }
    }
}
