/* 
 * Copyright (C), 2014-2016, 时代际客(深圳)软件有限公司
 * File Name: @(#)MapUtil.java
 * Encoding UTF-8
 * Author: pengyuanbing
 * Version: 3.0
 * Date: 2016年1月29日
 */
package com.iokays.orientdb.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 功能描述
 * 
 * <p>
 * <a href="MapUtil.java"><i>View Source</i></a>
 * </p>
 * 
 * @author pengyuanbing
 * @version 3.0
 * @since 1.0
 */
public final class MapUtil {
    
    private MapUtil() {}
    
    /**
     * 
     * Map 值 排序
     * @param map value
     * @param <K> key
     * @param <V> value
     * @return map
     */
    @SuppressWarnings("hiding")
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
