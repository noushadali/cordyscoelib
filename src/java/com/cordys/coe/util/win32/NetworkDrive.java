package com.cordys.coe.util.win32;

import com.cordys.coe.util.cmdline.CmdLine;
import com.cordys.coe.util.cmdline.CmdLineException;

import com.eibus.util.logger.CordysLogger;

import java.util.ArrayList;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class can do drivemappings on a Win32 machine.
 *
 * @author  pgussow
 */
public class NetworkDrive
{
    /**
     * Holds the logger that is used.
     */
    private static final CordysLogger LOG = CordysLogger.getCordysLogger(NetworkDrive.class);
    /**
     * The name of the command to execute.
     */
    private static final String CMD_NAME = "net";
    /**
     * Holds the driveletter to use.
     */
    private String sDriveLetter;
    /**
     * Holds the name of the share.
     */
    private String sLocation;
    /**
     * Holds the password to use.
     */
    private String sPassword;
    /**
     * Holds the username to use.
     */
    private String sUsername;

    /**
     * Constructor.
     *
     * @param  sLocation  The name of the share.
     */
    public NetworkDrive(String sLocation)
    {
        this(sLocation, null, null, null);
    }

    /**
     * Constructor.
     *
     * @param  sLocation     The name of the share.
     * @param  sDriveLetter  The driveletter to use.
     */
    public NetworkDrive(String sLocation, String sDriveLetter)
    {
        this(sLocation, sDriveLetter, null, null);
    }

    /**
     * Constructor.
     *
     * @param  sLocation     The name of the share.
     * @param  sDriveLetter  The driveletter to use.
     * @param  sUsername     The username to use.
     * @param  sPassword     The password to use.
     */
    public NetworkDrive(String sLocation, String sDriveLetter, String sUsername, String sPassword)
    {
        if ((sDriveLetter != null) && (sDriveLetter.trim().length() > 0))
        {
            validateDriveLetter(sDriveLetter);

            this.sDriveLetter = sDriveLetter;
        }

        this.sLocation = sLocation;
        this.sUsername = sUsername;
        this.sPassword = sPassword;
    }

    /**
     * Get the network drive mapping for a driveletter.
     *
     * @param   driveLetter  The driveletter to use.
     *
     * @return  An <code>NetworkDrive</code> object.
     *
     * @throws  CmdLineException  DOCUMENTME
     */
    public static NetworkDrive getNetworkDriveMapping(String driveLetter)
                                               throws CmdLineException
    {
        validateDriveLetter(driveLetter);

        NetworkDrive[] networkDriveArr = getNetworkMappings();

        for (int i = 0; i < networkDriveArr.length; i++)
        {
            NetworkDrive nd = (NetworkDrive) networkDriveArr[i];

            if ((nd.getDriveLetter() != null) &&
                    nd.getDriveLetter().equals(driveLetter.toUpperCase()))
            {
                return nd;
            }
        }
        return null;
    }

    /**
     * This method retrieves all mapped network drives.
     *
     * @return  An array of <code>NetworkDrive</code> objects.
     *
     * @throws  CmdLineException  DOCUMENTME
     */
    public static NetworkDrive[] getNetworkMappings()
                                             throws CmdLineException
    {
        ArrayList<NetworkDrive> networkDrives = new ArrayList<NetworkDrive>();

        CmdLine clCMD = new CmdLine(CMD_NAME);
        clCMD.addArgument("use");

        int iReturn = clCMD.execute();

        if (iReturn != 0)
        {
            throw new CmdLineException("Error executing mapping (" + iReturn + ")\nStdOut:\n" +
                                       clCMD.getStdOut() + "\nStdErr:\n" + clCMD.getStdErr());
        }

        String sStdOut = clCMD.getStdOut();

        if (LOG.isDebugEnabled())
        {
            LOG.debug("The current drive mappings:\n" + sStdOut);
        }

        networkDrives = parseNetUseOutput(sStdOut);

        return networkDrives.toArray(new NetworkDrive[networkDrives.size()]);
    }

    /**
     * This method creates the drive mapping.
     *
     * @throws  CmdLineException  DOCUMENTME
     */
    public void create()
                throws CmdLineException
    {
        CmdLine clCMD = new CmdLine(CMD_NAME);
        clCMD.addArgument("use");

        if ((sDriveLetter != null) && (sDriveLetter.length() > 0))
        {
            clCMD.addArgument(sDriveLetter);
        }
        clCMD.addArgument(sLocation);

        if ((sUsername != null) && (sUsername.length() > 0))
        {
            clCMD.addArgument("/USER:" + sUsername);
        }

        if ((sPassword != null) && (sPassword.length() > 0))
        {
            clCMD.addArgument(sPassword);
        }

        int iReturn = clCMD.execute();

        if (iReturn != 0)
        {
            throw new CmdLineException("Error executing mapping (" + iReturn + ")\nStdOut:\n" +
                                       clCMD.getStdOut() + "\nStdErr:\n" + clCMD.getStdErr());
        }
    }
    
