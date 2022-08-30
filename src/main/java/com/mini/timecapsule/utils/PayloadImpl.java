package com.mini.timecapsule.utils;

import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class PayloadImpl extends UObject {

	private EndResult result;
	private PropertyMap data;
	private PropertyMap info;

	private ModelAndView modelAndView = new ModelAndView();

	@JsonIgnore
	private PropertyMap viewData;

	private PayloadImpl(EndResult result, PropertyMap data, PropertyMap info) {
		this.result = result;
		this.data = data;
		this.info = info;
		this.viewData = new PropertyMap();
	}

	public PropertyMap addData(String name, Object value) {
		return PropertyMap.newInstance(name, value);
	}
	public PropertyMap addData(Map<String, Object> data) {
		return PropertyMap.from(data);
	}

	public ModelAndView toModelAndView(String viewName) {
		modelAndView.setViewName(viewName);
		return modelAndView;
	}
}
