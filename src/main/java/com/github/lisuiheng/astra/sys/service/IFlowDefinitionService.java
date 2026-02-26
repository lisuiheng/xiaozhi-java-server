package com.github.lisuiheng.astra.sys.service;

import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.sys.dto.FlowDefinitionDto;
import com.github.lisuiheng.astra.sys.dto.FlowDefinitionVo;

/**
 * 流程定义服务接口
 *
 * @author xiaozhi
 */
public interface IFlowDefinitionService {

    /**
     * 查询流程定义列表
     *
     * @param flowDefinition 查询参数
     * @param pageQuery      分页参数
     * @return 流程定义分页列表
     */
    TableDataInfo<FlowDefinitionVo> queryList(FlowDefinitionDto flowDefinition, PageQuery pageQuery);

    /**
     * 查询未发布的流程定义列表
     *
     * @param flowDefinition 查询参数
     * @param pageQuery      分页参数
     * @return 未发布的流程定义分页列表
     */
    TableDataInfo<FlowDefinitionVo> unPublishList(FlowDefinitionDto flowDefinition, PageQuery pageQuery);
}