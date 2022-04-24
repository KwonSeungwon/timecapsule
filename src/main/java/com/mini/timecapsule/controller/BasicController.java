package com.mini.timecapsule.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j

@Controller
public class BasicController {
    @GetMapping("/")
    public String home() {
        return "index"; //home.jsp 파일로 찾아가서 타임리프파일을 찾아간다 .
    }
}
