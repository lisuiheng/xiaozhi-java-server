package com.github.lisuiheng.astra.sys.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.common.domain.R;
import com.github.lisuiheng.astra.common.log.annotation.Log;
import com.github.lisuiheng.astra.common.log.enums.BusinessType;
import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.common.web.core.BaseController;
import com.github.lisuiheng.astra.sys.domain.bo.SysLogininforBo;
import com.github.lisuiheng.astra.sys.domain.vo.SysLogininforVo;
import com.github.lisuiheng.astra.sys.service.ISysLogininforService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 系统访问记录
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/monitor/logininfor")
public class SysLogininforController extends BaseController {

    private final ISysLogininforService logininforService;

    /**
     * 获取登录记录列表
     */
    @SaCheckPermission("monitor:logininfor:list")
    @GetMapping("/list")
    public TableDataInfo<SysLogininforVo> list(SysLogininforBo logininforBo, PageQuery pageQuery) {
        return logininforService.selectPageLogininforList(logininforBo, pageQuery);
    }

    /**
     * 清空登录记录
     */
    @SaCheckPermission("monitor:logininfor:remove")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/clean")
    public R<Void> clean() {
        logininforService.cleanLogininfor();
        return R.ok();
    }

    /**
     * 批量删除登录日志
     * @param infoIds 日志ids
     */
    @SaCheckPermission("monitor:logininfor:remove")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{infoIds}")
    public R<Void> remove(@PathVariable Long[] infoIds) {
        return toAjax(logininforService.deleteLogininforByIds(infoIds));
    }
}