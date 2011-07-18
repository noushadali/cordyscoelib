/**
 * (c) 2006 Cordys R&D B.V. All rights reserved. The computer program(s) is the
 * proprietary information of Cordys B.V. and provided under the relevant
 * License Agreement containing restrictions on use and disclosure. Use is
 * subject to the License Agreement.
 */
package com.cordys.coe.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Collections related utils.
 *
 * @author  mpoyhone
 */
public class CollectionUtils
{
    /**
     * Type-safe version of arrayToHashMap for converting the pairs to a map.
     *
     * <p>This version takes the destination map instead of creating a new one.</p>
     *
     * @param  destMap   Keys and values are put into this map.
     * @param  mappings  Array of arrays containing the keys and values.
     */
    public static <K, V> void appendToMap(Map<K, V> destMap, Pair<K, V>... mappings)
    {
        for (Pair<K, V> m : mappings)
        {
            destMap.put(m.getFirst(), m.getSecond());
        }
    }

    /**
     * Appends the given values to the set.
     *
     * @param  resSet  Elements are added to this set.
     * @param  tElems  Elements to be added
     */
    public static <T> void appendToSet(Set<T> resSet, T... tElems)
    {
        for (T tElem : tElems)
        {
            resSet.add(tElem);
        }
    }

    /**
     * Converts the given array or key/value arrays to a hash map. This method can be used to initialize static member
     * fields easily:
     *
     * <pre>
        private static final Map<String, String> myMap =
                 CollectionUtils.arrayToHashMap( new Object[][] {
                                                   { "key1", "value1" },
                                                   { "key2", "value2" }
                                                 });

     * </pre>
     *
     * @param   oaaArray  Array of arrays containing the keys and values.
     *
     * @return  Created hash map initialized with the given mappings.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Map arrayToHashMap(Object[]... oaaArray)
    {
        Map mResMap = new HashMap();

        for (Object[] oaMapping : oaaArray)
        {
            mResMap.put(oaMapping[0], oaMapping[1]);
        }

        return mResMap;
    }

    /**
     * Creates a hash set with the given values. This method can be used to initialize static member fields easily:
     *
     * <pre>
        private static final Set<String> mySet =
                arrayToHashSet(new String[] {
                            "value1",
                            "value2"
                         });
     * </pre>
     *
     * @param   tElems  Elements to be added
     *
     * @return  Created hash set initialized with the given values.
     */
    public static <T> Set<T> arrayToHashSet(T... tElems)
    {
        Set<T> sResSet = new HashSet<T>();

        appendToSet(sResSet, tElems);

        return sResSet;
    }

    /**
     * Converts the given array or key/value arrays to a map.
     *
     * <p>This version takes the destination map instead of creating a new one.</p>
     *
     * @param  destMap   Keys and values are put into this map.
     * @param  oaaArray  Array of arrays containing the keys and values.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void arrayToMap(Map destMap, Object[]... oaaArray)
    {
        for (Object[] oaMapping : oaaArray)
        {
            destMap.put(oaMapping[0], oaMapping[1]);
        }
    }

    /**
     * Type-safe version of arrayToHashMap for converting the pairs to a hash map. This method can be used to initialize
     * static member fields easily:
     *
     * <pre>
        private static final Map<String, String> myMap =
            CollectionUtils.pairsToHashMap(new Pair("key1", "value1"),
                                           new Pair("key2", "value2"));
     * </pre>
     *
     * @param   mappings  Array of arrays containing the keys and values.
     *
     * @return  Created hash map initialized with the given mappings.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <K, V> Map<K, V> pairsToHashMap(Pair<K, V>... mappings)
    {
        Map mResMap = new HashMap();

        for (Pair<K, V> m : mappings)
        {
            mResMap.put(m.getFirst(), m.getSecond());
        }

        return mResMap;
    }
}
