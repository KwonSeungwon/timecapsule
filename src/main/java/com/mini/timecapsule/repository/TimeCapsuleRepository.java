package com.mini.timecapsule.repository;

import com.mini.timecapsule.model.TimeCapsule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeCapsuleRepository extends JpaRepository<TimeCapsule, Long>, QuerydslPredicateExecutor<TimeCapsule> {

    List<TimeCapsule> findByEmail(String email);
}
