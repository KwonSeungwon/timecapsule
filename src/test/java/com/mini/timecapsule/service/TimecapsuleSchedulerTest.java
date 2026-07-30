package com.mini.timecapsule.service;

import com.mini.timecapsule.dao.LetterPaperRepository;
import com.mini.timecapsule.model.LetterPaper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.MailSendException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

class TimecapsuleSchedulerTest {

    private static final int MAX_ATTEMPTS = 10;

    @Mock
    private LetterPaperRepository letterPaperRepository;

    @Mock
    private CapsuleNotificationMailer mailer;

    @InjectMocks
    private TimecapsuleScheduler scheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(scheduler, "maxAttempts", MAX_ATTEMPTS);
    }

    private LetterPaper dueCapsule() {
        LetterPaper capsule = LetterPaper.newEntity(
                "보낸이", "10년 뒤의 나에게", "test@example.com", "enc",
                120, 880, "DESERT", "붉은 사막",
                LetterPaper.LetterPaperType.LETTER, LetterPaper.CapsuleType.BOTTLE,
                "내용", null, null, ZonedDateTime.now().minusMinutes(1), false);
        capsule.setId(1L);
        return capsule;
    }

    private void givenDue(LetterPaper... capsules) {
        given(letterPaperRepository.findDueForNotification(any(), any(), anyInt()))
                .willReturn(new ArrayList<>(List.of(capsules)));
    }

    @Test
    void 개봉일이_지난_캡슐에_메일을_보내고_알림완료로_표시한다() {
        LetterPaper capsule = dueCapsule();
        givenDue(capsule);

        scheduler.checkAndNotifyOpenCapsules();

        then(mailer).should(times(1)).send(capsule);
        assertThat(capsule.getIsNotified()).isTrue();
        assertThat(capsule.getNotifyAttempts()).isZero();
    }

    @Test
    void 발송실패시_isNotified는_false로_남고_시도횟수가_증가한다() {
        LetterPaper capsule = dueCapsule();
        givenDue(capsule);
        willThrow(new MailSendException("connect refused")).given(mailer).send(capsule);

        scheduler.checkAndNotifyOpenCapsules();

        assertThat(capsule.getIsNotified()).isFalse();
        assertThat(capsule.getNotifyAttempts()).isEqualTo(1);
    }

    @Test
    void 수신이메일이_없으면_발송하지않고_알림완료로_표시한다() {
        LetterPaper capsule = dueCapsule();
        capsule.setReceiverEmail(null);
        givenDue(capsule);

        scheduler.checkAndNotifyOpenCapsules();

        then(mailer).should(never()).send(any());
        assertThat(capsule.getIsNotified()).isTrue();
    }

    @Test
    void 한_캡슐이_실패해도_나머지_캡슐은_계속_발송한다() {
        LetterPaper failing = dueCapsule();
        LetterPaper ok = dueCapsule();
        ok.setId(2L);
        givenDue(failing, ok);
        willThrow(new MailSendException("boom")).given(mailer).send(failing);

        scheduler.checkAndNotifyOpenCapsules();

        assertThat(failing.getIsNotified()).isFalse();
        assertThat(ok.getIsNotified()).isTrue();
        then(mailer).should(times(1)).send(ok);
    }

    @Test
    void 대상이_없으면_저장하지_않는다() {
        givenDue();

        scheduler.checkAndNotifyOpenCapsules();

        then(mailer).should(never()).send(any());
        then(letterPaperRepository).should(never()).saveAll(any());
    }
}
