package com.cordys.coe.util.wcpproperties;

import com.cordys.coe.util.xml.dom.XMLHelper;
import com.cordys.coe.util.xml.dom.XPathHelper;

import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class contains the information for a given property.
 *
 * @author  pgussow
 */
public class PropertyInfo
{
    /**
     * Holds the name of the tag 'property'.
     */
    protected static final String TAG_PROPERTY = "property";
    /**
     * Holds the name of the tag 'name'.
     */
    protected static final String TAG_NAME = "name";
    /**
     * Holds the name of the tag 'caption'.
     */
    protected static final String TAG_CAPTION = "caption";
    /**
     * Holds the name of the tag 'description'.
     */
    protected static final String TAG_DESCRIPTION = "description";
    /**
     * Holds the name of the tag 'default'.
     */
    protected static final String TAG_DEFAULT_VALUE = "default";
    /**
     * Holds the name of the tag 'whereused'.
     */
    protected static final String TAG_WHEREUSED = "whereused";
    /**
     * Holds the name of the tag 'class'.
     */
    protected static final String TAG_CLASS = "class";
    /**
     * Holds the name of the tag 'component'.
     */
    protected static final String ATTR_COMPONENT = "component";
    /**
     * Holds the name of the tag 'mandatory'.
     */
    protected static final String TAG_MANDATORY = "mandatory";
    /**
     * Holds the name of the attribute 'component'.
     */
    protected static final String TAG_COMPONENT = ATTR_COMPONENT;
    /**
     * Holds all the classes.
     */
    private ArrayList<ClassInfo> m_alClasses = new ArrayList<ClassInfo>();
    /**
     * Holds the name.
     */
    private String m_sName;
    /**
     * Holds the default value.
     */
    private String m_sValue;

    /**
     * Creates a new PropertyInfo object.
     *
     * @param  sName   The name of the property.
     * @param  sValue  The default value of the property.
     * @param  ciInfo  The classinfo in which the class the property was found.
     */
    public PropertyInfo(String sName, String sValue, ClassInfo ciInfo)
    {
        m_sName = sName;
        m_sValue = sValue;

        if (ciInfo != null)
        {
            m_alClasses.add(ciInfo);
        }
    }

    /**
     * This method adds a class to the list.
     *
     * @param  ciInfo  The class to add.
     */
    public void addClass(ClassInfo ciInfo)
    {
        if (!m_alClasses.contains(ciInfo))
        {
            m_alClasses.add(ciInfo);
        }
    }

    /**
     * This method gets the FQN of the class where it is used.
     *
     * @return  The FQN of the class where it is used.
     */
    public ArrayList<ClassInfo> getClassInfo()
    {
        return m_alClasses;
    }

    /**
     * This method gets the default value.
     *
     * @return  The default value.
     */
    public String getDefaultValue()
    {
        return m_sValue;
    }

    /**
     * This method gets the property name.
     *
     * @return  The property name.
     */
    public String getName()
    {
        return m_sName;
    }

    /**
     * This method gets the where-used classes.
     *
     * @return  The where-used classes.
     */
    public ArrayList<ClassInfo> getWhereUsedClasses()
    {
        return m_alClasses;
    }

    /**
     * This method sets the default value for the property.
     *
     * @param  sValue  The default value for the property.
     */
    public void setDefaultValue(String sValue)
    {
        m_sValue = sValue;
    }

    /**
     * Returns a string representation.
     *
     * @return  The string representation.
     *
     * @see     java.lang.Object#toString()
     */
    @Override public String toString()
    {
        StringBuffer sbReturn = new StringBuffer(1024);

        sbReturn.append(getName());
        sbReturn.append("\t'");
        sbReturn.append(getDefaultValue().trim());
        sbReturn.append("'\tUsed in: ");

        for (Iterator<ClassInfo> iClasses = m_alClasses.iterator(); iClasses.hasNext();)
        {
            ClassInfo ciInfo = (ClassInfo) iClasses.next();
            sbReturn.append(ciInfo.getClassName() + "(" +
                            ciInfo.getSourcepath().getComponentName() + ")");

            if (iClasses.hasNext())
            {
                sbReturn.append(", ");
            }
        }

        return sbReturn.toString();
    }

