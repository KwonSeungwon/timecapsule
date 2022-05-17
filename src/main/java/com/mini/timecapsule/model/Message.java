package com.mini.timecapsule.model;

import lombok.Getter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Table(name = "message")
@DynamicUpdate
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "message_Id")
    private Long id;
    //mail 연결
    //

    private Date dateInfo; //요청일

    private String contents; //내용

    private String requeInfo;//요청자

    private Boolean transfer;// 전송여부


}
