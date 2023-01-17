package com.mini.timecapsule.dto;

import com.mini.timecapsule.model.LetterPaper;
import lombok.Data;

import java.io.Serializable;

@Data
public class SendCapsuleDto implements Serializable {

    private String coordinates;

    private String sender;

    private String password;

    private LetterPaper.LetterPaperType letterPaperType;

    private String content;

    private String requestorInfo;

    private Long userId;
}
