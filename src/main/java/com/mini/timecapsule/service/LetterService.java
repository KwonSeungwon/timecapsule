package com.mini.timecapsule.service;

import com.mini.timecapsule.dao.ContentFilterRepository;
import com.mini.timecapsule.dao.CoordinatesRepository;
import com.mini.timecapsule.dao.LetterPaperRepository;
import com.mini.timecapsule.dao.UserRepository;
import com.mini.timecapsule.dto.LetterDto;
import com.mini.timecapsule.dto.SendCapsuleDto;
import com.mini.timecapsule.exception.CustomException;
import com.mini.timecapsule.exception.ExceptionStructure;
import com.mini.timecapsule.model.ContentFilter;
import com.mini.timecapsule.model.LetterPaper;
import com.mini.timecapsule.model.QCoordinates;
import com.mini.timecapsule.model.User;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 편지관련 제어 공통서비스로 변경
 */
@Service
@RequiredArgsConstructor
public class LetterService {

    private final UserRepository userRepository;
    private final LetterPaperRepository letterPaperRepository;
    private final ContentFilterRepository contentFilterRepository;

    public void sendLetter(SendCapsuleDto sendCapsuleDto) {

        Optional<User> opUser = userRepository.findById(sendCapsuleDto.getUserId());

        if (!opUser.isPresent()) {
            //에러처리
            throw new CustomException(ExceptionStructure.NOT_FOUND_USER);
        }

        //편지지 내용 검사 sendCapsuleDto.getContent()
        int filteringCount = letterFilteringContent(sendCapsuleDto.getContent());
        if (filteringCount > 0) {

        }


        //블락 유저 체크 sendCapsuleDto.getRequestorInfo()

        LetterPaper letter = LetterPaper.newEntity(sendCapsuleDto.getSender(),
                opUser.get(), sendCapsuleDto.getLetterPaperType(), sendCapsuleDto.getContent(),
                sendCapsuleDto.getRequestorInfo());

        letterPaperRepository.save(letter);

    }

    private int letterFilteringContent(String content) {
        List<String> keywords = contentFilterRepository.findFilterKeywordByUsed(ContentFilter.Used.ACTIVE);
        int count = 0;

        for (String keyword : keywords) {
            if(content.contains(keyword)) count++;
        }

        return count;
    }

    private LetterPaper getLetter(Long id) {
        if (id == null) {
            //에러처리
        }

        Optional<LetterPaper> opLetter = letterPaperRepository.findById(id);

        if (!opLetter.isPresent()) {
            //에러처리
        }

        return opLetter.get();
    }

    /**
     * 편지 리스트를 가져오는 함수
     * @param letterDto
     */
    public List<LetterPaper> list(LetterDto letterDto) {

        User user = null;

        //세션통해 유저아이디 받아오기

        List<LetterPaper> letters = letterPaperRepository.findByUser(user);

        return letters;
    }

    public void readLetter(LetterDto letterDto) {

        LetterPaper letter = getLetter(letterDto.getId());

        letter.read();
    }

    public void removeLetter(LetterDto letterDto) {

        LetterPaper letter = getLetter(letterDto.getId());

        /*
        본인 세션정보에서 자기편지인지 체크
        if (letter.getUser().equals(세션정보)) {
            //에러처리
        }
         */

        letter.remove();
    }

}
