/**
 *  2003 Cordys R&D B.V. All rights reserved.     The computer program(s) is
 * the proprietary information of Cordys R&D B.V.     and provided under the
 * relevant License Agreement containing restrictions     on use and
 * disclosure. Use is subject to the License Agreement.
 */
package com.cordys.coe.util.system;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Find;
import com.eibus.xml.nom.Node;

/**
 * This class enables the user to configure a set of system-commands that should be executed in
 * batch. The configuration-XML looks like this:
 *
 * @author  pgussow
 */
public class BatchExecute
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
            executeBatch(args[0]);
        }
        catch (Exception e)
        {
            System.out.println("Error:\n" + e);
        }

        System.exit(0);
    }

    /**
     * This method displays the usage of this program.
     */
    private static void displayUsage()
    {
        System.out.println("Usage:");
        System.out.println("    java com.vanenburg.util.prockill.ProcessKiller <configfile.xml>");
        System.out.println("");
        System.exit(-1);
    }

    /**
     * This method reads the configurationfile. Then the file is processed and all the commands that
     * are in the file will be executed.
     *
     * @param   sFileName  Name of the configurationfile.
     *
     * @throws  Exception  DOCUMENTME
     */
    private static void executeBatch(String sFileName)
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

        // Process the configured processes.
        int[] aiCommands = Find.match(iXMLNode, "?<command>");

        // Get the current runtime.
        Runtime runtime = Runtime.getRuntime();

        for (int iCount = 0; iCount < aiCommands.length; iCount++)
        {
            // First see if the processid-tag is available
            int iTmpNode = Find.firstMatch(aiCommands[iCount], "?<program>");

            if (iTmpNode != 0)
            {
                String sProgram = Node.getData(iTmpNode);

                if ((sProgram != null) && !sProgram.equals(""))
                {
                    // Build up the actual command
                    String sActualCommand = sProgram;
                    int[] aiArgs = Find.match(aiCommands[iCount], "?<arguments><argument>");

                    // Fill all the arguments
                    for (int iArgCount = 0; iArgCount < aiArgs.length; iCount++)
                    {
                        String sData = Node.getData(aiArgs[iArgCount]);

                        if ((sData != null) && !sData.equals("") && !sData.equals("\"\""))
                        {
                            sActualCommand += (" " + sData);
                        }
                    }

                    // Execute the command
                    try
                    {
                        runtime.exec(sActualCommand);
                    }
                    catch (Exception e)
                    {
                        System.out.println("Error executing command\n" + sActualCommand +
                                           "\n Error:\n" + e + "\n");
                    }
                }
            }
        }
    }
}
