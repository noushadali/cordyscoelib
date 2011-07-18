/**
 * (c) 2009 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Immutable object which contains two typed object references. This can be safely used as a map
 * key.
 *
 * @param   <T1>  First reference type.
 * @param   <T2>  Second reference type.
 *
 * @author  mpoyhone
 */
public class Tuple2<T1, T2>
{
    /**
     * Contains the first value.
     */
    protected final T1 value1;
    /**
     * Contains the second value.
     */
    protected final T2 value2;
    
    /**
     * Defines array types.
     *
     * @author mpoyhone
     */
    private enum EArrayType
    {
        BOOLEAN, BYTE, SHORT, CHAR, INT, LONG, FLOAT, DOUBLE
    }

    /**
     * Maps from the array element class to array type.
     */
    private static final Map<Class<?>, EArrayType> arrayTypeMap = new HashMap<Class<?>, EArrayType>();
    
    static {
        arrayTypeMap.put(byte.class, EArrayType.BYTE);
        arrayTypeMap.put(boolean.class, EArrayType.BOOLEAN);
        arrayTypeMap.put(short.class, EArrayType.SHORT);
        arrayTypeMap.put(char.class, EArrayType.CHAR);
        arrayTypeMap.put(int.class, EArrayType.INT);
        arrayTypeMap.put(long.class, EArrayType.LONG);
        arrayTypeMap.put(float.class, EArrayType.FLOAT);
        arrayTypeMap.put(double.class, EArrayType.DOUBLE);
    }

    /**
     * Constructor for Tuple2.
     *
     * @param  value1  first value.
     * @param  value2  second value.
     */
    public Tuple2(T1 value1, T2 value2)
    {
        super();
        this.value1 = value1;
        this.value2 = value2;
    }

    /**
     * @see  java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (obj == null)
        {
            return false;
        }

        if (getClass() != obj.getClass())
        {
            return false;
        }

        Tuple2<T1, T2> other = (Tuple2<T1, T2>) obj;

        if (!safeEquals(value1, other.value1))
        {
            return false;
        }

        if (!safeEquals(value2, other.value2))
        {
            return false;
        }

        return true;
    }

    /**
     * Returns the value1.
     *
     * @return  Returns the value1.
     */
    public T1 getValue1()
    {
        return value1;
    }

    /**
     * Returns the value2.
     *
     * @return  Returns the value2.
     */
    public T2 getValue2()
    {
        return value2;
    }

    /**
     * @see  java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + safeHashcode(value1);
        result = (prime * result) + safeHashcode(value2);
        return result;
    }

    /**
     * @see  java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("Tuple2 [value1=%s, value2=%s]", value1, value2);
    }

    /**
     * This handles both normal and array equals methods.
     *
     * @param   v1  First object
     * @param   v2  Second object.
     *
     * @return  <code>true</code> if objects are equal.
     */
    protected static boolean safeEquals(Object v1, Object v2)
    {
        if (v1 == null)
        {
            return v2 == null;
        }

        Class<?> cls = v1.getClass();

        if (cls.isArray())
        {
            EArrayType type = arrayTypeMap.get(cls.getComponentType());
            
            if (type != null) {
                switch (type) {
                case BOOLEAN : return Arrays.equals((boolean[]) v1, (boolean[]) v2);
                case BYTE : return Arrays.equals((byte[]) v1, (byte[]) v2);
                case SHORT : return Arrays.equals((short[]) v1, (short[]) v2);
                case CHAR : return Arrays.equals((char[]) v1, (char[]) v2);
                case INT : return Arrays.equals((int[]) v1, (int[]) v2);
                case LONG : return Arrays.equals((long[]) v1, (long[]) v2);
                case FLOAT : return Arrays.equals((float[]) v1, (float[]) v2);
                default :
                    throw new IllegalStateException("Unimplemented array type: " + cls);
                }
            }
            
            return Arrays.equals((Object[]) v1, (Object[]) v2);
        }
        
        return v1.equals(v2);
    }

    /**
     * This handles both normal and array hashCode methods.
     *
     * @param   v  Object
     *
     * @return  Object's hashcode.
     */
    protected static int safeHashcode(Object v)
    {
        if (v == null)
        {
            return 0;
        }
        
        Class<?> cls = v.getClass();

        if (cls.isArray())
        {
            EArrayType type = arrayTypeMap.get(cls.getComponentType());
            
            if (type != null) {
                switch (type) {
                case BOOLEAN : return Arrays.hashCode((boolean[]) v);
                case BYTE : return Arrays.hashCode((byte[]) v);
                case SHORT : return Arrays.hashCode((short[]) v);
                case CHAR : return Arrays.hashCode((char[]) v);
                case INT : return Arrays.hashCode((int[]) v);
                case LONG : return Arrays.hashCode((long[]) v);
                case FLOAT : return Arrays.hashCode((float[]) v);
                default :
                    throw new IllegalStateException("Unimplemented array type: " + cls);
                }
            }
            
            return Arrays.hashCode((Object[]) v);
        }

        return v.hashCode();
    }
}
