package com.mini.timecapsule.controller;

import com.mini.timecapsule.dto.UserDto;
import com.mini.timecapsule.model.Coordinates;
import com.mini.timecapsule.service.UserService;
import com.mini.timecapsule.utils.bind.GetRestMapping;
import com.mini.timecapsule.utils.bind.PostRestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetRestMapping(value = "/api/timecapsule/join")
    public Coordinates join() {

        return userService.join();
    }

    @GetRestMapping(value = "/api/timecapsule/user")
    public ModelAndView get(UserDto userDTO) {

        userService.getUser(userDTO);

        return null;
    }
    @PostRestMapping(value = "/api/timecapsule/user")
    public void createUser(@RequestBody UserDto userDTO) {

        userService.createUser(userDTO);
    }

    @GetMapping(value = "/api/timecapsule/users")
    public ModelAndView getList(UserDto userDTO) {

        userService.getUserList(userDTO);

        return null;
    }


    @PutMapping(value = "/api/timecapsule/user")
    public ModelAndView update(UserDto userDTO) {

        return null;
    }

    @DeleteMapping(value = "/api/timecapsule/user")
    public ModelAndView delete(UserDto userDTO) {

        return null;
    }

}
