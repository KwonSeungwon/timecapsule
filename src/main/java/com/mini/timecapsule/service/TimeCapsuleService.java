package com.mini.timecapsule.service;

import com.mini.timecapsule.dto.TimeCapsuleDto;
import com.mini.timecapsule.model.QTimeCapsule;
import com.mini.timecapsule.model.TimeCapsule;
import com.mini.timecapsule.repository.TimeCapsuleRepository;
import com.mini.timecapsule.utils.CustomWebUtils;
import com.querydsl.core.BooleanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TimeCapsuleService {

    @Autowired
    private TimeCapsuleRepository timeCapsuleRepository;

    public void list(CustomWebUtils customWebUtils, TimeCapsuleDto timeCapsuleDto) {
//        BooleanBuilder predicate = new BooleanBuilder();
//        QTimeCapsule qTimeCapsule = QTimeCapsule.timeCapsule;
//
//        if (timeCapsuleDto.getId() != null) {
//            predicate.and(qTimeCapsule.id.eq(timeCapsuleDto.getId()));
//        }
//        if (timeCapsuleDto.getName() != null) {
//            predicate.and(qTimeCapsule.name.eq(timeCapsuleDto.getName()));
//        }
//        if (timeCapsuleDto.getDate() != null) {
////            ZonedDateTime start =
//        }
//
//        Iterable<TimeCapsule> list = timeCapsuleRepository.findAll(predicate);
    }

    public void create(CustomWebUtils customWebUtils, TimeCapsuleDto timeCapsuleDto) {
//        int sendingCount = 0;
//        TimeCapsule timeCapsule = TimeCapsule.newEntity(sendingCount++, timeCapsuleDto.getUiUrl(), timeCapsuleDto.getName(), timeCapsuleDto.getEmail());
//        timeCapsuleRepository.save(timeCapsule);
//        log.info("Save new Data");
    }

    public void update() {
//        timeCapsuleRepository.save();
    }

    public void delete() {
//        timeCapsuleRepository.delete();
    }

}
