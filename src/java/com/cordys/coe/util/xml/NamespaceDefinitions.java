package com.cordys.coe.util.xml;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * This class contains all namespace definitions and it's default prefixes.
 *
 * @author  pgussow
 */
public class NamespaceDefinitions
{
    /**
     * Holds the namespace for SOAP 1.1.
     */
    public static final String XMLNS_SOAP_1_1 = "http://schemas.xmlsoap.org/soap/envelope/";
    /**
     * Holds the prefix for SOAP 1.1.
     */
    public static final String PREFIX_SOAP_1_1 = "SOAP";
    /**
     * Holds the namespace for XMLSchema 1.0.
     */
    public static final String XMLNS_XMLSCHEMA_1_0 = "http://www.w3.org/2001/XMLSchema";
    /**
     * Holds the prefix for XMLSchema 1.0.
     */
    public static final String PREFIX_XMLSCHEMA_1_0 = "xsd";
    /**
     * Holds the namespace for XMLSchema Instance.
     */
    public static final String XMLNS_XMLSCHEMA_INSTANCE = "http://www.w3.org/2001/XMLSchema-instance";
    /**
     * Holds the prefix for XMLSchema Instance.
     */
    public static final String PREFIX_XMLSCHEMA_INSTANCE = "xsi";
    /**
     * Holds the namespace declaration for the XForms standard.
     */
    public static final String XMLNS_XFORMS = "http://www.w3.org/2002/xforms";
    /**
     * Holds the standard prefix for the XForms standard.
     */
    public static final String PREFIX_XFORMS = "xforms";
    /**
     * Holds the namespace declaration for the WCP extentions within the definition.
     */
    public static final String XMLNS_XFORMS_WCP = "http://schemas.cordys.com/wcp/xforms";
    /**
     * Holds the default prefix for the WCP extentions within the definition.
     */
    public static final String PREFIX_XFORMS_WCP = "wcpforms";
    /**
     * Holds the namespace declaration for the eibus extentions within the definition.
     */
    public static final String XMLNS_XFORMS_EIBUS = "http://schemas.cordys.com/wcp/webframework";
    /**
     * Holds the default prefix for the eibus extentions within the definition.
     */
    public static final String PREFIX_XFORMS_EIBUS = "eibus";
    /**
     * Holds the namespace declaration for the XML events.
     */
    public static final String XMLNS_XML_EVENTS = "http://www.w3.org/2001/xml-events";
    /**
     * Holds the default prefix for the XML events.
     */
    public static final String PREFIX_XML_EVENTS = "ev";
    /**
     * Holds the namespace for the WSRP types.
     */
    public static final String XMLNS_WSRP_TYPES = "urn:oasis:names:tc:wsrp:v1:types";
    /**
     * Holds the default prefix for the WSRP types.
     */
    public static final String PREFIX_WSRP_TYPES = "wsrp";
    /**
     * The namespace for the Cordys general namespace within SOAP faults.
     */
    public static final String XMLNS_CORDYS_GENERAL_1_0 = "http://schemas.cordys.com/General/1.0/";
    /**
     * The namespace for the Cordys general namespace within SOAP faults.
     */
    public static final String PREFIX_CORDYS_GENERAL_1_0 = "cord";

    /**
     * This method returns a new HashMap containing all predefined mappings.
     *
     * @return  A new hashmap containing the prefix/namespace mappings.
     */
    public static HashMap<String, String> getNamespaceMappings()
    {
        HashMap<String, String> hmReturn = new LinkedHashMap<String, String>();

        hmReturn.put(PREFIX_SOAP_1_1, XMLNS_SOAP_1_1);
        hmReturn.put(PREFIX_XMLSCHEMA_1_0, XMLNS_XMLSCHEMA_1_0);
        hmReturn.put(PREFIX_XMLSCHEMA_INSTANCE, XMLNS_XMLSCHEMA_INSTANCE);
        hmReturn.put(PREFIX_XFORMS, XMLNS_XFORMS);
        hmReturn.put(PREFIX_XFORMS_WCP, XMLNS_XFORMS_WCP);
        hmReturn.put(PREFIX_XFORMS_EIBUS, XMLNS_XFORMS_EIBUS);
        hmReturn.put(PREFIX_XML_EVENTS, XMLNS_XML_EVENTS);
        hmReturn.put(PREFIX_WSRP_TYPES, XMLNS_WSRP_TYPES);
        hmReturn.put(PREFIX_CORDYS_GENERAL_1_0, XMLNS_CORDYS_GENERAL_1_0);

        return hmReturn;
    }
}
