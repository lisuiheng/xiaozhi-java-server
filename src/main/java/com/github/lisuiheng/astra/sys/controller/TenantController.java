package com.github.lisuiheng.astra.sys.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.common.domain.R;
import com.github.lisuiheng.astra.common.web.core.BaseController;
import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.sys.domain.entity.Tenant;
import com.github.lisuiheng.astra.sys.domain.vo.SysTenantVo;
import com.github.lisuiheng.astra.sys.service.ITenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 租户管理
 * 
 * @author Qoder
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/tenant")
public class TenantController extends BaseController {

    private final ITenantService tenantService;

    /**
     * 获取租户列表
     */
    @SaCheckPermission("system.tenant.list")
    @GetMapping("/list")
    public TableDataInfo<SysTenantVo> list(Tenant tenant, PageQuery pageQuery) {
        return tenantService.queryPageList(tenant, pageQuery);
    }

    /**
     * 根据租户ID获取租户详细信息
     */
    @SaCheckPermission("system.tenant.query")
    @GetMapping(value = "/{id}")
    public R<SysTenantVo> getInfo(@PathVariable Long id) {
        return R.ok(tenantService.queryById(id));
    }
}