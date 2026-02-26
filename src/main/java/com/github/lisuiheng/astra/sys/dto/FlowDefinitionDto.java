package com.github.lisuiheng.astra.sys.dto;

import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 流程定义数据传输对象
 *
 * @author xiaozhi
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FlowDefinitionDto extends PageQuery {

    private static final long serialVersionUID = 1L;

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
     * 是否发布（0未发布 1已发布 9失效）
     */
    private Integer isPublish;
}