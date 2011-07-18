package com.cordys.coe.util.cmdline;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * This class is a wrapper around the commandline. It supports synchronous execution of a shell command. When the
 * command has finished you can get the output of both stdout and stderr.
 *
 * @author  pgussow
 */
public class CmdLine
{
    /**
     * Holds the logger for this class.
     */
    private static final Logger LOG = Logger.getLogger(CmdLine.class);
    /**
     * Offset in the argument array where the real arguments start.
     */
    private int argumentOffset = 1;
    /**
     * Holds the arguments for the command.
     */
    private ArrayList<String> m_alArguments;
    /**
     * Indicates if this class should throw an exception when something is written to Std Error.
     */
    private boolean m_bFailOnStdErr = true;
    /**
     * Holds the working folder.
     */
    private File m_fWorkingFolder = null;
    /**
     * Holds the environment variables for the command.
     */
    private Map<String, String> m_mEnvVariables;
    /**
     * Holds the command to execute.
     */
    private String m_sCommand;
    /**
     * Holds the output from stdErr.
     */
    private String m_sStdErr = "";
    /**
     * Holds the output from stdout.
     */
    private String m_sStdOut = "";

    /**
     * Creates a new CmdLine object. The parameter should be the name of the executable WITHOUT the arguments. Arguments
     * for the command should be added later using addArgument(String).
     *
     * @param   sCommand  The command to execute.
     *
     * @throws  CmdLineException  DOCUMENTME
     */
    public CmdLine(String sCommand)
            throws CmdLineException
    {
        this.m_sCommand = sCommand;

        if ((m_sCommand == null) || (m_sCommand.length() == 0))
        {
            throw new CmdLineException("Command cannot be null.");
        }
        m_alArguments = new ArrayList<String>();
        m_alArguments.add(0, sCommand);
        argumentOffset = 1;
    }

    /**
     * Creates a new CmdLine object. The parameter should be the name of the executable WITHOUT the arguments. Arguments
     * for the command should be added later using addArgument(String). This version is when the command consist of
     * multiple arguments (e.g. cmd.exe /c ...).
     *
     * @param   commands  The command to execute.
     *
     * @throws  CmdLineException  In case of any exceptions.
     */
    public CmdLine(String... commands)
            throws CmdLineException
    {
        if ((commands == null) || (commands.length == 0))
        {
            throw new CmdLineException("Command cannot be null.");
        }

        this.m_sCommand = commands[0];
        m_alArguments = new ArrayList<String>();

        for (String cmd : commands)
        {
            m_alArguments.add(cmd);
        }

        argumentOffset = commands.length;
    }

    /**
     * This method adds an argument to the command.
     *
     * @param  sArgument  The argument to add.
     */
    public void addArgument(String sArgument)
    {
        m_alArguments.add(sArgument);
    }

    /**
     * This method executes the command. It waits for the command to finish and then returns the exitcode of the
     * command. When this method returns you can use getStdOut() and getStdErr() to get the output of the command.
     *
     * @return  The exitcode of the command.
     *
     * @throws  CmdLineException  DOCUMENTME
     */
    public int execute()
                throws CmdLineException
    {
        return startProcess().waitFor(false);
    }

    /**
     * This method gets the command that is executed.
     *
     * @return  The command that is executed.
     */
    public String getCommand()
    {
        return m_sCommand;
    }

    /**
     * This method gets whether or not the commandline should throw an exception when the command writes something to
     * stderr.
     *
     * @return  Whether or not the commandline should throw an exception when the command writes something to stderr.
     */
    public boolean getFailOnStdErr()
    {
        return m_bFailOnStdErr;
    }

    /**
     * This method returns the full command that was executed.
     *
     * @return  The full command.
     */
    public String getFullCommand()
    {
        StringBuffer sbReturn = new StringBuffer("");

        for (Iterator<String> iCommands = m_alArguments.iterator(); iCommands.hasNext();)
        {
            String sPart = iCommands.next();
            sbReturn.append("\"");
            sbReturn.append(sPart);
            sbReturn.append("\"");

            if (iCommands.hasNext())
            {
                sbReturn.append(" ");
            }
        }

        if (m_fWorkingFolder != null)
        {
            sbReturn.append(" in folder ");
            sbReturn.append(m_fWorkingFolder.getPath());
        }

        if (m_mEnvVariables != null)
        {
            sbReturn.append(" with environment: " + m_mEnvVariables);
        }

        return sbReturn.toString();
    }

