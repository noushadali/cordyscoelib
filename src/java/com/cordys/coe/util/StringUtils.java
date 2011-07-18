/**
 * (c) 2008 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String manipulation routines.
 *
 * @author  mpoyhone
 */
public class StringUtils
{
    /**
     * Creates a regular expression from glob-pattern. Supported formats:
     *
     * <pre>
         a/**&nbsp;/b
         a/*.xml
         a/*
         a*&nbsp;/**&nbsp;/b.xml
     * </pre>
     *
     * @param   globPattern        Glob pattern to be converted.
     * @param   useForwardSlashes  If <code>true</code> backslashes are converted to forward slashes
     *                             in the regexp.
     * @param   regexFlags         Standard regex flags to be used in the returned pattern.
     *
     * @return  Converted regexp.
     */
    public static Pattern createGlobRegex(String globPattern, boolean useForwardSlashes,
                                          int regexFlags)
    {
        StringBuilder sb = new StringBuilder(512);

        sb.append('^');

        for (int j = 0; j < globPattern.length(); j++)
        {
            char ch = globPattern.charAt(j);

            switch (ch)
            {
                case '\\':
                    if (useForwardSlashes)
                    {
                        sb.append('/');
                    }
                    else
                    {
                        // Escape \
                        sb.append("\\\\");
                    }
                    break;

                case '?':
                    sb.append('.');
                    break;

                case '*':
                    if ((j < (globPattern.length() - 1)) && (globPattern.charAt(j + 1) == '*'))
                    {
                        // This is **, so match everything.
                        sb.append(".+");
                        j++;
                    }
                    else
                    {
                        // This is *, so match only file and folder names.
                        sb.append("[^/]+");
                    }
                    break;

                case '.':
                case '[':
                case ']':
                case '(':
                case ')':
                case '{':
                case '}':
                case '^':
                case '$':
                case '|':
                    // Escape these regexp characters.
                    sb.append("\\").append(ch);
                    break;

                default:
                    sb.append(ch);
                    break;
            }
        }

        sb.append('$');

        return Pattern.compile(sb.toString(), regexFlags);
    }

    /**
     * This method returns whether or not a string is filled.
     *
     * @param   source  The source to check.
     *
     * @return  true if the string is set. Otherwise false.
     */
    public static boolean isSet(String source)
    {
        return (source != null) && (source.trim().length() > 0);
    }

    /**
     * Executes replacement operations for the given string.
     *
     * @param   str           Input string.
     * @param   patterns      Regex patterns array.
     * @param   replacements  Replacement strings for the regex pattern array.
     *
     * @return  Result string.
     */
    public static String replacePatterns(String str, Pattern[] patterns, String[] replacements)
    {
        Pair<String, Integer> res = replacePatternsWithCount(str, patterns, replacements);

        return res.getFirst();
    }

    /**
     * Executes replacement operations for the given string.
     *
     * @param   str           Input string.
     * @param   patterns      Regex patterns array.
     * @param   replacements  Replacement strings for the regex pattern array.
     *
     * @return  (result string, replacement count).
     */
    public static Pair<String, Integer> replacePatternsWithCount(String str, Pattern[] patterns,
                                                                 String[] replacements)
    {
        if (str == null)
        {
            throw new NullPointerException("'str' cannot be null.");
        }

        if (patterns == null)
        {
            throw new NullPointerException("'patterns' cannot be null.");
        }

        if (replacements == null)
        {
            throw new NullPointerException("'replacements' cannot be null.");
        }

        if (patterns.length != replacements.length)
        {
            throw new IllegalArgumentException("Replacement array must have the same length as the pattern array.");
        }

        boolean first = true;
        StringBuffer source = null;
        StringBuffer dest = new StringBuffer(str.length() + 1024);
        int matchCount = 0;

        for (int i = 0; i < patterns.length; i++)
        {
            Pattern p = patterns[i];
            String replacement = replacements[i];
            Matcher m;

            if (first)
            {
                // For the first match, use the string parameter.
                m = p.matcher(str);
                dest.setLength(0);
            }
            else
            {
                // Use the previous destination buffer as the source for this operation.
                if (source == null)
                {
                    source = new StringBuffer(str.length() + 1024);
                }

                StringBuffer tmp = dest;

                dest = source;
                dest.setLength(0);
                source = tmp;
                m = p.matcher(source);
            }

            boolean match = false;

            while (m.find())
            {
                m.appendReplacement(dest, replacement);
                matchCount++;
                match = true;
            }

            if (first && !match)
            {
                // Optimization for non-matching files.
                continue;
            }

            m.appendTail(dest);
            first = false;
        }

        if (matchCount == 0)
        {
            // No matches found, so just return the original string.
            return new Pair<String, Integer>(str, 0);
        }

        return new Pair<String, Integer>(dest.toString(), matchCount);
    }

    /**
     * This method returns the lower case version of the source string. This method could be used in
     * a BPM to accommodate the lack of this function in XPath 1.0
     *
     * @param   source  The source string.
     *
     * @return  The lower case version of the string.
     */
    public static String toLowerCase(String source)
    {
        String returnValue = "";

        if (isSet(source))
        {
            returnValue = source.toLowerCase();
        }

        return returnValue;
    }

    /**
     * This method returns the upper case version of the source string. This method could be used in
     * a BPM to accommodate the lack of this function in XPath 1.0
     *
     * @param   source  The source string.
     *
     * @return  The upper case version of the string.
     */
    public static String toUpperCase(String source)
    {
        String returnValue = "";

        if (isSet(source))
        {
            returnValue = source.toUpperCase();
        }

        return returnValue;
    }
}
