package com.mini.timecapsule.dao;

import com.mini.timecapsule.model.DailyHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface DailyHistoryRepository extends JpaRepository<DailyHistory, Long>, QuerydslPredicateExecutor<DailyHistory> {
}
