package com.github.lisuiheng.astra.sys.service;

import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.sys.domain.bo.FlowTaskBo;
import com.github.lisuiheng.astra.sys.domain.vo.FlowHisTaskVo;
import com.github.lisuiheng.astra.sys.domain.vo.FlowTaskVo;

/**
 * 工作流任务服务接口
 *
 * @author
 */
public interface ISysWorkflowTaskService {

    /**
     * 查询当前用户的待办任务
     *
     * @param flowTaskBo 参数
     * @param pageQuery  分页
     * @return 结果
     */
    TableDataInfo<FlowTaskVo> pageByTaskWait(FlowTaskBo flowTaskBo, PageQuery pageQuery);

    /**
     * 查询当前用户的已办任务
     *
     * @param flowTaskBo 参数
     * @param pageQuery  分页
     * @return 结果
     */
    TableDataInfo<FlowHisTaskVo> pageByTaskFinish(FlowTaskBo flowTaskBo, PageQuery pageQuery);

    /**
     * 查询当前租户所有待办任务
     *
     * @param flowTaskBo 参数
     * @param pageQuery  分页
     * @return 结果
     */
    TableDataInfo<FlowTaskVo> pageByAllTaskWait(FlowTaskBo flowTaskBo, PageQuery pageQuery);

    /**
     * 查询已办任务
     *
     * @param flowTaskBo 参数
     * @param pageQuery  分页
     * @return 结果
     */
    TableDataInfo<FlowHisTaskVo> pageByAllTaskFinish(FlowTaskBo flowTaskBo, PageQuery pageQuery);

    /**
     * 查询当前用户的抄送
     *
     * @param flowTaskBo 参数
     * @param pageQuery  分页
     * @return 结果
     */
    TableDataInfo<FlowTaskVo> pageByTaskCopy(FlowTaskBo flowTaskBo, PageQuery pageQuery);
}