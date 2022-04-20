package com.mini.timecapsule.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j

@RestController
public class BasicController {
    @RequestMapping("/")
    public String home(){
        log.info("home Controller");
        return "home"; //home.jsp 파일로 찾아가서 타임리프파일을 찾아간다 .
    }
}
