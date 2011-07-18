package com.cordys.coe.tools.migration.xforms.rules;

import com.cordys.coe.util.xml.nom.XPathHelper;

import com.eibus.xml.nom.Node;
import com.eibus.xml.xpath.XPathMetaInfo;

/**
 * This rule checks the datatype <-> dataType attribute.
 *
 * @author  pgussow
 */
public class DataTypeRule extends BaseXFormRule
{
    /**
     * Creates a new DataTypeRule object.
     */
    public DataTypeRule()
    {
        super("DataType", "The wcpforms:datatype has changed to wcpforms:dataType", true);
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

        int[] aiDataTypes = XPathHelper.selectNodes(iCAF, "//*[@wcpforms:datatype]", xmi);

        if (aiDataTypes.length > 0)
        {
            // Found problems.
            for (int iProblemNode : aiDataTypes)
            {
                IMessage mMessage = addMessage("Found wcpforms:datatype. This should be converted to wcpforms:dataType",
                                               iProblemNode);

                if (bFix)
                {
                    String sValue = Node.getAttributeNS(iProblemNode, "datatype",
                                                        "http://schemas.cordys.com/wcp/xforms", "");
                    Node.removeAttributeNS(iProblemNode, "datatype",
                                           "http://schemas.cordys.com/wcp/xforms");
                    Node.setAttributeNS(iProblemNode, "dataType",
                                        "http://schemas.cordys.com/wcp/xforms", sValue);

                    mMessage.setFixedContext(iProblemNode);
                }
            }
        }
    }
}
