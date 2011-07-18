package com.cordys.coe.util.wcpproperties;

import java.io.File;

/**
 * This class identifies a source path.
 *
 * @author  pgussow
 */
public class SourcePath
{
    /**
     * Holds the source location.
     */
    private File m_fSourceFile;
    /**
     * Holds the component name.
     */
    private String m_sComponent;

    /**
     * Creates a new SourcePath object.
     *
     * @param  sComponent   The name of the component.
     * @param  fSourceFile  The location of the sources.
     */
    public SourcePath(String sComponent, File fSourceFile)
    {
        m_sComponent = sComponent;
        m_fSourceFile = fSourceFile;
    }

    /**
     * This method gets the component name.
     *
     * @return  The component name.
     */
    public String getComponentName()
    {
        return m_sComponent;
    }

    /**
     * This method gets the locaiton of the sources.
     *
     * @return  The locaiton of the sources.
     */
    public File getLocation()
    {
        return m_fSourceFile;
    }
}
