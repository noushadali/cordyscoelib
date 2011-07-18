package com.cordys.coe.util.i18n;

import com.cordys.coe.util.xml.dom.XMLHelper;
import com.cordys.coe.util.xml.dom.XPathHelper;

import com.eibus.localization.message.Message;
import com.eibus.localization.message.MessageSet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * This class can generate a new class containing the definition of the static message definitions
 * so that they can be used in applications.
 *
 * @author  pgussow
 */
public class CoEMessageClassGenerator
{
    /**
     * DOCUMENTME.
     */
    private String[] m_asLocales;
    /**
     * Holds the corresponding CoEMessageSet.
     */
    private CoEMessageSet m_cmsSet;
    /**
     * Holds the resource bundle to use.
     */
    private ResourceBundle m_rbBundle;
    /**
     * Holds the error messages that occurred.
     */
    private String m_sError;
    /**
     * Holds the name of the message set.
     */
    private String m_sName;
    /**
     * Holds the package of the message bundle.
     */
    private String m_sPackage;

    /**
     * Creates a new CoEMessageClassGenerator object.
     *
     * @param  cBase  The base class.
     * @param  sName  the name of the resource bundle.
     */
    public CoEMessageClassGenerator(Class<?> cBase, String sName)
    {
        this(cBase.getPackage().getName(), sName);
    }

    /**
     * Creates a new CoEMessageClassGenerator object.
     *
     * @param  sPackage  The package name.
     * @param  sName     The name od the resource bundle.
     */
    public CoEMessageClassGenerator(String sPackage, String sName)
    {
        m_sPackage = sPackage;
        m_sName = sName;

        m_cmsSet = new CoEMessageSet(m_sPackage, sName);

        m_rbBundle = ResourceBundle.getBundle(m_cmsSet.getFullyQualifiedName(),
                                              new Locale("", "", ""));
    }

