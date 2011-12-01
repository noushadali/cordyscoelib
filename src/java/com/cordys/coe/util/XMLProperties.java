/**
 *  2005 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys R&D B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util;

import com.cordys.coe.exception.GeneralException;
import com.cordys.coe.util.xml.nom.XPathHelper;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Node;
import com.eibus.xml.xpath.XPathMetaInfo;

import java.io.File;
import java.io.FileNotFoundException;

import java.lang.reflect.Constructor;

/**
 * This class is a wrapper around an XML property-file.
 *
 * @author  pgussow
 */
public class XMLProperties
{
    /**
     * Indicates that the value should be grabbed from an attribute.
     */
    protected static final int VIA_ATTRIBUTE = 0;
    /**
     * Indicates that the value should be grabbed from an child node.
     */
    protected static final int VIA_TAG = 1;
    /**
     * Holds a reference to the document that should be used if strings have to be parsed.
     */
    private static Document s_dDoc = null;
    /**
     * Holds whether or not the object is dirty.
     */
    private boolean m_bDirty;
    /**
     * This boolean indicates whether or not this class should clean up the XML node that was passed
     * on.
     */
    private boolean m_bShouldCleanup = false;
    /**
     * Holds the XMLNode.
     */
    private int m_iPropNode;
    /**
     * Holds the namespace that should be used.
     */
    private String m_sNamespace = "";
    /**
     * Holds the prefix that should be used.
     */
    private String m_sPrefix = "";
    /**
     * Holds the namespace/prefix mapping for this class.
     */
    private XPathMetaInfo m_xmi = new XPathMetaInfo();

    /**
     * Constructor. Creates the class based on an XML-node.
     */
    public XMLProperties()
    {
        if (s_dDoc == null)
        {
            s_dDoc = new Document();
        }
    }

    /**
     * Constructor. Creates the class based on a file.
     *
     * @param   sFilename  The name of the file in which the xml is located
     *
     * @throws  GeneralException       In case of any exceptions.
     * @throws  FileNotFoundException  In case the property file is not found.
     */
    public XMLProperties(String sFilename)
                  throws GeneralException, FileNotFoundException
    {
        // See if the file is available
        File fFile = new File(sFilename);

        if (!fFile.exists())
        {
            throw new FileNotFoundException("Propertiesfile not found.");
        }

        // Load the XML-file
        try
        {
            m_iPropNode = s_dDoc.load(sFilename);
            m_bShouldCleanup = true;

            // Check if namespaces are needed.
            checkForNamespaceNeed();
        }
        catch (Exception e)
        {
            throw new GeneralException(e.fillInStackTrace(), "Error loading the propertiesfile.");
        }
        m_bDirty = false;
    }

    /**
     * Constructor. Creates the class based on an XML-node.
     *
     * @param   iPropNode  The rootnode for the properties.
     *
     * @throws  GeneralException  In case of any exceptions.
     */
    public XMLProperties(int iPropNode)
                  throws GeneralException
    {
        if (iPropNode != 0)
        {
            m_iPropNode = iPropNode;

            // Check if namespaces are needed.
            checkForNamespaceNeed();
        }
        else
        {
            throw new GeneralException("XML-node cannot be 0.");
        }
    }

    /**
     * This method deletes the main configuration node.
     */
    public void cleanUp()
    {
        if (m_bShouldCleanup)
        {
            Node.delete(m_iPropNode);
        }
        m_iPropNode = 0;
    }

    /**
     * This method returns the Boolean-value of the given key.
     *
     * @param   sKey  The key to get the value of.
     *
     * @return  The Boolean-value of the given key.
     */
    public boolean getAttrBooleanValue(String sKey)
    {
        return getBooleanValue(VIA_ATTRIBUTE, sKey, false);
    }

    /**
     * This method returns the Boolean-value of the given key.
     *
     * @param   sKey      The key to get the value of.
     * @param   bDefault  The default value.
     *
     * @return  The Boolean-value of the given key.
     */
    public boolean getAttrBooleanValue(String sKey, boolean bDefault)
    {
        return getBooleanValue(VIA_ATTRIBUTE, sKey, bDefault);
    }

