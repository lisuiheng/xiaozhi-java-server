package com.github.lisuiheng.astra.server.ai.mapper;

import com.github.lisuiheng.astra.common.core.mapper.BaseMapperPlus;
import com.github.lisuiheng.astra.server.ai.model.entity.AiSession;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AiSessionMapper extends BaseMapperPlus<AiSession, AiSession> {
}