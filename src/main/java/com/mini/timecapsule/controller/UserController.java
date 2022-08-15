package com.mini.timecapsule.controller;

import com.mini.timecapsule.dto.UserDTO;
import com.mini.timecapsule.service.UserService;
import com.mini.timecapsule.utils.CustomWebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(value = "/api/timecapsule/user")
    public ResponseEntity<ModelAndView> get(CustomWebUtils.PayloadImpl payload, UserDTO userDTO) {

        userService.getUser(payload, userDTO);

        return ResponseEntity.ok(payload.toModelAndView());
        //return "success";
    }
    @GetMapping(value = "/api/timecapsule/userCreate")
    public ModelAndView createUser(CustomWebUtils.Payload payload, UserDTO userDTO) {

        System.out.println(userDTO);
        userService.createUser(payload, userDTO);

        return payload.toModelAndView();
    }

    @GetMapping(value = "/api/timecapsule/users")
    public ModelAndView getList(CustomWebUtils.Payload payload, UserDTO userDTO) {

        userService.getUserList(payload, userDTO);

        return payload.toModelAndView();
    }
/*
    @PostMapping(value = "/api/timecapsule/user")
    public ModelAndView post(CustomWebUtils.Payload payload, UserDTO userDTO) {

        return payload.toModelAndView();
    }*/

    @PutMapping(value = "/api/timecapsule/user")
    public ModelAndView update(CustomWebUtils.Payload payload, UserDTO userDTO) {

        return payload.toModelAndView();
    }

    @DeleteMapping(value = "/api/timecapsule/user")
    public ModelAndView delete(CustomWebUtils.Payload payload, UserDTO userDTO) {

        return payload.toModelAndView();
    }

}
