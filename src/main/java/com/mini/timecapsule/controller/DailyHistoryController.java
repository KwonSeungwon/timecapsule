package com.mini.timecapsule.controller;

import com.mini.timecapsule.model.DailyHistory;
import com.mini.timecapsule.service.DailyHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DailyHistoryController {

    private final DailyHistoryService dailyHistoryService;

//    @Scheduled(cron = "0 30 13 ? * MON-SAT")
    public void create() {

    }

    public List<DailyHistory> findAll() {
        return dailyHistoryService.findAll();
    }

}
