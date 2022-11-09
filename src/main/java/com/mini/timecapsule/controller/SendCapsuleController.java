package com.mini.timecapsule.controller;

import com.mini.timecapsule.dto.LetterDto;
import com.mini.timecapsule.dto.SendCapsuleDto;
import com.mini.timecapsule.service.LetterService;
import com.mini.timecapsule.utils.bind.DeleteRestMapping;
import com.mini.timecapsule.utils.bind.GetRestMapping;
import com.mini.timecapsule.utils.bind.PostRestMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*")
public class SendCapsuleController {

    private final LetterService letterService;

    @GetRestMapping("/api/valid/Coordinates")
    public Boolean validCoordinates(SendCapsuleDto sendCapsuleDto) {

        return true;
//        return sendCapsuleService.validCoordinates(sendCapsuleDto);
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
