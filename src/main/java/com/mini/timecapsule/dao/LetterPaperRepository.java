package com.mini.timecapsule.dao;


import com.mini.timecapsule.model.LetterPaper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;

public interface LetterPaperRepository extends JpaRepository<LetterPaper, Long>, QuerydslPredicateExecutor<LetterPaper> {

    List<LetterPaper> findAllByIsPublicTrue();

    List<LetterPaper> findByReceiverEmail(String receiverEmail);

    /**
     * 개봉일이 지났는데 아직 알림이 안 나간 캡슐의 id.
     *
     * <p>엔티티가 아니라 id 만 돌려준다. 발송은 {@code CapsuleNotifier} 가 캡슐 한 건마다
     * 별도 트랜잭션으로 처리하므로, 여기서 읽어둔 엔티티를 그대로 넘기면 detached 상태가 된다.
     *
     * <p>필터 세 개는 전부 "빠지면 영원히 메일이 안 간다" 쪽이라 조심해서 읽을 것:
     * <ul>
     *   <li>{@code isNotified} 는 <b>NULL 허용</b>이다. 컬럼이 생기기 전에 만들어진 행은
     *       NULL 이고, {@code = false} 로 비교하면 SQL 3-value logic 상 UNKNOWN 이라
     *       조용히 제외된다.</li>
     *   <li>상태는 REMOVE <b>만</b> 제외한다. 예전엔 {@code status = UNOPENED} 였는데,
     *       /find 로 먼저 찾아 열어본 사람은 알림을 영영 못 받았다.</li>
     *   <li>{@code notifyAttempts} 상한 초과는 제외 — SMTP 영구 장애일 때 같은 캡슐이
     *       매 주기 큐를 붙잡는 것을 막는다.</li>
     * </ul>
     *
     * <p>{@code pageable} 은 <b>필수</b>다. 상한이 없으면 배포 직후 첫 tick 이 밀린 캡슐
     * 전부를 한 번에 직렬 발송한다. 상태 필터가 {@code = UNOPENED} 에서
     * {@code <> REMOVE} 로 넓어진 뒤로 대기열은 더 커졌다. 메일이 in-cluster sink 를
     * 보고 있을 땐 티가 안 나지만, 실제 SMTP 를 붙이는 순간 그 적체가 한 번에 나가고
     * 수신 MTA 에게는 스팸 발송과 구분되지 않는다.
     *
     * <p>정렬은 개봉일 오름차순이다. 상한에 걸려 잘릴 때 오래 기다린 캡슐이 먼저 나가야
     * 매 주기 뒤로 밀리는 캡슐(starvation)이 생기지 않는다.
     */
    @Query("""
            select l.id from LetterPaper l
            where l.openAt < :now
              and (l.isNotified is null or l.isNotified = false)
              and (l.status is null or l.status <> :excludedStatus)
              and (l.notifyAttempts is null or l.notifyAttempts < :maxAttempts)
            order by l.openAt asc
            """)
    List<Long> findIdsDueForNotification(@Param("now") ZonedDateTime now,
                                         @Param("excludedStatus") LetterPaper.LetterStatus excludedStatus,
                                         @Param("maxAttempts") int maxAttempts,
                                         Pageable pageable);

    /**
     * 재시도 예산이 소진돼 {@code findIdsDueForNotification} 에서 <b>영구히</b> 빠진 캡슐 수.
     *
     * <p>이 숫자는 "메일을 영영 못 받는 사용자 수"다. 세지 않으면 조용한 데이터 유실이
     * 그대로 방치된다 — 기동할 때마다 로그로 알린다.
     */
    @Query("""
            select count(l) from LetterPaper l
            where (l.isNotified is null or l.isNotified = false)
              and (l.status is null or l.status <> :excludedStatus)
              and l.notifyAttempts >= :maxAttempts
            """)
    long countExhausted(@Param("excludedStatus") LetterPaper.LetterStatus excludedStatus,
                        @Param("maxAttempts") int maxAttempts);

    /**
     * 소진된 캡슐을 대기열로 되돌린다. 되돌린 건수를 반환한다.
     *
     * <p>운영자가 명시적으로 켤 때만 실행된다({@code timecapsule.notification.requeue-exhausted-on-start}).
     * 이미 발송된 캡슐과 삭제된 캡슐은 건드리지 않는다 — 되살리면 중복 발송이 된다.
     */
    @Modifying(clearAutomatically = true)
    @Query("""
            update LetterPaper l set l.notifyAttempts = 0
            where (l.isNotified is null or l.isNotified = false)
              and (l.status is null or l.status <> :excludedStatus)
              and l.notifyAttempts >= :maxAttempts
            """)
    int resetExhaustedNotifyAttempts(@Param("excludedStatus") LetterPaper.LetterStatus excludedStatus,
                                     @Param("maxAttempts") int maxAttempts);

}
