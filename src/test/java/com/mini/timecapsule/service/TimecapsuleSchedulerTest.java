package com.mini.timecapsule.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.mini.timecapsule.dao.LetterPaperRepository;
import com.mini.timecapsule.model.LetterPaper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

/**
 * 스케줄러는 "대상 id 를 뽑아 한 건씩 넘긴다"까지만 한다.
 * 실제 발송/상태변경은 {@link CapsuleNotifier} 가 캡슐 단위 트랜잭션으로 처리한다.
 */
class TimecapsuleSchedulerTest {

    private static final int MAX_ATTEMPTS = 10;
    private static final int BATCH_SIZE = 100;

    @Mock
    private LetterPaperRepository letterPaperRepository;

    @Mock
    private CapsuleNotifier notifier;

    @InjectMocks
    private TimecapsuleScheduler scheduler;

    private ListAppender<ILoggingEvent> logs;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(scheduler, "maxAttempts", MAX_ATTEMPTS);
        ReflectionTestUtils.setField(scheduler, "batchSize", BATCH_SIZE);
        ReflectionTestUtils.setField(scheduler, "requeueExhaustedOnStart", false);
        given(notifier.isMailConfigured()).willReturn(true);

        logs = new ListAppender<>();
        logs.start();
        ((Logger) LoggerFactory.getLogger(TimecapsuleScheduler.class)).addAppender(logs);
    }

    @AfterEach
    void tearDown() {
        ((Logger) LoggerFactory.getLogger(TimecapsuleScheduler.class)).detachAppender(logs);
    }

    private boolean loggedAtLeast(Level level) {
        return logs.list.stream().anyMatch(e -> e.getLevel().isGreaterOrEqual(level));
    }

    private void givenDue(Long... ids) {
        given(letterPaperRepository.findIdsDueForNotification(any(), any(), anyInt(), any()))
                .willReturn(List.of(ids));
    }

    @Test
    @DisplayName("개봉일이 지난 캡슐을 한 건씩 notifier 에 넘긴다")
    void dispatchesEachCapsuleIndividually() {
        givenDue(1L, 2L, 3L);

        scheduler.checkAndNotifyOpenCapsules();

        then(notifier).should(times(1)).notifyOne(1L, MAX_ATTEMPTS);
        then(notifier).should(times(1)).notifyOne(2L, MAX_ATTEMPTS);
        then(notifier).should(times(1)).notifyOne(3L, MAX_ATTEMPTS);
    }

    @Test
    @DisplayName("삭제된 캡슐은 조회 대상에서 제외한다")
    void excludesRemovedCapsules() {
        givenDue();

        scheduler.checkAndNotifyOpenCapsules();

        then(letterPaperRepository).should()
                .findIdsDueForNotification(any(), eq(LetterPaper.LetterStatus.REMOVE), eq(MAX_ATTEMPTS), any());
    }

    /**
     * 한 주기에 발송할 건수에 상한이 없으면 배포 직후 첫 tick 이 밀린 캡슐 전부를
     * 한 번에 직렬 발송한다. sink 를 보고 있을 땐 조용하지만 실제 SMTP 를 붙이는
     * 순간 그 적체가 한 번에 터지고, 수신 MTA 입장에선 스팸 발송과 똑같이 보인다.
     * 이 테스트를 지우면 상한이 사라져도 아무도 모른다.
     */
    @Test
    @DisplayName("한 주기에 조회하는 건수를 batch-size 로 제한한다")
    void boundsTheNumberOfCapsulesPerTick() {
        ReflectionTestUtils.setField(scheduler, "batchSize", 25);
        givenDue();

        scheduler.checkAndNotifyOpenCapsules();

        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        then(letterPaperRepository).should()
                .findIdsDueForNotification(any(), any(), anyInt(), pageable.capture());
        assertThat(pageable.getValue().getPageSize()).isEqualTo(25);
        // 항상 첫 페이지다. offset 을 올리면 아직 발송 못 한 캡슐을 건너뛰게 된다 —
        // 처리된 캡슐은 isNotified/notifyAttempts 로 대기열에서 빠지므로 커서가 필요 없다.
        assertThat(pageable.getValue().getPageNumber()).isZero();
    }

    /**
     * 상한에 걸려 이번 주기가 잘렸다는 사실은 반드시 로그로 남아야 한다.
     * 조용히 자르면 "이번 주기에 대상 100건을 다 보냈다"로 읽히는데 실제로는
     * 남은 캡슐이 있다 — 알림 지연을 조사할 단서가 통째로 사라진다.
     */
    @Test
    @DisplayName("상한까지 채워서 잘린 주기는 경고로 남긴다")
    void warnsWhenTheTickWasTruncated() {
        ReflectionTestUtils.setField(scheduler, "batchSize", 3);
        givenDue(1L, 2L, 3L);

        scheduler.checkAndNotifyOpenCapsules();

        assertThat(loggedAtLeast(Level.WARN)).isTrue();
    }

    @Test
    @DisplayName("상한에 못 미치는 주기는 경고를 남기지 않는다")
    void doesNotWarnWhenEverythingFitInOneTick() {
        ReflectionTestUtils.setField(scheduler, "batchSize", 100);
        givenDue(LongStream.rangeClosed(1, 99).boxed().toArray(Long[]::new));

        scheduler.checkAndNotifyOpenCapsules();

        assertThat(loggedAtLeast(Level.WARN)).isFalse();
    }

    @Test
    @DisplayName("한 건이 통째로 실패해도 나머지는 계속 처리한다")
    void oneFailureDoesNotStopTheBatch() {
        givenDue(1L, 2L);
        willThrow(new IllegalStateException("tx rollback")).given(notifier).notifyOne(1L, MAX_ATTEMPTS);

        scheduler.checkAndNotifyOpenCapsules();

        then(notifier).should(times(1)).notifyOne(2L, MAX_ATTEMPTS);
    }

    @Test
    @DisplayName("대상이 없으면 아무 것도 하지 않는다")
    void doesNothingWhenNoneDue() {
        givenDue();

        scheduler.checkAndNotifyOpenCapsules();

        then(notifier).should(never()).notifyOne(anyLong(), anyInt());
    }

    /**
     * 메일이 아예 설정되지 않았으면(= MAIL_HOST 시크릿 없이 매니페스트만 적용해
     * localhost:25 로 떨어진 상태) 한 건도 건드리지 않고 주기를 통째로 건너뛴다.
     *
     * <p>발송을 시도해봐야 전부 즉시 실패하고, 그 실패가 notifyAttempts 로 쌓이면
     * 매분 주기 기준 10분 만에 그 사이 개봉일이 된 캡슐이 전부 영구 미발송이 된다.
     * 재시도 예산은 캡슐 고유의 문제를 위한 것이지 배포 실수를 위한 것이 아니다.
     */
    @Test
    @DisplayName("메일이 설정되지 않았으면 대기열을 조회조차 하지 않는다")
    void skipsTheWholeTickWhenMailIsNotConfigured() {
        given(notifier.isMailConfigured()).willReturn(false);

        scheduler.checkAndNotifyOpenCapsules();

        then(letterPaperRepository).should(never())
                .findIdsDueForNotification(any(), any(), anyInt(), any());
        then(notifier).should(never()).notifyOne(anyLong(), anyInt());
    }

    /**
     * 건너뛰는 것만으로는 부족하다 — 조용히 건너뛰면 "보낼 게 없었다"와 구분이 안 된다.
     * health 의 mail 인디케이터는 일부러 꺼놨으므로(끄지 않으면 메일 장애가 사이트
     * 전체 다운이 된다) 매 주기 ERROR 로그가 이 상태를 알리는 주된 수단이다.
     */
    @Test
    @DisplayName("메일이 설정되지 않은 주기는 매번 ERROR 로 남긴다")
    void logsAtErrorEveryTickWhileMailIsNotConfigured() {
        given(notifier.isMailConfigured()).willReturn(false);

        scheduler.checkAndNotifyOpenCapsules();
        scheduler.checkAndNotifyOpenCapsules();

        assertThat(logs.list.stream().filter(e -> e.getLevel() == Level.ERROR)).hasSize(2);
    }

    /**
     * 재시도 예산이 소진된 캡슐은 쿼리에서 <b>영구히</b> 빠진다. 그걸 되살릴 관리자
     * 화면은 인증 없이 JWT 를 뿌리던 문제로 통째로 제거됐고, 지금은 어디에도 리셋
     * 경로가 없다. 최소한 "몇 건이 영구 미발송 상태인지"는 기동할 때마다 보여야 한다.
     */
    @Test
    @DisplayName("영구 미발송 캡슐이 있으면 기동할 때 ERROR 로 알린다")
    void reportsPermanentlyUndeliverableCapsulesOnStartup() {
        given(letterPaperRepository.countExhausted(LetterPaper.LetterStatus.REMOVE, MAX_ATTEMPTS)).willReturn(3L);

        scheduler.reviewUndeliverableCapsulesOnStartup();

        assertThat(logs.list.stream().filter(e -> e.getLevel() == Level.ERROR)).hasSize(1);
        then(notifier).should(never()).requeueExhausted(anyInt());
    }

    @Test
    @DisplayName("영구 미발송 캡슐이 없으면 기동 로그를 남기지 않는다")
    void staysQuietOnStartupWhenNothingIsStuck() {
        given(letterPaperRepository.countExhausted(any(), anyInt())).willReturn(0L);

        scheduler.reviewUndeliverableCapsulesOnStartup();

        assertThat(loggedAtLeast(Level.WARN)).isFalse();
    }

    /**
     * 복구 경로. HTTP 엔드포인트가 아니라 기동 옵션인 이유는, 이 서비스가 비회원
     * 전용이라 인증된 관리자 표면이 없기 때문이다 — 되살리기 API 를 열면
     * 아무나 호출할 수 있다. 단일 replica + Recreate 라 재기동이 이미 표준 운영 동작이다.
     */
    @Test
    @DisplayName("복구 플래그를 켜고 기동하면 소진된 캡슐을 대기열로 되돌린다")
    void requeuesExhaustedCapsulesWhenTheRecoveryFlagIsOn() {
        ReflectionTestUtils.setField(scheduler, "requeueExhaustedOnStart", true);
        given(notifier.requeueExhausted(MAX_ATTEMPTS)).willReturn(3);

        scheduler.reviewUndeliverableCapsulesOnStartup();

        then(notifier).should().requeueExhausted(MAX_ATTEMPTS);
    }
}
