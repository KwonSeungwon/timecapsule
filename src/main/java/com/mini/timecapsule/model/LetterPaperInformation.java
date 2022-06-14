package com.mini.timecapsule.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@Table(name = "Letter_paper_information")
public class LetterPaperInformation {

    @Id
    private Long id;

    private String emailAddress;

    private ZonedDateTime sendRequestAt;

    private String coordinates;

    private ZonedDateTime sendAt;

    private Boolean sendState;
    
}
