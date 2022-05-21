package com.mini.timecapsule.model;

import lombok.Getter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;


/**
 * id : id(auto_increment)
 * password : 로그인비밀번호
 * name : 이름
 * authority : 권한(제한없음, 업데이트, 삭제)
 * other : 메모
 * uniqueness : 특이사항
 */
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
    @Enumerated(EnumType.STRING)
    private Auth authority;
    private String uniqueness;
    public static Manager newManger(String password, String name, Auth authority, String uniqueness) {
        Manager manager = new Manager();
        manager.password = password;
        manager.name = name;
        manager.authority = authority;
        manager.uniqueness = uniqueness;
        return manager;
    }
    public enum Auth {
        UNLIMITED,
        UPDATE,
        DELETE,
    }
}
