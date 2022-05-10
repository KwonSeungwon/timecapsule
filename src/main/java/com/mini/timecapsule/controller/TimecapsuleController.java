package com.mini.timecapsule.controller;

import com.mini.timecapsule.dto.TimeCapsuleDto;
import com.mini.timecapsule.service.TimeCapsuleService;
import com.mini.timecapsule.utils.CustomWebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TimecapsuleController {
    @Autowired
    private TimeCapsuleService timeCapsuleService;

    @GetMapping(value = "/timecapsule")
    public ModelAndView timecapsule() {
        return new ModelAndView("home");
    }
    @GetMapping(value = "/api/timecapsule")
    public ModelAndView list(CustomWebUtils customWebUtils, TimeCapsuleDto timeCapsuleDto) {
        ModelAndView modelAndView = new ModelAndView();
        timeCapsuleService.list(customWebUtils, timeCapsuleDto);
        return customWebUtils.callEmptyPage();
    }
    @PostMapping("/api/timecapsule")
    public ModelAndView create(CustomWebUtils customWebUtils, @RequestBody TimeCapsuleDto timeCapsuleDto) {

        timeCapsuleService.create(customWebUtils, timeCapsuleDto);

        return customWebUtils.callEmptyPage();
    }

//    @PatchMapping("/api/timecapsule") //트렌드는 patch인데 뭘 써야할까?
    @PutMapping("/api/timecapsule")
    public ModelAndView update() {
        timeCapsuleService.update();
        return null;
    }
    @DeleteMapping("/api/timecapsule")
    public ModelAndView delete() {
        timeCapsuleService.delete();
        return null;
    }
}
