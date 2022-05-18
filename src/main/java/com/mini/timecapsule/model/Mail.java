package com.mini.timecapsule.model;

import lombok.Getter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "mail")
@DynamicUpdate
public class Mail {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "mail_no")
    private Long id;

    private String address;//메일주소

    private LocalDateTime firRequestDate; //첫요청일

    private LocalDateTime lastRequestDate; //마지막요청일

    private Long totalCount; //총 건수

    private Long invalidCount; // 비유효건수
    //private LocalDateTime lastRequestDate;//마지막 요청가능일

    private String permission; //제한 이메일 여부 Y/N






}
