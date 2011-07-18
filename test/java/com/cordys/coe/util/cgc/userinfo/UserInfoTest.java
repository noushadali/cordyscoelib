package com.cordys.coe.util.cgc.userinfo;

import com.cordys.coe.util.FileUtils;
import com.cordys.coe.util.general.Util;
import com.cordys.coe.util.xml.dom.XMLHelper;

import junit.framework.TestCase;

import org.w3c.dom.Document;

/**
 * This class tests the parsing of the user info information.
 *
 * @author pgussow
 */
public class UserInfoTest extends TestCase
{
    /**
     * This method tests the parser using DOM (JAXP).
     */
    public void testDOMParser()
    {
        try
        {
            Document dDoc = XMLHelper.createDocumentFromStream(UserInfoTest.class.getResourceAsStream("source.xml"));

            IUserInfo ui = UserInfoFactory.createUserInfo(dDoc.getDocumentElement());

            String sOK = FileUtils.readTextStreamContents(UserInfoTest.class.getResourceAsStream("output.txt"));

            String sActual = ui.toString();
            
            byte[] ba1 = sOK.getBytes();
            byte[] ba2 = sActual.getBytes();
            
            for (int iCount = 0; iCount < ba2.length; iCount++)
            {
                if (iCount > ba2.length || ba1[iCount] != ba2[iCount])
                {
                    System.out.println("Diff: " + iCount);
                }
            }
            

            assertEquals("The parsed content not ok", sOK, sActual);
        }
        catch (Exception e)
        {
            fail(Util.getStackTrace(e));
        }
    }

    /**
     * Test the NOM variant.
     */
    public void testNOMParser()
    {
        try
        {
            com.eibus.xml.nom.Document dDoc = new com.eibus.xml.nom.Document();
            int iTuple = dDoc.load(FileUtils.readStreamContents(UserInfoTest.class.getResourceAsStream("source.xml")));

            IUserInfo ui = UserInfoFactory.createUserInfo(iTuple);

            String sOK = FileUtils.readTextStreamContents(UserInfoTest.class.getResourceAsStream("output.txt"));

            String sActual = ui.toString();

            assertEquals("The parsed content not ok", sOK, sActual);
        }
        catch (Exception e)
        {
            fail(Util.getStackTrace(e));
        }
    }
}
