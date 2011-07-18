package com.cordys.coe.tools.wcpproperties;

import com.cordys.coe.util.exceptions.XMLWrapperException;
import com.cordys.coe.util.wcpproperties.PropertyInfo;
import com.cordys.coe.util.wcpproperties.StaticPropertyInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;

/**
 * This class contains the static property mappings for the different Cordys versions. There are a
 * couple of properties which cannot be found using regex searches on the source code. They are
 * defined here.<br>
 *
 * @author  pgussow
 */
public class StaticProperties
{
    /**
     * Holds the global definition of the C2 version.
     */
    private static final String VERSION_C2 = "C2";
    /**
     * Holds the logger that is used.
     */
    private static final Logger LOG = Logger.getLogger(StaticProperties.class);
    /**
     * Holds all the static properties for the given Cordys version.
     */
    private LinkedHashMap<String, LinkedHashMap<String, PropertyInfo>> m_lhmStaticProps = new LinkedHashMap<String, LinkedHashMap<String, PropertyInfo>>();
    /**
     * Holds the Cordys version.
     */
    private String m_sCordysVersion;

    /**
     * Creates a new StaticProperties object.
     *
     * @param   sCordysVersion  Holds the Cordys version.
     *
     * @throws  XMLWrapperException   DOCUMENTME
     * @throws  TransformerException  DOCUMENTME
     */
    public StaticProperties(String sCordysVersion)
                     throws XMLWrapperException, TransformerException
    {
        if (sCordysVersion.indexOf(VERSION_C2) > -1)
        {
            // For all C2 versions the static properties are the same.
            m_sCordysVersion = VERSION_C2;
        }

        // Build up the lists
        buildC2List();
    }

    /**
     * This method adds the properties for the given Cordys version.
     *
     * @param   sCordysVersion  The Cordys version.
     * @param   lhmProperties   The list of properties to add it.
     *
     * @throws  XMLWrapperException   DOCUMENTME
     * @throws  TransformerException  DOCUMENTME
     */
    public static void addStaticProperties(String sCordysVersion,
                                           LinkedHashMap<String, PropertyInfo> lhmProperties)
                                    throws XMLWrapperException, TransformerException
    {
        StaticProperties spProps = new StaticProperties(sCordysVersion);
        spProps.addProperties(lhmProperties);
    }

    /**
     * This method adds the static properties to the given list.
     *
     * @param  lhmProperties  The list to add them to.
     */
    public void addProperties(LinkedHashMap<String, PropertyInfo> lhmProperties)
    {
        LinkedHashMap<String, PropertyInfo> lhmStatic = m_lhmStaticProps.get(m_sCordysVersion);

        if (lhmStatic != null)
        {
            for (Iterator<PropertyInfo> iPropertyInfo = lhmStatic.values().iterator();
                     iPropertyInfo.hasNext();)
            {
                PropertyInfo piInfo = iPropertyInfo.next();

                if (LOG.isDebugEnabled())
                {
                    LOG.debug("Adding property " + piInfo.getName());
                }
                lhmProperties.put(piInfo.getName(), piInfo);
            }
        }
    }

    /**
     * This method builds up the additional properties for any C2 version.
     *
     * @throws  XMLWrapperException   DOCUMENTME
     * @throws  TransformerException  DOCUMENTME
     */
    private void buildC2List()
                      throws XMLWrapperException, TransformerException
    {
        LinkedHashMap<String, PropertyInfo> lhmProps = new LinkedHashMap<String, PropertyInfo>();
        m_lhmStaticProps.put(VERSION_C2, lhmProps);

        DefinitionFile dfStatic = new DefinitionFile("staticwcpproperties.xml");

        ArrayList<WcpProperty> alProps = dfStatic.getProperties(VERSION_C2);

        for (WcpProperty wp : alProps)
        {
            StaticPropertyInfo spi = new StaticPropertyInfo(wp.getName(), wp.getCaption(),
                                                            wp.getDefaultValue(), wp.getComponent(),
                                                            wp.isMandatory() ? "true" : "false",
                                                            wp.getDescription());
            ArrayList<WcpPropertyWhereUsed> alWhereUsed = wp.getWhereUsed();

            for (WcpPropertyWhereUsed wpwu : alWhereUsed)
            {
                spi.addClass(wpwu.getClassName(), wpwu.getComponent());
            }

            lhmProps.put(spi.getName(), spi);
        }
    }
}
