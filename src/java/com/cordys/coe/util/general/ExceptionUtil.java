package com.cordys.coe.util.general;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains utilities with regard to exceptions.<br>
 * NOTE: Make sure this class has no dependencies to any Cordys library!!
 *
 * @author  pgussow
 */
public class ExceptionUtil
{
    /**
     * This method returns the string-representation of the stacktrace of the passed on exception.
     *
     * @param   tException  The exception to get the stacktrace of.
     *
     * @return  The string-representation of the stacktrace.
     */
    public static String getStackTrace(Throwable tException)
    {
        // Get the stack-trace
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tException.printStackTrace(pw);
        pw.flush();

        return sw.getBuffer().toString();
    }

    /**
     * Returns a string representation of the stack trace only with the generating method
     * information. The output will looke like this: (Exception Name): (Message) [(Method
     * Name):(Line Number)] (Exception Name2): (Message2) [(Method Name2):(Line Number2)] ...
     *
     * @param   t                  The exception to get the stacktrace of.
     * @param   includeMethodInfo  If <code>true</code> the method information is included.
     *
     * @return  The string-representation of the stacktrace.
     */
    public static String getSimpleErrorTrace(Throwable t, boolean includeMethodInfo)
    {
        StringBuilder res = new StringBuilder(512);
        String[] trace = getSimpleErrorTraceElements(t, includeMethodInfo);

        for (String s : trace)
        {
            res.append(s).append("\n");
        }

        return res.toString();
    }

    /**
     * Returns a string representation of the stack trace only with the generating method
     * information. The output array elements will looke like this: (Exception Name): (Message)
     * [(Method Name):(Line Number)]
     *
     * @param   t                  The exception to get the stacktrace of.
     * @param   includeMethodInfo  If <code>true</code> the method information is included.
     *
     * @return  The string-representation of the stacktrace as a string array.
     */
    public static String[] getSimpleErrorTraceElements(Throwable t, boolean includeMethodInfo)
    {
        List<Throwable> exceptionList = new ArrayList<Throwable>(20);
        Throwable tmp = t;

        while (tmp != null)
        {
            exceptionList.add(tmp);
            tmp = tmp.getCause();
        }

        String[] res = new String[exceptionList.size()];
        int i = 0;

        for (Throwable ex : exceptionList)
        {
            StackTraceElement[] elements = ex.getStackTrace();
            String exClassName = ex.getClass().getSimpleName();
            String msg = ex.getMessage();
            String methodName = elements[0].getMethodName();
            int lineNumber = elements[0].getLineNumber();

            if (includeMethodInfo && (methodName != null))
            {
                res[i++] = String.format("%s: %s [%s:%s]", exClassName, msg, methodName,
                                         (lineNumber > 0) ? Integer.toString(lineNumber) : "");
            }
            else
            {
                res[i++] = String.format("%s: %s", exClassName, msg);
            }
        }

        return res;
    }
}
