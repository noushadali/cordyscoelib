/**
 *       2003 Cordys R&D B.V. All rights reserved.
 *      The computer program(s) is the proprietary information of Cordys R&D B.V.
 *      and provided under the relevant License Agreement containing restrictions
 *      on use and disclosure. Use is subject to the License Agreement.
 */
package com.cordys.coe.util.cpc;

import com.eibus.xml.nom.Document;
import com.eibus.xml.nom.Find;
import com.eibus.xml.nom.Node;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;

/**
 * This class contains static utility methods that can be used thruh the JavaCall processor.
 *
 * @author  pgussow
 */
public class CPCUtilities
{
    /**
     * Indicates the '+'-operator.
     */
    private static final int OP_ADD = 0;
    /**
     * Indicates the '-'-operator.
     */
    private static final int OP_SUBSTRACT = 1;
    /**
     * Indicates the '/'-operator.
     */
    private static final int OP_DIVIDE = 2;
    /**
     * Indicates the '*'-operator.
     */
    private static final int OP_MULTIPLY = 3;

    /**
     * This method adds time to a given date.
     *
     * @param   date     a date in the specified format (null or empty is current date)
     * @param   value    the number of units that will be added to date.
     * @param   unit     one of 'year', 'month', 'week', 'day', 'hour', 'minute', 'second'.
     * @param   sFormat  The format to use for the in/output
     *
     * @return  A string representation of the date.
     */
    public static String addTimeToDate(String date, int value, String unit, String sFormat)
    {
        String sRealFormat = sFormat;

        if ((sRealFormat == null) || sRealFormat.equals(""))
        {
            sRealFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        }

        SimpleDateFormat UTCFormat = new SimpleDateFormat(sRealFormat);

        Date dDate = new Date(); // current date

        if ((date != null) && (date.length() != 0))
        {
            try
            {
                dDate = UTCFormat.parse(date);
            }
            catch (Exception e)
            {
                // ignore exceptions
            }
        }

        // set time in a default calendar.
        Calendar cal = Calendar.getInstance();
        cal.setTime(dDate);

        // parse unit: one of 'year', 'month', 'week', 'day', 'hour', 'minute', 'second'.
        int iUnit = -1;

        if (unit.equalsIgnoreCase("year"))
        {
            iUnit = Calendar.YEAR;
        }
        else if (unit.equalsIgnoreCase("month"))
        {
            iUnit = Calendar.MONTH;
        }
        else if (unit.equalsIgnoreCase("week"))
        {
            iUnit = Calendar.WEEK_OF_YEAR;
        }
        else if (unit.equalsIgnoreCase("day"))
        {
            iUnit = Calendar.DAY_OF_MONTH;
        }
        else if (unit.equalsIgnoreCase("hour"))
        {
            iUnit = Calendar.HOUR_OF_DAY;
        }
        else if (unit.equalsIgnoreCase("minute"))
        {
            iUnit = Calendar.MINUTE;
        }
        else if (unit.equalsIgnoreCase("second"))
        {
            iUnit = Calendar.SECOND;
        }
        else
        {
            throw new RuntimeException("Unknown unit: " + unit);
        }

        // add time to calendar
        cal.add(iUnit, value);

        // new calendar date
        Date newDate = cal.getTime();

        String sReturn = fixedDateFormat(UTCFormat, newDate);

        return sReturn;
    } // addTimeToDate

    /**
     * This method calculates the difference between 2 dates.
     *
     * @param   a  The first date.
     * @param   b  The second date.
     *
     * @return  The difference.
     */
    public static int calculateDayDifference(final Date a, final Date b)
    {
        int tempDifference = 0;
        int difference = 0;
        Calendar earlier = Calendar.getInstance();
        Calendar later = Calendar.getInstance();

        if (a.compareTo(b) < 0)
        {
            earlier.setTime(a);
            later.setTime(b);
        }
        else
        {
            earlier.setTime(b);
            later.setTime(a);
        }

        while (earlier.get(Calendar.YEAR) != later.get(Calendar.YEAR))
        {
            tempDifference = 365 * (later.get(Calendar.YEAR) - earlier.get(Calendar.YEAR));
            difference += tempDifference;

            earlier.add(Calendar.DAY_OF_YEAR, tempDifference);
        }

        if (earlier.get(Calendar.DAY_OF_YEAR) != later.get(Calendar.DAY_OF_YEAR))
        {
            tempDifference = later.get(Calendar.DAY_OF_YEAR) - earlier.get(Calendar.DAY_OF_YEAR);
            difference += tempDifference;

            earlier.add(Calendar.DAY_OF_YEAR, tempDifference);
        }

        return difference;
    }

