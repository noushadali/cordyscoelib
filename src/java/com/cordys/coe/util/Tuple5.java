/**
 * (c) 2009 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util;

/**
 * Immutable object which contains five typed object references. This can be safely used as a map
 * key.
 *
 * @param   <T1>  First reference type.
 * @param   <T2>  Second reference type.
 * @param   <T3>  Third reference type.
 * @param   <T4>  Fourth reference type.
 * @param   <T5>  Fifth reference type.
 *
 * @author  mpoyhone
 */
public class Tuple5<T1, T2, T3, T4, T5> extends Tuple4<T1, T2, T3, T4>
{
    /**
     * Contains the fifth value.
     */
    protected final T5 value5;

    /**
     * Constructor for Tuple5.
     *
     * @param  value1  first value.
     * @param  value2  second value.
     * @param  value3  third value.
     * @param  value4  fourth value.
     * @param  value5  fifth value.
     */
    public Tuple5(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5)
    {
        super(value1, value2, value3, value4);
        this.value5 = value5;
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

        if (!super.equals(obj))
        {
            return false;
        }

        if (getClass() != obj.getClass())
        {
            return false;
        }

        Tuple5<T1, T2, T3, T4, T5> other = (Tuple5<T1, T2, T3, T4, T5>) obj;

        return safeEquals(value5, other.value5);
    }

    /**
     * Returns the value5.
     *
     * @return  Returns the value5.
     */
    public T5 getValue5()
    {
        return value5;
    }

    /**
     * @see  java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = (prime * result) + safeHashcode(value5);
        return result;
    }

    /**
     * @see  java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("Tuple4 [value1=%s, value2=%s, value3=%s, value4=%s, value5=%s]",
                             value1, value2, value3, value4, value5);
    }
}
