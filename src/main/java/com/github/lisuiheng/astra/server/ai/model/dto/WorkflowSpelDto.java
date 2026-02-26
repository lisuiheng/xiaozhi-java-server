package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;
import java.util.Date;

/**
 * 工作流spel表达式定义DTO
 */
@Data
public class WorkflowSpelDto {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 组件名称
     */
    private String componentName;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 参数
     */
    private String methodParams;

    /**
     * 预览spel值
     */
    private String viewSpel;

    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;
}