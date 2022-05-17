package com.mini.timecapsule.model;

import jdk.vm.ci.meta.Local;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.stereotype.Controller;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "date_info")
@DynamicUpdate
public class DateInfo {
    /*
    * 일별정보 테이블
    * */

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "dateInfo_no")
    private Long id;
    @Column
    private LocalDateTime date; //

    private Long totalRequestCount;//총전송요천수

    private String mostRequestAddress; //최다 요청주소

    private Long caseNumber; //해당건수

    private LocalDateTime firstRequestTime; //첫요청시간

    private LocalDateTime lastRequestTime; //마지막요청

    private Long validRequest;//유효요청건

    private Long invalidRequest; //미유효

}
