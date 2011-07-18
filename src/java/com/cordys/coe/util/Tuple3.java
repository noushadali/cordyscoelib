/**
 * (c) 2009 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util;

/**
 * Immutable object which contains three typed object references. This can be safely used as a map
 * key.
 *
 * @param   <T1>  First reference type.
 * @param   <T2>  Second reference type.
 * @param   <T3>  Third reference type.
 *
 * @author  mpoyhone
 */
public class Tuple3<T1, T2, T3> extends Tuple2<T1, T2>
{
    /**
     * Contains the third value.
     */
    protected final T3 value3;

    /**
     * Constructor for Tuple3.
     *
     * @param  value1  first value.
     * @param  value2  second value.
     * @param  value3  third value.
     */
    public Tuple3(T1 value1, T2 value2, T3 value3)
    {
        super(value1, value2);
        this.value3 = value3;
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

        Tuple3<T1, T2, T3> other = (Tuple3<T1, T2, T3>) obj;

        return safeEquals(value3, other.value3);
    }

    /**
     * Returns the value3.
     *
     * @return  Returns the value3.
     */
    public T3 getValue3()
    {
        return value3;
    }

    /**
     * @see  java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = (prime * result) + safeHashcode(value3);
        return result;
    }

    /**
     * @see  java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("Tuple3 [value1=%s, value2=%s, value3=%s]", value1, value2, value3);
    }
}
