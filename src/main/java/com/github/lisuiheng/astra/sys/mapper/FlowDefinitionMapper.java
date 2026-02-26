package com.github.lisuiheng.astra.sys.mapper;

import com.github.lisuiheng.astra.common.core.mapper.BaseMapperPlus;
import com.github.lisuiheng.astra.sys.entity.FlowDefinition;
import com.github.lisuiheng.astra.sys.dto.FlowDefinitionVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 流程定义数据访问层
 *
 * @author xiaozhi
 */
public interface FlowDefinitionMapper extends BaseMapperPlus<FlowDefinition, FlowDefinitionVo> {

    /**
     * 查询流程定义列表
     *
     * @param flowDefinition 查询参数
     * @return 流程定义列表
     */
    List<FlowDefinitionVo> selectFlowDefinitionList(@Param("flowDefinition") FlowDefinition flowDefinition);
}