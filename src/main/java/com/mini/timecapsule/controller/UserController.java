package com.mini.timecapsule.controller;

import com.mini.timecapsule.dto.UserDTO;
import com.mini.timecapsule.service.UserService;
import com.mini.timecapsule.utils.CustomWebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.ModelAndView;

public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "/api/timecapsule/user")
    public ModelAndView get(CustomWebUtils.Payload payload, UserDTO userDTO) {

        userService.getUser(payload, userDTO);

        return payload.toModelAndView();
    }

    @GetMapping(value = "/api/timecapsule/users")
    public ModelAndView getList(CustomWebUtils.Payload payload, UserDTO userDTO) {

        userService.getUserList(payload, userDTO);

        return payload.toModelAndView();
    }

    @PostMapping(value = "/api/timecapsule/user")
    public ModelAndView post(CustomWebUtils.Payload payload, UserDTO userDTO) {

        return payload.toModelAndView();
    }

    @PutMapping(value = "/api/timecapsule/user")
    public ModelAndView update(CustomWebUtils.Payload payload, UserDTO userDTO) {

        return payload.toModelAndView();
    }

    @DeleteMapping(value = "/api/timecapsule/user")
    public ModelAndView delete(CustomWebUtils.Payload payload, UserDTO userDTO) {

        return payload.toModelAndView();
    }

}
