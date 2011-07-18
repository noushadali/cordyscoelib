package com.cordys.coe.util.classpath;

import java.io.File;

import java.util.ArrayList;

/**
 * This interface describes the result object for the find class.
 *
 * @author  pgussow
 */
public interface IFindClassResult
{
    /**
     * This method gets the location of the JAR file.
     *
     * @return  The location of the JAR file.
     */
    String getLocation();

    /**
     * This method gets the log output for this search.
     *
     * @return  The log output for this search.
     */
    String getLogOutput();

    /**
     * This method gets the list of files/folders that this class has scanned.
     *
     * @return  The list of files/folders that this class has scanned.
     */
    ArrayList<File> getSearchedFiles();

    /**
     * This method gets the exception that has occurred.
     *
     * @return  The exception that has occurred.
     */
    Throwable getThrowable();

    /**
     * This method gets whether or not the search was executed ok.
     *
     * @return  Whether or not the search was executed ok.
     */
    boolean isOK();
}
