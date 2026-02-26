package com.github.lisuiheng.astra.sys.mapper;

import com.github.lisuiheng.astra.common.core.mapper.BaseMapperPlus;
import com.github.lisuiheng.astra.sys.domain.entity.Tenant;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租户表 Mapper接口
 * 
 * @author Qoder
 */
@Mapper
public interface TenantMapper extends BaseMapperPlus<Tenant, Tenant> {
}