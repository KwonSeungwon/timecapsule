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
 * id : 생성일(date or String) yyyymmdd
 * totalSendRequestCount : 해당일 총 전송 요청 건수
 * mostRequestAddress : 가장 많이 요청된 이메일(어떻게 찾아낼 건지?)
 * mostRequestAddressCount : 가장 많이 요청된 이메일의 카운트 수(위 문제와 동일)
 * firstRequestAt : 첫 요청시간
 * lastRequestAt : 마지막 요청시간
 * validRequestCount : 유효한(했던) 요청 건수
 * invalidRequestCount : 비유효(실패) 요청 건수
 * 개인적 의견 : 해당 테이블은 통계쿼리로 대체가 가능하지 않을까?
 */
@Entity
@Getter
@Setter
@Table(name = "daily_history")
@DynamicUpdate
public class DailyHistory {
    @Id
    @Column(length = 6)
    private String id;
    private Long totalSendRequestCount;
    private String mostRequestAddress;
    private Long mostRequestAddressCount;
    private ZonedDateTime firstRequestAt;
    private ZonedDateTime lastRequestAt;
    private Long validRequestCount;
    private Long invalidRequestCount;

    public static DailyHistory newDailyHistory(String date, Long totalSendRequestCount, String mostRequestAddress, Long mostRequestAddressCount,
                                         ZonedDateTime firstRequestAt, ZonedDateTime lastRequestAt, Long validRequestCount,
                                         Long invalidRequestCount) {
        DailyHistory dailyTotalInfo = new DailyHistory();
        dailyTotalInfo.id = date;
        dailyTotalInfo.totalSendRequestCount = totalSendRequestCount;
        dailyTotalInfo.mostRequestAddress = mostRequestAddress;
        dailyTotalInfo.mostRequestAddressCount = mostRequestAddressCount;
        dailyTotalInfo.firstRequestAt = firstRequestAt;
        dailyTotalInfo.lastRequestAt = lastRequestAt;
        dailyTotalInfo.validRequestCount = validRequestCount;
        dailyTotalInfo.invalidRequestCount = invalidRequestCount;

        return dailyTotalInfo;
    }

}
