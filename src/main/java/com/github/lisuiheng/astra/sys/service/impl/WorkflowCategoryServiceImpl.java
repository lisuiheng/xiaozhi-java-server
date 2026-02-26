package com.github.lisuiheng.astra.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.lisuiheng.astra.sys.entity.WorkflowCategory;
import com.github.lisuiheng.astra.sys.mapper.WorkflowCategoryMapper;
import com.github.lisuiheng.astra.sys.service.IWorkflowCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 工作流分类服务实现类
 */
@Service
public class WorkflowCategoryServiceImpl extends ServiceImpl<WorkflowCategoryMapper, WorkflowCategory> implements IWorkflowCategoryService {

    @Autowired
    private WorkflowCategoryMapper workflowCategoryMapper;

    @Override
    public List<WorkflowCategory> listCategories() {
        return workflowCategoryMapper.selectList(new LambdaQueryWrapper<WorkflowCategory>()
                .orderByAsc(WorkflowCategory::getOrderNum));
    }

    @Override
    public WorkflowCategory getCategoryById(Long categoryId) {
        return workflowCategoryMapper.selectById(categoryId);
    }

    @Override
    public int insertCategory(WorkflowCategory category) {
        return workflowCategoryMapper.insert(category);
    }

    @Override
    public int updateCategory(WorkflowCategory category) {
        return workflowCategoryMapper.updateById(category);
    }

    @Override
    public int deleteCategoryById(Long categoryId) {
        return workflowCategoryMapper.deleteById(categoryId);
    }
}