package com.cordys.coe.util.cmdline;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import java.util.HashMap;
import java.util.Properties;

/**
 * This class creates a batchfile for the passed on SOAP processor so that it can be started from
 * the commandprompt.
 *
 * @author  pgussow
 */
public class SPBatchCreator
{
    /**
     * Identifies the name of the parameter that holds the name of the output folder.
     */
    public static final String OUTPUT_FOLDER = "o";
    /**
     * Identifies the name of the parameter that holds the name of the batch file.
     */
    public static final String NAME_OF_BATCHFILE = "n";
    /**
     * Identifies the name of the parameter that holds whether or not the pause-command should be
     * included.
     */
    public static final String USE_PAUSE = "p";
    /**
     * Identifies the name of the parameter that holds the full DN of the SOAP processor.
     */
    public static final String DN_SOAPPROCESSOR = "dnsp";
    /**
     * Identifies the name of the parameter that holds the name of the propertyfile.
     */
    private static final String PROP_FILENAME = "f";
    /**
     * Identifies the name of the property in the propertyfile that holds the vm arguments.
     */
    private static final String PROP_VM_NORMAL_ARG = "vm.normal.arg";
    /**
     * Identifies the name of the property in the propertyfile that holds the vm arguments for
     * debugmode.
     */
    private static final String PROP_VM_DEBUG_ARG = "vm.debug.arg";
    /**
     * Identifies the name of the property that holds the name of the output folder.
     */
    private static final String PROP_OUTPUT_DIR = "output.dir";
    /**
     * Identifies the name of the property that holds whether or not the pause-command should be
     * included.
     */
    private static final String PROP_USE_PAUSE = "use.pause";
    /**
     * Identifies the name of the property that holds the name of the batch file.
     */
    private static final String PROP_BATCHFILE_NAME = "batchfile.name";
    /**
     * Holds the default anme of the propertyfile.
     */
    private static final String DEFAULT_PROP_FILENAME = "spbatch.properties";
    /**
     * Holds all the parameters for this class.
     */
    private HashMap<String, String> hmParameters;

    /**
     * Constructor.
     *
     * @throws  FileNotFoundException  DOCUMENTME
     * @throws  IOException            DOCUMENTME
     */
    public SPBatchCreator()
                   throws FileNotFoundException, IOException
    {
        hmParameters = new HashMap<String, String>();
    }