    /**
     * This method concatenates all the String-tags in the passed on XML. The param iXMLNode should
     * point to an XML of this format:
     *
     * <pre>
       &lt;roottag&gt;
            &lt;sometag&gt;This is the first part&lt;/sometag&gt;
            &lt;someothertag&gt;2nd part&lt;/someothertag&gt;
            &lt;someothertag&gt; Last part&lt;/someothertag&gt;
       &lt;/roottag&gt;
     * </pre>
     *
     * <p>Given the sample XML the return will be: "This is the first part2nd part Last part"</p>
     *
     * @param   iXMLNode        The source XML.
     * @param   sValidChildren  DOCUMENTME
     *
     * @return  The data of all the children of the node iXMLNode concatenated as one String.
     */
    public static String concatString(int iXMLNode, String sValidChildren)
    {
        String sReturn = "";

        // See if only a protion of the children need to be concatenated.
        int iValidChildren = -1;

        if ((sValidChildren != null) && !sValidChildren.equals(""))
        {
            try
            {
                iValidChildren = Integer.parseInt(sValidChildren);
            }
            catch (Exception e)
            {
                // Ignore the exception
            }
        }

        if (iValidChildren != 0)
        {
            int iChildNode = Node.getFirstChild(iXMLNode);
            int iCount = 0;

            while (iChildNode != 0)
            {
                sReturn += Node.getData(iChildNode);
                iChildNode = Node.getNextSibling(iChildNode);
                iCount++;

                if ((iValidChildren != -1) && (iCount == iValidChildren))
                {
                    break;
                }
            }
        }

        return sReturn;
    } // concatString

    /**
     * Returns string value ifTrue (if expressionTrueFalse = "1" (true)) else ifFalse The code
     * behind this function is very simple:
     *
     * <p>return expressionTrueFalse.equals("1") ? ifTrue : ifFalse;</p>
     *
     * <p>This function can be used in BPMs in BCP C3, since ? : expressions are not possible in BCP
     * C3. However, this function is only used in C2.</p>
     *
     * @param   expressionTrueFalse
     * @param   ifTrue
     * @param   ifFalse
     *
     * @return  ifTrue when expressionTrueFalse is true else ifFalse.
     */
    public static String conditionalExpression(String expressionTrueFalse, String ifTrue,
                                               String ifFalse)
    {
        return expressionTrueFalse.equals("1") ? ifTrue : ifFalse;
    }

    /**
     * Returns string value ifTrue (if expressionTrueFalse = "1" (true)) else ifFalse The code
     * behind this function is very simple:
     *
     * <p>return expressionTrueFalse.equals("1") ? ifTrue : ifFalse;</p>
     *
     * <p>This function can be used in BPMs in BCP C3, since ? : expressions are not possible in BCP
     * C3. This function is used in C3.</p>
     *
     * @param   expressionTrueFalse
     * @param   ifTrue
     * @param   ifFalse
     *
     * @return  ifTrue when expressionTrueFalse is true else ifFalse.
     */
    public static String conditionalExpression(boolean expressionTrueFalse, String ifTrue,
                                               String ifFalse)
    {
        return expressionTrueFalse ? ifTrue : ifFalse;
    }

