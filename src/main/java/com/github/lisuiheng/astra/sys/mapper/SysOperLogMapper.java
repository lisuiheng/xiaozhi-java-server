package com.github.lisuiheng.astra.sys.mapper;

import com.github.lisuiheng.astra.common.core.mapper.BaseMapperPlus;
import com.github.lisuiheng.astra.sys.domain.entity.SysOperLog;
import com.github.lisuiheng.astra.sys.domain.vo.SysOperLogVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志 数据层
 *
 * @author xiaozhi
 */
@Mapper
public interface SysOperLogMapper extends BaseMapperPlus<SysOperLog, SysOperLogVo> {

}