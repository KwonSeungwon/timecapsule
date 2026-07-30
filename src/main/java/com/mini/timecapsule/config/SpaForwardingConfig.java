package com.mini.timecapsule.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * Vue Router 가 createWebHistory(history mode) 라 /write · /my-capsule 같은 딥링크가
 * 반드시 서버를 거친다. 정적 자원이 있으면 그대로 주고, 없으면 SPA 셸(index.html)로 넘긴다.
 *
 * 주의: 예전엔 WebConfig 와 MvcConfiguration 이 **둘 다** "/**" 에 리소스 핸들러를
 * 등록해 서로를 덮어썼고, 살아남은 쪽엔 fallback 이 없어 모든 딥링크가 깨져 있었다.
 * 리소스 핸들러는 이 클래스 하나로만 유지할 것.
 */
@Configuration
public class SpaForwardingConfig implements WebMvcConfigurer {

    /**
     * SPA 셸로 fallback 하면 안 되는 경로 — 실물이 없으면 404 가 맞다.
     *
     * <p>"무엇이 SPA 라우트가 아닌가" 의 <b>단일 출처</b>다.
     * {@code SpringSecurity} 가 이 목록을 그대로 읽어서, 여기 적힌 것 중
     * 공개 정적 자원이 아닌 것을 잠근다. 목록을 두 곳에 복사하지 말 것.
     */
    public static final String[] NON_SPA_PREFIXES = {
            "api/", "actuator/", "js/", "css/", "img/", "fonts/", "images/", "h2-console/"
    };

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requested = location.createRelative(resourcePath);
                        if (requested.exists() && requested.isReadable()) {
                            return requested;
                        }
                        if (isNonSpaPath(resourcePath)) {
                            return null;
                        }
                        return new ClassPathResource("/static/index.html");
                    }
                });
    }

    private boolean isNonSpaPath(String resourcePath) {
        for (String prefix : NON_SPA_PREFIXES) {
            if (resourcePath.startsWith(prefix)) {
                return true;
            }
        }
        // 확장자가 붙은 요청(=파일)인데 실물이 없으면 404 가 맞다.
        // 여기서 index.html 을 돌려주면 없는 번들을 요청한 브라우저가
        // HTML 을 JS 로 파싱하려다 알 수 없는 에러를 낸다.
        int lastSlash = resourcePath.lastIndexOf('/');
        return resourcePath.indexOf('.', lastSlash + 1) >= 0;
    }
}
