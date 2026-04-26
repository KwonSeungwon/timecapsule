package com.mini.timecapsule.dao;


import com.mini.timecapsule.model.LetterPaper;
import com.mini.timecapsule.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.ZonedDateTime;
import java.util.List;

public interface LetterPaperRepository extends JpaRepository<LetterPaper, Long>, QuerydslPredicateExecutor<LetterPaper> {

    List<LetterPaper> findByUser(User user);

    List<LetterPaper> findAllByIsPublicTrue();

    List<LetterPaper> findAllByOpenAtBeforeAndIsNotifiedFalseAndStatus(ZonedDateTime now, LetterPaper.LetterStatus status);

}
