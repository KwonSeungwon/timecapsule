package com.mini.timecapsule.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@Table(name = "daily_total_info")
@DynamicUpdate
public class DailyTotalInfo {

    /**
     * 아이디, 생성일 yyMMdd
     */
    @Id
    @Column(length = 6)
    private String id;

    /**
     * 해당일 총 전송 요청 건수
     */
    private Long totalSendRequestCount;

    /**
     * 가장많이 요청된 메일주소
     */
    private String mostRequestAddress;

    /**
     * 가장많이 요청된 메일주소의 요청수
     */
    private Long mostRequestAddressCount;

    /**
     * 첫 요청 시간
     */
    private ZonedDateTime firstRequestAt;

    /**
     * 마지막 요청 시간
     */
    private ZonedDateTime lastRequestAt;

    /**
     * 유효 요청 건수
     */
    private Long validRequestCount;

    /**
     * 비 유효 요청 건수
     */
    private Long invalidRequestCount;

    public static DailyTotalInfo newEntity(String date, Long totalSendRequestCount, String mostRequestAddress, Long mostRequestAddressCount,
                                           ZonedDateTime firstRequestAt, ZonedDateTime lastRequestAt, Long validRequestCount,
                                           Long invalidRequestCount) {
        DailyTotalInfo dailyTotalInfo = new DailyTotalInfo();
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
