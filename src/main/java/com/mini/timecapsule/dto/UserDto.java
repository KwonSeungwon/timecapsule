package com.mini.timecapsule.dto;

import com.mini.timecapsule.model.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserDto implements Serializable {

    private String coordinates;

    private Long id;

    private String password;

    private String email;

    private User.OpenDay openDayType;

    private User.CapsuleType capsuleType;

    private String uiUrl;

    private String name;

    private String createdAt;

    private String firstTargetAt;

    private String lastAccessAt;

}
