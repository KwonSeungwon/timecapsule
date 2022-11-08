package com.mini.timecapsule.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 결과 코드나 메세지를 통해 화면단에 결과를 넘겨주기위한
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class BaseResponse implements Serializable {

	private String code;
}