    /**
     * Main method.
     *
     * @param  saArgs  The commandline arguments.
     */
    public static void main(String[] saArgs)
    {
        SPBatchCreator sbcCreator = null;

        try
        {
            sbcCreator = new SPBatchCreator();

            int iCount = 0;

            while (iCount < saArgs.length)
            {
                String sParameter = saArgs[iCount];
                String sValue = null;
                int iIncCount = 1;

                if (sParameter.startsWith("-"))
                {
                    if (iCount == (saArgs.length - 1))
                    {
                        throw new Exception("Invalid usage.");
                    }
                    sValue = saArgs[iCount + 1];
                    iIncCount = 2;
                    sParameter = sParameter.substring(1);
                }

                if (iIncCount == 1)
                {
                    // It's the DN of the soapprocessor
                    sbcCreator.addParameter(DN_SOAPPROCESSOR, sParameter);
                }
                else if (sParameter.equalsIgnoreCase(OUTPUT_FOLDER) ||
                             sParameter.equalsIgnoreCase(NAME_OF_BATCHFILE) ||
                             sParameter.equalsIgnoreCase(USE_PAUSE) ||
                             sParameter.equals(PROP_FILENAME))
                {
                    sbcCreator.addParameter(sParameter, sValue);
                }
                else
                {
                    printUsage();
                }
                iCount += iIncCount;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            printUsage();
        }

        // Check for mandatory parameter
        if (sbcCreator.getParameter(DN_SOAPPROCESSOR) == null)
        {
            printUsage();
        }

        try
        {
            // Create the batchfile.
            sbcCreator.readPropertyfile();
            sbcCreator.createFile();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * This method adds the passed on parameter and value. If the parameter is already set it throws
     * an Exception.
     *
     * @param   sName   The name of the parameter.
     * @param   sValue  The value of the parameter.
     *
     * @throws  Exception  DOCUMENTME
     */
    public void addParameter(String sName, String sValue)
                      throws Exception
    {
        if (hmParameters.containsKey(sName))
        {
            throw new Exception("Parameter " + sName + " already set.");
        }

        if ((sValue == null) || (sValue.length() == 0))
        {
            throw new Exception("Parameter " + sName + " must be filled.");
        }
        hmParameters.put(sName, sValue);
    }

    /**
     * This method creates the batchfile for the specific soapprocessor.
     *
     * @throws  Exception  DOCUMENTME
     */
    public void createFile()
                    throws Exception
    {
        // Get the cordys classpath
        CmdLine clLine = new CmdLine("java");
        clLine.addArgument("com.eibus.util.CordysClassPath");
        clLine.addArgument(getParameter(DN_SOAPPROCESSOR));
        clLine.execute();

        String sCordysClassPath = clLine.getStdOut();

        // Create the output folder.
        String sDestFolder = getParameter(OUTPUT_FOLDER);
        File fDir = new File(sDestFolder);

        if (!fDir.exists())
        {
            fDir.mkdirs();
        }

        File fBatchFile = new File(fDir, getParameter(NAME_OF_BATCHFILE));

        if (fBatchFile.exists())
        {
            fBatchFile.delete();
        }

        FileWriter fwOut = new FileWriter(fBatchFile, false);
        BufferedWriter bwOut = new BufferedWriter(fwOut);

        try
        {
            bwOut.write("@echo off");
            bwOut.newLine();
            bwOut.newLine();
            bwOut.write("rem Batchfile for starting the processor: ");
            bwOut.write(getParameter(DN_SOAPPROCESSOR));
            bwOut.newLine();
            bwOut.write("rem === Setting the classpath");
            bwOut.newLine();
            bwOut.write("SET CLASSPATH=");
            bwOut.write(sCordysClassPath);
            bwOut.newLine();
            bwOut.newLine();

            bwOut.write("set DEBUG_PARAM=%1");
            bwOut.newLine();
            bwOut.write("if defined DEBUG_PARAM goto :StartInDebug");
            bwOut.newLine();
            bwOut.write("echo Starting " + getParameter(DN_SOAPPROCESSOR) +
                        " with the following parameters:");
            bwOut.newLine();

            String sTemp = getParameter(PROP_VM_NORMAL_ARG);

            if ((sTemp != null) && (sTemp.length() > 0))
            {
                bwOut.write("echo " + sTemp);
                bwOut.newLine();
            }

            if (sTemp == null)
            {
                sTemp = "";
            }

            bwOut.write("@java " + sTemp + " com.eibus.soap.Processor \"" +
                        getParameter(DN_SOAPPROCESSOR) + "\"");
            bwOut.newLine();
            bwOut.write("goto :EndProgram");
            bwOut.newLine();
            bwOut.newLine();

            bwOut.write(":StartInDebug");
            bwOut.newLine();
            bwOut.write("echo Starting " + getParameter(DN_SOAPPROCESSOR) +
                        " in debug mode with the following parameters:");
            bwOut.newLine();
            sTemp = getParameter(PROP_VM_DEBUG_ARG);

            if ((sTemp != null) && (sTemp.length() > 0))
            {
                bwOut.write("echo " + sTemp);
                bwOut.newLine();
            }

            if (sTemp == null)
            {
                sTemp = "";
            }

            bwOut.write("@java " + sTemp + " com.eibus.soap.Processor \"" +
                        getParameter(DN_SOAPPROCESSOR) + "\"");
            bwOut.newLine();
            bwOut.newLine();
            bwOut.write(":EndProgram");
            bwOut.newLine();

            if ("on".equals(getParameter(USE_PAUSE)))
            {
                bwOut.newLine();
                bwOut.write("pause");
                bwOut.newLine();
            }

            bwOut.flush();
        }
        finally
        {
            bwOut.close();
            fwOut.close();
        }

        System.out.println("File " + fBatchFile.getAbsolutePath() + " created.");
    }

    /**
     * This method returns the value for the given parameter.
     *
     * @param   sName  The name of the parameter.
     *
     * @return  The value for the given parameter.
     */
    public String getParameter(String sName)
    {
        String sReturn = null;

        if (hmParameters.containsKey(sName))
        {
            sReturn = hmParameters.get(sName);
        }
        else if (sName.equalsIgnoreCase(OUTPUT_FOLDER))
        {
            sReturn = System.getProperty("user.home");
        }
        else if (sName.equalsIgnoreCase(NAME_OF_BATCHFILE))
        {
            String sDN = getParameter(DN_SOAPPROCESSOR);
            sDN = sDN.substring(3, sDN.indexOf(","));
            sDN = sDN.replaceAll("\\s", "");
            sReturn = "start" + sDN + ".bat";
        }
        else if (sName.equalsIgnoreCase(USE_PAUSE))
        {
            sReturn = "on";
        }

        return sReturn;
    }

    /**
     * This method reads the propertyfile and adds the properties that are defined in the
     * propertyfile to the parameter list. The values that are passed on via the commandline are
     * leading.
     *
     * @throws  Exception  DOCUMENTME
     */
    public void readPropertyfile()
                          throws Exception
    {
        String sFilename = getParameter(PROP_FILENAME);
        InputStream isProps = null;

        if ((sFilename == null) || (sFilename.length() == 0))
        {
            // No propertyfile has been passed on, so we use the default propertyfile.
            isProps = SPBatchCreator.class.getResourceAsStream(DEFAULT_PROP_FILENAME);
        }
        else
        {
            // Try getting the inputstream based on the passed on filename.
            isProps = new FileInputStream(sFilename);
        }

        Properties pProp = new Properties();
        pProp.load(isProps);

        // Now read all the properties.
        String sValue = pProp.getProperty(PROP_VM_NORMAL_ARG);

        if (sValue == null)
        {
            sValue = "";
        }

        if (sValue.length() > 0)
        {
            addParameter(PROP_VM_NORMAL_ARG, sValue);
        }
        sValue = pProp.getProperty(PROP_VM_DEBUG_ARG);

        if (sValue == null)
        {
            sValue = "";
        }

        if (sValue.length() > 0)
        {
            addParameter(PROP_VM_DEBUG_ARG, sValue);
        }

        if (!hmParameters.containsKey(OUTPUT_FOLDER))
        {
            sValue = pProp.getProperty(PROP_OUTPUT_DIR);

            if (sValue == null)
            {
                sValue = "";
            }

            if (sValue.length() > 0)
            {
                addParameter(OUTPUT_FOLDER, sValue);
            }
        }

        if (!hmParameters.containsKey(NAME_OF_BATCHFILE))
        {
            sValue = pProp.getProperty(PROP_BATCHFILE_NAME);

            if (sValue == null)
            {
                sValue = "";
            }

            if (sValue.length() > 0)
            {
                addParameter(NAME_OF_BATCHFILE, sValue);
            }
        }

        if (!hmParameters.containsKey(USE_PAUSE))
        {
            sValue = pProp.getProperty(PROP_USE_PAUSE);

            if (sValue == null)
            {
                sValue = "";
            }

            if (sValue.length() > 0)
            {
                addParameter(USE_PAUSE, sValue);
            }
        }
    }

    /**
     * This method prints the usage of the program to stdout.
     */
    private static void printUsage()
    {
        System.out.println("Usage:");
        System.out.println("  java com.cordys.coe.util.cmdline.SPBatchCreator");
        System.out.println("           [-o <outputfolder>]		Folder in which to write the batchfile");
        System.out.println("           [-n <nameofbatchfile>]	Name of the batchfile");
        System.out.println("           [-p on | off]			Whether or not to include the pause-command (default on).");
        System.out.println("           [-f <propertyfileloc]	The location of the propertyfile. Properties defined in this one override others.");
        System.out.println("           <DN of the soapprocessor>");
        System.out.println("");
        System.out.println("Example:");
        System.out.println("  java com.cordys.coe.util.cmdline.SPBatchCreator -o c:/bin -n starttest.bat \"cn=iMaster Processor,cn=iMaster Services,cn=soap nodes,o=Zwitserleven,cn=cordys,o=vanenburg.com\"");

        System.exit(-1);
    }
}
