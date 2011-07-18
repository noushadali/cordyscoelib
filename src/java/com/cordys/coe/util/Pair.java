/**
 * (c) 2007 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util;

/**
 * Class that can contain two values.
 *
 * @author  mpoyhone
 */
public class Pair<T1, T2>
{
    /**
     * First value.
     */
    private T1 first;
    /**
     * Second value.
     */
    private T2 second;

    /**
     * Constructor for Pair.
     */
    public Pair()
    {
    }

    /**
     * Constructor for Pair.
     *
     * @param  first   First value.
     * @param  second  Second value.
     */
    public Pair(T1 first, T2 second)
    {
        super();
        this.first = first;
        this.second = second;
    }

    /**
     * @see  java.lang.Object#equals(java.lang.Object)
     */
    @Override @SuppressWarnings("rawtypes")
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

        final Pair other = (Pair) obj;

        if (first == null)
        {
            if (other.first != null)
            {
                return false;
            }
        }
        else if (!first.equals(other.first))
        {
            return false;
        }

        if (second == null)
        {
            if (other.second != null)
            {
                return false;
            }
        }
        else if (!second.equals(other.second))
        {
            return false;
        }
        return true;
    }

    /**
     * Returns the first.
     *
     * @return  Returns the first.
     */
    public T1 getFirst()
    {
        return first;
    }

    /**
     * Returns the second.
     *
     * @return  Returns the second.
     */
    public T2 getSecond()
    {
        return second;
    }

    /**
     * @see  java.lang.Object#hashCode()
     */
    @Override public int hashCode()
    {
        final int PRIME = 31;
        int result = 1;
        result = (PRIME * result) + ((first == null) ? 0 : first.hashCode());
        result = (PRIME * result) + ((second == null) ? 0 : second.hashCode());
        return result;
    }

    /**
     * Sets the first.
     *
     * @param  first  The first to be set.
     */
    public void setFirst(T1 first)
    {
        this.first = first;
    }

    /**
     * Sets the second.
     *
     * @param  second  The second to be set.
     */
    public void setSecond(T2 second)
    {
        this.second = second;
    }

    /**
     * @see  java.lang.Object#toString()
     */
    @Override public String toString()
    {
        StringBuilder sbRes = new StringBuilder(100);

        sbRes.append("[");

        if (first != null)
        {
            sbRes.append(first.toString());
        }
        else
        {
            sbRes.append("null");
        }

        sbRes.append(", ");

        if (second != null)
        {
            sbRes.append(second.toString());
        }
        else
        {
            sbRes.append("null");
        }

        sbRes.append("]");

        return sbRes.toString();
    }
}