    /**
     * This method returns a new XML structure based on the XML-structure passed on in the iInputDoc
     * variable. The outputdocument will have a roottag with a name that is the value of the tag
     * &lt;roottag&gt; in the inputdoc. An example:
     *
     * <pre>
        &lt;inputdoc&gt;
            &lt;roottag&gt;NewRoot&lt;/roottag&gt;
            &lt;sometag&gt;Value&lt;/sometag&gt;
            &lt;someothertag&gt;Another value&lt;/someothertag&gt;
            &lt;tag&gt;
                &lt;tag1&gt;bla&lt;/tag1&gt;
                &lt;tag2&gt;bla&lt;/tag2&gt;
            &lt;/tag&gt;
       &lt;/inputdoc&gt;
     * </pre>
     *
     * <p>The output will be:</p>
     *
     * <pre>
        &lt;NewRoot&gt;
            &lt;sometag&gt;Value&lt;/sometag&gt;
            &lt;someothertag&gt;Another value&lt;/someothertag&gt;
            &lt;tag&gt;
                &lt;tag1&gt;bla&lt;/tag1&gt;
                &lt;tag2&gt;bla&lt;/tag2&gt;
            &lt;/tag&gt;
       &lt;/NewRoot&gt;
     * </pre>
     *
     * @param   iInputDoc  The inputdocument.
     *
     * @return  The outputdocument with the new roottag.
     */
    public static int createRoottag(int iInputDoc)
    {
        int iReturn = 0;
        Document dDoc = Node.getDocument(iInputDoc);

        int iRootNode = Find.firstMatch(iInputDoc, "fChild<roottag>");

        if (iRootNode != 0)
        {
            String sNewRoottag = Node.getDataWithDefault(iRootNode, null);

            if (sNewRoottag != null)
            {
                // Create the new
                iReturn = dDoc.createElement(sNewRoottag);

                int iNode = Node.getFirstChild(iInputDoc);

                while (iNode != 0)
                {
                    if (!Node.getName(iNode).equals("roottag"))
                    {
                        int iNewNode = Node.duplicate(iNode);
                        Node.appendToChildren(iNewNode, iReturn);
                    }
                    iNode = Node.getNextSibling(iNode);
                }
            }
        }
        return iReturn;
    } // createRoottag

    /**
     * This method does simple calculations based on the XML that was received. iMathXML should
     * point to an XML-structure like this:
     *
     * <pre>
       &lt;math&gt;
            &lt;quantity&gt;10&lt;/quantity&gt;
            &lt;operator1&gt;*&lt;/operator&gt; &lt;price&gt;1.2&lt;/price&gt;
            &lt;operator2&gt;-&lt;operator2&gt; &lt;discount&gt;10&lt;/discount&gt;
       &lt;/math&gt;
     * </pre>
     *
     * @param   iMathXML  The XML with the definition of the calculation.
     *
     * @return  A double with the result of the calculation.
     */
    public static double doMath(int iMathXML)
    {
        double dReturn = 0;

        int iNode = Node.getFirstChild(iMathXML);
        int iOperator = -1;

        while (iNode != 0)
        {
            // Pick up the operator
            if (Node.getName(iNode).startsWith("operator"))
            {
                String sOperator = Node.getDataWithDefault(iNode, "");

                if (sOperator.equals("+"))
                {
                    iOperator = OP_ADD;
                }
                else if (sOperator.equals("-"))
                {
                    iOperator = OP_SUBSTRACT;
                }
                else if (sOperator.equals("/"))
                {
                    iOperator = OP_DIVIDE;
                }
                else if (sOperator.equals("*"))
                {
                    iOperator = OP_MULTIPLY;
                }
            }
            else
            {
                // Since it's not an operator, it's a value
                String sTemp = Node.getDataWithDefault(iNode, "0");

                if ((sTemp != null) && sTemp.equals(""))
                {
                    sTemp = "0";
                }

                double dValue = Double.parseDouble(sTemp);

                switch (iOperator)
                {
                    case OP_ADD:
                        dReturn += dValue;
                        break;

                    case OP_SUBSTRACT:
                        dReturn -= dValue;
                        break;

                    case OP_DIVIDE:
                        dReturn = dReturn / dValue;
                        break;

                    case OP_MULTIPLY:
                        dReturn = dReturn * dValue;
                        break;

                    case -1:
                        dReturn = dValue;
                }
                iOperator = -1;
            }
            iNode = Node.getNextSibling(iNode);
        }

        return dReturn;
    } // doMath