    /**
     * This method deletes all drive mappings.
     *
     * @throws  CmdLineException  A CmdLineException
     */
    public static void deleteAll()
                throws CmdLineException
    {
        CmdLine clCMD = new CmdLine(CMD_NAME);
        clCMD.addArgument("use");
        clCMD.addArgument("*");
        clCMD.addArgument("/DELETE");
        clCMD.addArgument("/yes");

        int iReturn = clCMD.execute();

        if (iReturn != 0)
        {
            throw new CmdLineException("Error deleting all drive mappings  (" + iReturn +
                                       ")\nStdOut:\n" + clCMD.getStdOut() + "\nStdErr:\n" +
                                       clCMD.getStdErr());
        }
    }


    /**
     * This method deletes the drive mapping. It is deleted by location.
     *
     * @throws  CmdLineException  DOCUMENTME
     */
    public void delete()
                throws CmdLineException
    {
        CmdLine clCMD = new CmdLine(CMD_NAME);
        clCMD.addArgument("use");
        clCMD.addArgument(sLocation);
        clCMD.addArgument("/DELETE");
        clCMD.addArgument("/yes");

        int iReturn = clCMD.execute();

        if (iReturn != 0)
        {
            throw new CmdLineException("Error deleting the mapping by location (" + iReturn +
                                       ")\nStdOut:\n" + clCMD.getStdOut() + "\nStdErr:\n" +
                                       clCMD.getStdErr());
        }
    }

    /**
     * This method deletes the drive mapping. It is deleted by drive letter.
     *
     * @throws  CmdLineException  DOCUMENTME
     */
    public void deleteDriveLetter()
                           throws CmdLineException
    {
        CmdLine clCMD = new CmdLine(CMD_NAME);
        clCMD.addArgument("use");
        clCMD.addArgument(sDriveLetter);
        clCMD.addArgument("/DELETE");
        clCMD.addArgument("/yes");

        int iReturn = clCMD.execute();

        if (iReturn != 0)
        {
            throw new CmdLineException("Error deleting the mapping by driveletter (" + iReturn +
                                       ")\nStdOut:\n" + clCMD.getStdOut() + "\nStdErr:\n" +
                                       clCMD.getStdErr());
        }
    }

    /**
     * This method gets the driveletter to use.
     *
     * @return  The driveletter to use.
     */
    public String getDriveLetter()
    {
        return sDriveLetter;
    }

    /**
     * This method gets the name of the share.
     *
     * @return  The name of the share.
     */
    public String getLocation()
    {
        return sLocation;
    }

    /**
     * This method gets the password to use.
     *
     * @return  The password to use.
     */
    public String getPassword()
    {
        return sPassword;
    }

    /**
     * This method gets the username to use.
     *
     * @return  The username to use.
     */
    public String getUsername()
    {
        return sUsername;
    }

    /**
     * This method will recreate the network mapping. It will do the following:<br>
     * 1. Check if the current drive is already mapped.<br>
     * 2. If so, delete the mapping with the drive letter that is needed.<br>
     * 3. Delete all mappings for the current location (to prevent username clashes)<br>
     * 4. Create the mapping again.
     *
     * @throws  CmdLineException  DOCUMENTME
     */
    public void recreate()
                  throws CmdLineException
    {
        // First we need to check if the location has been mapped without a drive letter.
        ArrayList<NetworkDrive> alToBeDeleted = new ArrayList<NetworkDrive>();

        NetworkDrive[] andMappings = getNetworkMappings();

        for (NetworkDrive ndDrive : andMappings)
        {
            if ((getLocation() != null) && (getLocation().length() > 0))
            {
                if (getLocation().equalsIgnoreCase(ndDrive.getLocation()) ||
                        ((getDriveLetter() != null) && (getDriveLetter().length() > 0) &&
                             getDriveLetter().equals(ndDrive.getDriveLetter())))
                {
                    alToBeDeleted.add(ndDrive);
                }
            }
        }

        for (NetworkDrive ndDrive : alToBeDeleted)
        {
            if ((ndDrive.getDriveLetter() != null) && (ndDrive.getDriveLetter().length() > 0))
            {
                ndDrive.deleteDriveLetter();
            }
            else
            {
                ndDrive.delete();
            }
        }

        // Now create the mapping again.
        create();
    }

    /**
     * Returns a string representation of the object.
     *
     * @return  A string representation of the object.
     */
    @Override public String toString()
    {
        StringBuffer sbBuffer = new StringBuffer();
        sbBuffer.append("DriveLetter: ");
        sbBuffer.append("'" + sDriveLetter + "'");
        sbBuffer.append("\n");
        sbBuffer.append("Location: ");
        sbBuffer.append(sLocation);
        sbBuffer.append("\n");
        sbBuffer.append("Username: ");
        sbBuffer.append(sUsername);
        sbBuffer.append("\n");
        sbBuffer.append("Password: ");
        sbBuffer.append(sPassword);
        sbBuffer.append("\n\n");
        return sbBuffer.toString();
    }

