/**
 * (c) 2007 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util.test.junit;

import com.cordys.coe.util.FileUtils;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Node;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

/**
 * Helper base class for JUnit tests that require NOM nodes. Helps with keeping track of the allocated nodes. This class
 * also automatically checks for memory leaks and enables NOM exceptions during testing.
 *
 * @author  mpoyhone
 */
public class NomTestCase extends TestCase
{
    /**
     * NOM document for all XML nodes created.
     */
    protected static Document dDoc = new Document();
    /**
     * Contains references to all created NOM nodes. Nodes created with the 'noGarbage' methods are not added to this
     * list.
     */
    private List<Integer> lGarbage = new ArrayList<Integer>(50);
    /**
     * If <code>true</code> memory leaks are also check if the processing ended in error.
     */
    private boolean checkForLeaksOnError = true;
    /**
     * Set by the test case to indicate a successful test run.
     */
    private boolean success = false;

    /**
     * @see  junit.framework.TestCase#setUp()
     */
    @Override protected void setUp()
                            throws Exception
    {
        super.setUp();

        initialize();
    }

    /**
     * @see  junit.framework.TestCase#tearDown()
     */
    @Override protected void tearDown()
                               throws Exception
    {
        super.tearDown();

        uninitialize();
    }

    /**
     * Initializes the test case. This sets the NOM leak info baseline and enables NOM exceptions.
     */
    public void initialize()
    {
        Document.setLeakInfoBaseline();
    }

    /**
     * Deletes all garbage NOM nodes and assets that there were no NOM leaks. This also disables the NOM exceptions.
     */
    public void uninitialize()
    {
        for (int iNode : lGarbage)
        {
            Node.delete(iNode);
        }

        lGarbage.clear();

        if (checkForLeaksOnError || success)
        {
            assertNoNomLeaks();
        }
    }

    /**
     * Parses the XML string into NOM XML. The root node is added to the garbage list.
     *
     * @param   sXml  XML string.
     *
     * @return  XML root node.
     */
    public int parse(String sXml)
    {
        int iNode = parseNoGarbage(sXml);

        addNomGarbage(iNode);

        return iNode;
    }

    /**
     * Parses the XML string into NOM XML. The XML is not added to the garbage list.
     *
     * @param   sXml  XML string.
     *
     * @return  XML root node.
     */
    public static int parseNoGarbage(String sXml)
    {
        int iNode = 0;

        try
        {
            iNode = dDoc.parseString(sXml);
        }
        catch (Exception e)
        {
            // Just print the exception and fail.
            e.printStackTrace();
            fail("XML parse exception: " + e);
        }

        return iNode;
    }

    /**
     * Loads the XML from a file into NOM XML. The root node is added to the garbage list.
     *
     * @param   fFile  File object pointing to the file to be loaded.
     *
     * @return  XML root node.
     *
     * @throws  Exception  In case of any exceptions
     */
    public int loadXmlFile(File fFile)
                    throws Exception
    {
        int iNode = dDoc.load(fFile.getAbsolutePath());

        addNomGarbage(iNode);

        return iNode;
    }

    /**
     * Loads the XML resource (a file in the classpath) into NOM XML. The root node is added to the garbage list.
     *
     * @param   sResourcePath  Resource path. If relative (does not start with '/' it is relative to the parent
     *                         package).
     *
     * @return  XML root node.
     */
    public int loadXmlResource(String sResourcePath)
    {
        int iNode = loadXmlResourceNoGarbage(sResourcePath);

        addNomGarbage(iNode);

        return iNode;
    }

    /**
     * Loads the XML resource (a file in the classpath) into NOM XML. The XML is not added to the garbage list.
     *
     * @param   sResourcePath  Resource path. If relative (does not start with '/' it is relative to the parent
     *                         package).
     *
     * @return  XML root node.
     */
    public int loadXmlResourceNoGarbage(String sResourcePath)
    {
        int iNode = 0;

        try
        {
            iNode = parseNoGarbage(FileUtils.readTextResourceContents(sResourcePath, getClass()));
        }
        catch (Exception e)
        {
            // Just print the exception and fail.
            e.printStackTrace();
            fail("Unable to load XML resource '" + sResourcePath + "': " + e);
        }

        return iNode;
    }

    /**
     * Adds the NOM node to the clean up list.
     *
     * @param   iNode  NOM node to be added.
     *
     * @return  Returns the added node for convenience.
     */
    public int addNomGarbage(int iNode)
    {
        lGarbage.add(iNode);
        return iNode;
    }

    /**
     * Tests that the two NOM nodes are equal. Currently this uses a simple writeToString() check.
     *
     * @param  controlNode  Control node that has the expected value.
     * @param  testNode     Test node that is compared to the control node.
     */
    public static void assertNodesEqual(int controlNode, int testNode)
    {
        assertNodesEqual(controlNode, testNode, false);
    }

    /**
     * Tests that the two NOM nodes are equal. Currently this uses a simple writeToString() check.
     *
     * @param  controlNode  Control node that has the expected value.
     * @param  testNode     Test node that is compared to the control node.
     * @param  prettyPrint  If <code>true</code>, nodes are pretty printed before textual comparison.
     */
    public static void assertNodesEqual(int controlNode, int testNode, boolean prettyPrint)
    {
        String s1 = Node.writeToString(controlNode, prettyPrint);
        String s2 = Node.writeToString(testNode, prettyPrint);

        assertEquals(s1, s2);
    }

    /**
     * Asserts that there are no leaked NOM nodes after the last base line.
     */
    public static void assertNoNomLeaks()
    {
        String sInfo = Document.getLeakInfo(0, false);
        int iPos = sInfo.indexOf('>');

        if (!sInfo.startsWith("<NOMLeakInfo") || (iPos <= 0))
        {
            fail("Unable to parse the NOM leak info string: " + sInfo);
        }

        Pattern pMatchPattern = Pattern.compile("numOfGrowingDocuments=\"(\\d+)\"");
        Matcher mMatcher = pMatchPattern.matcher(sInfo.substring(0, iPos));

        if (!mMatcher.find())
        {
            fail("Unable to parse the NOM leak info string.");
        }

        assertTrue("NOM nodes leaked:" + sInfo, "0".equals(mMatcher.group(1)));
    }

    /**
     * Returns the checkForLeaksOnError.
     *
     * @return  Returns the checkForLeaksOnError.
     */
    public boolean isCheckForLeaksOnError()
    {
        return checkForLeaksOnError;
    }

    /**
     * Sets the checkForLeaksOnError. If false and the success flag is also false, memory leaks are not checked.
     *
     * @param  checkForLeaksOnError  The checkForLeaksOnError to be set.
     */
    public void setCheckForLeaksOnError(boolean checkForLeaksOnError)
    {
        this.checkForLeaksOnError = checkForLeaksOnError;
    }

    /**
     * Returns the success flag value.
     *
     * @return  Returns the success flag value.
     */
    public boolean isSuccess()
    {
        return success;
    }

    /**
     * Sets the success flag value. <code>true</code> means that the test run passed.
     *
     * @param  success  The success flag value to be set.
     */
    public void setSuccess(boolean success)
    {
        this.success = success;
    }
}
