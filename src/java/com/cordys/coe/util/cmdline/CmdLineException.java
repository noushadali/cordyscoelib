package com.cordys.coe.util.cmdline;

/**
 * Command line exception class.
 * 
 * @author pgussow
 */
public class CmdLineException extends Exception
{
    /** Holds the code if the result is undefined. */
    public static final int RETURNCODE_UNDEFINED = -99;
    /** Holds the returncode of the command. -99 means undefined. */
    private int m_iReturnCode = RETURNCODE_UNDEFINED;
    /** Holds the parent commandline that caused the error. */
    private CmdLine m_cmdLine;

    /**
     * Creates a new CmdLineException object.
     * 
     * @deprecated
     */
    public CmdLineException()
    {
        super();
    }

    /**
     * Creates a new CmdLineException object.
     * 
     * @param sMessage The messgae for the exception.
     */
    public CmdLineException(String sMessage)
    {
        super(sMessage);
    }
    
    /**
     * Creates a new CmdLineException object.
     * 
     * @param sMessage The messgae for the exception.
     * @param cmdLine the cmd line
     */
    public CmdLineException(String sMessage, CmdLine cmdLine)
    {
        super(sMessage);
        m_cmdLine = cmdLine;
    }

    /**
     * Creates a new CmdLineException object.
     * 
     * @param tCause The cause of the execption.
     * @deprecated
     */
    public CmdLineException(Throwable tCause)
    {
        super(tCause);
    }

    /**
     * Creates a new CmdLineException object.
     * 
     * @param sMessage The message for the exception.
     * @param tCause The cause of the execption.
     * @deprecated
     */
    public CmdLineException(String sMessage, Throwable tCause)
    {
        super(sMessage, tCause);
    }

    /**
     * Creates a new CmdLineException object.
     * 
     * @param iReturnCode The returncode of the program.
     * @param sMessage the message (stderr).
     * @deprecated Use {@link CmdLineStderrException#CmdLineStderrException(int, String, CmdLine)}
     */
    public CmdLineException(int iReturnCode, String sMessage)
    {
        super(sMessage);
        m_iReturnCode = iReturnCode;
    }

    /**
     * Creates a new CmdLineException object.
     * 
     * @param iReturnCode The returncode of the program.
     * @param sMessage the message (stderr).
     * @param cmdLine the cmd line
     */
    public CmdLineException(int iReturnCode, String sMessage, CmdLine cmdLine)
    {
        super(sMessage);
        m_iReturnCode = iReturnCode;
        m_cmdLine = cmdLine;
    }

    /**
     * Creates a new CmdLineException object.
     * 
     * @param sMessage the message (stderr).
     * @param tCause the t cause
     * @param cmdLine the cmd line
     */
    public CmdLineException(String sMessage, Throwable tCause, CmdLine cmdLine)
    {
        super(sMessage, tCause);
        m_cmdLine = cmdLine;
    }

    /**
     * This method gets the return code.
     * 
     * @return The return code.
     */
    public int getReturnCode()
    {
        return m_iReturnCode;
    }

    /**
     * This method gets the command line that was executed.
     * 
     * @return The command line that was executed.
     */
    public CmdLine getCmdLine()
    {
        return m_cmdLine;
    }
}
