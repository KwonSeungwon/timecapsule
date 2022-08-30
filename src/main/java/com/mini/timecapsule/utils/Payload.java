package com.mini.timecapsule.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Component
public interface Payload {

    public EndResult getResult();

    public void setResult(EndResult result);

    public PropertyMap getData();

    public void setData(PropertyMap data);

    public PropertyMap getInfo();

    public void setInfo(PropertyMap info);

    @JsonIgnore
    public PropertyMap getViewData();

    public void setViewData(PropertyMap viewData);

    public default void addData(String name, Object value) {
        getData().put(name, value);
    }

    public default void addData(Map<String, Object> data) {
        getData().putAll(data);
    }

    public default Object getData(String name) {
        return getData().get(name);
    }

    public default void addInfo(String name, Object value) {
        getInfo().put(name, value);
    }

    public default void addInfo(Map<String, Object> info) {
        getInfo().putAll(info);
    }

    public default Object getInfo(String name) {
        return getInfo().get(name);
    }

    public default void addViewData(String name, Object value) {
        getViewData().put(name, value);
    }

    public default void addViewData(Map<String, Object> data) {
        getViewData().putAll(data);
    }

    public default Object getViewData(String name) {
        return getViewData().get(name);
    }

    public ModelAndView toModelAndView(String viewName);

    public default ModelAndView toModelAndView() {
        return toModelAndView(null);
    }

    public default ModelAndView redirect(String url) {
        return redirect(url, null);
    }

    public default ModelAndView redirect(String url, PropertyMap model) {
        ModelAndView modelAndView = new ModelAndView(UStrings.concat("redirect:", url));

        if (model != null) {
            modelAndView.addAllObjects(model);
        }
        return modelAndView;
    }

    public default String toJson() {
        return UJsons.stringify(this);
    }

    default boolean matches(EndResult result) {
        return result != null && result.getCode() == getResult().getCode();
    }

    public static <HttpServletRichResponse> void asCreated(Payload payload, Object... paths) {
        payload.setResult(EndResults.CREATED);
//
//        HttpServletRichResponse response = InstanceContext.getContext().getHttpServletRichResponse();
//
//        if (response == null) {
//            return;
//        }
//        response.setStatus(HttpStatus.CREATED.value());
//        response.setHeader("location", response.getRequest().buildAbsoluteURL(paths));
    }

    public static void asUpdated(Payload payload) {
        payload.setResult(EndResults.UPDATED);
    }

    public static void asDeleted(Payload payload) {
        payload.setResult(EndResults.DELETED);
    }
}
