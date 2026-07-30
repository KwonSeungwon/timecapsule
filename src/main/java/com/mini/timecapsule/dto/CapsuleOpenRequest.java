package com.mini.timecapsule.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapsuleOpenRequest {
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}
