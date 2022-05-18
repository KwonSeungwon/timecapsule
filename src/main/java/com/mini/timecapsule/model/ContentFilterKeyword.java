package com.mini.timecapsule.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@Table(name = "content_filter_keyword")
@DynamicUpdate
public class ContentFilterKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String keyword;

    private ZonedDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        ACTIVE,
        INACTIVE
    }

    public static ContentFilterKeyword newEntity(String keyword) {
        ContentFilterKeyword contentFilterKeyword = new ContentFilterKeyword();
        contentFilterKeyword.keyword = keyword;
        contentFilterKeyword.createdAt = ZonedDateTime.now();
        contentFilterKeyword.status = Status.ACTIVE;

        return contentFilterKeyword;
    }


    /**
     * id
     * 필터단어
     * 등록일
     * 사용여부
     */
}
