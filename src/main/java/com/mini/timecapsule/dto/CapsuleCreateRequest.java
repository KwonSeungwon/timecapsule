package com.mini.timecapsule.dto;

import com.mini.timecapsule.model.LetterPaper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CapsuleCreateRequest {
    private String senderName;
    private String title;
    private String receiverEmail;
    private String password;
    private String content;
    private ZonedDateTime openAt;
    private Boolean isPublic;
    private LetterPaper.LetterPaperType letterPaperType;
    private LetterPaper.CapsuleType capsuleType;
    private String imageUrl;
    private String requestorInfo;
}
