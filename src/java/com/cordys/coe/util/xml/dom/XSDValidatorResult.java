package com.cordys.coe.util.xml.dom;

import java.util.ArrayList;

/**
 * This class contains the XSD validation results.
 *
 * @author   $author$
 * @version  $Revision$
 */
public class XSDValidatorResult
{
    /**
     * DOCUMENTME.
     */
    private boolean isValid = false;
    /**
     * DOCUMENTME.
     */
    private ArrayList<String> messages = new ArrayList<String>();

    /**
     * Add a message to the validation result.
     *
     * @param  message  The message
     */
    public void addMessage(String message)
    {
        this.messages.add(message);
    }

    /**
     * Gets all validation messages.
     *
     * @return  a arraylist with messages
     */
    public ArrayList<String> getMessages()
    {
        return messages;
    }

    /**
     * Returns true is XSD is valid.
     *
     * @return  true if valid
     */
    public boolean isValid()
    {
        return isValid;
    }

    /**
     * Set valid.
     *
     * @param  isValid  the value
     */
    public void setValid(boolean isValid)
    {
        this.isValid = isValid;
    }
}
