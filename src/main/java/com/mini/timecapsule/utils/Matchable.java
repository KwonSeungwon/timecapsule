package com.mini.timecapsule.utils;

/**
 * 매칭 여부 판단을 수행할 수 있는 인터페이스.
 * <p>
 * 매칭은 일치와는 다른 고유의 규칙을 통해 결정될 수 있으므로 이와 같은 인터페이스를 별도로 정의했다.
 *
 * @param <T> 일치 여부를 구하는 것이 아니므로 반드시 이 인터페이스를 구현한 클래스 타입일 필요가 없다.
 */
public interface Matchable<T> {

	/**
	 * 매칭 여부를 판단하는 메소드.
	 *
	 * @param object
	 * @return {@code this}와 {@code object}의 매칭 여부.
	 */
	boolean matches(T object);

}
