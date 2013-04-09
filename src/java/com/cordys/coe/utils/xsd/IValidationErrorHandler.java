package com.cordys.coe.utils.xsd;

import org.xml.sax.ErrorHandler;

/**
 * Holds the validation message error handler.
 * 
 * @author pgussow
 */
public interface IValidationErrorHandler extends ErrorHandler
{
    /**
     * Returns true is XSD is valid.
     * 
     * @return true if valid
     */
    boolean isValid();

    /**
     * This method gets the XML that was validated.
     * 
     * @return The XML that was validated.
     */
    String getXML();

    /**
     * This method gets the location of the XSD that was used to validate against.
     * 
     * @return The location of the XSD that was used to validate against.
     */
    String getXSDFile();

    /**
     * This method returns the validation errors that occurred.
     * 
     * @return The validation errors that occurred.
     */
    String getValidationMessages();
}
