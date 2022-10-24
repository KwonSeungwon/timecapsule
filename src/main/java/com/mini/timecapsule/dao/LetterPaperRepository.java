package com.mini.timecapsule.dao;


import com.mini.timecapsule.model.LetterPaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface LetterPaperRepository extends JpaRepository<LetterPaper, Long>, QuerydslPredicateExecutor<LetterPaper> {
}