    /**
     * This method returns the Integer-value of the given key.
     *
     * @param   sKey  The key to get the value of.
     *
     * @return  The Integer-value of the given key.
     */
    public int getAttrIntegerValue(String sKey)
    {
        return getIntegerValue(VIA_ATTRIBUTE, sKey, -1);
    }

    /**
     * This method returns the Integer-value of the given key.
     *
     * @param   sKey      The key to get the value of.
     * @param   iDefault  the default value.
     *
     * @return  The Integer-value of the given key.
     */
    public int getAttrIntegerValue(String sKey, int iDefault)
    {
        return getIntegerValue(VIA_ATTRIBUTE, sKey, iDefault);
    }

    /**
     * This method returns the long-value of the data of the given tag. If that tag cannot be found
     * -1 is returned.
     *
     * @param   sKey  The key to get the value of.
     *
     * @return  The long-value of the key.
     */
    public long getAttrLongValue(String sKey)
    {
        return getLongValue(VIA_ATTRIBUTE, sKey, -1);
    }

    /**
     * This method returns the long-value of the data of the given tag. If that tag cannot be found
     * -1 is returned.
     *
     * @param   sKey      The key to get the value of.
     * @param   lDefault  The default value to return.
     *
     * @return  The long-value of the key.
     */
    public long getAttrLongValue(String sKey, long lDefault)
    {
        return getLongValue(VIA_ATTRIBUTE, sKey, lDefault);
    }

    /**
     * This method returns the value of the data of the given tag. If that tag cannot be found
     * <code>null</code> is returned.
     *
     * @param   sKey  The key to get the value of.
     *
     * @return  The value of the key.
     */
    public String getAttrStringValue(String sKey)
    {
        return getStringValue(VIA_ATTRIBUTE, sKey, null);
    }

    /**
     * This method returns the value of the data of the given tag. If that tag cannot be found the
     * default is returned.
     *
     * @param   sKey      The key to get the value of.
     * @param   sDefault  The default value to return.
     *
     * @return  The long-value of the key.
     */
    public String getAttrStringValue(String sKey, String sDefault)
    {
        return getStringValue(VIA_ATTRIBUTE, sKey, sDefault);
    }

    /**
     * This method returns the Boolean-value of the given key.
     *
     * @param   sKey  The key to get the value of.
     *
     * @return  The Boolean-value of the given key.
     */
    public boolean getBooleanValue(String sKey)
    {
        return getBooleanValue(VIA_TAG, sKey, false);
    }

    /**
     * This method returns the Boolean-value of the given key.
     *
     * @param   sKey      The key to get the value of.
     * @param   bDefault  The default value.
     *
     * @return  The Boolean-value of the given key.
     */
    public boolean getBooleanValue(String sKey, boolean bDefault)
    {
        return getBooleanValue(VIA_TAG, sKey, bDefault);
    }

    /**
     * This method returns the XML node it wraps.
     *
     * @return  The XML node it wraps.
     */
    public int getConfigNode()
    {
        return m_iPropNode;
    }

    /**
     * This method returns the Integer-value of the given key.
     *
     * @param   sKey  The key to get the value of.
     *
     * @return  The Integer-value of the given key.
     */
    public int getIntegerValue(String sKey)
    {
        return getIntegerValue(VIA_TAG, sKey, -1);
    }

    /**
     * This method returns the Integer-value of the given key.
     *
     * @param   sKey      The key to get the value of.
     * @param   iDefault  the default value.
     *
     * @return  The Integer-value of the given key.
     */
    public int getIntegerValue(String sKey, int iDefault)
    {
        return getIntegerValue(VIA_TAG, sKey, iDefault);
    }

    /**
     * This method returns the long-value of the data of the given tag. If that tag cannot be found
     * -1 is returned.
     *
     * @param   sKey  The key to get the value of.
     *
     * @return  The long-value of the key.
     */
    public long getLongValue(String sKey)
    {
        return getLongValue(VIA_TAG, sKey, -1);
    }

