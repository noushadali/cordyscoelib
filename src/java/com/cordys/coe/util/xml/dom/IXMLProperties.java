package com.cordys.coe.util.xml.dom;

import com.cordys.coe.util.exceptions.XMLWrapperException;

import java.text.DateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * This interface describes the XML Proeprties objects.
 *
 * @author  pgussow
 */
public interface IXMLProperties
{
    /**
     * Identifies the bitmask for a field that is part of no key.
     */
    int ALL_KEY = 0;
    /**
     * Identifies the bitmask for a field that is part of the technical key.
     */
    int TECHNICAL_KEY = (int) Math.pow(2, 1);
    /**
     * Identifies the bitmask for a field that is part of the functional key.
     */
    int FUNCTIONAL_KEY = (int) Math.pow(2, 2);

    /**
     * This method checks if the passed on XMLProperties has the same keys and values as this
     * object.
     *
     * @param   xpProps  The XML properties to compare with.
     *
     * @return  true if both pieces of XML are the same. Otherwise false.
     */
    boolean equals(IXMLProperties xpProps);

    /**
     * This method checks if the passed on XMLProperties has the values as this object. It will
     * compare all data in all fields with eachother.
     *
     * @param   xpProps  The XML properties to compare with.
     *
     * @return  true if both pieces of XML are the same. Otherwise false.
     */
    boolean fullEquals(IXMLProperties xpProps);

    /**
     * This method returns all the fields for this property class.
     *
     * @return  All the fields for this property class.
     */
    LinkedHashSet<String> getAllKeys();

    /**
     * This method returns the Boolean-value of the given key.
     *
     * @param   sKey  The key to get the value of.
     *
     * @return  The Boolean-value of the given key.
     */
    boolean getBooleanValue(String sKey);

    /**
     * This method returns the Boolean-value of the given key.
     *
     * @param   sKey      The key to get the value of.
     * @param   bDefault  The default value if the key was not found.
     *
     * @return  The Boolean-value of the given key.
     */
    boolean getBooleanValue(String sKey, boolean bDefault);

    /**
     * This method gets the configuration node.
     *
     * @return  The configuration node.
     */
    Node getConfigNode();

    /**
     * This method gets the date formatter that is used for storing a date.
     *
     * @return  The date formatter that is used for storing a date.
     */
    DateFormat getDateFormatter();

    /**
     * This method returns the datetime-value of the given key.
     *
     * @param   sKey  The key to get the value of.
     *
     * @return  The DateTime-value of the given key.
     */
    Date getDateTimeValue(String sKey);

    /**
     * This method returns the Date-value of the given key.
     *
     * @param   sKey      The key to get the value of.
     * @param   oDefault  The default value to return is the key was not found.
     *
     * @return  The date-value of the given key.
     */
    Date getDateTimeValue(String sKey, Date oDefault);

    /**
     * This method returns all the fields for this property class that are in the functional key.
     *
     * @return  All the fields for this property class that are in the functional key.
     */
    LinkedHashSet<String> getFunctionalKeys();

    /**
     * This method returns the Integer-value of the given key.
     *
     * @param   sKey  The key to get the value of.
     *
     * @return  The Integer-value of the given key.
     */
    int getIntegerValue(String sKey);

    /**
     * This method returns the Integer-value of the given key.
     *
     * @param   sKey      The key to get the value of.
     * @param   iDefault  The default value to return is the key was not found.
     *
     * @return  The Integer-value of the given key.
     */
    int getIntegerValue(String sKey, int iDefault);

    /**
     * This method returns the registered keys for these properties. If the hashmap hasn't been
     * created yet, we will create the hashmap for this class. Within the hashmap there will be 3
     * other hashmaps. For each type of key there is a seperate hashmap.
     *
     * @return  The registered keys for these properties.
     */
    HashMap<Integer, LinkedHashSet<String>> getKeys();

