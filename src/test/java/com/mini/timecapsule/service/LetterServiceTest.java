package com.mini.timecapsule.service;

import com.mini.timecapsule.dao.ContentFilterRepository;
import com.mini.timecapsule.dao.LetterPaperRepository;
import com.mini.timecapsule.dao.UserRepository;
import com.mini.timecapsule.dto.CapsuleCreateRequest;
import com.mini.timecapsule.model.LetterPaper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.ZonedDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LetterServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LetterPaperRepository letterPaperRepository;

    @Mock
    private ContentFilterRepository contentFilterRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private LetterService letterService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendLetter() {
        // Given
        CapsuleCreateRequest request = CapsuleCreateRequest.builder()
                .senderName("Sender")
                .title("Title")
                .receiverEmail("receiver@example.com")
                .password("password123")
                .content("Hello, this is a time capsule.")
                .openAt(ZonedDateTime.now().plusYears(1))
                .isPublic(true)
                .letterPaperType(LetterPaper.LetterPaperType.LETTER)
                .capsuleType(LetterPaper.CapsuleType.BOTTLE)
                .build();

        when(contentFilterRepository.findFilterKeywordByUsed(any())).thenReturn(Collections.emptyList());
        when(passwordEncoder.encode(any())).thenReturn("encryptedPassword");

        // When
        letterService.sendLetter(request);

        // Then
        verify(letterPaperRepository, times(1)).save(any(LetterPaper.class));
        verify(passwordEncoder, times(1)).encode("password123");
    }
}
