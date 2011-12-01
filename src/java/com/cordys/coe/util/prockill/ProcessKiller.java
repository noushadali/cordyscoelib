/**
 *       2003 Cordys R&D B.V. All rights reserved.
 *      The computer program(s) is the proprietary information of Cordys R&D B.V.
 *      and provided under the relevant License Agreement containing restrictions
 *      on use and disclosure. Use is subject to the License Agreement.
 */
package com.cordys.coe.util.prockill;

import com.cordys.coe.util.system.processes.SystemProcess;
import com.cordys.coe.util.system.processes.SystemProcessList;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Find;
import com.eibus.xml.nom.Node;

/**
 * This class kills all processes that are specified in the passed on XML-file. The XML-file should
 * look like this: <processes> <process> <exename>java.exe</exename> or <processid>250</processid>
 * </process> <process> <exename>cmd.exe</exename> or <processid>1288</processid> </process>
 * </processes> The <processid> is leading. That means if it finds the processid-tag it ignores the
 * <exename>.
 *
 * @author  pgussow
 */
public class ProcessKiller
{
    /**
     * The main method.
     *
     * @param  args  The arguments passed on when the program started.
     */
    public static void main(String[] args)
    {
        // Check if there is 1 argument passed on
        if (args.length != 1)
        {
            displayUsage();
        }

        // Process the configurationfile and kill the configured processes.
        try
        {
            killConfiguredProcesses(args[0]);
        }
        catch (Exception e)
        {
            System.out.println("Error:\n" + e);
        }

        System.exit(0);
    } // main

    /**
     * This method displays the usage of this program.
     */
    private static void displayUsage()
    {
        System.out.println("Usage:");
        System.out.println("    java com.vanenburg.util.prockill.ProcessKiller <configfile.xml>");
        System.out.println("");
        System.exit(-1);
    } // displayUsage

    /**
     * This method reads the file. Then the file is processed and all processes that are in the file
     * are killed.
     *
     * @param   sFileName  Name of the configurationfile.
     *
     * @throws  Exception  DOCUMENTME
     */
    private static void killConfiguredProcesses(String sFileName)
                                         throws Exception
    {
        // Check if the filename is filled.
        if ((sFileName == null) || sFileName.equals("") || sFileName.equals("\"\""))
        {
            throw new Exception("Filename cannot be empty.");
        }

        // Read the XMLfile
        Document dDoc = new Document();
        int iXMLNode = dDoc.load(sFileName);

        if (iXMLNode == 0)
        {
            throw new Exception("Configurationfile couldn't be loaded.");
        }

        // Create the systemprocesslist and get the processes.
        SystemProcessList splList = new SystemProcessList();
        splList.getSystemProcessesList();

        // Process the configured processes.
        int[] aiProcess = Find.match(iXMLNode, "?<process>");

        for (int iCount = 0; iCount < aiProcess.length; iCount++)
        {
            // First see if the processid-tag is available
            int iTmpNode = Find.firstMatch(aiProcess[iCount], "?<processid>");

            if (iTmpNode != 0)
            {
                String sData = Node.getData(iTmpNode);

                if ((sData != null) && !sData.equals(""))
                {
                    SystemProcess spProc = splList.findSystemProcess(Long.parseLong(sData));

                    if (spProc != null)
                    {
                        // Kill the process
                        splList.killProcess(spProc);
                    }
                }
            }
            else
            {
                // No processid found, try for the <exename>
                iTmpNode = Find.firstMatch(aiProcess[iCount], "?<exename>");

                if (iTmpNode != 0)
                {
                    String sData = Node.getData(iTmpNode);

                    if ((sData != null) && !sData.equals(""))
                    {
                        System.out.println("About to kill all '" + sData + "'");
                        // Kill case-insensitive, but don't kill ourself.
                        splList.killProcess(sData, false, false);
                    }
                }
            }
        }
    } // killConfiguredProcesses
} // ProcessKiller
