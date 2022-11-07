package com.mini.timecapsule.service;

import com.mini.timecapsule.dao.CoordinatesRepository;
import com.mini.timecapsule.model.Coordinates;
import com.mini.timecapsule.model.QCoordinates;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 좌표를 관리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class CoordinatesService {

    @Autowired
    private CoordinatesRepository coordinatesRepository;

    /**
     * 좌표 생성 함수
     * x 0 ~ 999, y 0 ~ 999 100만개의 좌표 생성
     */
    public void initializationCoordinate() {

        Long count = coordinatesRepository.count();

        if (count > 0) {
            return;
        }

        List<Coordinates> coordinatesList = new ArrayList<>();
        //TODO :  테스트용으로 반복회수를 100으로 변경 원래는 1000이 맞음
        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                Coordinates coordinates = Coordinates.newCoordinates(Integer.toString(x),
                        Integer.toString(y));
                coordinatesList.add(coordinates);
            }
        }
        coordinatesRepository.saveAll(coordinatesList);
    }

    /**
     * fix되지 않은 Coordinates을 가져와서 랜덤으로 하나 던져줌
     * @return Coordinates
     */
    public Coordinates findUnFixedCoordinates() {
        List<Coordinates> unFixedCoordinates = coordinatesRepository.findByIsFixed(false);

        int max = unFixedCoordinates.size();
        int randomIndex = (int)(Math.random() * max);

        //점유된상태로 넘김
        Coordinates coordinate = unFixedCoordinates.get(randomIndex);
        coordinate.preemption();
        coordinatesRepository.save(coordinate);

        return coordinate;
    }

    /**
     * 점유되었지만 회원가입이 되지않은 좌표를 풀어줌
     * 좌표 받은지 삼십분이지나도록 미가입시 점유 풀리게
     * 스케쥴러로 생성
     */
    public void initUnLinkCoordinate() {
//        LocalDateTime limitTime = LocalDateTime.now().minusMinutes(30L);
//        List<Coordinates> targets = coordinatesRepository.findByFixedAtLitterAndLinkAtIsNull(limitTime);
//        for (Coordinates coordinate : targets) {
//            coordinate.free();
//        }
//        coordinatesRepository.saveAll(targets);
    }

}
