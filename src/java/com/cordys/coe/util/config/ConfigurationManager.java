package com.cordys.coe.util.config;

import com.cordys.coe.util.exceptions.XMLWrapperException;
import com.cordys.coe.util.xml.dom.NiceDOMWriter;
import com.cordys.coe.util.xml.dom.XMLHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Iterator;
import java.util.LinkedHashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class is used to store the configuration of the connections which have been made. The
 * configurations are stored at: {user.home}/cordysconf.xml
 *
 * @author  pgussow
 */
public class ConfigurationManager
{
    /**
     * Holds the singleton.
     */
    private static ConfigurationManager s_cmManager = null;
    /**
     * j Holds the name of the configuration file.
     */
    private static final String FILE_NAME = ".cordysconf/configurations.xml";
    /**
     * Holds the XML of the configuration.
     */
    private Document m_dConfigFile;
    /**
     * Holds the configuration file.
     */
    private File m_fConfigFile;
    /**
     * Holds the current configurations.
     */
    private LinkedHashMap<String, IConfiguration> m_lhmConfigurations = new LinkedHashMap<String, IConfiguration>();

    /**
     * Creates a new ConfigurationManager object.
     *
     * @throws  ConfigurationManagerException  DOCUMENTME
     */
    private ConfigurationManager()
                          throws ConfigurationManagerException
    {
        String sUserFolder = System.getProperty("user.home");

        if ((sUserFolder == null) || (sUserFolder.length() == 0))
        {
            throw new ConfigurationManagerException(ConfigurationManagerException.EC_USER_FOLDER,
                                                    "User folder not found.");
        }

        m_fConfigFile = new File(sUserFolder, FILE_NAME);

        if (m_fConfigFile.exists())
        {
            loadConfiguration();
        }
    }

    /**
     * This method returns the instance of the configuration manager.
     *
     * @return  the configuration manager.
     *
     * @throws  ConfigurationManagerException  DOCUMENTME
     */
    public static ConfigurationManager getInstance()
                                            throws ConfigurationManagerException
    {
        if (s_cmManager == null)
        {
            s_cmManager = new ConfigurationManager();
        }

        return s_cmManager;
    }

    /**
     * This method adds a configuration. If the name is already taken it throws an exception.
     *
     * @param   cConfig  The configuration to add.
     *
     * @throws  ConfigurationManagerException  DOCUMENTME
     */
    public void addConfiguration(IConfiguration cConfig)
                          throws ConfigurationManagerException
    {
        if (cConfig == null)
        {
            throw new ConfigurationManagerException(ConfigurationManagerException.EC_CONFIGURATION,
                                                    "Configuration cannot be null.");
        }

        if (m_lhmConfigurations.containsKey(cConfig.getName()))
        {
            throw new ConfigurationManagerException(ConfigurationManagerException.EC_CONFIGURATION,
                                                    "There is already a configuration with name " +
                                                    cConfig.getName());
        }
        m_lhmConfigurations.put(cConfig.getName(), cConfig);
    }

    /**
     * This method returns the configuration object with the given name.
     *
     * @param   sName  The name for the configuration.
     *
     * @return  The configuration.
     */
    public IConfiguration getConfiguration(String sName)
    {
        return m_lhmConfigurations.get(sName);
    }

    /**
     * This method returns the list of configurations.
     *
     * @return  The list of configurations.
     */
    public LinkedHashMap<String, IConfiguration> getConfigurations()
    {
        return m_lhmConfigurations;
    }

    /**
     * This method returns the list of stored webgateway connections.
     *
     * @return  The list of stored web gateway connections.
     */
    public LinkedHashMap<String, IWebGatewayConfiguration> getWebGatewayConfigurations()
    {
        LinkedHashMap<String, IWebGatewayConfiguration> lhmReturn = new LinkedHashMap<String, IWebGatewayConfiguration>();

        for (IConfiguration cConfig : getConfigurations().values())
        {
            if (cConfig instanceof IWebGatewayConfiguration)
            {
                IWebGatewayConfiguration wgc = (IWebGatewayConfiguration) cConfig;
                lhmReturn.put(wgc.getName(), wgc);
            }
        }
        return lhmReturn;
    }

    /**
     * This method removes a configuration from the list.
     *
     * @param   cConfig  The configuration to remove.
     *
     * @throws  ConfigurationManagerException  DOCUMENTME
     */
    public void removeConfiguration(IConfiguration cConfig)
                             throws ConfigurationManagerException
    {
        if (cConfig == null)
        {
            throw new ConfigurationManagerException(ConfigurationManagerException.EC_CONFIGURATION,
                                                    "Configuration cannot be null.");
        }

        if (m_lhmConfigurations.containsKey(cConfig.getName()))
        {
            m_lhmConfigurations.remove(cConfig.getName());
        }
    }

    /**
     * This mehtod saves all configurations to the file.
     *
     * @throws  ConfigurationManagerException  DOCUMENTME
     */
    public void saveConfigurations()
                            throws ConfigurationManagerException
    {
        Document dDoc = XMLHelper.createDocumentFromXML("<configurations/>");

        for (Iterator<IConfiguration> iConfigs = m_lhmConfigurations.values().iterator();
                 iConfigs.hasNext();)
        {
            IConfiguration cConfig = iConfigs.next();
            cConfig.toXMLStructure(dDoc.getDocumentElement());
        }

        // Now write it to file.
        FileOutputStream fos = null;

        try
        {
            if (!m_fConfigFile.getParentFile().exists())
            {
                m_fConfigFile.getParentFile().mkdirs();
            }
            fos = new FileOutputStream(m_fConfigFile, false);

            String sXML = NiceDOMWriter.write(dDoc.getDocumentElement());

            fos.write(sXML.getBytes());
            fos.flush();
        }
        catch (Exception e)
        {
            throw new ConfigurationManagerException(ConfigurationManagerException.EC_CONFIG_FILE,
                                                    "Error writing the configuration file.", e);
        }
        finally
        {
            if (fos != null)
            {
                try
                {
                    fos.close();
                }
                catch (IOException e)
                {
                    // Ignore it.
                }
            }
        }
    }

    /**
     * This method loads the configurations from the file.
     *
     * @throws  ConfigurationManagerException  DOCUMENTME
     */
    private void loadConfiguration()
                            throws ConfigurationManagerException
    {
        try
        {
            m_dConfigFile = XMLHelper.loadXMLFile(m_fConfigFile.getAbsolutePath());
        }
        catch (XMLWrapperException e)
        {
            throw new ConfigurationManagerException(ConfigurationManagerException.EC_CONFIG_FILE,
                                                    "Error loading the file containing the configurations.");
        }

        // Parse the XML into the the configurations.
        NodeList nlConfigs = null;

        try
        {
            nlConfigs = XMLHelper.getNodeList(m_dConfigFile.getDocumentElement(),
                                              "./configuration");
        }
        catch (XMLWrapperException e)
        {
            throw new ConfigurationManagerException(ConfigurationManagerException.EC_CONFIG_FILE,
                                                    "Error finding the stored configrations in the XML.");
        }

        for (int iCount = 0; iCount < nlConfigs.getLength(); iCount++)
        {
            Element nConfig = (Element) nlConfigs.item(iCount);

            IConfiguration cConfig = ConfigurationFactory.createConfiguration(nConfig);
            m_lhmConfigurations.put(cConfig.getName(), cConfig);
        }
    }
}
