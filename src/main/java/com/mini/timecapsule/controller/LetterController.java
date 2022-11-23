package com.mini.timecapsule.controller;

import com.mini.timecapsule.dto.LetterDto;
import com.mini.timecapsule.dto.SendCapsuleDto;
import com.mini.timecapsule.model.LetterPaper;
import com.mini.timecapsule.service.LetterService;
import com.mini.timecapsule.utils.bind.DeleteRestMapping;
import com.mini.timecapsule.utils.bind.GetRestMapping;
import com.mini.timecapsule.utils.bind.PostRestMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*")
public class LetterController {

    private final LetterService letterService;

    @GetRestMapping("/api/v1/letter")
    public ResponseEntity<List<LetterPaper>> list(@RequestBody LetterDto letterDto) {
        return ResponseEntity.ok(letterService.list(letterDto));
    }

    @PostRestMapping("/api/v1/letter")
    public void send(@RequestBody SendCapsuleDto sendCapsuleDto) {
        letterService.sendLetter(sendCapsuleDto);

    }

    @PostRestMapping("/api/v1/letter/{id}")
    public void read(@PathVariable("id") Long id, @RequestBody LetterDto letterDto) {
        letterService.readLetter(letterDto);
    }

    @DeleteRestMapping("/api/v1/letter/{id}")
    public void remove(@PathVariable("id") Long id, @RequestBody LetterDto letterDto) {
        letterService.removeLetter(letterDto);
    }


}
