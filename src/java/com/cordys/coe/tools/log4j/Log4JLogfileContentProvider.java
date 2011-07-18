package com.cordys.coe.tools.log4j;

import com.cordys.coe.tools.es.Log4JLogEvent;
import com.cordys.coe.tools.es.swt.EventDetails;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.util.ArrayList;

/**
 * This class reads a Log4J logfile.
 *
 * @author  pgussow
 */
@SuppressWarnings("unused")
public class Log4JLogfileContentProvider
    implements ILogContentProvider
{
    /**
     * Identfies that this provider has to read based on filesize.
     */
    public static final int TYPE_BLOCK = 0;
    /**
     * Identfies that this provider has to read based on record count.
     */
    public static final int TYPE_RECORDCOUNT = 1;
    /**
     * Holds all the pages read and their offset in the file.
     */
    private ArrayList<PageInfo> m_alOffsets = new ArrayList<PageInfo>();
    /**
     * Holds the current page.
     */
    private int m_iCurrentPage = 0;
    /**
     * Holds the current type of reader.
     */
    private int m_iType = TYPE_BLOCK;
    /**
     * Holds the current offset.
     */
    private long m_lCurrentOffset;
    /**
     * Holds the size of the page.
     */
    private long m_lPageSize;
    /**
     * Holds the actual file object used for reading.
     */
    private RandomAccessFile m_rafFile;
    /**
     * Holds the name of the file.
     */
    private String m_sFilename;

    /**
     * Constructor.
     *
     * @param  iType      The type of browser.
     * @param  lPageSize  The pagesize.
     * @param  sFilename  The name of the file.
     */
    public Log4JLogfileContentProvider(int iType, long lPageSize, String sFilename)
    {
        m_iType = iType;
        m_lPageSize = lPageSize;
        m_sFilename = sFilename;
        m_lCurrentOffset = 0;

        if ((m_iType != TYPE_BLOCK) && (m_iType != TYPE_RECORDCOUNT))
        {
            throw new IllegalArgumentException("Type must be TYPE_BLOCK or TYPE_RECORDCOUNT.");
        }

        File fFile = new File(sFilename);

        if (!fFile.exists())
        {
            throw new IllegalArgumentException("File " + sFilename + " does not exist.");
        }

        if (m_lPageSize <= 0)
        {
            throw new IllegalArgumentException("page size must be greater then 0.");
        }

        try
        {
            m_rafFile = new RandomAccessFile(fFile, "r");
        }
        catch (FileNotFoundException e)
        {
            // Can not happen, already checked.
        }
    }

    /**
     * This method closes the content provider.
     */
    public void close()
    {
        try
        {
            m_rafFile.close();
        }
        catch (IOException e)
        {
            // Ignore it.
        }
    }

    /**
     * This method gets the name of the content provider.
     *
     * @return  The name of the content provider.
     */
    public String getName()
    {
        return m_sFilename;
    }

    /**
     * This method reads the next set of data from the log source.
     *
     * @param   lpPanel  The panel that should handle the read log messages.
     *
     * @throws  IOException  DOCUMENTME
     *
     * @see     com.cordys.coe.tools.log4j.ILogContentProvider#getNextDataset(com.cordys.coe.tools.log4j.Log4JPanel)
     */
    public synchronized void getNextDataset(Log4JPanel lpPanel)
                                     throws IOException
    {
        m_iCurrentPage++;

        PageInfo piInfo = null;

        if (m_alOffsets.size() > m_iCurrentPage)
        {
            // Page has already been visited, so we know the offset.
            piInfo = m_alOffsets.get(m_iCurrentPage);
        }
        else
        {
            // The page does not exist set to we need to create it.
            piInfo = new PageInfo(m_iCurrentPage);
            piInfo.setStartOffset(m_lCurrentOffset);
            m_alOffsets.add(piInfo);
        }

        // Seek the proper position to start reading.
        m_rafFile.seek(piInfo.getStartOffset());

        // Set the tags to look for.
        String sRootTag = "<log4j:event";
        String sEndRootTag = "</log4j:event>";
        boolean bStopReading = false;
        int iCurrentRecordCount = 0;
        long lCurrentBlockSize = 0;
        long lCurrentMessageStartOffset = 0;

        // Read the first line and start reading this block.
        String sBuffer = m_rafFile.readLine();
        int iCurrentStartPos = -1;
        int iEndPos = -1;
        StringBuffer sbCurrentMessage = null;

        while ((sBuffer != null) && !bStopReading)
        {
            if (iCurrentStartPos == -1)
            {
                iCurrentStartPos = sBuffer.indexOf(sRootTag);
            }
            iEndPos = sBuffer.indexOf(sEndRootTag);

            if ((iCurrentStartPos > -1) && (iEndPos == -1) && (sbCurrentMessage == null))
            {
                // Found a start-entry without an end-entry
                String sTemp = sBuffer.substring(iCurrentStartPos);
                sbCurrentMessage = new StringBuffer();
                sbCurrentMessage.append(sTemp);
                sbCurrentMessage.append(System.getProperty("line.separator"));
            }
            else if ((sbCurrentMessage != null) && (iEndPos == -1))
            {
                sbCurrentMessage.append(sBuffer);
                sbCurrentMessage.append(System.getProperty("line.separator"));
            }
            else if ((sbCurrentMessage != null) && (iEndPos > -1))
            {
                String sTemp = sBuffer.substring(iCurrentStartPos, iEndPos + sEndRootTag.length());
                sbCurrentMessage.append(sTemp);
                sbCurrentMessage.append(System.getProperty("line.separator"));

                // Found the full message
                String sCurrentMessage = sbCurrentMessage.toString();
                EventDetails ed = EventDetails.createInstanceFromLog4JXML(sCurrentMessage);
                Log4JLogEvent lleEvent = new Log4JLogEvent(ed);
                lpPanel.receive(lleEvent);

                // Update the counters.
                iCurrentRecordCount++;
                lCurrentBlockSize = lCurrentBlockSize +
                                    (m_rafFile.getFilePointer() - lCurrentMessageStartOffset);

                // Now see if we should stop.
                if (m_iType == TYPE_RECORDCOUNT)
                {
                    if (iCurrentRecordCount >= m_lPageSize)
                    {
                        bStopReading = true;
                    }
                }
                else if (m_iType == TYPE_BLOCK)
                {
                    if (lCurrentBlockSize >= m_lPageSize)
                    {
                        bStopReading = true;
                    }
                }

                if (bStopReading)
                {
                    m_lCurrentOffset += lCurrentBlockSize;
                    piInfo.setEndOffset(m_rafFile.getFilePointer());
                    piInfo.setRecordCount(iCurrentRecordCount);
                }

                // Be ready to read the next message
                sbCurrentMessage = null;
            }

            if (sbCurrentMessage == null)
            {
                lCurrentMessageStartOffset = m_rafFile.getFilePointer();
            }
            sBuffer = m_rafFile.readLine();
        }
    }

    /**
     * This method reads the next set of data from the log source.
     *
     * @param   aepPanel  The panel that should handle the read log messages.
     *
     * @throws  IOException  DOCUMENTME
     *
     * @see     com.cordys.coe.tools.log4j.ILogContentProvider#getPreviousDataset(com.cordys.coe.tools.log4j.Log4JPanel)
     */
    public void getPreviousDataset(Log4JPanel aepPanel)
                            throws IOException
    {
        if (m_lCurrentOffset > 0)
        {
        }
    }

    /**
     * Class that wraps around the pages in the file.
     *
     * @author  pgussow
     */
    private class PageInfo
    {
        /**
         * Holds the page number.
         */
        private int m_iPageNumber;
        /**
         * Holds the number of records in this page.
         */
        private int m_iRecordCount = 0;
        /**
         * Holds the end offset.
         */
        private long m_lEndOffset;
        /**
         * Holds the start offset.
         */
        private long m_lStartOffset;

        /**
         * Constructor.
         *
         * @param  iPageNumber  The page number.
         */
        public PageInfo(int iPageNumber)
        {
            m_iPageNumber = iPageNumber;
        }

        /**
         * Constructor.
         *
         * @param  lStartOffset  the starting offset.
         * @param  lEndOffset    The end offset.
         * @param  iPageNumber   The page number.
         */
		public PageInfo(long lStartOffset, long lEndOffset, int iPageNumber)
        {
            m_lStartOffset = lStartOffset;
            m_lEndOffset = lEndOffset;
            m_iPageNumber = iPageNumber;
        }

        /**
         * This method gets the end offset.
         *
         * @return  The end offset.
         */
        public long getEndOffset()
        {
            return m_lEndOffset;
        }

        /**
         * This method gets the page number.
         *
         * @return  The page number.
         */
        public int getPageNumber()
        {
            return m_iPageNumber;
        }

        /**
         * This method gets the number of records in this page.
         *
         * @return  The number of records in this page.
         */
        public int getRecordCount()
        {
            return m_iRecordCount;
        }

        /**
         * This method gets the start offset.
         *
         * @return  The start offset.
         */
        public long getStartOffset()
        {
            return m_lStartOffset;
        }

        /**
         * This method sets the end offset.
         *
         * @param  m_lEndOffset  The end offset.
         */
        public void setEndOffset(long m_lEndOffset)
        {
            this.m_lEndOffset = m_lEndOffset;
        }

        /**
         * This method sets the number of records in this page.
         *
         * @param  m_iRecordCount  The number of records in this page.
         */
        public void setRecordCount(int m_iRecordCount)
        {
            this.m_iRecordCount = m_iRecordCount;
        }

        /**
         * This method sets the start offset.
         *
         * @param  m_lStartOffset  The start offset.
         */
        public void setStartOffset(long m_lStartOffset)
        {
            this.m_lStartOffset = m_lStartOffset;
        }
    }
}
