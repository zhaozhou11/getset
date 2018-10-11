package com.jd.uav.common.getset;


import com.zhaozhou.frame.property.annotation.PropertyCopy;
import com.zhaozhou.frame.property.factory.PropertyCopySolver;

/**
 * Created by zhaozhou on 2018/10/10.
 */
public class TestWithNoAnnotation {

    public static class DesBeanClass {
        @PropertyCopy(ignored = true)
        private String name;
        @PropertyCopy(transClass = IntegerToString.class, transMethod = "getString")
        private String age;
        @PropertyCopy(ignored = true)
        private String sex;


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public static void main(String[] args) {
            SrcBeanClass src = new SrcBeanClass();
            src.setName("name");
            src.setAge(12);
            src.setSex("男");

            try {
                com.jd.uav.common.getset.DesBeanClass des = (com.jd.uav.common.getset.DesBeanClass) PropertyCopySolver.copyJavaBean(src, new com.jd.uav.common.getset.DesBeanClass());
                des.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

        public static class SrcBeanClass {

            private String name;
            private Integer age;
            private String sex;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public Integer getAge() {
                return age;
            }

            public void setAge(Integer age) {
                this.age = age;
            }

            public String getSex() {
                return sex;
            }

            public void setSex(String sex) {
                this.sex = sex;
            }
        }

    }
