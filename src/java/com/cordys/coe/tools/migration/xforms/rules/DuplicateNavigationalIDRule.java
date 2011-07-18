package com.cordys.coe.tools.migration.xforms.rules;

import com.cordys.coe.util.xml.nom.XPathHelper;

import com.eibus.xml.nom.Node;
import com.eibus.xml.xpath.XPathMetaInfo;

import java.util.HashMap;

/**
 * This rule checks for duplicate navigational IDs.
 *
 * @author  pgussow
 */
public class DuplicateNavigationalIDRule extends BaseXFormRule
{
    /**
     * Creates a new DataTypeRule object.
     */
    public DuplicateNavigationalIDRule()
    {
        super("DuplicateNavigationalID",
              "It could be possible that there are multiple navigational bars on a single page that they have duplicate IDs. Note: It can be fixed, but it won't fix event-handlers or script references to the button.",
              true);
    }

    /**
     * This method validates the CAF and fixes the problems as far as it is possible.
     *
     * @param  iCAF  The CAF definition. This XML will be changed.
     * @param  bFix  Whether or not the CAF definition should be fixed.
     *
     * @see    com.cordys.coe.tools.migration.xforms.rules.IXFormValidationRule#validate(int, boolean)
     */
    public void validate(int iCAF, boolean bFix)
    {
        // Find all wcpforms:datatype elements
        XPathMetaInfo xmi = getXPathMetaInfo();

        String[] asTypes = new String[] { "navFirst", "navPrevious", "navNext", "navLast" };

        for (String sNavType : asTypes)
        {
            int[] aiDataTypes = XPathHelper.selectNodes(iCAF,
                                                        "//*[@standardid=\"" + sNavType + "\"]",
                                                        xmi);

            if (aiDataTypes.length > 1)
            {
                // Only if there are more then 1 there MIGHT be a problem
                // So we'll first build up a HashMap with all the actual ID's
                HashMap<String, Integer> hmIDs = new HashMap<String, Integer>();

                for (int iProblemNode : aiDataTypes)
                {
                    String sID = Node.getAttribute(iProblemNode, "id", "");

                    if (sID.length() > 0)
                    {
                        // Now check if it exists.
                        if (hmIDs.containsKey(sID))
                        {
                            // Now we have found a problem!

                            IMessage mMessage = addMessage("Found a duplicate id: " + sID +
                                                           " for type " + sNavType, iProblemNode);

                            if (bFix)
                            {
                                // First get a new unique id
                                int iTmpCount = 1;

                                while (hmIDs.containsKey(sNavType + iTmpCount))
                                {
                                    iTmpCount++;
                                }

                                Node.setAttribute(iProblemNode, "id", sNavType + iTmpCount);

                                mMessage.setFixedContext(iProblemNode);
                                hmIDs.put(sNavType + iTmpCount, iProblemNode);
                            }
                        }
                        else
                        {
                            hmIDs.put(sID, iProblemNode);
                        }
                    }
                }
            }
        }
    }
}