    /**
     * This method does simple calculations based on the XML that was received. iMathXML should
     * point to an XML-structure like this:
     *
     * <pre>
       &lt;math&gt;
            &lt;quantity&gt;10&lt;/quantity&gt;
            &lt;operator1&gt;*&lt;/operator&gt;
            &lt;price&gt;1.2&lt;/price&gt;
            &lt;operator2&gt;-&lt;operator2&gt;
            &lt;discount&gt;10&lt;/discount&gt;
       &lt;/math&gt;
     * </pre>
     *
     * @param   iMathXML  The XML with the definition of the calculation.
     *
     * @return  An integer with the result of the calculation. The calculation is done with doubles,
     *          but the result is rounded to in Integer
     */
    public static int doMathInteger(int iMathXML)
    {
        int iReturn = 0;

        double dTemp = doMath(iMathXML);
        long lTemp = Math.round(dTemp);

        if (lTemp > Integer.MAX_VALUE)
        {
            iReturn = Integer.MAX_VALUE;
        }
        else
        {
            iReturn = (int) lTemp;
        }

        return iReturn;
    } // doMathInteger

    /**
     * This is a dummy method. It does nothing, but it can be used to send a dummy message to. This
     * method is needed so in a CPC flow we can generate the message from the WSDL before it is
     * actually used.
     *
     * @param  iNode  DOCUMENTME
     */
    public static void dummyMethod(int iNode)
    {
        // It does nothing.
        iNode = 0;
    } // dummyMethod

