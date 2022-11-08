package com.mini.timecapsule.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
/**
 * 기본 vo
 * 화면단에서 정보 넘길때 아이디같은 공통적인 정보를 넘기려고 만들어봄
 *
 *
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class BaseDTO implements Serializable {

	private String id;


}
