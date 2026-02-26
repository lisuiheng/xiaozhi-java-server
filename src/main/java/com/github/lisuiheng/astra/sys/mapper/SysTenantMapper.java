package com.github.lisuiheng.astra.sys.mapper;
import com.github.lisuiheng.astra.sys.domain.SysTenant;
import com.github.lisuiheng.astra.sys.domain.vo.SysTenantVo;
import com.github.lisuiheng.astra.common.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Mapper;
/**
 * 租户Mapper接口
 *
 * @author Qoder
 */
@Mapper
public interface SysTenantMapper extends BaseMapperPlus<SysTenant, SysTenantVo> {
}