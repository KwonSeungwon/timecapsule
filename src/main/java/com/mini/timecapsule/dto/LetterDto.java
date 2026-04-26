package com.mini.timecapsule.dto;

import com.mini.timecapsule.model.LetterPaper;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LetterDto {

    private Long id;

    private LetterPaper.LetterStatus status;

    private String requestorInfo;

}
