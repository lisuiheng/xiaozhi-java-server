package com.github.lisuiheng.astra.sys.mapper;
import com.github.lisuiheng.astra.sys.domain.SysSocial;
import com.github.lisuiheng.astra.common.core.mapper.BaseMapperPlus;
import com.github.lisuiheng.astra.sys.domain.vo.SysSocialVo;
import org.apache.ibatis.annotations.Mapper;
/**
 * 社会化关系Mapper接口
 *
 * @author Qoder
 */
@Mapper
public interface SysSocialMapper extends BaseMapperPlus<SysSocial, SysSocialVo> {
}