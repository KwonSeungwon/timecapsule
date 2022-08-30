package com.mini.timecapsule.utils;

import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 배열이나 콜렉센, 맵 등을 다룰 때 유용한 유틸리티 클래스.
 * <p>
 * 다음 {@link Collection}이나 {@link Map} 및 그 구현체들을 다룬다.
 * <p>
 * <ul>
 * <li>{@link List}와 이를 구현한 {@link ArrayList}
 * <li>{@link Set}와 이를 구현한 {@link HashSet}
 * <li>{@link HashMap}와 이를 구현한 {@link HashMap}
 * </ul>
 */
@Component
public abstract class UCollections {

	/**
	 * 배열에 특정 요소가 존재하는 지 여부.
	 * <p>
	 * {@code array}와 {@code item} 중 하나 이상이 {@code null}이면 {@code false}를 반환한다.
	 *
	 * @param <E>   요소의 타입.
	 * @param array
	 * @param item
	 * @return 요소의 존부.
	 */
	public static <E> boolean contains(E[] array, E item) {
		if (array == null || item == null) {
			return false;
		}
		for (Object element : array) {
			if (item.equals(element)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * {@link Collection}의 객체에 특정 요소가 존재하는 지 여부.
	 * <p>
	 * {@code collection}과 {@code item} 중 하나 이상이 {@code null}이면 {@code false}를 반환한다.
	 *
	 * @param <E>        요소의 타입.
	 * @param collection
	 * @param item
	 * @return 요소의 존부.
	 */
	public static <E> boolean contains(Collection<E> collection, E item) {
		return collection != null && collection.contains(item);
	}

	/**
	 * 배열에 {@code matchable}과 매칭되는 특정 요소가 존재하는 지 여부.
	 * <p>
	 * {@code array}와 {@code matchable} 중 하나 이상이 {@code null}이면 {@code false}를 반환한다.
	 *
	 * @param <E>       요소의 타입.
	 * @param array
	 * @param matchable {@link Matchable}의 객체.
	 * @return 요소의 존부.
	 */
	public static <E> boolean matches(E[] array, Matchable<E> matchable) {
		return findFirstOrNull(array, matchable) != null;
	}

	/**
	 * {@link Collection}의 객체에 {@code matchable}과 매칭되는 특정 요소가 존재하는 지 여부.
	 * <p>
	 * {@code collection}과 {@code matchable} 중 하나 이상이 {@code null}이면 {@code false}를
	 * 반환한다.
	 *
	 * @param <E>        요소의 타입.
	 * @param collection
	 * @param matchable  {@link Matchable}의 객체.
	 * @return 요소의 존부.
	 */
	public static <E> boolean matches(Collection<E> collection, Matchable<E> matchable) {
		return findFirstOrNull(collection, matchable) != null;
	}

	/**
	 * 배열에 매개변수로 전달된 요소가 모두 포함되는 지 여부.
	 * <p>
	 * {@code array}가 {@code null}이거나 비었으면 {@code false}를 반환한다.
	 *
	 * @param <E>      요소의 타입.
	 * @param array
	 * @param elements
	 * @return 요소의 존부.
	 */
	@SafeVarargs
	public static <E> boolean containsAll(E[] array, E... elements) {
		if (isEmpty(array) || isEmpty(elements)) {
			return false;
		}
		for (Object element : elements) {
			if (!contains(array, element)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * {@link Collection} 객체에 매개변수로 전달된 요소가 모두 포함되는 지 여부.
	 * <p>
	 * {@code collection}가 {@code null}이거나 비었으면 {@code false}를 반환한다.
	 *
	 * @param <E>        요소의 타입.
	 * @param collection
	 * @param items
	 * @return 요소의 존부.
	 */
	@SafeVarargs
	public static <E> boolean containsAll(Collection<E> collection, E... items) {
		if (isEmpty(collection) || isEmpty(items)) {
			return false;
		}
		for (E item : items) {
			if (!contains(collection, item)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 배열에 매개변수로 전달된 요소 중 어느 하나라도 포함되는 지 여부.
	 * <p>
	 * {@code array}가 {@code null}이거나 비었으면 {@code false}를 반환한다.
	 *
	 * @param <E>      요소의 타입.
	 * @param array
	 * @param elements
	 * @return 요소의 존부.
	 */
	@SafeVarargs
	public static <E> boolean containsAny(E[] array, E... elements) {
		if (isEmpty(array) || isEmpty(elements)) {
			return false;
		}
		for (Object element : elements) {
			if (contains(array, element)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * {@link Collection} 객체에 매개변수로 전달된 요소가 하나라도 포함되는 지 여부.
	 * <p>
	 * {@code collection}가 {@code null}이거나 비었으면 {@code false}를 반환한다.
	 *
	 * @param <E>        요소의 타입.
	 * @param collection
	 * @param items
	 * @return 요소의 존부.
	 */
	@SafeVarargs
	public static <E> boolean containsAny(Collection<E> collection, E... items) {
		if (isEmpty(collection) || isEmpty(items)) {
			return false;
		}
		for (E item : items) {
			if (contains(collection, item)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 배열이 매개변수로 전달된 요소를 전부 포함하고 다른 요소는 포함하지 않는지 여부.
	 * <p>
	 * {@code array}가 {@code null}이거나 비었으면 {@code false}를 반환한다.
	 *
	 * @param <E>      요소의 타입.
	 * @param array
	 * @param elements
	 * @return 요소의 존부.
	 */
	@SafeVarargs
	public static <E> boolean containsOnly(E[] array, E... elements) {
		return containsAll(array, elements) && array.length == elements.length;
	}

	/**
	 * {@link Collection} 객체가 전달된 요소를 전부 포함하고 다른 요소는 포함하지 않는지 여부.
	 * <p>
	 * {@code collection}가 {@code null}이거나 비었으면 {@code false}를 반환한다.
	 *
	 * @param <E>        요소의 타입.
	 * @param collection
	 * @param items
	 * @return 요소의 존부.
	 */
	@SafeVarargs
	public static <E> boolean containsOnly(Collection<E> collection, E... items) {
		return containsAll(collection, items) && collection.size() == items.length;
	}

	/**
	 * {@code key}가 {@link Map} 객체의 키 중 하나인지 여부.
	 * <p>
	 * {@code map}이 {@code null}이거나 비었으면 {@code false}를 반환한다.
	 *
	 * @param <K> 키의 타입.
	 * @param <V> 값의 타입.
	 * @param map
	 * @param key
	 * @return 키 여부.
	 */
	public static <K, V> boolean containsKey(Map<K, V> map, K key) {
		return map != null && map.containsKey(key);
	}

	/**
	 * {@code value}가 {@link Map} 객체의 값 중 하나인지 여부.
	 * <p>
	 * {@code map}이 {@code null}이거나 비었으면 {@code false}를 반환한다.
	 *
	 * @param <K>   키의 타입.
	 * @param <V>   값의 타입.
	 * @param map
	 * @param value
	 * @return 값 여부.
	 */
	public static <K, V> boolean containsValue(Map<K, V> map, V value) {
		return map != null && map.containsValue(value);
	}

	/**
	 * 배열에서 {@code matchable}과 매칭되는 요소들로 구성된 {@link List}의 객체를 생성한다.
	 *
	 * @param <E>       요소의 타입.
	 * @param array
	 * @param matchable {@link Matchable} 객체.
	 * @return 생성된 {@code List} 객체.
	 */
	public static <E> List<E> findAll(E[] array, Matchable<E> matchable) {
		List<E> results = new ArrayList<>();

		if (isEmpty(array) || matchable == null) {
			return results;
		}
		for (E element : array) {
			if (matchable.matches(element)) {
				results.add(element);
			}
		}
		return results;
	}

	/**
	 * {@link Collection} 객체에서 {@code matchable}과 매칭되는 요소들로 구성된 {@link List}의 객체를
	 * 생성한다.
	 *
	 * @param <E>       요소의 타입.
	 * @param array
	 * @param matchable {@link Matchable} 객체.
	 * @return 생성된 {@code List} 객체.
	 */
	public static <E> List<E> findAll(Collection<E> collection, Matchable<E> matchable) {
		List<E> results = new ArrayList<>();

		if (isEmpty(collection) || matchable == null) {
			return results;
		}
		for (E item : collection) {
			if (matchable.matches(item)) {
				results.add(item);
			}
		}
		return results;
	}

	/**
	 * 값이 {@code matchable}과 매칭되는 모든 요소들과 그 키로 구성된 {@link Map}의 객체를 생성한다.
	 *
	 * @param <K>       키의 타입.
	 * @param <V>       값의 타입.
	 * @param map
	 * @param matchable {@link Matchable}의 객체.
	 * @return 생성된 {@link Map}의 객체.
	 */
	public static <K, V> Map<K, V> findAll(Map<K, V> map, Matchable<V> matcher) {
		Map<K, V> results = new HashMap<>();

		for (K key : map.keySet()) {
			V value = map.get(key);

			if (matcher.matches(value)) {
				results.put(key, value);
			}
		}
		return results;
	}

	/**
	 * 키가 {@code matchable}과 매칭되는 모든 요소들과 그 값으로 구성된 {@link Map}의 객체를 생성한다.
	 *
	 * @param <K>       키의 타입.
	 * @param <V>       값의 타입.
	 * @param map
	 * @param matchable {@link Matchable}의 객체.
	 * @return 생성된 {@link Map}의 객체.
	 */
	public static <K, V> Map<K, V> findAllOfKey(Map<K, V> map, Matchable<K> matcher) {
		Map<K, V> results = new HashMap<>();

		for (K key : map.keySet()) {
			if (matcher.matches(key)) {
				results.put(key, map.get(key));
			}
		}
		return results;
	}

	/**
	 * 배열에서 {@code matchable}과 매칭되는 첫번째 요소를 반환한다.
	 * <p>
	 * 만약 매칭되는 요소를 찾지 못하면 {@code defaultValue}를 반환한다.
	 *
	 * @param <E>          요소의 타입.
	 * @param array
	 * @param matchable    {@link Matchable}의 객체.
	 * @param defaultValue
	 * @return 매칭되는 첫번째 요소.
	 */
	public static <E> E findFirstOrDefault(E[] array, Matchable<E> matchable, E defaultValue) {
		if (isEmpty(array) || matchable == null) {
			return defaultValue;
		}
		for (E element : array) {
			if (matchable.matches(element)) {
				return element;
			}
		}
		return defaultValue;
	}

	/**
	 * 배열에서 {@code matchable}과 매칭되는 첫번째 요소를 반환한다.
	 * <p>
	 * 만약 매칭되는 요소를 찾지 못하면 {@code null}을 반환한다.
	 *
	 * @param <E>       요소의 타입.
	 * @param array
	 * @param matchable {@link Matchable}의 객체.
	 * @return 매칭되는 첫번째 요소.
	 */
	public static <E> E findFirstOrNull(E[] array, Matchable<E> matchable) {
		return findFirstOrDefault(array, matchable, null);
	}

	/**
	 * {@link Collection} 객체에서 {@code matchable}과 매칭되는 첫번째 요소를 반환한다.
	 * <p>
	 * 만약 매칭되는 요소를 찾지 못하면 {@code defaultValue}를 반환한다.
	 *
	 * @param <E>          요소의 타입.
	 * @param collection
	 * @param matchable    {@link Matchable}의 객체.
	 * @param defaultValue
	 * @return 매칭되는 첫번째 요소.
	 */
	public static <E> E findFirstOrDefault(Collection<E> collection, Matchable<E> matchable, E defaultValue) {
		if (isEmpty(collection) || matchable == null) {
			return defaultValue;
		}
		for (E item : collection) {
			if (matchable.matches(item)) {
				return item;
			}
		}
		return defaultValue;
	}

	/**
	 * {@link Collection} 객체에서 {@code matchable}과 매칭되는 첫번째 요소를 반환한다.
	 * <p>
	 * 만약 매칭되는 요소를 찾지 못하면 {@code null}을 반환한다.
	 *
	 * @param <E>        요소의 타입.
	 * @param collection
	 * @param matchable  {@link Matchable}의 객체.
	 * @return 매칭되는 첫번째 요소.
	 */
	public static <E> E findFirstOrNull(Collection<E> collection, Matchable<E> matchable) {
		return findFirstOrDefault(collection, matchable, null);
	}

	/**
	 * 키가 문자열인 {@link Map} 객체에서 {@code path}로 표현되는 키의 값을 반환한다.
	 * <p>
	 * 만약 매칭되는 요소를 찾지 못하면 {@code defaultValue}를 반환한다.
	 *
	 * @param map
	 * @param path         다층 구조인 맵에서 부모-자식의 키들을 도트로 연결한 값. (ex:
	 *                     "depth1.depth2.depth3")
	 * @param defaultValue
	 * @return {@code path}에 해당하는 값.
	 */
	public static Object findOrDefault(Map<String, ?> map, String path, Object defaultValue) {
		if (isEmpty(map) || UStrings.isBlank(path)) {
			return defaultValue;
		}
		return find(map, UStrings.splitIntoArray(path, "\\."), 0, defaultValue);
	}

	/**
	 * 키가 문자열인 {@link Map} 객체에서 {@code path}로 표현되는 키의 값을 반환한다.
	 * <p>
	 * 만약 매칭되는 요소를 찾지 못하면 {@code null}을 반환한다.
	 *
	 * @param map
	 * @param path 다층 구조인 맵에서 부모-자식의 키들을 도트로 연결한 값. (ex: "depth1.depth2.depth3")
	 * @return {@code path}에 해당하는 값.
	 */
	public static Object findOrNull(Map<String, ?> map, String path) {
		return findOrDefault(map, path, null);
	}

	private static Object find(Map<?, ?> map, String[] paths, int depth, Object defaultValue) {
		Object value = map.get(paths[depth++]);

		if (paths.length == depth) {
			return value;
		}
		if (value != null && value instanceof Map<?, ?>) {
			return find((Map<?, ?>) value, paths, depth, defaultValue);
		}
		return defaultValue;
	}

	/**
	 * 배열의 마지막 요소를 반환한다.
	 * <p>
	 * 베열이 {@code null}이거나 비었으면 {@code null}을 반환한다.
	 *
	 * @param array
	 * @return 마지막 요소
	 */
	public static <E> E getLast(E[] array) {
		return isEmpty(array) ? null : array[array.length - 1];
	}

	/**
	 * 목록의 마지막 요소를 반환한다.
	 * <p>
	 * 목록이 {@code null}이거나 비었으면 {@code null}을 반환한다.
	 *
	 * @param collection
	 * @return 마지막 요소
	 */
	public static <E> E getLast(List<E> collection) {
		return isEmpty(collection) ? null : collection.get(collection.size() - 1);
	}

	/**
	 * 빈 문자열인지 여부.
	 * <p>
	 * {@code array}가 {@code null}일 경우 {@code false}를 반환한다.
	 *
	 * @param <E>   요소의 타입.
	 * @param array
	 * @return 빈 문자열인지 여부.
	 */
	public static <E> boolean isEmpty(E[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * 빈 {@link Collection} 객체인지 여부.
	 * <p>
	 * {@code collection}이 {@code null}일 경우 {@code false}를 반환한다.
	 *
	 * @param <E>        요소의 타입.
	 * @param collection
	 * @return
	 */
	public static <E> boolean isEmpty(Collection<E> collection) {
		return collection == null || collection.isEmpty();
	}

	/**
	 * 빈 {@link Map} 객체인지 여부.
	 * <p>
	 * {@code map}이 {@code null}일 경우 {@code false}를 반환한다.
	 *
	 * @param <K> 키의 타입.
	 * @param <V> 값의 타입.
	 * @param map
	 * @return
	 */
	public static <K, V> boolean isEmpty(Map<K, V> map) {
		return map == null || map.isEmpty();
	}

	/**
	 * 두 배열의 일치 여부.
	 * <p>
	 * (1) 두 배열이 모두 {@code null}이 아니고, (2) 요소의 개수가 같으며, (3) 각 인덱스의 요소도 같아야 한다.
	 *
	 * @param array1
	 * @param array2
	 * @return 두 값 중 하나 또는 둘 다 {@code null}이면 {@code false}를 반환한다.
	 */
	public static <E> boolean isEqual(E[] array1, E[] array2) {
		if (array1 == null || array2 == null || array1.length != array2.length) {
			return false;
		}
		for (int i = 0; i < array1.length; i++) {
			if (!UObjects.isEqual(array1[i], array2[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 두 배열의 일치 여부.
	 * <p>
	 * (1) 두 배열이 모두 {@code null}이거나 (2) 요소의 개수가 같으며 각 인덱스의 요소도 같아야 한다.
	 *
	 * @param array1
	 * @param array2
	 * @return 둘 다 {@code null}이면 {@code true}를 반환한다.
	 */
	public static <E> boolean isEqualOrBothNull(E[] array1, E[] array2) {
		return isEqual(array1, array2) || array1 == array2;
	}

	/**
	 * 두 리스트의 일치 여부.
	 * <p>
	 * (1) 두 리스트가 모두 {@code null}이 아니고, (2) 요소의 개수가 같으며, (3) 각 인덱스의 요소도 같아야 한다.
	 *
	 * @param list1
	 * @param list2
	 * @return 두 값 중 하나 또는 둘 다 {@code null}이면 {@code false}를 반환한다.
	 */
	public static <E> boolean isEqual(List<E> list1, List<E> list2) {
		if (list1 == null || list2 == null || list1.size() != list2.size()) {
			return false;
		}
		for (int i = 0; i < list1.size(); i++) {
			if (!UObjects.isEqual(list1.get(i), list2.get(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 두 리스트의 일치 여부.
	 * <p>
	 * (1) 두 리스트가 모두 {@code null}이거나 (2) 요소의 개수가 같으며 각 인덱스의 요소도 같아야 한다.
	 *
	 * @param list1
	 * @param list2
	 * @return 둘 다 {@code null}이면 {@code true}를 반환한다.
	 */
	public static <E> boolean isEqualOrBothNull(List<E> list1, List<E> list2) {
		return isEqual(list1, list2) || list1 == list2;
	}

	/**
	 * 두 세트의 일치 여부.
	 * <p>
	 * (1) 두 세트가 모두 {@code null}이 아니고, (2) 요소의 개수가 같으며, (3) 같은 요소들을 포함하고 있어야 한다.
	 *
	 * @param set1
	 * @param set2
	 * @return 두 값 중 하나 또는 둘 다 {@code null}이면 {@code false}를 반환한다.
	 */
	public static <E> boolean isEqual(Set<E> set1, Set<E> set2) {
		if (set1 == null || set2 == null || set1.size() != set2.size()) {
			return false;
		}
		for (E elem : set1) {
			if (set2.contains(elem)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 두 세트의 일치 여부.
	 * <p>
	 * (1) 두 세트가 모두 {@code null}이거나 (2) 요소의 개수가 같으며 같은 요소들을 포함하고 있어야 한다.
	 *
	 * @param set1
	 * @param set2
	 * @return 둘 다 {@code null}이면 {@code true}를 반환한다.
	 */
	public static <E> boolean isEqualOrBothNull(Set<E> set1, Set<E> set2) {
		return isEqual(set1, set2) || set1 == set2;
	}

	/**
	 * 두 맵의 일치 여부.
	 * <p>
	 * (1) 두 맵이 모두 {@code null}이 아니고, (2) 키-값 쌍의 개수가 같으며, (3) 같은 키에는 같은 값이 할당되어 있어야
	 * 한다.
	 *
	 * @param map1
	 * @param map2
	 * @return 두 값 중 하나 또는 둘 다 {@code null}이면 {@code false}를 반환한다.
	 */
	public static <K, V> boolean isEqual(Map<K, V> map1, Map<K, V> map2) {
		if (map1 == null || map2 == null || map1.size() != map2.size()) {
			return false;
		}
		for (K key : map1.keySet()) {
			if (UObjects.isEqual(map1.get(key), map2.get(key))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 두 맵의 일치 여부.
	 * <p>
	 * (1) 두 맵이 모두 {@code null}이거나 (2) 키-값 쌍의 개수가 같으며 같은 키에는 같은 값이 할당되어 있어야 한다.
	 *
	 * @param map1
	 * @param map2
	 * @return 둘 다 {@code null}이면 {@code true}를 반환한다.
	 */
	public static <K, V> boolean isEqualOrBothNull(Map<K, V> map1, Map<K, V> map2) {
		return isEqual(map1, map2) || map1 == map2;
	}

	/**
	 * 배열에 매개변수로 전달된 {@code matchable}과 매칭되는 요소가 모두 존재하는 지 여부.
	 * <p>
	 * {@code array}가 {@code null}이거나 비었으면 {@code false}를 반환한다.
	 *
	 * @param <E>        요소의 타입.
	 * @param array
	 * @param matchables {@link Matchable}의 객체.
	 * @return 요소의 존부.
	 */
	@SafeVarargs
	public static <E> boolean matchesAll(E[] array, Matchable<E>... matchables) {
		if (isEmpty(array) || isEmpty(matchables)) {
			return false;
		}
		for (Matchable<E> matchable : matchables) {
			if (!matches(array, matchable)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 배열에 매개변수로 전달된 {@code matchable}과 매칭되는 요소가 모두 존재하는 지 여부.
	 * <p>
	 * {@code array}가 {@code null}이거나 비었으면 {@code false}를 반환한다.
	 *
	 * @param <E>        요소의 타입.
	 * @param array
	 * @param matchables {@link Matchable}의 객체.
	 * @return 요소의 존부.
	 */
	@SafeVarargs
	public static <E> boolean matchesAll(Collection<E> collection, Matchable<E>... matchables) {
		if (isEmpty(collection) || isEmpty(matchables)) {
			return false;
		}
		for (Matchable<E> matchable : matchables) {
			if (!matches(collection, matchable)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 배열에 매개변수로 전달된 {@code matchable}과 매칭되는 요소가 하나라도 존재하는 지 여부.
	 * <p>
	 * {@code array}가 {@code null}이거나 비었으면 {@code false}를 반환한다.
	 *
	 * @param <E>        요소의 타입.
	 * @param array
	 * @param matchables {@link Matchable}의 객체.
	 * @return 요소의 존부.
	 */
	@SafeVarargs
	public static <E> boolean matchesAny(E[] array, Matchable<E>... matchables) {
		if (isEmpty(array) || isEmpty(matchables)) {
			return false;
		}
		for (Matchable<E> matchable : matchables) {
			if (matches(array, matchable)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * {@link Collection} 객체에 매개변수로 전달된 {@code matchable}과 매칭되는 요소가 하나라도 존재하는 지 여부.
	 * <p>
	 * {@code collection}가 {@code null}이거나 비었으면 {@code false}를 반환한다.
	 *
	 * @param <E>        요소의 타입.
	 * @param collection
	 * @param matchables {@link Matchable}의 객체.
	 * @return 요소의 존부.
	 */
	@SafeVarargs
	public static <E> boolean matchesAny(Collection<E> collection, Matchable<E>... matchables) {
		if (isEmpty(collection) || isEmpty(matchables)) {
			return false;
		}
		for (Matchable<E> matchable : matchables) {
			if (matches(collection, matchable)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 매개변수들을 요소로 하는 배열을 만든다.
	 *
	 * @param <E>      요소의 타입.
	 * @param elements
	 * @return 배열.
	 */
	@SafeVarargs
	public static <E> E[] newArray(E... elements) {
		return elements;
	}

	/**
	 * 주어진 타입과 길이를 가진 빈 배열을 만든다.
	 *
	 * @param <E>         요소의 타입.
	 * @param elementType
	 * @param length
	 * @return 배열.
	 */
	@SuppressWarnings("unchecked")
	public static <E> E[] newArray(Class<E> elementType, int length) {
		return (E[]) Array.newInstance(elementType, length);
	}

	/**
	 * 매개변수들을 요소로 하는 {@link List}의 객체를 만든다.
	 *
	 * @param <E>      요소의 타입.
	 * @param elements
	 * @return
	 */
	@SafeVarargs
	public static <E> List<E> newList(E... elements) {
		return toList(elements);
	}

	/**
	 * 매개변수들을 요소로 하는 {@link Set}의 객체를 만든다.
	 *
	 * @param <E>      요소의 타입.
	 * @param elements
	 * @return
	 */
	@SafeVarargs
	public static <E> Set<E> newSet(E... elements) {
		return toSet(elements);
	}

	/**
	 * {@code matchable}과 매칭되는 모든 요소를 제거한다.
	 *
	 * @param <E>       요소의 타입.
	 * @param iterable
	 * @param matchable {@link Matchable}의 객체.
	 * @return 제거된 요소들로 구성된 {@link List}의 객체.
	 */
	public static <E> List<E> removeAll(Iterable<E> iterable, Matchable<E> matchable) {
		List<E> results = new ArrayList<>();

		for (Iterator<E> iterator = iterable.iterator(); iterator.hasNext();) {
			E item = iterator.next();

			if (matchable.matches(item)) {
				iterator.remove();
				results.add(item);
			}
		}
		return results;
	}

	/**
	 * 값이 {@code matchable}과 매칭되는 모든 요소를 제거한다.
	 *
	 * @param <K>       키의 타입.
	 * @param <V>       값의 타입.
	 * @param map
	 * @param matchable {@link Matchable}의 객체.
	 * @return 제거된 요소들의 키와 값으로 구성된 {@link Map}의 객체.
	 */
	public static <K, V> Map<K, V> removeAll(Map<K, V> map, Matchable<V> matchable) {
		Map<K, V> results = new HashMap<>();

		for (K key : map.keySet()) {
			if (matchable.matches(map.get(key))) {
				results.put(key, map.remove(key));
			}
		}
		return results;
	}

	/**
	 * 키가 {@code matchable}과 매칭되는 모든 요소를 제거한다.
	 *
	 *
	 * @param <K>       키의 타입.
	 * @param <V>       값의 타입.
	 * @param map
	 * @param matchable {@link Matchable}의 객체.
	 * @return 제거된 요소들의 키와 값으로 구성된 {@link Map}의 객체.
	 */
	public static <K, V> Map<K, V> removeAllOfKey(Map<K, V> map, Matchable<K> matchable) {
		Map<K, V> results = new HashMap<>();

		for (K key : map.keySet()) {
			if (matchable.matches(key)) {
				results.put(key, map.remove(key));
			}
		}
		return results;
	}

	/**
	 * {@code matchable}과 매칭되는 첫번째 요소를 제거한다.
	 *
	 * @param <E>       요소의 타입.
	 * @param iterable
	 * @param matchable {@link Matchable}의 객체.
	 * @return 제거된 요소.
	 */
	public static <E> E removeFirst(Iterable<E> iterable, Matchable<E> matchable) {
		for (Iterator<E> iterator = iterable.iterator(); iterator.hasNext();) {
			E item = iterator.next();

			if (matchable.matches(item)) {
				iterator.remove();
				return item;
			}
		}
		return null;
	}

	/**
	 * 순서를 뒤집은 배열을 생성하여 반환한다.
	 *
	 * @param <E>         요소의 타입.
	 * @param array
	 * @param elementType 배열의 타입.
	 * @return 순서가 역전된 새 배열.
	 */
	public static <E> E[] reverse(E[] array, Class<E> elementType) {
		if (array == null) {
			return newArray(elementType, 0);
		}
		int lastIndex = array.length;
		E[] dest = newArray(elementType, lastIndex--);

		for (int i = 0; i <= lastIndex; i++) {
			dest[lastIndex - i] = array[i];
		}
		return dest;
	}

	/**
	 * 순서를 뒤집은 {@link List}의 객체를 생성하여 반환한다.
	 *
	 * @param <E>  요소의 타입.
	 * @param list
	 * @return 순서가 역전된 {@code List}의 새 객체.
	 */
	public static <E> List<E> reverse(List<E> list) {
		List<E> dest = new ArrayList<>();

		if (list != null) {
			dest.addAll(list);
			Collections.reverse(dest);
		}
		return dest;
	}

	/**
	 * 배열의 길이를 반환한다.
	 * <p>
	 * {@code array}가 {@code null}이면 {@code 0}을 반환한다.
	 *
	 * @param <E>   요소의 타입.
	 * @param array
	 * @return 배열의 길이.
	 */
	public static <E> int size(E[] array) {
		return array != null ? array.length : 0;
	}

	/**
	 * {@link Collection} 객체의 크기를 반환한다.
	 * <p>
	 * {@code collection}이 {@code null}이면 {@code 0}을 반환한다.
	 *
	 * @param <E>        요소의 타입.
	 * @param collection
	 * @return {@code Collection} 객체의 크기
	 */
	public static <E> int size(Collection<E> collection) {
		return collection != null ? collection.size() : 0;
	}

	/**
	 * {@link Map} 객체의 크기를 반환한다.
	 * <p>
	 * {@code map}이 {@code null}이면 {@code 0}을 반환한다.
	 *
	 * @param <K> 키의 타입.
	 * @param <V> 값의 타입.
	 * @param map
	 * @return {@code Map} 객체의 크기
	 */
	public static <K, V> int size(Map<K, V> map) {
		return map != null ? map.size() : 0;
	}

	/**
	 * {@link Collection} 객체로부터 같은 타입의 배열을 생성하여 반환한다.
	 * <p>
	 * {@code collection}이 {@code null}이면 빈 배열을 생성하여 반환한다.
	 *
	 * @param <E>         요소의 타입.
	 * @param collection
	 * @param elementType
	 * @return 새 배열.
	 */
	public static <E> E[] toArray(Collection<E> collection, Class<E> elementType) {
		return collection != null
				? collection.toArray(newArray(elementType, collection.size()))
						: newArray(elementType, 0);
	}

	/**
	 * 배열로부터 같은 타입의 {@link List} 객체를 생성하여 반환한다.
	 * <p>
	 * {@code array}가 {@code null}이면 빈 {@code List} 객체를 생성하여 반환한다.
	 *
	 * @param <E>   요소의 타입.
	 * @param array
	 * @return 새 {@code List} 객체
	 */
	public static <E> List<E> toList(E[] array) {
		return array != null ? new ArrayList<>(Arrays.asList(array)) : new ArrayList<>();
	}

	/**
	 * {@link Set} 객체로부터 같은 타입의 {@link List} 객체를 생성하여 반환한다.
	 * <p>
	 * {@code set}이 {@code null}이면 빈 {@code List} 객체를 생성하여 반환한다.
	 *
	 * @param <E> 요소의 타입.
	 * @param set
	 * @return 새 {@code List} 객체
	 */
	public static <E> List<E> toList(Set<E> set) {
		return set != null ? new ArrayList<>(set) : new ArrayList<>();
	}

	/**
	 * 배열로부터 같은 타입의 {@link Set} 객체를 생성하여 반환한다.
	 * <p>
	 * {@code array}가 {@code null}이면 빈 {@code Set} 객체를 생성하여 반환한다.
	 *
	 * @param <E>   요소의 타입.
	 * @param array
	 * @return 새 {@code Set} 객체
	 */
	public static <E> Set<E> toSet(E[] array) {
		return array != null ? new HashSet<>(Arrays.asList(array)) : new HashSet<>();
	}

	/**
	 * {@link List} 객체로부터 같은 타입의 {@link Set} 객체를 생성하여 반환한다.
	 * <p>
	 * {@code list}가 {@code null}이면 빈 {@code Set} 객체를 생성하여 반환한다.
	 *
	 * @param <E>  요소의 타입.
	 * @param list
	 * @return 새 {@code Set} 객체
	 */
	public static <E> Set<E> toSet(List<E> list) {
		return list != null ? new HashSet<>(list) : new HashSet<>();
	}

}
