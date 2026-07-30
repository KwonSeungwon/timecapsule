package com.mini.timecapsule.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    /**
     * 이 서비스의 "하루" 경계.
     *
     * <p>프론트는 사용자가 고른 날짜의 KST 자정을 openAt 으로 보내고, 사용자도 KST 로 읽는다.
     * 서버 기본 시간대에 의존하면 컨테이너 TZ 에 따라 D-Day 가 하루씩 밀린다.
     */
    public static final ZoneId SERVICE_ZONE = ZoneId.of("Asia/Seoul");

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
     * 알림 발송 시도 횟수. SMTP 가 계속 실패하는 캡슐이 매 스케줄마다
     * 영원히 재시도되는 것을 막는다.
     */
    private Integer notifyAttempts = 0;

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
     * 개봉까지 남은 일수 (D-Day).
     *
     * <p>경과 시간이 아니라 <b>KST 달력 날짜</b> 차이다. 사용자는 달력에서 날짜를 고르고
     * 프론트는 그 날 KST 자정을 openAt 으로 보낸다. 시간 차를 그대로 잘라 쓰면
     * (예전 구현) 개봉 전날 저녁에 이미 D-0 이 떠서 오늘 열리는 것처럼 보인다.
     */
    public long getDDay() {
        return getDDay(ZonedDateTime.now(SERVICE_ZONE));
    }

    /** 테스트에서 기준 시각을 고정하기 위한 오버로드. */
    long getDDay(ZonedDateTime now) {
        if (openAt == null) return 0;
        return ChronoUnit.DAYS.between(
                now.withZoneSameInstant(SERVICE_ZONE).toLocalDate(),
                openAt.withZoneSameInstant(SERVICE_ZONE).toLocalDate());
    }

    /**
     * 캡슐을 개봉 상태로 바꾼다.
     *
     * <p>여기서 던지는 {@code IllegalStateException} 은 <b>내부 불변식 위반</b>이지
     * 사용자에게 보여줄 메시지가 아니다. API 경로에서는 도달할 수 없다 —
     * {@code LetterService#getDetail} 이 개봉일 전이면 {@code read()} 를 부르지 않고
     * 그대로 반환하고, 컨트롤러가 {@code openable:false} + {@code content} 없음으로
     * 200 을 내려준다. 그게 프론트가 소비하는 계약이다.
     *
     * <p>예전엔 {@code GlobalExceptionHandler} 에 이 예외를 <b>403 + e.getMessage()</b> 로
     * 내보내는 핸들러가 있었고, "D-N 을 사용자에게 알려주려는 것"이라고 정당화됐다.
     * 그 정당화는 사실이 아니었다(위 경로 때문에 도달 불가). 남은 건 Spring/Hibernate/
     * Jackson 이 던지는 <b>임의의</b> {@code IllegalStateException} 메시지를 그대로
     * 클라이언트에 흘려보내는 통로였고, 상태코드도 의미상 틀렸다. 그래서 제거했다.
     *
     * <p><b>이 예외를 다시 사용자 문구로 쓰지 말 것.</b> 개봉 가능 여부는 200 응답의
     * {@code openable} / {@code dday} 필드로 전달한다.
     */
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

    /** 알림 발송 성공 처리 */
    public void markNotified() {
        this.isNotified = true;
        this.updatedAt = LocalDateTime.now();
    }

    /** 알림 발송 실패 1회 기록. 상한에 도달하면 true(=포기) 를 돌려준다. */
    public boolean recordNotifyFailure(int maxAttempts) {
        this.notifyAttempts = (this.notifyAttempts == null ? 0 : this.notifyAttempts) + 1;
        this.updatedAt = LocalDateTime.now();
        return this.notifyAttempts >= maxAttempts;
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
