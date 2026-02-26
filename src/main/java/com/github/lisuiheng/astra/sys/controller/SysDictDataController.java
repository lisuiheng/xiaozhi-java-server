package com.github.lisuiheng.astra.sys.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.common.domain.R;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import com.github.lisuiheng.astra.common.excel.utils.ExcelUtil;
import com.github.lisuiheng.astra.common.idempotent.annotation.RepeatSubmit;
import com.github.lisuiheng.astra.common.log.annotation.Log;
import com.github.lisuiheng.astra.common.log.enums.BusinessType;
import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.common.web.core.BaseController;
import com.github.lisuiheng.astra.sys.domain.bo.SysDictDataBo;
import com.github.lisuiheng.astra.sys.domain.vo.SysDictDataVo;
import com.github.lisuiheng.astra.sys.service.ISysDictDataService;
import com.github.lisuiheng.astra.sys.service.ISysDictTypeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据字典信息
 *
 * @author Qoder
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/dict/data")
public class SysDictDataController extends BaseController {

    private final ISysDictDataService dictDataService;
    private final ISysDictTypeService dictTypeService;

    /**
     * 查询字典数据列表
     */
    @SaCheckPermission("system:dict:list")
    @GetMapping("/list")
    public TableDataInfo<SysDictDataVo> list(SysDictDataBo dictData, PageQuery pageQuery) {
        return dictDataService.selectPageDictDataList(dictData, pageQuery);
    }

    /**
     * 导出字典数据列表
     */
    @Log(title = "字典数据", businessType = BusinessType.EXPORT)
    @SaCheckPermission("system:dict:export")
    @PostMapping("/export")
    public void export(SysDictDataBo dictData, HttpServletResponse response) {
        List<SysDictDataVo> list = dictDataService.selectDictDataList(dictData);
        ExcelUtil.exportExcel(list, "字典数据", SysDictDataVo.class, response);
    }

    /**
     * 查询字典数据详细
     *
     * @param dictCode 字典code
     */
    @SaCheckPermission("system:dict:query")
    @GetMapping(value = "/{dictCode}")
    public R<SysDictDataVo> getInfo(@PathVariable Long dictCode) {
        return R.ok(dictDataService.selectDictDataById(dictCode));
    }

    /**
     * 根据字典类型查询字典数据信息
     *
     * @param dictType 字典类型
     */
    @GetMapping(value = "/type/{dictType}")
    public R<List<SysDictDataVo>> dictType(@PathVariable String dictType) {
        List<SysDictDataVo> data = dictDataService.selectDictDataByType(dictType);
        return R.ok(data);
    }

    /**
     * 新增字典数据
     */
    @SaCheckPermission("system:dict:add")
    @Log(title = "字典数据", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysDictDataBo dictData) {
        // 不需要检查字典类型唯一性，而是检查字典类型是否存在
        return toAjax(dictDataService.insertDictData(dictData));
    }

    /**
     * 修改字典数据
     */
    @SaCheckPermission("system:dict:edit")
    @Log(title = "字典数据", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysDictDataBo dictData) {
        // 不需要检查字典类型唯一性
        return toAjax(dictDataService.updateDictData(dictData));
    }

    /**
     * 删除字典数据
     *
     * @param dictCodes 字典code串
     */
    @SaCheckPermission("system:dict:remove")
    @Log(title = "字典数据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictCodes}")
    public R<Void> remove(@PathVariable Long[] dictCodes) {
        return toAjax(dictDataService.deleteDictDataByIds(dictCodes));
    }
}