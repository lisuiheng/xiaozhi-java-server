package com.github.lisuiheng.astra.sys.service;

import com.github.lisuiheng.astra.sys.domain.bo.SysTenantBo;
import com.github.lisuiheng.astra.sys.domain.vo.SysTenantVo;

import java.util.List;

/**
 * 租户服务接口
 */
public interface ISysTenantService {

    /**
     * 查询租户列表
     *
     * @param sysTenantBo 租户业务对象
     * @return 租户列表
     */
    List<SysTenantVo> queryList(SysTenantBo sysTenantBo);
}