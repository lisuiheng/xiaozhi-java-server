package com.github.lisuiheng.astra.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.sys.entity.WorkflowSpel;
import com.github.lisuiheng.astra.sys.mapper.WorkflowSpelMapper;
import com.github.lisuiheng.astra.sys.service.IWorkflowSpelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 工作流spel表达式定义服务实现类
 */
@Service
public class WorkflowSpelServiceImpl extends ServiceImpl<WorkflowSpelMapper, WorkflowSpel> implements IWorkflowSpelService {

    @Autowired
    private WorkflowSpelMapper workflowSpelMapper;

    @Override
    public TableDataInfo<WorkflowSpel> listWorkflows(WorkflowSpel workflowSpel, PageQuery pageQuery) {
        LambdaQueryWrapper<WorkflowSpel> lqw = buildQueryWrapper(workflowSpel);
        Page<WorkflowSpel> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        Page<WorkflowSpel> result = workflowSpelMapper.selectPage(page, lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public List<WorkflowSpel> listWorkflows(WorkflowSpel workflowSpel) {
        LambdaQueryWrapper<WorkflowSpel> lqw = buildQueryWrapper(workflowSpel);
        return workflowSpelMapper.selectList(lqw);
    }

    @Override
    public WorkflowSpel getById(Long id) {
        return workflowSpelMapper.selectById(id);
    }

    @Override
    public int insertWorkflowSpel(WorkflowSpel workflowSpel) {
        return workflowSpelMapper.insert(workflowSpel);
    }

    @Override
    public int updateWorkflowSpel(WorkflowSpel workflowSpel) {
        return workflowSpelMapper.updateById(workflowSpel);
    }

    @Override
    public int deleteWorkflowSpelById(Long id) {
        return workflowSpelMapper.deleteById(id);
    }

    private LambdaQueryWrapper<WorkflowSpel> buildQueryWrapper(WorkflowSpel workflowSpel) {
        LambdaQueryWrapper<WorkflowSpel> lqw = new LambdaQueryWrapper<>();
        lqw.eq(workflowSpel.getId() != null, WorkflowSpel::getId, workflowSpel.getId());
        lqw.eq(workflowSpel.getComponentName() != null, WorkflowSpel::getComponentName, workflowSpel.getComponentName());
        lqw.eq(workflowSpel.getMethodName() != null, WorkflowSpel::getMethodName, workflowSpel.getMethodName());
        lqw.eq(workflowSpel.getMethodParams() != null, WorkflowSpel::getMethodParams, workflowSpel.getMethodParams());
        lqw.eq(workflowSpel.getViewSpel() != null, WorkflowSpel::getViewSpel, workflowSpel.getViewSpel());
        lqw.eq(workflowSpel.getStatus() != null, WorkflowSpel::getStatus, workflowSpel.getStatus());
        lqw.like(workflowSpel.getRemark() != null, WorkflowSpel::getRemark, workflowSpel.getRemark());
        lqw.orderByAsc(WorkflowSpel::getId);
        return lqw;
    }
}