/**
 *  2003 Cordys R&D B.V. All rights reserved.     The computer program(s) is
 * the proprietary information of Cordys R&D B.V.     and provided under the
 * relevant License Agreement containing restrictions     on use and
 * disclosure. Use is subject to the License Agreement.
 */
package com.cordys.coe.util.general;

import com.eibus.xml.nom.Node;

import com.novell.ldap.util.DN;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;

/**
 * This class contains all sorts of general utilities.
 */
public class Util
{
    /**
     * Indicates wether or not debugging is enabled.
     */
    public static boolean DEBUG = false;

    static
    {
        if (System.getProperties().containsKey("coelib.debug"))
        {
            DEBUG = true;
        }
    }

    /**
     * Appends the exception stack trace to the XML node.
     *
     * @param  t            Exception to be added.
     * @param  detailsNode  NOM XML node under which the create elements will be added.
     */
    public static void appendExceptionToXml(Throwable t, int detailsNode)
    {
        int stackTraceNode = Node.createElement("stacktrace", detailsNode);

        List<Throwable> exceptionList = new ArrayList<Throwable>(20);
        Throwable tmp = t;

        while (tmp != null)
        {
            exceptionList.add(tmp);
            tmp = tmp.getCause();
        }

        for (Throwable ex : exceptionList)
        {
            int exceptionNode = Node.createElement("exception", stackTraceNode);

            Node.setAttribute(exceptionNode, "class", ex.getClass().getName());
            Node.setAttribute(exceptionNode, "message", ex.getMessage());

            StackTraceElement[] elements = ex.getStackTrace();

            for (StackTraceElement element : elements)
            {
                int methodNode = Node.createElement("method", exceptionNode);
                String methodName = element.getMethodName();
                int lineNumber = elements[0].getLineNumber();
                String fileName = element.getFileName();

                Node.setAttribute(methodNode, "class", element.getClassName());
                Node.setAttribute(methodNode, "name",
                                  (methodName != null) ? methodName : "unknown");
                Node.setAttribute(methodNode, "file", (fileName != null) ? fileName : "unknown");
                Node.setAttribute(methodNode, "line",
                                  (lineNumber > 0) ? Integer.toString(lineNumber) : "unknown");
            }
        }
    }

    /**
     * This method returns the organization the passed on user is in.
     *
     * @param   sOrgUserDN  The DN of the organizational user.
     *
     * @return  The DN of the organization.
     */
    public static String getOrganizationFromUser(String sOrgUserDN)
    {
        String sReturn = null;

        if ((sOrgUserDN != null) && (sOrgUserDN.length() > 0))
        {
            sReturn = sOrgUserDN;

            DN dn = new DN(sOrgUserDN);
            sReturn = dn.getParent().getParent().toString();
        }

        return sReturn;
    }

    /**
     * This method returns the string-representation of the stacktrace of the passed on exception.
     *
     * @param   tException  The exception to get the stacktrace of.
     *
     * @return  The string-representation of the stacktrace.
     */
    public static String getStackTrace(Throwable tException)
    {
        // Get the stack-trace
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tException.printStackTrace(pw);
        pw.flush();

        return sw.getBuffer().toString();
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

        for (Iterator<Object> iKeys = tsTemp.iterator(); iKeys.hasNext();)
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
     * This method fills up the string sSource with cChar until the string has the length of
     * iLength. The characters are
     *
     * @param   sSource  The source string to which the character is to be added.
     * @param   cChar    The char to be added.
     * @param   iLength  The total length of the string.
     *
     * @return  The string with the character padded to the total length of iLength.
     */
    public static String padLeft(String sSource, String cChar, int iLength)
    {
        String sReturn = sSource;

        if (sReturn == null)
        {
            sReturn = "";
        }

        String sChararacter = cChar;

        if (sChararacter == null)
        {
            sChararacter = " ";
        }

        while (sReturn.length() < iLength)
        {
            sReturn = sChararacter + sReturn;
        }
        return sReturn;
    }

    /**
     * This method gets the user for the passed on request.
     *
     * @param   sSource  The source string to which the character is to be added.
     * @param   cChar    The char to be added.
     * @param   iLength  The total length of the string.
     *
     * @return  The string with the character padded to the total length of iLength.
     */
    public static String padRight(String sSource, String cChar, int iLength)
    {
        StringBuffer sbBuffer = new StringBuffer();

        if (sSource != null)
        {
            sbBuffer.append(sSource);
        }

        String sChararacter = cChar;

        if (sChararacter == null)
        {
            sChararacter = " ";
        }

        while (sbBuffer.length() < iLength)
        {
            sbBuffer.append(sChararacter);
        }
        return sbBuffer.toString();
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
