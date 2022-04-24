package com.mini.timecapsule.service;

import com.mini.timecapsule.model.TimeCapsule;
import com.mini.timecapsule.repository.TimeCapsuleRepository;
import com.querydsl.core.BooleanBuilder;
import model.QTimeCapsule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TimeCapsuleService {

    @Autowired
    private TimeCapsuleRepository timeCapsuleRepository;

    public void list() {
        BooleanBuilder predicate = new BooleanBuilder();
        QTimeCapsule qTimeCapsule = QTimeCapsule.timeCapsule;

        Iterable<TimeCapsule> list = timeCapsuleRepository.findAll();
        for(TimeCapsule timeCapsule : list) {
            System.out.println(timeCapsule.getId());
            System.out.println(timeCapsule.getName());
            System.out.println(timeCapsule.getEmail());
            System.out.println(timeCapsule.getUiUrl());
        }
    }

    public void create() {
        timeCapsuleRepository.save(new TimeCapsule(1, "test1", "test1", "test2"));
        timeCapsuleRepository.save(new TimeCapsule(1, "test2", "test2", "test2"));
        timeCapsuleRepository.save(new TimeCapsule(1, "test3", "test3", "test3"));
        timeCapsuleRepository.save(new TimeCapsule(1, "test4", "test4", "test4"));
        timeCapsuleRepository.save(new TimeCapsule(1, "test5", "test5", "test5"));
    }

    public void update() {
//        timeCapsuleRepository.save();
    }

    public void delete() {
//        timeCapsuleRepository.delete();
    }

}
