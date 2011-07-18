package com.cordys.coe.test;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.cordys.coe.util.system.SystemInfo;
import com.cordys.coe.util.xml.dom.XMLHelper;
import com.cordys.coe.util.xml.dom.XPathHelper;

/**
 * DOCUMENTME
 *
 * @author $author$
 */
public class TestXPAthAPI
{
    /**
     * Main method.
     *
     * @param saArguments The commandline arguments.
     */
    public static void main(String[] saArguments)
    {
        try
        {
            System.out.println(SystemInfo.getSystemInformation());
            Document dDoc = XMLHelper.loadXMLFile("c:/temp/templates.xml");
            Node nTemp = XPathHelper.selectSingleNode(dDoc.getDocumentElement(), ".//template");
            if (nTemp != null)
            {
                System.out.println(nTemp);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
