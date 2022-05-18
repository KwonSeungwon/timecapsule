package com.mini.timecapsule.model;

import lombok.Getter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "image")
@DynamicUpdate
public class Image {
 //test


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "image_no")
    private Long id;

    private String type;//템플릿 타입(어느곳에서 사용할건지 )

    private String name;//이름

    private String capsule;//캡슐

    private Boolean use;//시트지 사용여부

    private String imageAddress;//시트지 주소

    private LocalDateTime date;//등록일

    private Long count;//사용카운트(인기 카운트)

}
