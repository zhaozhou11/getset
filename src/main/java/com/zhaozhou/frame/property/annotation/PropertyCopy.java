package com.zhaozhou.frame.property.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by zhaozhou on 2018/7/11.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface PropertyCopy {

    /**
     * 原bean中对应的的字段名称
     * @return
     */
    public String name() default "";


    /**
     * 是否忽略目标bean的此属性：true:忽略此属性，此时不对本属性进行赋值操作
     * @return
     */
    public boolean ignored() default false;


    /**
     * 对原bean对应的字段进行转换的类
     * @return
     */
    public Class transClass() default Object.class;

    /**
     * 转换类对应的方法
     * @return
     */
    public String transMethod() default "";


}
