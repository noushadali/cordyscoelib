package com.cordys.coe.util.cmdline;

/**
 * Command line exception class.
 *
 * @author  pgussow
 */
public class CmdLineException extends Exception
{
    /**
     * Holds the code if the result is undefined.
     */
    public static final int RETURNCODE_UNDEFINED = -99;
    /**
     * Holds the returncode of the command. -99 means undefined.
     */
    private int m_iReturnCode = RETURNCODE_UNDEFINED;

    /**
     * Creates a new CmdLineException object.
     */
    public CmdLineException()
    {
        super();
    }

    /**
     * Creates a new CmdLineException object.
     *
     * @param  sMessage  The messgae for the exception.
     */
    public CmdLineException(String sMessage)
    {
        super(sMessage);
    }

    /**
     * Creates a new CmdLineException object.
     *
     * @param  tCause  The cause of the execption.
     */
    public CmdLineException(Throwable tCause)
    {
        super(tCause);
    }

    /**
     * Creates a new CmdLineException object.
     *
     * @param  sMessage  The message for the exception.
     * @param  tCause    The cause of the execption.
     */
    public CmdLineException(String sMessage, Throwable tCause)
    {
        super(sMessage, tCause);
    }

    /**
     * Creates a new CmdLineException object.
     *
     * @param  iReturnCode  The returncode of the program.
     * @param  sMessage     the message (stderr).
     */
    public CmdLineException(int iReturnCode, String sMessage)
    {
        super(sMessage);
        m_iReturnCode = iReturnCode;
    }

    /**
     * This method gets the return code.
     *
     * @return  The return code.
     */
    public int getReturnCode()
    {
        return m_iReturnCode;
    }
}
