package com.mini.timecapsule.utils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PropertyMap extends HashMap<String, Object> {
    private static final long serialVersionUID = 2708203377189112526L;

    public PropertyMap() {
    }

    public Long getAsLong(String name) {
        return this.getAsLongOrDefault(name, (Long)null);
    }

    public Long getAsLongOrDefault(String name, Long defaultValue) throws IllegalStateException {
        Object value = this.getOrDefault(name, defaultValue);
        if (value instanceof String) {
            throw new IllegalStateException("value is not instance of String.class");
        } else {
            return value != null ? Long.parseLong((String)value) : defaultValue;
        }
    }

    public void merge(PropertyMap src) {
        if (!isEmpty(src)) {
            Iterator var2 = src.keySet().iterator();

            while(true) {
                while(var2.hasNext()) {
                    String key = (String)var2.next();
                    Object srcValue = src.get(key);
                    Object destValue = this.get(key);
                    if (srcValue instanceof PropertyMap && destValue instanceof PropertyMap) {
                        ((PropertyMap)destValue).merge((PropertyMap)srcValue);
                    } else {
                        this.put(key, srcValue);
                    }
                }

                return;
            }
        }
    }

    public static PropertyMap merge(PropertyMap... maps) {
        PropertyMap dest = new PropertyMap();
        if (isEmpty(maps)) {
            return dest;
        } else {
            PropertyMap[] var2 = maps;
            int var3 = maps.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                PropertyMap map = var2[var4];
                dest.merge(map);
            }

            return dest;
        }
    }

    public static PropertyMap newInstance(String name, Object value) {
        PropertyMap map = new PropertyMap();
        map.put(name, value);
        return map;
    }

    public static PropertyMap from(Map<String, ?> src) {
        PropertyMap map = new PropertyMap();
        if (src != null) {
            map.putAll(src);
        }

        return map;
    }

    public static <E> boolean isEmpty(Collection<E> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <K, V> boolean isEmpty(Map<K, V> map) {
        return map == null || map.isEmpty();
    }

    public static <E> boolean isEmpty(E[] array) {
        return array == null || array.length == 0;
    }
}

