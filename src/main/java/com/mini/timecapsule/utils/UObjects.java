package com.mini.timecapsule.utils;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class UObjects {
    public UObjects() {
    }

    public static boolean isEqual(Object value1, Object value2) {
        return value1 != null && value2 != null && value1.equals(value2);
    }

    public static String toString(Object object) {
        return ToStringBuilder.reflectionToString(object, ToStringStyle.MULTI_LINE_STYLE);
    }
}
