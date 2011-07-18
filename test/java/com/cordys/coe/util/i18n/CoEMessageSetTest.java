package com.cordys.coe.util.i18n;

import com.cordys.coe.util.FileUtils;
import com.cordys.coe.util.general.Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import junit.framework.TestCase;

/**
 * This class contains the test cases for the property-based localization files.
 *
 * @author  pgussow
 */
public class CoEMessageSetTest extends TestCase
{
    /**
     * This testcase tests that the objects are created properly and that the default values are
     * returned properly.
     */
    public void testCreateMessageSet()
    {
        CoEMessageSet cms = new CoEMessageSet(CoEMessageSetTest.class, "TestMessages");

        CoEMessage cmSNI = new CoEMessage(cms, "some.nice.identifier");
        CoEMessage cmDS = new CoEMessage(cms, "different.string");
        CoEMessage cmOID = new CoEMessage(cms, "only.in.default");

        Locale lDefault = new Locale("", "", "");

        assertEquals("Invalid message for some.nice.identifier",
                     "some.nice.identifier from default.", cmSNI.getMessage(lDefault));
        assertEquals("Invalid message for different.string", "different.string from default.",
                     cmDS.getMessage(lDefault));
        assertEquals("Invalid message for only.in.default",
                     "only defined in the default file with parameter {0}.",
                     cmOID.getMessage(lDefault));

        Locale[] alLocales = cms.getAvailableLocales();
        List<Locale> lList = Arrays.asList(alLocales);

        assertEquals("Missing available locale: en", true,
                     lList.contains(new Locale("en", "", "")));
        assertEquals("Missing available locale: nl", true,
                     lList.contains(new Locale("nl", "", "")));
        assertEquals("Missing available locale: nl_BE", true,
                     lList.contains(new Locale("nl", "BE", "")));
        assertEquals("Missing available locale: nl_BE_VL", true,
                     lList.contains(new Locale("nl", "BE", "VL")));

        // Test it via the message.
        alLocales = cmSNI.getAvailableLocales();
        lList = Arrays.asList(alLocales);

        assertEquals("Missing available locale: en", true,
                     lList.contains(new Locale("en", "", "")));
        assertEquals("Missing available locale: nl", true,
                     lList.contains(new Locale("nl", "", "")));
        assertEquals("Missing available locale: nl_BE", true,
                     lList.contains(new Locale("nl", "BE", "")));
        assertEquals("Missing available locale: nl_BE_VL", true,
                     lList.contains(new Locale("nl", "BE", "VL")));

        // Test the message IDs
        String sBaseFQN = CoEMessageSetTest.class.getPackage().getName() + ".TestMessages";
        assertEquals("The FQN of the message is wrong", sBaseFQN + ":some.nice.identifier",
                     cmSNI.getFullyQualifiedResourceID());
        assertEquals("The FQN of the message is wrong", sBaseFQN + ":different.string",
                     cmDS.getFullyQualifiedResourceID());
        assertEquals("The FQN of the message is wrong", sBaseFQN + ":only.in.default",
                     cmOID.getFullyQualifiedResourceID());

        // Test the resource context.
        assertEquals("The FQN of the message context is wrong", sBaseFQN,
                     cmSNI.getResourceContext());
        assertEquals("The FQN of the message context is wrong", sBaseFQN,
                     cmDS.getResourceContext());

        // Test the message ID
        assertEquals("The ID of the message is wrong", "some.nice.identifier",
                     cmSNI.getResourceID());
        assertEquals("The ID of the message is wrong", "different.string", cmDS.getResourceID());

        // Test the message toString
        assertEquals("The FQN of the message is wrong", sBaseFQN + ":some.nice.identifier",
                     cmSNI.toString());
        assertEquals("The FQN of the message is wrong", sBaseFQN + ":different.string",
                     cmDS.toString());
        assertEquals("The FQN of the message is wrong", sBaseFQN + ":only.in.default",
                     cmOID.toString());
    }

