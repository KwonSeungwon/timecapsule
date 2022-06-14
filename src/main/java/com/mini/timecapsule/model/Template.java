package com.mini.timecapsule.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.ZonedDateTime;

/**
 * 불필요로 생각됨
 * id
 * type : 템플릿타입
 * name : 템플릿이름
 * explain : 템플릿설명
 * urlAddress : 시트지 주소(url 인 경우)
 * createdAt : 시트지 등록일
 * templateStatus : 시트지 사용여부
 */
@Entity
@Getter
@Setter
@Table(name = "template")
@DynamicUpdate
public class Template {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "template_type")
    private TemplateType type;

    private String name;

    private String explain;

    private String urlAddress;

    @Column(name = "createdAt")
    private ZonedDateTime createdAt;

    private TemplateStatus templateStatus;

    public static Template newTemplate(TemplateType type, String name, String explain, String urlAddress, ZonedDateTime createdAt, TemplateStatus templateStatus) {
        Template template = new Template();
        template.type = type;
        template.name = name;
        template.explain = explain;
        template.urlAddress = urlAddress;
        template.createdAt = createdAt;
        template.templateStatus = templateStatus;

        return template;
    }

    public enum TemplateType {
        TIMECAPSULE,
        LETTER,
    }

    public enum TemplateStatus {
        ACTIVE,
        INACTIVE,
    }
}
