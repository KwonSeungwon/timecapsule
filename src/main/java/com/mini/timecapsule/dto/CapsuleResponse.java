package com.mini.timecapsule.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mini.timecapsule.model.LetterPaper;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CapsuleResponse {

    private Long id;
    private String senderName;
    private String title;
    private String receiverEmail;
    private Integer xCoord;
    private Integer yCoord;
    private String biome;
    private String locationName;
    private LetterPaper.LetterPaperType letterPaperType;
    private LetterPaper.CapsuleType capsuleType;
    private LetterPaper.LetterStatus status;
    private ZonedDateTime createdAt;
    private ZonedDateTime openAt;
    private String content;
    private String imageUrl;
    private String requestorInfo;
    private LocalDateTime openedAt;
    private Boolean isNotified;
    private Boolean isPublic;
    private long dday;
    private boolean openable;

    @JsonProperty("xCoord")
    public Integer getXCoord() {
        return xCoord;
    }

    @JsonProperty("yCoord")
    public Integer getYCoord() {
        return yCoord;
    }

    public static CapsuleResponse from(LetterPaper letter) {
        return from(letter, false);
    }

    /**
     * 공개 탐색(/public)용 뷰. 수신자 이메일·본문·작성자정보는 절대 넣지 않는다.
     * 인증 없이 누구나 조회 가능한 엔드포인트라 이메일이 나가면 그대로 수집당한다.
     * 필드를 추가할 땐 개인 식별 정보가 아닌지 먼저 확인할 것.
     */
    public static CapsuleResponse publicView(LetterPaper letter) {
        CapsuleResponse full = from(letter, false);
        return CapsuleResponse.builder()
                .id(full.getId())
                .senderName(full.getSenderName())
                .title(full.getTitle())
                .xCoord(full.getXCoord())
                .yCoord(full.getYCoord())
                .biome(full.getBiome())
                .locationName(full.getLocationName())
                .letterPaperType(full.getLetterPaperType())
                .capsuleType(full.getCapsuleType())
                .status(full.getStatus())
                .createdAt(full.getCreatedAt())
                .openAt(full.getOpenAt())
                .isPublic(full.getIsPublic())
                .dday(full.getDday())
                .openable(full.isOpenable())
                .build();
    }

    public static CapsuleResponse from(LetterPaper letter, boolean includeContent) {
        boolean openable = letter.getOpenAt() == null || !ZonedDateTime.now().isBefore(letter.getOpenAt());

        return CapsuleResponse.builder()
                .id(letter.getId())
                .senderName(letter.getSenderName())
                .title(letter.getTitle())
                .receiverEmail(letter.getReceiverEmail())
                .xCoord(letter.getXCoord())
                .yCoord(letter.getYCoord())
                .biome(letter.getBiome())
                .locationName(letter.getLocationName())
                .letterPaperType(letter.getLetterPaperType())
                .capsuleType(letter.getCapsuleType())
                .status(letter.getStatus())
                .createdAt(letter.getCreatedAt())
                .openAt(letter.getOpenAt())
                .content(includeContent ? letter.getContent() : null)
                .imageUrl(includeContent ? letter.getImageUrl() : null)
                .requestorInfo(letter.getRequestorInfo())
                .openedAt(letter.getOpenedAt())
                .isNotified(letter.getIsNotified())
                .isPublic(letter.getIsPublic())
                .dday(letter.getDDay())
                .openable(openable)
                .build();
    }
}
