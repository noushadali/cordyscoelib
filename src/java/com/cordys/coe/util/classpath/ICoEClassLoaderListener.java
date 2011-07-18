package com.cordys.coe.util.classpath;

/**
 * This interface describes the methods that are called from the CoEClassLoader.
 *
 * @author  pgussow
 */
public interface ICoEClassLoaderListener
{
    /**
     * This method is called when a class is loaded by the classloader.
     *
     * @param  sFQN                     The fully qualified name of the class that was loaded.
     * @param  cpeSource                The ClassPathEntry from which it was loaded.
     * @param  bLoadedByCoEClassLoader  Whether or not the class was loaded by the CoEClassLoader.
     */
    void classLoaded(String sFQN, ClassPathEntry cpeSource, boolean bLoadedByCoEClassLoader);
}
