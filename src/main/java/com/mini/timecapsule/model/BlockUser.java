package com.mini.timecapsule.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@Table(name = "block_user_ip_address")
@DynamicUpdate
public class BlockUser {

    @Id
    @Column(name = "ip")
    private String id;

    private String ipType;

    private String memo;

    private ZonedDateTime createdDate;

    public BlockUser() {} //default construct
    public BlockUser(String ip, String ipType, String memo) {
        this.id = ip;
        this.ipType = ipType;
        this.memo = memo;
        this.createdDate = ZonedDateTime.now();
    }
}
