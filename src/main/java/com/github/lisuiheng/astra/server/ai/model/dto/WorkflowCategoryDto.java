package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;
import java.util.Date;

/**
 * 工作流分类DTO
 */
@Data
public class WorkflowCategoryDto {

    /**
     * 流程分类ID
     */
    private Long categoryId;

    /**
     * 租户编号
     */
    private String tenantId;

    /**
     * 父级分类id
     */
    private Long parentId;

    /**
     * 父级分类名称
     */
    private String parentName;

    /**
     * 祖级列表
     */
    private String ancestors;

    /**
     * 流程分类名称
     */
    private String categoryName;

    /**
     * 显示顺序
     */
    private Integer orderNum;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    private String delFlag;

    /**
     * 创建部门
     */
    private Long createDept;

    /**
     * 创建者
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新者
     */
    private Long updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;
}