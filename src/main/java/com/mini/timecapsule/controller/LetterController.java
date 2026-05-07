package com.mini.timecapsule.controller;

import com.mini.timecapsule.dto.CapsuleCreateRequest;
import com.mini.timecapsule.dto.CapsuleOpenRequest;
import com.mini.timecapsule.model.LetterPaper;
import com.mini.timecapsule.service.LetterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/capsule")
@CrossOrigin(origins = "*")
public class LetterController {

    private final LetterService letterService;

    /**
     * 타임캡슐 생성 (보내기)
     */
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid CapsuleCreateRequest request) {
        letterService.sendLetter(request);
        return ResponseEntity.ok().build();
    }

    /**
     * 이메일로 타임캡슐 목록 조회
     */
    @GetMapping("/list")
    public ResponseEntity<List<LetterPaper>> listByEmail(@RequestParam String email) {
        return ResponseEntity.ok(letterService.listByEmail(email));
    }

    /**
     * 타임캡슐 개봉 (상세 조회)
     */
    @PostMapping("/{id}/open")
    public ResponseEntity<LetterPaper> open(@PathVariable Long id, @RequestBody CapsuleOpenRequest request) {
        return ResponseEntity.ok(letterService.getDetail(id, request.getPassword()));
    }

    /**
     * 공개된 타임캡슐 탐색
     */
    @GetMapping("/public")
    public ResponseEntity<List<LetterPaper>> listPublic() {
        return ResponseEntity.ok(letterService.findPublicCapsules());
    }
}
