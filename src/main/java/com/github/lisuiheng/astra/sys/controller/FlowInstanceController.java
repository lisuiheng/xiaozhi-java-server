package com.github.lisuiheng.astra.sys.controller;

import com.github.lisuiheng.astra.common.domain.R;
import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.sys.dto.FlowInstanceDto;
import com.github.lisuiheng.astra.sys.dto.FlowInstanceVo;
import com.github.lisuiheng.astra.sys.service.IFlowInstanceService;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 工作流实例控制器
 *
 * @author xiaozhi
 */
@Slf4j
@RestController
@RequestMapping("/workflow/instance")
@RequiredArgsConstructor
public class FlowInstanceController {

    private final IFlowInstanceService flowInstanceService;

    /**
     * 查询运行中实例列表
     *
     * @param flowInstance 查询参数
     * @param pageQuery    分页参数
     * @return 运行中的流程实例分页列表
     */
    @GetMapping("/pageByRunning")
    public TableDataInfo<FlowInstanceVo> pageByRunning(FlowInstanceDto flowInstance, PageQuery pageQuery) {
        log.info("查询运行中实例列表，参数：{}", flowInstance);
        return flowInstanceService.pageByRunning(flowInstance, pageQuery);
    }

    /**
     * 查询已完成实例列表
     *
     * @param flowInstance 查询参数
     * @param pageQuery    分页参数
     * @return 已完成的流程实例分页列表
     */
    @GetMapping("/pageByFinish")
    public TableDataInfo<FlowInstanceVo> pageByFinish(FlowInstanceDto flowInstance, PageQuery pageQuery) {
        log.info("查询已完成实例列表，参数：{}", flowInstance);
        return flowInstanceService.pageByFinish(flowInstance, pageQuery);
    }

    /**
     * 分页查询当前登录人单据
     *
     * @param flowInstance 查询参数
     * @param pageQuery    分页参数
     * @return 当前用户的流程实例分页列表
     */
    @GetMapping("/pageByCurrent")
    public TableDataInfo<FlowInstanceVo> pageByCurrent(FlowInstanceDto flowInstance, PageQuery pageQuery) {
        log.info("查询当前用户实例列表，参数：{}", flowInstance);
        return flowInstanceService.pageByCurrent(flowInstance, pageQuery);
    }

    /**
     * 获取流程变量
     *
     * @param instanceId 流程实例id
     * @return 流程变量
     */
    @GetMapping("/instanceVariable/{instanceId}")
    public R<Map<String, Object>> instanceVariable(@PathVariable Long instanceId) {
        log.info("获取流程实例变量，实例ID：{}", instanceId);
        Map<String, Object> variables = flowInstanceService.instanceVariable(instanceId);
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("variable", variables);
        return R.ok(result);
    }
}