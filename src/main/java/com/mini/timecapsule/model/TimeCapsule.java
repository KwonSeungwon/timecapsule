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

    private Integer sendingCount;

    private String uiUrl;

    private String name;

    private String email;

    private ZonedDateTime createdAt;

    public TimeCapsule() {} //default construct
    public TimeCapsule(Integer sendingCount, String uiUrl, String name, String email) {
        this.sendingCount = sendingCount;
        this.uiUrl = uiUrl;
        this.name = name;
        this.email = email;
        this.createdAt = ZonedDateTime.now();
    }


}
