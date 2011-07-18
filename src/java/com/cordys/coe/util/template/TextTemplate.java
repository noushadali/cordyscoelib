/**
 * (c) 2006 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util.template;

import java.util.LinkedList;
import java.util.List;

import java.util.regex.Matcher;

/**
 * A text template that can contain variables. The template is parsed into string and variable
 * components and when rendered, these components are concatenated together.
 *
 * @author  mpoyhone
 */
public class TextTemplate
{
    /**
     * Contains string and <code>Variable</code> components of this template.
     */
    private Object[] oaComponents;

    /**
     * Parses the template from the given text.
     *
     * @param   sText           Template text.
     * @param   bCreateIfFixed  If <code>true</code>, template is created even if the passed string
     *                          is a fixed value.
     *
     * @return  Parsed template object.
     */
    public static TextTemplate createFromString(String sText, boolean bCreateIfFixed)
    {
        return createFromString(sText, bCreateIfFixed, EVariableType.STRING);
    }

    /**
     * Parses the template from the given text.
     *
     * @param   sText             Template text.
     * @param   bCreateIfFixed    If <code>true</code>, template is created even if the passed
     *                            string is a fixed value.
     * @param   etDefaultVarType  Default variable type.
     *
     * @return  Parsed template object.
     */
    public static TextTemplate createFromString(String sText, boolean bCreateIfFixed,
                                                EVariableType etDefaultVarType)
    {
        int iLastMatchEndPos = 0;
        Matcher mMatcher = VariableData.pVariablePattern.matcher(sText);
        List<Object> lComponentList = new LinkedList<Object>();

        while ((iLastMatchEndPos < (sText.length() - 1)) && mMatcher.find(iLastMatchEndPos))
        {
            int iMatchStart = mMatcher.start();
            int iMatchEnd = mMatcher.end();
            VariableData vVar = VariableData.parseVariableFromMatcher(mMatcher, etDefaultVarType);

            if (vVar != null)
            {
                if (iMatchStart > iLastMatchEndPos)
                {
                    lComponentList.add(sText.substring(iLastMatchEndPos, iMatchStart));
                }

                lComponentList.add(vVar);
            }
            else
            {
                // This was an ordinary string that only looked like a variable.
                lComponentList.add(sText.substring(iLastMatchEndPos, iMatchEnd));
            }

            iLastMatchEndPos = iMatchEnd;
        }

        if (iLastMatchEndPos < sText.length())
        {
            lComponentList.add(sText.substring(iLastMatchEndPos));
        }

        if (!bCreateIfFixed)
        {
            // Check if this is fixed value.
            boolean bFixed = true;

            for (Object oTmp : lComponentList)
            {
                if (!(oTmp instanceof String))
                {
                    bFixed = false;
                    break;
                }
            }

            if (bFixed)
            {
                return null;
            }
        }

        TextTemplate ttRes = new TextTemplate();

        ttRes.oaComponents = (Object[]) lComponentList.toArray(new Object[lComponentList.size()]);

        return ttRes;
    }

    /**
     * Returns the template components. Returns String and/or Variable objects.
     *
     * @return  Returns the components.
     */
    public Object[] getOaComponents()
    {
        return oaComponents;
    }

    /**
     * Renders the template contents to as string based on the passed variables.
     *
     * @param   vcContext  Context object containing all needed information.
     *
     * @return  Rendered template text.
     */
    public String renderTemplate(VariableContext vcContext)
    {
        StringBuilder sbRes = new StringBuilder(512);

        for (Object oTmp : oaComponents)
        {
            if (oTmp instanceof String)
            {
                sbRes.append(oTmp);
            }
            else
            {
                String sVarValue = ((VariableData) oTmp).resolveValueAsString(vcContext);

                sbRes.append(sVarValue);
            }
        }

        return sbRes.toString();
    }
}
