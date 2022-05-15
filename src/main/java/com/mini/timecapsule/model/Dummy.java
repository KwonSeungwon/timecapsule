package com.mini.timecapsule.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "dummy")
public class Dummy {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String loginId;

    private String passowrd;

    //추후 enum으로..
    private String auth;

    private String other;

    public Dummy() {}
    public Dummy(String loginId, String passowrd, String auth, String other) {
        this.loginId = loginId;
        this.passowrd = passowrd;
        this.auth = auth;
        this.other = other;
    }
}
