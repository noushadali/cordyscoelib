package com.cordys.coe.tools.es;

import com.eibus.connector.nom.SOAPMessage;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Find;
import com.eibus.xml.nom.Node;

/**
 * Class to wrap a published message as it was received.
 *
 * @author  pgussow
 */
public class ReceivedMessage
{
    /**
     * Holds the complete message as it was received.
     */
    private int iMessage;
    /**
     * Holds the subject of the message.
     */
    private String sSubject;

    /**
     * Creates a new ReceivedMessage object.
     *
     * @param  iMessage  The message that was received.
     */
    public ReceivedMessage(int iMessage)
    {
        this.iMessage = Node.getRoot(iMessage);
        sSubject = SOAPMessage.getMessageId(this.iMessage);
    }

    /**
     * This method deletes the XML nodes it controls.
     */
    public void cleanUp()
    {
        Node.delete(iMessage);
    }

    /**
     * This method returns the XML data of the published method. The callee is responsible for
     * cleaning up the XML node.
     *
     * @return  The XML data of the published method.
     */
    public int getData()
    {
        int iReturn = 0;
        Document dDoc = Node.getDocument(iMessage);
        int iData = Find.firstMatch(iMessage, "?<SOAP:Body>");

        if (iData != 0)
        {
            int iChild = Node.getFirstElement(iData);

            if (iChild != 0)
            {
                iReturn = Node.duplicate(iChild);
            }
            else
            {
                try
                {
                    iReturn = dDoc.parseString(Node.getData(iData));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        return iReturn;
    }

    /**
     * This method returns the data of the message as a string.
     *
     * @return  The data of the message as a string.
     */
    public String getDataString()
    {
        String sReturn = "";

        int iData = getData();

        sReturn = Node.writeToString(iData, false);

        return sReturn;
    }

    /**
     * This method returns the value of iMessage.
     *
     * @return  Returns the iMessage.
     */
    public int getMessage()
    {
        return iMessage;
    }

    /**
     * This method returns the value of sSubject.
     *
     * @return  Returns the sSubject.
     */
    public String getSubject()
    {
        return sSubject;
    }

    /**
     * Returns the string representation of this message.
     *
     * @return  The string representation of this message.
     */
    @Override public String toString()
    {
        return sSubject;
    }

    /**
     * This method cleans up the object.
     *
     * @throws  Throwable  DOCUMENTME
     */
    @Override protected void finalize()
                               throws Throwable
    {
        cleanUp();
        super.finalize();
    }
}
