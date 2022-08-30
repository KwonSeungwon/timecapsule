package com.mini.timecapsule.utils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class PropertyMap extends HashMap<String, Object> {

    private static final long serialVersionUID = 2708203377189112526L;

    /**
     * {@code path}로 표현되는 키의 문자열 값을 {@link BigDecimal}로 변환하여 반환한다.
     * <p>
     * 만약 매칭되는 요소를 찾지 못하면 {@code defaultValue}를 반환한다.
     *
     * @param path         다층 구조인 맵에서 부모-자식의 키들을 도트로 연결한 값. (ex:
     *                     {@code "depth1.depth2.depth3"})
     * @param defaultValue
     * @return {@code path}에 해당하는 값을 {@code BigDecimal}으로 변환한 값.
     * @throws IllegalStateException 해당 값이 {@link String}의 객체가 아닐 때.
     */
    public BigDecimal findAsBigDecimalOrDefault(String path, BigDecimal defaultValue) throws IllegalStateException {
        Object value = findOrDefault(path, defaultValue);

        if (!(value instanceof String)) {
            throw new IllegalStateException("value is not instance of String.class");
        }
        return UNumbers.toBigDecimal(value, defaultValue);
    }

    /**
     * {@code path}로 표현되는 키의 문자열 값을 {@link BigDecimal}로 변환하여 반환한다.
     * <p>
     * 만약 매칭되는 요소를 찾지 못하면 {@code null}을 반환한다.
     *
     * @param path 다층 구조인 맵에서 부모-자식의 키들을 도트로 연결한 값. (ex:
     *             {@code "depth1.depth2.depth3"})
     * @return {@code path}에 해당하는 값을 {@code BigDecimal}으로 변환한 값.
     * @throws IllegalStateException 해당 값이 {@link String}의 객체가 아닐 때.
     */
    public BigDecimal findAsBigDecimalOrNull(String path) {
        return findAsBigDecimalOrDefault(path, null);
    }

    /**
     * {@code path}로 표현되는 키의 문자열 값을 {@link Long}으로 변환하여 반환한다.
     * <p>
     * 만약 매칭되는 요소를 찾지 못하면 {@code defaultValue}를 반환한다.
     *
     * @param path         다층 구조인 맵에서 부모-자식의 키들을 도트로 연결한 값. (ex:
     *                     {@code "depth1.depth2.depth3"})
     * @param defaultValue
     * @return {@code path}에 해당하는 값을 {@code Long}으로 변환한 값.
     * @throws IllegalStateException 해당 값이 {@link String}의 객체가 아닐 때.
     */
    public Long findAsLongOrDefault(String path, Long defaultValue) throws IllegalStateException {
        Object value = findOrDefault(path, defaultValue);

        if (!(value instanceof String)) {
            throw new IllegalStateException("value is not instance of String.class");
        }
        try {
            return value != null ? Long.parseLong((String) value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * {@code path}로 표현되는 키의 문자열 값을 {@link Long}으로 변환하여 반환한다.
     * <p>
     * 만약 매칭되는 요소를 찾지 못하면 {@code null}을 반환한다.
     *
     * @param path 다층 구조인 맵에서 부모-자식의 키들을 도트로 연결한 값. (ex:
     *             {@code "depth1.depth2.depth3"})
     * @return {@code path}에 해당하는 값을 {@code BigDecimal}으로 변환한 값.
     * @throws IllegalStateException 해당 값이 {@link String}의 객체가 아닐 때.
     */
    public Long findAsLongOrNull(String path) {
        return findAsLongOrDefault(path, null);
    }

    /**
     * {@code path}로 표현되는 키의 값을 반환한다.
     * <p>
     * 만약 매칭되는 요소를 찾지 못하면 {@code defaultValue}를 반환한다.
     *
     * @param path         다층 구조인 맵에서 부모-자식의 키들을 도트로 연결한 값. (ex:
     *                     {@code "depth1.depth2.depth3"})
     * @param defaultValue
     * @return {@code path}에 해당하는 값.
     */
    public Object findOrDefault(String path, Object defaultValue) {
        return UCollections.findOrDefault(this, path, defaultValue);
    }

    /**
     * {@code path}로 표현되는 키의 값을 반환한다.
     * <p>
     * 만약 매칭되는 요소를 찾지 못하면 {@code null}을 반환한다.
     *
     * @param path 다층 구조인 맵에서 부모-자식의 키들을 도트로 연결한 값. (ex:
     *             {@code "depth1.depth2.depth3"})
     * @return {@code path}에 해당하는 값.
     */
    public Object findOrNull(String path) {
        return UCollections.findOrNull(this, path);
    }

    /**
     * {@code name}을 키로 하는 문자열 값을 {@link BigDecimal}로 변환하여 반환한다.
     * <p>
     * 만약 매칭되는 요소를 찾지 못하면 {@code null}을 반환한다.
     *
     * @param name
     * @return 값을 {@code BigDecimal}로 변환한 값.
     * @throws IllegalStateException 해당 값이 {@link String}의 객체가 아닐 때.
     */
    public BigDecimal getAsBigDeciaml(String name) {
        return getAsBigDeciamlOrDefault(name, null);
    }

    /**
     * {@code name}을 키로 하는 문자열 값을 {@link BigDecimal}로 변환하여 반환한다.
     * <p>
     * 만약 매칭되는 요소를 찾지 못하면 {@code defaultValue}를 반환한다.
     *
     * @param name
     * @param defaultValue
     * @return 값을 {@code BigDecimal}로 변환한 값.
     * @throws IllegalStateException 해당 값이 {@link String}의 객체가 아닐 때.
     */
    public BigDecimal getAsBigDeciamlOrDefault(String name, BigDecimal defaultValue) throws IllegalStateException {
        Object value = getOrDefault(name, defaultValue);

        if (!(value instanceof String)) {
            throw new IllegalStateException("value is not instance of String.class");
        }
        return UNumbers.toBigDecimal(value, defaultValue);
    }

    /**
     * {@code name}을 키로 하는 문자열 값을 {@link Long}으로 변환하여 반환한다.
     * <p>
     * 만약 매칭되는 요소를 찾지 못하면 {@code null}을 반환한다.
     *
     * @param name
     * @return 값을 {@code Long}으로 변환한 값.
     * @throws IllegalStateException 해당 값이 {@link String}의 객체가 아닐 때.
     */
    public Long getAsLong(String name) {
        return getAsLongOrDefault(name, null);
    }

    /**
     * {@code name}을 키로 하는 문자열 값을 {@link Long}으로 변환하여 반환한다.
     * <p>
     * 만약 매칭되는 요소를 찾지 못하면 {@code defaultValue}를 반환한다.
     *
     * @param name
     * @param defaultValue
     * @return 값을 {@code Long}으로 변환한 값.
     * @throws IllegalStateException 해당 값이 {@link String}의 객체가 아닐 때.
     */
    public Long getAsLongOrDefault(String name, Long defaultValue) throws IllegalStateException {
        Object value = getOrDefault(name, defaultValue);

        if (!(value instanceof String)) {
            throw new IllegalStateException("value is not instance of String.class");
        }
        return value != null ? Long.parseLong((String) value) : defaultValue;
    }

    /**
     * {@code this}에 매개변수인 {@link PropertyMap} 객체를 합친다.
     * <p>
     * 키가 같은 경우, {@code src}의 값으로 대체한다.
     *
     * @param src
     */
    public void merge(PropertyMap src) {
        if (UCollections.isEmpty(src)) {
            return;
        }
        for (String key : src.keySet()) {
            Object srcValue = src.get(key);
            Object destValue = this.get(key);

            if (srcValue instanceof PropertyMap && destValue instanceof PropertyMap) {
                ((PropertyMap) destValue).merge((PropertyMap) srcValue);
            } else {
                put(key, srcValue);
            }
        }
    }

    /**
     * {@code value}를 항상 문자열로 저장한다.
     * <p>
     * JSON으로 역직렬화할 때 숫자의 경우 정확한 타입을 찾지 못하는 경우가 있다. 예컨대, Jackson2 라이브러리는 정수를
     * {@link Integer}로, 실수를 {@link Double}로 변환한다. {@link Long}이나
     * {@link BigDecimal}를 예상하고 형변환을 시도할 경우 {@link ClassCastException}이 발생할 수 있다. 이
     * 경우에는 이 값들을 문자열로 다루는 것이 안전하다.
     * <p>
     * 문자열로 변환할 때,
     * <ul>
     * <li>{@link String}은 그 값을 그대로 사용한다.
     * <li>{@code BigDecimal}은 {@link UNumbers#stringify(BigDecimal)}의 반환값을 사용한다.
     * <li>기타의 경우에는 {@link String#valueOf(Object)}의 반환값을 사용한다.
     * </ul>
     * <p>
     * 문자열들을 꺼내올 때 적절한 값으로의 형변환을 함께 하기 위해 {@code getAs...()}나 {@code findAs...()}
     * 메소드들이 정의되어 있다.
     *
     * @param name
     * @param value
     */
    public void putAsString(String name, Object value) {
        if (value instanceof String) {
            put(name, value);
        } else if (value instanceof BigDecimal) {
            put(name, value != null ? UNumbers.stringify((BigDecimal) value) : null);
        } else {
            put(name, String.valueOf(value));
        }
    }

    /**
     * 매개변수로 전달된 맵들을 합쳐 새로운 맵을 생성한다.
     * <p>
     * 키가 같은 경우, 뒤의 것이 앞의 것을 대체한다.
     *
     * @param maps
     * @return 새 맵.
     */
    public static PropertyMap merge(PropertyMap... maps) {
        PropertyMap dest = new PropertyMap();

        if (UCollections.isEmpty(maps)) {
            return dest;
        }
        for (PropertyMap map : maps) {
            dest.merge(map);
        }
        return dest;
    }

    /**
     * 한 쌍의 키와 값을 가진 맵을 생성한다.
     *
     * @param name
     * @param value
     * @return 새 맵.
     */
    public static PropertyMap newInstance(String name, Object value) {
        PropertyMap map = new PropertyMap();
        map.put(name, value);

        return map;
    }

    /**
     * 키가 문자열인 {@link Map} 객체로부터 새 맵을 만든다.
     * <p>
     * Jackson2가 {@link LinkedHashMap}을 사용하듯, 개별 라이브러리에서 고유 형태의 맵을 사용하는 경우
     * {@link PropertyMap}으로 치환하기 위해 사용된다.
     *
     * @param src
     * @return 새 맵.
     */
    public static PropertyMap from(Map<String, ?> src) {
        PropertyMap map = new PropertyMap();

        if (src != null) {
            map.putAll(src);
        }
        return map;
    }
}

