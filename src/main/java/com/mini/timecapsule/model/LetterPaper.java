package com.mini.timecapsule.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.ZonedDateTime;


/**
 * id : id(auto_increment)
 * timeCapsuleId : 타입캡슐 아이디(FK)
 * createdAt : 전송요청일
 * letterTemplateId : 템플릿(view) id /FK?
 * content : 내용/컨텐츠
 * requestorInfo : 전송자 정보
 * requestStatus : 전송상태(활성, 비활성)
 */
@Entity
@Getter
@Setter
@Table(name = "letter_paper")
@DynamicUpdate
public class LetterPaper {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long timeCapsuleId;

    private ZonedDateTime createdAt;
    private Long letterTemplateId;
    private String content;
    private String requestorInfo;
    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus;
    public enum RequestStatus {
        ACTIVE,
        INACTIVE
    }
    public static LetterPaper newEntity(Long timeCapsuleId, Long letterTemplateId, String content, String requestorInfo) {
        LetterPaper letterPaper = new LetterPaper();
        letterPaper.timeCapsuleId = timeCapsuleId;
        letterPaper.letterTemplateId = letterTemplateId;
        letterPaper.content = content;
        letterPaper.requestorInfo = requestorInfo;
        letterPaper.requestStatus = RequestStatus.ACTIVE;
        return letterPaper;
    }
}
