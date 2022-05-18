package com.mini.timecapsule.model;

import lombok.Getter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Table
@DynamicUpdate
public class Test {

    /* 악용하는 사람들의 데이터 저장 */

}
