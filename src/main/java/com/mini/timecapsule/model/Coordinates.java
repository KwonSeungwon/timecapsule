package com.mini.timecapsule.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * time capsule 좌표테이블
 * id : key
 * xCoordinates : x축 좌표
 * yCoordinates : y축 좌표
 * createdAt : 생성일
 * isFixed : 점유하고 있는지
 * User(외래키) / 유저가 생성하는 신규가입하면 생성됨
 * ※ user라는 단어는 H2에서는 예약어이기 떄문에 사용할 수 없습니다.
 */
@Entity
@Getter
@Setter
@Table(name = "coordinates")
public class Coordinates {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String xCoordinates;

    private String yCoordinates;

    private LocalDateTime fixedAt;

    private LocalDateTime linkAt;

    private Boolean isFixed;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="id")
    @JsonIgnore
    private User user;


    //신규생성(일괄적으로 생성할 예정)
    public static Coordinates newCoordinates(String xCoordinates, String yCoordinates) {

        Coordinates coordinates = new Coordinates();
        coordinates.xCoordinates = xCoordinates;
        coordinates.yCoordinates = yCoordinates;
        coordinates.fixedAt = null;
        coordinates.isFixed = false;
        coordinates.linkAt = null;
        coordinates.user = null;

        return coordinates;
    }

    //첫 진입시 좌표선점
    public void preemption() {
        this.isFixed = true;
        this.fixedAt = LocalDateTime.now();
    }

    //선점 해제
    public void free() {
        this.isFixed = false;
        this.fixedAt = null;
    }

    // 정상적으로 신규게정을 만들었을 때
    public void fixCoordinatesAndUser(User user) {
        this.user = user;
        this.linkAt = LocalDateTime.now();
    }
}
