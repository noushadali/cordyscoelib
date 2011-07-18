package com.cordys.coe.tools.wcpproperties;

import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.commons.configuration.PropertiesConfiguration;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * This class provides the content for the current wcp.properties.
 *
 * @author  pgussow
 */
class WcpPropertyContentProvider
    implements IStructuredContentProvider
{
    /**
     * The meta information about the properties.
     */
    private DefinitionFile m_dfMetaFile;
    /**
     * Holds the properties.
     */
    private LinkedHashMap<String, ActualProperty> m_lhmProperties = null;
    /**
     * Holds the current cordys version.
     */
    private String m_sCordysVersion = "";
    /**
     * Holds the parent editor.
     */
    private WCPPropertiesEditor m_wpeEditor;

    /**
     * Creates a new WcpPropertyContentProvider object.
     *
     * @param  wpeEditor   The parent editor.
     * @param  dfMetaFile  The file containing the meta information about properties.
     */
    public WcpPropertyContentProvider(WCPPropertiesEditor wpeEditor, DefinitionFile dfMetaFile)
    {
        m_wpeEditor = wpeEditor;
        m_dfMetaFile = dfMetaFile;
    }

    /**
     * This method is when the viewer is disposed.
     */
    public void dispose()
    {
        if (m_lhmProperties != null)
        {
            m_lhmProperties.clear();
        }
        m_lhmProperties = null;
    }

    /**
     * This method gets the content of this provider..
     *
     * @return  The content of this provider..
     */
    public LinkedHashMap<String, ActualProperty> getActualProperties()
    {
        return m_lhmProperties;
    }

    /**
     * This method returns the objects that need to be shown in the current grid.
     *
     * @param   oInputElement  The input element (which should be a PropertyConfiguration object.
     *
     * @return  The objects that need to be shown.
     */
    public Object[] getElements(Object oInputElement)
    {
        Object[] aoReturn = new Object[0];

        if (oInputElement instanceof PropertiesConfiguration)
        {
            PropertiesConfiguration pcConfig = (PropertiesConfiguration) oInputElement;

            m_lhmProperties = new LinkedHashMap<String, ActualProperty>();

            for (Iterator<?> iKeys = pcConfig.getKeys(); iKeys.hasNext();)
            {
                String sKey = (String) iKeys.next();
                String sValue = pcConfig.getString(sKey);

                ActualProperty apNew = new ActualProperty(sKey, sValue);

                WcpProperty wp = m_dfMetaFile.getProperty(m_sCordysVersion, apNew.getName());
                apNew.setWcpProperty(wp);
                m_lhmProperties.put(apNew.getName(), apNew);
            }

            aoReturn = m_lhmProperties.values().toArray();
        }
        return aoReturn;
    }

    /**
     * This method is called when the input changes for the given viewer. In this case nothing needs
     * to be done.
     *
     * @param  vViewer    The current viewer.
     * @param  oOldInput  The old input object.
     * @param  oNewInput  The new input object.
     */
    public void inputChanged(Viewer vViewer, Object oOldInput, Object oNewInput)
    {
        if (m_lhmProperties != null)
        {
            m_lhmProperties.clear();
        }
        m_lhmProperties = null;
        m_sCordysVersion = m_wpeEditor.getCordysVersion();
    }
}
