package com.mini.timecapsule.model;

import lombok.Getter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "manager")
@DynamicUpdate
public class Manager {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "manager_no")
    private Long id;

    private String password;

    private String name;

    private String authority; //권한


}
