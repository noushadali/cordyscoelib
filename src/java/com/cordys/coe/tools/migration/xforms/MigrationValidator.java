package com.cordys.coe.tools.migration.xforms;

import com.cordys.coe.tools.migration.xforms.rules.IMessage;
import com.cordys.coe.tools.migration.xforms.rules.IXFormValidationRule;
import com.cordys.coe.tools.migration.xforms.rules.XFormRuleFactory;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Node;

import java.io.PrintStream;

import java.util.ArrayList;

/**
 * This class tries to validate a certain XForm to make sure the difinition is OK and valid against
 * C3 definitions.
 *
 * @author  pgussow
 */
public class MigrationValidator
{
    /**
     * Holds the static document to use for XML parsing.
     */
    private static Document s_dDoc = new Document();
    /**
     * Holds the defined XForm validation rules.
     */
    private ArrayList<IXFormValidationRule> m_alValidationRules = new ArrayList<IXFormValidationRule>();
    /**
     * Holds the XML.
     */
    private int m_iCAF = 0;
    /**
     * Holds the fixed CAF definition.
     */
    private int m_iFixedCAF = 0;

    /**
     * Creates a new MigrationValidator object.
     *
     * @param  iXML  THe XML definition of the XForm.
     */
    public MigrationValidator(int iXML)
    {
        m_iCAF = iXML;

        m_alValidationRules = XFormRuleFactory.getRules();
    }

    /**
     * Main method.
     *
     * @param  saArguments  The commandline arguments.
     */
    public static void main(String[] saArguments)
    {
        try
        {
// String sFile = "D:\\temp\\xformss\\BLink\\Translation\\BFHldBlinkBehVrl.caf";
            String sFile = "D:\\temp\\xformss\\BLink\\Maintenance\\BFHldBlinkBehGbr.caf";

            int iXML = s_dDoc.load(sFile);

            MigrationValidator mv = new MigrationValidator(iXML);
            mv.validateAndFix();

            System.out.println(Node.writeToString(mv.printMessagesToXML(), true));

            mv.printMessages();

            mv.cleanUp();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * This method frees the XML definition.
     */
    public void cleanUp()
    {
        if (m_iFixedCAF != 0)
        {
            Node.delete(m_iFixedCAF);
            m_iFixedCAF = 0;
        }

        for (IXFormValidationRule xvr : m_alValidationRules)
        {
            xvr.cleanUp();
        }
    }

    /**
     * This method gets the CAF definition.
     *
     * @return  The CAF definition.
     */
    public int getCAFDefinition()
    {
        return m_iCAF;
    }

    /**
     * This method gets the fixed caf definition.
     *
     * @return  The fixed caf definition.
     */
    public int getFixedCAF()
    {
        return m_iFixedCAF;
    }

    /**
     * This method gets the validation rules that have been executed.
     *
     * @return  The validation rules that have been executed.
     */
    public IXFormValidationRule[] getValidationRules()
    {
        return m_alValidationRules.toArray(new IXFormValidationRule[0]);
    }

    /**
     * This method prints all messages for the current form.
     */
    public void printMessages()
    {
        printMessages(System.out);
    }

    /**
     * This method prints all messages for the current form.
     *
     * @param  ps  The stream to print to.
     */
    public void printMessages(PrintStream ps)
    {
        for (IXFormValidationRule xvrRule : m_alValidationRules)
        {
            ps.println("Rule: " + xvrRule.getRuleName());
            ps.println("===============================");

            IMessage[] amMessages = xvrRule.getMessages();

            for (IMessage mMessage : amMessages)
            {
                ps.print("- ");

                if (mMessage.getLineNumber() > 0)
                {
                    ps.print("Line " + mMessage.getLineNumber() + ": ");
                }

                ps.println(mMessage.getDescription());
            }

            ps.println("===============================");
        }
    }

    /**
     * This method writes the messages to an XML format.
     *
     * @return  DOCUMENTME
     */
    public int printMessagesToXML()
    {
        int iReturn = s_dDoc.createElement("report");

        int iRule = Node.createElement("rules", iReturn);

        for (IXFormValidationRule xvrRule : m_alValidationRules)
        {
            xvrRule.printMessagesToXML(iRule);
        }

        int iCAF = Node.createElement("original", iReturn);
        Node.duplicateAndAppendToChildren(getCAFDefinition(), getCAFDefinition(), iCAF);

        iCAF = Node.createElement("fixed", iReturn);
        Node.duplicateAndAppendToChildren(getFixedCAF(), getFixedCAF(), iCAF);

        return iReturn;
    }

    /**
     * This method executes the validation.
     */
    public void validate()
    {
        validate(false);
    }

    /**
     * This method executes the validation.
     *
     * @param  bFix  Whether or not the problems should be fixed.
     */
    public void validate(boolean bFix)
    {
        int iCAF = getCAFDefinition();

        if (bFix)
        {
            if (m_iFixedCAF != 0)
            {
                Node.delete(m_iFixedCAF);
                m_iFixedCAF = 0;
            }

            m_iFixedCAF = Node.duplicate(iCAF);

            iCAF = getFixedCAF();
        }

        for (IXFormValidationRule xvrRule : m_alValidationRules)
        {
            xvrRule.validate(iCAF, bFix);
        }
    }

    /**
     * This method executes the validation. Also the problems will be corrected if possible.
     */
    public void validateAndFix()
    {
        validate(true);
    }
}
