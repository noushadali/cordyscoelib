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
     * Constructor for CmdLineStderrException
     */
    public CmdLineStderrException()
    {
        super();
    }

    /**
     * Constructor for CmdLineStderrException
     * @param iReturnCode
     * @param sMessage
     */
    public CmdLineStderrException(int iReturnCode, String sMessage)
    {
        super(iReturnCode, sMessage);
    }

    /**
     * Constructor for CmdLineStderrException
     * @param sMessage
     * @param tCause
     */
    public CmdLineStderrException(String sMessage, Throwable tCause)
    {
        super(sMessage, tCause);
    }

    /**
     * Constructor for CmdLineStderrException
     * @param sMessage
     */
    public CmdLineStderrException(String sMessage)
    {
        super(sMessage);
    }

    /**
     * Constructor for CmdLineStderrException
     * @param tCause
     */
    public CmdLineStderrException(Throwable tCause)
    {
        super(tCause);
    }
}
