package com.github.lisuiheng.astra.sys.controller;

import com.github.lisuiheng.astra.common.domain.R;
import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.sys.dto.FlowDefinitionDto;
import com.github.lisuiheng.astra.sys.dto.FlowDefinitionVo;
import com.github.lisuiheng.astra.sys.service.IFlowDefinitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 流程定义控制器
 *
 * @author xiaozhi
 */
@Slf4j
@RestController
@RequestMapping("/workflow/definition")
@RequiredArgsConstructor
public class FlowDefinitionController {

    private final IFlowDefinitionService flowDefinitionService;

    /**
     * 查询流程定义列表
     *
     * @param flowDefinition 查询参数
     * @param pageQuery      分页参数
     * @return 流程定义分页列表
     */
    @GetMapping("/list")
    public TableDataInfo<FlowDefinitionVo> list(FlowDefinitionDto flowDefinition, PageQuery pageQuery) {
        log.info("查询流程定义列表，参数：{}", flowDefinition);
        return flowDefinitionService.queryList(flowDefinition, pageQuery);
    }

    /**
     * 查询未发布的流程定义列表
     *
     * @param flowDefinition 查询参数
     * @param pageQuery      分页参数
     * @return 未发布的流程定义分页列表
     */
    @GetMapping("/unPublishList")
    public TableDataInfo<FlowDefinitionVo> unPublishList(FlowDefinitionDto flowDefinition, PageQuery pageQuery) {
        log.info("查询未发布的流程定义列表，参数：{}", flowDefinition);
        return flowDefinitionService.unPublishList(flowDefinition, pageQuery);
    }

    /**
     * 获取流程定义详细信息
     *
     * @param id 流程定义id
     * @return 流程定义详细信息
     */
    @GetMapping(value = "/{id}")
    public R<FlowDefinitionVo> getInfo(@PathVariable Long id) {
        log.info("获取流程定义详细信息，id：{}", id);
        // 这里需要添加具体实现逻辑
        // 由于我们没有具体的查询方法，暂时返回空的VO
        FlowDefinitionVo vo = new FlowDefinitionVo();
        vo.setId(id);
        return R.ok(vo);
    }
}