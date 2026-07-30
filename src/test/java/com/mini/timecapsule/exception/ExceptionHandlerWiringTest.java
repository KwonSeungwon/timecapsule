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

    private boolean handlesCustomException(Class<?> adviceType) {
        for (Method method : adviceType.getMethods()) {
            ExceptionHandler annotation = AnnotationUtils.findAnnotation(method, ExceptionHandler.class);
            if (annotation != null && Arrays.asList(annotation.value()).contains(CustomException.class)) {
                return true;
            }
        }
        return false;
    }
}
