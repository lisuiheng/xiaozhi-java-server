package com.github.lisuiheng.astra.server.ai.mapper;

import com.github.lisuiheng.astra.common.core.mapper.BaseMapperPlus;
import com.github.lisuiheng.astra.server.ai.model.entity.KnowledgeBase;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface KnowledgeBaseMapper extends BaseMapperPlus<KnowledgeBase, KnowledgeBase> {
}