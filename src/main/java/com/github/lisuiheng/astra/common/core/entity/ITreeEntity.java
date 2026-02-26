package com.github.lisuiheng.astra.common.core.entity;

/**
 * 树形实体接口
 * 
 * @author 
 */
public interface ITreeEntity {
    /**
     * 获取ID
     */
    Object getId();

    /**
     * 获取父ID
     */
    Object getParentId();
}