    /**
     * This method returns the long-value of the data of the given tag. If that tag cannot be found
     * -1 is returned.
     *
     * @param   sKey      The key to get the value of.
     * @param   lDefault  The default value to return.
     *
     * @return  The long-value of the key.
     */
    public long getLongValue(String sKey, long lDefault)
    {
        return getLongValue(VIA_TAG, sKey, lDefault);
    }

    /**
     * This method returns an array containing the XMLProperties for the given XQL-path.<br>
     * Note: If your XML uses namespaces, please use the ns: prefix for the namespace of the root.
     *
     * @param   sXPath  The XPath to execute
     *
     * @return  an array containing the configuration.
     *
     * @throws  GeneralException  In case of any exceptions.
     */
    public XMLProperties[] getProperties(String sXPath)
                                  throws GeneralException
    {
        int[] aiNodes = XPathHelper.selectNodes(m_iPropNode, sXPath, m_xmi);

        XMLProperties[] axpReturn = new XMLProperties[aiNodes.length];

        for (int iCount = 0; iCount < aiNodes.length; iCount++)
        {
            axpReturn[iCount] = new XMLProperties();
            axpReturn[iCount].setBaseXMLNode(aiNodes[iCount]);
        }

        return axpReturn;
    }

    /**
     * This method returns an array containing the XMLProperties for the given XQL-path. The class
     * passed on should extend from XMLProperties and have a constructor that takes an int.
     *
     * @param   sXQL    The XQL to execute
     * @param   cClass  The class to instantiate.
     *
     * @return  an array containing the configuration.
     *
     * @throws  GeneralException  In case of any exceptions.
     */
    public XMLProperties[] getProperties(String sXQL, Class<?> cClass)
                                  throws GeneralException
    {
        return getProperties(sXQL, cClass, false);
    }

    /**
     * This method returns an array containing the XMLProperties for the given XQL-path. The class
     * passed on should extend from XMLProperties and have a constructor that takes an int.<br>
     * Note: If your XML uses namespaces, please use the ns: prefix for the namespace of the root.
     *
     * @param   sXPath      The XPath to execute
     * @param   cClass      The class to instantiate.
     * @param   bDuplicate  Indicates whether or not to duplicate the node.
     *
     * @return  an array containing the configuration.
     *
     * @throws  GeneralException  In case of any exceptions.
     */
    public XMLProperties[] getProperties(String sXPath, Class<?> cClass, boolean bDuplicate)
                                  throws GeneralException
    {
        // First find the proper constructor
        Constructor<?> cConstructor = null;

        try
        {
            cConstructor = cClass.getConstructor(new Class[] { int.class });
        }
        catch (SecurityException e)
        {
            throw new GeneralException(e, "Error finding constructor.");
        }
        catch (NoSuchMethodException e)
        {
            throw new GeneralException(e, "Error finding constructor.");
        }

        // Now find the proper ones.
        int[] aiNodes = XPathHelper.selectNodes(m_iPropNode, sXPath, m_xmi);
        XMLProperties[] axpReturn = new XMLProperties[aiNodes.length];

        for (int iCount = 0; iCount < aiNodes.length; iCount++)
        {
            try
            {
                int iTriggerNode = aiNodes[iCount];

                if (bDuplicate == true)
                {
                    iTriggerNode = Node.duplicate(iTriggerNode);
                }

                axpReturn[iCount] = (XMLProperties) cConstructor.newInstance(new Object[]
                                                                             {
                                                                                 new Integer(iTriggerNode)
                                                                             });
            }
            catch (Exception e)
            {
                throw new GeneralException(e, "Error creating the XMLProperties class.");
            }
        }

        return axpReturn;
    }

    /**
     * This method returns the String-value of the given key.
     *
     * @param   sKey  The key to get the value of.
     *
     * @return  The String-value of the given key.
     */
    public String getStringValue(String sKey)
    {
        return getStringValue(VIA_TAG, sKey, null);
    }

    /**
     * This method returns the String-value of the given key.
     *
     * @param   sKey      The key to get the value of.
     * @param   sDefault  The default value to return.
     *
     * @return  The String-value of the given key.
     */
    public String getStringValue(String sKey, String sDefault)
    {
        return getStringValue(VIA_TAG, sKey, sDefault);
    }

