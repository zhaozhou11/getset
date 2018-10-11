package com.zhaozhou.frame.property.factory;

import com.google.common.base.Strings;

import com.zhaozhou.frame.property.annotation.PropertyCopy;
import com.zhaozhou.frame.property.util.ClassTransUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhaozhou on 2018/7/11.
 *
 */
public class PropertyCopySolver {

    private static Logger logger = LoggerFactory.getLogger(PropertyCopySolver.class);


    /**
     * bean对应字段copy
     * @param srcBean 源bean
     * @param desBean 目标bean
     * @return 对目标bean进行设值之后的bean
     * @throws IllegalArgumentException
     * @throws Exception
     */
    public static Object copyJavaBean(Object srcBean, Object desBean) throws IllegalArgumentException, Exception{
            Class c = desBean.getClass();
            List<AnnotationParam> params = new LinkedList<AnnotationParam>();

            //获取所有属性字段
            Field[] fields = c.getDeclaredFields();
            for (Field f: fields){
                //属性上没有GetSet注解，则直接使用srcBean的参数？
                AnnotationParam param = new AnnotationParam();
                param.setDesFieldName(f.getName());
                if(f.isAnnotationPresent(PropertyCopy.class)){
                    //获取注解信息
                    PropertyCopy annotation = (PropertyCopy)f.getAnnotation(PropertyCopy.class);
                    if(annotation.ignored()){
                        continue;
                    }
                    //注解有属性名，则设置属性名
                    if(!Strings.isNullOrEmpty(annotation.name())){
                        param.setSrcFieldName(annotation.name());
                    }
                    param.setTransClass(annotation.transClass());
                    param.setTransMethod(annotation.transMethod());
                    param.setIgnore(annotation.ignored());
                }

                if(Strings.isNullOrEmpty(param.getSrcFieldName())){
                    param.setSrcFieldName(param.getDesFieldName());
                }

                params.add(param);
            }
            return copyBean(srcBean, desBean, params);
    }


    /**
     * 根据配置，将srcBean中的属性转换赋值给destBean中对应的属性
     * @param srcBean
     * @param desBean
     * @param params
     * @return
     */
    private static Object copyBean(Object srcBean, Object desBean, List<AnnotationParam> params) throws Exception{

        if (CollectionUtils.isEmpty(params)) {
            return desBean;
        }

        for (AnnotationParam param : params) {
            try {
                Object value = getValueFromBean(srcBean, param.getSrcFieldName());

                //字段忽略或为空？
                if (param.isIgnore() || value == null) {
                    continue;
                }

                //需要转换的类？
                if (param.getTransClass() != null && !param.getTransClass().equals(Object.class) && !Strings.isNullOrEmpty(param.getTransMethod())) {

                    //示例化转换类并查找对应的转换方法
                    Object transClass = BeanUtils.instantiateClass(param.getTransClass());
                    Method transMethod = BeanUtils.findMethod(transClass.getClass(), param.getTransMethod(), value.getClass());
                    if (transMethod == null) {
                        throw new NoSuchMethodException("can not find " + param.getTransClass().getSimpleName() + "." + param.getTransMethod());
                    }
                    //根据转换方法进行转换
                    value = transMethod.invoke(transClass, value);
                }

                //原属性和目标属性类型不同?
                Field destField = desBean.getClass().getDeclaredField(param.desFieldName);
                if (!destField.getType().equals(value.getClass())) {
                    //目标属性不为基础类型？
                    if (!ClassTransUtil.isBaseType(destField.getType())) {
                        throw new ClassCastException("can not translate from " + value.getClass().getName() + " to " + destField.getType().getName());
                    }
                    //基础类型？则进行转换
                    value = transPrimitiveOrWrapper(value, destField.getType());
                    if (value == null) {
                        throw new ClassCastException("can not translate from " + value.getClass().getName() + " to " + destField.getType().getName());
                    }
                }

                boolean ret = setValueOfBean(desBean, param.getDesFieldName(), value);

            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("@@exception:{}", e.getMessage());
            }

        }

        return desBean;
    }


