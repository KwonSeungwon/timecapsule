package com.mini.timecapsule.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.ZonedDateTime;


/**
 * id : id(auto_increment)
 * filterKeyword : 필터단어
 * used : 필터단어 사용여부(사용, 미사용)
 * createdDate : 필터단어생성일
 */
@Entity
@Getter
@Setter
@Table(name = "content_filter")
public class ContentFilter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "filter_keyword")
    private String filterKeyword;

    @Enumerated(EnumType.STRING)
    private Used used;

    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    @Column(name = "created_date")
    private ZonedDateTime createdDate;

    public static ContentFilter newContentFilter(String filterKeyword, Used used) {
        ContentFilter filter = new ContentFilter();
        filter.filterKeyword = filterKeyword;
        filter.used = used;
        filter.createdDate = ZonedDateTime.now();
        return filter;
    }

    public enum Used {
        ACTIVE,
        INACTIVE,
    }

}
