package com.mini.timecapsule.controller;

import com.mini.timecapsule.dto.UserDTO;
import com.mini.timecapsule.model.Coordinates;
import com.mini.timecapsule.service.UserService;
import com.mini.timecapsule.utils.bind.GetRestMapping;
import com.mini.timecapsule.utils.bind.PostRestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @PostRestMapping(value = "/api/timecapsule/user")
    public ModelAndView get(UserDTO userDTO) {

        userService.getUser(userDTO);

        return null;
    }
    @GetRestMapping(value = "/api/timecapsule/userCreate")
    public void createUser(UserDTO userDTO) {

        userService.createUser(userDTO);
    }

    @GetMapping(value = "/api/timecapsule/users")
    public ModelAndView getList(UserDTO userDTO) {

        userService.getUserList(userDTO);

        return null;
    }


    @PutMapping(value = "/api/timecapsule/user")
    public ModelAndView update(UserDTO userDTO) {

        return null;
    }

    @DeleteMapping(value = "/api/timecapsule/user")
    public ModelAndView delete(UserDTO userDTO) {

        return null;
    }

}
