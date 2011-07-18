package com.cordys.coe.tools.wcpproperties;

import com.cordys.coe.util.exceptions.XMLWrapperException;
import com.cordys.coe.util.xml.dom.XMLHelper;
import com.cordys.coe.util.xml.dom.XPathHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class wraps around the wcpproperties.xml file. It reads and parses the XML and maintains the
 * lists for the different Cordys versions.
 *
 * @author  pgussow
 */
public class DefinitionFile
{
    /**
     * Holds the XPath for getting the properties.
     */
    private static final String XPATH_PROPERTY = "./property";
    /**
     * Holds teh attribute holding the Cordys version.
     */
    private static final String ATTR_VERSION = "version";
    /**
     * Holds the XPath for selecting the Cordys versions.
     */
    private static final String XPATH_CORDYS = "./cordys";
    /**
     * Holds the name of the XML file containing the wcp properties.
     */
    private static final String WCPPROPERTIES_XML = "wcpproperties.xml";
    /**
     * Holds the currently parsed properties.
     */
    private static LinkedHashMap<String, LinkedHashMap<String, WcpProperty>> m_lhmProperties = new LinkedHashMap<String, LinkedHashMap<String, WcpProperty>>();

    /**
     * Creates a new DefinitionFile object.
     *
     * @throws  XMLWrapperException   DOCUMENTME
     * @throws  TransformerException  DOCUMENTME
     */
    public DefinitionFile()
                   throws XMLWrapperException, TransformerException
    {
        this(WCPPROPERTIES_XML);
    }

    /**
     * Creates a new DefinitionFile object.
     *
     * @param   sFilename  The name of the file to load.
     *
     * @throws  XMLWrapperException   DOCUMENTME
     * @throws  TransformerException  DOCUMENTME
     */
    public DefinitionFile(String sFilename)
                   throws XMLWrapperException, TransformerException
    {
        Document dDoc = XMLHelper.createDocumentFromStream(DefinitionFile.class.getResourceAsStream(sFilename));

        // First get the different versions.
        NodeList nlVersions = XPathHelper.selectNodeList(dDoc.getDocumentElement(), XPATH_CORDYS);

        for (int iCount = 0; iCount < nlVersions.getLength(); iCount++)
        {
            Element eCordys = (Element) nlVersions.item(iCount);
            String sCordysVersion = eCordys.getAttribute(ATTR_VERSION);

            if (!m_lhmProperties.containsKey(sCordysVersion))
            {
                LinkedHashMap<String, WcpProperty> alProperties = new LinkedHashMap<String, WcpProperty>();
                m_lhmProperties.put(sCordysVersion, alProperties);

                NodeList nlProperties = XPathHelper.selectNodeList(eCordys, XPATH_PROPERTY);

                for (int iPropertyCount = 0; iPropertyCount < nlProperties.getLength();
                         iPropertyCount++)
                {
                    Element eProperty = (Element) nlProperties.item(iPropertyCount);

                    WcpProperty wpNew = new WcpProperty(eProperty);
                    alProperties.put(wpNew.getName(), wpNew);
                }
            }
        }
    }

    /**
     * This method returns the different Cordys versions that are defined in the XML file.
     *
     * @return  The list of Cordys versions.
     */
    public String[] getCordysVersions()
    {
        ArrayList<String> alReturn = new ArrayList<String>(m_lhmProperties.keySet());

        return alReturn.toArray(new String[0]);
    }

    /**
     * This method returns the properties for the given Cordys version.
     *
     * @param   sCordysVersion  The Cordys version to get the properties for.
     *
     * @return  The properties for the given Cordys version.
     */
    public ArrayList<WcpProperty> getProperties(String sCordysVersion)
    {
        ArrayList<WcpProperty> alReturn = null;

        if (m_lhmProperties.containsKey(sCordysVersion))
        {
            alReturn = new ArrayList<WcpProperty>(m_lhmProperties.get(sCordysVersion).values());
        }

        return alReturn;
    }

    /**
     * This method returns the property definition for the given Cordys version and property name.
     *
     * @param   sCordysVersion  The Cordys version.
     * @param   sPropertyName   The name of the property.
     *
     * @return  The property definition.
     */
    public WcpProperty getProperty(String sCordysVersion, String sPropertyName)
    {
        WcpProperty wpReturn = null;

        if (m_lhmProperties.containsKey(sCordysVersion))
        {
            LinkedHashMap<String, WcpProperty> lhmProperties = m_lhmProperties.get(sCordysVersion);

            if (lhmProperties.containsKey(sPropertyName))
            {
                wpReturn = lhmProperties.get(sPropertyName);
            }
        }

        return wpReturn;
    }

    /**
     * This method returns all property names for the given Cordys version.
     *
     * @param   sCordysVersion  The cordys version.
     *
     * @return  All property names for the given version.
     */
    public String[] getPropertyNames(String sCordysVersion)
    {
        String[] asReturn = new String[0];

        if (m_lhmProperties.containsKey(sCordysVersion))
        {
            LinkedHashMap<String, WcpProperty> lhmProps = m_lhmProperties.get(sCordysVersion);
            asReturn = lhmProps.keySet().toArray(new String[0]);
        }

        return asReturn;
    }
}
