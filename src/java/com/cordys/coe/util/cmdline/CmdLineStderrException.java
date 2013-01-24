package com.cordys.coe.util.cmdline;

/**
 * Command line exception class for errors when process failed because
 * it wrote to stderr.
 *
 * @author  mpoyhone
 */
public class CmdLineStderrException extends CmdLineException
{
    
    /**
     * Instantiates a new cmd line stderr exception.
     * @deprecated
     */
    public CmdLineStderrException()
    {
        super();
    }
    
    /**
     * Instantiates a new cmd line stderr exception.
     * 
     * @param iReturnCode the i return code
     * @param sMessage the s message
     * @param cmdLine the cmd line that was executed.
     */
    public CmdLineStderrException(int iReturnCode, String sMessage, CmdLine cmdLine)
    {
        super(iReturnCode, sMessage, cmdLine);
    }

    /**
     * Instantiates a new cmd line stderr exception.
     * 
     * @param iReturnCode the i return code
     * @param sMessage the s message
     * @deprecated Please use the {@link CmdLineStderrException#CmdLineStderrException(int, String, CmdLine)} constructor.
     */
    public CmdLineStderrException(int iReturnCode, String sMessage)
    {
        super(iReturnCode, sMessage);
    }

    /**
     * Instantiates a new cmd line stderr exception.
     * 
     * @param sMessage the s message
     * @param tCause the t cause
     * @deprecated Please use the {@link CmdLineStderrException#CmdLineStderrException(String, Throwable, CmdLine))} constructor.
     */
    public CmdLineStderrException(String sMessage, Throwable tCause)
    {
        super(sMessage, tCause);
    }
    
    /**
     * Instantiates a new cmd line stderr exception.
     * 
     * @param sMessage the s message
     * @param tCause the t cause
     * @param cmdLine the cmd line that was executed.
     * 
     */
    public CmdLineStderrException(String sMessage, Throwable tCause, CmdLine cmdLine)
    {
        super(sMessage, tCause, cmdLine);
    }

    /**
     * Instantiates a new cmd line stderr exception.
     * 
     * @param sMessage the s message
     * @deprecated
     */
    public CmdLineStderrException(String sMessage)
    {
        super(sMessage);
    }

    /**
     * Instantiates a new cmd line stderr exception.
     * 
     * @param tCause the t cause
     * @deprecated
     */
    public CmdLineStderrException(Throwable tCause)
    {
        super(tCause);
    }
}
