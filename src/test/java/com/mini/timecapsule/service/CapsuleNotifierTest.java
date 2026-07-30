package com.mini.timecapsule.service;

import com.mini.timecapsule.dao.LetterPaperRepository;
import com.mini.timecapsule.model.LetterPaper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.MailSendException;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

/**
 * 캡슐 한 통의 알림 발송. 트랜잭션 경계가 캡슐 <b>한 건</b>이라는 게 이 클래스의 존재 이유다.
 *
 * <p>예전에는 스케줄러 메서드 전체가 하나의 트랜잭션이었다. 50건을 보내고 51번째에서
 * 예외가 나면 앞의 50건은 메일이 이미 나갔는데 {@code isNotified} 는 롤백돼서
 * 다음 주기에 전부 다시 발송됐다. 중복 발송 반경을 1건으로 좁힌다.
 */
class CapsuleNotifierTest {

    private static final int MAX_ATTEMPTS = 10;

    @Mock
    private LetterPaperRepository letterPaperRepository;

    @Mock
    private CapsuleNotificationMailer mailer;

    @InjectMocks
    private CapsuleNotifier notifier;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private LetterPaper dueCapsule() {
        LetterPaper capsule = LetterPaper.newEntity(
                "보낸이", "10년 뒤의 나에게", "test@example.com", "enc",
                120, 880, "DESERT", "붉은 사막",
                LetterPaper.LetterPaperType.LETTER, LetterPaper.CapsuleType.BOTTLE,
                "내용", null, null, ZonedDateTime.now().minusMinutes(1), false);
        capsule.setId(1L);
        given(letterPaperRepository.findById(1L)).willReturn(Optional.of(capsule));
        return capsule;
    }

    @Test
    @DisplayName("메일을 보내고 알림완료로 표시한다")
    void sendsAndMarksNotified() {
        LetterPaper capsule = dueCapsule();

        notifier.notifyOne(1L, MAX_ATTEMPTS);

        then(mailer).should(times(1)).send(capsule);
        assertThat(capsule.getIsNotified()).isTrue();
        assertThat(capsule.getNotifyAttempts()).isZero();
    }

    @Test
    @DisplayName("발송에 실패하면 isNotified 는 false 로 남고 시도횟수만 올라간다")
    void failureKeepsCapsuleRetryable() {
        LetterPaper capsule = dueCapsule();
        willThrow(new MailSendException("connect refused")).given(mailer).send(capsule);

        notifier.notifyOne(1L, MAX_ATTEMPTS);

        assertThat(capsule.getIsNotified()).isFalse();
        assertThat(capsule.getNotifyAttempts()).isEqualTo(1);
    }

    @Test
    @DisplayName("수신 이메일이 없으면 발송하지 않고 알림완료로 표시한다")
    void skipsCapsuleWithoutReceiver() {
        LetterPaper capsule = dueCapsule();
        capsule.setReceiverEmail("  ");

        notifier.notifyOne(1L, MAX_ATTEMPTS);

        then(mailer).should(never()).send(any());
        assertThat(capsule.getIsNotified()).isTrue();
    }

    @Test
    @DisplayName("조회와 발송 사이에 캡슐이 사라져도 터지지 않는다")
    void toleratesCapsuleDisappearing() {
        given(letterPaperRepository.findById(99L)).willReturn(Optional.empty());

        assertThatCode(() -> notifier.notifyOne(99L, MAX_ATTEMPTS)).doesNotThrowAnyException();
        then(mailer).should(never()).send(any());
    }
}
