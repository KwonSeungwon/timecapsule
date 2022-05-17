package com.mini.timecapsule.model;

import lombok.Getter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "filter")
@DynamicUpdate
public class Filter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "filter_no")
    private Long id;

    private String filterword;//필터단어

    private LocalDateTime regiDate; //등록일

    private Boolean useInfo; //사용여부




}