    /**
     * This method parses the output of the 'net use' command. This method is protected so that it
     * can be tested in a JUnit testcase.
     *
     * @param   sOutput  The output of the net use command.
     *
     * @return  The list of currently mapped network drives.
     */
    protected static ArrayList<NetworkDrive> parseNetUseOutput(String sOutput)
    {
        ArrayList<NetworkDrive> alReturn = new ArrayList<NetworkDrive>();

        String[] asLines = sOutput.split("[\r]{0,1}\n");

        if ((asLines != null) && (asLines.length > 5))
        {
            // We have to try to be language independent. We'll first try to get the indices for
            // each column. We can do that based on the 4th line. At max we have 4 columns: Status,
            // Local, Remote and Network
            int[] aiColumns = new int[4];
            aiColumns[0] = 0;

            String sColumnLine = asLines[3];

            if ((sColumnLine != null) && (sColumnLine.length() > 0))
            {
                Matcher mMatcher = Pattern.compile("[\\w]+").matcher(sColumnLine);
                int iCount = 0;

                while (mMatcher.find() && (iCount < 4))
                {
                    // Found a column
                    aiColumns[iCount++] = mMatcher.start();
                }

                if (LOG.isDebugEnabled())
                {
                    StringBuffer sbTemp = new StringBuffer(512);

                    for (int iTmp = 0; iTmp < aiColumns.length; iTmp++)
                    {
                        sbTemp.append("Column " + (iTmp + 1) + ": " + aiColumns[iTmp] + "\n");
                    }

                    LOG.debug("Found columns:\n" + sbTemp.toString());
                }
            }
            else
            {
                // For some reason the header was not found. We'll use the default values
                aiColumns[0] = 0;
                aiColumns[1] = 13;
                aiColumns[2] = 23;
                aiColumns[3] = 49;

                if (LOG.isDebugEnabled())
                {
                    LOG.debug("Couldn't find the column line, so using the default values: 0, 13, 23 and 49");
                }
            }

            // The last 3 lines do not contain mappings.
            // Determine the amount of lines at the end to skip.
            int iLinesToRemove = 1;
            int iCurrentIndex = asLines.length - 1;

            while ((asLines[iCurrentIndex] == null) || (asLines[iCurrentIndex].length() == 0))
            {
                iCurrentIndex--;
                iLinesToRemove++;
            }

            int iLastLine = asLines.length - iLinesToRemove;

            for (int iCount = 6; iCount < iLastLine; iCount++)
            {
                String sLine = asLines[iCount];

                // We need to build up the line with proper seperators. It is possible that an entry
                // is split over 2 lines We'll determine whether or not this sline is multi-line by
                // already looking at the next line
                boolean bMultiLine = false;

                if (sLine.endsWith(" ") && (iCount < (iLastLine - 1)) &&
                        asLines[iCount + 1].startsWith(" "))
                {
                    // It is definately a multiline
                    bMultiLine = true;
                }

                // First we'll build up the string with a proper seperator, so that we can handle
                // multi-line and single-line the same way.
                sLine = sLine.substring(0, aiColumns[1] - 1) + "|" + sLine.substring(aiColumns[1]);
                sLine = sLine.substring(0, aiColumns[2] - 1) + "|" + sLine.substring(aiColumns[2]);

                if (bMultiLine == true)
                {
                    // Increase iCount already to make sure the line is not parsed as an actual
                    // line.
                    sLine += ("|" + asLines[++iCount].trim());
                }
                else
                {
                    sLine = sLine.substring(0, aiColumns[3] - 1) + "|" +
                            sLine.substring(0, aiColumns[3]);
                }

                // 0: Status
                // 1: Drive letter
                // 2: Share name (Location)
                // 3: Network name.
                String[] asEntries = sLine.split("\\|");

                String sDriveLetter = asEntries[1].trim();
                String sLocation = asEntries[2].trim();

                NetworkDrive ndDrive = new NetworkDrive(sLocation, sDriveLetter);
                alReturn.add(ndDrive);
            }
        }

        return alReturn;
    }

    /**
     * This method validates if the driveletter is correct.
     *
     * @param  sDriveLetter  The drive letter to check.
     */
    private static void validateDriveLetter(String sDriveLetter)
    {
        if (sDriveLetter == null)
        {
            return;
        }

        if ((sDriveLetter.length() == 0) || (!sDriveLetter.matches("[A-Z]:")))
        {
            throw new RuntimeException("Drive " + sDriveLetter + " is not a valid drive.");
        }
    }
}
