package com.mini.timecapsule.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class SendLetterDTO extends BaseDTO {

	/*편지내용*/
	private String letter;

}
