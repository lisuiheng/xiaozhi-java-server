package com.github.lisuiheng.astra.sys.service;

import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.sys.domain.bo.SysNoticeBo;
import com.github.lisuiheng.astra.sys.domain.vo.SysNoticeVo;

/**
 * 通知公告 服务层
 *
 * @author ruoyi
 */
public interface ISysNoticeService {

    /**
     * 根据条件分页查询通知公告列表
     *
     * @param notice notice
     * @param pageQuery pageQuery
     * @return 通知公告集合
     */
    TableDataInfo<SysNoticeVo> selectPageNoticeList(SysNoticeBo notice, PageQuery pageQuery);

    /**
     * 查询通知公告信息
     *
     * @param noticeId 通知公告ID
     * @return 通知公告
     */
    SysNoticeVo selectNoticeById(Long noticeId);

    /**
     * 新增通知公告
     *
     * @param bo 通知公告信息
     * @return 结果
     */
    int insertNotice(SysNoticeBo bo);

    /**
     * 修改通知公告
     *
     * @param bo 通知公告信息
     * @return 结果
     */
    int updateNotice(SysNoticeBo bo);

    /**
     * 删除通知公告信息
     *
     * @param noticeIds 需要删除的通知公告ID
     * @return 结果
     */
    int deleteNoticeByIds(Long[] noticeIds);
}