    /**
     * This method returns the date formatted by the given format. If sDate is null or empty the
     * current date will be used. If sFormat is null or empty the SQL-server default will be used
     * (yyyy-mm-ddTHH:MM:SS.M If iDate is specified it should look like this:
     *
     * <pre>
       <date>
                <year>2003</year>
                <month>01</month>
                <day>27</day>
                <hour>12</hour>
                <minute>00</minute>
                <second>10</second>
                <milisecond>0</milisecond>
       </date>
     * </pre>
     *
     * <p>The supported formats can be found at the JavaDoc for SimpleDateFormat</p>
     *
     * @param   iDate    The date to format.
     * @param   sFormat  The format to use for the output
     *
     * @return  A string representation of the date.
     *
     * @see     java.text.SimpleDateFormat
     */
    public static String formatDate(int iDate, String sFormat)
    {
        String sReturn = null;

        Date dDate = new Date();

        if (iDate != 0)
        {
            int iYearNode = Find.firstMatch(iDate, "?<year>");
            int iMonthNode = Find.firstMatch(iDate, "?<month>");
            int iDayNode = Find.firstMatch(iDate, "?<day>");
            int iHoursNode = Find.firstMatch(iDate, "?<hour>");
            int iMinutesNode = Find.firstMatch(iDate, "?<minute>");
            int iSecondsNode = Find.firstMatch(iDate, "?<second>");
            int iMilisecondsNode = Find.firstMatch(iDate, "?<milisecond>");

            if ((iYearNode != 0) && (iMonthNode != 0) && (iDayNode != 0))
            {
                try
                {
                    Calendar cCal = Calendar.getInstance();
                    cCal.set(Calendar.YEAR,
                             Integer.parseInt(Node.getDataWithDefault(iYearNode, "0")));
                    cCal.set(Calendar.MONTH,
                             Integer.parseInt(Node.getDataWithDefault(iMonthNode, "0")));
                    cCal.set(Calendar.DATE,
                             Integer.parseInt(Node.getDataWithDefault(iDayNode, "0")));
                    cCal.set(Calendar.HOUR,
                             Integer.parseInt(Node.getDataWithDefault(iHoursNode, "0")));
                    cCal.set(Calendar.MINUTE,
                             Integer.parseInt(Node.getDataWithDefault(iMinutesNode, "0")));
                    cCal.set(Calendar.SECOND,
                             Integer.parseInt(Node.getDataWithDefault(iSecondsNode, "0")));
                    cCal.set(Calendar.MILLISECOND,
                             Integer.parseInt(Node.getDataWithDefault(iMilisecondsNode, "0")));
                    dDate = cCal.getTime();
                }
                catch (Exception e)
                {
                    // Ignore the exceptions
                }
            }
        }

        String sRealFormat = sFormat;

        if ((sRealFormat == null) || sRealFormat.equals(""))
        {
            sRealFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(sRealFormat);

        sReturn = fixedDateFormat(sdf, dDate);

        return sReturn;
    } // formatDate

    /**
     * This method returns the date formatted by the given format. If date is null or empty the
     * current date will be used. if sFormat is null or empty the SQL-server default will be used
     * :'yyyy-mm-ddTHH:MM:SS.M' The should be specified in the following format '17/04/2003' (as
     * returned by the standard (generated) BAC).
     *
     * @param   date     a date in the format 'dd/MM/yyyy' e.g. '17/04/2003'
     * @param   sFormat  The format to use for the output
     *
     * @return  A string representation of the date.
     */
    public static String formatWCPDate(String date, String sFormat)
    {
        String sReturn = null;

        Date dDate = new Date();

        if ((date != null) && (date.length() != 0))
        {
            SimpleDateFormat wcpFormat = new SimpleDateFormat("dd/MM/yyyy");

            try
            {
                dDate = wcpFormat.parse(date);
            }
            catch (Exception e)
            {
                // ignore exceptions
            }
        }

        String sRealFormat = sFormat;

        if ((sRealFormat == null) || sRealFormat.equals(""))
        {
            sRealFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(sRealFormat);

        sReturn = fixedDateFormat(sdf, dDate);

        return sReturn;
    } // formatWCPDate

    /**
     * Gets the IP address of the local host.
     *
     * @return  the IP address of the local host.
     *
     * @throws  UnknownHostException  DOCUMENTME
     */
    public static String getLocalHostAddress()
                                      throws UnknownHostException
    {
        return InetAddress.getLocalHost().getHostAddress();
    }

    /**
     * Gets the localhost name.
     *
     * @return  the name of the local host.
     *
     * @throws  UnknownHostException  DOCUMENTME
     */
    public static String getLocalHostName()
                                   throws UnknownHostException
    {
        return InetAddress.getLocalHost().getHostName();
    }

    /**
     * This calculates the length of the period between two dates.
     *
     * @param   date1    a date in the specified format (null or empty is current date)
     * @param   date2    a date in the specified format (null or empty is current date)
     * @param   unit     one of 'week', 'day', 'hour', 'minute', 'second'.
     * @param   sFormat  The format to use for the input
     *
     * @return  the length of the period in the specified unit.
     */
    public static long getPeriodLength(String date1, String date2, String unit, String sFormat)
    {
        String sRealFormat = sFormat;

        if ((sRealFormat == null) || sRealFormat.equals(""))
        {
            sRealFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        }

        SimpleDateFormat UTCFormat = new SimpleDateFormat(sRealFormat);

        // parse date 1
        Date dDate1 = new Date(); // current date

        if ((date1 != null) && (date1.length() != 0))
        {
            try
            {
                dDate1 = UTCFormat.parse(date1);
            }
            catch (Exception e)
            {
                // ignore exceptions
            }
        }

        // parse date 2
        Date dDate2 = new Date(); // current date

        if ((date2 != null) && (date2.length() != 0))
        {
            try
            {
                dDate2 = UTCFormat.parse(date2);
            }
            catch (Exception e)
            {
                // ignore exceptions
            }
        }

        long diff_ms = Math.abs(dDate2.getTime() - dDate1.getTime());
        long diff_sec = diff_ms / 1000;
        long diff_min = diff_sec / 60;
        long diff_hour = diff_min / 60;
        long diff_day = diff_hour / 24;
        long diff_week = diff_day / 7;

        if (unit.equalsIgnoreCase("week"))
        {
            return diff_week;
        }
        else if (unit.equalsIgnoreCase("day"))
        {
            return diff_day;
        }
        else if (unit.equalsIgnoreCase("hour"))
        {
            return diff_hour;
        }
        else if (unit.equalsIgnoreCase("minute"))
        {
            return diff_min;
        }
        else if (unit.equalsIgnoreCase("second"))
        {
            return diff_sec;
        }
        else
        {
            throw new RuntimeException("Unknown unit: '" + unit + "'.");
        }
    }

    /**
     * This method returns wether or not a tag is present.
     *
     * @param   iXML   The XML in which to find the tag
     * @param   sPath  The NOM-search-string.
     *
     * @return  True is only returned if the tag exists.
     */
    public static boolean isTagAvailable(int iXML, String sPath)
    {
        boolean bReturn = false;

        if ((sPath == null) || sPath.equals(""))
        {
            throw new RuntimeException("Searchpath cannot be empy.");
        }

        int iNode = Find.firstMatch(iXML, sPath);

        if (iNode != 0)
        {
            bReturn = true;
        }

        return bReturn;
    } // isTagAvailable

    /**
     * This method returns wether or not a tag is present and if it's not empty.
     *
     * @param   iXML   The XML in which to find the tag
     * @param   sPath  The NOM-search-string.
     *
     * @return  True is only returned if the tag exists AND that tag is filled with data.
     */
    public static boolean isTagFilled(int iXML, String sPath)
    {
        boolean bReturn = false;

        if ((sPath == null) || sPath.equals(""))
        {
            throw new RuntimeException("Searchpath cannot be empy.");
        }

        int iNode = Find.firstMatch(iXML, sPath);

        if (iNode != 0)
        {
            String sData = Node.getData(iNode);

            if ((sData != null) && !sData.equals(""))
            {
                bReturn = true;
            }
        }

        return bReturn;
    } // isTagFilled

    /**
     * Sleeps the number of seconds.
     *
     * @param   secs  Number of second to sleep.
     *
     * @return  Number of milliseconds actually slept.
     */
    public static long sleepSeconds(double secs)
    {
        long start = System.currentTimeMillis();

        try
        {
            Thread.sleep((long) (1000 * secs));
        }
        catch (InterruptedException e)
        {
            throw new IllegalStateException("Sleep was interrupted.", e);
        }

        long end = System.currentTimeMillis();

        return end - start;
    }

    /**
     * This method trims the passed on string. It removed both the leading and the trailing spaces.
     *
     * @param   sSource  The source-string
     *
     * @return  A string with no leading or trailing spaces.
     */
    public static String trimString(String sSource)
    {
        String sReturn = sSource.trim();

        return sReturn;
    } // trimString

    /**
     * Formats the date with the given date format class. This method fixes milliseconds in the
     * formated string. Milliseconds are outputed with minimum of three digits and zeroes are
     * appended the left (which is incorrect for more than three digits).
     *
     * @param   sdf    Date format object to be used.
     * @param   dDate  Date to be formatted.
     *
     * @return  Formatted string.
     */
    private static String fixedDateFormat(SimpleDateFormat sdf, Date dDate)
    {
        String sFormat = sdf.toPattern();
        String sMillisPattern = sFormat.replaceFirst(".*[^S](S+).*", "$1");

        if (sMillisPattern.equals(sFormat))
        {
            // No milliseconds defined.
            return sdf.format(dDate);
        }

        int iMillisDigits = sMillisPattern.length();

        if (iMillisDigits == 3)
        {
            // This is the default behaviour, so use the default implementation.
            return sdf.format(dDate);
        }

        FieldPosition fp = new FieldPosition(DateFormat.Field.MILLISECOND);
        StringBuffer sb = new StringBuffer(50);

        sdf.format(dDate, sb, fp);

        int iStart = fp.getBeginIndex();
        int iEnd = fp.getEndIndex();

        if ((iStart < 0) || (iEnd <= iStart) || (iEnd > sb.length()))
        {
            // Probably the field was not in the string.
            return sb.toString();
        }

        String sValue = sb.substring(iStart, iEnd);

        if (iMillisDigits < 3)
        {
            sValue = sValue.substring(0, iMillisDigits);
        }
        else
        {
            sValue = Integer.toString(Integer.parseInt(sValue));

            // When less that one 1000 millis, add the zeros to the beginning.
            // otherwise we change the actual value.
            while (sValue.length() < 3)
            {
                sValue = "0" + sValue;
            }

            // Add extra zeros to the end.
            while (sValue.length() < iMillisDigits)
            {
                sValue += "0";
            }
        }

        sb.replace(iStart, iEnd, sValue);

        return sb.toString();
    }
} // CPCUtilities
