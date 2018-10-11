package com.zhaozhou.frame.property.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by zhaozhou on 2018/8/2.
 */
public class ClassTransUtil {

    /**
     * 判断一个类是否为基本数据类型。
     * @param clazz 要判断的类。
     * @return true 表示为基本数据类型。
     */
    public static boolean isBaseType(Class clazz) {
        return
                (
                                clazz.equals(String.class) ||
                                clazz.equals(Integer.class) ||
                                clazz.equals(Byte.class) ||
                                clazz.equals(Long.class) ||
                                clazz.equals(Double.class) ||
                                clazz.equals(Float.class) ||
                                clazz.equals(Character.class) ||
                                clazz.equals(Short.class) ||
                                clazz.equals(BigDecimal.class) ||
                                clazz.equals(BigInteger.class) ||
                                clazz.equals(Boolean.class) ||
                                clazz.equals(Date.class) ||
                                clazz.isPrimitive()
                );
    }

    public static  <T> T transToBaseType(Class<T> baseClass, String str){
        if(baseClass.equals(String.class)){
            return (T)str;
        }
        if(baseClass.equals(Integer.class)){
            return (T)Integer.valueOf(str);
        }
        if(baseClass.equals(Byte.class)){
            return (T)Byte.valueOf(str);
        }
        if(baseClass.equals(Long.class)){
            return (T)Long.valueOf(str);
        }
        if(baseClass.equals(Double.class)){
            return (T)Double.valueOf(str);
        }
        if(baseClass.equals(Float.class)){
            return (T)Float.valueOf(str);
        }
        /*if(baseClass.equals(Character.class)){
            return (T)str;
        }*/
        if(baseClass.equals(Short.class)){
            return (T)Short.valueOf(str);
        }
        if(baseClass.equals(BigDecimal.class)){
            return (T)BigDecimal.valueOf(Double.valueOf(str));
        }
        if(baseClass.equals(BigInteger.class)){
            return (T)BigInteger.valueOf(Long.valueOf(str));
        }
        if(baseClass.equals(Boolean.class)){
            return (T)Boolean.valueOf(str);
        }
        if(baseClass.equals(Date.class)){
            return (T)new Date(Long.valueOf(str));
        }

        return null;
    }
}
