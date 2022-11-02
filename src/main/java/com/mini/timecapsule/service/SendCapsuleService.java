package com.mini.timecapsule.service;

import com.mini.timecapsule.dao.CoordinatesRepository;
import com.mini.timecapsule.dao.LetterPaperRepository;
import com.mini.timecapsule.dao.UserRepository;
import com.mini.timecapsule.dto.SendCapsuleDto;
import com.mini.timecapsule.model.LetterPaper;
import com.mini.timecapsule.model.QCoordinates;
import com.mini.timecapsule.model.User;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SendCapsuleService {

    private final CoordinatesRepository coordinatesRepository;
    private final UserRepository userRepository;
    private final LetterPaperRepository letterPaperRepository;

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

    public void sendLetter(SendCapsuleDto sendCapsuleDto) {

        Optional<User> opUser = userRepository.findById(sendCapsuleDto.getUserId());

        if (!opUser.isPresent()) {
            //에러처리
        }

        //편지지 내용 검사 sendCapsuleDto.getContent()

        //블락 유저 체크 sendCapsuleDto.getRequestorInfo()

        LetterPaper letter = LetterPaper.newEntity(sendCapsuleDto.getSender(),
                opUser.get(), sendCapsuleDto.getLetterPaperType(), sendCapsuleDto.getContent(),
                sendCapsuleDto.getRequestorInfo());

        letterPaperRepository.save(letter);
    }
    
}
