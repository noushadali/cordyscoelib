package com.cordys.coe.test;

import com.cordys.coe.util.cmdline.CmdLine;
import com.cordys.coe.util.general.Util;
import com.cordys.coe.util.xml.dom.XMLHelper;
import com.cordys.coe.util.xml.dom.XPathHelper;

import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This tool outputs which files are locked and by whom.
 *
 * @author  pgussow
 */
public class ShowLocks
{
    /**
     * Holds the location of the SVN client.
     */
    private static final String SVN_LOCATION = "c:/apps/svn/bin/svn.exe";
    /**
     * Holds the URL to check.
     */
    private String m_url;

    /**
     * Creates a new ShowLocks object.
     *
     * @param  url  The URL to check.
     */
    public ShowLocks(String url)
    {
        m_url = url;
    }

    /**
     * Main method.
     *
     * @param  saArguments  Commandline arguments.
     */
    public static void main(String[] saArguments)
    {
        try
        {
            String url = "https://svn.cordys.csc.nl/svn/csc_nl/Customers/Embraer/CIM/trunk";
            ShowLocks sl = new ShowLocks(url);

            sl.execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * This method will .
     *
     * @throws  Exception  In case of any exceptions
     */
    public void execute()
                 throws Exception
    {
        CmdLine cl = new CmdLine("cmd.exe");
        cl.addArgument("/c");
        cl.addArgument(SVN_LOCATION);
        // CmdLine cl = new CmdLine(SVN_LOCATION);
        cl.addArgument("ls");
        cl.addArgument("-R");
        cl.addArgument("--xml");
        cl.addArgument(m_url);

        File file = File.createTempFile("svnlocks1", ".xml");
        cl.addArgument(">");
        cl.addArgument(file.getCanonicalPath());

        cl.execute();

        Document doc = XMLHelper.loadXMLFile(file.getCanonicalPath());

        NodeList locks = XPathHelper.selectNodeList(doc.getDocumentElement(), "//self::node()[lock]");

        for (int count = 0; count < locks.getLength(); count++)
        {
            Element entry = (Element) locks.item(count);

            String name = XPathHelper.getStringValue(entry, "name/text()");
            name = Util.padRight(name, " ", 100);

            String locker = XPathHelper.getStringValue(entry, "lock/owner/text()", "unknown");
            locker = Util.padRight(locker, " ", 10);

            String date = XPathHelper.getStringValue(entry, "lock/created/text()", "unknown");

            System.out.println(name + "(by " + locker + " on " + date + ")");
        }
    }
}
