package com.jd.uav.common.getset;

/**
 * Created by zhaozhou on 2018/7/11.
 */
public class SrcBeanClass {

    private String name;
    private Integer age;
    private String sex;

    public String getName() {
        return name;
    }

    public SrcBeanClass setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getAge() {
        return age;
    }

    public SrcBeanClass setAge(Integer age) {
        this.age = age;
        return this;
    }

    public String getSex() {
        return sex;
    }

    public SrcBeanClass setSex(String sex) {
        this.sex = sex;
        return this;
    }
}
