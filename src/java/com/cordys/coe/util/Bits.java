package com.cordys.coe.util;

/**
 * This class contains static methods to check for bitmasks.
 *
 * @author  pgussow
 */
public class Bits
{
    /**
     * Creates a new Bits object.
     */
    private Bits()
    {
        // Empty constructor to prevent instantiation.
    }

    /**
     * This method returns if in the iToCheck the bit is set identified by iBit.
     *
     * @param   iToCheck  The mask to check.
     * @param   iBit      The bit that needs to be checked.
     *
     * @return  true if the bit is set. Otherwise false.
     */
    public static boolean isBitSet(int iToCheck, int iBit)
    {
        return (iToCheck & iBit) == iBit;
    }

    /**
     * DOCUMENTME.
     *
     * @param   toCheck  DOCUMENTME
     * @param   mask     DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    public static boolean matchesMask(int toCheck, int mask)
    {
        return toCheck == (toCheck | mask);
    }

    /**
     * DOCUMENTME.
     *
     * @param   toCheck  DOCUMENTME
     * @param   mask     DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    public static boolean matchesMask(long toCheck, long mask)
    {
        return toCheck == (toCheck | mask);
    }

    /**
     * DOCUMENTME.
     *
     * @param   toCheck  DOCUMENTME
     * @param   mask     DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    public static boolean matchesMask(long toCheck, int mask)
    {
        return toCheck == (toCheck | mask);
    }

    /**
     * DOCUMENTME.
     *
     * @param   target  DOCUMENTME
     * @param   toSet   DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    public static int setBits(int target, int toSet)
    {
        return target | toSet;
    }

    /**
     * DOCUMENTME.
     *
     * @param   target  DOCUMENTME
     * @param   toSet   DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    public static long setBits(long target, long toSet)
    {
        return target | toSet;
    }

    /**
     * DOCUMENTME.
     *
     * @param   target    DOCUMENTME
     * @param   toToggle  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    public static int toggleBits(int target, int toToggle)
    {
        return target ^ toToggle;
    }

    /**
     * DOCUMENTME.
     *
     * @param   target    DOCUMENTME
     * @param   toToggle  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    public static long toggleBits(long target, long toToggle)
    {
        return target ^ toToggle;
    }

    /**
     * DOCUMENTME.
     *
     * @param   target   DOCUMENTME
     * @param   toUnset  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    public static int unsetBits(int target, int toUnset)
    {
        return target & ~toUnset;
    }

    /**
     * DOCUMENTME.
     *
     * @param   target   DOCUMENTME
     * @param   toUnset  DOCUMENTME
     *
     * @return  DOCUMENTME
     */
    public static long unsetBits(long target, long toUnset)
    {
        return target & ~toUnset;
    }
}
