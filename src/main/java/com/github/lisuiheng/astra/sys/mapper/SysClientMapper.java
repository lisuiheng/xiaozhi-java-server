package com.github.lisuiheng.astra.sys.mapper;
import com.github.lisuiheng.astra.sys.domain.SysClient;
import com.github.lisuiheng.astra.sys.domain.vo.SysClientVo;
import com.github.lisuiheng.astra.common.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Mapper;
/**
 * 授权管理Mapper接口
 *
 * @author Qoder
 */
@Mapper
public interface SysClientMapper extends BaseMapperPlus<SysClient, SysClientVo> {
}