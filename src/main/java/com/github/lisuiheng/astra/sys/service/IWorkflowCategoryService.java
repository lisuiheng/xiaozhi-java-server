package com.github.lisuiheng.astra.sys.service;

import com.github.lisuiheng.astra.sys.entity.WorkflowCategory;
import java.util.List;

/**
 * 工作流分类服务接口
 */
public interface IWorkflowCategoryService {

    /**
     * 查询工作流分类列表
     * @return 工作流分类列表
     */
    List<WorkflowCategory> listCategories();

    /**
     * 根据ID查询工作流分类
     * @param categoryId 分类ID
     * @return 工作流分类
     */
    WorkflowCategory getCategoryById(Long categoryId);

    /**
     * 新增工作流分类
     * @param category 工作流分类
     * @return 结果
     */
    int insertCategory(WorkflowCategory category);

    /**
     * 修改工作流分类
     * @param category 工作流分类
     * @return 结果
     */
    int updateCategory(WorkflowCategory category);

    /**
     * 删除工作流分类
     * @param categoryId 分类ID
     * @return 结果
     */
    int deleteCategoryById(Long categoryId);
}