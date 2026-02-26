package com.github.lisuiheng.astra.common.utils.reflect;

import cn.hutool.core.util.ReflectUtil;
import com.github.lisuiheng.astra.common.utils.StringUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 反射工具类. 提供调用getter/setter方法, 访问私有变量, 调用私有方法, 获取泛型类型Class, 被AOP过的真实类等工具函数.
 *
 * @author Lion Li
 */
@SuppressWarnings("rawtypes")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReflectUtils extends ReflectUtil {

    private static final String SETTER_PREFIX = "set";

    private static final String GETTER_PREFIX = "get";

    /**
     * 调用Getter方法.
     * 支持多级，如：对象名.对象名.方法
     */
    @SuppressWarnings("unchecked")
    public static <E> E invokeGetter(Object obj, String propertyName) {
        Object object = obj;
        for (String name : StringUtils.split(propertyName, ".")) {
            String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(name);
            object = invoke(object, getterMethodName);
        }
        return (E) object;
    }

    /**
     * 调用Setter方法, 仅匹配方法名。
     * 支持多级，如：对象名.对象名.方法
     */
    public static <E> void invokeSetter(Object obj, String propertyName, E value) {
        Object object = obj;
        String[] names = StringUtils.split(propertyName, ".");
        for (int i = 0; i < names.length; i++) {
            if (i < names.length - 1) {
                String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(names[i]);
                object = invoke(object, getterMethodName);
            } else {
                String setterMethodName = SETTER_PREFIX + StringUtils.capitalize(names[i]);
                Method method = getMethodByName(object.getClass(), setterMethodName);
                invoke(object, method, value);
            }
        }
    }

    /**
     * 实例化对象
     *
     * @param klass 要实例化的类
     * @param args 构造函数参数
     * @param <T> 泛型类型
     * @return 实例化后的对象
     */
    public static <T> T newInstance(Class<T> klass, Object... args) {
        return ReflectUtil.newInstance(klass, args);
    }

    /**
     * 获取字段值
     *
     * @param obj       对象
     * @param fieldName 字段名
     * @return 字段值
     */
    public static Object getFieldValue(Object obj, String fieldName) {
        return ReflectUtil.getFieldValue(obj, fieldName);
    }

    /**
     * 获取字段
     *
     * @param clazz     类
     * @param fieldName 字段名
     * @return 字段
     */
    public static Field getField(Class<?> clazz, String fieldName) {
        return ReflectUtil.getField(clazz, fieldName);
    }

    /**
     * 获取静态字段值
     *
     * @param field 字段
     * @return 静态字段值
     */
    public static Object getStaticFieldValue(Field field) {
        return ReflectUtil.getStaticFieldValue(field);
    }
}