    /**
     * This method writes the content for this property to XML. It uses the previous version to fill
     * in missing blanks.
     *
     * @param   eParent           The parent element.
     * @param   ePreviousVersion  The element pointing to the previous version.
     *
     * @throws  TransformerException  DOCUMENTME
     */
    public void toXML(Element eParent, Element ePreviousVersion)
               throws TransformerException
    {
        Element eProperty = XMLHelper.createElement(TAG_PROPERTY, eParent);

        // Do the name.
        XMLHelper.createTextElement(TAG_NAME, getName(), eProperty);

        // Do the caption. Find it in the previous version.
        String sCaption = getPreviousValue(ePreviousVersion, getName(), TAG_CAPTION, getName());
        XMLHelper.createTextElement(TAG_CAPTION, sCaption, eProperty);

        // Do the description. Find it in the previous version.
        String sDescription = getPreviousValue(ePreviousVersion, getName(), TAG_DESCRIPTION,
                                               getName());
        XMLHelper.createTextElement(TAG_DESCRIPTION, sDescription, eProperty);

        // Do the default value. Find it in the previous version.
        String sDefaultValue = getPreviousValue(ePreviousVersion, getName(), TAG_DEFAULT_VALUE,
                                                getDefaultValue());
        XMLHelper.createTextElement(TAG_DEFAULT_VALUE, sDefaultValue, eProperty);

        // Do the component name where it was found first.
        XMLHelper.createTextElement(TAG_COMPONENT,
                                    m_alClasses.iterator().next().getSourcepath()
                                    .getComponentName(), eProperty);

        // Do the mandatory component. Find it in the previous version.
        String sMandatory = getPreviousValue(ePreviousVersion, getName(), TAG_MANDATORY, "false");
        XMLHelper.createTextElement(TAG_MANDATORY, sMandatory, eProperty);

        // Now do the where-used.
        Element eWhereUsed = XMLHelper.createElement(TAG_WHEREUSED, eProperty);

        for (Iterator<ClassInfo> iClasses = m_alClasses.iterator(); iClasses.hasNext();)
        {
            ClassInfo ciInfo = (ClassInfo) iClasses.next();
            Element eClass = (Element) XMLHelper.createTextElement(TAG_CLASS, ciInfo.getClassName(),
                                                                   eWhereUsed);
            eClass.setAttribute(ATTR_COMPONENT, ciInfo.getSourcepath().getComponentName());
        }
    }

    /**
     * This method returns the value for the given tag if it's found in the previous version.
     *
     * @param   ePreviousVersion  The element pointing to the previous version.
     * @param   sPropertyName     The name of the property.
     * @param   sTag              The name of the tag to find.
     * @param   sDefault          The default value if the tag is not found in the previous version.
     *
     * @return  The value for this tag from the previous version. If not found the default value is
     *          returned.
     *
     * @throws  TransformerException  DOCUMENTME
     */
    protected String getPreviousValue(Element ePreviousVersion, String sPropertyName, String sTag,
                                      String sDefault)
                               throws TransformerException
    {
        String sReturn = sDefault;

        if (ePreviousVersion != null)
        {
            Node nPrev = XPathHelper.selectSingleNode(ePreviousVersion,
                                                      "./property[name=\"" + sPropertyName +
                                                      "\"]/" + sTag + "/text()");

            if (nPrev != null)
            {
                String sTemp = nPrev.getNodeValue();

                if (sTemp != null)
                {
                    sReturn = sTemp;
                }
            }
        }
        return sReturn;
    }
}
