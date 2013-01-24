/**
 * (c) 2009 Cordys R&D B.V. All rights reserved. The computer program(s) is the proprietary information of Cordys B.V. and
 * provided under the relevant License Agreement containing restrictions on use and disclosure. Use is subject to the License
 * Agreement.
 */
package com.cordys.coe.util.cmdline;

import org.apache.log4j.Logger;

/**
 * Contains the process and stream pumpers that are started by the CmdLine class.
 * 
 * @author mpoyhone
 */
public class RunningProcess
{
    /**
     * Holds the logger for this class.
     */
    private static final Logger lLogger = Logger.getLogger(CmdLine.class);
    /**
     * Holds the created process.
     */
    private Process pProcess;
    /**
     * stdout pumper.
     */
    private CmdLineStreamPumper clspStdOut;
    /**
     * stderr pumper.
     */
    private CmdLineStreamPumper clspStdErr;
    /**
     * stdout pumper thread.
     */
    private Thread tStdOut;
    /**
     * stderr pumper thread.
     */
    private Thread tStdErr;
    /**
     * Owning command line object.
     */
    private CmdLine m_clCmdLine;

    /**
     * Constructor for RunningProcess
     * 
     * @param clCmdLine Owning command line object.
     * @param pProcess Created process.
     */
    public RunningProcess(CmdLine clCmdLine, Process pProcess)
    {
        m_clCmdLine = clCmdLine;
        this.pProcess = pProcess;
    }

    /**
     * Starts the stdout and stderr pumpers.
     * 
     * @throws CmdLineException Thrown if the operation failed.
     */
    public void startPumpers() throws CmdLineException
    {
        if (pProcess == null)
        {
            throw new CmdLineException("Process is not set.", m_clCmdLine);
        }

        // Now attach the streampumpers to capture the output.
        clspStdOut = new CmdLineStreamPumper(pProcess.getInputStream(), true);
        clspStdErr = new CmdLineStreamPumper(pProcess.getErrorStream(), true);

        tStdOut = new Thread(clspStdOut);
        tStdErr = new Thread(clspStdErr);
        tStdOut.start();
        tStdErr.start();
    }

    /**
     * Waits until the process is finished.
     * 
     * @param terminateOnInterrupt If <code>true</code> the process is terminated if the waiting is interruped.
     * @return Process return code.
     */
    public int waitFor(boolean terminateOnInterrupt) throws CmdLineException
    {
        int iReturn = 0;

        if (pProcess == null)
        {
            throw new CmdLineException("Process is not set.", m_clCmdLine);
        }

        // Wait for the command to finish.
        try
        {
            if (lLogger.isDebugEnabled())
            {
                lLogger.debug("Waiting for the streams to finish.");
            }
            clspStdOut.waitFor();
            clspStdErr.waitFor();

            if (lLogger.isDebugEnabled())
            {
                lLogger.debug("Waiting for the process to finish.");
            }

            int iTmpReturn = pProcess.waitFor();

            if (lLogger.isDebugEnabled())
            {
                lLogger.debug("The exit code: " + iTmpReturn);
            }
        }
        catch (InterruptedException e)
        {
            lLogger.error("Error waiting for the streams.", e);

            if (terminateOnInterrupt)
            {
                // Destroy the process.
                pProcess.destroy();
            }
        }

        // Try to get the exitcode
        iReturn = pProcess.exitValue();
        pProcess = null;

        if (lLogger.isInfoEnabled())
        {
            lLogger.info("Returncode: " + iReturn + "\nCommand: " + m_clCmdLine.getFullCommand());
        }

        // Get the stdout and stderr messages.
        m_clCmdLine.setStdErr(clspStdErr.toString());
        m_clCmdLine.setStdOut(clspStdOut.toString());

        if (lLogger.isDebugEnabled())
        {
            lLogger.debug("StdOut:\n" + m_clCmdLine.getStdOut());
            lLogger.debug("StdErr:\n" + m_clCmdLine.getStdErr());
        }

        if ((m_clCmdLine.getStdErr().length() > 0) && (m_clCmdLine.getFailOnStdErr() == true))
        {
            throw new CmdLineStderrException(iReturn, m_clCmdLine.getStdErr(), m_clCmdLine);
        }

        return iReturn;
    }

    /**
     * Terminates the process if it is still running.
     */
    public void terminate()
    {
        if (pProcess != null)
        {
            pProcess.destroy();
            pProcess = null;
        }
    }

    /**
     * Returns the process.
     * 
     * @return Returns the process.
     */
    public Process getProcess()
    {
        return pProcess;
    }
}
