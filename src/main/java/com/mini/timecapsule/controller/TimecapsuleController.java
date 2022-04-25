package com.mini.timecapsule.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import com.mini.timecapsule.service.TimeCapsuleService;

@Controller
public class TimecapsuleController {
    @Autowired
    private TimeCapsuleService timeCapsuleService;

    @GetMapping(value = "/timecapsule")
    public ModelAndView timecapsule() {
        timeCapsuleService.create();
        timeCapsuleService.list();
        return new ModelAndView("home");
    }
    @GetMapping(value = "/api/timecapsule")
    public ModelAndView list() {
        ModelAndView modelAndView = new ModelAndView();
        timeCapsuleService.list();
        return null;
    }
    @PostMapping("/timecapsule")
    public ModelAndView create() {

        timeCapsuleService.create();
        ModelAndView modelAndView = new ModelAndView("home");

        return modelAndView;
    }
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
