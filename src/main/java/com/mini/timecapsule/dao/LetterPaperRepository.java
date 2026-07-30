package com.mini.timecapsule.dao;


import com.mini.timecapsule.model.LetterPaper;
import org.springframework.data.jpa.repository.JpaRepository;
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
                                         @Param("maxAttempts") int maxAttempts);

}
