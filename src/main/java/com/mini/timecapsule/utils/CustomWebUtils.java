package com.mini.timecapsule.utils;

import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;

public interface CustomWebUtils {

    HashMap<String, Object> data = new HashMap<>();


    ModelAndView modelAndView(String target);
    default ModelAndView callEmptyPage() {return this.modelAndView(null);}

    default ModelAndView loadTargetPage(String target) {
        return this.modelAndView(target);
    }

}
