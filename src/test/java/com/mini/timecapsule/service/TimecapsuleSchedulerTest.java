package com.mini.timecapsule.service;

import com.mini.timecapsule.dao.LetterPaperRepository;
import com.mini.timecapsule.model.LetterPaper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TimecapsuleSchedulerTest {

    @Mock
    private LetterPaperRepository letterPaperRepository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private TimecapsuleScheduler timecapsuleScheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckAndNotifyOpenCapsules() {
        // Given
        String receiverEmail = "test@example.com";
        LetterPaper capsule = new LetterPaper();
        capsule.setId(1L);
        capsule.setReceiverEmail(receiverEmail);
        capsule.setStatus(LetterPaper.LetterStatus.UNOPENED);
        capsule.setIsNotified(false);
        capsule.setOpenAt(ZonedDateTime.now().minusHours(1));

        when(letterPaperRepository.findAllByOpenAtBeforeAndIsNotifiedFalseAndStatus(any(ZonedDateTime.class), eq(LetterPaper.LetterStatus.UNOPENED)))
                .thenReturn(Collections.singletonList(capsule));

        // When
        timecapsuleScheduler.checkAndNotifyOpenCapsules();

        // Then
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());
        
        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals(receiverEmail, sentMessage.getTo()[0]);
        assertEquals("[TimeCapsule] 당신에게 도착한 비밀 편지가 있습니다.", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains(receiverEmail));
        assertTrue(sentMessage.getText().contains("링크: http://timecapsule.chatflow.ai.kr/my-capsule?email="));

        verify(letterPaperRepository, times(1)).save(capsule);
        assertTrue(capsule.getIsNotified());
    }

    @Test
    void testCheckAndNotifyOpenCapsules_NoEmail() {
        // Given
        LetterPaper capsule = new LetterPaper();
        capsule.setId(1L);
        capsule.setReceiverEmail(null); // No email
        capsule.setStatus(LetterPaper.LetterStatus.UNOPENED);
        capsule.setIsNotified(false);

        when(letterPaperRepository.findAllByOpenAtBeforeAndIsNotifiedFalseAndStatus(any(ZonedDateTime.class), eq(LetterPaper.LetterStatus.UNOPENED)))
                .thenReturn(Collections.singletonList(capsule));

        // When
        timecapsuleScheduler.checkAndNotifyOpenCapsules();

        // Then
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
        verify(letterPaperRepository, never()).save(any(LetterPaper.class));
    }
}
