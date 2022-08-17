package com.mini.timecapsule.utils.bind;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping(
        method = {RequestMethod.POST}
)
public @interface PostRestMapping {
    @AliasFor(
            annotation = RequestMapping.class
    )
    String name() default "";

    @AliasFor(
            annotation = RequestMapping.class
    )
    String[] value() default {};

    @AliasFor(
            annotation = RequestMapping.class
    )
    String[] path() default {};

    @AliasFor(
            annotation = RequestMapping.class
    )
    String[] params() default {};

    @AliasFor(
            annotation = RequestMapping.class
    )
    String[] headers() default {};

    @AliasFor(
            annotation = RequestMapping.class
    )
    String[] consumes() default {"application/json;charset=UTF-8"};

    @AliasFor(
            annotation = RequestMapping.class
    )
    String[] produces() default {"application/json;charset=UTF-8"};
}
