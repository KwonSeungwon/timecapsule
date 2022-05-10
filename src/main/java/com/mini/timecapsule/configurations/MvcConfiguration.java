package com.mini.timecapsule.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

@Configuration //구성(설정) 클래스임을 명시
public class MvcConfiguration implements WebMvcConfigurer {

    //기본 위치를 static -> templates로 따라가게 변경
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/templates")
                .setCacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES));
    }
}
