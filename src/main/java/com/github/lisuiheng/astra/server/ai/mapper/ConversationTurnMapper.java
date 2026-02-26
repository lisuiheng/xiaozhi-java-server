package com.github.lisuiheng.astra.server.ai.mapper;

import com.github.lisuiheng.astra.common.core.mapper.BaseMapperPlus;
import com.github.lisuiheng.astra.server.ai.model.entity.ConversationTurn;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConversationTurnMapper extends BaseMapperPlus<ConversationTurn, ConversationTurn> {
}