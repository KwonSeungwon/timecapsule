package com.mini.timecapsule.controller;

import com.mini.timecapsule.dto.UserDTO;
import com.mini.timecapsule.service.UserService;
import com.mini.timecapsule.utils.Payload;
import com.mini.timecapsule.utils.PayloadImpl;
import com.mini.timecapsule.utils.bind.GetRestMapping;
import com.mini.timecapsule.utils.bind.PostRestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostRestMapping(value = "/api/timecapsule/user")
    public ResponseEntity<ModelAndView> get(Payload payload, UserDTO userDTO) {

        userService.getUser(payload, userDTO);

        return ResponseEntity.ok(payload.toModelAndView());
        //return "success";
    }
    @GetRestMapping(value = "/api/timecapsule/userCreate")
    public void createUser(PayloadImpl payload, UserDTO userDTO) {

//        System.out.println(userDTO);
//        return userService.createUser(payload, userDTO);
        userService.createUser(payload, userDTO);
    }

    @GetMapping(value = "/api/timecapsule/users")
    public ModelAndView getList(Payload payload, UserDTO userDTO) {

        userService.getUserList(payload, userDTO);

        return payload.toModelAndView();
    }


    @PutMapping(value = "/api/timecapsule/user")
    public ModelAndView update(Payload payload, UserDTO userDTO) {

        return payload.toModelAndView();
    }

    @DeleteMapping(value = "/api/timecapsule/user")
    public ModelAndView delete(Payload payload, UserDTO userDTO) {

        return payload.toModelAndView();
    }

}
