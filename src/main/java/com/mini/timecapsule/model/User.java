package com.mini.timecapsule.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * time capsule + user 테이블
 */
@Entity
@Getter
@Setter
@Table(name = "users")
@DynamicUpdate
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 알림을 받을 이메일 주소
     */
    private String email;

    /**
     * 좌표
     */
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name="coordinate_id", nullable = false)
    private Coordinates coordinates;

    /**
     * 캡슐명
     */
    @Column(nullable = false)
    private String name;

    /**
     * 비밀번호
     */
    @Column(nullable = false)
    private String password;

    /**
     * 상태
     */
    @Enumerated(EnumType.STRING)
    private Status status;

    /**
     * 캡슐타입
     */
    private CapsuleType capsuleType;

    /**
     * 오픈 가능일 타입
     */
    @Enumerated(EnumType.STRING)
    private OpenDay openDayType;

    /**
     * 생성일
     */
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    private ZonedDateTime createdAt;

    /**
     * 작성가능일
     */
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    private ZonedDateTime writeableAt;

    /**
     * 첫편지일수
     */
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    private ZonedDateTime firstTargetAt;

    /**
     * 마지막 접속일
     */
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    private ZonedDateTime lastAccessAt;

    /**
     * 기타데이터
     */
    private String dummy;

    public enum Status {
        ACTIVE,     //회원가입
        BAN,        //밴당한상태
        INACTIVE,   //정지(회원의지)
    }

    public enum OpenDay {
        NEW_YEAR("01-01"),
        SPRING_BREAK("02-28"),
        END_YEAR("12-31");

        private final String value;

        OpenDay(String s) {
            this.value = s;
        }

        public String getValue() {
            return value;
        }
    }

    public enum CapsuleType {
        EGG, 
        LETTERBOTTLE, 
        BAMBOOTUBE, 
        CANDYCASE 
    }

    public static User joinUser(Coordinates coordinates, String password, String name, String email,
                                CapsuleType capsuleType, OpenDay openDayType,
                                ZonedDateTime writeableAt, String dummy) {
        User user = new User();
        user.coordinates = coordinates;
        user.password = password;
        user.name = name;
        user.email = email;
        user.capsuleType = capsuleType;
        user.openDayType = openDayType;
        user.writeableAt = writeableAt;
        user.lastAccessAt = ZonedDateTime.now();
        user.status = Status.ACTIVE;
        user.createdAt = ZonedDateTime.now();
        user.dummy = dummy;
        return user;
    }
}
