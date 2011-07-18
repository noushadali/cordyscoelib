package com.cordys.coe.tools.multicast;

import com.cordys.coe.util.XMLProperties;
import com.cordys.coe.util.cmdline.CmdLine;
import com.cordys.coe.util.cmdline.CmdLineException;
import com.cordys.coe.util.xml.nom.XPathHelper;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Node;

import java.io.File;
import java.io.FileWriter;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;

/**
 * This class executes an UDP ping and will analyze the response time. If it is bigger then the
 * threshold the listed commands will be executed.
 *
 * @author  pgussow
 */
public class MultiCastPing
{
    /**
     * Holds the logger that is used.
     */
    private static final Logger LOG = Logger.getLogger(MultiCastPing.class);
    /**
     * Holds the document to use for configuration XML.
     */
    private static Document s_dDoc = new Document();
    /**
     * DOCUMENTME.
     */
    private ArrayList<XMLProperties> m_alCommands;
    /**
     * DOCUMENTME.
     */
    private ArrayList<String> m_alIPs;
    /**
     * DOCUMENTME.
     */
    private File m_fLog;
    /**
     * DOCUMENTME.
     */
    private int m_iInterval;
    /**
     * DOCUMENTME.
     */
    private int m_iThreshold;
    /**
     * DOCUMENTME.
     */
    private XMLProperties m_xp;

    /**
     * Creates a new MultiCastPing object.
     *
     * @param   sConfigFile  The configuration file.
     *
     * @throws  Exception  In case of any exception.
     */
    public MultiCastPing(String sConfigFile)
                  throws Exception
    {
        int iConfig = s_dDoc.load(sConfigFile);
        m_xp = new XMLProperties(iConfig);

        m_fLog = new File(m_xp.getStringValue("logfolder"));

        if (!m_fLog.exists())
        {
            m_fLog.mkdirs();
        }

        m_iThreshold = m_xp.getIntegerValue("threshold");
        m_iInterval = m_xp.getIntegerValue("interval", 0);

        m_alIPs = new ArrayList<String>();

        int[] ai = XPathHelper.selectNodes(iConfig, "expectedips/ip");

        for (int iNode : ai)
        {
            m_alIPs.add(Node.getDataWithDefault(iNode, ""));
        }

        m_alCommands = new ArrayList<XMLProperties>();

        XMLProperties[] axp = m_xp.getProperties("commands/command", XMLProperties.class);

        for (XMLProperties iCommand : axp)
        {
            m_alCommands.add(iCommand);
        }
    }

    /**
     * Main method.
     *
     * @param  saArguments  The command line arguments.
     */
    public static void main(String[] saArguments)
    {
        File fLock = null;

        try
        {
            if (saArguments.length != 2)
            {
                displayUsage();
            }

            fLock = new File(saArguments[1]);

            if (!fLock.createNewFile())
            {
                System.err.println("Lock file already exists. Exiting program");
                System.exit(2);
            }

            MultiCastPing mcp = new MultiCastPing(saArguments[0]);
            mcp.run();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            // Clean up the lock file
            fLock.delete();
        }
    }

