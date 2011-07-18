/**
 *      © 2003 Cordys R&D B.V. All rights reserved.
 *      The computer program(s) is the proprietary information of Cordys R&D B.V.
 *      and provided under the relevant License Agreement containing restrictions
 *      on use and disclosure. Use is subject to the License Agreement.
 */
package com.cordys.coe.util.system.processes;

import java.util.Vector;

/**
 * DOCUMENTME.
 *
 * @author  pgussow
 */
public class SystemProcessList
{
    static
    {
        System.loadLibrary("sysprocj");
    } // static

    /**
     * Vector that contains the current system processes.
     */
    private Vector<?> vProcesses;

    /**
     * Constructor. Initializes the class and creates the vector to store the processes.
     */
    public SystemProcessList()
    {
        vProcesses = new Vector<Object>();
        nativeEnableKill();
    } // SystemProcessList

    /**
     * This method returns the processid of the current process.
     *
     * @return  A long containing the processID of the current process
     */
    public static native long getCurrentProcessID();

    /**
     * Main test method...
     *
     * @param  arg  DOCUMENTME
     */
    public static void main(String[] arg)
    {
        try
        {
            SystemProcessList spl = new SystemProcessList();
            spl.getSystemProcessesList();
            System.out.println("Current ProcID: " + getCurrentProcessID());

            spl.killProcess("IEXPLORE.EXE");
        }
        catch (Exception e)
        {
            System.out.println("Exception:\n" + e);
        }
    }

    /**
     * This method disabled the securitysetting to enable killing of processes.
     */
    public void disableKill()
    {
        nativeDisableKill();
    }

    /**
     * This method returns the SystemProcess for the given processID.
     *
     * @param   lProcessID  The processID to find.
     *
     * @return  An instance of the SystemProcess-class containing the information for the given process.
     */
    public SystemProcess findSystemProcess(long lProcessID)
    {
        SystemProcess spReturn = null;

        for (int iCount = 0; iCount < vProcesses.size(); iCount++)
        {
            SystemProcess spCurrent = (SystemProcess) vProcesses.get(iCount);

            if (Long.parseLong(spCurrent.getProcessID()) == lProcessID)
            {
                spReturn = spCurrent;
                break;
            }
        }

        return spReturn;
    } // findSystemProcess

    /**
     * This method asks the DLL to add all the current system processes to the passed on vector.
     *
     * @return  A vector containing all the system processes.
     *
     * @throws  Exception  In case of any exceptions
     */
    public Vector<?> getSystemProcessesList()
                                     throws Exception
    {
        vProcesses.clear();

        try
        {
            nativeGetSystemProcesses(vProcesses);
        }
        catch (Exception e)
        {
            throw e;
        }
        return vProcesses;
    } // getSystemProcessesList

    /**
     * This method kills the given process.
     *
     * @param  spProcess  The process to kill.
     */
    public void killProcess(SystemProcess spProcess)
    {
        nativeKillProcess(spProcess);
    } // killProcess

    /**
     * This method kills all processes that have an exename that is the same as sExeName. It retrieves the list of
     * curerntly running system processes and searches for all processes that correspond with the given name. After a
     * process is found it is killed. The current process cannot be killed.
     *
     * @param   sExeName  The name of the exe's to kill.
     *
     * @throws  Exception  DOCUMENTME
     */
    public void killProcess(String sExeName)
                     throws Exception
    {
        killProcess(sExeName, false, true);
    } // killProcess

    /**
     * This method kills all processes that have an exename that is the same as sExeName. It retrieves the list of
     * curerntly running system processes and searches for all processes that correspond with the given name. After a
     * process is found it is killed.
     *
     * @param   sExeName     The name of the exe's to kill.
     * @param   bKillItself  Boolean that indicates wether or not the process should be able to kill itself if sExeName
     *                       == own virtual machine.
     * @param   bMatchCase   Indicates wether or not the passed on exename should be matched casesensitive (cmd.exe !=
     *                       CDM.EXE).
     *
     * @throws  Exception  In case of any exceptions
     */
    public void killProcess(String sExeName, boolean bKillItself, boolean bMatchCase)
                     throws Exception
    {
        boolean bShouldSelfDestruct = false;

        // First retrieve the current list of processes
        Vector<?> vProc = getSystemProcessesList();

        // Get the current processID
        long lCurrentID = getCurrentProcessID();

        // Now iterate trough the list to find all processes that match the given exename
        for (int iCount = 0; iCount < vProc.size(); iCount++)
        {
            SystemProcess spProcess = (SystemProcess) vProc.get(iCount);

            boolean bEquals = false;

            if (bMatchCase == true)
            {
                bEquals = (spProcess.getExeName() != null) && spProcess.getExeName().equals(sExeName);
            }
            else
            {
                bEquals = (spProcess.getExeName() != null) && spProcess.getExeName().equalsIgnoreCase(sExeName);
            }

            if (bEquals)
            {
                if (Long.parseLong(spProcess.getProcessID()) == lCurrentID)
                {
                    if (bKillItself == true)
                    {
                        bShouldSelfDestruct = true;
                    }
                }
                else
                {
                    killProcess(spProcess);
                }
            }
        }

        // If this process should be killed, make sure it is killed AFTER all the others
        // are killed.
        if (bShouldSelfDestruct == true)
        {
            killProcess(findSystemProcess(lCurrentID));
        }
    } // killProcess

    /**
     * This method disables the privilege to kill all processes.
     */
    private native void nativeDisableKill();

    /**
     * This method enables the privilege to kill all processes.
     */
    private native void nativeEnableKill();

    /**
     * This method adds all the processes found to the vector.
     *
     * @param  vProcesses  The vector that should contain the system processes.
     */
    private synchronized native void nativeGetSystemProcesses(Vector<?> vProcesses);

    /**
     * This method kills the passed on process.
     *
     * @param  spProcess  The system process to kill.
     */
    private native void nativeKillProcess(SystemProcess spProcess);
} // SystemProcessList
