package com.mini.timecapsule.controller;

import com.mini.timecapsule.dto.CapsuleCreateRequest;
import com.mini.timecapsule.dto.CapsuleOpenRequest;
import com.mini.timecapsule.dto.CapsuleResponse;
import com.mini.timecapsule.model.LetterPaper;
import com.mini.timecapsule.service.LetterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

// @CrossOrigin(origins = "*") 를 다시 붙이지 말 것.
// SPA 는 이 앱이 같은 origin 에서 서빙하고(정적 자원 + /api 가 한 WAR),
// dev 는 vue.config.js 의 devServer.proxy 가 같은 origin 으로 만들어 준다.
// 즉 CORS 가 필요한 호출자가 없다. 반면 "*" 를 열어두면 임의의 사이트에 심긴
// 스크립트가 /api/v1/capsule/list?email= 응답을 읽어갈 수 있다
// (인증이 없으므로 쿠키 없이도 성공한다 — 이메일만 알면 목록이 털린다).
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/capsule")
public class LetterController {

    private final LetterService letterService;

    /**
     * 타임캡슐 생성 (보내기)
     */
    @PostMapping
    public ResponseEntity<CapsuleResponse> create(@RequestBody @Valid CapsuleCreateRequest request) {
        LetterPaper letter = letterService.sendLetter(request);
        return ResponseEntity.ok(CapsuleResponse.from(letter));
    }

    /**
     * 이메일로 타임캡슐 목록 조회
     */
    @GetMapping("/list")
    public ResponseEntity<List<CapsuleResponse>> listByEmail(@RequestParam String email) {
        return ResponseEntity.ok(letterService.listByEmail(email).stream()
                .map(CapsuleResponse::from)
                .toList());
    }

    /**
     * 타임캡슐 개봉 (상세 조회)
     */
    @PostMapping("/{id}/open")
    public ResponseEntity<CapsuleResponse> open(@PathVariable Long id, @RequestBody @Valid CapsuleOpenRequest request) {
        LetterPaper letter = letterService.getDetail(id, request.getPassword());
        boolean includeContent = letter.getOpenAt() == null || !java.time.ZonedDateTime.now().isBefore(letter.getOpenAt());
        return ResponseEntity.ok(CapsuleResponse.from(letter, includeContent));
    }

    /**
     * 공개된 타임캡슐 탐색
     */
    @GetMapping("/public")
    public ResponseEntity<List<CapsuleResponse>> listPublic() {
        return ResponseEntity.ok(letterService.findPublicCapsules().stream()
                .map(CapsuleResponse::publicView)
                .toList());
    }
}
