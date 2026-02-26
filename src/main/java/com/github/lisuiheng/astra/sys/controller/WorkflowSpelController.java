package com.github.lisuiheng.astra.sys.controller;

import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.common.domain.R;
import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.sys.entity.WorkflowSpel;
import com.github.lisuiheng.astra.sys.service.IWorkflowSpelService;
import com.github.lisuiheng.astra.server.ai.model.dto.WorkflowSpelDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 工作流spel表达式定义控制器
 */
@RestController
@RequestMapping("/workflow/spel")
public class WorkflowSpelController {

    @Autowired
    private IWorkflowSpelService workflowSpelService;

    /**
     * 查询工作流spel表达式定义列表
     */
    @GetMapping("/list")
    public R<TableDataInfo<WorkflowSpelDto>> listWorkflows(WorkflowSpel workflowSpel, PageQuery pageQuery) {
        TableDataInfo<WorkflowSpel> pageInfo = workflowSpelService.listWorkflows(workflowSpel, pageQuery);
        
        // 转换为DTO
        List<WorkflowSpelDto> dtoList = pageInfo.getRows().stream()
                .map(entity -> {
                    WorkflowSpelDto dto = new WorkflowSpelDto();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                })
                .collect(Collectors.toList());
        
        TableDataInfo<WorkflowSpelDto> dtoPageInfo = new TableDataInfo<>();
        dtoPageInfo.setRows(dtoList);
        dtoPageInfo.setTotal(pageInfo.getTotal());
        
        return R.ok(dtoPageInfo);
    }

    /**
     * 根据ID查询工作流spel表达式定义
     */
    @GetMapping("/{id}")
    public R<WorkflowSpelDto> getWorkflowSpel(@PathVariable Long id) {
        WorkflowSpel workflowSpel = workflowSpelService.getById(id);
        if (workflowSpel == null) {
            return R.fail("工作流spel表达式定义不存在");
        }
        
        WorkflowSpelDto dto = new WorkflowSpelDto();
        BeanUtils.copyProperties(workflowSpel, dto);
        
        return R.ok(dto);
    }

    /**
     * 新增工作流spel表达式定义
     */
    @PostMapping
    public R<Void> addWorkflowSpel(@RequestBody WorkflowSpelDto workflowSpelDto) {
        WorkflowSpel workflowSpel = new WorkflowSpel();
        BeanUtils.copyProperties(workflowSpelDto, workflowSpel);
        
        workflowSpelService.insertWorkflowSpel(workflowSpel);
        
        return R.ok();
    }

    /**
     * 修改工作流spel表达式定义
     */
    @PutMapping
    public R<Void> updateWorkflowSpel(@RequestBody WorkflowSpelDto workflowSpelDto) {
        WorkflowSpel workflowSpel = new WorkflowSpel();
        BeanUtils.copyProperties(workflowSpelDto, workflowSpel);
        
        workflowSpelService.updateWorkflowSpel(workflowSpel);
        
        return R.ok();
    }

    /**
     * 删除工作流spel表达式定义
     */
    @DeleteMapping("/{id}")
    public R<Void> deleteWorkflowSpel(@PathVariable Long id) {
        workflowSpelService.deleteWorkflowSpelById(id);
        
        return R.ok();
    }
}