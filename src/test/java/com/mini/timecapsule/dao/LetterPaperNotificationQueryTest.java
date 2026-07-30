package com.mini.timecapsule.dao;

import com.mini.timecapsule.model.LetterPaper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * "누가 알림 대상인가" 를 정하는 쿼리. 여기서 빠지면 그 캡슐은 <b>영원히</b>
 * 메일을 못 받는다 — 조용히 실패하는 종류라 쿼리 자체를 테스트로 고정한다.
 */
@DataJpaTest
class LetterPaperNotificationQueryTest {

    @Autowired
    private LetterPaperRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private static final ZonedDateTime NOW = ZonedDateTime.now();

    private LetterPaper capsule(ZonedDateTime openAt) {
        LetterPaper capsule = LetterPaper.newEntity(
                "보낸이", "제목", "test@example.com", "enc",
                1, 2, "BEACH", "고요한 바다",
                LetterPaper.LetterPaperType.LETTER, LetterPaper.CapsuleType.BOTTLE,
                "내용", null, null, openAt, false);
        return entityManager.persistFlushFind(capsule);
    }

    private List<Long> due() {
        entityManager.flush();
        entityManager.clear();
        return repository.findIdsDueForNotification(NOW, LetterPaper.LetterStatus.REMOVE, 10);
    }

    @Test
    @DisplayName("개봉일이 지난 미발송 캡슐을 찾는다")
    void findsDueCapsule() {
        Long id = capsule(NOW.minusDays(1)).getId();

        assertThat(due()).containsExactly(id);
    }

    @Test
    @DisplayName("개봉일이 아직인 캡슐은 대상이 아니다")
    void ignoresFutureCapsule() {
        capsule(NOW.plusDays(1));

        assertThat(due()).isEmpty();
    }

    @Test
    @DisplayName("is_notified 가 NULL 인 예전 행도 대상이다")
    void includesRowsWithNullIsNotified() {
        Long id = capsule(NOW.minusDays(1)).getId();
        // isNotified / notifyAttempts 컬럼이 생기기 전에 만들어진 행은 NULL 이다.
        // `l.isNotified = false` 로 비교하면 SQL 3-value logic 때문에 UNKNOWN 이 되어
        // 조용히 빠진다 — 그 캡슐은 영원히 메일을 못 받는다.
        entityManager.getEntityManager()
                .createNativeQuery("update letter_paper set is_notified = null, notify_attempts = null where id = :id")
                .setParameter("id", id)
                .executeUpdate();

        assertThat(due()).containsExactly(id);
    }

    @Test
    @DisplayName("이미 발송된 캡슐은 다시 보내지 않는다")
    void skipsAlreadyNotified() {
        LetterPaper capsule = capsule(NOW.minusDays(1));
        capsule.markNotified();

        assertThat(due()).isEmpty();
    }

    @Test
    @DisplayName("사용자가 먼저 열어본 캡슐도 알림 대상이다")
    void includesManuallyOpenedCapsule() {
        LetterPaper capsule = capsule(NOW.minusDays(1));
        // /find 로 직접 찾아 비밀번호로 열면 status 가 OPENED 가 된다. 예전 쿼리는
        // status = UNOPENED 만 봤기 때문에, 메일이 나가기 전에 열어본 사람은
        // "열렸습니다" 알림을 영영 못 받았다.
        capsule.read();

        assertThat(due()).containsExactly(capsule.getId());
    }

    @Test
    @DisplayName("삭제된 캡슐은 알림 대상이 아니다")
    void excludesRemovedCapsule() {
        LetterPaper capsule = capsule(NOW.minusDays(1));
        capsule.remove();

        assertThat(due()).isEmpty();
    }

    @Test
    @DisplayName("시도횟수 상한을 넘긴 캡슐은 큐에서 빠진다")
    void excludesExhaustedCapsule() {
        LetterPaper capsule = capsule(NOW.minusDays(1));
        for (int i = 0; i < 10; i++) {
            capsule.recordNotifyFailure(10);
        }

        assertThat(due()).isEmpty();
    }
}
