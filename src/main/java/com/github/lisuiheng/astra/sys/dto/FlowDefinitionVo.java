package com.github.lisuiheng.astra.sys.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 流程定义视图对象
 *
 * @author xiaozhi
 */
@Data
public class FlowDefinitionVo {

    private static final long serialVersionUID = 1L;

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
     * 流程分类名称
     */
    private String categoryName;

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
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}