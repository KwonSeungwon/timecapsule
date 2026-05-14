package com.mini.timecapsule.dao;

import com.mini.timecapsule.model.Coordinates;
import com.mini.timecapsule.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {

    @Query("SELECT u FROM User u WHERE u.coordinates = :coordinates")
    Optional<User> findByCoordinates(@Param("coordinates") Coordinates coordinates);
}
