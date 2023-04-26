package com.mini.timecapsule.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class StartController {

    @GetMapping(value = "/start")
    public String startPage() {
        return "/start";
    }




}
