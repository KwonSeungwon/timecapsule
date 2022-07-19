package com.mini.timecapsule.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLOutput;

@RestController
@AllArgsConstructor
public class TestController {

    @PostMapping("/api/boards/post")
    public void Test(){
        System.out.println("teststst");
    }
}
