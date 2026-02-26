package com.github.lisuiheng.astra.sys.dto;

import lombok.Data;
import java.util.Date;

/**
 * 工作流实例视图对象
 *
 * @author xiaozhi
 */
@Data
public class FlowInstanceVo {

    private Long id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 对应flow_definition表的id
     */
    private Long definitionId;

    /**
     * 流程定义名称
     */
    private String flowName;

    /**
     * 流程定义编码
     */
    private String flowCode;

    /**
     * 版本
     */
    private String version;

    /**
     * 业务id
     */
    private String businessId;

    /**
     * 流程激活状态（0挂起 1激活）
     */
    private Integer activityStatus;

    /**
     * 流程状态（0待提交 1审批中 2 审批通过 3自动通过 8已完成 9已退回 10失效）
     */
    private String flowStatus;

    /**
     * 流程状态名称
     */
    private String flowStatusName;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 流程分类
     */
    private String category;

    /**
     * 流程分类名称
     */
    private String categoryName;

    /**
     * 业务编码
     */
    private String businessCode;

    /**
     * 业务标题
     */
    private String businessTitle;
}