package com.mini.timecapsule.controller;

import com.mini.timecapsule.dto.SendCapsuleDto;
import com.mini.timecapsule.dto.UserDto;
import com.mini.timecapsule.model.Coordinates;
import com.mini.timecapsule.model.User;
import com.mini.timecapsule.service.CoordinatesService;
import com.mini.timecapsule.service.UserService;
import com.mini.timecapsule.utils.bind.GetRestMapping;
import com.mini.timecapsule.utils.bind.PostRestMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/timecapsule/*")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final CoordinatesService coordinatesService;

    @GetRestMapping("/coordinates/valid")
    public Boolean validCoordinates(SendCapsuleDto sendCapsuleDto) {

        return coordinatesService.validCoordinates(sendCapsuleDto);
    }

    @GetRestMapping(value = "/join")
    public Coordinates join() {

        return userService.join();
    }

    @GetRestMapping(value = "/user")
    public ModelAndView get(UserDto userDTO) {

        ModelAndView mv = new ModelAndView();
        User user = userService.getUser(userDTO);
        mv.addObject("user", user);

        return mv;
    }
    @PostRestMapping(value = "/api/timecapsule/user")
    public void createUser(@RequestBody UserDto userDTO) {

        userService.createUser(userDTO);
    }

    @GetMapping(value = "/users")
    public ModelAndView getList(UserDto userDTO) {

        userService.getUserList(userDTO);

        return null;
    }


    @PutMapping(value = "/user")
    public ModelAndView update(UserDto userDTO) {

        return null;
    }

    @DeleteMapping(value = "/user")
    public ModelAndView delete(UserDto userDTO) {

        return null;
    }

}
