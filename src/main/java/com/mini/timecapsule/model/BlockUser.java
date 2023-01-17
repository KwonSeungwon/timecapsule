package com.mini.timecapsule.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.ZonedDateTime;

/**
 * 불필요로생각됨
 * 디자이너분의 컨셉대로 본인메일입력및 간단 인증절차를 넣는건어떤지? 서비스 안정성이 많이 보장될거같음BlockUser
 * id : 차단된 id 주소
 * idType : 차단된 ip의 타입(고민해봐야 됨)
 * memo : 메모(차단사유등의 이유)
 * createdAt : 생성일
 */
@Entity
@Getter
@Setter
@Table(name = "block_user")
@DynamicUpdate
public class BlockUser {
    @Id
    @Column(name = "ip")
    private String id;

    private String ipType;

    private String memo;

    private ZonedDateTime createdAt;

    private ZonedDateTime updatedAt;

    public static BlockUser newBlockUser(String ip, String ipType, String memo) {
        BlockUser blockUser = new BlockUser();
        blockUser.id = ip;
        blockUser.ipType = ipType;
        blockUser.memo = memo;
        blockUser.createdAt = ZonedDateTime.now();
        blockUser.updatedAt = ZonedDateTime.now();
        return blockUser;
    }

    public void update(String memo) {
        this.memo = memo;
        this.updatedAt = ZonedDateTime.now();
    }
}
