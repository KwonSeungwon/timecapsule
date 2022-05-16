package com.mini.timecapsule.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@Table(name = "mailing_history")
@DynamicUpdate
public class Template {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "url_link", nullable = false)
    private String templateUrl;

    private String explain;

    private String code;

    @Column(name = "template_type")
    private String type;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;

    @Column(name = "used")
    private Boolean isUsed;

    public Template() {}
    public Template(String templateUrl, String explain, String code, String type, ZonedDateTime createdDate, Boolean isUsed) {
        this.templateUrl = templateUrl;
        this.explain = explain;
        this.code = code;
        this.type = type;
        this.createdDate = createdDate;
        this.isUsed = isUsed;
    }
}
