package com.mini.timecapsule.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@Table(name = "filter_option")
public class FilterOption {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "filter_detail")
    private String filterDetail;

    private Boolean used;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;

    public FilterOption() {}
    public FilterOption(String filterDetail, Boolean used, ZonedDateTime createdDate) {
        this.filterDetail = filterDetail;
        this.used = used;
        this.createdDate = createdDate;
    }
}
