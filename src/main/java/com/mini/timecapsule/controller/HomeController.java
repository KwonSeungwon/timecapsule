package com.mini.timecapsule.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * SPA 셸 진입점. vue-router 가 history mode 라 "/" 는 서버로 들어온다.
 *
 * <p>예전엔 여기서 {@code coordinatesService.initializationCoordinate()} 를 호출해
 * 방문 한 번마다 좌표 10,000 행 시딩을 시도하고 예외는 통째로 삼켰다. 좌표 테이블은
 * 회원 시절 잔재였고 캡슐 생성 경로({@code LetterService#sendLetter})는 좌표를
 * {@code Random} 으로 뽑으므로 그 테이블을 읽지 않는다 — 서브시스템째로 제거했다.
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }
}
