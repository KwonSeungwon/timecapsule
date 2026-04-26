package com.mini.timecapsule.controller;

import com.mini.timecapsule.dto.SendCapsuleDto;
import com.mini.timecapsule.dto.UserDto;
//import com.mini.timecapsule.jwt.TokenProvider;
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
import org.springframework.http.ResponseEntity;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/timecapsule")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final CoordinatesService coordinatesService;
    /**
     * JWT 토큰 관리
     */
//    private final TokenProvider tokenProvider;


    @GetRestMapping("/coordinates/valid")
    public Boolean validCoordinates(SendCapsuleDto sendCapsuleDto) {

        return coordinatesService.validCoordinates(sendCapsuleDto);
    }
    @PostMapping("/login")
    public ResponseEntity<?> checkLogin(@Valid @RequestBody UserDto dto){
        try {
            User user = userService.getUser(dto);
            // In a real app we'd return a JWT token, for this side project we can return the user info
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Login failed");
        }
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
    @PostRestMapping(value = "/user")
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
