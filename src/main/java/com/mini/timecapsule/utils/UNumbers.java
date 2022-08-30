package com.mini.timecapsule.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;

/**
 * 숫자, 특히 {@link BigDecimal}을 다룰 때 유용한 유틸리티 클래스.
 * <p>
 * 자주 쓰이는 {@code BigDecimal} 상수들도 정의되어 있다.
 */
public abstract class UNumbers {

	/**
	 * {@code BigDecimal}로 정의된 상수 {@code -1}
	 */
	public static final BigDecimal MINUS_ONE = new BigDecimal("-1");

	/**
	 * {@code BigDecimal}로 정의된 상수 {@code 0}
	 */
	public static final BigDecimal ZERO = BigDecimal.ZERO;

	/**
	 * {@code BigDecimal}로 정의된 상수 {@code 0.5}
	 */
	public static final BigDecimal POINT_FIVE = new BigDecimal("0.5");

	/**
	 * {@code BigDecimal}로 정의된 상수 {@code 1}
	 */
	public static final BigDecimal ONE = BigDecimal.ONE;

	/**
	 * {@code BigDecimal}로 정의된 상수 {@code 2}
	 */
	public static final BigDecimal TWO = new BigDecimal("2");

	/**
	 * {@code BigDecimal}로 정의된 상수 {@code 3}
	 */
	public static final BigDecimal THREE = new BigDecimal("3");

	/**
	 * {@code BigDecimal}로 정의된 상수 {@code 2}
	 */
	public static final BigDecimal FOUR = new BigDecimal("4");

	/**
	 * {@code BigDecimal}로 정의된 상수 {@code 5}
	 */
	public static final BigDecimal FIVE = new BigDecimal("5");

	/**
	 * {@code BigDecimal}로 정의된 상수 {@code 6}
	 */
	public static final BigDecimal SIX = new BigDecimal("6");

	/**
	 * {@code BigDecimal}로 정의된 상수 {@code 8}
	 */
	public static final BigDecimal EIGHT = new BigDecimal("8");

	/**
	 * {@code BigDecimal}로 정의된 상수 {@code 10}
	 */
	public static final BigDecimal TEN = BigDecimal.TEN;

	/**
	 * {@code BigDecimal}로 정의된 상수 {@code 12}
	 */
	public static final BigDecimal TWELVE = new BigDecimal("12");

	/**
	 * {@code BigDecimal}로 정의된 상수 {@code 16}
	 */
	public static final BigDecimal SIXTEEN = new BigDecimal("16");

	/**
	 * {@code BigDecimal}로 정의된 상수 {@code 20}
	 */
	public static final BigDecimal TWENTY = new BigDecimal("20");

	/**
	 * {@code BigDecimal}로 정의된 상수 30
	 */
	public static final BigDecimal THIRTY = new BigDecimal("30");

	/**
	 * {@code BigDecimal}로 정의된 상수 {@code 50}
	 */
	public static final BigDecimal FIFTY = new BigDecimal("50");

	/**
	 * {@code BigDecimal}로 정의된 상수 {@code 60}
	 */
	public static final BigDecimal SIXTY = new BigDecimal("60");

	/**
	 * {@code BigDecimal}로 정의된 상수 {@code 100}
	 */
	public static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

	/**
	 * {@code BigDecimal}로 정의된 상수 {@code 1000}
	 */
	public static final BigDecimal ONE_THOUSAND = new BigDecimal("1000");

	/**
	 * {@code BigDecimal}로 정의된 상수 {@code 10000}
	 */
	public static final BigDecimal TEN_THOUSAND = new BigDecimal("10000");

	/**
	 * 절댓값을 반환함.
	 * <p>
	 * 만약, {@code value}가 {@code null}이면 {@code null}을 반환함.
	 *
	 * @param value
	 * @return 절댓값 또는 {@code null}.
	 */
	public static BigDecimal abs(BigDecimal value) {
		return isLessThanZero(value) ? value.negate() : value;
	}

	/**
	 * 매개변수 값들의 평균을 구한다.
	 * <p>
	 * 이때 {@code null}인 매개변수는 {@code 0}으로 처리한다.
	 *
	 * @param values
	 * @return 소수 첫째 자리에서 반올림한 평균.
	 */
	public static BigDecimal average(BigDecimal... values) {
		return average(UCollections.toList(values));
	}

