package com.github.lisuiheng.astra.sys.mapper;
import com.github.lisuiheng.astra.common.core.mapper.BaseMapperPlus;
import com.github.lisuiheng.astra.sys.domain.SysConfig;
import com.github.lisuiheng.astra.sys.domain.vo.SysConfigVo;
import org.apache.ibatis.annotations.Mapper;
/**
 * 参数配置 数据层
 *
 * @author Qoder
 */
@Mapper
public interface SysConfigMapper extends BaseMapperPlus<SysConfig, SysConfigVo> {
}