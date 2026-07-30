package com.mini.timecapsule.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@code CustomException} 을 처리하는 {@code @ControllerAdvice} 는 딱 하나여야 한다.
 *
 * <p>두 개가 있으면 어느 쪽이 뽑히는지는 클래스패스 스캔 순서에 달린다. 그 순서는
 * 테스트(디렉터리 스캔)와 운영(WAR 안 WEB-INF/classes)에서 서로 다르다. 그래서
 * 아래 버그가 <b>운영에서만</b> 났고 MockMvc 로도 RANDOM_PORT 로도 재현되지 않았다:
 *
 * <pre>
 * Failure in @ExceptionHandler customizedExceptionHandler#NotFoundExceptionHandler()
 * java.lang.IllegalArgumentException: Status code '404002' should be a three-digit positive integer
 * </pre>
 *
 * <p>핸들러가 저렇게 터지면 원래 예외가 그대로 필터 체인까지 올라가 사용자에게는
 * <b>403 + 빈 본문</b>이 나간다. 비밀번호를 틀려도, 비속어가 걸려도, 아무 안내가 없다.
 *
 * <p>행위 테스트로는 이 조건을 못 잡는다. 그래서 배선 자체를 못 박는다.
 */
@SpringBootTest
class ExceptionHandlerWiringTest {

    @Autowired
    private ApplicationContext context;

    @Test
    @DisplayName("CustomException 을 처리하는 advice 는 GlobalExceptionHandler 하나뿐이다")
    void exactlyOneAdviceHandlesCustomException() {
        Map<String, Object> advices = context.getBeansWithAnnotation(ControllerAdvice.class);

        List<String> handlingCustomException = advices.entrySet().stream()
                .filter(entry -> handlesCustomException(AopUtils.getTargetClass(entry.getValue())))
                .map(Map.Entry::getKey)
                .sorted()
                .toList();

        assertEquals(List.of("globalExceptionHandler"), handlingCustomException,
                "CustomException 을 처리하는 @ControllerAdvice 가 둘 이상이면 "
                        + "어느 쪽이 뽑힐지 클래스패스 순서에 따라 달라진다");
    }

    /**
     * {@code IllegalStateException} 을 사용자 응답으로 번역하는 핸들러가 없어야 한다.
     *
     * <p>예전엔 이런 핸들러가 있었다:
     * <pre>
     * &#64;ExceptionHandler(IllegalStateException.class)   // → 403 + e.getMessage()
     * </pre>
     * 정당화는 "개봉 전 캡슐의 '아직 개봉할 수 없습니다. D-N' 을 사용자에게 전달하려고"
     * 였다. <b>사실이 아니었다.</b> {@code LetterService#getDetail} 은 개봉일 전이면
     * {@code read()} 를 부르지 않고 그대로 반환하므로, API 경로에서 그 예외는 애초에
     * 발생하지 않는다(개봉 전 캡슐은 200 + {@code openable:false} 로 응답된다).
     *
     * <p>그래서 실제로 남아 있던 것은, Spring · Hibernate · Jackson 이 던지는
     * <b>임의의</b> {@code IllegalStateException} 의 메시지를 아무 검열 없이
     * 클라이언트에게 흘려보내는 통로였다. 그 메시지에는 빈 이름, 커넥션 상태,
     * 파서 내부 상태 같은 것이 들어간다. 게다가 상태코드 403 은 의미상으로도 틀렸다.
     *
     * <p>이 테스트가 깨졌다면 그 핸들러가 되살아난 것이다. 개봉 가능 여부를
     * 사용자에게 알리고 싶다면 예외가 아니라 200 응답의 {@code openable}/{@code dday}
     * 필드를 쓸 것 — {@code LetterControllerPrivacyTest} 가 그 계약을 지킨다.
     */
    @Test
    @DisplayName("IllegalStateException 을 그대로 사용자에게 내보내는 핸들러가 없다")
    void noAdviceLeaksIllegalStateExceptionToTheClient() {
        Map<String, Object> advices = context.getBeansWithAnnotation(ControllerAdvice.class);

        List<String> offenders = advices.entrySet().stream()
                .filter(entry -> handles(AopUtils.getTargetClass(entry.getValue()),
                        IllegalStateException.class))
                .map(Map.Entry::getKey)
                .sorted()
                .toList();

        assertEquals(List.of(), offenders,
                "IllegalStateException 전용 핸들러가 있다. 이 예외는 프레임워크가 던지는 것까지 "
                        + "전부 걸려들기 때문에 내부 상태 문자열이 그대로 사용자에게 나간다. "
                        + "일반 500 핸들러(로그 + 고정 문구)로 떨어지게 둘 것");
    }

    private boolean handlesCustomException(Class<?> adviceType) {
        return handles(adviceType, CustomException.class);
    }

    private boolean handles(Class<?> adviceType, Class<? extends Throwable> exceptionType) {
        for (Method method : adviceType.getMethods()) {
            ExceptionHandler annotation = AnnotationUtils.findAnnotation(method, ExceptionHandler.class);
            if (annotation != null && Arrays.asList(annotation.value()).contains(exceptionType)) {
                return true;
            }
        }
        return false;
    }
}
