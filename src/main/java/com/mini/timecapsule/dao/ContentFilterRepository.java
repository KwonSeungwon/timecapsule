package com.mini.timecapsule.dao;


import com.mini.timecapsule.model.ContentFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface ContentFilterRepository extends JpaRepository<ContentFilter, Long>, QuerydslPredicateExecutor<ContentFilter> {

    List<String> findFilterKeywordByUsed(ContentFilter.Used used);
}