    /**
     * This method tests the creation of the message class.
     */
    public void testMessageClassGeneration()
    {
        CoEMessageClassGenerator cmcg = new CoEMessageClassGenerator(CoEMessageSetTest.class,
                                                                     "TestMessages");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        cmcg.generateCoEJavaClass(baos,
                                  CoEMessageSetTest.class.getPackage().getName() + ".TestMessages");

        String sContent = baos.toString();

        // try { FileWriter fw = new FileWriter(new
        // File("./test/java/com/cordys/coe/util/i18n/TestMessages.java")); fw.write(sContent);
        // fw.flush(); fw.close(); } catch (IOException e1) { e1.printStackTrace(); }

        // Now read the file to make sure it's the same.
        try
        {
            String sFileContent = FileUtils.readTextFileContents(new File("./test/java/com/cordys/coe/util/i18n/TestMessages.java"));

            assertEquals("The content of the generated file does not match the reference file.",
                         sFileContent, sContent);
        }
        catch (IOException e)
        {
            fail(Util.getStackTrace(e));
        }
    }

    /**
     * This testcase tests that the objects are created properly and that the proper values are
     * returned based on a different localization class.
     */
    public void testOtherLocaleClass()
    {
        CoEMessageSet cms = new CoEMessageSet(CoEMessageSetTest.class, "TestMessages");

        CoEMessage cmSNI = new CoEMessage(cms, "some.nice.identifier");
        CoEMessage cmDS = new CoEMessage(cms, "different.string");
        CoEMessage cmOID = new CoEMessage(cms, "only.in.default");

        Locale lDefault = new Locale("en", "", "");

        assertEquals("Invalid message for some.nice.identifier",
                     "some.nice.identifier from en class", cmSNI.getMessage(lDefault));
        assertEquals("Invalid message for different.string", "different.string from en class",
                     cmDS.getMessage(lDefault));

        assertEquals("Invalid message for only.in.default",
                     "only defined in the default file with parameter {0}.",
                     cmOID.getMessage(lDefault));
    }

    /**
     * This testcase tests that the objects are created properly and that the proper values are
     * returned based on a different localization file.
     */
    public void testOtherLocaleFile()
    {
        CoEMessageSet cms = new CoEMessageSet(CoEMessageSetTest.class, "TestMessages");

        CoEMessage cmSNI = new CoEMessage(cms, "some.nice.identifier");
        CoEMessage cmDS = new CoEMessage(cms, "different.string");
        CoEMessage cmOID = new CoEMessage(cms, "only.in.default");

        Locale lDefault = new Locale("nl", "", "");

        assertEquals("Invalid message for some.nice.identifier",
                     "some.nice.identifier uit de nl file.", cmSNI.getMessage(lDefault));
        assertEquals("Invalid message for different.string", "different.string uit de nl file.",
                     cmDS.getMessage(lDefault));

        assertEquals("Invalid message for only.in.default",
                     "only defined in the default file with parameter {0}.",
                     cmOID.getMessage(lDefault));

        lDefault = new Locale("nl", "BE", "");

        assertEquals("Invalid message for some.nice.identifier",
                     "some.nice.identifier uit de nl_BE file.", cmSNI.getMessage(lDefault));
        assertEquals("Invalid message for different.string", "different.string uit de nl_BE file.",
                     cmDS.getMessage(lDefault));

        assertEquals("Invalid message for only.in.default",
                     "only defined in the default file with parameter {0}.",
                     cmOID.getMessage(lDefault));

        lDefault = new Locale("nl", "BE", "VL");

        assertEquals("Invalid message for some.nice.identifier",
                     "some.nice.identifier uit de nl_BE_VL file.", cmSNI.getMessage(lDefault));
        assertEquals("Invalid message for different.string",
                     "different.string uit de nl_BE_VL file.", cmDS.getMessage(lDefault));

        assertEquals("Invalid message for only.in.default",
                     "only defined in the default file with parameter {0}.",
                     cmOID.getMessage(lDefault));
    }

    /**
     * This method tests the generated class.
     */
    public void testWithGenerated()
    {
        Locale lDefault = new Locale("nl", "", "");

        assertEquals("Invalid message for some.nice.identifier",
                     "some.nice.identifier uit de nl file.",
                     TestMessages.SOME_NICE_IDENTIFIER.getMessage(lDefault));
        assertEquals("Invalid message for different.string", "different.string uit de nl file.",
                     TestMessages.DIFFERENT_STRING.getMessage(lDefault));

        assertEquals("Invalid message for only.in.default",
                     "only defined in the default file with parameter {0}.",
                     TestMessages.ONLY_IN_DEFAULT.getMessage(lDefault));
    }

    /**
     * This method sets up the test case.
     *
     * @throws  Exception  DOCUMENTME
     *
     * @see     junit.framework.TestCase#setUp()
     */
    @Override protected void setUp()
                            throws Exception
    {
        super.setUp();
    }
}
