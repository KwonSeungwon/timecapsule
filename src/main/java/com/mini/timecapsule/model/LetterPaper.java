package com.mini.timecapsule.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@Table(name = "letter_paper")
@DynamicUpdate
public class LetterPaper {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * 타임캡슐 아이디
     */
    private Long timeCapsuleId;

    /**
     * 전송요청일
     */
    private ZonedDateTime createdAt;

    /**
     * 편지지 아이디
     */
    private Long letterTemplateId;

    /**
     * 내용
     */
    private String content;

    /**
     * 전송자 정보
     */
    private String requestorInfo;

    /**
     * 전송상태
     */
    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        ACTIVE,
        INACTIVE
    }

    public static LetterPaper newEntity(Long timeCapsuleId, Long letterTemplateId, String content, String requestorInfo) {
        LetterPaper letterPaper = new LetterPaper();
        letterPaper.timeCapsuleId = timeCapsuleId;
        letterPaper.letterTemplateId = letterTemplateId;
        letterPaper.content = content;
        letterPaper.requestorInfo = requestorInfo;
        letterPaper.status = Status.ACTIVE;
        return letterPaper;
    }

}
