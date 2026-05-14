package com.mini.timecapsule.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;


/**
 * id : id(auto_increment)
 * senderName : 보내는사람명
 * title : 타임캡슐 제목
 * receiverEmail : 받는사람 이메일
 * password : 비밀번호
 * xCoord : x좌표
 * yCoord : y좌표
 * biome : 바이옴
 * locationName : 장소명
 * letterTemplateType : 편지지타입
 * capsuleType : 캡슐 외형 타입
 * status : 상태(작성, 오픈, 삭제)
 * createdAt : 전송요청일
 * content : 내용/컨텐츠
 * imageUrl : 추억 이미지 URL
 * requestorInfo : 전송자 정보
 * openedAt : 열어본날짜
 * openAt : 개봉 예정일
 * isPublic : 공개 여부
 */
@Entity
@Getter
@Setter
@Table(name = "letter_paper")
@DynamicUpdate
public class LetterPaper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String senderName;

    private String title;

    private String receiverEmail;

    @Column(length = 255)
    private String password;

    private Integer xCoord;

    private Integer yCoord;

    private String biome;

    private String locationName;

    /**
     * 편지지타입 (내부 디자인)
     */
    @Enumerated(EnumType.STRING)
    private LetterPaperType letterPaperType;

    /**
     * 캡슐 외형 타입 (디자인)
     */
    @Enumerated(EnumType.STRING)
    private CapsuleType capsuleType;

    /**
     * 편지상태
     * - 미오픈, 오픈, 삭제(계정요청)
     */
    @Enumerated(EnumType.STRING)
    private LetterStatus status;

    /**
     * 작성일
     */
    private ZonedDateTime createdAt;

    /**
     * 개봉 예정일
     */
    private ZonedDateTime openAt;

    /**
     * 내용
     */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * 이미지 URL
     */
    private String imageUrl;

    /**
     * 작성자정보
     */
    private String requestorInfo;

    /**
     * 열어본 날짜
     */
    private LocalDateTime openedAt;

    /**
     * 알림 발송 여부
     */
    private Boolean isNotified = false;

    /**
     * 공개 여부
     */
    private Boolean isPublic = false;

    /**
     * 변경일
     */
    private LocalDateTime updatedAt;

    public enum LetterStatus {
        UNOPENED,
        OPENED,
        REMOVE
    }

    public enum LetterPaperType {
        LETTER,
        NOTE,
        POLAROID,
        SHEEPSKIN
    }

    public enum CapsuleType {
        BOTTLE,
        BAMBOO,
        EGG,
        CANDYBOX
    }

    /**
     * 개봉까지 남은 일수 계산 (D-Day)
     */
    public long getDDay() {
        if (openAt == null) return 0;
        return ChronoUnit.DAYS.between(ZonedDateTime.now(), openAt);
    }

    public void read() {
        if (openAt != null && ZonedDateTime.now().isBefore(openAt)) {
            throw new IllegalStateException("아직 개봉할 수 없는 타임캡슐입니다. D-" + getDDay());
        }
        this.status = LetterStatus.OPENED;
        this.openedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void remove() {
        this.status = LetterStatus.REMOVE;
        this.updatedAt = LocalDateTime.now();
    }

    public static LetterPaper newEntity(String senderName, String title, String receiverEmail, String password,
                                        Integer xCoord, Integer yCoord, String biome, String locationName,
                                        LetterPaperType letterPaperType, CapsuleType capsuleType, String content,
                                        String imageUrl, String requestorInfo, ZonedDateTime openAt, Boolean isPublic) {
        LetterPaper letterPaper = new LetterPaper();
        letterPaper.senderName = senderName;
        letterPaper.title = title;
        letterPaper.receiverEmail = receiverEmail;
        letterPaper.password = password;
        letterPaper.xCoord = xCoord;
        letterPaper.yCoord = yCoord;
        letterPaper.biome = biome;
        letterPaper.locationName = locationName;
        letterPaper.letterPaperType = letterPaperType;
        letterPaper.capsuleType = capsuleType;
        letterPaper.status = LetterStatus.UNOPENED;
        letterPaper.content = content;
        letterPaper.imageUrl = imageUrl;
        letterPaper.createdAt = ZonedDateTime.now();
        letterPaper.requestorInfo = requestorInfo;
        letterPaper.openAt = openAt;
        letterPaper.isPublic = isPublic != null ? isPublic : false;
        return letterPaper;
    }
}
