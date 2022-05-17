package com.mini.timecapsule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
/**
 * 패키지 위치가 다른경우 아래 어노테이션 사용
 * 프로젝트명Application아래애 패키지들이 있어야 springboot가 스캔함
 */
//@ComponentS4can(basePackages = {"mainPakagePostion", "addpakagePostions"})
public class TimecapsuleApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimecapsuleApplication.class, args);
    }

}
