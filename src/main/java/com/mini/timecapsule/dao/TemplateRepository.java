package com.mini.timecapsule.dao;

import com.mini.timecapsule.model.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface TemplateRepository extends JpaRepository<Template, Long>, QuerydslPredicateExecutor<Template> {
}
