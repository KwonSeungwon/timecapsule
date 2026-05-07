package com.mini.timecapsule.service;

import com.mini.timecapsule.dao.ContentFilterRepository;
import com.mini.timecapsule.dao.LetterPaperRepository;
import com.mini.timecapsule.dao.UserRepository;
import com.mini.timecapsule.dto.CapsuleCreateRequest;
import com.mini.timecapsule.dto.LetterDto;
import com.mini.timecapsule.dto.SendCapsuleDto;
import com.mini.timecapsule.exception.CustomException;
import com.mini.timecapsule.exception.ExceptionStructure;
import com.mini.timecapsule.model.ContentFilter;
import com.mini.timecapsule.model.LetterPaper;
import com.mini.timecapsule.model.User;
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

    private final UserRepository userRepository;
    private final LetterPaperRepository letterPaperRepository;
    private final ContentFilterRepository contentFilterRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void sendLetter(CapsuleCreateRequest request) {
        // 비속어 필터링 검사
        int filteringCount = letterFilteringContent(request.getContent());
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

        letterPaperRepository.save(letter);
    }

    /**
     * @deprecated Use {@link #sendLetter(CapsuleCreateRequest)} instead.
     */
    @Deprecated
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

        // For backward compatibility, we can adapt SendCapsuleDto to CapsuleCreateRequest
        CapsuleCreateRequest request = CapsuleCreateRequest.builder()
                .senderName(sendCapsuleDto.getSender())
                .title(sendCapsuleDto.getTitle())
                .receiverEmail(opUser.get().getEmail()) // Use user's email as receiver for member flow
                .password(sendCapsuleDto.getPassword())
                .content(sendCapsuleDto.getContent())
                .openAt(sendCapsuleDto.getOpenAt())
                .isPublic(sendCapsuleDto.getIsPublic())
                .letterPaperType(sendCapsuleDto.getLetterPaperType())
                .capsuleType(sendCapsuleDto.getCapsuleType())
                .imageUrl(sendCapsuleDto.getImageUrl())
                .requestorInfo(sendCapsuleDto.getRequestorInfo())
                .build();

        sendLetter(request);
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
        List<LetterPaper> letters = letterPaperRepository.findByReceiverEmail(email);
        // 리스트에서는 내용을 숨김 처리
        letters.forEach(letter -> letter.setContent(null));
        return letters;
    }

    /**
     * 편지 상세 조회 (비밀번호 확인 포함)
     */
    @Transactional(readOnly = true)
    public LetterPaper getDetail(Long id, String password) {
        LetterPaper letter = getLetter(id);

        if (!passwordEncoder.matches(password, letter.getPassword())) {
            throw new CustomException(ExceptionStructure.INVALID_PASSWORD);
        }

        // 개봉 예정일 확인 및 내용 숨김 처리
        if (letter.getOpenAt() != null && ZonedDateTime.now().isBefore(letter.getOpenAt())) {
            letter.setContent(null);
            letter.setImageUrl(null);
        }

        return letter;
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
        // User user = null; 
        // return letterPaperRepository.findByUser(user);
        return null;
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
