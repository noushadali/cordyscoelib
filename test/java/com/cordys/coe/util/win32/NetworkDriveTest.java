package com.cordys.coe.util.win32;

import com.cordys.coe.util.FileUtils;

import java.io.IOException;

import java.util.ArrayList;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import junit.framework.TestCase;

/**
 * This class tests the network drive class to make sure that it works
 * properly.
 *
 * @author pgussow
 */
public class NetworkDriveTest extends TestCase
{
    
    /**
     * @throws Exception
     *
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception
    {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.DEBUG);
    }

    /**
     * This method parses the output of the net use command to create
     * the proper wrappers around it.
     */
    public void testParseEnglishOutput()
                                throws IOException
    {
        String sOutput = getContent("english.txt");

        ArrayList<NetworkDrive> alDrives = NetworkDrive.parseNetUseOutput(sOutput);

        assertEquals("Expecting 5 drives in the english version", 5,
                     alDrives.size());
        
        System.out.println(alDrives.toString());
        
    }

    /**
     * This method parses the output of the net use command to create
     * the proper wrappers around it.
     */
    public void testParseGermanOutput()
                               throws IOException
    {
        String sOutput = getContent("german.txt");
        
        ArrayList<NetworkDrive> alDrives = NetworkDrive.parseNetUseOutput(sOutput);

        assertEquals("Expecting 7 drives in the german version", 7,
                     alDrives.size());
        
        System.out.println(alDrives.toString());
    }

    /**
     * This method reads the content of the given resource.
     *
     * @param sResourceName the name of the resource.
     *
     * @return The content of the resource.
     */
    private String getContent(String sResourceName)
                       throws IOException
    {
        return FileUtils.readTextStreamContents(NetworkDriveTest.class.getResourceAsStream(sResourceName));
    }
}
