package com.mini.timecapsule.service;

import com.mini.timecapsule.dao.ContentFilterRepository;
import com.mini.timecapsule.dao.LetterPaperRepository;
import com.mini.timecapsule.dao.UserRepository;
import com.mini.timecapsule.dto.CapsuleCreateRequest;
import com.mini.timecapsule.exception.CustomException;
import com.mini.timecapsule.model.LetterPaper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    void testListByEmail() {
        // Given
        String email = "receiver@example.com";
        LetterPaper letter = new LetterPaper();
        letter.setReceiverEmail(email);
        letter.setContent("Secret content");

        when(letterPaperRepository.findByReceiverEmail(email)).thenReturn(Collections.singletonList(letter));

        // When
        List<LetterPaper> result = letterService.listByEmail(email);

        // Then
        assertEquals(1, result.size());
        assertNull(result.get(0).getContent()); // Content should be hidden in list
        verify(letterPaperRepository, times(1)).findByReceiverEmail(email);
    }

    @Test
    void testGetDetail_Success() {
        // Given
        Long id = 1L;
        String password = "password123";
        LetterPaper letter = new LetterPaper();
        letter.setId(id);
        letter.setPassword("encryptedPassword");
        letter.setContent("Secret content");
        letter.setOpenAt(ZonedDateTime.now().minusDays(1)); // Already openable

        when(letterPaperRepository.findById(id)).thenReturn(Optional.of(letter));
        when(passwordEncoder.matches(password, "encryptedPassword")).thenReturn(true);

        // When
        LetterPaper result = letterService.getDetail(id, password);

        // Then
        assertEquals("Secret content", result.getContent());
        verify(passwordEncoder, times(1)).matches(password, "encryptedPassword");
    }

    @Test
    void testGetDetail_BeforeOpenAt() {
        // Given
        Long id = 1L;
        String password = "password123";
        LetterPaper letter = new LetterPaper();
        letter.setId(id);
        letter.setPassword("encryptedPassword");
        letter.setContent("Secret content");
        letter.setOpenAt(ZonedDateTime.now().plusDays(1)); // Not yet openable

        when(letterPaperRepository.findById(id)).thenReturn(Optional.of(letter));
        when(passwordEncoder.matches(password, "encryptedPassword")).thenReturn(true);

        // When
        LetterPaper result = letterService.getDetail(id, password);

        // Then
        assertNull(result.getContent()); // Content should be hidden if not yet openable
    }

    @Test
    void testGetDetail_WrongPassword() {
        // Given
        Long id = 1L;
        String password = "wrongPassword";
        LetterPaper letter = new LetterPaper();
        letter.setId(id);
        letter.setPassword("encryptedPassword");

        when(letterPaperRepository.findById(id)).thenReturn(Optional.of(letter));
        when(passwordEncoder.matches(password, "encryptedPassword")).thenReturn(false);

        // When & Then
        assertThrows(CustomException.class, () -> letterService.getDetail(id, password));
    }
}
