package com.cordys.coe.util.ac;

import com.cordys.coe.util.i18n.CoEMessage;
import com.cordys.coe.util.i18n.CoEMessageSet;
import com.eibus.localization.IStringResource;

/**
 * Holds the Class ACMessages.
 */
public class ACMessages
{
    /**
     * Holds the definition of the CoE message set.
     */
    public static final CoEMessageSet MESSAGE_SET = new CoEMessageSet("com.cordys.coe.util.ac", "ACMessages");

    /** Holds the Constant ERROR_CREATING_SOAP_OPERATION. */
    public static final CoEMessage ERROR_CREATING_SOAP_OPERATION = new CoEMessage(MESSAGE_SET, "ac.error.creating.soap");

    /** Holds the Constant TIMEOUT_SENDING_SOAP_CALL. */
    public static final CoEMessage TIMEOUT_SENDING_SOAP_CALL = new CoEMessage(MESSAGE_SET, "ac.error.timeout.soap");

    /** Holds the Constant ERROR_SENDING_SOAP_CALL. */
    public static final CoEMessage ERROR_SENDING_SOAP_CALL = new CoEMessage(MESSAGE_SET, "ac.error.sending.soap");

    /** Holds the Constant ERROR_READING_XML_OBJECT_WITH_KEY. */
    public static final IStringResource ERROR_READING_XML_OBJECT_WITH_KEY = new CoEMessage(MESSAGE_SET, "ac.error.reading.file");

    /** Holds the Constant ERROR_WHILE_READING_XML_STORE_FILE. */
    public static final IStringResource ERROR_WHILE_READING_XML_STORE_FILE = new CoEMessage(MESSAGE_SET, "ac.error.while.reading.file");

    /** Holds the Constant ERROR_READING_XML_STORE_COLLECTION. */
    public static final IStringResource ERROR_READING_XML_STORE_COLLECTION = new CoEMessage(MESSAGE_SET, "ac.error.reading.collection");

    /** Holds the Constant ERROR_WHILE_READING_XML_STORE_COLLECTION. */
    public static final IStringResource ERROR_WHILE_READING_XML_STORE_COLLECTION = new CoEMessage(MESSAGE_SET, "ac.error.while.reading.collection");

    /** Holds the Constant ERROR_CHECKING_IF_LOCATION_IS_A_FOLDER. */
    public static final IStringResource ERROR_CHECKING_IF_LOCATION_IS_A_FOLDER = new CoEMessage(MESSAGE_SET, "ac.error.checking.folder");

}