    /**
     * This method returns the XML-node pointing to the key.
     *
     * @param   sKey  The key to get the value of.
     *
     * @return  The XML-node pointing to the key.
     */
    public int getXMLNode(String sKey)
    {
        return XPathHelper.selectSingleNode(m_iPropNode, m_sPrefix + sKey, m_xmi);
    }

    /**
     * This method returns a new XMLProperties-class for the given key. If the key was not found,
     * null is returned.
     *
     * @param   sKey  The key to get the value of.
     *
     * @return  A new XMLProperties-class for the given key.
     *
     * @throws  GeneralException  In case of any exceptions.
     */
    public XMLProperties getXMLProperties(String sKey)
                                   throws GeneralException
    {
        XMLProperties xReturn = null;
        int iTmp = getXMLNode(sKey);

        if (iTmp != 0)
        {
            xReturn = new XMLProperties();
            xReturn.setBaseXMLNode(iTmp);
        }

        return xReturn;
    }

    /**
     * This method returns a new XMLProperties-class for the given key. If the key was not found,
     * null is returned. The class passed on should extend from XMLProperties and have a constructor
     * that takes an int.
     *
     * @param   sKey    The key to get the value of.
     * @param   cClass  The class that should be instantiated instead of XMLProperties. The class
     *                  should extend XMLProperties though.
     *
     * @return  A new XMLProperties-class for the given key.
     *
     * @throws  GeneralException  In case of any exceptions.
     */
    public XMLProperties getXMLProperties(String sKey, Class<?> cClass)
                                   throws GeneralException
    {
        XMLProperties xReturn = null;

        String sXPathFilter = sKey;

        XMLProperties[] axpTemp = getProperties(sXPathFilter, cClass);

        if (axpTemp.length > 0)
        {
            xReturn = axpTemp[0];
        }

        return xReturn;
    }

    /**
     * Returns whether or not the object is dirty.
     *
     * @return  Whether or not the object is dirty.
     */
    public boolean isDirty()
    {
        return m_bDirty;
    }

    /**
     * This method sets the XML that should be used.
     *
     * @param   sXML  The rootnode for the properties.
     *
     * @throws  GeneralException  In case of any exceptions.
     */
    public void setBaseXML(String sXML)
                    throws GeneralException
    {
        try
        {
            m_iPropNode = s_dDoc.load(sXML.getBytes());
            m_bShouldCleanup = true;
        }
        catch (Exception e)
        {
            throw new GeneralException(e, "Error parsing the XML");
        }

        if (m_iPropNode == 0)
        {
            throw new GeneralException("XML-node cannot be 0.");
        }
    }

    /**
     * This method sets the XML node that should be used.
     *
     * @param   iPropNode  The rootnode for the properties.
     *
     * @throws  GeneralException  In case of any exceptions.
     */
    public void setBaseXMLNode(int iPropNode)
                        throws GeneralException
    {
        if (iPropNode == 0)
        {
            throw new GeneralException("XML-node cannot be 0.");
        }
        this.m_iPropNode = iPropNode;
    }

