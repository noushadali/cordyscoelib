package com.cordys.coe.util.jvm;

/**
 * Holds the definition of a Java version.
 *
 * @author  pgussow
 */
public interface IJavaVersionInfo
{
    /**
     * This method returns the display for the given version.
     *
     * @return  The display for the given version.
     */
    String getDisplayName();

    /**
     * This method gets the major.minor for this version.
     *
     * @return  The major.minor for this version.
     */
    String getMajorMinor();
}
