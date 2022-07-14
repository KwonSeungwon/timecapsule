package com.mini.timecapsule.utils;


import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class UStrings {
    public UStrings() {
    }

    public static String concat(Object... values) {
        return StringUtils.join(values);
    }

    public static String concat(Collection<?> collection) {
        return StringUtils.join(new Collection[]{collection});
    }

    public static String insert(String text, String sep, int... indexes) {
        if (indexes != null && indexes.length != 0) {
            List<String> result = new ArrayList();
            int start = 0;

            for(int i = 0; i < indexes.length; ++i) {
                int end = indexes[i];
                result.add(text.substring(start, end));
                start = end;
            }

            result.add(text.substring(start));
            return join((Collection)result, sep);
        } else {
            return text;
        }
    }

    public static boolean isBlank(String text) {
        return StringUtils.isBlank(text);
    }

    public static boolean isEmpty(String text) {
        return StringUtils.isEmpty(text);
    }

    public static boolean isEqual(String text1, String text2) {
        return StringUtils.equals(text1, text2);
    }

    public static boolean isMatched(String text, String pattern) {
        return text != null && pattern != null && Pattern.matches(pattern, text);
    }

    public static boolean isValid(String text) {
        return StringUtils.isNotBlank(text);
    }

    public static String join(Object[] values, String separator) {
        return StringUtils.join(values, separator);
    }

    public static String join(Collection<?> collection, String separator) {
        return StringUtils.join(collection, separator);
    }

    public static String mask(String text, int expose, String ofPattern, String hiddenBy) {
        return isValid(text) && text.length() > expose ? concat(text.substring(0, expose), text.substring(expose).replaceAll(ofPattern, hiddenBy)) : text;
    }

    public static String maskEmail(String email, int expose, String ofPattern, String hiddenBy) {
        String[] values = splitIntoArray(email, "@");
        if (isEmpty(values)) {
            return mask(email, expose, ofPattern, hiddenBy);
        } else {
            values[0] = mask(values[0], expose, ofPattern, hiddenBy);
            return join((Object[])values, "@");
        }
    }

    public static String pad(String text, int length, String by) {
        if (text.length() > length) {
            throw new IllegalArgumentException("length of text is smaller than parameterized length");
        } else {
            return concat(buildPadding(length - text.length(), by), text);
        }
    }

    public static String padByZero(String text, int length) {
        return pad(text, length, "0");
    }

    public static String padBack(String text, int length, String by) {
        if (text.length() > length) {
            throw new IllegalArgumentException("length of text is smaller than parameterized length");
        } else {
            return concat(text, buildPadding(length - text.length(), by));
        }
    }

    private static String buildPadding(int length, String by) {
        int times = length / by.length() + 1;
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < times; ++i) {
            builder.append(by);
        }

        return builder.toString().substring(0, length);
    }

    public static String[] splitIntoArray(String text, String pattern) {
        return isBlank(text) ? new String[0] : text.trim().split(pattern);
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
