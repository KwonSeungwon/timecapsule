package com.mini.timecapsule.controller;

import com.mini.timecapsule.service.CoordinatesService;
import com.mini.timecapsule.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;


/**
 *  @RequestMapping(method = RequestMethod.XYZ) 보다
 *  @GetMapping, @PostMapping, @PutMapping, @PatchMapping ,@DeleteMapping을 사용할 것을 권장함..
 */
@Controller
public class HomeController {

    @Autowired
    private CoordinatesService coordinatesService;

    @GetMapping("/")
    public String hello(){

        coordinatesService.initializationCoordinate();

        return "home";
    }

}
