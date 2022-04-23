package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import service.TimeCapsuleService;

@Controller
public class TimecapsuleController {

    @Autowired
    private TimeCapsuleService timeCapsuleService;

    @RequestMapping("/")
    public String home() {
        timeCapsuleService.create();
        return "home";
    }



}
