package com.mini.timecapsule.service;

import com.mini.timecapsule.dao.ContentFilterRepository;
import com.mini.timecapsule.dao.LetterPaperRepository;
import com.mini.timecapsule.dto.CapsuleCreateRequest;
import com.mini.timecapsule.dto.LetterDto;
import com.mini.timecapsule.exception.CustomException;
import com.mini.timecapsule.exception.ExceptionStructure;
import com.mini.timecapsule.model.ContentFilter;
import com.mini.timecapsule.model.LetterPaper;
import com.mini.timecapsule.utils.LocationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * 편지관련 제어 공통서비스
 */
@Service
@RequiredArgsConstructor
public class LetterService {

    private final LetterPaperRepository letterPaperRepository;
    private final ContentFilterRepository contentFilterRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public LetterPaper sendLetter(CapsuleCreateRequest request) {
        // 비속어 필터링 검사
        String content = request.getContent() != null ? request.getContent() : "";
        int filteringCount = letterFilteringContent(content);
        if (filteringCount > 0) {
            throw new CustomException(ExceptionStructure.INVALID_CONTENT);
        }

        // 1. 랜덤 좌표 생성 (0-999)
        Random random = new Random();
        int x = random.nextInt(1000);
        int y = random.nextInt(1000);

        // 2. 바이옴 정보 매핑
        String biome = LocationMapper.getBiome(x, y);
        String locationName = LocationMapper.getBiomeName(biome);

        // 3. 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(request.getPassword());

        // 4. 엔티티 생성 및 저장
        LetterPaper letter = LetterPaper.newEntity(
                request.getSenderName(),
                request.getTitle(),
                request.getReceiverEmail(),
                encryptedPassword,
                x,
                y,
                biome,
                locationName,
                request.getLetterPaperType(),
                request.getCapsuleType(),
                request.getContent(),
                request.getImageUrl(),
                request.getRequestorInfo(),
                request.getOpenAt(),
                request.getIsPublic()
        );

        return letterPaperRepository.save(letter);
    }


    private int letterFilteringContent(String content) {
        List<String> keywords = contentFilterRepository.findFilterKeywordByUsed(ContentFilter.Used.ACTIVE);
        int count = 0;

        for (String keyword : keywords) {
            if (content.contains(keyword)) count++;
        }

        return count;
    }

    /**
     * 이메일로 편지 리스트를 가져오는 함수
     */
    @Transactional(readOnly = true)
    public List<LetterPaper> listByEmail(String email) {
        return letterPaperRepository.findByReceiverEmail(email);
    }

    /**
     * 편지 상세 조회 (비밀번호 확인 포함)
     */
    @Transactional
    public LetterPaper getDetail(Long id, String password) {
        LetterPaper letter = getLetter(id);

        if (!passwordEncoder.matches(password, letter.getPassword())) {
            throw new CustomException(ExceptionStructure.INVALID_PASSWORD);
        }

        if (letter.getOpenAt() != null && ZonedDateTime.now().isBefore(letter.getOpenAt())) {
            return letter;
        }

        if (letter.getStatus() != LetterPaper.LetterStatus.OPENED) {
            letter.read();
        }
        return letter;
    }

    private LetterPaper getLetter(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        Optional<LetterPaper> opLetter = letterPaperRepository.findById(id);

        if (!opLetter.isPresent()) {
            throw new CustomException(ExceptionStructure.NOT_FOUND_LETTER);
        }

        return opLetter.get();
    }

    /**
     * 공개된 타임캡슐 탐색 (상용화 추가 기능)
     */
    public List<LetterPaper> findPublicCapsules() {
        return letterPaperRepository.findAllByIsPublicTrue();
    }

    /**
     * ⚠️ 현재 호출자가 없다(컨트롤러는 {@link #getDetail} 만 쓴다).
     *
     * <p>{@link #getDetail} 과 달리 개봉일 전이어도 {@code read()} 를 그대로 부르므로
     * {@code IllegalStateException} 이 나갈 수 있다. 그 예외는 더 이상 사용자 문구로
     * 번역되지 않고 일반 500 으로 처리된다. 이 메서드를 다시 배선할 거라면
     * {@link #getDetail} 처럼 개봉일을 먼저 확인하도록 고친 뒤에 쓸 것.
     */
    @Transactional
    public void readLetter(LetterDto letterDto) {
        LetterPaper letter = getLetter(letterDto.getId());

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
