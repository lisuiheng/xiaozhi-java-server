package com.github.lisuiheng.astra.sys.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.lisuiheng.astra.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 流程定义实体类
 *
 * @author xiaozhi
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("flow_definition")
public class FlowDefinition extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId
    private Long id;

    /**
     * 流程定义编码
     */
    private String flowCode;

    /**
     * 流程定义名称
     */
    private String flowName;

    /**
     * 流程分类id
     */
    private String category;

    /**
     * 流程版本
     */
    private String version;

    /**
     * 是否发布（0未发布 1已发布 9失效）
     */
    private Integer isPublish;

    /**
     * 活跃状态（1活跃 0挂起）
     */
    private Integer activityStatus;

    /**
     * 表单路径
     */
    private String formPath;

    /**
     * 扩展信息
     */
    private String ext;

    /**
     * 自定义表单
     */
    private String formCustom;

    /**
     * 设计器模型（CLASSICS经典模型 MIMIC仿钉钉模型）
     */
    private String modelValue;
    
    /**
     * 删除标志（0代表存在 1代表删除）
     */
    private String delFlag;
}