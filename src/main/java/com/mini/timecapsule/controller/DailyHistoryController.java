package com.mini.timecapsule.controller;

import com.mini.timecapsule.model.DailyHistory;
import com.mini.timecapsule.service.DailyHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class DailyHistoryController {


    @Autowired
    private DailyHistoryService dailyHistoryService;

//    @Scheduled(cron = "0 30 13 ? * MON-SAT")
    public void create() {

    }

    public List<DailyHistory> findAll() {
        return dailyHistoryService.findAll();
    }

}
