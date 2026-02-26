package com.github.lisuiheng.astra.common.core.utils;

import com.github.lisuiheng.astra.common.core.entity.ITreeEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * 树形结构构建工具类
 * 
 * @author 
 */
public class TreeBuildUtils {

    /**
     * 构建树形结构
     *
     * @param list      所有节点列表
     * @param rootId    根节点ID
     * @param converter 转换器，将原对象转换为树节点对象
     * @param <T>       原始对象类型
     * @param <R>       树节点类型
     * @return 树形结构列表
     */
    public static <T, R> List<R> build(List<T> list, Object rootId, BiFunction<T, List<R>, R> converter) {
        List<R> result = new ArrayList<>();
        
        // 查找根节点
        for (T item : list) {
            if (safeEquals(getRootId(item), rootId)) {
                // 构建当前节点及其子树
                R node = buildNode(item, list, converter);
                result.add(node);
            }
        }
        
        return result;
    }

    /**
     * 构建单个节点及其子树
     */
    private static <T, R> R buildNode(T item, List<T> list, BiFunction<T, List<R>, R> converter) {
        List<R> children = new ArrayList<>();
        
        // 查找当前节点的子节点
        for (T child : list) {
            if (safeEquals(getParentId(child), getId(item))) {
                R childNode = buildNode(child, list, converter);
                children.add(childNode);
            }
        }
        
        // 使用转换器构造节点，传入原始对象和子节点列表
        return converter.apply(item, children);
    }

    /**
     * 获取节点ID，这里假设实体类中有getId()方法
     * 需要根据实际情况调整
     */
    private static <T> Object getId(T item) {
        if (item instanceof ITreeEntity) {
            return ((ITreeEntity) item).getId();
        }
        // 对于没有实现ITreeEntity的类型，暂时返回null，实际应用中需要根据具体类型处理
        return null;
    }

    /**
     * 获取父节点ID，这里假设实体类中有getParentId()方法
     * 需要根据实际情况调整
     */
    private static <T> Object getParentId(T item) {
        if (item instanceof ITreeEntity) {
            return ((ITreeEntity) item).getParentId();
        }
        // 对于没有实现ITreeEntity的类型，暂时返回null，实际应用中需要根据具体类型处理
        return null;
    }

    /**
     * 获取根节点ID，这里假设实体类中有getParentId()方法
     * 需要根据实际情况调整
     */
    private static <T> Object getRootId(T item) {
        if (item instanceof ITreeEntity) {
            return ((ITreeEntity) item).getParentId();
        }
        // 对于没有实现ITreeEntity的类型，暂时返回null，实际应用中需要根据具体类型处理
        return null;
    }
    
    /**
     * 安全的相等比较，处理null和类型转换
     */
    private static boolean safeEquals(Object obj1, Object obj2) {
        if (obj1 == null && obj2 == null) {
            return true;
        }
        if (obj1 == null || obj2 == null) {
            return false;
        }
        // 如果两个对象类型不同但都是数字类型，尝试进行类型转换比较
        if (obj1.getClass() != obj2.getClass()) {
            // 尝试转换为Long进行比较
            try {
                Long long1 = obj1 instanceof Long ? (Long) obj1 : 
                            obj1 instanceof Integer ? ((Integer) obj1).longValue() :
                            obj1 instanceof String ? Long.valueOf((String) obj1) : null;
                Long long2 = obj2 instanceof Long ? (Long) obj2 : 
                            obj2 instanceof Integer ? ((Integer) obj2).longValue() :
                            obj2 instanceof String ? Long.valueOf((String) obj2) : null;
                
                if (long1 != null && long2 != null) {
                    return long1.equals(long2);
                }
            } catch (NumberFormatException e) {
                // 如果转换失败，继续使用原始对象比较
            }
        }
        return obj1.equals(obj2);
    }
}