    /**
     * This method will actually run the commands.
     */
    public void run()
    {
        do
        {
            try
            {
                LinkedHashMap<String, PingResponse> lhmResp = new LinkedHashMap<String, PingResponse>();

                CmdLine cl = parseCommand(m_xp.getConfigNode(), "cmd/arg");

                if (LOG.isInfoEnabled())
                {
                    LOG.info("Executing Ping request");
                }

                cl.execute();

                if (LOG.isInfoEnabled())
                {
                    LOG.info("Parsing ping response");
                }

                if (LOG.isDebugEnabled())
                {
                    LOG.debug("Output:\n" + cl.getStdOut());
                }

                String[] asLines = cl.getStdOut().split("[\r]*\n");

                // Find every line that actually came back with a response.
                for (String sLine : asLines)
                {
                    if (sLine.matches("64 bytes from ([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}):[\\W]+icmp_seq=([0-9]+)[\\W]+ttl=([0-9]+)[\\W]+time=([0-9.]+)[\\W]+ms([\\W]*\\(DUP!\\)){0,1}"))
                    {
                        // It's a ping response.
                        PingResponse pr = new PingResponse(sLine);

                        if (LOG.isDebugEnabled())
                        {
                            LOG.debug("Ping Response: " + pr);
                        }

                        // For now we'll only store the first reply.
                        if (!lhmResp.containsKey(pr.getIP()))
                        {
                            lhmResp.put(pr.getIP(), pr);
                        }
                    }
                }

                // Now see if we have all the needed replies
                if (LOG.isInfoEnabled())
                {
                    LOG.info("Making sure all IPs replied");
                }

                boolean bAllThere = true;

                for (String sIP : m_alIPs)
                {
                    if (!lhmResp.containsKey(sIP))
                    {
                        LOG.error("Missing response from machine: " + sIP);
                        bAllThere = false;
                    }
                }

                // If all responses are there, also check the threshold
                if (bAllThere)
                {
                    if (LOG.isInfoEnabled())
                    {
                        LOG.info("Checking for the ping threshold");
                    }

                    for (PingResponse prTemp : lhmResp.values())
                    {
                        if (prTemp.getResponseTime() > m_iThreshold)
                        {
                            LOG.error(prTemp.getIP() + " exceeded the threshold: " +
                                      prTemp.getResponseTime());
                            bAllThere = false;
                        }
                    }
                }

                if (bAllThere == false)
                {
                    // There were errors, so we need to execute the other commands and dump the
                    // output.
                    for (XMLProperties xpCmd : m_alCommands)
                    {
                        File fLogFile = new File(m_fLog, xpCmd.getStringValue("file"));

                        cl = parseCommand(xpCmd.getConfigNode(), "cmd/arg");

                        if (LOG.isDebugEnabled())
                        {
                            LOG.debug("Executing command: " + cl.getFullCommand());
                        }

                        int iReturnCode = 0;

                        try
                        {
                            iReturnCode = cl.execute();
                        }
                        finally
                        {
                            FileWriter fw = new FileWriter(fLogFile, true);

                            try
                            {
                                fw.write(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(new Date()) +
                                         ":\n");
                                fw.write("Code: " + iReturnCode + "\n");
                                fw.write("StdOut\n" + cl.getStdOut() + "\n");
                                fw.write("StdErr\n" + cl.getStdErr() + "\n\n");
                                fw.flush();
                            }
                            finally
                            {
                                if (fw != null)
                                {
                                    fw.close();
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception e)
            {
                LOG.error("Error executing ping run.", e);
            }

            try
            {
                if (LOG.isDebugEnabled())
                {
                    LOG.debug("Sleeping for " + m_iInterval);
                }
                Thread.sleep(m_iInterval);
            }
            catch (InterruptedException e)
            {
                LOG.info("Thread interrupted.", e);
            }
        }
        while (m_iInterval > 0);
    }

    /**
     * This method displays the usage of the tool.
     */
    private static void displayUsage()
    {
        System.out.println("Usage:");
        System.out.println("    java com.cordys.coe.tools.multicast.MultiCastPing <config_file> <lock_file>");
        System.exit(1);
    }

    /**
     * DOCUMENTME.
     *
     * @param   iNode   DOCUMENTME
     * @param   sXPath  DOCUMENTME
     *
     * @return  DOCUMENTME
     *
     * @throws  CmdLineException
     */
    private CmdLine parseCommand(int iNode, String sXPath)
                          throws CmdLineException
    {
        CmdLine clReturn = null;

        int[] ai = XPathHelper.selectNodes(iNode, sXPath);

        if (ai.length > 0)
        {
            clReturn = new CmdLine(Node.getDataWithDefault(ai[0], ""));

            for (int iCount = 1; iCount < ai.length; iCount++)
            {
                clReturn.addArgument(Node.getDataWithDefault(ai[iCount], ""));
            }
        }

        return clReturn;
    }
}
