package com.github.lisuiheng.astra.sys.mapper;

import com.github.lisuiheng.astra.common.core.mapper.BaseMapperPlus;
import com.github.lisuiheng.astra.sys.domain.entity.SysLogininfor;
import com.github.lisuiheng.astra.sys.domain.vo.SysLogininforVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统访问日志情况信息 数据层
 *
 * @author Lion Li
 */
@Mapper
public interface SysLogininforMapper extends BaseMapperPlus<SysLogininfor, SysLogininforVo> {
}