package com.mini.timecapsule.utils;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public abstract class UObjects {


    public static boolean isEqual(Object object1, Object object2) {
        return object1 != null && object2 != null && object1.equals(object2);
    }


    /**
     * {@code value}가 {@code null}이 아니면 {@code value}을 반환하고, {@code null}이면
     * {@code defaultValue}를 반환한다.
     * <p>
     * 따라서 반환값은 언제나 안전하다.
     *
     * @param value
     * @param defaultValue
     * @return {@code value} 또는 {@code defaultValue}
     */
    public static <T> T safe(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * {@link Object#toString()}을 보완하기 위한 메소드.
     * <p>
     * {@link UObject}를 상속할 수 없는 경우 이 메소드를 이용하여 {@code Object.toString()}을 재지정한다.
     *
     * @param object
     * @return 객체의 문자열 표현.
     */
    public static String toString(Object object) {
        return ToStringBuilder.reflectionToString(object, ToStringStyle.MULTI_LINE_STYLE);
    }
}
