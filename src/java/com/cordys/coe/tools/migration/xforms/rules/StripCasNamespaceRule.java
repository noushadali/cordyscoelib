package com.cordys.coe.tools.migration.xforms.rules;

import com.cordys.coe.util.xml.nom.XPathHelper;

import com.eibus.xml.nom.Node;
import com.eibus.xml.xpath.XPathMetaInfo;

/**
 * Rule to strip the CAS-VCM namespace from the XForm.
 *
 * @author  pgussow
 */
public class StripCasNamespaceRule extends BaseXFormRule
{
    /**
     * Creates a new StripCasNamespaceRule object.
     */
    public StripCasNamespaceRule()
    {
        super("StripCasNamespace", "There could be unneeded cas-vcm namespace declarations.", true);
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

        int[] aiDataTypes = XPathHelper.selectNodes(iCAF,
                                                    "//*[namespace-uri()=\"http://schemas.cordys.com/1.0/cas-vcm\"]",
                                                    xmi);

        if (aiDataTypes.length > 0)
        {
            // Found problems.
            for (int iProblemNode : aiDataTypes)
            {
                IMessage mMessage = addMessage("Found the namespace http://schemas.cordys.com/1.0/cas-vcm in the XForm.",
                                               iProblemNode);

                if (bFix)
                {
                    Node.removeAttribute(iProblemNode, "xmlns");

                    mMessage.setFixedContext(iProblemNode);
                }
            }
        }
    }
}
