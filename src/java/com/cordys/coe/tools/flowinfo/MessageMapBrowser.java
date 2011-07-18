package com.cordys.coe.tools.flowinfo;

import com.cordys.coe.util.xml.dom.NamespaceConstants;
import com.cordys.coe.util.xml.dom.NiceDOMWriter;
import com.cordys.coe.util.xml.dom.XPathHelper;

import javax.xml.transform.TransformerException;

import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolItem;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class is able to browse the messagemap.
 *
 * @author  pgussow
 */
public class MessageMapBrowser
{
    static
    {
        NamespaceConstants.registerPrefix("bpm", "http://schemas.cordys.com/bpm/instance/1.0");
    }

    /**
     * Holds the first button.
     */
    private ToolItem m_bFirst;
    /**
     * Holds the last ToolItem.
     */
    private ToolItem m_bLast;
    /**
     * Holds the next ToolItem.
     */
    private ToolItem m_bNext;
    /**
     * Holds the previous ToolItem.
     */
    private ToolItem m_bPrevious;
    /**
     * Holds the full message map.
     */
    private Document m_dMessageMap;
    /**
     * Holds the current index.
     */
    private int m_iIndex = 0;
    /**
     * Holds the list of messages in the message map.
     */
    private NodeList m_nlMessages;
    /**
     * Holds the text control to write the message to.
     */
    private Text m_tControl;

    /**
     * Creates a new MessageMapBrowser object.
     *
     * @param   dMessageMap  The XML document for the whole message map.
     * @param   tControl     The control to display the message map in.
     * @param   bFirst       The first ToolItem.
     * @param   bPrevious    The previous ToolItem.
     * @param   bNext        The next ToolItem.
     * @param   bLast        The last ToolItem.
     *
     * @throws  TransformerException  In case of any exceptions.
     */
    public MessageMapBrowser(Document dMessageMap, Text tControl, ToolItem bFirst,
                             ToolItem bPrevious, ToolItem bNext, ToolItem bLast)
                      throws TransformerException
    {
        m_dMessageMap = dMessageMap;
        m_tControl = tControl;
        m_bFirst = bFirst;
        m_bPrevious = bPrevious;
        m_bNext = bNext;
        m_bLast = bLast;
        m_nlMessages = XPathHelper.selectNodeList(dMessageMap.getDocumentElement(), "./*",
                                                  NamespaceConstants.getPrefixResolver());

        first();
    }

    /**
     * This method shows the first message in the message map..
     */
    public void first()
    {
        if (m_nlMessages.getLength() > 0)
        {
            m_iIndex = 0;
            displayXML(m_nlMessages.item(m_iIndex));
        }
        else
        {
            m_tControl.setText("");
        }

        setProperToolItems();
    }

    /**
     * This method shows the last message in the message map.
     */
    public void last()
    {
        if (m_nlMessages.getLength() > 0)
        {
            m_iIndex = m_nlMessages.getLength() - 1;
            displayXML(m_nlMessages.item(m_iIndex));
        }
        else
        {
            m_tControl.setText("");
        }

        setProperToolItems();
    }

    /**
     * This method shows the next message in the message map.
     */
    public void next()
    {
        if (m_nlMessages.getLength() > 0)
        {
            m_iIndex++;
            displayXML(m_nlMessages.item(m_iIndex));
        }
        else
        {
            m_tControl.setText("");
        }

        setProperToolItems();
    }

    /**
     * This method shows the previous message in the message map.
     */
    public void previous()
    {
        if (m_nlMessages.getLength() > 0)
        {
            m_iIndex--;
            displayXML(m_nlMessages.item(m_iIndex));
        }
        else
        {
            m_tControl.setText("");
        }

        setProperToolItems();
    }

    /**
     * This method shows the complete message map.
     */
    public void showAll()
    {
        m_iIndex = -1;
        displayXML(m_dMessageMap.getDocumentElement());
    }

    /**
     * This method displays the given node.
     *
     * @param  nNode  The node to display.
     */
    private void displayXML(Node nNode)
    {
        m_tControl.setText(NiceDOMWriter.write(nNode, 4, true, false, false));
    }

    /**
     * This method makes sure the proper ToolItems are enabled.
     */
    private void setProperToolItems()
    {
        if (m_iIndex == -1)
        {
            // All is being displayed, so only first and last available
            m_bFirst.setEnabled(true);
            m_bPrevious.setEnabled(false);
            m_bNext.setEnabled(false);
            m_bLast.setEnabled(true);
        }
        else if (m_iIndex == 0)
        {
            // First is being displayed
            m_bFirst.setEnabled(false);
            m_bPrevious.setEnabled(false);
            m_bNext.setEnabled(true);
            m_bLast.setEnabled(true);
        }
        else if (m_iIndex == (m_nlMessages.getLength() - 1))
        {
            // First is being displayed
            m_bFirst.setEnabled(true);
            m_bPrevious.setEnabled(true);
            m_bNext.setEnabled(false);
            m_bLast.setEnabled(false);
        }
        else
        {
            m_bFirst.setEnabled(true);
            m_bPrevious.setEnabled(true);
            m_bNext.setEnabled(true);
            m_bLast.setEnabled(true);
        }
    }
}
