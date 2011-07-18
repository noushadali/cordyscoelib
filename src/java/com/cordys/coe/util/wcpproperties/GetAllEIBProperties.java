package com.cordys.coe.util.wcpproperties;

import com.cordys.coe.tools.wcpproperties.StaticProperties;
import com.cordys.coe.util.xml.dom.XMLHelper;
import com.cordys.coe.util.xml.dom.XPathHelper;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class gets all the EIBProperties which are being called. This regex gets all the properties
 * which are called directly:<br>
 *
 * <p>EIBProperties\.(getProperty|getInteger|getString)\("([^",)]+)"(,"([^")]+))<br>
 * </p>
 *
 * @author  pgussow
 */
public class GetAllEIBProperties
{
    /**
     * Identiefies the location of the file holding the actual wcp.properties.
     */
    private static final String WCPPROPERTIES_XML = "./src/java/com/cordys/coe/tools/wcpproperties/wcpproperties.xml";
    /**
     * Identifies the location of the file holding the template file.
     */
    private static final String TEMPLATEWCPPROPERTIES_XML = "./src/java/com/cordys/coe/tools/wcpproperties/templatewcpproperties.xml";
    /**
     * Holds the component name for Portal.
     */
    public static final String COMP_PORTAL = "Portal";
    /**
     * Holds teh component name for Studio.
     */
    public static final String COMP_STUDIO = "Studio";
    /**
     * Holds the component name for Orchestrator.
     */
    public static final String COMP_ORCHESTRATOR = "Orchestrator";
    /**
     * Holds teh component name for Integrator.
     */
    public static final String COMP_INTEGRATOR = "Integrator";
    /**
     * Holds the logger that is used.
     */
    private static final Logger LOG = Logger.getLogger(GetAllEIBProperties.class);
    /**
     * Holds the logger that is used for the resolving.
     */
    private static final Logger LOG_RESOLVER = Logger.getLogger(GetAllEIBProperties.class.getName() +
                                                                ".Resolver");
    /**
     * Holds the list of source folders to scan.
     */
    private ArrayList<SourcePath> m_alFolders = new ArrayList<SourcePath>();
    /**
     * This list contains the properties that should not be matched.
     */
    private LinkedHashMap<String, String> m_lhmExceptions = new LinkedHashMap<String, String>();
    /**
     * Holds all the plain access properties.
     */
    private LinkedHashMap<String, PropertyInfo> m_lhmProperties = new LinkedHashMap<String, PropertyInfo>();
    /**
     * Pattern for the plain file.
     */
    private Pattern m_pProperties = Pattern.compile("EIBProperties\\.(getProperty|getInteger|getString|getBoolean)\\(\\s*(\"*([^\",\\s)]+)\"*)(,\\s*\"*([^\")]*))*");
    /**
     * Holds the cordys version for this property run.
     */
    private String m_sVersion;

    /**
     * Creates a new GetAllEIBProperties object.
     */
    public GetAllEIBProperties()
    {
        // Build up the exceptions of properties that should not be matched and in which file they
        // are located.
        m_lhmExceptions.put("com.eibus.applicationconnector.monitor.Monitor.oldPropName",
                            COMP_INTEGRATOR + "|com.eibus.applicationconnector.monitor.Monitor");
        m_lhmExceptions.put("com.eibus.applicationconnector.monitor.Monitor.newPropName",
                            COMP_INTEGRATOR + "|com.eibus.applicationconnector.monitor.Monitor");
        m_lhmExceptions.put("com.eibus.applicationconnector.monitor.Monitor.propName",
                            COMP_INTEGRATOR + "|com.eibus.applicationconnector.monitor.Monitor");
        m_lhmExceptions.put("com.eibus.management.WCPPropertiesLegacySettingProvider.propertyName",
                            COMP_INTEGRATOR +
                            "|com.eibus.management.WCPPropertiesLegacySettingProvider");
        m_lhmExceptions.put("com.eibus.license.internal.LicFile.properyName",
                            COMP_INTEGRATOR + "|com.eibus.license.internal.LicFile");
        m_lhmExceptions.put("com.eibus.util.system.SSLConfiguration.propertyName",
                            COMP_INTEGRATOR + "|com.eibus.util.system.SSLConfiguration");
        m_lhmExceptions.put("com.eibus.web.",
                            COMP_INTEGRATOR + "|com.eibus.web.isapi.ExtensionControlBlock");
        m_lhmExceptions.put("com.eibus.web.isapi.extension.",
                            COMP_INTEGRATOR + "|com.eibus.web.isapi.ExtensionControlBlock");
        m_lhmExceptions.put("security.dos.",
                            COMP_INTEGRATOR + "|com.eibus.security.dos.WatchThread");
        m_lhmExceptions.put("java.vm.name",
                            COMP_INTEGRATOR + "|com.eibus.applicationconnector.monitor.Monitor");
        m_lhmExceptions.put("java.vm.version",
                            COMP_INTEGRATOR + "|com.eibus.applicationconnector.monitor.Monitor");
        m_lhmExceptions.put("java.home",
                            COMP_INTEGRATOR +
                            "|com.eibus.applicationconnector.monitor.OSProcessWatcher");
        m_lhmExceptions.put("com.eibus.applicationconnector.ldap.FailOver",
                            COMP_INTEGRATOR +
                            "|com.eibus.applicationconnector.monitor.ProcessHandle");
        m_lhmExceptions.put("com.eibus.tools.elv.EventLogViewerForm.property",
                            COMP_INTEGRATOR + "|com.eibus.tools.elv.EventLogViewerForm");
        m_lhmExceptions.put("security.dos.default.numRequest",
                            COMP_INTEGRATOR + "|com.eibus.security.dos.WatchThread");
        m_lhmExceptions.put("security.dos.blockPeriod",
                            COMP_INTEGRATOR + "|com.eibus.security.dos.ClearList");
        m_lhmExceptions.put("security.dos.watchInterval",
                            COMP_INTEGRATOR + "|com.eibus.security.dos.WatchThread");

        // The user.name
        m_lhmExceptions.put("user.name", "ALL");
    }

    /**
     * Main method.
     *
     * @param  saArguments  The commandline arguments.
     */
    public static void main(String[] saArguments)
    {
        try
        {
            PropertyConfigurator.configure("./test/log4j.properties");

            File fBaseSource = new File("D:\\development\\bcpsrc\\c2bld538");

            GetAllEIBProperties gaep = new GetAllEIBProperties();
            gaep.setCordysVersion("C2 Build 538");
            gaep.addSourcePath(new SourcePath(COMP_INTEGRATOR,
                                              new File(fBaseSource, "integrator\\src\\java")));
            gaep.addSourcePath(new SourcePath(COMP_ORCHESTRATOR,
                                              new File(fBaseSource, "orchestrator\\src\\java")));
            gaep.addSourcePath(new SourcePath(COMP_STUDIO,
                                              new File(fBaseSource, "studio\\src\\java")));
            gaep.addSourcePath(new SourcePath(COMP_PORTAL,
                                              new File(fBaseSource, "studio\\src\\java")));
            /*
             * gaep.analyzeFile(new SourcePath("Integrator",                                  new
             * File(fBaseSource, "integrator\\src\\java")), new
             * File("D:\\development\\bcpsrc\\c2bld538\\integrator\\src\\java\\com\\eibus\\transport\\groupmembership\\comlayer\\CommsAddressing.java
             * "));/*/
            gaep.execute();

            String sIntegrator = gaep.printProperties();

            LOG.info("WCP properties:\n" + sIntegrator);

            // The toTemplate is only needed with a new major release.
            // gaep.toTemplateXMLFile(TEMPLATEWCPPROPERTIES_XML, "C2", false);
            gaep.toXMLFile(WCPPROPERTIES_XML);
            // */
        }
        catch (Exception e)
        {
            LOG.error("Error parsing sources", e);
            e.printStackTrace();
        }
    }

    /**
     * This method fills up the string sSource with cChar until the string has the length of
     * iLength. The characters are
     *
     * @param   sSource  The source string to which the character is to be added.
     * @param   cChar    The char to be added.
     * @param   iLength  The total length of the string.
     *
     * @return  The string with the character padded to the total length of iLength.
     */
    public static String padLeft(String sSource, String cChar, int iLength)
    {
        String sReturn = sSource;

        if (sReturn == null)
        {
            sReturn = "";
        }

        String sChararacter = cChar;

        if (sChararacter == null)
        {
            sChararacter = " ";
        }

        while (sReturn.length() < iLength)
        {
            sReturn = sChararacter + sReturn;
        }
        return sReturn;
    }

    /**
     * This method gets the user for the passed on request.
     *
     * @param   sSource  The source string to which the character is to be added.
     * @param   cChar    The char to be added.
     * @param   iLength  The total length of the string.
     *
     * @return  The string with the character padded to the total length of iLength.
     */
    public static String padRight(String sSource, String cChar, int iLength)
    {
        StringBuffer sbBuffer = new StringBuffer();

        if (sSource != null)
        {
            sbBuffer.append(sSource);
        }

        String sChararacter = cChar;

        if (sChararacter == null)
        {
            sChararacter = " ";
        }

        while (sbBuffer.length() < iLength)
        {
            sbBuffer.append(sChararacter);
        }
        return sbBuffer.toString();
    }

    /**
     * This method adds a new source path to the list that should be scanned.
     *
     * @param  spSource  The source path.
     */
    public void addSourcePath(SourcePath spSource)
    {
        m_alFolders.add(spSource);
    }

    /**
     * Goes through all the sources.
     *
     * @throws  Exception  DOCUMENTME
     */
    public void execute()
                 throws Exception
    {
        for (SourcePath spFolder : m_alFolders)
        {
            parseFolder(spFolder, spFolder.getLocation());
        }

        // Now add the static properties.
        StaticProperties.addStaticProperties(getCordysVersion(), m_lhmProperties);
    }

    /**
     * This method gets the Cordys version.
     *
     * @return  The Cordys version.
     */
    public String getCordysVersion()
    {
        return m_sVersion;
    }

    /**
     * This method writes all the found properties to a string.
     *
     * @return  The found properties.
     */
    public String printProperties()
    {
        StringWriter swWriter = new StringWriter();
        PrintWriter pw = new PrintWriter(swWriter);
        pw.println("EIBProperties:");

        for (Iterator<String> iPlain = m_lhmProperties.keySet().iterator(); iPlain.hasNext();)
        {
            String sKey = (String) iPlain.next();
            PropertyInfo piInfo = m_lhmProperties.get(sKey);
            pw.println(piInfo.toString());
        }

        return swWriter.getBuffer().toString();
    }

    /**
     * This method sets the Cordys version for this run.
     *
     * @param  sVersion  The new version.
     */
    public void setCordysVersion(String sVersion)
    {
        m_sVersion = sVersion;
    }

    /**
     * This method writes the properties to the template XML file. The template is used to store and
     * maintain the descriptions that are already known. When the toXMLFile is called it will read
     * all static information about a property from the template.xml.
     *
     * @param   sFilename   The name of the file to write to.
     * @param   sVersion    The version for this template.
     * @param   bOverwrite  Whether or not to overwrite the existing template file.
     *
     * @throws  Exception  DOCUMENTME
     */
    public void toTemplateXMLFile(String sFilename, String sVersion, boolean bOverwrite)
                           throws Exception
    {
        Document dCurrent = null;
        Element ePreviousVersion = null;

        if (bOverwrite == true)
        {
            dCurrent = XMLHelper.createDocumentFromXML("<template/>");
        }
        else
        {
            // We need to load it, since we will be using the pervious version to get
            // the documentation for each property.
            dCurrent = XMLHelper.loadXMLFile(sFilename);

            // Get the root of the previous version. This will be the last entry of the
            // <cordys> tag under the document element.
            NodeList nlChildren = dCurrent.getDocumentElement().getChildNodes();

            for (int iCount = nlChildren.getLength() - 1; iCount >= 0; iCount--)
            {
                Node nTemp = nlChildren.item(iCount);

                if ((nTemp.getNodeType() == Node.ELEMENT_NODE) &&
                        nTemp.getLocalName().equals("cordys"))
                {
                    // Found the previous version
                    ePreviousVersion = (Element) nTemp;
                    break;
                }
            }

            // Check if the template for the current version exists.
            // If so, remove it.
            Element ePrev = (Element) XPathHelper.selectSingleNode(dCurrent.getDocumentElement(),
                                                                   "./cordys[@version=\"" +
                                                                   sVersion + "\"]");

            if (ePrev != null)
            {
                ePreviousVersion = ePrev;
                ePrev.getParentNode().removeChild(ePrev);
            }
        }

        // Now write the XML file
        Element eNewCordysVersion = XMLHelper.createElement("cordys",
                                                            dCurrent.getDocumentElement());
        eNewCordysVersion.setAttribute("version", sVersion);

        ArrayList<PropertyInfo> alTemp = new ArrayList<PropertyInfo>(m_lhmProperties.values());
        Collections.sort(alTemp, new PropertyInfoComparator());

        for (Iterator<PropertyInfo> iPlain = alTemp.iterator(); iPlain.hasNext();)
        {
            PropertyInfo piInfo = (PropertyInfo) iPlain.next();

            if (!(piInfo instanceof StaticPropertyInfo))
            {
                // Only if it's not a static property we need to copy the description from
                // the previous version.
                piInfo.toXML(eNewCordysVersion, ePreviousVersion);
            }
            else
            {
                piInfo.toXML(eNewCordysVersion, null);
            }
        }

        // Now serialize the XML again.
        OutputFormat of = new OutputFormat();
        of.setIndent(4);
        of.setIndenting(true);

        FileOutputStream fos = new FileOutputStream(sFilename, false);

        try
        {
            XMLSerializer xs = new XMLSerializer(fos, of);
            xs.serialize(dCurrent);
        }
        finally
        {
            fos.close();
        }
    }

    /**
     * This method writes all the properties to the given XML file.
     *
     * @param   sFilename  The name of the XML file to write.
     *
     * @throws  Exception  DOCUMENTME
     */
    public void toXMLFile(String sFilename)
                   throws Exception
    {
        File fTemp = new File(sFilename);

        Document dDoc = null;
        Element eTemplate = null;

        if (fTemp.exists())
        {
            // We need to load it, since we will be using the pervious version to get
            // the documentation for each property.
            dDoc = XMLHelper.loadXMLFile(fTemp.getCanonicalPath());

            // Check if the properties for the current version exist.
            // If so, remove it.
            Element ePrev = (Element) XPathHelper.selectSingleNode(dDoc.getDocumentElement(),
                                                                   "./cordys[@version=\"" +
                                                                   getCordysVersion() + "\"]");

            if (ePrev != null)
            {
                ePrev.getParentNode().removeChild(ePrev);
            }
        }
        else
        {
            // Create a new and empty dom document.
            dDoc = XMLHelper.createDocumentFromXML("<properties/>");
        }

        // Get the template for the current version.
        eTemplate = getTemplate(getCordysVersion());

        // Create the property entries.
        Element eNewCordysVersion = XMLHelper.createElement("cordys", dDoc.getDocumentElement());
        eNewCordysVersion.setAttribute("version", getCordysVersion());

        ArrayList<PropertyInfo> alTemp = new ArrayList<PropertyInfo>(m_lhmProperties.values());
        Collections.sort(alTemp, new PropertyInfoComparator());

        for (Iterator<PropertyInfo> iPlain = alTemp.iterator(); iPlain.hasNext();)
        {
            PropertyInfo piInfo = (PropertyInfo) iPlain.next();

            piInfo.toXML(eNewCordysVersion, eTemplate);
        }

        // Now serialize the XML again.
        OutputFormat of = new OutputFormat();
        of.setIndent(4);
        of.setIndenting(true);

        FileOutputStream fos = new FileOutputStream(fTemp, false);

        try
        {
            XMLSerializer xs = new XMLSerializer(fos, of);
            xs.serialize(dDoc);
        }
        finally
        {
            fos.close();
        }
    }

    /**
     * This method analyzes the complete file.
     *
     * @param   spPath  The source path.
     * @param   fFile   The file to analyze.
     *
     * @throws  IOException  DOCUMENTME
     */
    private void analyzeFile(SourcePath spPath, File fFile)
                      throws IOException
    {
        if (LOG.isDebugEnabled())
        {
            LOG.debug("Analyzing file " + fFile.getCanonicalPath());
        }

        if (fFile.length() > 0)
        {
            byte[] baBuffer = new byte[(int) fFile.length()];

            FileInputStream fis = new FileInputStream(fFile);
            fis.read(baBuffer);

            String sContent = new String(baBuffer);
            fis.close();

            if ((sContent != null) && (sContent.length() > 0))
            {
                // First strip all comments from the content.
                sContent = sContent.replaceAll("/\\*[\\d\\D]*?\\*/", "");
                // Strip line comments
                sContent = sContent.replaceAll("^[\\s]*//[^\\n]*", "");

                // Get the name of the class and the name of the package
                Matcher mPackage = Pattern.compile("package ([^;]+)").matcher(sContent);
                mPackage.find();

                String sPackageName = mPackage.group(1);

                // Get the classname
                Matcher mClass = Pattern.compile("class\\s+([^\\s\\n]+)[\\s\\n]{0,1}(extends|implements|\\{)*")
                                        .matcher(sContent);
                boolean bClass = mClass.find();

                if (bClass == true)
                {
                    String sClassName = mClass.group(1);

                    String sFQN = sPackageName + "." + sClassName.trim();

                    Matcher mPlain = m_pProperties.matcher(sContent);
                    boolean bFound = false;

                    while (mPlain.find())
                    {
                        bFound = true;

                        String sName = mPlain.group(3);
                        String sValue = mPlain.group(5);

                        if (sValue == null)
                        {
                            sValue = "unknown";
                        }
                        else if (sValue.length() == 0)
                        {
                            sValue = "<empty>";
                        }

                        // Check if the name is a static refence.
                        if (!mPlain.group(2).startsWith("\"") && (sName.indexOf(".") == -1))
                        {
                            sName = sFQN + "." + sName;
                        }

                        // If needed and possible, resolve the name.
                        sName = resolvePropertyName(sName, sFQN, sContent);

                        if (LOG.isDebugEnabled())
                        {
                            LOG.debug("Adding property " + sName + " and default value " + sValue);
                        }

                        PropertyInfo piInfo = null;

                        if (m_lhmProperties.containsKey(sName))
                        {
                            piInfo = m_lhmProperties.get(sName);

                            if (piInfo.getDefaultValue().equals("unknown") &&
                                    !sValue.equals("unknown"))
                            {
                                piInfo.setDefaultValue(sValue);
                            }

                            piInfo.addClass(new ClassInfo(sFQN, spPath));
                        }
                        else
                        {
                            // The default value. It could also be possible that the default value
                            // is something like new Integer(LDAPDirectory.LDAP_PORT Note the
                            // lacking ')' at the end.
                            Matcher mMatcher = Pattern.compile("new\\s*(Integer|String)\\s*\\((.+)")
                                                      .matcher(sValue);

                            if (mMatcher.find())
                            {
                                String sTemp = mMatcher.group(2);

                                if (sTemp.indexOf('.') > -1)
                                {
                                    String sClassname = sTemp.substring(0, sTemp.lastIndexOf('.'));
                                    String sConstName = sTemp.substring(sTemp.lastIndexOf('.') + 1);

                                    // Try to resolve the classname via an import.
                                    String sRealFQN = "";
                                    mMatcher = Pattern.compile("import\\s+([a-zA-Z0-9_.]+" +
                                                               sClassname + ")\\s*;").matcher(sContent);

                                    if (mMatcher.find())
                                    {
                                        sRealFQN = mMatcher.group(1);
                                    }

                                    // If that doesn't work assume it's within the same package
                                    // and try to get the file content.
                                    if (sRealFQN.length() == 0)
                                    {
                                        sRealFQN = sFQN.substring(0, sFQN.lastIndexOf('.')) +
                                                   sClassname;
                                    }

                                    sTemp = resolveFromOtherFile(sRealFQN, sConstName);

                                    if ((sTemp != null) && (sTemp.length() > 0))
                                    {
                                        sValue = sTemp;
                                    }
                                }
                            }
                            else
                            {
                                // It could be a constant declaration within the current class.
                                if (sValue.toUpperCase().equals(sValue) &&
                                        Pattern.compile("[a-zA-Z]").matcher(sValue).find())
                                {
                                    // An internal reference again.
                                    String sRegEx = "\\s+" + sValue +
                                                    "\\s+=\\s*\"{0,1}([^\";]+)\"{0,1}\\s*;";
                                    mMatcher = Pattern.compile(sRegEx).matcher(sContent);

                                    if (mMatcher.find())
                                    {
                                        sValue = mMatcher.group(1);
                                    }
                                }
                            }

                            // First check if the listed property should be included.
                            if (!isExceptionProperty(sName, sFQN, spPath.getComponentName()))
                            {
                                piInfo = new PropertyInfo(sName, sValue,
                                                          new ClassInfo(sFQN, spPath));
                                m_lhmProperties.put(sName, piInfo);
                            }
                        }
                    }

                    if (bFound)
                    {
                        if (LOG.isDebugEnabled())
                        {
                            LOG.debug("Found plain property in file " + fFile.getCanonicalPath());
                        }
                    }
                }
                else
                {
                    if (LOG.isDebugEnabled())
                    {
                        LOG.debug("Ignoring file " + fFile.getCanonicalPath() +
                                  " because no class was found.");
                    }
                }
            }
            else
            {
                if (LOG.isDebugEnabled())
                {
                    LOG.debug("File " + fFile.getCanonicalPath() + " is empty");
                }
            }
        }
        else
        {
            if (LOG.isDebugEnabled())
            {
                LOG.debug("File " + fFile.getCanonicalPath() + " has length 0");
            }
        }
    }

    /**
     * This method returns the element for the template for the current version.
     *
     * @param   sCordysVersion  The current Cordys version.
     *
     * @return  The template element.
     *
     * @throws  Exception  DOCUMENTME
     */
    private Element getTemplate(String sCordysVersion)
                         throws Exception
    {
        Element eReturn = null;

        if (sCordysVersion.indexOf("C2") > -1)
        {
            sCordysVersion = "C2";
        }

        Document dDoc = XMLHelper.loadXMLFile(TEMPLATEWCPPROPERTIES_XML);

        // Check if the properties for the current version exist.
        // If so, remove it.
        eReturn = (Element) XPathHelper.selectSingleNode(dDoc.getDocumentElement(),
                                                         "./cordys[@version=\"" + sCordysVersion +
                                                         "\"]");

        if (eReturn == null)
        {
            // Then just try the latest entry as the template.
            NodeList nlChildren = dDoc.getDocumentElement().getChildNodes();

            for (int iCount = nlChildren.getLength() - 1; iCount >= 0; iCount--)
            {
                Node nTemp = nlChildren.item(iCount);

                if ((nTemp.getNodeType() == Node.ELEMENT_NODE) &&
                        nTemp.getLocalName().equals("cordys"))
                {
                    // Found the previous version
                    eReturn = (Element) nTemp;
                    break;
                }
            }
        }

        return eReturn;
    }

    /**
     * This method returns whether or not the given property should be included in the list of
     * actual properties.
     *
     * @param   sPropertyName   The name of the property.
     * @param   sClassname      The classname in which it was found.
     * @param   sComponentName  The name of the component.
     *
     * @return  true if the class should not be included. Otherwise false.
     */
    private boolean isExceptionProperty(String sPropertyName, String sClassname,
                                        String sComponentName)
    {
        boolean bReturn = false;

        if (m_lhmExceptions.containsKey(sPropertyName))
        {
            String sExcString = m_lhmExceptions.get(sPropertyName);

            if (sExcString.equals(sComponentName + "|" + sClassname) ||
                    sExcString.equalsIgnoreCase("all"))
            {
                if (LOG.isDebugEnabled())
                {
                    LOG.debug("Property " + sPropertyName + " is an excluded property.");
                }
                bReturn = true;
            }
        }

        return bReturn;
    }

    /**
     * Parses all the files in the folder.
     *
     * @param   spPath   The source path in which this file resides.
     * @param   fFolder  The folder to parse.
     *
     * @throws  IOException  DOCUMENTME
     */
    private void parseFolder(SourcePath spPath, File fFolder)
                      throws IOException
    {
        String[] saList = fFolder.list(new FilenameFilter()
            {
                public boolean accept(File dir, String name)
                {
                    return (dir.isDirectory() || name.endsWith(".java"));
                }
            });

        for (int iCount = 0; iCount < saList.length; iCount++)
        {
            String sFilename = saList[iCount];
            File fFile = new File(fFolder, sFilename);

            if (fFile.isDirectory())
            {
                parseFolder(spPath, fFile);
            }
            else
            {
                if (sFilename.endsWith(".java"))
                {
                    analyzeFile(spPath, fFile);
                }
            }
        }
    }

    /**
     * This method gets the value of sConstName from class sFQN. The assumption is that it is
     * defined within the current source folder.
     *
     * @param   sFQN        The FQN of the class.
     * @param   sConstName  The name of the constant.
     *
     * @return  The resolved value. If it could not be found an empty string is returned.
     *
     * @throws  IOException  DOCUMENTME
     */
    private String resolveFromOtherFile(String sFQN, String sConstName)
                                 throws IOException
    {
        String sReturn = "";

        // If needed we'll go through all the sourcefolders to find the proper file.
        for (SourcePath spPath : m_alFolders)
        {
            File fFile = new File(spPath.getLocation(), sFQN.replaceAll("\\.", "/") + ".java");

            if (fFile.exists())
            {
                byte[] baBuffer = new byte[(int) fFile.length()];

                FileInputStream fis = new FileInputStream(fFile);
                fis.read(baBuffer);

                String sContent = new String(baBuffer);
                fis.close();

                String sRegEx = "\\s+" + sConstName + "\\s+=\\s*\"{0,1}([^\";]+)\"{0,1}\\s*;";
                Matcher mMatcher = Pattern.compile(sRegEx).matcher(sContent);

                if (mMatcher.find())
                {
                    sReturn = mMatcher.group(1);

                    if (sReturn.toUpperCase().equals(sReturn) &&
                            Pattern.compile("[a-zA-Z]").matcher(sReturn).find())
                    {
                        // An internal reference again.
                        boolean bResolved = false;

                        while (!bResolved)
                        {
                            sRegEx = "String\\s+" + sReturn +
                                     "\\s+=\\s*\"{0,1}([^\";]+)\"{0,1}\\s*;";
                            mMatcher = Pattern.compile(sRegEx).matcher(sContent);

                            if (mMatcher.find())
                            {
                                sReturn = mMatcher.group(1);

                                if (!sReturn.toUpperCase().equals(sReturn))
                                {
                                    bResolved = true;
                                }
                            }
                        }
                    }

                    break;
                }
            }
        }

        return sReturn;
    }

    /**
     * This method resolves the name of the property to the actual name for use within the
     * wcp.properties file.
     *
     * @param   sName     The name of the property.
     * @param   sFQN      The FQN of the current class.
     * @param   sContent  The content of the current class in which the property was found.
     *
     * @return  The resolved name. If the name did not need to be resolved the return value is the
     *          same as sName.
     *
     * @throws  IOException  DOCUMENTME
     */
    private String resolvePropertyName(String sName, String sFQN, String sContent)
                                throws IOException
    {
        String sReturn = sName;

        // It needs to be resolved in the following scenarios:
        // 1. If the name contains only uppercase characters.
        // 2. If the name contains a . and the last part after the . are all upper case.
        boolean bResolve = sName.toUpperCase().equals(sName);
        String sClassname = "";
        String sConstName = "";

        if (!bResolve)
        {
            // Also check the 2nd option.
            if (sName.lastIndexOf('.') > -1)
            {
                sConstName = sName.substring(sName.lastIndexOf('.') + 1);
                sClassname = sName.substring(0, sName.lastIndexOf('.'));

                if (sConstName.toUpperCase().equals(sConstName))
                {
                    bResolve = true;
                }
            }
        }
        else
        {
            // Resolve it within the current class.
            sConstName = sName;
            sClassname = sFQN;
        }

        if (bResolve)
        {
            // We need to resolve it.
            if (LOG_RESOLVER.isDebugEnabled())
            {
                LOG_RESOLVER.debug("We need to resolve " + sConstName + " within class " +
                                   sClassname + " for property " + sName + ".Current FQN: " + sFQN);
            }

            if (sFQN.equals(sClassname))
            {
                // We can resolve it within the current class.
                String sRegEx = "String\\s+" + sConstName + "\\s+=\\s*\"{0,1}([^\"]+)\"{0,1}";
                Matcher mMatcher = Pattern.compile(sRegEx).matcher(sContent);

                if (mMatcher.find())
                {
                    sReturn = mMatcher.group(1);
                }
            }
            else
            {
                // Try to resolve the classname via an import.
                String sRealFQN = "";
                Matcher mMatcher = Pattern.compile("import\\s+([a-zA-Z0-9_.]+" + sClassname +
                                                   ")\\s*;").matcher(sContent);

                if (mMatcher.find())
                {
                    sRealFQN = mMatcher.group(1);
                }

                // If that doesn't work assume it's within the same package and try to get the
                // file content.
                if (sRealFQN.length() == 0)
                {
                    sRealFQN = sFQN.substring(0, sFQN.lastIndexOf('.')) + sClassname;
                }

                String sTemp = resolveFromOtherFile(sRealFQN, sConstName);

                if ((sTemp != null) && (sTemp.length() > 0))
                {
                    sReturn = sTemp;
                }
            }

            if (LOG_RESOLVER.isDebugEnabled())
            {
                LOG_RESOLVER.debug(sConstName + " resolved to '" + sReturn + "'");
            }
        }

        return sReturn;
    }

    /**
     * Comparator for the property info objects.
     *
     * @author  pgussow
     */
    public class PropertyInfoComparator
        implements Comparator<PropertyInfo>
    {
        /**
         * This method compares the first and the second object.
         *
         * @param   o1  The first object.
         * @param   o2  The second object.
         *
         * @return  A negative integer, zero, or a positive integer as the first argument is less
         *          than, equal to, or greater than the second.
         */
        public int compare(PropertyInfo o1, PropertyInfo o2)
        {
            return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
        }
    }
}
