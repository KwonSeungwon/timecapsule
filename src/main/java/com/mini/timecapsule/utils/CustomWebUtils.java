package com.mini.timecapsule.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import java.util.Map;

public interface CustomWebUtils {

    public interface Payload {

//        public EndResult getResult();
//
//        public void setResult(EndResult result);

        public PropertyMap getData();

        public void setData(PropertyMap data);

        public PropertyMap getInfo();

        public void setInfo(PropertyMap info);

        @JsonIgnore
        public PropertyMap getViewData();

        public void setViewData(PropertyMap viewData);

        default public void addData(String name, Object value) {
            getData().put(name, value);
        }

        default public void addData(Map<String, Object> data) {
            getData().putAll(data);
        }

        default public Object getData(String name) {
            return getData().get(name);
        }

        default public void addInfo(String name, Object value) {
            getInfo().put(name, value);
        }

        default public void addInfo(Map<String, Object> info) {
            getInfo().putAll(info);
        }

        default public Object getInfo(String name) {
            return getInfo().get(name);
        }

        default public void addViewData(String name, Object value) {
            getViewData().put(name, value);
        }

        default public void addViewData(Map<String, Object> data) {
            getViewData().putAll(data);
        }

        default public Object getViewData(String name) {
            return getViewData().get(name);
        }

        public ModelAndView toModelAndView(String viewName);

        default public ModelAndView toModelAndView() {
            return toModelAndView(null);
        }

        default public ModelAndView redirect(String url) {
            return redirect(url, null);
        }

        default public ModelAndView redirect(String url, PropertyMap model) {
            ModelAndView modelAndView = new ModelAndView(UStrings.concat("redirect:", url));

            if (model != null) {
                modelAndView.addAllObjects(model);
            }
            return modelAndView;
        }

//        default public String toJson() {
//            return UJsons.stringify(this);
//        }
//
//        default boolean matches(EndResult result) {
//            return result != null && result.getCode() == getResult().getCode();
//        }
//
//        public static void asCreated(Payload payload, Object... paths) {
//            payload.setResult(EndResults.CREATED);
//
//            HttpServletRichResponse response = InstanceContext.getContext().getHttpServletRichResponse();
//
//            if (response == null) {
//                return;
//            }
//            response.setStatus(HttpStatus.CREATED.value());
//            response.setHeader("location", response.getRequest().buildAbsoluteURL(paths));
//        }
//
//        public static void asUpdated(Payload payload) {
//            payload.setResult(EndResults.UPDATED);
//        }
//
//        public static void asDeleted(Payload payload) {
//            payload.setResult(EndResults.DELETED);
//        }
//    }

    }
    public class PayloadImpl implements Payload {
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

        public static Payload newInstance(EndResult result) {
            return new PayloadImpl(result, new PropertyMap(), new PropertyMap());
        }

        public EndResult getResult() {
            return this.result;
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


    }
    public class EndResult extends UObject {
        private int code;
        private String message;

        protected EndResult(int code, String message) {
            this.code = code;
            this.message = message;
        }

        @JsonIgnore
        public int getStatus() {
            return this.code / 1000;
        }

        public static EndResult of(int code, String message) {
            return new EndResult(code, message);
        }

        public int getCode() {
            return this.code;
        }

        public String getMessage() {
            return this.message;
        }
    }

    public abstract class UObject {
        public UObject() {
           }

        public String toString() {
            return UObjects.toString(this);
        }
    }
    public abstract class UObjects {
        public UObjects() {
        }

        public static boolean isEqual(Object value1, Object value2) {
            return value1 != null && value2 != null && value1.equals(value2);
        }

        public static String toString(Object object) {
            return ToStringBuilder.reflectionToString(object, ToStringStyle.MULTI_LINE_STYLE);
        }
    }


}
