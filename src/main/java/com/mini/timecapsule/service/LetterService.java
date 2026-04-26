package com.mini.timecapsule.service;

import com.mini.timecapsule.dao.ContentFilterRepository;
import com.mini.timecapsule.dao.LetterPaperRepository;
import com.mini.timecapsule.dao.UserRepository;
import com.mini.timecapsule.dto.LetterDto;
import com.mini.timecapsule.dto.SendCapsuleDto;
import com.mini.timecapsule.exception.CustomException;
import com.mini.timecapsule.exception.ExceptionStructure;
import com.mini.timecapsule.model.ContentFilter;
import com.mini.timecapsule.model.LetterPaper;
import com.mini.timecapsule.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 편지관련 제어 공통서비스
 */
@Service
@RequiredArgsConstructor
public class LetterService {

    private final UserRepository userRepository;
    private final LetterPaperRepository letterPaperRepository;
    private final ContentFilterRepository contentFilterRepository;

    @Transactional
    public void sendLetter(SendCapsuleDto sendCapsuleDto) {

        Optional<User> opUser = userRepository.findById(sendCapsuleDto.getUserId());

        if (!opUser.isPresent()) {
            throw new CustomException(ExceptionStructure.NOT_FOUND_USER);
        }

        // 비속어 필터링 검사
        int filteringCount = letterFilteringContent(sendCapsuleDto.getContent());
        if (filteringCount > 0) {
            throw new CustomException(ExceptionStructure.INVALID_CONTENT);
        }

        LetterPaper letter = LetterPaper.newEntity(
                sendCapsuleDto.getSender(),
                sendCapsuleDto.getTitle(),
                opUser.get(),
                sendCapsuleDto.getLetterPaperType(),
                sendCapsuleDto.getCapsuleType(),
                sendCapsuleDto.getContent(),
                sendCapsuleDto.getImageUrl(),
                sendCapsuleDto.getRequestorInfo(),
                sendCapsuleDto.getOpenAt(),
                sendCapsuleDto.getIsPublic()
        );

        letterPaperRepository.save(letter);
    }

    private int letterFilteringContent(String content) {
        List<String> keywords = contentFilterRepository.findFilterKeywordByUsed(ContentFilter.Used.ACTIVE);
        int count = 0;

        for (String keyword : keywords) {
            if (content.contains(keyword)) count++;
        }

        return count;
    }

    private LetterPaper getLetter(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        Optional<LetterPaper> opLetter = letterPaperRepository.findById(id);

        if (!opLetter.isPresent()) {
            throw new CustomException("LETTER NOT FOUND");
        }

        return opLetter.get();
    }

    /**
     * 편지 리스트를 가져오는 함수
     */
    public List<LetterPaper> list(LetterDto letterDto) {
        // TODO: 세션을 통해 유저 정보를 가져오는 로직 구현 필요
        User user = null; 
        return letterPaperRepository.findByUser(user);
    }

    /**
     * 공개된 타임캡슐 탐색 (상용화 추가 기능)
     */
    public List<LetterPaper> findPublicCapsules() {
        return letterPaperRepository.findAllByIsPublicTrue();
    }

    @Transactional
    public void readLetter(LetterDto letterDto) {
        LetterPaper letter = getLetter(letterDto.getId());
        
        // 개봉 가능 시점 체크는 LetterPaper.read() 엔티티 내부에서 수행
        letter.read();
        letterPaperRepository.save(letter);
    }

    @Transactional
    public void removeLetter(LetterDto letterDto) {
        LetterPaper letter = getLetter(letterDto.getId());
        letter.remove();
        letterPaperRepository.save(letter);
    }

}
