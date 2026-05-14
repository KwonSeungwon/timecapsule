package com.mini.timecapsule.dto;

import com.mini.timecapsule.model.LetterPaper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank(message = "보내는 사람 이름은 필수입니다.")
    private String senderName;

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "받는 사람 이메일은 필수입니다.")
    private String receiverEmail;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @NotNull(message = "개봉일은 필수입니다.")
    private ZonedDateTime openAt;

    private Boolean isPublic;

    @NotNull(message = "편지지 타입은 필수입니다.")
    private LetterPaper.LetterPaperType letterPaperType;

    @NotNull(message = "캡슐 타입은 필수입니다.")
    private LetterPaper.CapsuleType capsuleType;

    private String imageUrl;
    private String requestorInfo;
}
