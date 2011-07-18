package com.cordys.coe.util.system;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.Iterator;
import java.util.Properties;
import java.util.TreeSet;

/**
 * This class returns information about the current JVM.
 *
 * @author  pgussow
 */
public class SystemInfo
{
    /**
     * This method returns the boot classpath nicely formatted.
     *
     * @return  The boot classpath.
     */
    public static String getBootClasspath()
    {
        StringWriter swWriter = new StringWriter(1024);
        PrintWriter pwOut = new PrintWriter(swWriter);

        writePath("sun.boot.class.path", System.getProperties(), "Boot classpath", pwOut);

        return swWriter.getBuffer().toString();
    }

    /**
     * This method returns the JVM specific classpath nicely formatted.
     *
     * @return  The JVM specific classpath.
     */
    public static String getJVMClasspath()
    {
        StringWriter swWriter = new StringWriter(1024);
        PrintWriter pwOut = new PrintWriter(swWriter);

        writePath("java.class.path", System.getProperties(), "JVM specific classpath", pwOut);

        return swWriter.getBuffer().toString();
    }

    /**
     * This method returns the status of the system. It returns a string that contains all the
     * settings for the current virtual machine.
     *
     * @return  A string containing the full system information.
     */
    public static String getSystemInformation()
    {
        StringWriter swWriter = new StringWriter(1024);
        PrintWriter pwOut = new PrintWriter(swWriter);

        Properties pSystemProperties = System.getProperties();

        // Output the OS information
        pwOut.println("General system information");
        pwOut.println("==========================");
        pwOut.println();
        writeProperty("os.name", pSystemProperties, "OS Name", pwOut);
        writeProperty("os.version", pSystemProperties, "OS Version", pwOut);
        writeProperty("os.arch", pSystemProperties, "OS Architecture", pwOut);
        writeProperty("sun.cpu.isalist", pSystemProperties, "CPU", pwOut);
        pwOut.println();

        pwOut.println("User information");
        pwOut.println("================");
        pwOut.println();
        writeProperty("user.name", pSystemProperties, "Username", pwOut);
        writeProperty("user.dir", pSystemProperties, "Working folder", pwOut);
        writeProperty("user.home", pSystemProperties, "Home folder", pwOut);
        writeProperty("user.language", pSystemProperties, "Language", pwOut);
        writeProperty("user.country", pSystemProperties, "Country", pwOut);
        pwOut.println();

        // Output the information about the virtual machine
        pwOut.println("Virtual machine information");
        pwOut.println("===========================");
        pwOut.println();
        writeProperty("java.version", pSystemProperties, "Version", pwOut);
        writeProperty("java.vm.vendor", pSystemProperties, "Vendor", pwOut);
        writeProperty("java.vm.info", pSystemProperties, "Run mode", pwOut);
        writeProperty("java.vm.version", pSystemProperties, "JVM Version", pwOut);

        // Write the different paths.
        writePath("sun.boot.library.path", pSystemProperties, "Boot path", pwOut);
        writePath("java.library.path", pSystemProperties, "JVM specific path", pwOut);
        writePath("sun.boot.class.path", pSystemProperties, "Boot classpath", pwOut);
        writePath("java.class.path", pSystemProperties, "JVM specific classpath", pwOut);
        pwOut.println();

        // Output all properties.
        pwOut.println("All properties (sorted by name)");
        pwOut.println("===============================");
        pwOut.println();

        TreeSet<Object> tsTemp = new TreeSet<Object>(pSystemProperties.keySet());

        for (Iterator<?> iKeys = tsTemp.iterator(); iKeys.hasNext();)
        {
            String sKey = (String) iKeys.next();
            String sValue = pSystemProperties.getProperty(sKey);
            pwOut.print(sKey);
            pwOut.print(" = ");
            pwOut.println(sValue);
        }

        return swWriter.getBuffer().toString();
    }

    /**
     * This method returns a path nicely formatted.
     *
     * @param  sPropName    The name of the system property.
     * @param  pProperties  The properties collection.
     * @param  sCaption     The caption for this path.
     * @param  pwOut        The printwriter to output it.
     */
    private static void writePath(String sPropName, Properties pProperties, String sCaption,
                                  PrintWriter pwOut)
    {
        if (pProperties.containsKey(sPropName))
        {
            String sValue = pProperties.getProperty(sPropName);

            if ((sValue != null) && (sValue.length() > 0))
            {
                pwOut.println(sCaption);

                // Seperate the entries.
                String[] saEntries = sValue.split(pProperties.getProperty("path.separator"));

                for (int iCount = 0; iCount < saEntries.length; iCount++)
                {
                    StringBuffer sbTemp = new StringBuffer(100);
                    sbTemp.append("\t");

                    if (iCount < 10)
                    {
                        sbTemp.append("0");
                    }
                    sbTemp.append(String.valueOf(iCount));
                    sbTemp.append(": ");
                    sbTemp.append(saEntries[iCount]);
                    pwOut.println(sbTemp);
                }
            }
        }
    }

    /**
     * This method writes the property to the given output writer.
     *
     * @param  sPropName    The name of the property.
     * @param  pProperties  The propertiescollection.
     * @param  sCaption     The caption.
     * @param  pwOut        The output writer.
     */
    private static void writeProperty(String sPropName, Properties pProperties, String sCaption,
                                      PrintWriter pwOut)
    {
        if (pProperties.containsKey(sPropName))
        {
            String sValue = pProperties.getProperty(sPropName);

            if ((sValue != null) && (sValue.length() > 0))
            {
                pwOut.println(sCaption + " = " + sValue);
            }
        }
    }
}
