package com.cordys.coe.tools.migration.xforms.rules;

import java.util.ArrayList;

/**
 * Factory object for creating the rule objects.
 *
 * @author  pgussow
 */
public class XFormRuleFactory
{
    /**
     * This method returns an array list with all possible rules.
     *
     * @return  The list of rules.
     */
    public static ArrayList<IXFormValidationRule> getRules()
    {
        ArrayList<IXFormValidationRule> alReturn = new ArrayList<IXFormValidationRule>();

        alReturn.add(new DataTypeRule());
        alReturn.add(new DuplicateNavigationalIDRule());
        alReturn.add(new StripCasNamespaceRule());

        return alReturn;
    }
}
