/**
 * Eclipse JMX Console Copyright (C) 2006 Jeff Mesnil Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License. Code was inspired from
 * org.eclipse.equinox.client source, (c) 2006 IBM
 */
package com.cordys.coe.tools.jmx;

import java.math.BigDecimal;
import java.math.BigInteger;

import java.util.LinkedHashMap;

import javax.management.AttributeNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.eclipse.swt.widgets.Text;

/**
 * DOCUMENTME.
 * 
 * @author $author$
 */
public class MBeanUtils
{
    /**
     * This method gets the value for the given attribute.
     * 
     * @param mbsc The JMX connection.
     * @param objectName The object name for the object.
     * @param attribute The name of the attribute to get the value for.
     * @return The value for the given attribute.
     * @throws Exception In case of any exceptions
     */
    public static Object getAttributeValue(MBeanServerConnection mbsc, ObjectName objectName, String attribute) throws Exception
    {
        Object retVal = null;

        try
        {
            retVal = mbsc.getAttribute(objectName, attribute);
        }
        catch (AttributeNotFoundException anfe)
        {
            // We'll ignore it and just return null.
        }

        return retVal;
    }

    /**
     * This method gets the integer value for the given attribute.
     * 
     * @param mbsc The JMX connection.
     * @param objectName The object name for the object.
     * @param attribute The name of the attribute to get the value for.
     * @return The integer value for the given attribute.
     * @throws Exception In case of any exceptions
     */
    public static int getIntAttributeValue(MBeanServerConnection mbsc, ObjectName objectName, String attribute) throws Exception
    {
        int retVal = -1;

        Object tmp = getAttributeValue(mbsc, objectName, attribute);

        if (tmp != null)
        {
            if (tmp instanceof Integer)
            {
                retVal = (Integer) tmp;
            }
            else
            {
                retVal = Integer.parseInt(tmp.toString());
            }
        }

        return retVal;
    }

    /**
     * This method gets the long value for the given attribute.
     * 
     * @param mbsc The JMX connection.
     * @param objectName The object name for the object.
     * @param attribute The name of the attribute to get the value for.
     * @return The long value for the given attribute.
     * @throws Exception In case of any exceptions
     */
    public static long getLongAttributeValue(MBeanServerConnection mbsc, ObjectName objectName, String attribute)
            throws Exception
    {
        long retVal = -1;

        Object tmp = getAttributeValue(mbsc, objectName, attribute);

        if (tmp != null)
        {
            if (tmp instanceof Long)
            {
                retVal = (Long) tmp;
            }
            else
            {
                retVal = Long.parseLong(tmp.toString());
            }
        }

        return retVal;
    }

    /**
     * DOCUMENTME.
     * 
     * @param val DOCUMENTME
     * @return DOCUMENTME
     */
    public static Number createNumber(String val)
    {
        try
        {
            return new Byte(val);
        }
        catch (NumberFormatException e)
        {
        }

        try
        {
            return new BigDecimal(val);
        }
        catch (NumberFormatException e)
        {
        }

        try
        {
            return new BigInteger(val);
        }
        catch (NumberFormatException e)
        {
        }
        return null;
    }

    /**
     * This method returns a default value for the given type.
     * 
     * @param sType The type of the parameter.
     * @return The default value for it.
     */
    public static String getDefaultValue(String sType)
    {
        String sReturn = "";

        if (sType.equals("byte") || sType.equals("short") || sType.equals("java.lang.Short") || sType.equals("int")
                || sType.equals("java.lang.Integer") || sType.equals("long") || sType.equals("java.lang.Long")
                || sType.equals("float") || sType.equals("java.lang.Float") || sType.equals("double")
                || sType.equals("java.lang.Double") || sType.equals("char"))
        {
            sReturn = "0";
        }
        else if (sType.equals("boolean") || sType.equals("java.lang.Boolean"))
        {
            sReturn = "true";
        }

        return sReturn;
    }

    /**
     * DOCUMENTME.
     * 
     * @param lhmParams DOCUMENTME
     * @param params DOCUMENTME
     * @return DOCUMENTME
     * @throws ClassNotFoundException DOCUMENTME
     */
    public static Object[] getParameters(LinkedHashMap<String, Text> lhmParams, MBeanParameterInfo[] params)
            throws ClassNotFoundException
    {
        if ((lhmParams == null) || (params == null))
        {
            return null;
        }

        Object[] ret = new Object[lhmParams.size()];

        for (int i = 0; (i < ret.length) && (i < params.length); i++)
        {
            MBeanParameterInfo param = params[i];
            String textValue = lhmParams.get(param.getName()).getText();

            Object result = null;

            result = getParameterValue(param, textValue);

            ret[i] = result;
        }
        return ret;
    }

