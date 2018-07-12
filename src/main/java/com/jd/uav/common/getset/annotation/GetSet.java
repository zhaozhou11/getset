package com.jd.uav.common.getset.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by zhaozhou on 2018/7/11.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface GetSet {

    /**
     * 要获取的字段名称
     * @return
     */
    public String name() default "";


    /**
     * 处理字段的类
     * @return
     */
    public Class resolverClass() default Object.class;

    /**
     * 处理字段的方法
     * @return
     */
    public String resolverMethod() default "";

}
