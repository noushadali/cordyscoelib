package com.cordys.coe.test;

import java.io.FileInputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.cordys.coe.tools.orgmanager.log4j.Log4JConfigurationWrapper;
import com.cordys.coe.util.xml.dom.NiceDOMWriter;
import com.cordys.coe.util.xml.dom.XMLHelper;

/**
 * @author pgussow
 *
 */
public class TestLog4JWrapper
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
			Document dDoc = XMLHelper.createDocumentFromStream(new FileInputStream("c:\\temp\\testconf.xml"));
			
			Log4JConfigurationWrapper lcw = new Log4JConfigurationWrapper(dDoc.getDocumentElement());
			
			Element e = lcw.toXML();
			
			System.out.println(NiceDOMWriter.write(e, 2, true, false, false));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
