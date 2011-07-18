/*
 * Created on Nov 11, 2004
 *
 * TODO To change the template for this generated file go to Window - Preferences - Java - Code Style
 * - Code Templates
 */
package com.cordys.coe.util.log;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

/**
 * A simple wrapper class for Ant logging facility.
 *
 * @author  mpoyhone
 */
public class AntTaskLogger
    implements LogInterface
{
    /**
     * Content name to be used in front of the messages.
     */
    protected String sContentName;
    /**
     * The Ant task.
     */
    protected Task tTask = null;

    /**
     * Creates a new TaskLogger object.
     *
     * @param  tTask  The Ant task that will be used for logging.
     */
    public AntTaskLogger(Task tTask)
    {
        this.tTask = tTask;
    }

    /**
     * Creates a new TaskLogger object.
     *
     * @param  tTask         The Ant task that will be used for logging.
     * @param  sContentName  Content name to be used in front of the messages.
     */
    public AntTaskLogger(Task tTask, String sContentName)
    {
        this(tTask);
        this.sContentName = sContentName;
    }

    /**
     * Writes a debug log message.
     *
     * @param  sMsg  The log message.
     */
    public void debug(String sMsg)
    {
        if (tTask != null)
        {
            if (sContentName != null)
            {
                tTask.log("[" + sContentName + "] " + sMsg, Project.MSG_DEBUG);
            }
            else
            {
                tTask.log(sMsg, Project.MSG_DEBUG);
            }
        }
    }

    /**
     * Writes an error log message.
     *
     * @param  sMsg  The log message.
     */
    public void error(String sMsg)
    {
        if (tTask != null)
        {
            if (sContentName != null)
            {
                tTask.log("[" + sContentName + "] " + sMsg, Project.MSG_ERR);
            }
            else
            {
                tTask.log(sMsg, Project.MSG_ERR);
            }
        }
    }

    /**
     * Writes an error log message.
     *
     * @param  sMsg        The log message.
     * @param  tException  The exception to be logged.
     */
    public void error(String sMsg, Throwable tException)
    {
        if (tTask != null)
        {
            if (sContentName != null)
            {
                tTask.log("[" + sContentName + "] " + sMsg + " : " + tException, Project.MSG_ERR);
            }
            else
            {
                tTask.log(sMsg + " : " + tException, Project.MSG_ERR);
            }
        }
    }

    /**
     * Writes an info log message.
     *
     * @param  sMsg  The log message.
     */
    public void info(String sMsg)
    {
        if (tTask != null)
        {
            if (sContentName != null)
            {
                tTask.log("[" + sContentName + "] " + sMsg, Project.MSG_INFO);
            }
            else
            {
                tTask.log(sMsg, Project.MSG_INFO);
            }
        }
    }

    /**
     * @see  com.cordys.coe.util.log.LogInterface#isDebugEnabled()
     */
    public boolean isDebugEnabled()
    {
        if (tTask != null)
        {
            // TODO: How to get this from Ant?
            return true;
        }

        return false;
    }

    /**
     * @see  com.cordys.coe.util.log.LogInterface#isInfoEnabled()
     */
    public boolean isInfoEnabled()
    {
        if (tTask != null)
        {
            // TODO: How to get this from Ant?
            return true;
        }

        return false;
    }
}
