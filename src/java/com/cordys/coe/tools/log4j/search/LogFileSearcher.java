package com.cordys.coe.tools.log4j.search;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * This class is usefull to find messages in large log files that are within a certain period. When the log file is a couple of
 * 100 Mbs then you can't really open it easily. This program will search all log4j entries to be within a certain time frame and
 * write that to a different file.
 * 
 * @author pgussow
 */
public class LogFileSearcher
{
    /** Holds the logger to use. */
    private static final Logger LOG = Logger.getLogger(LogFileSearcher.class);
    /** Holds the pattern to search for to get the timestamp */
    private static final Pattern P_TIMESTAMP = Pattern.compile("timestamp=\"(\\d+)\"");
    /** Holds the folder in which the logfiles are present. */
    private File m_folder;
    /** Holds the start time. */
    private Date m_start;
    /** Holds the end time. */
    private Date m_end;

    /**
     * Instantiates a new log file searcher.
     * 
     * @param folder The folder
     * @param start The start
     * @param end The end
     */
    public LogFileSearcher(File folder, Date start, Date end)
    {
        m_folder = folder;
        m_start = start;
        m_end = end;
    }

    /**
     * This method searches for the log entries in the given time frame. Those entries are written to a new file.
     */
    public void search()
    {
        String[] files = m_folder.list();
        for (String file : files)
        {
            LOG.info("Processing file " + file);

            BufferedReader br = null;
            BufferedWriter bw = null;
            try
            {
                File realFile = new File(m_folder, file);
                long current = 0;
                long total = realFile.length();
                long percentage = 0;

                br = new BufferedReader(new FileReader(realFile));

                StringBuilder sb = new StringBuilder(1024);
                boolean capturing = false;
                String line = null;

                while ((line = br.readLine()) != null)
                {
                    current += line.length() + System.getProperty("line.separator").length();

                    long newPercentage = Math.round(((float) current / (float) total) * 100.0f);
                    if (newPercentage > percentage)
                    {
                        percentage = newPercentage;
                        LOG.info("At " + percentage);
                    }

                    if (line.startsWith("<log4j:event"))
                    {
                        capturing = true;
                        sb = new StringBuilder();
                        sb.append(line);
                    }
                    else if (capturing)
                    {
                        if (line.startsWith(""))
                        {
                            // It was a new line, so we're done; we have the full line
                            capturing = false;
                            String entry = sb.toString();
                            sb = null;

                            // TODO: Now check to see if this entry matches the search criteria
                            Matcher m = P_TIMESTAMP.matcher(entry);
                            if (m.find())
                            {
                                long timestamp = Long.parseLong(m.group(1));

                                if (timestamp >= m_start.getTime() && timestamp <= m_end.getTime())
                                {
                                    LOG.debug("Found an entry matching the timeframe");
                                }

                                // Create the file if needed
                                if (bw == null)
                                {
                                    bw = new BufferedWriter(new FileWriter(new File(realFile.getParentFile(), "filtered_"
                                            + realFile.getName())));
                                }
                                bw.write(entry);
                                bw.newLine();
                            }
                            else
                            {
                                LOG.debug("Could not find timestamp in log entry:\n" + entry);
                            }
                        }
                        else
                        {
                            sb.append(line);
                        }
                    }
                    else
                    {
                        LOG.debug("Ignoring line " + line);
                    }
                }
            }
            catch (Exception e)
            {
                LOG.error("Error searching file " + file, e);
            }
            finally
            {
                if (br != null)
                {
                    try
                    {
                        br.close();
                    }
                    catch (IOException e)
                    {
                        LOG.debug("Error closing file", e);
                    }
                }
            }
        }
    }

    /**
     * Main method.
     * 
     * @param saArguments Commandline arguments.
     */
    public static void main(String[] saArguments)
    {
        try
        {
            String defaultStart = "2013-11-28T08:35:00.000";
            String defaultEnd = "2013-11-28T08:45:00.000";
            String defaultPath = "C:\\temp\\process";
            if (saArguments.length == 3)
            {
                defaultStart = saArguments[0];
                defaultEnd = saArguments[1];
                defaultPath = saArguments[2];
            }

            BasicConfigurator.configure();
            Logger.getRootLogger().setLevel(Level.INFO);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

            Date start = sdf.parse(defaultStart);
            Date end = sdf.parse(defaultEnd);

            LogFileSearcher lfs = new LogFileSearcher(new File(defaultPath), start, end);

            lfs.search();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
