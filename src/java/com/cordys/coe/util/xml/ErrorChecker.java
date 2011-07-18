package com.cordys.coe.util.xml;

import java.util.ArrayList;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class handles validation errors and stores them internally.
 *
 * @author  hansz
 */
public class ErrorChecker extends DefaultHandler
{
    /**
     * This list holds all the errors that occurred.
     */
    private ArrayList<ParseError> alErrors = new ArrayList<ParseError>();

    /**
     * This method gets fired when an error occurred.
     *
     * @param   speError  The exception that occurred.
     *
     * @throws  SAXException  DOCUMENTME
     */
    @Override public void error(SAXParseException speError)
                         throws SAXException
    {
        ParseError peError = new ParseError(speError);
        alErrors.add(peError);
    }

    /**
     * This method returns the list containing all the errors that occurred.
     *
     * @return  The list containing all the errors that occurred.
     */
    public ArrayList<ParseError> getAllErrors()
    {
        return alErrors;
    }
}
