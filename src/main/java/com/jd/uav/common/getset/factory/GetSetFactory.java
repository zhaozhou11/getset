package com.jd.uav.common.getset.factory;

import com.google.common.base.Strings;
import com.jd.uav.common.getset.annotation.GetSet;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by zhaozhou on 2018/7/11.
 *
 */
public class GetSetFactory {

    private static Logger logger = Logger.getLogger(GetSetFactory.class.getName());


    public static Object getSetResolver(Object srcBean, Object desBean){
        try {
            Class c = desBean.getClass();
            //类上有GetSet注解？
            boolean classExist = c.isAnnotationPresent(GetSet.class);
            boolean fieldExit = false;

            //获取所有属性字段
            Field[] fields = c.getDeclaredFields();
            for (Field f: fields){
                //属性上有GetSet注解？
                fieldExit = false;
                if(f.isAnnotationPresent(GetSet.class)){
                    fieldExit = true;
                    //获取注解信息
                    GetSet annotation = (GetSet)f.getAnnotation(GetSet.class);
                    //获取srcBean中属性为annotation.name()的值
                    if(!Strings.isNullOrEmpty(annotation.name())){
                        //注解配置了处理的类及方法？
                        if(!annotation.resolverClass().getSimpleName().equals(Object.class.getSimpleName()) && !Strings.isNullOrEmpty(annotation.resolverMethod())){
                            try {
                                Object val = getValueFromBean(srcBean,annotation.name());
                                    //获取处理方法
                                /*annotation.resolverClass()*/
                                    Object resolverClass = annotation.resolverClass().newInstance();
                                    Method m = annotation.resolverClass().getDeclaredMethod(annotation.resolverMethod(),val.getClass());
                                    if(m == null){
                                        logger.log(Level.WARNING, "@@@@找不到方法：" + annotation.resolverClass() + "." + annotation.resolverMethod() + "@@@@");
                                    }else{
                                        //对数据进行处理，获取返回值
                                        Object retVal = m.invoke(resolverClass, val);
                                        boolean ret = setValueOfBean(desBean, f.getName(), retVal);
                                        if (!ret) {
                                            logger.log(Level.WARNING, "@@@@设置属性值失败！@@@@");
                                        }
                                    }
                            }catch (Exception e){
                                e.printStackTrace();
                                logger.log(Level.WARNING, "@@@@异常，e=" + e.getMessage() + "@@@@");
                            }
                        }else{
                            Object val = getValueFromBean(srcBean,annotation.name());
                            boolean ret = setValueOfBean(desBean, f.getName(),val);
                            if(!ret){
                                logger.log(Level.WARNING, "@@@@设置属性值失败！@@@@");
                            }
                        }
                        continue;
                    }
                }

                //类上有注解？
                if(classExist || fieldExit){
                    Object val = getValueFromBean(srcBean,f.getName());
                    boolean ret = setValueOfBean(desBean, f.getName(),val);
                    if(!ret){
                        logger.log(Level.WARNING, "@@@@设置属性值失败！@@@@");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.WARNING, "@@@@异常，e=" + e.getMessage() + "@@@@");
        }

        return desBean;
    }





    public static Object getValueFromBean(Object bean, String property){
        Object obj = null;
        try {
            //获取所有读写方法
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            //遍历属性
            for (int i = 0; i < propertyDescriptors.length; i++) {
                PropertyDescriptor descriptor = propertyDescriptors[i];
                String propertyName = descriptor.getName();
                if (!propertyName.equals("class") && propertyName.equalsIgnoreCase(property)) {
                    Method readMethod = descriptor.getReadMethod();
                    obj = readMethod.invoke(bean, new Object[0]);
                    return obj;
                }
            }
        }catch (Exception e){
            e.getMessage();
        }
        return obj;
    }


    /**
     * 设置bean的属性值
     * @param bean
     * @param property
     * @param value
     * @return
     * @throws IntrospectionException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public static boolean setValueOfBean(Object bean, String property,Object value)
            throws IntrospectionException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
            //获取所有读写方法
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            //遍历属性
            for (int i = 0; i < propertyDescriptors.length; i++) {
                PropertyDescriptor descriptor = propertyDescriptors[i];
                String propertyName = descriptor.getName();
                if (!propertyName.equals("class") && propertyName.equalsIgnoreCase(property)) {
                    Method writeMethod = descriptor.getWriteMethod();
                    if(writeMethod == null){
                        writeMethod =  findSetterMethod(bean, property);
                        if(writeMethod == null){
                            logger.log(Level.WARNING, "@@@@找不到setter方法@@@@");
                            return false;
                        }
                    }
                    writeMethod.invoke(bean,value);
                    return true;
                }
            }
            return false;
    }

    private static Method findSetterMethod(Object bean, String field){
        Method[] methods = bean.getClass().getMethods();
        for (Method m: methods){
            if(m.getName().startsWith("set")){
                String str = "set" + field;
                if(str.equalsIgnoreCase(m.getName())){
                    return m;
                }
            }
        }
        return null;
    }
}
