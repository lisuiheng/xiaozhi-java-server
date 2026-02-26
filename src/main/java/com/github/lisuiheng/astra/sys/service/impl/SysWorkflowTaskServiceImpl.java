package com.github.lisuiheng.astra.sys.service.impl;

import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.lisuiheng.astra.sys.domain.bo.FlowTaskBo;
import com.github.lisuiheng.astra.sys.domain.vo.FlowHisTaskVo;
import com.github.lisuiheng.astra.sys.domain.vo.FlowTaskVo;
import com.github.lisuiheng.astra.sys.service.ISysWorkflowTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 工作流任务服务实现类
 *
 * @author
 */
@Service
@RequiredArgsConstructor
public class SysWorkflowTaskServiceImpl implements ISysWorkflowTaskService {

    @Override
    public TableDataInfo<FlowTaskVo> pageByTaskWait(FlowTaskBo flowTaskBo, PageQuery pageQuery) {
        // TODO: 实现查询当前用户的待办任务逻辑
        List<FlowTaskVo> list = new ArrayList<>();
        Page<FlowTaskVo> page = new Page<>();
        page.setRecords(list);
        page.setCurrent(pageQuery.getPageNum());
        page.setSize(pageQuery.getPageSize());
        page.setTotal(0);
        return TableDataInfo.build(page);
    }

    @Override
    public TableDataInfo<FlowHisTaskVo> pageByTaskFinish(FlowTaskBo flowTaskBo, PageQuery pageQuery) {
        // TODO: 实现查询当前用户的已办任务逻辑
        List<FlowHisTaskVo> list = new ArrayList<>();
        Page<FlowHisTaskVo> page = new Page<>();
        page.setRecords(list);
        page.setCurrent(pageQuery.getPageNum());
        page.setSize(pageQuery.getPageSize());
        page.setTotal(0);
        return TableDataInfo.build(page);
    }

    @Override
    public TableDataInfo<FlowTaskVo> pageByAllTaskWait(FlowTaskBo flowTaskBo, PageQuery pageQuery) {
        // TODO: 实现查询当前租户所有待办任务逻辑
        List<FlowTaskVo> list = new ArrayList<>();
        Page<FlowTaskVo> page = new Page<>();
        page.setRecords(list);
        page.setCurrent(pageQuery.getPageNum());
        page.setSize(pageQuery.getPageSize());
        page.setTotal(0);
        return TableDataInfo.build(page);
    }

    @Override
    public TableDataInfo<FlowHisTaskVo> pageByAllTaskFinish(FlowTaskBo flowTaskBo, PageQuery pageQuery) {
        // TODO: 实现查询已办任务逻辑
        List<FlowHisTaskVo> list = new ArrayList<>();
        Page<FlowHisTaskVo> page = new Page<>();
        page.setRecords(list);
        page.setCurrent(pageQuery.getPageNum());
        page.setSize(pageQuery.getPageSize());
        page.setTotal(0);
        return TableDataInfo.build(page);
    }

    @Override
    public TableDataInfo<FlowTaskVo> pageByTaskCopy(FlowTaskBo flowTaskBo, PageQuery pageQuery) {
        // TODO: 实现查询当前用户的抄送逻辑
        List<FlowTaskVo> list = new ArrayList<>();
        Page<FlowTaskVo> page = new Page<>();
        page.setRecords(list);
        page.setCurrent(pageQuery.getPageNum());
        page.setSize(pageQuery.getPageSize());
        page.setTotal(0);
        return TableDataInfo.build(page);
    }
}