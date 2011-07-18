package com.cordys.coe.util.cmdline;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

/**
 * This class can be used to read data from a stream untill it is closed.
 *
 * @author  pgussow
 */
public class CmdLineStreamPumper
    implements Runnable
{
	/**
     * Holds the logger for this class.
     */
    private static final Logger LOG = Logger.getLogger(CmdLine.class);
    /**
     * Identifies a new line.
     */
    private static final String NEWLINE = System.getProperty("line.separator");
    /**
     * The sleep time.
     */
    private static final int SLEEP = 100;
    /**
     * Indicates whether or not to append the newlines to the buffer.
     */
    private boolean bAppendNewlines = false;
    /**
     * Idicates whether or not the thread can finish.
     */
    private boolean bFinished;
    /**
     * The reader for the stream.
     */
    private BufferedReader brIn;
    /**
     * The buffer that will hold all the data.
     */
    private StringBuffer sb = new StringBuffer();

    /**
     * Create a new stream pumper.
     *
     * @param  is  input stream to read data from
     */
    public CmdLineStreamPumper(InputStream is)
    {
        brIn = new BufferedReader(new InputStreamReader(is));
    }

    /**
     * Create a new stream pumper.
     *
     * @param  isInputStream    input stream to read data from
     * @param  bAppendNewlines  if true, it will coaleasce lines
     */
    public CmdLineStreamPumper(InputStream isInputStream, boolean bAppendNewlines)
    {
        brIn = new BufferedReader(new InputStreamReader(isInputStream));
        this.bAppendNewlines = bAppendNewlines;
    }

    /**
     * Tells whether the end of the stream has been reached.
     *
     * @return  true is the stream has been exhausted.
     */
    public synchronized boolean isFinished()
    {
        return bFinished;
    }

    /**
     * Copies data from the input stream to the string buffer Terminates as soon as the input stream
     * is closed or an error occurs.
     */
    public void run()
    {
        synchronized (this)
        {
            // Just in case this object is reused in the future
            bFinished = false;
        }

        try
        {
            String sBuffer;

            while ((sBuffer = brIn.readLine()) != null)
            {
                if (!bAppendNewlines)
                {
                    sb.append(sBuffer);
                }
                else
                {
                    sb.append(sBuffer + NEWLINE);
                }
                
                LOG.debug("Read: " + sBuffer);

                try
                {
                    Thread.sleep(SLEEP);
                }
                catch (InterruptedException e)
                {
                }
            }
        }
        catch (Exception e)
        {
            // ignore
        }
        finally
        {
            synchronized (this)
            {
                bFinished = true;
                notify();
            }
        }
    }

    /**
     * This method returns the data of the stream as string.
     *
     * @return  The data of the stream as string.
     */
    @Override public synchronized String toString()
    {
        return sb.toString();
    }

    /**
     * This method blocks until the stream pumper finishes.
     *
     * @throws  InterruptedException  DOCUMENTME
     *
     * @see     #isFinished()
     */
    public synchronized void waitFor()
                              throws InterruptedException
    {
        while (!isFinished())
        {
            wait();
        }
    }
}
