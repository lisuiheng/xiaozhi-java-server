package com.github.lisuiheng.astra.sys.dto;

import lombok.Data;

/**
 * 工作流实例查询参数
 *
 * @author xiaozhi
 */
@Data
public class FlowInstanceDto {

    /**
     * 流程分类
     */
    private String category;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 流程定义编码
     */
    private String flowCode;

    /**
     * 流程定义名称
     */
    private String flowName;

    /**
     * 创建者ids
     */
    private String[] createByIds;

    /**
     * 业务id
     */
    private String businessId;
}