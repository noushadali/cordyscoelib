package com.cordys.coe.tools.migration.xforms.rules;

/**
 * This interface describes the different checks that could be done.
 *
 * @author  pgussow
 */
public interface IXFormValidationRule
{
    /**
     * This method cleans the XML references that are being held.
     */
    void cleanUp();

    /**
     * This method gets the messages for this validation rule.
     *
     * @return  The messages for this validation rule.
     */
    IMessage[] getMessages();

    /**
     * This method gets the rule description.
     *
     * @return  The rule description.
     */
    String getRuleDescription();

    /**
     * This method gets the name of the rule.
     *
     * @return  The name of the rule.
     */
    String getRuleName();

    /**
     * This method gets whether or not this error can be fixed automatically.
     *
     * @return  Whether or not this error can be fixed automatically.
     */
    boolean isAutomaticallyFixable();

    /**
     * This method writes the messages to the given XML node.
     *
     * @param  iParent  THe parent XML node.
     */
    void printMessagesToXML(int iParent);

    /**
     * This method just validates the CAF without changing it. It will just register the messages.
     *
     * @param  iCAF  The CAF definition.
     */
    void validate(int iCAF);

    /**
     * This method validates the CAF and fixes the problems as far as it is possible.
     *
     * @param  iCAF  The CAF definition. This XML will be changed.
     * @param  bFix  Whether or not the CAF definition should be fixed.
     */
    void validate(int iCAF, boolean bFix);
}
