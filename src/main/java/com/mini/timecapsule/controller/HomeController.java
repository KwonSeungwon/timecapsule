package com.mini.timecapsule.controller;

import com.mini.timecapsule.service.CoordinatesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final CoordinatesService coordinatesService;

    // 루트 경로는 WebConfig의 ViewControllerRegistry에서 처리하도록 맡기거나
    // 여기서 명시적으로 forward 합니다.
    @GetMapping("/")
    public String index(){
        try {
            coordinatesService.initializationCoordinate();
        } catch (Exception e) {}
        return "forward:/index.html";
    }

}
