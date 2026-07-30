package com.mini.timecapsule.service;

import com.mini.timecapsule.dao.LetterPaperRepository;
import com.mini.timecapsule.model.LetterPaper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

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

    @Mock
    private LetterPaperRepository letterPaperRepository;

    @Mock
    private CapsuleNotifier notifier;

    @InjectMocks
    private TimecapsuleScheduler scheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(scheduler, "maxAttempts", MAX_ATTEMPTS);
    }

    private void givenDue(Long... ids) {
        given(letterPaperRepository.findIdsDueForNotification(any(), any(), anyInt()))
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
                .findIdsDueForNotification(any(), eq(LetterPaper.LetterStatus.REMOVE), eq(MAX_ATTEMPTS));
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
}
