package com.mini.timecapsule.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;


/**
 * id : id(auto_increment)
 * name : 보내는사람명
 * userId : 타입캡슐(user) 아이디(FK)
 * letterTemplateType : 편지지타입(이미지 나오는대로 enum class화)
 * status : 상태(작성, 오픈, 삭제)
 * createdAt : 전송요청일
 * content : 내용/컨텐츠
 * requestorInfo : 전송자 정보
 * openedAt : 열어본날짜
 */
@Entity
@Getter
@Setter
@Table(name = "letter_paper")
@DynamicUpdate
public class LetterPaper {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ManyToOne
    private User user;

    /**
     * 편지지타입
     * - 이미지 나오는대로 enum class 화
     */
    private LetterPaperType letterPaperType;

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
     * 내용
     */
    private String content;

    /**
     * 작성자정보
     */
    private String requestorInfo;

    /**
     * 열어본 날짜
     */
    private LocalDateTime openedAt;

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

    public void read() {
        this.status = LetterStatus.OPENED;
        this.openedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void remove() {
        this.status = LetterStatus.REMOVE;
        this.updatedAt = LocalDateTime.now();
    }

    public static LetterPaper newEntity(String name, User user, LetterPaperType letterPaperType,
                                        String content, String requestorInfo) {
        LetterPaper letterPaper = new LetterPaper();
        letterPaper.name = name;
        letterPaper.user = user;
        letterPaper.letterPaperType = letterPaperType;
        letterPaper.status = LetterStatus.UNOPENED;
        letterPaper.content = content;
        letterPaper.createdAt = ZonedDateTime.now();
        letterPaper.requestorInfo = requestorInfo;
        return letterPaper;
    }
}
