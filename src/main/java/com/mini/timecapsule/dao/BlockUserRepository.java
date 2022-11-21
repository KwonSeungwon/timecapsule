package com.mini.timecapsule.dao;

import com.mini.timecapsule.model.BlockUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface BlockUserRepository extends JpaRepository<BlockUser, Long>, QuerydslPredicateExecutor<BlockUser> {
}
