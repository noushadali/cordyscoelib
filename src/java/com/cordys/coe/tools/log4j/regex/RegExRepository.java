package com.cordys.coe.tools.log4j.regex;

import com.cordys.coe.util.exceptions.XMLWrapperException;
import com.cordys.coe.util.xml.dom.NiceDOMWriter;
import com.cordys.coe.util.xml.dom.XMLHelper;
import com.cordys.coe.util.xml.dom.XPathHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class wraps around the XML file containing the stored regular expressions.
 *
 * @author  pgussow
 */
public class RegExRepository
{
    /**
     * Holds all the current entries.
     */
    private ArrayList<RegExEntry> m_alEntries = new ArrayList<RegExEntry>();
    /**
     * Holds the file of the repository.
     */
    private File m_fFile;

    /**
     * Creates a new RegExRepository object.
     *
     * @param   fFile  The file that contains the XML.
     *
     * @throws  XMLWrapperException   DOCUMENTME
     * @throws  IOException           DOCUMENTME
     * @throws  TransformerException  DOCUMENTME
     */
    public RegExRepository(File fFile)
                    throws XMLWrapperException, IOException, TransformerException
    {
        m_fFile = fFile;

        Document dDoc = null;

        if (!fFile.exists())
        {
            String sXML = "<regexrepository><regexentry><regex>(.*Received -.*)|(.*Sending \\(MessageOptions.*)</regex><description>Shows all messages that are sent and received by the processor.</description></regexentry></regexrepository>";
            dDoc = XMLHelper.createDocumentFromXML(sXML);
        }
        else
        {
            dDoc = XMLHelper.loadXMLFile(fFile.getCanonicalPath());
        }

        if (dDoc != null)
        {
            parseXML(dDoc.getDocumentElement());
        }
    }

    /**
     * This method adds an entry.
     *
     * @param  reeEntry  the entry to add.
     */
    public void addEntry(RegExEntry reeEntry)
    {
        if (m_alEntries.contains(reeEntry))
        {
            m_alEntries.remove(reeEntry);
        }
        m_alEntries.add(reeEntry);
    }

    /**
     * This method returns all entries.
     *
     * @return  All entries.
     */
    public RegExEntry[] getAllEntries()
    {
        return m_alEntries.toArray(new RegExEntry[m_alEntries.size()]);
    }

    /**
     * This method gets the entry for the given regex. If the given RegEx is not found an entry is
     * created.
     *
     * @param   sRegEx  The regex to find.
     *
     * @return  The entry for the given regex.
     */
    public RegExEntry getRegExEntry(String sRegEx)
    {
        RegExEntry reeReturn = null;

        for (RegExEntry reeCurrent : m_alEntries)
        {
            if (reeCurrent.getRegEx().equals(sRegEx))
            {
                reeReturn = reeCurrent;
                break;
            }
        }

        if (reeReturn == null)
        {
            reeReturn = new RegExEntry(sRegEx, "");
            m_alEntries.add(reeReturn);
        }

        return reeReturn;
    }

    /**
     * This method returns an iterator over the entire entry list.
     *
     * @return  The iterator.
     */
    public Iterator<RegExEntry> iterator()
    {
        return m_alEntries.iterator();
    }

    /**
     * This method removes the entry from the repository.
     *
     * @param  reeEntry  The entry to remove.
     */
    public void removeEntry(RegExEntry reeEntry)
    {
        removeEntry(reeEntry.getRegEx());
    }

    /**
     * This method removes the regex from the repository.
     *
     * @param  sRegEx  The regex to remove.
     */
    public void removeEntry(String sRegEx)
    {
        for (RegExEntry reeCurrent : m_alEntries)
        {
            if (reeCurrent.getRegEx().equals(sRegEx))
            {
                m_alEntries.remove(reeCurrent);
                break;
            }
        }
    }

    /**
     * This method saves the current status of the repository to the file.
     *
     * @throws  IOException  DOCUMENTME
     */
    public void saveFile()
                  throws IOException
    {
        Document dDoc = XMLHelper.createDocumentFromXML("<regexrepository/>");
        Node nParent = dDoc.getDocumentElement();

        for (Iterator<RegExEntry> iRegExes = m_alEntries.iterator(); iRegExes.hasNext();)
        {
            RegExEntry reeEntry = iRegExes.next();
            reeEntry.toXML(nParent);
        }

        FileWriter fos = new FileWriter(m_fFile, false);

        try
        {
            fos.write(NiceDOMWriter.write(nParent));
            fos.flush();
        }
        finally
        {
            fos.close();
        }
    }

    /**
     * This method updates the given entry.
     *
     * @param  sCurrentKey  The current key.
     * @param  reeEntry     The new entry.
     */
    public void updateEntry(String sCurrentKey, RegExEntry reeEntry)
    {
        if (sCurrentKey != null)
        {
            if (!sCurrentKey.equals(reeEntry.getRegEx()))
            {
                m_alEntries.remove(sCurrentKey);
                addEntry(reeEntry);
            }
        }
        else
        {
            addEntry(reeEntry);
        }
    }

    /**
     * This method parses the XML for the repsitory.
     *
     * @param   eRoot  The root element.
     *
     * @throws  TransformerException  DOCUMENTME
     */
    private void parseXML(Element eRoot)
                   throws TransformerException
    {
        NodeList nlRegExes = XPathHelper.selectNodeList(eRoot, "//regexentry");

        for (int iCount = 0; iCount < nlRegExes.getLength(); iCount++)
        {
            Node nEntry = nlRegExes.item(iCount);
            RegExEntry reeEntry = new RegExEntry(nEntry);

            m_alEntries.add(reeEntry);
        }
    }
}