    /**
     * Creates a new CoEMessageClassGenerator object. This constructor should be used for generating
     * a JS file.
     *
     * @param   fXMLFile   The file to load.
     * @param   sID        The ID of the message bundle.
     * @param   sBaseURL   The base web URL to load it from.
     * @param   asLocales  The list of locales to preload.
     *
     * @throws  Exception  In case of any exceptions.
     */
    public CoEMessageClassGenerator(File fXMLFile, String sID, String sBaseURL, String[] asLocales)
                             throws Exception
    {
        m_sPackage = sBaseURL;
        m_sName = sID;
        m_asLocales = asLocales;

        // Load the XML file and create the bundle.
        Properties p = new Properties();
        Document dDoc = XMLHelper.loadXMLFile(fXMLFile.getAbsolutePath());
        m_sName = dDoc.getDocumentElement().getAttribute("id");

        NodeList nl = XPathHelper.selectNodeList(dDoc.getDocumentElement(), "Message");

        for (int iCount = 0; iCount < nl.getLength(); iCount++)
        {
            String sMsgID = XPathHelper.getStringValue(nl.item(iCount), "@id");
            String sMessage = XPathHelper.getStringValue(nl.item(iCount), "MessageText/text()");
            p.setProperty(sMsgID, sMessage);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        p.store(baos, "No comment");

        m_rbBundle = new PropertyResourceBundle(new ByteArrayInputStream(baos.toByteArray()));
    }

    /**
     * Main method.
     *
     * @param  saArguments  The commandline arguments.
     */
    public static void main(String[] saArguments)
    {
        try
        {
            if (saArguments.length == 3)
            {
                CoEMessageClassGenerator cmcg = new CoEMessageClassGenerator(saArguments[0],
                                                                             saArguments[1]);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                if (cmcg.generateStandardJavaClass(baos, saArguments[0] + "." + saArguments[1]))
                {
                    System.out.println("Success!!");
                    System.out.println(baos.toString());
                }
                else
                {
                    System.out.println("Failure!!");
                    System.out.println(cmcg.getErrorMessages());
                }
            }
            else
            {
                System.out.println("Usage:");
                System.out.println("\t" + CoEMessageClassGenerator.class.getName() +
                                   " <package> <resource_name>");
                System.exit(1);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * This method will generate the actual class using the CoEMessage and CoEMessageSet classes.
     *
     * @param   osStream  The stream to write the class to.
     * @param   sFQCN     The fully qualified name for the class that is to be generated. For
     *                    example: com.cordys.coe.test.i18n.TestMessages.
     *
     * @return  true if everything went ok and false if there were errors.
     */
    public boolean generateCoEJavaClass(OutputStream osStream, String sFQCN)
    {
        return generateJavaClass(osStream, sFQCN, CoEMessageSet.class, CoEMessage.class);
    }

    /**
     * This method will generate the actual class.
     *
     * @param   sFQCN          The fully qualified name for the class that is to be generated. For
     *                         example: com.cordys.coe.test.i18n.TestMessages.
     * @param   fSourceFolder  The folder which is the root for the sources.
     *
     * @return  true if everything went ok and false if there were errors.
     *
     * @throws  IOException  in case of any errors.
     */
    public boolean generateCoEJavaClass(String sFQCN, File fSourceFolder)
                                 throws IOException
    {
        boolean bReturn = true;

        String sRealFilename = sFQCN.replace('.', '/') + ".java";

        File fRealFile = new File(fSourceFolder, "./" + sRealFilename);

        FileOutputStream fos = new FileOutputStream(fRealFile, false);

        try
        {
            bReturn = generateCoEJavaClass(fos, sFQCN);

            fos.flush();
        }
        finally
        {
            fos.close();
        }

        return bReturn;
    }

    /**
     * This method will generate the Javascript wrapper for the message bundle.
     *
     * @param   osStream  The stream to write the class to.
     *
     * @return  true if everything went ok and false if there were errors.
     */
    public boolean generateJavaScript(OutputStream osStream)
    {
        PrintStream psOut = new PrintStream(osStream);
        ByteArrayOutputStream baosError = new ByteArrayOutputStream();
        PrintStream psError = new PrintStream(baosError);

        String sMSName = "MS_" + m_sName.replaceAll("[^a-zA-Z0-9]+", "_").toUpperCase();

        // Add the header.
        psOut.print("/**\n");
        psOut.print(" * This file contains the messages for the message bundle with id:\n");
        psOut.print(" * " + m_sName + "\n");
        psOut.print(" */\n\n");

        // Do the message set
        psOut.print("/**\n");
        psOut.print(" * Holds the definition of the message set.\n");
        psOut.print(" */\n");
        psOut.print("var " + sMSName + " = new MessageSet('" + m_sName + "', '" + m_sPackage + "'");

        if ((m_asLocales != null) && (m_asLocales.length > 0))
        {
            for (String sLocale : m_asLocales)
            {
                psOut.print(", '" + sLocale + "'");
            }
        }
        psOut.print(");\n");

        Enumeration<String> eKeys = m_rbBundle.getKeys();

        ArrayList<String> alMessageIDs = new ArrayList<String>();

        // For every message write the JS definition. The message is the comment.
        boolean bOk = true;

        while (eKeys.hasMoreElements())
        {
            String sMsgId = eKeys.nextElement();

            String sMessageText = m_rbBundle.getString(sMsgId);

            if ((sMsgId == null) || (sMessageText == null))
            {
                reportError(1, psError, null);
                bOk = false;
                break;
            }

            String sJavaStdMsgId = convertToJavaConvention(sMsgId);

            // Stop if any duplicates are found
            if (alMessageIDs.contains(sMsgId))
            {
                reportError(2, psError, sMsgId);
                bOk = false;
                break;
            }
            alMessageIDs.add(sMsgId);

            // Convert the message text to proper javadoc. The message text could be CData
            // containing newlines.
            String sJavadoc = sMessageText;
            String[] asLines = sJavadoc.split("(\\n|\\r\\n)");
            psOut.print("/**\n");
            psOut.print(" * Holds the definition of the message with ID " + sMsgId + ".\n");
            psOut.print(" * Message text:\n");

            for (int iCount = 0; iCount < asLines.length; iCount++)
            {
                psOut.print(" * ");
                psOut.print(asLines[iCount]);
                psOut.print("\n");
            }
            psOut.print(" */\n");

            psOut.print("var " + sJavaStdMsgId + " = " + sMSName + ".getMessage('" + sMsgId +
                        "');\n");
        }

        m_sError = baosError.toString();

        return bOk;
    }

    /**
     * This method will generate the actual class.
     *
     * @param   sFQCN          The fully qualified name for the class that is to be generated. For
     *                         example: com.cordys.coe.test.i18n.TestMessages.
     * @param   fSourceFolder  The folder which is the root for the sources.
     *
     * @return  true if everything went ok and false if there were errors.
     *
     * @throws  IOException  in case of any errors.
     */
    public boolean generateJavaScript(String sFQCN, File fSourceFolder)
                               throws IOException
    {
        boolean bReturn = true;

        String sRealFilename = sFQCN + ".js";

        File fRealFile = new File(fSourceFolder, "./" + sRealFilename);

        FileOutputStream fos = new FileOutputStream(fRealFile, false);

        try
        {
            bReturn = generateJavaScript(fos);

            fos.flush();
        }
        finally
        {
            fos.close();
        }

        return bReturn;
    }

    /**
     * This method will generate the actual class using the standard Cordys Message and MessageSet
     * classes.
     *
     * @param   osStream  The stream to write the class to.
     * @param   sFQCN     The fully qualified name for the class that is to be generated. For
     *                    example: com.cordys.coe.test.i18n.TestMessages.
     *
     * @return  true if everything went ok and false if there were errors.
     */
    public boolean generateStandardJavaClass(OutputStream osStream, String sFQCN)
    {
        return generateJavaClass(osStream, sFQCN, MessageSet.class, Message.class);
    }

    /**
     * This method gets the error messages.
     *
     * @return  The error messages.
     */
    public String getErrorMessages()
    {
        return m_sError;
    }

    /**
     * converts the input id to Java convention for constants.
     *
     * @param   sMessageID  Message id to be converted
     *
     * @return  A proper Java identifier.
     */
    private static String convertToJavaConvention(String sMessageID)
    {
        char[] cArray = sMessageID.toCharArray();
        StringBuffer sConverted = new StringBuffer();

        // First we need to replace the ID to make sure we get a valid java identifier.
        sMessageID = sMessageID.replaceAll("[^a-zA-Z0-9]+", "_");

        if (sMessageID.equals(sMessageID.toLowerCase()) ||
                sMessageID.equals(sMessageID.toUpperCase()))
        {
            sConverted.append(sMessageID);
        }
        else
        {
            sConverted.append(cArray[0]);

            for (int i = 1; i < cArray.length; i++)
            {
                String toAppend = "" + cArray[i];

                if (Character.isUpperCase(cArray[i]) && !Character.isUpperCase(cArray[i - 1]) &&
                        (cArray[i - 1] != '_') && (cArray[i - 1] != ' '))
                {
                    toAppend = "_" + cArray[i];
                }
                sConverted.append(toAppend);
            }
        }
        return sConverted.toString().toUpperCase();
    }

    /**
     * This method reports and error to the error stream.
     *
     * @param  iErrorCode  The error code.
     * @param  psError     The error stream.
     * @param  sDetail     Optional detail message.
     */
    private static void reportError(int iErrorCode, PrintStream psError, String sDetail)
    {
        // 0: No resource name
        // 1: Message id or message text is null
        // 2: duplicate message id
        String errorMessage;

        switch (iErrorCode)
        {
            case 0:
                errorMessage = "Message bundle id not specified.";
                break;

            case 1:
                errorMessage = "Message id or message text not specified.";
                break;

            case 2:
                errorMessage = "Duplicate message id found for";
                break;

            default:
                errorMessage = "unknown error";
        }
        psError.println(errorMessage);

        if (sDetail != null)
        {
            psError.println("Detail:" + sDetail);
        }
    }

    /**
     * This method will generate the actual class using the CoEMessage and CoEMessageSet classes.
     *
     * @param   osStream     The stream to write the class to.
     * @param   sFQCN        The fully qualified name for the class that is to be generated. For
     *                       example: com.cordys.coe.test.i18n.TestMessages.
     * @param   cMessageSet  The class to use for the message set.
     * @param   cMessage     The class to use for the messages.
     *
     * @return  true if everything went ok and false if there were errors.
     */
    private boolean generateJavaClass(OutputStream osStream, String sFQCN, Class<?> cMessageSet,
                                      Class<?> cMessage)
    {
        PrintStream psOut = new PrintStream(osStream);
        ByteArrayOutputStream baosError = new ByteArrayOutputStream();
        PrintStream psError = new PrintStream(baosError);

        // Figure out the package name
        String sPackageName = "";
        int iDelimiter = sFQCN.lastIndexOf(".");

        if (iDelimiter > 0)
        {
            sPackageName = sFQCN.substring(0, sFQCN.lastIndexOf("."));
        }

        String sClassName = sFQCN.substring(sFQCN.lastIndexOf(".") + 1);

        // First we generate the header
        if (sPackageName.trim().length() > 0)
        {
            psOut.print("package " + sPackageName + ";\n");
            psOut.print("\n");
        }

        // Now do the imports
        psOut.print("import " + cMessage.getName() + ";\n");
        psOut.print("import " + cMessageSet.getName() + ";\n");
        psOut.print("\n");

        // Add some comments
        psOut.print("/**\n");
        psOut.print(" * This code is generated by running " +
                    CoEMessageClassGenerator.class.getName() + ".\n");
        psOut.print(" */\n");

        // Start of class
        psOut.print("public class " + sClassName + "\n{\n");

        psOut.print("\t/**\n");
        psOut.print("\t * Holds the definition of the CoE message set.\n");
        psOut.print("\t */\n");
        psOut.print("\tpublic static final " + cMessage.getSimpleName() + " MESSAGE_SET = new " +
                    cMessageSet.getSimpleName() + "(\"" + m_sPackage + "\", \"" + m_sName +
                    "\");\n\n");

        Enumeration<String> eKeys = m_rbBundle.getKeys();

        ArrayList<String> alMessageIDs = new ArrayList<String>();

        // For every message write the default message as comment and MessageIds as public static
        // final CoEMessage Message_id = new CoEMessage(MESSAGE_SET, Message_id);
        boolean bOk = true;

        while (eKeys.hasMoreElements())
        {
            String sMsgId = eKeys.nextElement();

            String sMessageText = m_rbBundle.getString(sMsgId);

            if ((sMsgId == null) || (sMessageText == null))
            {
                reportError(1, psError, null);
                bOk = false;
                break;
            }

            String sJavaStdMsgId = convertToJavaConvention(sMsgId);

            // Stop if any duplicates are found
            if (alMessageIDs.contains(sMsgId))
            {
                reportError(2, psError, sMsgId);
                bOk = false;
                break;
            }
            alMessageIDs.add(sMsgId);

            // Convert the message text to proper javadoc. The message text could be CData
            // containing newlines.
            String sJavadoc = sMessageText;
            String[] asLines = sJavadoc.split("(\\n|\\r\\n)");
            psOut.print("\t/**\n");
            psOut.print("\t * Holds the definition of the message with ID " + sMsgId + ".\n");
            psOut.print("\t * Message text:\n");

            for (int iCount = 0; iCount < asLines.length; iCount++)
            {
                psOut.print("\t * ");
                psOut.print(asLines[iCount]);
                psOut.print("\n");
            }
            psOut.print("\t */\n");

            psOut.print("\tpublic static final " + cMessage.getSimpleName() + " " + sJavaStdMsgId +
                        " = new " + cMessageSet.getSimpleName() + "(MESSAGE_SET, \"" + sMsgId +
                        "\");\n");
        }

        psOut.print("\n}");

        m_sError = baosError.toString();

        return bOk;
    }
}
