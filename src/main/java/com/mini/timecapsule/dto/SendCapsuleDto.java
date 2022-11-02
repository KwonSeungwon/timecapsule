package com.mini.timecapsule.dto;

import com.mini.timecapsule.model.LetterPaper;
import lombok.Data;

@Data
public class SendCapsuleDto {

    private String coordinates;

    private String sender;

    private String password;

    private LetterPaper.LetterPaperType letterPaperType;

    private String content;

    private String requestorInfo;

    private Long userId;
}
