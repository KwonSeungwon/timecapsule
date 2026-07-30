package com.mini.timecapsule.service;

import com.mini.timecapsule.dao.LetterPaperRepository;
import com.mini.timecapsule.model.LetterPaper;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.MailSendException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.times;

/**
 * 커밋 단위가 <b>캡슐 한 건</b>이라는 것을 실제 트랜잭션으로 확인한다.
 *
 * <p>이 파일이 존재하는 이유: {@code TimecapsuleSchedulerTest} 와 {@code CapsuleNotifierTest}
 * 는 전부 Mockito 라 트랜잭션이 없다. 누군가 루프를 스케줄러 안으로 되돌리고
 * {@code @Transactional} 을 붙여도 그 테스트들은 전부 통과한다 — 배치 전체가 한
 * 트랜잭션이 되는 회귀(= 한 건이 터지면 이미 메일이 나간 앞의 캡슐들까지
 * {@code isNotified} 가 롤백돼 다음 주기에 전원 중복 발송)를 못 잡는다.
 * 실제로 운영에서 그렇게 터졌던 버그다.
 *
 * <p>그래서 두 층으로 검사한다.
 * <ol>
 *   <li><b>배치 후 최종 상태</b>: 1번은 발송완료로 남고 2번만 실패 기록이 남는다.
 *       한 건의 실패가 다른 캡슐의 상태를 오염시키지 않는다.</li>
 *   <li><b>커밋 시점</b>(이쪽이 회귀를 잡는 핵심): 2번을 발송하는 <i>도중에</i>
 *       별도 커넥션으로 1번 행을 읽어, 그 시점에 이미 <b>커밋</b>돼 있는지 본다.
 *       배치 전체가 한 트랜잭션이면 아직 커밋 전이라 여기서 false 가 읽힌다.
 *       최종 상태만 보면 예외를 삼키는 배치-트랜잭션 변형을 놓친다.</li>
 * </ol>
 *
 * <p>실패한다면 커밋 경계가 깨진 것이다. 이 테스트를 고치지 말고 경계를 되돌릴 것.
 */
@SpringBootTest(properties = {
        // 다른 테스트 컨텍스트와 같은 mem DB 를 쓰면 남의 캡슐이 대기열에 섞인다.
        "spring.datasource.url=jdbc:h2:mem:commitboundary;DB_CLOSE_DELAY=-1",
        // 스케줄러가 스스로 도는 것을 막는다. 이 테스트는 tick 을 직접 호출한다.
        "timecapsule.notification.cron=-"
})
class CapsuleNotificationCommitBoundaryTest {

    private static final String FIRST = "first@example.com";
    private static final String SECOND = "second@example.com";

    @Autowired
    private TimecapsuleScheduler scheduler;

    @Autowired
    private LetterPaperRepository letterPaperRepository;

    @Autowired
    private DataSource dataSource;

    @MockBean
    private CapsuleNotificationMailer mailer;

    private Long firstId;
    private Long secondId;

    /** 2번을 발송하려는 순간, 1번이 이미 커밋돼 있었는가. */
    private final AtomicBoolean firstWasCommittedBeforeSecondRan = new AtomicBoolean();

    @BeforeEach
    void setUp() {
        letterPaperRepository.deleteAll();
        // 대기열은 openAt 오름차순이라 1번이 먼저 처리된다.
        firstId = save(FIRST, ZonedDateTime.now().minusDays(2)).getId();
        secondId = save(SECOND, ZonedDateTime.now().minusDays(1)).getId();

        given(mailer.isConfigured()).willReturn(true);
        willAnswer(invocation -> {
            LetterPaper capsule = invocation.getArgument(0);
            if (!SECOND.equals(capsule.getReceiverEmail())) {
                return null;
            }
            firstWasCommittedBeforeSecondRan.set(isNotifiedInCommittedState(firstId));
            // 개별 메시지 거절(failedMessages 가 채워진 형태). 인프라 장애가 아니므로
            // 이 캡슐의 재시도 횟수가 올라가는 게 맞다.
            throw new MailSendException(Map.of(new Object(), new MessagingException("550 mailbox unavailable")));
        }).given(mailer).send(any());
    }

    private LetterPaper save(String receiverEmail, ZonedDateTime openAt) {
        return letterPaperRepository.save(LetterPaper.newEntity(
                "보낸이", "제목", receiverEmail, "enc",
                1, 2, "BEACH", "고요한 바다",
                LetterPaper.LetterPaperType.LETTER, LetterPaper.CapsuleType.BOTTLE,
                "내용", null, null, openAt, false));
    }

    /**
     * 진행 중인 트랜잭션과 <b>무관한</b> 커넥션으로 읽는다.
     *
     * <p>{@code JdbcTemplate} 이나 리포지토리를 쓰면 트랜잭션에 묶인 커넥션을 그대로
     * 재사용해 아직 커밋 안 된 값까지 보인다 — 그러면 이 검사는 아무것도 증명하지 못한다.
     */
    private boolean isNotifiedInCommittedState(Long capsuleId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement("select is_notified from letter_paper where id = ?")) {
            statement.setLong(1, capsuleId);
            try (ResultSet rows = statement.executeQuery()) {
                return rows.next() && rows.getBoolean(1);
            }
        } catch (Exception e) {
            throw new IllegalStateException("커밋 상태 확인 실패: capsuleId=" + capsuleId, e);
        }
    }

    @Test
    @DisplayName("한 캡슐의 발송 실패가 다른 캡슐의 발송완료 표시를 되돌리지 않는다")
    void oneFailureDoesNotRollBackAnotherCapsule() {
        scheduler.checkAndNotifyOpenCapsules();

        LetterPaper first = letterPaperRepository.findById(firstId).orElseThrow();
        LetterPaper second = letterPaperRepository.findById(secondId).orElseThrow();

        then(mailer).should(times(2)).send(any());
        assertThat(first.getIsNotified()).isTrue();
        assertThat(first.getNotifyAttempts()).isZero();
        assertThat(second.getIsNotified()).isFalse();
        assertThat(second.getNotifyAttempts()).isEqualTo(1);
    }

    @Test
    @DisplayName("다음 캡슐을 발송하기 전에 앞 캡슐이 이미 커밋돼 있다")
    void eachCapsuleIsCommittedBeforeTheNextOneIsSent() {
        scheduler.checkAndNotifyOpenCapsules();

        assertThat(firstWasCommittedBeforeSecondRan)
                .withFailMessage("2번 캡슐을 발송하는 시점에 1번이 아직 커밋되지 않았다 — "
                        + "배치 전체가 한 트랜잭션이라는 뜻이다. 그러면 뒤에서 한 건만 터져도 "
                        + "이미 메일이 나간 앞 캡슐들의 isNotified 가 통째로 롤백되고 "
                        + "다음 주기에 전원 중복 발송된다. 커밋 단위는 캡슐 한 건이어야 한다")
                .isTrue();
    }
}
