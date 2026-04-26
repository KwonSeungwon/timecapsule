package com.mini.timecapsule.dto;

import com.mini.timecapsule.model.LetterPaper;
import lombok.Data;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
public class SendCapsuleDto implements Serializable {

    private String title;

    private String coordinates;

    private String sender;

    private String password;

    private LetterPaper.LetterPaperType letterPaperType;

    private LetterPaper.CapsuleType capsuleType;

    private String content;

    private String imageUrl;

    private String requestorInfo;

    private Long userId;

    private Boolean isPublic;

    /**
     * 개봉 예정일 (미래의 특정 시점)
     */
    private ZonedDateTime openAt;
}
