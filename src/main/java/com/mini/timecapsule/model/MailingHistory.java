package com.mini.timecapsule.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@Table(name = "mailing_history")
@DynamicUpdate
public class MailingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    @Column(name = "send_date", nullable = false)
    private ZonedDateTime sendDate;

    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    @Column(name ="arrived_date", nullable = false)
    private ZonedDateTime arrivedDate;

    @Column(name ="sended_ip", nullable = false)
    private String sendIp;
    @Column(name ="send_state", nullable = false)
    private Boolean sendState;

    public MailingHistory() {}
    public MailingHistory(ZonedDateTime sendDate, ZonedDateTime arrivedDate, String sendIp, Boolean sendState) {
        this.sendDate = sendDate;
        this.arrivedDate = arrivedDate;
        this.sendIp = sendIp;
        this.sendState = sendState;
    }

}
