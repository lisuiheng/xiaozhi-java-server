package com.github.lisuiheng.astra.sys.service;

import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.sys.dto.FlowInstanceDto;
import com.github.lisuiheng.astra.sys.dto.FlowInstanceVo;

import java.util.Map;

/**
 * 工作流实例服务接口
 *
 * @author xiaozhi
 */
public interface IFlowInstanceService {

    /**
     * 查询运行中的流程实例列表
     *
     * @param flowInstance 查询参数
     * @param pageQuery    分页参数
     * @return 流程实例分页列表
     */
    TableDataInfo<FlowInstanceVo> pageByRunning(FlowInstanceDto flowInstance, PageQuery pageQuery);

    /**
     * 查询已完成的流程实例列表
     *
     * @param flowInstance 查询参数
     * @param pageQuery    分页参数
     * @return 流程实例分页列表
     */
    TableDataInfo<FlowInstanceVo> pageByFinish(FlowInstanceDto flowInstance, PageQuery pageQuery);

    /**
     * 查询当前用户的流程实例列表
     *
     * @param flowInstance 查询参数
     * @param pageQuery    分页参数
     * @return 流程实例分页列表
     */
    TableDataInfo<FlowInstanceVo> pageByCurrent(FlowInstanceDto flowInstance, PageQuery pageQuery);

    /**
     * 获取流程变量
     *
     * @param instanceId 实例id
     * @return 流程变量
     */
    Map<String, Object> instanceVariable(Long instanceId);
}