    /**
     * This method returns the proper value for the given parameter.
     * 
     * @param param The MBean parameter details.
     * @param textValue The String value of the parameter.
     * @return The proper return object to match the method's signature.
     * @throws ClassNotFoundException In case of any exceptions.
     */
    public static Object getParameterValue(MBeanParameterInfo param, String textValue) throws ClassNotFoundException
    {
        Object result;

        if (textValue.length() == 0)
        {
            result = null;
        }
        else if (param.getType().equals("byte"))
        { // $NON-NLS-1$
            result = new Byte(textValue);
        }
        else if (param.getType().equals("short"))
        { // $NON-NLS-1$
            result = new Short(textValue);
        }
        else if (param.getType().equals("java.lang.Short"))
        { // $NON-NLS-1$
            result = new Short(textValue);
        }
        else if (param.getType().equals("int"))
        { // $NON-NLS-1$
            result = new Integer(textValue);
        }
        else if (param.getType().equals("java.lang.Integer"))
        { // $NON-NLS-1$
            result = new Integer(textValue);
        }
        else if (param.getType().equals("long"))
        { // $NON-NLS-1$
            result = new Long(textValue);
        }
        else if (param.getType().equals("java.lang.Long"))
        { // $NON-NLS-1$
            result = new Long(textValue);
        }
        else if (param.getType().equals("float"))
        { // $NON-NLS-1$
            result = new Float(textValue);
        }
        else if (param.getType().equals("java.lang.Float"))
        { // $NON-NLS-1$
            result = new Float(textValue);
        }
        else if (param.getType().equals("double"))
        { // $NON-NLS-1$
            result = new Double(textValue);
        }
        else if (param.getType().equals("java.lang.Double"))
        { // $NON-NLS-1$
            result = new Double(textValue);
        }
        else if (param.getType().equals("char"))
        { // $NON-NLS-1$
            result = new Character(textValue.charAt(0));
        }
        else if (param.getType().equals("boolean"))
        { // $NON-NLS-1$
            result = new Boolean(textValue);
        }
        else if (param.getType().equals("java.lang.Boolean"))
        { // $NON-NLS-1$
            result = new Boolean(textValue);
        }
        else if (MBeanUtils.class.getClassLoader().loadClass("java.lang.Number")
                .isAssignableFrom(MBeanUtils.class.getClassLoader().loadClass(param.getType())))
        { // $NON-NLS-1$
            result = createNumber(textValue);
        }
        else
        {
            result = textValue;
        }
        return result;
    }

    /**
     * DOCUMENTME.
     * 
     * @param valueStr DOCUMENTME
     * @param type DOCUMENTME
     * @return DOCUMENTME
     * @throws ClassNotFoundException DOCUMENTME
     */
    public static Object getValue(String valueStr, String type) throws ClassNotFoundException
    {
        if ((valueStr == null) || (type == null))
        {
            return null;
        }

        if (type.equals("byte")) // $NON-NLS-1$
        {
            return new Byte(valueStr);
        }

        if (type.equals("short")) // $NON-NLS-1$
        {
            return new Short(valueStr);
        }

        if (type.equals("java.lang.Short")) // $NON-NLS-1$
        {
            return new Short(valueStr);
        }

        if (type.equals("int")) // $NON-NLS-1$
        {
            return new Integer(valueStr);
        }

        if (type.equals("java.lang.Integer")) // $NON-NLS-1$
        {
            return new Integer(valueStr);
        }

        if (type.equals("long")) // $NON-NLS-1$
        {
            return new Long(valueStr);
        }

        if (type.equals("java.lang.Long")) // $NON-NLS-1$
        {
            return new Long(valueStr);
        }

        if (type.equals("float")) // $NON-NLS-1$
        {
            return new Float(valueStr);
        }

        if (type.equals("java.lang.Float")) // $NON-NLS-1$
        {
            return new Float(valueStr);
        }

        if (type.equals("double")) // $NON-NLS-1$
        {
            return new Double(valueStr);
        }

        if (type.equals("java.lang.Double")) // $NON-NLS-1$
        {
            return new Double(valueStr);
        }

        if (type.equals("char")) // $NON-NLS-1$
        {
            return new Character(valueStr.charAt(0));
        }

        if (type.equals("boolean")) // $NON-NLS-1$
        {
            return new Boolean(valueStr);
        }

        if (MBeanUtils.class.getClassLoader().loadClass("java.lang.Number") // $NON-NLS-1$
                .isAssignableFrom(MBeanUtils.class.getClassLoader().loadClass(type)))
        {
            return createNumber(valueStr);
        }

        return valueStr;
    }

    /**
     * DOCUMENTME.
     * 
     * @param opInfo DOCUMENTME
     * @return DOCUMENTME
     */
    public static String prettySignature(MBeanOperationInfo opInfo)
    {
        StringBuffer sig = new StringBuffer(opInfo.getName());
        MBeanParameterInfo[] params = opInfo.getSignature();
        sig.append('(');

        for (int i = 0; i < params.length; i++)
        {
            if (i > 0)
            {
                sig.append(", "); // $NON-NLS-1$
            }

            MBeanParameterInfo param = params[i];
            sig.append(StringUtils.toString(param.getType(), false));
        }
        sig.append(')');
        return sig.toString();
    }

    /**
     * DOCUMENTME.
     * 
     * @param opInfo DOCUMENTME
     * @return DOCUMENTME
     */
    public static String prettySignature(MBeanNotificationInfo opInfo)
    {
        StringBuffer sig = new StringBuffer(opInfo.getName());
        String[] params = opInfo.getNotifTypes();
        sig.append('(');

        for (int i = 0; i < params.length; i++)
        {
            if (i > 0)
            {
                sig.append(", "); // $NON-NLS-1$
            }

            sig.append(StringUtils.toString(params[i], false));
        }
        sig.append(')');
        return sig.toString();
    }
}
