package com.github.lisuiheng.astra.sys.controller;

import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.sys.domain.bo.FlowTaskBo;
import com.github.lisuiheng.astra.sys.service.ISysWorkflowTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 工作流任务控制器
 *
 * @author
 */
@RestController
@RequestMapping("/workflow/task")
@RequiredArgsConstructor
public class WorkflowTaskController {

    private final ISysWorkflowTaskService sysWorkflowTaskService;

    /**
     * 查询当前用户的待办任务
     */
    @GetMapping("/pageByTaskWait")
    public TableDataInfo<?> pageByTaskWait(FlowTaskBo flowTaskBo, PageQuery pageQuery) {
        return sysWorkflowTaskService.pageByTaskWait(flowTaskBo, pageQuery);
    }

    /**
     * 查询当前用户的已办任务
     */
    @GetMapping("/pageByTaskFinish")
    public TableDataInfo<?> pageByTaskFinish(FlowTaskBo flowTaskBo, PageQuery pageQuery) {
        return sysWorkflowTaskService.pageByTaskFinish(flowTaskBo, pageQuery);
    }

    /**
     * 查询当前租户所有待办任务
     */
    @GetMapping("/pageByAllTaskWait")
    public TableDataInfo<?> pageByAllTaskWait(FlowTaskBo flowTaskBo, PageQuery pageQuery) {
        return sysWorkflowTaskService.pageByAllTaskWait(flowTaskBo, pageQuery);
    }

    /**
     * 查询已办任务
     */
    @GetMapping("/pageByAllTaskFinish")
    public TableDataInfo<?> pageByAllTaskFinish(FlowTaskBo flowTaskBo, PageQuery pageQuery) {
        return sysWorkflowTaskService.pageByAllTaskFinish(flowTaskBo, pageQuery);
    }

    /**
     * 查询当前用户的抄送
     */
    @GetMapping("/pageByTaskCopy")
    public TableDataInfo<?> pageByTaskCopy(FlowTaskBo flowTaskBo, PageQuery pageQuery) {
        return sysWorkflowTaskService.pageByTaskCopy(flowTaskBo, pageQuery);
    }
}