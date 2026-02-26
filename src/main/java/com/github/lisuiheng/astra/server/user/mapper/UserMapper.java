package com.github.lisuiheng.astra.server.user.mapper;

import com.github.lisuiheng.astra.common.core.mapper.BaseMapperPlus;
import com.github.lisuiheng.astra.server.user.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapperPlus<User, User> {
}