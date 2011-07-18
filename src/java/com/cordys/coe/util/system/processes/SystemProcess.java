/**
 *      © 2003 Cordys R&D B.V. All rights reserved.
 *      The computer program(s) is the proprietary information of Cordys R&D B.V.
 *      and provided under the relevant License Agreement containing restrictions
 *      on use and disclosure. Use is subject to the License Agreement.
 */
package com.cordys.coe.util.system.processes;

import com.eibus.xml.nom.Document;

/**
 * This class identifies a systemprocess.
 */
public class SystemProcess
{
    /**
     * Holds the number of threads used by the process.
     */
    private int iCntThreads;
    /**
     * Holds the priority of the process.
     */
    private int iPriority;
    /**
     * Holds the exefile of the process.
     */
    private String sExeFile;
    /**
     * Holds the moduleid of the process.
     */
    private String sModuleID;
    /**
     * Holds the parent-processid of the process.
     */
    private String sParentPID;
    /**
     * Holds the processid of the process.
     */
    private String sProcessID;

    /**
     * The default constructor.
     */
    public SystemProcess()
    {
    } // SystemProcess

    /**
     * Creates a new instance of SystemProcesses.
     *
     * @param  sProcessID   The ID of the process
     * @param  sModuleID    The ID of the module for that process
     * @param  iCntThreads  The number of threads the process is using.
     * @param  sParentPID   The processid of the parent of this process
     * @param  iPriority    The priority of the thread
     * @param  sExeFile     The name of the executable.
     */
    public SystemProcess(String sProcessID, String sModuleID, int iCntThreads, String sParentPID,
                         int iPriority, String sExeFile)
    {
        this.sProcessID = sProcessID;
        this.sModuleID = sModuleID;
        this.iCntThreads = iCntThreads;
        this.sParentPID = sParentPID;
        this.iPriority = iPriority;
        this.sExeFile = sExeFile;
    } // SystemProcess

    /**
     * This method gets the number of threads.
     *
     * @return  The number of threads.
     */
    public int getCntThreads()
    {
        return iCntThreads;
    } // getCntThreads

    /**
     * This method gets the name of the exefile.
     *
     * @return  The name of the exefile.
     */
    public String getExeName()
    {
        return sExeFile;
    } // getExeName

    /**
     * This method gets the moduleid.
     *
     * @return  The moduleid
     */
    public String getModuleID()
    {
        return sModuleID;
    } // getModuleID

    /**
     * This method gets the processid of the parentprocess.
     *
     * @return  The processid of the parentprocess.
     */
    public String getParentPID()
    {
        return sParentPID;
    } // getParentPID

    /**
     * This method gets the priority of the process.
     *
     * @return  The priority of the process.
     */
    public int getPriority()
    {
        return iPriority;
    } // getPriority

    /**
     * This method gets the processid.
     *
     * @return  The processid
     */
    public String getProcessID()
    {
        return sProcessID;
    } // getProcessID

    /**
     * This method sets the number of threads.
     *
     * @param  iCntThreads  The new number of threads being used.
     */
    public void setCntThreads(int iCntThreads)
    {
        this.iCntThreads = iCntThreads;
    } // setCntThreads

    /**
     * This method sets the name of the exefile.
     *
     * @param  sExeFile  The new name of the exefile.
     */
    public void setExeName(String sExeFile)
    {
        this.sExeFile = sExeFile;
    } // setExeName

    /**
     * This method sets the moduleid.
     *
     * @param  sModuleID  The new moduleid
     */
    public void setModuleID(String sModuleID)
    {
        this.sModuleID = sModuleID;
    } // setModuleID

    /**
     * This method sets the processid of the parentprocess.
     *
     * @param  sParentPID  The new processid of the parentprocess.
     */
    public void setParentPID(String sParentPID)
    {
        this.sParentPID = sParentPID;
    } // setParentPID

    /**
     * This method sets the priority of the process.
     *
     * @param  iPriority  The new priority of the process.
     */
    public void setPriority(int iPriority)
    {
        this.iPriority = iPriority;
    } // setPriority

    /**
     * This method sets the processid.
     *
     * @param  sProcessID  The new processid
     */
    public void setProcessID(String sProcessID)
    {
        this.sProcessID = sProcessID;
    } // setProcessID

    /**
     * This method returns the string-representation of this object.
     *
     * @return  The string-representation of this object.
     */
    @Override public String toString()
    {
        return "Details for process with PID " + sProcessID + "\n" +
               "   Module   : " + sModuleID + "\n" + "   Threads  : " + iCntThreads + "\n" +
               "   ParentPID: " + sParentPID + "\n" +
               "   Priority : " + iPriority + "\n" + "   Exe-file : " + sExeFile + "\n";
    } // toString

    /**
     * This method returns an XML-node that contains the XML-representation of the systemprocess.
     *
     * @param   dDoc  The document to create the XML in.
     *
     * @return  An XML-node that contains the XML-representation of the systemprocess.
     */
    public int toXML(Document dDoc)
    {
        int iReturn = dDoc.createElement("systemprocess");
        dDoc.createTextElement("processid", getProcessID(), iReturn);
        dDoc.createTextElement("exename", getExeName(), iReturn);
        dDoc.createTextElement("cntthread", "" + getCntThreads(), iReturn);
        dDoc.createTextElement("priority", "" + getPriority(), iReturn);
        dDoc.createTextElement("parentpid", getParentPID(), iReturn);
        dDoc.createTextElement("moduleid", getModuleID(), iReturn);

        return iReturn;
    } // toXML
} // SystemProcess