	/**
	 * 매개변수 값들의 평균을 구한다.
	 * <p>
	 * 이때 {@code null}인 매개변수는 {@code 0}으로 처리한다.
	 *
	 * @param scale        몫의 소수점 자릿수.
	 * @param roundingMode 올림/버림/반올림 등의 처리. {@link RoundingMode} 참조.
	 * @param values
	 * @return 평균.
	 */
	public static BigDecimal average(int scale, RoundingMode roundingMode, BigDecimal... values) {
		return average(UCollections.toList(values), scale, roundingMode);
	}

	/**
	 * {@link Collection} 객체 요소들의 평균을 구한다.
	 * <p>
	 * 이때 {@code null}인 요소는 {@code 0}으로 처리한다.
	 *
	 * @param values
	 * @return 소수 첫째 자리에서 반올림한 평균.
	 */
	public static BigDecimal average(Collection<BigDecimal> collection) {
		return average(collection, 0, RoundingMode.HALF_UP);
	}

	/**
	 * {@link Collection} 객체 요소들의 평균을 구한다.
	 * <p>
	 * 이때 {@code null}인 요소는 {@code 0}으로 처리한다.
	 *
	 * @param collection
	 * @param scale        몫의 소수점 자릿수.
	 * @param roundingMode 올림/버림/반올림 등의 처리. {@link RoundingMode} 참조.
	 * @return 평균.
	 * @throws IllegalArgumentException
	 */
	public static BigDecimal average(Collection<BigDecimal> collection, int scale, RoundingMode roundingMode) throws IllegalArgumentException {
		if (collection == null || collection.isEmpty()) {
			throw new IllegalArgumentException("collection cannot be null nor empty.");
		}
		return sum(collection).divide(UNumbers.toBigDecimal(collection.size()), scale, roundingMode);
	}

	/**
	 * 값을 복사하여 새로운 객체를 만듦.
	 * <p>
	 * 만약, {@code value}가 {@code null}이면 {@code null}을 반환함.
	 *
	 * @param value
	 * @return 복사된 새 객체 또는 {@code null}.
	 */
	public static BigDecimal clone(BigDecimal value) {
		return value != null ? value.add(ZERO) : null;
	}

	/**
	 * {@code 10}으로 나누기.
	 *
	 * @param value 피제수.
	 * @return 소수 첫째자리에서 반올림된 몫.
	 */
	public static BigDecimal divideBy10(BigDecimal value) {
		return value.divide(TEN, RoundingMode.HALF_UP);
	}

	/**
	 * {@code 10}으로 나누기.
	 *
	 * @param value        피제수.
	 * @param scale        몫의 소수점 자릿수.
	 * @param roundingMode 올림/버림/반올림 등의 처리. {@link RoundingMode} 참조.
	 * @return 몫.
	 */
	public static BigDecimal divideBy10(BigDecimal value, int scale, RoundingMode roundingMode) {
		return value.divide(TEN, scale, roundingMode);
	}

	/**
	 * {@code 60}으로 나누기.
	 *
	 * @param value 피제수.
	 * @return 소수 첫째자리에서 반올림된 몫.
	 */
	public static BigDecimal diviedBy60(BigDecimal value) {
		return value.divide(SIXTY, RoundingMode.HALF_UP);
	}

	/**
	 * {@code 60}으로 나누기.
	 *
	 * @param value        피제수.
	 * @param scale        몫의 소수점 자릿수.
	 * @param roundingMode 올림/버림/반올림 등의 처리. {@link RoundingMode} 참조.
	 * @return 몫.
	 */
	public static BigDecimal diviedBy60(BigDecimal value, int scale, RoundingMode roundingMode) {
		return value.divide(SIXTY, scale, roundingMode);
	}

	/**
	 * {@code 100}으로 나누기.
	 *
	 * @param value 피제수.
	 * @return 소수 첫째자리에서 반올림된 몫.
	 */
	public static BigDecimal divideBy100(BigDecimal value) {
		return value.divide(ONE_HUNDRED, RoundingMode.HALF_UP);
	}

	/**
	 * {@code 100}으로 나누기.
	 *
	 * @param value        피제수.
	 * @param scale        몫의 소수점 자릿수.
	 * @param roundingMode 올림/버림/반올림 등의 처리. {@link RoundingMode} 참조.
	 * @return 몫.
	 */
	public static BigDecimal divideBy100(BigDecimal value, int scale, RoundingMode roundingMode) {
		return value.divide(ONE_HUNDRED, scale, roundingMode);
	}

	/**
	 * 두 수가 일치하는 지 여부.
	 *
	 * @param <N>    {@link Comparable}을 구현한 클래스. 주로 {@link BigDecimal},
	 *               {@link Long}, {@link Integer}가 사용된다.
	 * @param value1
	 * @param value2
	 * @return 두 값 중 하나 또는 둘 다 {@code null}이면 {@code false}를 반환한다.
	 */
	public static <N extends Comparable<N>> boolean isEqual(N value1, N value2) {
		return value1 != null && value2 != null && value1.compareTo(value2) == 0;
	}

