package com.mini.timecapsule.service;

import com.mini.timecapsule.dao.LetterPaperRepository;
import com.mini.timecapsule.model.LetterPaper;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.net.ServerSocket;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchException;
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
        // 기본은 "메일이 제대로 설정된 상태". 설정 자체가 안 된 경우는 별도 테스트에서 본다.
        given(mailer.isConfigured()).willReturn(true);
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

    /**
     * 이 메시지 한 통이 거절된 경우(수신 주소 거부 등)는 캡슐 고유의 문제라
     * 재시도 예산을 깎는 게 맞다. 안 깎으면 영원히 실패하는 주소 하나가
     * 매 주기 대기열 자리를 차지한다.
     */
    @Test
    @DisplayName("메시지 단위 거절이면 isNotified 는 false 로 남고 시도횟수만 올라간다")
    void perMessageFailureKeepsCapsuleRetryableAndBurnsBudget() {
        LetterPaper capsule = dueCapsule();
        willThrow(perMessageFailure()).given(mailer).send(capsule);

        notifier.notifyOne(1L, MAX_ATTEMPTS);

        assertThat(capsule.getIsNotified()).isFalse();
        assertThat(capsule.getNotifyAttempts()).isEqualTo(1);
    }

    /** JavaMailSenderImpl 이 개별 메시지 거절에 쓰는 형태 — failedMessages 가 채워져 있다. */
    private MailSendException perMessageFailure() {
        return new MailSendException(Map.of(new Object(), new MessagingException("550 mailbox unavailable")));
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

    /**
     * 설정 오류가 캡슐의 재시도 예산을 먹어치우면 안 된다.
     *
     * <p>k8s deployment 는 MAIL_HOST 를 {@code secretKeyRef ... optional: true} 로 받는다.
     * 시크릿을 만들지 않고 매니페스트만 적용하면 localhost:25 로 떨어져 모든 발송이
     * 즉시 실패한다. 예전에는 그 실패가 그대로 notifyAttempts 로 쌓여서, 매분 주기 기준
     * <b>10분</b>이면 그 사이 개봉일이 된 캡슐이 전부 영구 미발송이 됐다.
     * 사람의 실수가 사용자 데이터를 영구히 못 쓰게 만드는 경로라 반드시 막아야 한다.
     */
    @Test
    @DisplayName("메일이 설정되지 않았으면 발송을 시도하지도, 시도횟수를 올리지도 않는다")
    void unconfiguredMailNeverBurnsRetryBudget() {
        given(mailer.isConfigured()).willReturn(false);
        LetterPaper capsule = dueCapsule();

        notifier.notifyOne(1L, MAX_ATTEMPTS);

        then(mailer).should(never()).send(any());
        assertThat(capsule.getNotifyAttempts()).isZero();
        assertThat(capsule.getIsNotified()).isFalse();
    }

    /**
     * 메일 서버에 아예 닿지 못한 실패도 캡슐의 문제가 아니다 — 같은 시각의 모든 캡슐이
     * 똑같이 실패한다. 여기서 예산을 깎으면 SMTP 장애 시간 × 주기만큼의 캡슐이
     * 통째로 소각된다. 운영 cron 이 매분이라 10분 장애면 끝이다.
     *
     * <p>대신 무한 재시도가 되는데, 이 클래스가 이미 선언한 트레이드오프
     * ("중복이 유실보다 낫다")와 같은 방향이다. 대기열은 batch-size 로 묶여 있고
     * 매 주기 ERROR 로그가 남으므로 조용히 방치되지도 않는다.
     */
    @Test
    @DisplayName("메일 서버 연결 실패는 재시도 횟수를 올리지 않는다")
    void transportFailureDoesNotBurnRetryBudget() {
        LetterPaper capsule = dueCapsule();
        // JavaMailSenderImpl 이 연결 실패에 쓰는 형태 — failedMessages 가 비어 있다.
        willThrow(new MailSendException("Mail server connection failed", new MessagingException("connect timed out")))
                .given(mailer).send(capsule);

        notifier.notifyOne(1L, MAX_ATTEMPTS);

        assertThat(capsule.getNotifyAttempts()).isZero();
        assertThat(capsule.getIsNotified()).isFalse();
    }

    @Test
    @DisplayName("SMTP 인증 실패도 재시도 횟수를 올리지 않는다")
    void authenticationFailureDoesNotBurnRetryBudget() {
        LetterPaper capsule = dueCapsule();
        // 비밀번호/앱 패스워드 오설정 — 캡슐이 아니라 배포 설정의 문제다.
        willThrow(new MailAuthenticationException("535 authentication failed"))
                .given(mailer).send(capsule);

        notifier.notifyOne(1L, MAX_ATTEMPTS);

        assertThat(capsule.getNotifyAttempts()).isZero();
    }

    /**
     * 메일 인프라 장애와 메시지 단위 거절을 가르는 기준은 Spring 이 실제로 던지는
     * 예외 모양에 의존한다({@code MailSendException.getFailedMessages()} 가 비었는가).
     * 그 가정이 깨지면 분류가 통째로 뒤집히므로, 진짜 소켓을 물려서 못 박는다.
     * 이 테스트가 깨지면 {@code CapsuleNotifier.isMailInfrastructureFailure} 를 다시 볼 것.
     */
    @Test
    @DisplayName("닿을 수 없는 SMTP 호스트는 인프라 장애로 분류된다")
    void unreachableHostIsClassifiedAsInfrastructureFailure() throws Exception {
        int deadPort;
        try (ServerSocket probe = new ServerSocket(0)) {
            deadPort = probe.getLocalPort();
        }
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost("127.0.0.1");
        sender.setPort(deadPort);
        sender.getJavaMailProperties().setProperty("mail.smtp.connectiontimeout", "2000");

        Exception thrown = catchException(() ->
                sender.send(new MimeMessage(Session.getInstance(new Properties()))));

        assertThat(thrown).isInstanceOf(MailSendException.class);
        // 연결 실패인데도 failedMessages 는 비어 있지 않다 — 남은 메시지 전부를 같은
        // 연결 예외에 매핑해 넣기 때문이다. "비었으면 인프라 장애"로 짰다가 여기서 걸렸다.
        assertThat(((MailSendException) thrown).getFailedMessages()).isNotEmpty();
        assertThat(CapsuleNotifier.isMailInfrastructureFailure(thrown)).isTrue();
    }

    @Test
    @DisplayName("메시지별 사유가 다른 실패는 인프라 장애가 아니다")
    void perMessageRejectionIsNotAnInfrastructureFailure() {
        assertThat(CapsuleNotifier.isMailInfrastructureFailure(perMessageFailure())).isFalse();
    }
}
