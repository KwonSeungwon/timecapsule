package com.mini.timecapsule.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

public class PayloadImpl extends UObject implements Payload {
    private EndResult result;
    private PropertyMap data;
    private PropertyMap info;
    @JsonIgnore
    private PropertyMap viewData;

    private PayloadImpl(EndResult result, PropertyMap data, PropertyMap info) {
        this.result = result;
        this.data = data;
        this.info = info;
        this.viewData = new PropertyMap();
    }

    public void addViewData(String name, Object value) {
        this.viewData.put(name, value);
    }

    @Override
    public void addViewData(Map<String, Object> data) {
        Payload.super.addViewData(data);
    }

    public Object getViewData(String name) {
        return this.viewData.get(name);
    }

    public ModelAndView toModelAndView(String viewName) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(viewName);
        modelAndView.addObject("result", this.result);
        modelAndView.addObject("data", this.data);
        modelAndView.addObject("info", this.info);
        modelAndView.addAllObjects(this.viewData);
        return modelAndView;
    }

    @Override
    public ModelAndView toModelAndView() {
        return Payload.super.toModelAndView();
    }

    @Override
    public ModelAndView redirect(String url) {
        return Payload.super.redirect(url);
    }

    @Override
    public ModelAndView redirect(String url, PropertyMap model) {
        return Payload.super.redirect(url, model);
    }

    @Override
    public String toJson() {
        return Payload.super.toJson();
    }

    public static Payload newInstance(EndResult result) {
        return new PayloadImpl(result, new PropertyMap(), new PropertyMap());
    }

    @Override
    public EndResults getResult() {
        return null;
    }

    @Override
    public void setResult(EndResults result) {

    }

    public PropertyMap getData() {
        return this.data;
    }

    public PropertyMap getInfo() {
        return this.info;
    }

    public PropertyMap getViewData() {
        return this.viewData;
    }

    public void setResult(EndResult result) {
        this.result = result;
    }

    public void setData(PropertyMap data) {
        this.data = data;
    }

    public void setInfo(PropertyMap info) {
        this.info = info;
    }

    public void setViewData(PropertyMap viewData) {
        this.viewData = viewData;
    }

    @Override
    public void addData(String name, Object value) {
        Payload.super.addData(name, value);
    }

    @Override
    public void addData(Map<String, Object> data) {
        Payload.super.addData(data);
    }

    @Override
    public Object getData(String name) {
        return Payload.super.getData(name);
    }

    @Override
    public void addInfo(String name, Object value) {
        Payload.super.addInfo(name, value);
    }

    @Override
    public void addInfo(Map<String, Object> info) {
        Payload.super.addInfo(info);
    }

    @Override
    public Object getInfo(String name) {
        return Payload.super.getInfo(name);
    }
}
