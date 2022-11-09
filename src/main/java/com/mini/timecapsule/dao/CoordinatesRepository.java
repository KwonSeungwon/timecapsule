package com.mini.timecapsule.dao;

import com.mini.timecapsule.model.Coordinates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface CoordinatesRepository extends JpaRepository<Coordinates, Long>, QuerydslPredicateExecutor<Coordinates> {

    List<Coordinates> findByIsFixed(Boolean isFixed);

    List<Coordinates> findByIsFixedTrueAndFixedAtAndLinkAtIsNull(LocalDateTime limitTime);
}