	/**
	 * {@code value >= other} 여부.
	 *
	 * @param <N>   {@link Comparable}을 구현한 클래스. 주로 {@link BigDecimal},
	 *              {@link Long}, {@link Integer}가 사용된다.
	 * @param value
	 * @param other
	 * @return {@code value >= other}
	 */
	public static <N extends Comparable<N>> boolean isEqualOrGreaterThan(N value, N other) {
		return value != null && value.compareTo(other) >= 0;
	}

	/**
	 * {@code value >= 0} 여부.
	 *
	 * @param value
	 * @return {@code value >= 0}
	 */
	public static boolean isEqualOrGreaterThanZero(BigDecimal value) {
		return isEqualOrGreaterThan(value, ZERO);
	}

	/**
	 * {@code value <= other} 여부.
	 *
	 * @param <N>   {@link Comparable}을 구현한 클래스. 주로 {@link BigDecimal},
	 *              {@link Long}, {@link Integer}가 사용된다.
	 * @param value
	 * @param other
	 * @return {@code value <= other}
	 */
	public static <N extends Comparable<N>> boolean isEqualOrLessThan(N value, N other) {
		return value != null && value.compareTo(other) <= 0;
	}

	/**
	 * {@code value <= 0} 여부.
	 *
	 * @param value
	 * @return {@code value <= 0}
	 */
	public static boolean isEqualOrLessThanZero(BigDecimal value) {
		return isEqualOrLessThan(value, ZERO);
	}

	/**
	 * {@code value > other} 여부.
	 *
	 * @param <N>   {@link Comparable}을 구현한 클래스. 주로 {@link BigDecimal},
	 *              {@link Long}, {@link Integer}가 사용된다.
	 * @param value
	 * @param other
	 * @return {@code value > other}
	 */
	public static <N extends Comparable<N>> boolean isGreaterThan(N value, N other) {
		return value != null && value.compareTo(other) > 0;
	}

	/**
	 * {@code value > 0} 여부.
	 *
	 * @param value
	 * @return {@code value > 0}
	 */
	public static boolean isGreaterThanZero(BigDecimal value) {
		return isGreaterThan(value, ZERO);
	}

	/**
	 * {@code value < other} 여부.
	 *
	 * @param <N>   {@link Comparable}을 구현한 클래스. 주로 {@link BigDecimal},
	 *              {@link Long}, {@link Integer}가 사용된다.
	 * @param value
	 * @param other
	 * @return {@code value < other}
	 */
	public static <N extends Comparable<N>> boolean isLessThan(N value, N other) {
		return value != null && value.compareTo(other) < 0;
	}

	/**
	 * {@code value < 0} 여부.
	 *
	 * @param value
	 * @return {@code value > 0}
	 */
	public static boolean isLessThanZero(BigDecimal value) {
		return isLessThan(value, ZERO);
	}

	/**
	 * {@code value == 0} 여부.
	 *
	 * @param value
	 * @return {@code value == 0}
	 */
	public static boolean isZero(BigDecimal value) {
		return isEqual(value, ZERO);
	}

	/**
	 * 최대값 찾기.
	 *
	 * @param <N>    {@link Comparable}을 구현한 클래스. 주로 {@link BigDecimal},
	 *               {@link Long}, {@link Integer}가 사용된다.
	 * @param values
	 * @return 최대값.
	 */
	@SafeVarargs
	public static <N extends Comparable<N>> N max(N... values) {
		N max = null;

		for (N value : values) {
			if (value == null) {
				continue;
			}
			if (max == null || isLessThan(max, value)) {
				max = value;
			}
		}
		return max;
	}

