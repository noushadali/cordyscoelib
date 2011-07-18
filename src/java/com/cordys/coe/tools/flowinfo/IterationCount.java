package com.cordys.coe.tools.flowinfo;

/**
 * This class wraps the iteration count hierarchy.
 *
 * @author  pgussow
 */
public class IterationCount
    implements Comparable<IterationCount>
{
    /**
     * Holds the child iteration object.
     */
    private IterationCount m_aiChild;
    /**
     * Holds the parent iterator.
     */
    private IterationCount m_aiParent;
    /**
     * Holds the current iteration count.
     */
    private int m_iCount;

    /**
     * Creates a new ActivityIteration object.
     *
     * @param  iCount  The current count.
     */
    public IterationCount(int iCount)
    {
        this(iCount, null);
    }

    /**
     * Creates a new ActivityIteration object.
     *
     * @param  iCount    The iteration count
     * @param  aiParent  The parent count.
     */
    public IterationCount(int iCount, IterationCount aiParent)
    {
        m_iCount = iCount;
        m_aiParent = aiParent;

        if (m_aiParent != null)
        {
            m_aiParent.setChild(this);
        }
    }

    /**
     * This method returns the iteration count hierarchy based on the given string.
     *
     * @param   sKey  The string representation.
     *
     * @return  The proper hierarchy.
     */
    public static IterationCount getInstance(String sKey)
    {
        IterationCount aiReturn = null;

        String[] asCounts = sKey.split(";");

        for (String sCount : asCounts)
        {
            aiReturn = new IterationCount(Integer.parseInt(sCount), aiReturn);
        }

        return aiReturn;
    }

    /**
     * Compares this object with the specified object for order. Returns a negative integer, zero,
     * or a positive integer as this object is less than, equal to, or greater than the specified
     * object.
     *
     * @param   aiOther  The object to compare to.
     *
     * @return
     *
     * @see     java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(IterationCount aiOther)
    {
        int iReturn = 0;

        if (!equals(aiOther))
        {
            // Now we need to determine whether or not they have the same amount of parents.
            IterationCount aiThisCurrent = getParent();

            while ((aiThisCurrent != null) && (aiThisCurrent.getParent() != null))
            {
                aiThisCurrent = aiThisCurrent.getParent();
            }

            IterationCount aiOtherCurrent = getParent();

            while ((aiOtherCurrent != null) && (aiOtherCurrent.getParent() != null))
            {
                aiOtherCurrent = aiOtherCurrent.getParent();
            }

            // We'll compare from the base
            while ((aiThisCurrent != null) && (aiOtherCurrent != null))
            {
                if (aiThisCurrent.getCount() == aiOtherCurrent.getCount())
                {
                    // The same, so we need to continue
                    aiThisCurrent = aiThisCurrent.getChild();
                    aiOtherCurrent = aiOtherCurrent.getChild();
                }
                else
                {
                    if (aiThisCurrent.getCount() < aiOtherCurrent.getCount())
                    {
                        iReturn = -1;
                    }
                    else
                    {
                        iReturn = 1;
                    }
                    break;
                }
            }
        }

        return iReturn;
    }

    /**
     * This method tests if the 2 objects are the same. This is
     *
     * @param   oOther  The object to compare with.
     *
     * @return  true if the objects are the same.
     *
     * @see     java.lang.Object#equals(java.lang.Object)
     */
    @Override public boolean equals(Object oOther)
    {
        boolean bReturn = false;

        if ((oOther != null) && (oOther instanceof IterationCount))
        {
            // Now we need to determine whether or not they have the same amount of parents.
            IterationCount aiThisCurrent = this;

            while ((aiThisCurrent != null) && (aiThisCurrent.getParent() != null))
            {
                aiThisCurrent = aiThisCurrent.getParent();
            }

            IterationCount aiOtherCurrent = (IterationCount) oOther;

            while ((aiOtherCurrent != null) && (aiOtherCurrent.getParent() != null))
            {
                aiOtherCurrent = aiOtherCurrent.getParent();
            }

            // We'll compare from the base
            bReturn = true;

            while ((aiThisCurrent != null) && (aiOtherCurrent != null))
            {
                if (aiThisCurrent.getCount() == aiOtherCurrent.getCount())
                {
                    // The same, so we need to continue
                    aiThisCurrent = aiThisCurrent.getChild();
                    aiOtherCurrent = aiOtherCurrent.getChild();
                }
                else
                {
                    bReturn = false;
                    break;
                }
            }

            if ((aiThisCurrent == null) ||
                    ((aiOtherCurrent == null) &&
                         !((aiThisCurrent == null) && (aiOtherCurrent == null))))
            {
                bReturn = false;
            }
        }
        return bReturn;
    }

    /**
     * This method gets the child object.
     *
     * @return  The child object.
     */
    public IterationCount getChild()
    {
        return m_aiChild;
    }

    /**
     * This method gets the iteration count.
     *
     * @return  The iteration count.
     */
    public int getCount()
    {
        return m_iCount;
    }

    /**
     * This method gets the parent iterator count.
     *
     * @return  The parent iterator count.
     */
    public IterationCount getParent()
    {
        return m_aiParent;
    }

    /**
     * This method sets the child relation.
     *
     * @param  aiChild  The child object.
     */
    public void setChild(IterationCount aiChild)
    {
        m_aiChild = aiChild;
    }

    /**
     * This method returns the string representation of the object.
     *
     * @return  The string representation of the object.
     *
     * @see     java.lang.Object#toString()
     */
    @Override public String toString()
    {
        StringBuilder sbReturn = new StringBuilder();

        IterationCount ic = this;

        while ((ic != null) && (ic.getParent() != null))
        {
            ic = ic.getParent();
        }

        while (ic != null)
        {
            sbReturn.append(ic.getCount());
            ic = ic.getChild();

            if (ic != null)
            {
                sbReturn.append(";");
            }
        }

        return sbReturn.toString();
    }
}