    /**
     * This method gets the output from stderr.
     *
     * @return  The output from stderr.
     */
    public String getStdErr()
    {
        return m_sStdErr;
    }

    /**
     * This method gets the output from stdout.
     *
     * @return  The output from stdout.
     */
    public String getStdOut()
    {
        return m_sStdOut;
    }

    /**
     * This method inserts an argument to the command in the given position. Shifts the argument currently at that
     * position to the right.
     *
     * @param  sArgument  The argument to add.
     * @param  iPos       Insertion position.
     */
    public void insertArgument(String sArgument, int iPos)
    {
        if (iPos < (m_alArguments.size() - argumentOffset))
        {
            m_alArguments.add(iPos + argumentOffset, sArgument);
        }
        else
        {
            m_alArguments.add(sArgument);
        }
    }

    /**
     * This method sets wether or not the commandline should throw an exception when the command writes something to
     * stderr.
     *
     * @param  bFailOnStdErr  Whether or not the commandline should throw an exception when the command writes something
     *                        to stderr.
     */
    public void setFailOnStdErr(boolean bFailOnStdErr)
    {
        m_bFailOnStdErr = bFailOnStdErr;
    }

    /**
     * This method sets an environment variable to the command.
     *
     * @param  name   The name of the environment variable to set.
     * @param  value  The value of the environment variable to set. If <code>null</code>, the value is removed.
     */
    public void setsEnvironmentVariable(String name, String value)
    {
        if (m_mEnvVariables == null)
        {
            m_mEnvVariables = new HashMap<String, String>();
        }

        if (value != null)
        {
            m_mEnvVariables.put(name, value);
        }
        else
        {
            m_mEnvVariables.remove(name);
        }
    }

    /**
     * This method sets the working folder in which the command should be executed.
     *
     * @param  fWorkingFolder  the new working folder.
     */
    public void setWorkingFolder(File fWorkingFolder)
    {
        m_fWorkingFolder = fWorkingFolder;
    }

    /**
     * This method starts the command. It returns an object which contains the actual process object and stdout and
     * stderr pumpers.
     *
     * @return  The exitcode of the command.
     *
     * @throws  CmdLineException  DOCUMENTME
     */
    public RunningProcess startProcess()
                                throws CmdLineException
    {
        // Create the process
        Process pProcess = execCommand();

        if (pProcess == null)
        {
            throw new CmdLineException("Could not create the process.");
        }

        RunningProcess proc = new RunningProcess(this, pProcess);

        proc.startPumpers();

        return proc;
    }

    /**
     * This method sets the stderr.
     *
     * @param  value  The stderr.
     */
    void setStdErr(String value)
    {
        m_sStdErr = value;
    }

    /**
     * This method sets the stdout.
     *
     * @param  value  The stdout.
     */
    void setStdOut(String value)
    {
        m_sStdOut = value;
    }

    /**
     * This method executes the command and it's parameters and returns the Process.
     *
     * @return  The process that was executed.
     *
     * @throws  CmdLineException  DOCUMENTME
     */
    private Process execCommand()
                         throws CmdLineException
    {
        Process pReturn = null;
        ProcessBuilder builder = new ProcessBuilder(m_alArguments);

        if (m_fWorkingFolder != null)
        {
            builder.directory(m_fWorkingFolder);
        }

        if (m_mEnvVariables != null)
        {
            builder.environment().putAll(m_mEnvVariables);
        }

        // Execute the command.
        try
        {
            if (LOG.isDebugEnabled())
            {
                LOG.debug("Executing this command:\n" + getFullCommand() + "\nWorking folder: " +
                              ((m_fWorkingFolder != null) ? m_fWorkingFolder.getAbsolutePath() : "."));
            }

            pReturn = builder.start();
        }
        catch (IOException e)
        {
            throw new CmdLineException(e);
        }
        return pReturn;
    }
}