	/**
	 * 최소값 찾기.
	 *
	 * @param <N>    {@link Comparable}을 구현한 클래스. 주로 {@link BigDecimal},
	 *               {@link Long}, {@link Integer}가 사용된다.
	 * @param values
	 * @return 최소값.
	 */
	@SafeVarargs
	public static <N extends Comparable<N>> N min(N... values) {
		N min = null;

		for (N value : values) {
			if (value == null) {
				continue;
			}
			if (min == null || isGreaterThan(min, value)) {
				min = value;
			}
		}
		return min;
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
	public static <N extends Number> N safe(N value, N defaultValue) {
		return UObjects.safe(value, defaultValue);
	}

	/**
	 * {@code value}가 {@code null}이 아니면 {@code value}을 반환하고, {@code null}이면
	 * {@code 0}을 반환한다.
	 * <p>
	 * 따라서 반환값은 언제나 안전하다.
	 *
	 * @param value
	 * @return {@code value} 또는 {@code 0}
	 */
	public static BigDecimal safe(BigDecimal value) {
		return UObjects.safe(value, ZERO);
	}

	/**
	 * {@code value}가 {@code null}이 아니면 {@code value}을 반환하고, {@code null}이면
	 * {@code 0}을 반환한다.
	 * <p>
	 * 따라서 반환값은 언제나 안전하다.
	 *
	 * @param value
	 * @return {@code value} 또는 {@code 0}
	 */
	public static Long safe(Long value) {
		return UObjects.safe(value, 0L);
	}

	/**
	 * {@code value}가 {@code null}이 아니면 {@code value}을 반환하고, {@code null}이면
	 * {@code 0}을 반환한다.
	 * <p>
	 * 따라서 반환값은 언제나 안전하다.
	 *
	 * @param value
	 * @return {@code value} 또는 {@code 0}
	 */
	public static Integer safe(Integer value) {
		return UObjects.safe(value, 0);
	}

	/**
	 * {@code value}가 {@code null}이 아니면 {@code value}을 반환하고, {@code null}이면
	 * {@code 0}을 반환한다.
	 * <p>
	 * 따라서 반환값은 언제나 안전하다.
	 *
	 * @param value
	 * @return {@code value} 또는 {@code 0}
	 */
	public static Float safe(Float value) {
		return UObjects.safe(value, 0.0f);
	}

	/**
	 * {@code value}가 {@code null}이 아니면 {@code value}을 반환하고, {@code null}이면
	 * {@code 0}을 반환한다.
	 * <p>
	 * 따라서 반환값은 언제나 안전하다.
	 *
	 * @param value
	 * @return {@code value} 또는 {@code 0}
	 */
	public static Double safe(Double value) {
		return UObjects.safe(value, 0.0);
	}

	/**
	 * 숫자를 문자열로 표현한다.
	 * <p>
	 * 만약, {@code value}가 {@code null}이면 {@code 0}을 문자로 변환한다.
	 *
	 * @param value
	 * @return {@code value} 또는 {@code 0}의 문자열 표현.
	 */
	public static String stringify(BigDecimal value) {
		return stringify(value, ZERO);
	}

	/**
	 * {@code scale}과 {roundingMode}에 맞는 숫자를 문자열로 표현한다.
	 * <p>
	 * 만약, {@code value}가 {@code null}이면 {@code 0}을 문자로 변환한다.
	 *
	 * @param value
	 * @param scale
	 * @param roundingMode
	 * @return {@code value} 또는 {@code 0}의 문자열 표현.
	 */
	public static String stringify(BigDecimal value, int scale, RoundingMode roundingMode) {
		return stringify(value, scale, roundingMode, ZERO);
	}

	/**
	 * 숫자를 문자열로 표현한다.
	 * <p>
	 * 만약, {@code value}가 {@code null}이면 {@code defaultValue}를 문자로 변환한다.
	 *
	 * @param value
	 * @param defaultValue
	 * @return {@code value} 또는 {@code 0}의 문자열 표현.
	 */
	public static String stringify(BigDecimal value, BigDecimal defaultValue) {
		return safe(value, defaultValue).stripTrailingZeros().toPlainString();
	}

	/**
	 * {@code scale}과 {roundingMode}에 맞는 숫자를 문자열로 표현한다.
	 * <p>
	 * 만약, {@code value}가 {@code null}이면 {@code defaultValue}를 문자로 변환한다.
	 *
	 * @param value
	 * @param scale
	 * @param roundingMode
	 * @param defaultValue
	 * @return {@code value} 또는 {@code 0}의 문자열 표현.
	 */
	public static String stringify(BigDecimal value, int scale, RoundingMode roundingMode, BigDecimal defaultValue) {
		return safe(value, defaultValue).setScale(scale, roundingMode).stripTrailingZeros().toPlainString();
	}

	/**
	 * 매개변수 값들의 합을 구한다.
	 * <p>
	 * 이때 {@code null}인 매개변수는 {@code 0}으로 처리한다.
	 *
	 * @param values
	 * @return 합.
	 */
	public static BigDecimal sum(BigDecimal... values) {
		return sum(UCollections.toList(values));
	}

	/**
	 * {@link Collection} 객체 요소들의 합을 구한다.
	 * <p>
	 * 이때 {@code null}인 요소는 {@code 0}으로 처리한다.
	 *
	 * @param values
	 * @return 합.
	 */
	public static BigDecimal sum(Collection<BigDecimal> values) {
		BigDecimal result = ZERO;

		if (values != null) {
			for (BigDecimal value : values) {
				result = result.add(UNumbers.safe(value));
			}
		}
		return result;
	}

	/**
	 * 객체를 {@link BigDecimal}의 객체로 변환한다.
	 * <p>
	 * 다만, 객체는 숫자를 나타내는 문자열처럼 숫자로 바꿀 수 있는 것이어야 하며, {@code value}가 {@code null}이면
	 * {@code 0}을 변환한다.
	 *
	 * @param value
	 * @return 숫자로 변환된 값.
	 */
	public static BigDecimal toBigDecimal(Object value) {
		return toBigDecimal(value, ZERO);
	}

	/**
	 * 객체를 {@link BigDecimal}의 객체로 변환한다.
	 * <p>
	 * 다만, 객체는 숫자를 나타내는 문자열처럼 숫자로 바꿀 수 있는 것이어야 하며, {@code value}가 {@code null}이면
	 * {@code defaultValue}를 변환한다.
	 *
	 * @param value
	 * @param defaultValue
	 * @return 숫자로 변환된 값.
	 */
	public static BigDecimal toBigDecimal(Object value, BigDecimal defaultValue) {
		String text = String.valueOf(value);
		return UStrings.isValid(text) ? new BigDecimal(text.replaceAll(",", "")) : defaultValue;
	}

	/**
	 * 래퍼 클래스의 객체에서 프리미티브 값을 얻는다.
	 * <p>
	 * 만약, {@code value}가 {@code null}이면 {@code defaultValue}를 변환한다.
	 *
	 * @param value
	 * @param defaultValue
	 * @return 프리미티브 값.
	 */
	public static int unbox(Integer value, int defaultValue) {
		return value != null ? value : defaultValue;
	}

	/**
	 * 래퍼 클래스의 객체에서 프리미티브 값을 얻는다.
	 * <p>
	 * 만약, {@code value}가 {@code null}이면 {@code 0}을 변환한다.
	 *
	 * @param value
	 * @return 프리미티브 값.
	 */
	public static int unbox(Integer value) {
		return unbox(value, 0);
	}

	/**
	 * 래퍼 클래스의 객체에서 프리미티브 값을 얻는다.
	 * <p>
	 * 만약, {@code value}가 {@code null}이면 {@code defaultValue}를 변환한다.
	 *
	 * @param value
	 * @param defaultValue
	 * @return 프리미티브 값.
	 */
	public static long unbox(Long value, long defaultValue) {
		return value != null ? value : defaultValue;
	}

	/**
	 * 래퍼 클래스의 객체에서 프리미티브 값을 얻는다.
	 * <p>
	 * 만약, {@code value}가 {@code null}이면 {@code 0}을 변환한다.
	 *
	 * @param value
	 * @return 프리미티브 값.
	 */
	public static long unbox(Long value) {
		return unbox(value, 0);
	}

	/**
	 * 래퍼 클래스의 객체에서 프리미티브 값을 얻는다.
	 * <p>
	 * 만약, {@code value}가 {@code null}이면 {@code defaultValue}를 변환한다.
	 *
	 * @param value
	 * @param defaultValue
	 * @return 프리미티브 값.
	 */
	public static float unbox(Float value, float defaultValue) {
		return value != null ? value : defaultValue;
	}

	/**
	 * 래퍼 클래스의 객체에서 프리미티브 값을 얻는다.
	 * <p>
	 * 만약, {@code value}가 {@code null}이면 {@code 0}을 변환한다.
	 *
	 * @param value
	 * @return 프리미티브 값.
	 */
	public static float unbox(Float value) {
		return unbox(value, 0);
	}

	/**
	 * 래퍼 클래스의 객체에서 프리미티브 값을 얻는다.
	 * <p>
	 * 만약, {@code value}가 {@code null}이면 {@code defaultValue}를 변환한다.
	 *
	 * @param value
	 * @param defaultValue
	 * @return 프리미티브 값.
	 */
	public static double unbox(Double value, double defaultValue) {
		return value != null ? value : defaultValue;
	}

	/**
	 * 래퍼 클래스의 객체에서 프리미티브 값을 얻는다.
	 * <p>
	 * 만약, {@code value}가 {@code null}이면 {@code 0}을 변환한다.
	 *
	 * @param value
	 * @return 프리미티브 값.
	 */
	public static double unbox(Double value) {
		return unbox(value, 0);
	}

}
