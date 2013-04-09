package com.cordys.coe.util.xml;

import java.util.ArrayList;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * This class handles and stores the XSD validation errors that might occur.
 */
class LocalErrorHandler implements IValidationErrorHandler
{
    /** Holds the errors. */
    private ArrayList<SAXParseException> m_alErrors = new ArrayList<SAXParseException>();
    /** Holds the fatal errors. */
    private ArrayList<SAXParseException> m_alFatals = new ArrayList<SAXParseException>();
    /** Holds the warnings. */
    private ArrayList<SAXParseException> m_alWarnings = new ArrayList<SAXParseException>();
    /** Holds the original XML file */
    private String m_xml;
    /** Holds the XSd that was used for validation. */
    private String m_xsd;

    /**
     * Instantiates a new local error handler.
     * 
     * @param xml The xml string
     */
    public LocalErrorHandler(String xml, String xsd)
    {
        m_xml = xml;
        m_xsd = xsd;
    }

    /**
     * @see com.cordys.coe.util.xml.ac.jmsconnector.util.xsd.IValidationErrorHandler#getXML()
     */
    @Override
    public String getXML()
    {
        return m_xml;
    }

    /**
     * @see com.cordys.coe.util.xml.ac.jmsconnector.util.xsd.IValidationErrorHandler#getXSDFile()
     */
    @Override
    public String getXSDFile()
    {
        return m_xsd;
    }

    /**
     * Error.
     * 
     * @param exception The exception
     * @throws SAXException The sAX exception
     * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
     */
    @Override
    public void error(SAXParseException exception) throws SAXException
    {
        m_alErrors.add(exception);
    }

    /**
     * Fatal error.
     * 
     * @param exception The exception
     * @throws SAXException The sAX exception
     * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
     */
    @Override
    public void fatalError(SAXParseException exception) throws SAXException
    {
        m_alFatals.add(exception);
    }

    /**
     * Returns true is XSD is valid.
     * 
     * @return true if valid
     */
    public boolean isValid()
    {
        return (m_alErrors.size() == 0) && (m_alFatals.size() == 0);
    }

    /**
     * This method returns the string representation of the object.
     * 
     * @return The string representation of the object.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return getValidationMessages();
    }

    /**
     * @see com.cordys.coe.util.xml.ac.jmsconnector.util.xsd.IValidationErrorHandler#getValidationMessages()
     */
    @Override
    public String getValidationMessages()
    {
        StringBuilder sbReturn = new StringBuilder(2048);

        if (m_alWarnings.size() > 0)
        {
            sbReturn.append("=============\n");
            sbReturn.append("Warnings:\n");
            sbReturn.append("=============\n");

            for (SAXParseException spe : m_alWarnings)
            {
                sbReturn.append("Line (").append(spe.getLineNumber()).append(":").append(spe.getColumnNumber());
                sbReturn.append("): ");
                sbReturn.append(spe.getMessage()).append("\n");
            }
        }

        if (m_alErrors.size() > 0)
        {
            sbReturn.append("=============\n");
            sbReturn.append("Errors:\n");
            sbReturn.append("=============\n");

            for (SAXParseException spe : m_alErrors)
            {
                sbReturn.append("Line (").append(spe.getLineNumber()).append(":").append(spe.getColumnNumber());
                sbReturn.append("): ");
                sbReturn.append(spe.getMessage()).append("\n");
            }
        }

        if (m_alFatals.size() > 0)
        {
            sbReturn.append("=============\n");
            sbReturn.append("Fatal errors:\n");
            sbReturn.append("=============\n");

            for (SAXParseException spe : m_alFatals)
            {
                sbReturn.append("Line (").append(spe.getLineNumber()).append(":").append(spe.getColumnNumber());
                sbReturn.append("): ");
                sbReturn.append(spe.getMessage()).append("\n");
            }
        }

        return sbReturn.toString();
    }

    /**
     * Warning.
     * 
     * @param exception The exception
     * @throws SAXException The sAX exception
     * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
     */
    @Override
    public void warning(SAXParseException exception) throws SAXException
    {
        m_alWarnings.add(exception);
    }
}