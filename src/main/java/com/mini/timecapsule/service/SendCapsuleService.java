package com.mini.timecapsule.service;

import com.mini.timecapsule.dao.CoordinatesRepository;
import com.mini.timecapsule.dto.SendCapsuleDto;
import com.mini.timecapsule.model.QCoordinates;
import com.querydsl.core.BooleanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SendCapsuleService {

    @Autowired
    private CoordinatesRepository coordinatesRepository;
    
    /**
       front-end에서 전달된 좌표값을 통해 실제 존재하는 좌표값인지 확인후 결과를 리턴 
     */
    public Boolean validCoordinates(SendCapsuleDto sendCapsuleDto) {

        QCoordinates qcoordinates = QCoordinates.coordinates;
        BooleanBuilder predicate = new BooleanBuilder();

        if (sendCapsuleDto.getCoordinates() != null) {
            //임시코드
            String[] coordinates = sendCapsuleDto.getCoordinates().split(",");
            predicate.and(qcoordinates.xCoordinates.eq(coordinates[0]));
            predicate.and(qcoordinates.yCoordinates.eq(coordinates[1]));
        }

        return coordinatesRepository.findOne(predicate).isPresent();
    }
}
