package com.github.lisuiheng.astra.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.sys.entity.FlowDefinition;
import com.github.lisuiheng.astra.sys.mapper.FlowDefinitionMapper;
import com.github.lisuiheng.astra.sys.service.IFlowDefinitionService;
import com.github.lisuiheng.astra.sys.dto.FlowDefinitionDto;
import com.github.lisuiheng.astra.sys.dto.FlowDefinitionVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 流程定义服务实现类
 *
 * @author xiaozhi
 */
@Service
@RequiredArgsConstructor
public class FlowDefinitionServiceImpl implements IFlowDefinitionService {

    private final FlowDefinitionMapper flowDefinitionMapper;

    /**
     * 查询流程定义列表
     *
     * @param flowDefinition 查询参数
     * @param pageQuery      分页参数
     * @return 流程定义分页列表
     */
    @Override
    public TableDataInfo<FlowDefinitionVo> queryList(FlowDefinitionDto flowDefinition, PageQuery pageQuery) {
        LambdaQueryWrapper<FlowDefinition> lqw = buildQueryWrapper(flowDefinition);
        Page<FlowDefinition> page = flowDefinitionMapper.selectPage(pageQuery.build(), lqw);
        
        // 将实体类列表转换为VO列表
        List<FlowDefinitionVo> voList = page.getRecords().stream()
            .map(this::convertToVo)
            .collect(Collectors.toList());
        
        Page<FlowDefinitionVo> voPage = new Page<>();
        voPage.setRecords(voList);
        voPage.setTotal(page.getTotal());
        voPage.setCurrent(page.getCurrent());
        voPage.setSize(page.getSize());
        return TableDataInfo.build(voPage);
    }

    /**
     * 查询未发布的流程定义列表
     *
     * @param flowDefinition 查询参数
     * @param pageQuery      分页参数
     * @return 未发布的流程定义分页列表
     */
    @Override
    public TableDataInfo<FlowDefinitionVo> unPublishList(FlowDefinitionDto flowDefinition, PageQuery pageQuery) {
        // 设置只查询未发布的流程定义
        flowDefinition.setIsPublish(0); // 0表示未发布
        LambdaQueryWrapper<FlowDefinition> lqw = buildQueryWrapper(flowDefinition);
        Page<FlowDefinition> page = flowDefinitionMapper.selectPage(pageQuery.build(), lqw);
        
        // 将实体类列表转换为VO列表
        List<FlowDefinitionVo> voList = page.getRecords().stream()
            .map(this::convertToVo)
            .collect(Collectors.toList());
        
        Page<FlowDefinitionVo> voPage = new Page<>();
        voPage.setRecords(voList);
        voPage.setTotal(page.getTotal());
        voPage.setCurrent(page.getCurrent());
        voPage.setSize(page.getSize());
        return TableDataInfo.build(voPage);
    }

    /**
     * 构建查询条件
     *
     * @param flowDefinition 查询参数
     * @return LambdaQueryWrapper
     */
    private LambdaQueryWrapper<FlowDefinition> buildQueryWrapper(FlowDefinitionDto flowDefinition) {
        LambdaQueryWrapper<FlowDefinition> lqw = Wrappers.lambdaQuery();
        lqw.eq(FlowDefinition::getDelFlag, "0"); // 只查询未删除的记录
        if (flowDefinition.getFlowCode() != null && !"".equals(flowDefinition.getFlowCode())) {
            lqw.like(FlowDefinition::getFlowCode, flowDefinition.getFlowCode());
        }
        if (flowDefinition.getFlowName() != null && !"".equals(flowDefinition.getFlowName())) {
            lqw.like(FlowDefinition::getFlowName, flowDefinition.getFlowName());
        }
        if (flowDefinition.getCategory() != null && !"".equals(flowDefinition.getCategory())) {
            lqw.eq(FlowDefinition::getCategory, flowDefinition.getCategory());
        }
        if (flowDefinition.getIsPublish() != null) {
            lqw.eq(FlowDefinition::getIsPublish, flowDefinition.getIsPublish());
        }
        lqw.orderByDesc(FlowDefinition::getCreateTime);
        return lqw;
    }
    
    /**
     * 将实体类转换为VO对象
     *
     * @param entity 流程定义实体
     * @return 流程定义VO对象
     */
    private FlowDefinitionVo convertToVo(FlowDefinition entity) {
        FlowDefinitionVo vo = new FlowDefinitionVo();
        vo.setId(entity.getId());
        vo.setFlowCode(entity.getFlowCode());
        vo.setFlowName(entity.getFlowName());
        vo.setCategory(entity.getCategory());
        vo.setVersion(entity.getVersion());
        vo.setIsPublish(entity.getIsPublish());
        vo.setActivityStatus(entity.getActivityStatus());
        vo.setFormPath(entity.getFormPath());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }
}