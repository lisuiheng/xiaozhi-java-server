package com.github.lisuiheng.astra.sys.controller;

import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.common.domain.R;
import com.github.lisuiheng.astra.common.excel.utils.ExcelUtil;
import com.github.lisuiheng.astra.common.log.annotation.Log;
import com.github.lisuiheng.astra.common.log.enums.BusinessType;
import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.sys.domain.bo.SysOperLogBo;
import com.github.lisuiheng.astra.sys.domain.vo.SysOperLogVo;
import com.github.lisuiheng.astra.sys.service.ISysOperLogService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 操作日志记录
 *
 * @author xiaozhi
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/monitor/operlog")
public class SysOperlogController {

    private final ISysOperLogService operLogService;

    /**
     * 获取操作日志记录列表
     */
    @SaCheckPermission("monitor:operlog:list")
    @GetMapping("/list")
    public TableDataInfo<SysOperLogVo> list(SysOperLogBo operLog, PageQuery pageQuery) {
        return operLogService.selectPageOperLogList(operLog, pageQuery);
    }

    /**
     * 导出操作日志记录列表
     */
    @Log(title = "操作日志", businessType = BusinessType.EXPORT)
    @SaCheckPermission("monitor:operlog:export")
    @PostMapping("/export")
    public void export(SysOperLogBo operLog, HttpServletResponse response) {
        List<SysOperLogVo> list = operLogService.selectOperLogList(operLog);
        ExcelUtil.exportExcel(list, "操作日志", SysOperLogVo.class, response);
    }

    /**
     * 批量删除操作日志
     *
     * @param operIds 日志ids
     */
    @SaCheckPermission("monitor:operlog:remove")
    @Log(title = "操作日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{operIds}")
    public R<Void> remove(@PathVariable Long[] operIds) {
        operLogService.deleteOperLogByIds(operIds);
        return R.ok();
    }

    /**
     * 清理操作日志记录
     */
    @SaCheckPermission("monitor:operlog:remove")
    @Log(title = "操作日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public R<Void> clean() {
        operLogService.cleanOperLog();
        return R.ok();
    }
}