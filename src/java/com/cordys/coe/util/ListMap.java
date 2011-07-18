/**
 * (c) 2007 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Map.Entry;

import java.util.Set;

/**
 * Hash map that can have multiple values mapped by the key. The values are kept in a list.
 *
 * @author  mpoyhone
 */
public class ListMap<K, V>
{
    /**
     * List implementation class.
     */
    private Class<? extends Collection<V>> listClass;
    /**
     * The actual map.
     */
    private HashMap<K, Collection<V>> map;

    /**
     * Constructor for ListHashMap.
     *
     * <p>Uses ArrayList value lists.</p>
     */
    public ListMap()
    {
        this(ArrayList.class);
    }

    /**
     * Constructor for ListHashMap.
     *
     * <p>Uses the given list class in value lists.</p>
     *
     * @param  listClass  List implementation class.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public ListMap(Class<? extends Collection> listClass)
    {
        this.listClass = (Class<? extends Collection<V>>) listClass;
        this.map = new HashMap<K, Collection<V>>();
    }

    /**
     * @see  java.util.HashMap#clear()
     */
    public void clear()
    {
        map.clear();
    }

    /**
     * @see  java.lang.Object#clone()
     */
    @Override
    @SuppressWarnings("unchecked")
    public ListMap<K, V> clone()
    {
        ListMap<K, V> res = new ListMap<K, V>();

        res.listClass = listClass;
        res.map = (HashMap<K, Collection<V>>) map.clone();

        return res;
    }

    /**
     * Checks if the map contains the given key and value.
     *
     * @param   key    Key.
     * @param   value  Value.
     *
     * @return  <code>true</code> if this map contains the given key and the value.
     */
    public boolean contains(K key, V value)
    {
        Collection<V> values = get(key);

        return (values != null) && values.contains(value);
    }

    /**
     * Checks if the map contains the given key.
     *
     * @param   key  Key.
     *
     * @return  <code>true</code> if this map contains the given key.
     */
    public boolean containsKey(K key)
    {
        return map.containsKey(key);
    }

    /**
     * @see  java.util.HashMap#containsValue(java.lang.Object)
     */
    public boolean containsValue(V value)
    {
        for (Collection<V> l : map.values())
        {
            if (l.contains(value))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * @see  java.util.HashMap#entrySet()
     */
    public Set<Entry<K, Collection<V>>> entrySet()
    {
        return map.entrySet();
    }

    /**
     * @see  java.util.AbstractMap#equals(java.lang.Object)
     */
    @Override @SuppressWarnings("rawtypes")
    public boolean equals(Object o)
    {
        if (!(o instanceof ListMap))
        {
            return false;
        }

        return map.equals(((ListMap) o).map);
    }

    /**
     * @see  java.util.HashMap#get(java.lang.Object)
     */
    public Collection<V> get(K key)
    {
        return map.get(key);
    }

    /**
     * @see  java.util.AbstractMap#hashCode()
     */
    @Override public int hashCode()
    {
        return map.hashCode();
    }

    /**
     * @see  java.util.HashMap#keySet()
     */
    public Set<K> keySet()
    {
        return map.keySet();
    }

    /**
     * @see  java.util.HashMap#put(java.lang.Object, java.lang.Object)
     */
    public void put(K key, V value)
    {
        getValueList(key, true).add(value);
    }

    /**
     * @see  java.util.HashMap#putAll(java.util.Map)
     */
    public void putAll(Map<? extends K, ? extends V> m)
    {
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet())
        {
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * @see  java.util.HashMap#remove(java.lang.Object)
     */
    public Collection<V> remove(Object key)
    {
        return map.remove(key);
    }

    /**
     * Removes the value from this map.
     *
     * @param   key    Key.
     * @param   value  Value.
     *
     * @return  <code>true</code> if the value existed with the given key.
     */
    public boolean remove(K key, V value)
    {
        Collection<V> values = get(key);

        return (values != null) && values.remove(value);
    }

    /**
     * @see  java.util.HashMap#size()
     */
    public int size()
    {
        return map.size();
    }

    /**
     * @see  java.util.AbstractMap#toString()
     */
    @Override public String toString()
    {
        return map.toString();
    }

    /**
     * @see  java.util.HashMap#values()
     */
    public Collection<V> values()
    {
        Collection<V> res = new ArrayList<V>(map.size() * 2);

        for (Collection<V> l : map.values())
        {
            res.addAll(l);
        }

        return res;
    }

    /**
     * Returns the list for the given key. If values are mapped for this key, <code>null</code> if returned.
     *
     * @param   key  Key
     *
     * @return  List of values or <code>null</code> if list does not exist or is empty.
     */
    public Collection<V> getNonEmpty(K key)
    {
        Collection<V> res = get(key);

        if ((res != null) && (res.size() > 0))
        {
            return res;
        }

        return null;
    }

    /**
     * @see  java.util.HashMap#isEmpty()
     */
    public boolean isEmpty()
    {
        for (Collection<V> l : map.values())
        {
            if (!l.isEmpty())
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the value list.
     *
     * @param   key     Key
     * @param   create  If <code>true</code> and the list does not exist, a new list is created.
     *
     * @return  Value list.
     */
    @SuppressWarnings("cast")
    private Collection<V> getValueList(K key, boolean create)
    {
        Collection<V> list = map.get(key);

        if ((list == null) && create)
        {
            try
            {
                list = (List<V>) listClass.newInstance();
                map.put(key, list);
            }
            catch (Exception e)
            {
                // This should never happen.
                throw new IllegalStateException("Unable to create list.", e);
            }
        }

        return list;
    }
}
