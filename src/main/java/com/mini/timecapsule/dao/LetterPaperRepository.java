package com.mini.timecapsule.dao;


import com.mini.timecapsule.model.LetterPaper;
import com.mini.timecapsule.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.ZonedDateTime;
import java.util.List;

public interface LetterPaperRepository extends JpaRepository<LetterPaper, Long>, QuerydslPredicateExecutor<LetterPaper> {

    List<LetterPaper> findAllByIsPublicTrue();

    List<LetterPaper> findByReceiverEmail(String receiverEmail);

    List<LetterPaper> findAllByOpenAtBeforeAndIsNotifiedFalseAndStatus(ZonedDateTime now, LetterPaper.LetterStatus status);

}
