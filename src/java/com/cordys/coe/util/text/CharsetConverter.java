/**
 * © 2005 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util.text;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import java.nio.charset.Charset;

import java.util.Iterator;

/**
 * Simple utility to convert text to one character set to another.
 *
 * @author  mpoyhone
 */
public class CharsetConverter
{
    /**
     * Main method.
     *
     * @param  args  Arguments.
     */
    public static void main(String[] args)
    {
        if (args.length < 2)
        {
            printUsage();
            return;
        }

        String sInputCharsetName = args[0];
        String sOutputCharsetName = args[1];
        Charset csInput;
        Charset csOutput;

        try
        {
            csInput = Charset.forName(sInputCharsetName);
            csOutput = Charset.forName(sOutputCharsetName);
        }
        catch (Exception e)
        {
            System.out.println(e);
            return;
        }

        Reader rInput = null;
        Writer wOutput = null;
        boolean bCloseInput = false;
        boolean bCloseOutput = false;

        try
        {
            InputStream isInputStream = null;
            OutputStream osOutputStream = null;

            if (args.length >= 3)
            {
                isInputStream = new FileInputStream(args[2]);
                bCloseInput = true;
            }
            else
            {
                isInputStream = System.in;
            }

            if (args.length >= 4)
            {
                osOutputStream = new FileOutputStream(args[3]);
                bCloseOutput = true;
            }
            else
            {
                osOutputStream = System.out;
            }

            rInput = new InputStreamReader(isInputStream, csInput);
            wOutput = new OutputStreamWriter(osOutputStream, csOutput);

            char[] caBuffer = new char[32000];
            int iRead;

            while ((iRead = rInput.read(caBuffer)) >= 0)
            {
                wOutput.write(caBuffer, 0, iRead);
            }

            wOutput.flush();
        }
        catch (Exception e)
        {
            System.out.println("Unable to convert. Exception : e" + e);
        }
        finally
        {
            if (bCloseInput)
            {
                try
                {
                    rInput.close();
                }
                catch (IOException ignored)
                {
                }
            }

            if (bCloseOutput)
            {
                try
                {
                    wOutput.close();
                }
                catch (IOException ignored)
                {
                }
            }
        }
    }

    /**
     * Prints command line arguments.
     */
    private static void printUsage()
    {
        System.out.println("Usage <input charset> <output charset> [<input file>] [<output file>]");
        System.out.println();
        System.out.println("If input file is not given, the input is read from standard input.");
        System.out.println("If output file is not given, the output is written to standard output.");
        System.out.println();
        System.out.println("Available character sets :");

        for (Iterator<String> iIter = Charset.availableCharsets().keySet().iterator();
                 iIter.hasNext();)
        {
            String sName = (String) iIter.next();

            System.out.println("\t" + sName);
        }
    }
}
