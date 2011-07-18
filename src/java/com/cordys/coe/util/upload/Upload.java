package com.cordys.coe.util.upload;

import com.eibus.util.system.EIBProperties;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import sun.misc.BASE64Decoder;

/**
 * This class contains methods to upload a file to a specific location.
 *
 * @author  pgussow
 */
public class Upload
{
    /**
     * This method uploads a file to the server. The filename should be a relative filename. The
     * file will be uploaded to the Cordys install dir. If the file exists an exception will be
     * thrown.
     *
     * @param   sFileName     The name of the file to write.
     * @param   sFileContent  The Base64 encoded content of the file.
     *
     * @throws  IOException  DOCUMENTME
     */
    public static void uploadFile(String sFileName, String sFileContent)
                           throws IOException
    {
        uploadFile(sFileName, sFileContent, false);
    }

    /**
     * This method uploads a file to the server. The filename should be a relative filename. The
     * file will be uploaded to the Cordys install dir.
     *
     * @param   sFileName     The name of the file to write.
     * @param   sFileContent  The Base64 encoded content of the file.
     * @param   bOverwrite    If set to true the method will throw an exception if the destination
     *                        file already exists.
     *
     * @throws  IOException  DOCUMENTME
     */
    public static void uploadFile(String sFileName, String sFileContent, boolean bOverwrite)
                           throws IOException
    {
        FileOutputStream fosOutput = null;

        // Get the location where Cordys is installed.
        String sInstallDir = EIBProperties.getInstallDir();

        File fInstallDir = new File(sInstallDir);

        if (!fInstallDir.exists() || !fInstallDir.isDirectory())
        {
            throw new IOException("Invalid Cordys install dir (" + sInstallDir + ")");
        }

        // Now check if the file already exists. If so, throw an exception.
        File fDestination = new File(fInstallDir, sFileName);

        if (fDestination.exists() && (bOverwrite == false))
        {
            throw new IOException("The file " + fDestination.getAbsolutePath() +
                                  " already exists.");
        }

        // Decode the Base64 data.
        BASE64Decoder bdDecoder = new BASE64Decoder();
        byte[] baFileContent = bdDecoder.decodeBuffer(sFileContent);

        // Now write the file
        try
        {
            fosOutput = new FileOutputStream(fDestination);

            fosOutput.write(baFileContent);
        }
        finally
        {
            fosOutput.close();
        }
    }
}
