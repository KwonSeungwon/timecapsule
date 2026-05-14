package com.mini.timecapsule.dao;


import com.mini.timecapsule.model.ContentFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface ContentFilterRepository extends JpaRepository<ContentFilter, Long>, QuerydslPredicateExecutor<ContentFilter> {

    @org.springframework.data.jpa.repository.Query("SELECT c.filterKeyword FROM ContentFilter c WHERE c.used = :used")
    List<String> findFilterKeywordByUsed(@org.springframework.data.repository.query.Param("used") ContentFilter.Used used);
}
