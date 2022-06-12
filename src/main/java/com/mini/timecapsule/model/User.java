package com.mini.timecapsule.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import java.util.*;
import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;

/**
 * 아이디(새로운좌표)를 만들었들 때 이용되는 테이블
 * id : key
 * coordinate : 좌표(id를 key로잡아도 될 것같음)
 * pw : 비밀번호
 * loginFailCount : 접속실패 횟수
 * status : 상태(활성화, 비활성화, 밴)
 * name : nick name
 * createdAt : 생성일
 * firstTargetAt : 첫대상일
 * ipaddress : 접속자 아이피
 * lastAccessAt : 마지막접속일 or 해당좌표에서 마지막으로 열어볼 수 있는 기간
 * dummy : 기타데이터
 * letterPaper : 내가 받은 편지(캡슐)들
 * template : 나의 캡슐 모양(템플릿)
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

    private Long coordinate;

    private String password;

    private Integer loginFailCount;

    private Status status;
    
    private ZonedDateTime createdAt; 

    private ZonedDateTime firstTargetAt;

    private String ipaddress;

    private ZonedDateTime lastAccessAt;

    private String dummy;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "id")
    List<LetterPaper> letterPaper = new ArrayList<>();

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id")
    private Template template;

    public enum Status {
        ACTIVE,     //회원가입
        DEACTIVE,   //비가입(캡슐대상 지목만된상태)
        BAN,        //밴당한상태
        INACTIVE,   //정지(회원의지)
    }

    public static User joinUser(String userName, String password, String name, String ipaddress, Template template) {
        User user = new User();
        user.password = password;
        user.template = template;
        user.lastAccessAt = ZonedDateTime.now();
        user.status = Status.ACTIVE;
        user.ipaddress = ipaddress;
        user.loginFailCount = 0;
        user.createdAt = ZonedDateTime.now();
        return user;
    }
}
