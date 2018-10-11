package com.jd.uav.common.getset;


import com.zhaozhou.frame.property.annotation.PropertyCopy;
import com.zhaozhou.frame.property.factory.PropertyCopySolver;

/**
 * Created by zhaozhou on 2018/7/11.
 */
public class DesBeanClass {
    @PropertyCopy(name = "sex")
    private String name;
    @PropertyCopy(name = "age", transClass = java.lang.String.class, transMethod = "valueOf")
    private String age;

    private String sex;


    public String getName() {
        return name;
    }

    public DesBeanClass setName(String name) {
        this.name = name;
        return this;
    }

    public String getAge() {
        return age;
    }

    public DesBeanClass setAge(String age) {
        this.age = age;
        return this;
    }

    public String getSex() {
        return sex;
    }

    public DesBeanClass setSex(String sex) {
        this.sex = sex;
        return this;
    }

    public static void main(String[] args){
        SrcBeanClass src = new SrcBeanClass();
        src.setName("name").setAge(12).setSex("男");

        //Object o = GetSetFactory.getValueFromBean(src,"age");

        try {
            DesBeanClass des = (DesBeanClass)PropertyCopySolver.copyJavaBean(src, new DesBeanClass());
            String age = des.getAge();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
