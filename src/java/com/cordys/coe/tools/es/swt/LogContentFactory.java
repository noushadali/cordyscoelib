package com.cordys.coe.tools.es.swt;

/**
 * This factory creates the different content providers.
 *
 * @author  pgussow
 */
public class LogContentFactory
{
    /**
     * This method creates a content provider based on a Log4J logfile. It will iterate through the
     * logfile based on number of bytes.
     *
     * @param   sFilename   The name of the logfile.
     * @param   lBlockSize  The number of bytes to read at once.
     *
     * @return  The content provider for this logfile.
     */
    public static ILogContentProvider createLog4JFileProviderByBytesBlock(String sFilename,
                                                                          long lBlockSize)
    {
        ILogContentProvider lcpReturn = null;

        lcpReturn = new Log4JLogfileContentProvider(Log4JLogfileContentProvider.TYPE_BLOCK,
                                                    lBlockSize, sFilename);

        return lcpReturn;
    }

    /**
     * This method creates a content provider based on a Log4J logfile. It will iterate through the
     * logfile based on the number of records.
     *
     * @param   sFilename  The name of the logfile.
     * @param   iPageSize  The number of records to read at once.
     *
     * @return  The content provider for this logfile.
     */
    public static ILogContentProvider createLog4JFileProviderByRecordCount(String sFilename,
                                                                           int iPageSize)
    {
        ILogContentProvider lcpReturn = null;

        lcpReturn = new Log4JLogfileContentProvider(Log4JLogfileContentProvider.TYPE_RECORDCOUNT,
                                                    iPageSize, sFilename);

        return lcpReturn;
    }
}