    /**
     * 获取bean中对应字段的值
     * @param bean bean
     * @param property 属性名称
     * @return 属性值
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public static Object getValueFromBean(Object bean, String property)
            throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException{

       Method readMethod = findReaderMethod(bean, property);
       if(readMethod == null){
           logger.warn("@@can not find read method, property:{}", property);
           return false;
       }

        return readMethod.invoke(bean,new Object[0]);
    }


    /**
     * 设置bean的属性值
     * @param bean bean
     * @param property 属性名称
     * @param value 属性值
     * @return true：设置成功；false设置失败
     * @throws IntrospectionException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public static boolean setValueOfBean(Object bean, String property,Object value)
            throws IntrospectionException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {


        //获取写属性方法
        Method writeMethod = findWritterMethod(bean, property);
        if(writeMethod == null){
            logger.warn("@@can not find write method, property:{}", property);
            return false;
        }

        writeMethod.invoke(bean, value);
        return true;
    }


    /**
     * 获取bean的writter方法
     * @param bean bean
     * @param fieldName 属性名
     * @return
     */
    public static Method findWritterMethod(Object bean, String fieldName){

        //查找属性描述器
        PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(bean.getClass(), fieldName);
        if (descriptor == null) {
            logger.warn("@@can not find property descriptor, property:{}", fieldName);
            return null;
        }

        Method writeMethod = descriptor.getWriteMethod();
        if(writeMethod != null){
            return writeMethod;
        }

        Method[] methods = bean.getClass().getMethods();
        for (Method m: methods){
            String methodName = null;
            if(m.getName().startsWith("set")){
                methodName = "set" + fieldName;
            }

            if(m.getName().startsWith("is")){
                methodName = "is" + fieldName;
            }

            if(methodName.equalsIgnoreCase(m.getName())){
                return m;
            }
        }
        return null;
    }


    /**
     * 查找bean的reader方法
     * @param bean bean
     * @param fieldName 属性名称
     * @return
     */
    public static Method findReaderMethod(Object bean, String fieldName){

        //查找属性描述器
        PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(bean.getClass(), fieldName);
        if (descriptor == null) {
            logger.warn("@@can not find property descriptor, property:{}", fieldName);
            return null;
        }

        Method readMethod = descriptor.getReadMethod();
        if(readMethod != null){
            return readMethod;
        }

        Method[] methods = bean.getClass().getMethods();
        for (Method m: methods){
            if(m.getName().startsWith("set")){
                String methodName = "set" + fieldName;
                if(methodName.equalsIgnoreCase(m.getName())){
                    return m;
                }
            }
        }
        return null;
    }






    /**
     * 对基本类型进行转换
     * @param src
     * @param desClass
     * @param <T>
     * @return
     */
    private static <T> Object transPrimitiveOrWrapper(Object src, Class<T> desClass){
        if(src.getClass().equals(desClass.getClass())){
            return src;
        }
        String str = src.toString();

        if(ClassTransUtil.isBaseType(desClass)){
            return ClassTransUtil.transToBaseType(desClass, str);
        }
        return null;
    }



    private static class AnnotationParam{
        private String desFieldName;
        private String srcFieldName;
        private boolean ignore;
        private Class  transClass;
        private String transMethod;

        public String getDesFieldName() {
            return desFieldName;
        }

        public AnnotationParam setDesFieldName(String desFieldName) {
            this.desFieldName = desFieldName;
            return this;
        }

        public String getSrcFieldName() {
            return srcFieldName;
        }

        public AnnotationParam setSrcFieldName(String srcFieldName) {
            this.srcFieldName = srcFieldName;
            return this;
        }


        public boolean isIgnore() {
            return ignore;
        }

        public AnnotationParam setIgnore(boolean ignore) {
            this.ignore = ignore;
            return this;
        }

        public Class getTransClass() {
            return transClass;
        }

        public AnnotationParam setTransClass(Class transClass) {
            this.transClass = transClass;
            return this;
        }

        public String getTransMethod() {
            return transMethod;
        }

        public AnnotationParam setTransMethod(String transMethod) {
            this.transMethod = transMethod;
            return this;
        }
    }
}
