package com.cordys.coe.tools.log4j;

import org.w3c.dom.Document;

import com.cordys.coe.util.xml.dom.NiceDOMWriter;
import com.cordys.coe.util.xml.dom.XMLHelper;

/**
 * Utility methods for the Log4J viewer.
 *
 * @author  pgussow
 */
public class Util
{
    /**
     * This method formats the data.
     *
     * @param   sXMLMessage  The message that might contain XML.
     *
     * @return  The formatted message.
     */
    public static String formatXmlMessage(String sXMLMessage)
    {
        String sFirstTag = findFirstXmlTag(sXMLMessage);
        int iStartPos;

        if ((sFirstTag != null) && ((iStartPos = sXMLMessage.indexOf("<" + sFirstTag)) != -1))
        {
            String sCloseTag = "</" + sFirstTag + ">";
            int j = sXMLMessage.lastIndexOf(sCloseTag);

            if (j != -1)
            {
                try
                {
                    String sXML = sXMLMessage.substring(iStartPos, j + sCloseTag.length());

                    Document dDoc = XMLHelper.createDocumentFromXML(sXML);
                    
                    while (dDoc == null)
                    {
                    	//Try to see if there are more then 1 end tag.
                    	j = sXMLMessage.lastIndexOf(sCloseTag, j - 1);
                    	if (j < 0)
                    	{
                    		break;
                    	}
                    	sXML = sXMLMessage.substring(iStartPos, j + sCloseTag.length());
                        dDoc = XMLHelper.createDocumentFromXML(sXML);
                    }

                    if (dDoc != null)
                    {
                        String sFormattedXML = NiceDOMWriter.write(dDoc.getDocumentElement(), 2,
                                                                   true, false, false);

                        sXMLMessage = sXMLMessage.substring(0, iStartPos) + sFormattedXML +
                                      sXMLMessage.substring(j + sCloseTag.length());
                    }
                }
                catch (Exception exception)
                {
                    // Ignore the exception
                }
            }
        }
        return sXMLMessage;
    }

    /**
     * This method finds the first tag (if available) in the message.
     *
     * @param   sOriginal  The original message
     *
     * @return  The first tag. If no XML was found it returns null.
     */
    private static String findFirstXmlTag(String sOriginal)
    {
        int iPossibleStartPos = sOriginal.indexOf("<");

        if (iPossibleStartPos != -1)
        {
            if (sOriginal.substring(iPossibleStartPos + 1, iPossibleStartPos + 2).equals("?"))
            {
                iPossibleStartPos = sOriginal.indexOf("<", iPossibleStartPos + 2);
            }

            int iPossibleEndPos = sOriginal.indexOf(">", iPossibleStartPos);

            if (iPossibleEndPos != -1)
            {
                String sPossibleTag = sOriginal.substring(iPossibleStartPos + 1, iPossibleEndPos);
                int iTagLength = sPossibleTag.length();

                for (int iCount = 0; iCount < iTagLength; iCount++)
                {
                    if (!Character.isWhitespace(sPossibleTag.charAt(iCount)))
                    {
                        continue;
                    }
                    sPossibleTag = sPossibleTag.substring(0, iCount);
                    break;
                }

                return sPossibleTag;
            }
        }
        return null;
    }
}
