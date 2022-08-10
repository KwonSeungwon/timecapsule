package com.mini.timecapsule.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

/**
 * time capsule + user 테이블
 * id : key
 * coordinates : 좌표 'xxxyyy' 좌표별 배경이달라
 * pw : 비밀번호 (4자리이상)
 * status : 상태(활성화, 비활성화, 밴)
 * name : 캡슐명
 * capsuleType : 캡슐타입
 * openDayType : enum type(1.1, 2.28, 12.31)
 * createdAt : 생성일
 * firstTargetAt : 첫대상일
 * lastAccessAt : 마지막접속일
 * dummy : 기타데이터
 * ※ user라는 단어는 H2에서는 예약어이기 떄문에 사용할 수 없습니다.
 */
@Entity
@Getter
@Setter
@Table(name = "users")
@DynamicUpdate
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    /**
     * 좌표
     * - 'xxxyyy' 형식
     * - 좌표에 따른 배경 변화
     * 좌표 테이블 만들기 + 좌표 세션유지
     */
    @Column(nullable = false, length = 6)
    private String coordinates;

    /**
     * 캡슐명
     */
    @Column(nullable = false)
    private String name;

    /**
     * 비밀번호
     * - 최소 4자리
     */
    @Column(nullable = false)
    @Size(min = 4)
    private String password;

    /**
     * 상태
     */
    @Enumerated(EnumType.STRING)
    private Status status;

    /**
     * 캡슐타입
     * 컵셉 정의되는대로 enum class화
     */
    private CapsuleType capsuleType;

    /**
     * 오픈 가능일
     * - type 형
     * - 1.1, 2.28, 12.31
     */
    @Enumerated(EnumType.STRING)
    private OpenDay openDayType;

    @OneToMany
    private List<LetterPaper> letterPapers;

    /**
     * 생성일
     */
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    private ZonedDateTime createdAt;

    /**
     * 작성가능일
     * 오픈일로부터 1~2개월이내, 최소작성기간에대한 고민필요
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
        EGG, // 계란형
        LETTERBOTTLE, //편지가 들어있는 병
        BAMBOOTUBE, // 죽통(대나무통)
        CANDYCASE // 사탕통
    }

    public static User joinUser(String coordinates, String password, String name,
                                CapsuleType capsuleType, OpenDay openDayType,
                                ZonedDateTime writeableAt, String dummy) {
        User user = new User();
        user.coordinates = coordinates;
        user.password = password;
        user.name = name;
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
