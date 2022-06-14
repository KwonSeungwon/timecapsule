package com.mini.timecapsule.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private Long id;

    private String email;

    private String uiUrl;

    private String name;

    private String date;
}
