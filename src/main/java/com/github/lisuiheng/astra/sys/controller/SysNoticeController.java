package com.github.lisuiheng.astra.sys.controller;

import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.common.domain.R;
import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.sys.domain.bo.SysNoticeBo;
import com.github.lisuiheng.astra.sys.domain.vo.SysNoticeVo;
import com.github.lisuiheng.astra.sys.service.ISysNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 通知公告信息操作处理
 *
 * @author xiaozhi
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/notice")
public class SysNoticeController {

    private final ISysNoticeService noticeService;

    /**
     * 获取通知公告列表
     */
    @GetMapping("/list")
    public TableDataInfo<SysNoticeVo> list(SysNoticeBo notice, PageQuery pageQuery) {
        return noticeService.selectPageNoticeList(notice, pageQuery);
    }

    /**
     * 根据通知公告编号获取详细信息
     *
     * @param noticeId 公告ID
     */
    @GetMapping(value = "/{noticeId}")
    public R<SysNoticeVo> getInfo(@PathVariable Long noticeId) {
        return R.ok(noticeService.selectNoticeById(noticeId));
    }

    /**
     * 新增通知公告
     */
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysNoticeBo notice) {
        int rows = noticeService.insertNotice(notice);
        return rows > 0 ? R.ok() : R.fail();
    }

    /**
     * 修改通知公告
     */
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysNoticeBo notice) {
        int rows = noticeService.updateNotice(notice);
        return rows > 0 ? R.ok() : R.fail();
    }

    /**
     * 删除通知公告
     *
     * @param noticeIds 公告ID串
     */
    @DeleteMapping("/{noticeIds}")
    public R<Void> remove(@PathVariable Long[] noticeIds) {
        int rows = noticeService.deleteNoticeByIds(noticeIds);
        return rows > 0 ? R.ok() : R.fail();
    }
}