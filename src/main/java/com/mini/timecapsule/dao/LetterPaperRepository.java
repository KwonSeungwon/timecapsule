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
     * 개봉일이 지났는데 아직 알림이 안 나간 캡슐.
     * notifyAttempts 상한을 넘긴 캡슐은 제외한다 — SMTP 가 영구 장애일 때
     * 같은 캡슐로 매 주기마다 무한 재시도하는 것을 막는다.
     */
    @Query("""
            select l from LetterPaper l
            where l.openAt < :now
              and l.isNotified = false
              and l.status = :status
              and (l.notifyAttempts is null or l.notifyAttempts < :maxAttempts)
            """)
    List<LetterPaper> findDueForNotification(@Param("now") ZonedDateTime now,
                                             @Param("status") LetterPaper.LetterStatus status,
                                             @Param("maxAttempts") int maxAttempts);

}
