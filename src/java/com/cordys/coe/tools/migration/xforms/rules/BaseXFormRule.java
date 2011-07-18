package com.cordys.coe.tools.migration.xforms.rules;

import com.eibus.xml.nom.Node;
import com.eibus.xml.xpath.XPathMetaInfo;

import java.util.ArrayList;

/**
 * This is the baseclass for all XForm validation rules.
 *
 * @author  pgussow
 */
public abstract class BaseXFormRule
    implements IXFormValidationRule
{
    /**
     * Holds all messages for this form.
     */
    private ArrayList<IMessage> m_alMessages = new ArrayList<IMessage>();
    /**
     * Holds whether or not this error can automatically be fixed.
     */
    private boolean m_bAutoFix;
    /**
     * Holds the description for this rule.
     */
    private String m_sDescription;
    /**
     * Holds the name of this rule.
     */
    private String m_sRuleName;

    /**
     * Constructor. Creates the XForms rule.
     *
     * @param  sRuleName     The name of the rule.
     * @param  sDescription  Holds the description for this rule.
     * @param  bAutoFix      Whether or not the problem can be fixed automatically.
     */
    protected BaseXFormRule(String sRuleName, String sDescription, boolean bAutoFix)
    {
        m_sRuleName = sRuleName;
        m_sDescription = sDescription;
        m_bAutoFix = bAutoFix;
    }

    /**
     * This method cleans the XML references that are being held.
     */
    public void cleanUp()
    {
        for (IMessage mMessage : m_alMessages)
        {
            mMessage.cleanUp();
        }
        m_alMessages.clear();
    }

    /**
     * This method gets the messages for this validation rule.
     *
     * @return  The messages for this validation rule.
     *
     * @see     com.cordys.coe.tools.migration.xforms.rules.IXFormValidationRule#getMessages()
     */
    public IMessage[] getMessages()
    {
        return m_alMessages.toArray(new IMessage[0]);
    }

    /**
     * This method gets the rule description.
     *
     * @return  The rule description.
     *
     * @see     com.cordys.coe.tools.migration.xforms.rules.IXFormValidationRule#getRuleDescription()
     */
    public String getRuleDescription()
    {
        return m_sDescription;
    }

    /**
     * This method gets the name of the rule.
     *
     * @return  The name of the rule.
     *
     * @see     com.cordys.coe.tools.migration.xforms.rules.IXFormValidationRule#getRuleName()
     */
    public String getRuleName()
    {
        return m_sRuleName;
    }

    /**
     * This method gets the XPathMeta info for the XForms.
     *
     * @return  The XPathMeta info for the XForms.
     */
    public XPathMetaInfo getXPathMetaInfo()
    {
        XPathMetaInfo xmiReturn = new XPathMetaInfo();

        xmiReturn.addNamespaceBinding("cordysxform", "http://schemas.cordys.com/1.0/xform");
        xmiReturn.addNamespaceBinding("xforms", "http://www.w3.org/2002/xforms/cr");
        xmiReturn.addNamespaceBinding("ev", "http://www.w3.org/2001/xml-events");
        xmiReturn.addNamespaceBinding("wcpforms", "http://schemas.cordys.com/wcp/xforms");
        xmiReturn.addNamespaceBinding("eibus", "http://schemas.cordys.com/wcp/webframework");
        xmiReturn.addNamespaceBinding("cas", "http://schemas.cordys.com/1.0/cas-vcm");

        return xmiReturn;
    }

    /**
     * This method gets whether or not this error can be fixed automatically.
     *
     * @return  Whether or not this error can be fixed automatically.
     *
     * @see     com.cordys.coe.tools.migration.xforms.rules.IXFormValidationRule#isAutomaticallyFixable()
     */
    public boolean isAutomaticallyFixable()
    {
        return m_bAutoFix;
    }

    /**
     * This method writes the messages to the given XML node.
     *
     * @param  iParent  The parent XML node.
     *
     * @see    com.cordys.coe.tools.migration.xforms.rules.IXFormValidationRule#printMessagesToXML(int)
     */
    public void printMessagesToXML(int iParent)
    {
        int iRule = Node.createElement("rule", iParent);
        Node.createTextElement("name", getRuleName(), iRule);
        Node.createTextElement("description", getRuleDescription(), iRule);
        Node.createTextElement("automaticallyfixable", String.valueOf(isAutomaticallyFixable()),
                               iRule);

        int iMessages = Node.createElement("messages", iRule);

        IMessage[] amMessages = getMessages();

        for (IMessage mMessage : amMessages)
        {
            mMessage.printToXML(iMessages);
        }
    }

    /**
     * This method just validates the CAF without changing it. It will just register the messages.
     *
     * @param  iCAF  The CAF definition.
     *
     * @see    com.cordys.coe.tools.migration.xforms.rules.IXFormValidationRule#validate(int)
     */
    public void validate(int iCAF)
    {
        validate(iCAF, false);
    }

    /**
     * This method adds a new message to the list of messages.
     *
     * @param   sMessage  The actual message.
     *
     * @return  The created message.
     */
    protected IMessage addMessage(String sMessage)
    {
        return addMessage(sMessage, 0, -1);
    }

    /**
     * This method adds a new message to the list of messages.
     *
     * @param   sMessage      The actual message.
     * @param   iContextNode  The context node.
     *
     * @return  The created message.
     */
    protected IMessage addMessage(String sMessage, int iContextNode)
    {
        return addMessage(sMessage, iContextNode, -1);
    }

    /**
     * This method adds a new message to the list of messages.
     *
     * @param   sMessage      The actual message.
     * @param   iContextNode  The context node.
     * @param   iLineNumber   The original line lumber.
     *
     * @return  The created message.
     */
    protected IMessage addMessage(String sMessage, int iContextNode, int iLineNumber)
    {
        IMessage mReturn = new Message(sMessage, iContextNode, iLineNumber);
        m_alMessages.add(mReturn);

        return mReturn;
    }
}