    /**
     * This method returns the long-value of the data of the given tag. If that tag cannot be found
     * -1 is returned.
     *
     * @param   sKey  The key to get the value of.
     *
     * @return  The long-value of the key.
     */
    long getLongValue(String sKey);

    /**
     * This method returns the long-value of the data of the given tag. If that tag cannot be found
     * -1 is returned.
     *
     * @param   sKey      The key to get the value of.
     * @param   lDefault  The default value to return.
     *
     * @return  The long-value of the key.
     */
    long getLongValue(String sKey, long lDefault);

    /**
     * This method returns an array containing the XMLProperties for the given XQL-path.
     *
     * @param   sXQL  The XQL to execute
     *
     * @return  an array containing the configuration.
     */
    IXMLProperties[] getProperties(String sXQL);

    /**
     * This method gets the root tag for this object.
     *
     * @return  The root tag for this object.
     */
    String getRootTag();

    /**
     * This method returns the short-value of the given key.
     *
     * @param   sKey  The key to get the value of.
     *
     * @return  The short-value of the given key.
     */
    short getShortValue(String sKey);

    /**
     * This method returns the Short-value of the given key.
     *
     * @param   sKey      The key to get the value of.
     * @param   iDefault  The default value to return is the key was not found.
     *
     * @return  The short-value of the given key.
     */
    short getShortValue(String sKey, short iDefault);

    /**
     * This method returns the String-value of the given key.
     *
     * @param   sKey  The key to get the value of.
     *
     * @return  The String-value of the given key.
     */
    String getStringValue(String sKey);

    /**
     * This method returns the String-value of the given key.
     *
     * @param   sKey      The key to get the value of.
     * @param   sDefault  The default value to return.
     *
     * @return  The String-value of the given key.
     */
    String getStringValue(String sKey, String sDefault);

    /**
     * This method returns all the fields for this property class that are in the technical key.
     *
     * @return  All the fields for this property class that are in the technical key.
     */
    LinkedHashSet<String> getTechnicalKeys();

    /**
     * This method returns the XML-node pointing to the key.
     *
     * @param   sKey  The key to get the value of.
     *
     * @return  The XML-node pointing to the key.
     */
    Node getXMLNode(String sKey);

    /**
     * This method returns a new XMLProperties-class for the given key. If the key was not found,
     * null is returned.
     *
     * @param   sKey  The key to get the value of.
     *
     * @return  A new XMLProperties-class for the given key.
     */
    IXMLProperties getXMLProperties(String sKey);

    /**
     * This method sets the original data for this object and reads the prefix from the original
     * node. When the node contains a tuple/old, that content will be deleted.
     *
     * @param  nOriginalData  The data to initialize the object with.
     */
    void initializeData(Node nOriginalData);

    /**
     * This method gets whether or not the object's values have changed.
     *
     * @return  Whether or not the object's values have changed.
     */
    boolean isChanged();

    /**
     * This method sets the date formatter that is used for storing a date.
     *
     * @param  dfFormatter  The date formatter that is used for storing a date.
     */
    void setDateFormatter(DateFormat dfFormatter);

    /**
     * This method sets the value for a specific field. The actual value stored will be
     * oValue.toString();
     *
     * @param  sField  The name of the field.
     * @param  oValue  The actual value.
     */
    void setValue(String sField, Object oValue);

    /**
     * Returns a string representation of the object.
     *
     * @return  A string representation of the object.
     */
    String toString();

    /**
     * This method returns the current values in an XML structure.
     *
     * @param   dDoc  The document to use to create the XML.
     *
     * @return  The current values in an XML structure.
     */
    Node toXML(Document dDoc);

    /**
     * This method creates the XML representing the tuple for this object. If there was an original
     * XML that XML is incorperated in the tuple as the old tag. If there is no original xml just
     * the tuple-new is returned.
     *
     * @param   nParent  The node under which to create the XML.
     *
     * @throws  XMLWrapperException  DOCUMENTME
     */
    void toXMLTuple(Node nParent)
             throws XMLWrapperException;
}
