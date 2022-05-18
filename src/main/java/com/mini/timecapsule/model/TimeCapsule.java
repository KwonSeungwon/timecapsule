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

@Entity
@Getter
@Setter
@Table(name = "time_capsule")
@DynamicUpdate
public class TimeCapsule {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * 요청 메일주소
     */
    private String email;

    /**
     * 요청 총건수
     */
    private Long sendingCount;

    /**
     * 요청비대상건수
     */
    private Long invalidRequestCount;

    /**
     * 타임캡슐 탬플릿 아이디
     */
    private Long timeCapsuleTemplateId;

    /**
     * 첫요청일, 생성일
     */
    private ZonedDateTime createdAt;

    /**
     * 마지막 요청일, 변경일
     */
    private ZonedDateTime updatedAt;

    /**
     * 요청가능일
     */
    private ZonedDateTime limitRequestedTime;

    public static TimeCapsule newEntity(String email, Long timeCapsuleTemplateId, ZonedDateTime limitRequestedTime) {
        TimeCapsule timeCapsule = new TimeCapsule();
        timeCapsule.email = email;
        timeCapsule.sendingCount = 1L;
        timeCapsule.invalidRequestCount = 0L;
        timeCapsule.timeCapsuleTemplateId = timeCapsuleTemplateId;
        timeCapsule.limitRequestedTime = limitRequestedTime;
        timeCapsule.createdAt = ZonedDateTime.now();
        return timeCapsule;
    }


}
