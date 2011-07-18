/**
 *      © 2003 Cordys R&D B.V. All rights reserved.
 *      The computer program(s) is the proprietary information of Cordys R&D B.V.
 *      and provided under the relevant License Agreement containing restrictions
 *      on use and disclosure. Use is subject to the License Agreement.
 */
package com.cordys.coe.util.log;

import java.io.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This class is used to create a log file and control the size of the logfile. If the logfile
 * reaches the specified max length, the first 75% of the file is deleted.
 */
public class LogFile
{
    /**
     * Holds all the LogFile-objects.
     */
    private static HashMap<String, LogFile> hmFiles = null;
    /**
     * Indicates whether or not to write the data-stamp as well. Default is true.
     */
    private boolean bWriteDate = true;
    /**
     * Holds the maximum-length for the file. Default is 1 MB.
     */
    private long liMaxLength = 1048576L;
    /**
     * Holds the printwriter to log to.
     */
    private PrintWriter pwLogFile = null;
    /**
     * Holds the name of the file.
     */
    private String sFile = null;

    /**
     * Constructor. Creates a new outputstream to the file
     *
     * @param   sFileName  Name of the logfile
     *
     * @throws  IOException  DOCUMENTME
     */
    private LogFile(String sFileName)
             throws IOException
    {
        pwLogFile = new PrintWriter(new BufferedWriter(new FileWriter(sFileName, true)), true);
        sFile = sFileName;
    } // LogFile

    /**
     * This method closes all open logfiles.
     */
    public static void closeAll()
    {
        Iterator<String> iIterator = hmFiles.keySet().iterator();

        while (iIterator.hasNext())
        {
            LogFile lfTemp = hmFiles.get(iIterator.next());
            lfTemp.close();
        }
    } // closeAll

    /**
     * This method returns all open logfiles.
     *
     * @return  All open logfiles.
     */
    public static LogFile[] getAllFiles()
    {
        LogFile[] alfReturn = new LogFile[hmFiles.size()];

        Iterator<String> iKeys = hmFiles.keySet().iterator();

        for (int iCount = 0; iKeys.hasNext() == true; iCount++)
        {
            alfReturn[iCount] = hmFiles.get(iKeys.next());
        }

        return alfReturn;
    } // getAllFiles

    /**
     * Returns the instance of the logfile.
     *
     * @param   sFilename  The name of the file
     *
     * @return  The LogFile for that file.
     */
    public static synchronized LogFile getInstance(String sFilename)
    {
        LogFile lfReturn = null;

        if (hmFiles == null)
        {
            hmFiles = new HashMap<String, LogFile>();
        }

        Object oTemp = hmFiles.get(sFilename);

        if (oTemp == null)
        {
            try
            {
                lfReturn = new LogFile(sFilename);
                hmFiles.put(sFilename, lfReturn);
            }
            catch (IOException e)
            {
                // Ignore exception, null will be returned.
            }
        }
        else
        {
            lfReturn = (LogFile) oTemp;
        }

        return lfReturn;
    } // getInstance

    /**
     * A static write method that can be called from JavaCall connector.
     *
     * @param   sFileName  The log file to be written to.
     * @param   sMessage   The log message.
     *
     * @throws  Exception  Thrown if the writing failed.
     */
    public static void writeLogFile(String sFileName, String sMessage)
                             throws Exception
    {
        LogFile lfFile = getInstance(sFileName);

        if (lfFile == null)
        {
            throw new Exception("Unable to create the log file " + sFileName);
        }

        lfFile.writeLog(sMessage);

        lfFile.close();
    }

    /**
     * This method closes the logfile. It also removes itself from the hashMap.
     */
    public void close()
    {
        // Remove it.
        hmFiles.remove(sFile);
        pwLogFile.close();
    } // close

    /**
     * This method return the maximum size for the logfile.
     *
     * @return  The maximum size for the logfile.
     */
    public long getMaxLength()
    {
        return liMaxLength;
    } // getMaxLength

    /**
     * This method return whether or not the date-stamp is written to the file.
     *
     * @return  whether or not the date-stamp is written to the file.
     */
    public boolean isWriteDate()
    {
        return bWriteDate;
    } // isWriteDate

    /**
     * Sets the maximum length for the logfile.
     *
     * @param  liMaxLength  The maximum length for the logfile.
     */
    public synchronized void setMaxLength(long liMaxLength)
    {
        this.liMaxLength = liMaxLength;
    } // setMaxLength

    /**
     * Sets whether or not the date-stamp is written to the file.
     *
     * @param  bWriteDate  Whether or not the date-stamp is written to the file.
     */
    public synchronized void setWriteDate(boolean bWriteDate)
    {
        this.bWriteDate = bWriteDate;
    } // setWriteDate

    /**
     * This function writes a new line to the logfile with the current date and time.
     *
     * @param  sMessage  Message to write to the logfile
     */
    public synchronized void writeLog(String sMessage)
    {
        Date dCurDate = new Date();

        if (bWriteDate == true)
        {
            pwLogFile.print(dCurDate.toString() + ": ");
        }
        pwLogFile.println(sMessage);
        checkLength();
    } // writeLog

    /**
     * This function checks if the logfile exceeds the maximum length. If so, the first 75% of the
     * characters are deleted from the file.
     */
    private void checkLength()
    {
        File fFile = new File(sFile);

        long liLength = fFile.length();

        if (liLength > liMaxLength)
        {
            try
            {
                pwLogFile.close();

                File file = new File(sFile);
                FileInputStream fis = new FileInputStream(sFile);
                fis.skip((long) (file.length() * 0.75));

                byte[] bytes = new byte[(int) (file.length() - (long) (file.length() * 0.75))];
                DataInputStream dis = new DataInputStream(fis);
                dis.readFully(bytes, 0, bytes.length);
                fis.close();
                file.delete();

                FileOutputStream fos = new FileOutputStream(sFile, false);
                fos.write(bytes);
                fos.close();
                pwLogFile = new PrintWriter(new BufferedWriter(new FileWriter(sFile, true)), true);
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
        }
    } // checkLength
} // LogFile
