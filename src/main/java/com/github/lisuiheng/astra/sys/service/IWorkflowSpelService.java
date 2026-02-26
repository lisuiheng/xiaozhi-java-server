package com.github.lisuiheng.astra.sys.service;

import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.sys.entity.WorkflowSpel;
import java.util.List;

/**
 * 工作流spel表达式定义服务接口
 */
public interface IWorkflowSpelService {

    /**
     * 查询工作流spel表达式定义列表
     * @param workflowSpel 查询条件
     * @param pageQuery 分页参数
     * @return 工作流spel表达式定义分页列表
     */
    TableDataInfo<WorkflowSpel> listWorkflows(WorkflowSpel workflowSpel, PageQuery pageQuery);

    /**
     * 查询工作流spel表达式定义列表
     * @param workflowSpel 查询条件
     * @return 工作流spel表达式定义列表
     */
    List<WorkflowSpel> listWorkflows(WorkflowSpel workflowSpel);

    /**
     * 根据ID查询工作流spel表达式定义
     * @param id 主键ID
     * @return 工作流spel表达式定义
     */
    WorkflowSpel getById(Long id);

    /**
     * 新增工作流spel表达式定义
     * @param workflowSpel 工作流spel表达式定义
     * @return 结果
     */
    int insertWorkflowSpel(WorkflowSpel workflowSpel);

    /**
     * 修改工作流spel表达式定义
     * @param workflowSpel 工作流spel表达式定义
     * @return 结果
     */
    int updateWorkflowSpel(WorkflowSpel workflowSpel);

    /**
     * 删除工作流spel表达式定义
     * @param id 主键ID
     * @return 结果
     */
    int deleteWorkflowSpelById(Long id);
}