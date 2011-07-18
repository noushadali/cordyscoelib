package com.cordys.coe.util.xml.dom;

import com.cordys.coe.util.Bits;
import com.cordys.coe.util.exceptions.XMLWrapperException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.TimeZone;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class functions as a wrapper around a piece of XML to enable easy access to the values
 * without doing XPath statements. Other classes can just use the getters on this class. NOTE: All
 * dates are stored as UTC. This means that when you write the object to XML the date will be in UTC
 * format.
 *
 * @author  pgussow
 */
public class XMLProperties
    implements IXMLProperties
{
    /**
     * DOCUMENTME.
     */
    private static final Integer WRAPPER_ALL_KEY = new Integer(ALL_KEY);
    /**
     * DOCUMENTME.
     */
    private static final Integer WRAPPER_TECHNICAL_KEY = new Integer(TECHNICAL_KEY);
    /**
     * DOCUMENTME.
     */
    private static final Integer WRAPPER_FUNCTIONAL_KEY = new Integer(FUNCTIONAL_KEY);
    /**
     * Holds all the keys for the specific class.
     */
    private static HashMap<Object, HashMap<Integer, LinkedHashSet<String>>> s_oKeys = new HashMap<Object, HashMap<Integer, LinkedHashSet<String>>>();
    /**
     * Indicates whether or not the values of this object have changed.
     */
    private boolean m_bChanged = false;
    /**
     * Holds the date format used.
     */

    // TODO: switch when support case 38519 is fixed
    // private DateFormat m_dfFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private DateFormat m_dfFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.0");
    /**
     * This hashmap contains the current values for the object.
     */
    private LinkedHashMap<String, String> m_lhmNewValues = null;
    /**
     * Holds the configuration node.
     */
    private Node m_nOriginalNode = null;
    /**
     * Holds the prefix of the configuration node. this is needed for the XPath.
     */
    private String m_sPrefix = "";
    /**
     * Holds the roottag of the object.
     */
    private String m_sRootTag = "";

    /**
     * Constructor. Creates the object.
     *
     * @param  sRootTag  The root tag.
     */
    public XMLProperties(String sRootTag)
    {
        m_sRootTag = sRootTag;

        // Internally we need to store the date as a UTC date. So we need to convert it before we
        // store it.
        Calendar cCal = Calendar.getInstance();

        // A call to get() is required before setting the timezone to force the
        // protected complete() method to be called and make setTimeZone()
        // actually translate the date.
        cCal.get(Calendar.HOUR_OF_DAY);
        cCal.setTimeZone(TimeZone.getTimeZone("UTC"));
        cCal.get(Calendar.HOUR_OF_DAY);

        m_dfFormatter.setCalendar(cCal);
    }

    /**
     * This method returns whether or not the given objects are equal to eachother. An object is the
     * same if all the fields have the same values.
     *
     * @param   oObject  The other object to compare it with.
     *
     * @return  true if the objects are equal. Otherwise false.
     *
     * @see     java.lang.Object#equals(java.lang.Object)
     */
    @Override public boolean equals(Object oObject)
    {
        if (oObject instanceof IXMLProperties)
        {
            return equals((IXMLProperties) oObject);
        }
        return super.equals(oObject);
    }

    /**
     * This method checks if the passed on XMLProperties has the same keys and values as this
     * object.
     *
     * @param   xpProps  The XML properties to compare with.
     *
     * @return  true if both pieces of XML are the same. Otherwise false.
     */
    public boolean equals(IXMLProperties xpProps)
    {
        boolean bReturn = true;

        Iterator<String> itKeys = getKeyIterator();

        while (itKeys.hasNext())
        {
            String sKey = (String) itKeys.next();
            String sOwnValue = getStringValue(sKey);
            String sOtherValue = xpProps.getStringValue(sKey);

            if (!(((sOwnValue != null) && (sOtherValue != null) && sOwnValue.equals(sOtherValue)) ||
                      ((sOwnValue == null) && (sOtherValue == null))))
            {
                bReturn = false;

                break;
            }
        }

        return bReturn;
    }

    /**
     * This method checks if the passed on XMLProperties has the values as this object. It will
     * compare all data in all fields with eachother.
     *
     * @param   xpProps  The XML properties to compare with.
     *
     * @return  true if both pieces of XML are the same. Otherwise false.
     */
    public boolean fullEquals(IXMLProperties xpProps)
    {
        boolean bReturn = true;

        LinkedHashMap<String, String> lhmCurrent = getCurrentValues();
        Iterator<String> itKeys = lhmCurrent.keySet().iterator();

        while (itKeys.hasNext())
        {
            String sKey = itKeys.next();
            String sOwnValue = getStringValue(sKey);
            String sOtherValue = xpProps.getStringValue(sKey);

            if (!(((sOwnValue != null) && (sOtherValue != null) && sOwnValue.equals(sOtherValue)) ||
                      ((sOwnValue == null) && (sOtherValue == null))))
            {
                bReturn = false;

                break;
            }
        }

        return bReturn;
    }

    /**
     * This method returns all the fields for this property class.
     *
     * @return  All the fields for this property class.
     */
    public LinkedHashSet<String> getAllKeys()
    {
        return getKeys(WRAPPER_ALL_KEY);
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
        return getBooleanValue(sKey, false);
    }

    /**
     * This method returns the Boolean-value of the given key.
     *
     * @param   sKey      The key to get the value of.
     * @param   bDefault  The default value if the key was not found.
     *
     * @return  The Boolean-value of the given key.
     */
    public boolean getBooleanValue(String sKey, boolean bDefault)
    {
        boolean bReturn = bDefault;

        try
        {
            String sTemp = getStringValue(sKey, String.valueOf(bDefault));

            if ((sTemp != null) && !sTemp.equals(""))
            {
                bReturn = Boolean.valueOf(sTemp).booleanValue();
            }
        }
        catch (Exception e)
        {
            log("Error getting the boolean value for " + sKey, e);
        }

        return bReturn;
    }

    /**
     * This method gets the configuration node.
     *
     * @return  The configuration node.
     */
    public Node getConfigNode()
    {
        return m_nOriginalNode;
    }

    /**
     * This method gets the date formatter that is used for storing a date.
     *
     * @return  The date formatter that is used for storing a date.
     */
    public DateFormat getDateFormatter()
    {
        return m_dfFormatter;
    }

    /**
     * This method returns the datetime-value of the given key.
     *
     * @param   sKey  The key to get the value of.
     *
     * @return  The DateTime-value of the given key.
     */
    public Date getDateTimeValue(String sKey)
    {
        return getDateTimeValue(sKey, null);
    }

    /**
     * This method returns the Date-value of the given key.
     *
     * @param   sKey      The key to get the value of.
     * @param   oDefault  The default value to return is the key was not found.
     *
     * @return  The date-value of the given key.
     */
    public Date getDateTimeValue(String sKey, Date oDefault)
    {
        Date oReturn = oDefault;

        try
        {
            String sTemp = getStringValue(sKey, null);

            if ((sTemp != null) && !sTemp.equals(""))
            {
                SimpleDateFormat UTCFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

                // Internally we are storing the date as a UTC date. So we need to convert it
                // before we store it.
                Calendar cCal = Calendar.getInstance();

                // A call to get() is required before setting the timezone to force the
                // protected complete() method to be called and make setTimeZone()
                // actually translate the date.
                cCal.get(Calendar.HOUR_OF_DAY);
                cCal.setTimeZone(TimeZone.getTimeZone("UTC"));
                cCal.get(Calendar.HOUR_OF_DAY);

                UTCFormat.setCalendar(cCal);

                oReturn = UTCFormat.parse(sTemp);
            }
        }
        catch (Exception e)
        {
            log("Error getting the date/time value for " + sKey, e);
        }

        return oReturn;
    }

    /**
     * This method returns all the fields for this property class that are in the functional key.
     *
     * @return  All the fields for this property class that are in the functional key.
     */
    public LinkedHashSet<String> getFunctionalKeys()
    {
        return getKeys(WRAPPER_FUNCTIONAL_KEY);
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
        return getIntegerValue(sKey, -1);
    }

    /**
     * This method returns the Integer-value of the given key.
     *
     * @param   sKey      The key to get the value of.
     * @param   iDefault  The default value to return is the key was not found.
     *
     * @return  The Integer-value of the given key.
     */
    public int getIntegerValue(String sKey, int iDefault)
    {
        int iReturn = iDefault;

        try
        {
            String sTemp = getStringValue(sKey, String.valueOf(iDefault));

            if ((sTemp != null) && !sTemp.equals(""))
            {
                iReturn = Integer.parseInt(sTemp);
            }
        }
        catch (Exception e)
        {
            log("Error getting the integer value for " + sKey, e);
        }

        return iReturn;
    }

    /**
     * This method returns the registered keys for these properties. If the hashmap hasn't been
     * created yet, we will create the hashmap for this class. Within the hashmap there will be 3
     * other hashmaps. For each type of key there is a seperate hashmap.
     *
     * @return  The registered keys for these properties.
     */
    public HashMap<Integer, LinkedHashSet<String>> getKeys()
    {
        Object oKey4Keys = this.getClass();
        HashMap<Integer, LinkedHashSet<String>> oMap = s_oKeys.get(oKey4Keys);

        if (null == oMap)
        {
            synchronized (this)
            {
                oMap = new HashMap<Integer, LinkedHashSet<String>>();
                s_oKeys.put(oKey4Keys, oMap);

                // Add the hashmaps for the different keys.
                oMap.put(WRAPPER_ALL_KEY, new LinkedHashSet<String>());
                oMap.put(WRAPPER_TECHNICAL_KEY, new LinkedHashSet<String>());
                oMap.put(WRAPPER_FUNCTIONAL_KEY, new LinkedHashSet<String>());

                initializeKeys();
            }
        }

        return oMap;
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
        return getLongValue(sKey, -1);
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
        long lReturn = lDefault;

        try
        {
            String sTemp = getStringValue(sKey, String.valueOf(lDefault));

            if ((sTemp != null) && !sTemp.equals(""))
            {
                lReturn = Long.valueOf(sTemp).longValue();
            }
        }
        catch (Exception e)
        {
            log("Error getting the long value for " + sKey, e);
        }

        return lReturn;
    }

    /**
     * This method returns an array containing the XMLProperties for the given XQL-path.
     *
     * @param   sXQL  The XQL to execute
     *
     * @return  an array containing the configuration.
     */
    public IXMLProperties[] getProperties(String sXQL)
    {
        IXMLProperties[] axpReturn = null;

        NodeList nlNodes = null;

        try
        {
            nlNodes = XPathHelper.selectNodeList(m_nOriginalNode, "./" + getPrefix4XPath() + sXQL);
        }
        catch (TransformerException e)
        {
            log("Error getting the XMLProperties instance for " + sXQL, e);
        }

        if (nlNodes != null)
        {
            axpReturn = new IXMLProperties[nlNodes.getLength()];

            for (int iCount = 0; iCount < nlNodes.getLength(); iCount++)
            {
                axpReturn[iCount] = new XMLProperties(nlNodes.item(iCount).getLocalName());
                axpReturn[iCount].initializeData(nlNodes.item(iCount));
            }
        }

        return axpReturn;
    }

    /**
     * This method gets the root tag for this object.
     *
     * @return  The root tag for this object.
     */
    public String getRootTag()
    {
        return m_sRootTag;
    }

    /**
     * This method returns the short-value of the given key.
     *
     * @param   sKey  The key to get the value of.
     *
     * @return  The short-value of the given key.
     */
    public short getShortValue(String sKey)
    {
        return getShortValue(sKey, Short.MIN_VALUE);
    }

    /**
     * This method returns the Short-value of the given key.
     *
     * @param   sKey      The key to get the value of.
     * @param   iDefault  The default value to return is the key was not found.
     *
     * @return  The short-value of the given key.
     */
    public short getShortValue(String sKey, short iDefault)
    {
        short iReturn = iDefault;

        try
        {
            String sTemp = getStringValue(sKey, String.valueOf(iDefault));

            if ((sTemp != null) && !sTemp.equals(""))
            {
                iReturn = Short.parseShort(sTemp);
            }
        }
        catch (Exception e)
        {
            log("Error getting the short value for " + sKey, e);
        }

        return iReturn;
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
        return getStringValue(sKey, null);
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
        String sReturn = sDefault;

        try
        {
            // Get the hashmap with all the current values.
            LinkedHashMap<String, String> lhmValues = getCurrentValues();

            Object oValue = lhmValues.get(sKey);

            if (oValue != null)
            {
                sReturn = oValue.toString();
            }
        }
        catch (Exception e)
        {
            log("Error getting the value for " + sKey, e);
        }

        return sReturn;
    }

    /**
     * This method returns all the fields for this property class that are in the technical key.
     *
     * @return  All the fields for this property class that are in the technical key.
     */
    public LinkedHashSet<String> getTechnicalKeys()
    {
        return getKeys(WRAPPER_TECHNICAL_KEY);
    }

    /**
     * This method returns the XML-node pointing to the key.
     *
     * @param   sKey  The key to get the value of.
     *
     * @return  The XML-node pointing to the key.
     */
    public Node getXMLNode(String sKey)
    {
        Node nReturn = null;

        try
        {
            nReturn = XPathHelper.selectSingleNode(m_nOriginalNode,
                                                   "./" + getPrefix4XPath() + sKey);
        }
        catch (TransformerException e)
        {
            log("Error getting the XML node value for " + sKey, e);
        }

        return nReturn;
    }

    /**
     * This method returns a new XMLProperties-class for the given key. If the key was not found,
     * null is returned.
     *
     * @param   sKey  The key to get the value of.
     *
     * @return  A new XMLProperties-class for the given key.
     */
    public IXMLProperties getXMLProperties(String sKey)
    {
        IXMLProperties xReturn = null;

        Node nTemp = null;

        try
        {
            nTemp = XPathHelper.selectSingleNode(m_nOriginalNode, "./" + getPrefix4XPath() + sKey);
        }
        catch (TransformerException e)
        {
            log("Error getting the XMLProperties instance " + sKey, e);
        }

        if (nTemp != null)
        {
            xReturn = new XMLProperties(nTemp.getLocalName());
            xReturn.initializeData(nTemp);
        }

        return xReturn;
    }

    /**
     * DOCUMENTME.
     *
     * @return  DOCUMENTME
     */
    @Override public int hashCode()
    {
        int iCode = 12345678;
        Iterator<String> itKeys = getFunctionalKeys().iterator();

        while (itKeys.hasNext())
        {
            String sKey = (String) itKeys.next();
            String sOwnValue = getStringValue(sKey);

            if (null == sOwnValue)
            {
                iCode *= 2;
            }
            else
            {
                iCode ^= sOwnValue.hashCode();
            }
        }

        return iCode;
    }

    /**
     * This method sets the original data for this object and reads the prefix from the original
     * node.
     *
     * @param  nOriginalData  The data to initialize the object with.
     */
    public void initializeData(Node nOriginalData)
    {
        m_nOriginalNode = nOriginalData;
        m_sPrefix = nOriginalData.getPrefix();
        m_lhmNewValues = null;
    }

    /**
     * This method gets whether or not the object's values have changed.
     *
     * @return  Whether or not the object's values have changed.
     */
    public boolean isChanged()
    {
        return m_bChanged;
    }

    /**
     * This method sets the date formatter that is used for storing a date.
     *
     * @param  dfFormatter  The date formatter that is used for storing a date.
     */
    public void setDateFormatter(DateFormat dfFormatter)
    {
        this.m_dfFormatter = dfFormatter;
    }

    /**
     * This method sets the value for a specific field. The actual value stored will be
     * oValue.toString();
     *
     * @param  sField  The name of the field.
     * @param  oValue  The actual value.
     */
    public void setValue(String sField, Object oValue)
    {
        LinkedHashMap<String, String> lhmValues = getCurrentValues();
        String sStringValue = null;

        if (oValue instanceof Date)
        {
            Date dDate = (Date) oValue;

            // Because the date formatter is initialized with the UTC timezone the internal date
            // is UTC.
            String sFormatted = m_dfFormatter.format(dDate);
            sStringValue = sFormatted;
        }
        else
        {
            if (null != oValue)
            {
                sStringValue = oValue.toString();
            }
        }
        lhmValues.put(sField, sStringValue);

        // TODO: This might need to be more extensive and also validate against the original XML.
        m_bChanged = true;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return  A string representation of the object.
     */
    @Override public String toString()
    {
        return XMLHelper.XML2String(m_nOriginalNode) + "\nActual values:\n" +
               m_lhmNewValues.toString();
    }

    /**
     * This method returns the current values in an XML structure.
     *
     * @param   dDoc  The document to use to create the XML.
     *
     * @return  The current values in an XML structure.
     */
    public Node toXML(Document dDoc)
    {
        Node nRootTag = dDoc.createElement(m_sRootTag);

        // Now do all the values.
        LinkedHashMap<String, String> lhmValues = getCurrentValues();

        for (Iterator<String> iFields = lhmValues.keySet().iterator(); iFields.hasNext();)
        {
            String sField = iFields.next();
            String sValue = lhmValues.get(sField);

            XMLHelper.createTextElement(sField, sValue, nRootTag);
        }

        return nRootTag;
    }

    /**
     * This method returns the current values in an XML structure.
     *
     * @param   eParent  The document to use to create the XML.
     *
     * @return  The current values in an XML structure.
     */
    public Element toXML(Element eParent)
    {
        Element eReturn = (Element) XMLHelper.createElement(m_sRootTag, eParent);

        // Now do all the values.
        LinkedHashMap<String, String> lhmValues = getCurrentValues();

        for (Iterator<String> iFields = lhmValues.keySet().iterator(); iFields.hasNext();)
        {
            String sField = iFields.next();
            String sValue = lhmValues.get(sField);

            XMLHelper.createTextElement(sField, sValue, eReturn);
        }

        return eReturn;
    }

    /**
     * This method creates the XML representing the tuple for this object. If there was an original
     * XML that XML is incorperated in the tuple as the old tag. If there is no original xml just
     * the tuple-new is returned.
     *
     * @param   nParent  The node under which to create the XML.
     *
     * @throws  XMLWrapperException  DOCUMENTME
     */
    @SuppressWarnings("deprecation")
    public void toXMLTuple(Node nParent)
                    throws XMLWrapperException
    {
        Document dDoc = nParent.getOwnerDocument();

        Node nTuple = dDoc.createElement("tuple");

        if (m_nOriginalNode != null)
        {
            Node nOld = dDoc.createElement("old");
            nTuple.appendChild(nOld);

            // Import the old one into this document.
            Node nCopy = dDoc.importNode(XMLHelper.stripPrefixes(m_nOriginalNode), true);
            nOld.appendChild(nCopy);
        }

        Node nNew = dDoc.createElement("new");
        nTuple.appendChild(nNew);

        // Create the XML for the current values.
        nNew.appendChild(toXML(dDoc));

        // Append it to the parent.
        nParent.appendChild(nTuple);
    }

    /**
     * This method returns the data of the given tag. If that tag cannot be found 'null' is
     * returned.
     *
     * @param   sTag  The tag to get the data of.
     *
     * @return  The value of that tag.
     */
    protected String getTagValue(String sTag)
    {
        String sReturn = null;

        Node nTemp = null;

        try
        {
            nTemp = XPathHelper.selectSingleNode(m_nOriginalNode,
                                                 "./" + getPrefix4XPath() + sTag + "/text()");
        }
        catch (TransformerException e)
        {
            log("Error getting the value for tag " + sTag, e);
        }

        if (nTemp != null)
        {
            sReturn = nTemp.getNodeValue();
        }

        return sReturn;
    }

    /**
     * Should initialize the keys of the properties in the whole hierarchy. Must be overriden in all
     * sub-classes, which contains properties.
     */
    protected void initializeKeys()
    {
        // NOP
    }

    /**
     * Adapter message for getting notifications for execeptions that occurred.
     *
     * @param  sMessage    The descriptive message.
     * @param  tThrowable  The exception that occurred.
     */
    protected void log(String sMessage, Throwable tThrowable)
    {
    }

    /**
     * This method registers a key with this object.
     *
     * @param  sKey  The key to register.
     */
    protected void registerKey(String sKey)
    {
        registerKey(sKey, ALL_KEY);
    }

    /**
     * This method registers a key with this object. Within the hashmap there
     *
     * @param  sKey      The key to register.
     * @param  iKeyType  The type of key.
     */
    protected void registerKey(String sKey, int iKeyType)
    {
        final int[] aiKeyTypes = new int[] { TECHNICAL_KEY, FUNCTIONAL_KEY };

        HashMap<Integer, LinkedHashSet<String>> oMap = getKeys();

        // Add the key to the proper hashmap.
        LinkedHashSet<String> lhmAllKeys = oMap.get(WRAPPER_ALL_KEY);

        if (!lhmAllKeys.contains(sKey))
        {
            lhmAllKeys.add(sKey);
        }

        for (int iCount = 0; iCount < aiKeyTypes.length; iCount++)
        {
            int iCurrentKey = aiKeyTypes[iCount];

            // Also see if the key has to go in one of the other hashmaps.
            if (Bits.isBitSet(iKeyType, iCurrentKey))
            {
                LinkedHashSet<String> lhmKeyMap = oMap.get(new Integer(iCurrentKey));

                if (!lhmKeyMap.contains(sKey))
                {
                    lhmKeyMap.add(sKey);
                }
            }
        }
    }

    /**
     * This method returns all the current values for this object. It first checks if the
     * LinkedHashMap is already initialized. If this is not the case it will parse the original XML
     * to retrieve all the values.
     *
     * @return  The LinkedHashMap containing all the values.
     */
    private LinkedHashMap<String, String> getCurrentValues()
    {
        if (m_lhmNewValues == null)
        {
            synchronized (m_sRootTag)
            {
                if (m_lhmNewValues == null)
                {
                    m_lhmNewValues = new LinkedHashMap<String, String>();

                    // Only read from the XML if the XML exists.
                    if (m_nOriginalNode != null)
                    {
                        LinkedHashSet<String> lhmAllFields = getAllKeys();

                        for (Iterator<String> iAllFields = lhmAllFields.iterator();
                                 iAllFields.hasNext();)
                        {
                            String sFieldName = (String) iAllFields.next();
                            String sValue = getTagValue(sFieldName);
                            m_lhmNewValues.put(sFieldName, sValue);
                        }
                    }
                }
            }
        }

        return m_lhmNewValues;
    }

    /**
     * DOCUMENTME.
     *
     * @return  DOCUMENTME
     */
    private Iterator<String> getKeyIterator()
    {
        boolean bCheckOnTechnicalKeys = false;

        for (Iterator<String> iKeys = getTechnicalKeys().iterator(); iKeys.hasNext();)
        {
            String sKey = (String) iKeys.next();

            if (null != getStringValue(sKey))
            {
                bCheckOnTechnicalKeys = true;
                break;
            }
        }

        if (bCheckOnTechnicalKeys)
        {
            return getTechnicalKeys().iterator();
        }
        return getFunctionalKeys().iterator();
    }

    /**
     * This method returns all the keys for the given key type.
     *
     * @param   oKey  The key-wrapper type (ALL_KEY, TECHNICAL_KEY, FUNCTIONAL_KEY)
     *
     * @return  The keys for the given key type.
     */
    private LinkedHashSet<String> getKeys(Integer oKey)
    {
        LinkedHashSet<String> lhmReturn = null;

        HashMap<Integer, LinkedHashSet<String>> hmKeys = getKeys();

        if (hmKeys.containsKey(oKey))
        {
            lhmReturn = hmKeys.get(oKey);
        }

        return lhmReturn;
    }

    /**
     * Return the prefix, used in namespaces.
     *
     * @return  DOCUMENTME
     */
    private String getPrefix4XPath()
    {
        if ((null == m_sPrefix) || (0 == m_sPrefix.length()))
        {
            return "";
        }
        return m_sPrefix + ":";
    }
}
