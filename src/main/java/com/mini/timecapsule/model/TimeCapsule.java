package com.mini.timecapsule.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.ZonedDateTime;


/**
 * 불필요로생각됨
 * id : id(auto_increment)
 * requestEmail : 요청메일주소
 * sendingCount : 요청 총 건수
 * invalidRequestCount : 요청비대상 건수
 * timeCapsuleTemplateId : 타임캡슐 템플릿 아이디(FK)
 * createdAt : 생성일
 * updatedAt : 변경일
 * limitRequestedTime : 요청가능일
 */
@Entity
@Getter
@Setter
@Table(name = "time_capsule")
@DynamicUpdate
public class TimeCapsule {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String requestEmail;
    private Long sendingCount;
    private Long invalidRequestCount;
    private Long timeCapsuleTemplateId;
    private ZonedDateTime createdAt;

    private ZonedDateTime updatedAt;
    private ZonedDateTime limitRequestedTime;

    public static TimeCapsule newTimeCapsule(String requestEmail, Long timeCapsuleTemplateId, ZonedDateTime limitRequestedTime) {
        TimeCapsule timeCapsule = new TimeCapsule();
        timeCapsule.requestEmail = requestEmail;
        timeCapsule.sendingCount = 1L;
        timeCapsule.invalidRequestCount = 0L;
        timeCapsule.timeCapsuleTemplateId = timeCapsuleTemplateId;
        timeCapsule.limitRequestedTime = limitRequestedTime;
        timeCapsule.createdAt = ZonedDateTime.now();
        return timeCapsule;
    }
}
