package com.mini.timecapsule.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.ZonedDateTime;


/**
 * id : id(auto_increment)
 * user : 발송하는 대상
 * email : 열어볼 수 있는 날짜알림을 원할경우 이메일주소 받음
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

    private String senderName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id", insertable = false, updatable = false)
    @JsonIgnore
    private User user;

    private String email;

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

    public static LetterPaper newEntity(User user, String email, Long letterTemplateId, String content, String requestorInfo) {
        LetterPaper letterPaper = new LetterPaper();
        letterPaper.user = user;
        letterPaper.email = email;
        letterPaper.letterTemplateId = letterTemplateId;
        letterPaper.content = content;
        letterPaper.requestorInfo = requestorInfo;
        letterPaper.requestStatus = RequestStatus.ACTIVE;
        return letterPaper;
    }
}
