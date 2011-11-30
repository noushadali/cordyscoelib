package com.cordys.coe.util.cpc;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Node;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * This class contains the testcases for the CPC utilities class methods.
 *
 * @author pgussow
 */
public class CPCUtilitiesTest extends TestCase
{
    /**
     * Holds the document used for testing.
     */
    private Document dDoc;

    /**
     * Constructor.
     *
     * @param sName The name of the test.
     */
    public CPCUtilitiesTest(String sName)
    {
        super(sName);
    }

    /**
     * Main test method.
     *
     * @param args Commandline arguments.
     */
    public static void main(String[] args)
    {
        TestCase tcCase = new CPCUtilitiesTest("testConcatString");
        tcCase.run();
    }

    /**
     * This method test the concat string method.
     */
    @SuppressWarnings("deprecation")
    public void testConcatString()
                          throws Exception
    {
        int iNode = 0;

        try
        {
            iNode = dDoc.createElement("roottag");
            dDoc.createTextElement("sometag", "This is the first part", iNode);
            dDoc.createTextElement("someothertag", "2nd part", iNode);
            dDoc.createTextElement("someothertag", " Last part", iNode);

            String sResult = CPCUtilities.concatString(iNode, "0");
            Assert.assertEquals(sResult, "");

            sResult = CPCUtilities.concatString(iNode, "1");
            Assert.assertEquals(sResult, "This is the first part");

            sResult = CPCUtilities.concatString(iNode, "2");
            Assert.assertEquals(sResult, "This is the first part2nd part");

            sResult = CPCUtilities.concatString(iNode, "3");
            Assert.assertEquals(sResult,
                                "This is the first part2nd part Last part");

            sResult = CPCUtilities.concatString(iNode, "4");
            Assert.assertEquals(sResult,
                                "This is the first part2nd part Last part");
        }
        catch (Exception e)
        {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        finally
        {
            if (iNode != 0)
            {
                Node.delete(iNode);
            }
        }
    }

    /**
     * This method sets up the tester.
     */
    protected void setUp()
                  throws Exception
    {
        dDoc = new Document();
    }
}
