package com.mini.timecapsule.controller;

import com.mini.timecapsule.dto.SendCapsuleDto;
import com.mini.timecapsule.service.SendCapsuleService;
import com.mini.timecapsule.utils.bind.GetRestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class SendCapsuleController {

    @Autowired
    private SendCapsuleService sendCapsuleService;

    @GetRestMapping("/api/valid/Coordinates")
    public Boolean validCoordinates(SendCapsuleDto sendCapsuleDto) {

        return true;
//        return sendCapsuleService.validCoordinates(sendCapsuleDto);
    }



}
