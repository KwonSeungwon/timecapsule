package com.mini.timecapsule.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@Table(name = "template")
@DynamicUpdate
public class Template {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Type type;

    private String name;

    private String description;

    private String address;

    private ZonedDateTime createdAt;

    private Status status;

    public enum Type {
        TIMECAPSULE,
        LETTER,
    }

    public enum Status {
        ACTIVE,
        INACTIVE,
    }

    /**
     * id
     * 템플릿타입
     * 이름
     * 설명
     * 시트지 주소
     * 시트지 등록일
     * 시트지 사용여부
     */
}
