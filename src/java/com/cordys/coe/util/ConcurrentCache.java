/**
 * (c) 2008 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util;

import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A concurrent cache implementation which is based on the ConcurrentHashMap. Read operations which
 * access an already cached value are not synchronized. Synchronization is only used during value
 * loading and the synchronization is only related to the given key, so read operations for other
 * keys are not are affected.
 *
 * @author  mpoyhone
 */
public class ConcurrentCache<K, V>
{
    /**
     * Contains a synchronization mutex object during value loading.
     */
    private final ConcurrentMap<K, Object> loadMutexMap = new ConcurrentHashMap<K, Object>();
    /**
     * Contains cached values mapped by the key.
     */
    private final ConcurrentMap<K, Entry<K, V>> valueMap = new ConcurrentHashMap<K, Entry<K, V>>();

    /**
     * Clears all entries currently in the cache.
     */
    public void clear()
    {
        List<Entry<K, V>> entries = new ArrayList<Entry<K, V>>(50);

        entries.addAll(valueMap.values());

        for (Entry<K, V> entry : entries)
        {
            if (valueMap.remove(entry.key, entry))
            {
                if (entry.value instanceof ICacheValue)
                {
                    ((ICacheValue) entry.value).onRemove();
                }
            }
        }
    }

    /**
     * Returns a cached value or if no value is found, uses the loader to load a new value. The
     * loading is synchronized over a key-specific mutex, so only the threads which access the same
     * non-loaded key are blocked during load.
     *
     * @param   key         Lookup key.
     * @param   loader      Loaded for loading a new value.
     * @param   loaderArgs  Arguments for the loader.
     *
     * @return  Cached or just loaded value or <code>null</code> if the value could not be loaded.
     */
    public V get(K key, IValueLoader<V> loader, Object... loaderArgs)
    {
        Entry<K, V> entry;

        // Try to get a cached value.
        entry = valueMap.get(key);

        if (entry != null)
        {
            // A cached value was found.
            return entry.value;
        }

        // No value found, so try to insert a mutex object. This might be
        // done by many threads at the same time, but only the first
        // one will succeed. All threads will synchronize over the same mutex,
        // which is linked to this key.
        Object mutex = new Object();
        Object tmp;

        tmp = loadMutexMap.putIfAbsent(key, mutex);

        if (tmp != null)
        {
            // Other thread had already inserted a mutex, so use that one.
            mutex = tmp;
        }

        // Use the loader to create a new value. The loading is synchronized over the
        // shared insert mutex.
        synchronized (mutex)
        {
            // Now that we have the mutex, check if the value has been loaded
            // by another thread (valueMap contains the value) or if the loading
            // has failed (loadMutexMap does not contain the mutex anymore).
            entry = valueMap.get(key);

            if ((entry == null) && loadMutexMap.containsKey(key))
            {
                try
                {
                    // No other thread has loaded the value yet.
                    V value = loader.loadEntry(loaderArgs);

                    // Insert the loaded value.
                    entry = new Entry<K, V>(key, value);
                    valueMap.put(key, entry);
                }
                finally
                {
                    // Finally, remove the mutex.
                    loadMutexMap.remove(key);
                }
            }
        }

        return (entry != null) ? entry.value : null;
    }

    /**
     * Returns a cached value, but does not try to load a new one.
     *
     * @param   key  Lookup key.
     *
     * @return  Cached value or <code>null</code>.
     */
    public V peek(K key)
    {
        Entry<K, V> entry = valueMap.get(key);

        return (entry != null) ? entry.value : null;
    }

    /**
     * Replaces the cached value with the given one.
     *
     * @param   key    Lookup key.
     * @param   value  New value.
     *
     * @return  Old value or <code>null</code>.
     */
    public V put(K key, V value)
    {
        Entry<K, V> newEntry = new Entry<K, V>(key, value);
        Entry<K, V> oldEntry;

        oldEntry = valueMap.put(key, newEntry);

        return (oldEntry != null) ? oldEntry.value : null;
    }

    /**
     * Removes the cached value.
     *
     * @param   key  Lookup key.
     *
     * @return  Cached value.
     */
    public V remove(K key)
    {
        Entry<K, V> entry = valueMap.remove(key);

        if (entry != null)
        {
            if (entry instanceof ICacheValue)
            {
                ((ICacheValue) entry).onRemove();
            }
        }

        return entry.value;
    }

    /**
     * Optional for cache values.
     *
     * @author  mpoyhone
     */
    public interface ICacheValue
    {
        /**
         * Called when the value was loaded.
         */
        void onLoad();

        /**
         * Called when the value was removed from cache.
         */
        void onRemove();
    }

    /**
     * Interface for the cache value loader.
     *
     * @param   <V>  Cache value type.
     *
     * @author  mpoyhone
     */
    public interface IValueLoader<V>
    {
        /**
         * Loads the given value.
         *
         * @param   args  Optional arguments passed by the get method.
         *
         * @return  Loaded value.
         */
        V loadEntry(Object... args);
    }

    /**
     * Cache entry which contains the key and the value.
     *
     * @param   <K>  Cache key type.
     * @param   <V>  Cache value type.
     *
     * @author  mpoyhone
     */
    protected static class Entry<K, V>
    {
        /**
         * DOCUMENTME.
         */
        protected final K key;
        /**
         * DOCUMENTME.
         */
        protected final V value;

        /**
         * Creates a new Entry object.
         *
         * @param  key    DOCUMENTME
         * @param  value  DOCUMENTME
         */
        protected Entry(K key, V value)
        {
            this.key = key;
            this.value = value;
        }
    }
}
