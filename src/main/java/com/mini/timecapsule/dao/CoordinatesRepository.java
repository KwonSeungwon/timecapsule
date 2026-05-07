package com.mini.timecapsule.dao;

import com.mini.timecapsule.model.Coordinates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CoordinatesRepository extends JpaRepository<Coordinates, Long>, QuerydslPredicateExecutor<Coordinates> {

    List<Coordinates> findByIsFixed(Boolean isFixed);

    List<Coordinates> findByIsFixedTrueAndFixedAtAndLinkAtIsNull(LocalDateTime limitTime);

    @Query("SELECT c FROM Coordinates c WHERE c.xCoordinates = :x AND c.yCoordinates = :y")
    Optional<Coordinates> findByXCoordinatesAndYCoordinates(@Param("x") String x, @Param("y") String y);
}