    /**
     * This method changes the value of the passed on property.
     *
     * @param  sKey    The name of the property
     * @param  sValue  The value of the property.
     */
    public void setValue(String sKey, String sValue)
    {
        int iNode = XPathHelper.selectSingleNode(m_iPropNode, m_sPrefix + sKey, m_xmi);

        if (iNode != 0)
        {
            // Remove all children.
            int iDataNode = Node.getFirstDataNode(iNode);

            while (iDataNode != 0)
            {
                int iTmp = iDataNode;
                iDataNode = Node.getNextSibling(iDataNode);
                Node.unlink(iTmp);
                Node.delete(iTmp);
            }

            String sRealValue = sValue;

            if (sRealValue == null)
            {
                sRealValue = "";
            }

            Document dDoc = Node.getDocument(iNode);
            dDoc.createText(sRealValue, iNode);
        }

        m_bDirty = true;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return  A string representation of the object.
     */
    @Override public String toString()
    {
        return Node.writeToString(m_iPropNode, false);
    }

    /**
     * This method returns the Boolean-value of the given key.
     *
     * @param   iType     The way the value should be retrieved (VIA_ATTRIBUTE or VIA_TAG)
     * @param   sKey      The key to get the value of.
     * @param   bDefault  The default value.
     *
     * @return  The Boolean-value of the given key.
     */
    protected boolean getBooleanValue(int iType, String sKey, boolean bDefault)
    {
        boolean bReturn = bDefault;

        try
        {
            String sTemp = getStringValue(iType, sKey, String.valueOf(bDefault));

            if ((sTemp != null) && !sTemp.equals(""))
            {
                bReturn = Boolean.valueOf(sTemp).booleanValue();
            }
        }
        catch (Exception e)
        {
            // Ignore exception, return the default.
        }
        return bReturn;
    }

    /**
     * This method returns the Integer-value of the given key.
     *
     * @param   iType     The way the value should be retrieved (VIA_ATTRIBUTE or VIA_TAG)
     * @param   sKey      The key to get the value of.
     * @param   iDefault  the default value.
     *
     * @return  The Integer-value of the given key.
     */
    protected int getIntegerValue(int iType, String sKey, int iDefault)
    {
        int iReturn = iDefault;

        try
        {
            String sTemp = getStringValue(iType, sKey, String.valueOf(iDefault));

            if ((sTemp != null) && !sTemp.equals(""))
            {
                iReturn = Integer.parseInt(sTemp);
            }
        }
        catch (Exception e)
        {
            // Ignore exception, return the default.
        }
        return iReturn;
    }

    /**
     * This method returns the long-value of the data of the given tag. If that tag cannot be found
     * -1 is returned.
     *
     * @param   iType     The way the value should be retrieved (VIA_ATTRIBUTE or VIA_TAG)
     * @param   sKey      The key to get the value of.
     * @param   lDefault  The default value to return.
     *
     * @return  The long-value of the key.
     */
    protected long getLongValue(int iType, String sKey, long lDefault)
    {
        long lReturn = lDefault;

        try
        {
            String sTemp = getStringValue(iType, sKey, String.valueOf(lDefault));

            if ((sTemp != null) && !sTemp.equals(""))
            {
                lReturn = Long.valueOf(sTemp).longValue();
            }
        }
        catch (Exception e)
        {
            // Ignore exception, return the default.
        }

        return lReturn;
    }

    /**
     * This method returns the String-value of the given key.
     *
     * @param   iType     The way the value should be retrieved (VIA_ATTRIBUTE or VIA_TAG)
     * @param   sKey      The key to get the value of.
     * @param   sDefault  The default value to return.
     *
     * @return  The String-value of the given key.
     */
    protected String getStringValue(int iType, String sKey, String sDefault)
    {
        String sReturn = sDefault;

        try
        {
            if (iType == VIA_TAG)
            {
                sReturn = XPathHelper.getStringValue(m_iPropNode, m_sPrefix + sKey + "/text()",
                                                     m_xmi, sDefault);
            }
            else if (iType == VIA_ATTRIBUTE)
            {
                sReturn = Node.getAttribute(m_iPropNode, sKey, sDefault);
            }
        }
        catch (Exception e)
        {
            // Ignore exception, return the default.
        }
        return sReturn;
    }

    /**
     * This method returns the data of the given tag. If that tag cannot be found 'null' is
     * returned.
     *
     * @param   sTag  The tag to get the data of.
     *
     * @return  The data of the tag.
     */
    protected String getTagValue(String sTag)
    {
        String sReturn = null;
        int iTmpNode = XPathHelper.selectSingleNode(m_iPropNode, m_sPrefix + sTag, m_xmi);

        if (iTmpNode != 0)
        {
            sReturn = Node.getDataWithDefault(iTmpNode, null);
        }
        return sReturn;
    }

    /**
     * This method checks if namespaces are needed.
     */
    private void checkForNamespaceNeed()
    {
        // Check if namespaces are needed.
        String sNamespace = Node.getNamespaceURI(m_iPropNode);

        if ((sNamespace != null) && (sNamespace.length() > 0))
        {
            m_sNamespace = sNamespace;
            m_sPrefix = "ns:";
            m_xmi.addNamespaceBinding("ns", m_sNamespace);
        }
    }
}
