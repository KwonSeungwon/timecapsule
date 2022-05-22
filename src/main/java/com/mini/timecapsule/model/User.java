package com.mini.timecapsule.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.ZonedDateTime;

/**
 * 디자이너분의 컨셉대로 본인메일입력및 간단 인증절차를 넣는건어떤지? 서비스 안정성이 많이 보장될거같음
 * id : key
 * userName : 메일주소
 * pw : 비밀번호
 * loginFailCount : 접속실패
 * status : 상태(활성화, 비활성화, 밴)
 * name : nick name
 * createdAt : 생성일
 * firstTargetAt : 첫대상일
 * lastAccessAt : 마지막접속일
 * dummy : 기타데이터
 */
@Entity
@Getter
@Setter
@Table(name = "user")
@DynamicUpdate
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ip")
    private String id;

    private String userName;

    private String password;

    private byte loginFailCount;

    private Status status;

    private String name;

    private ZonedDateTime createdAt;

    private ZonedDateTime firstTargetAt;

    private ZonedDateTime lastAccessAt;

    private String dummy;

    public enum Status {
        ACTIVE,     //회원가입
        DEACTIVE,   //비가입(캡슐대상 지목만된상태)
        BAN,        //밴당한상태
        INACTIVE,   //정지(회원의지)
    }

    public static User targetUser(String userName) {
        User user = new User();
        user.userName = userName;
        user.status = Status.DEACTIVE;
        user.firstTargetAt = ZonedDateTime.now();
        return user;
    }

    public static User joinUser(String userName, String password, String name) {
        User user = new User();
        user.userName = userName;
        user.password = password;
        user.name = name;
        user.lastAccessAt = ZonedDateTime.now();
        user.status = Status.ACTIVE;
        user.loginFailCount = 0;
        user.createdAt = ZonedDateTime.now();
        return user;
    }

}
