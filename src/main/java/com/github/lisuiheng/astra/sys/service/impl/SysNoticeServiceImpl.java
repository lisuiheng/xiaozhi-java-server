package com.github.lisuiheng.astra.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.common.utils.MapstructUtils;
import com.github.lisuiheng.astra.common.utils.ObjectUtils;
import com.github.lisuiheng.astra.common.utils.StringUtils;
import com.github.lisuiheng.astra.sys.domain.SysNotice;
import com.github.lisuiheng.astra.sys.domain.entity.SysUser;
import com.github.lisuiheng.astra.sys.domain.bo.SysNoticeBo;
import com.github.lisuiheng.astra.sys.domain.vo.SysNoticeVo;
import com.github.lisuiheng.astra.sys.mapper.SysNoticeMapper;
import com.github.lisuiheng.astra.sys.mapper.SysUserMapper;
import com.github.lisuiheng.astra.sys.service.ISysNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 公告 服务层实现
 *
 * @author ruoyi
 */
@RequiredArgsConstructor
@Service
public class SysNoticeServiceImpl implements ISysNoticeService {

    private final SysNoticeMapper baseMapper;
    private final SysUserMapper userMapper;

    /**
     * 分页查询通知公告列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 通知公告集合
     */
    @Override
    public TableDataInfo<SysNoticeVo> selectPageNoticeList(SysNoticeBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SysNotice> lqw = buildQueryWrapper(bo);
        Page<SysNoticeVo> page = baseMapper.selectVoPage(pageQuery.buildMpPage(), lqw);
        return TableDataInfo.build(page);
    }

    /**
     * 查询通知公告信息
     *
     * @param noticeId 通知公告ID
     * @return 通知公告
     */
    @Override
    public SysNoticeVo selectNoticeById(Long noticeId) {
        return baseMapper.selectVoById(noticeId);
    }

    /**
     * 新增公告
     *
     * @param bo 公告信息
     * @return 结果
     */
    @Override
    public int insertNotice(SysNoticeBo bo) {
        SysNotice notice = MapstructUtils.convert(bo, SysNotice.class);
        return baseMapper.insert(notice);
    }

    /**
     * 修改公告
     *
     * @param bo 公告信息
     * @return 结果
     */
    @Override
    public int updateNotice(SysNoticeBo bo) {
        SysNotice notice = MapstructUtils.convert(bo, SysNotice.class);
        return baseMapper.updateById(notice);
    }

    /**
     * 删除公告对象
     *
     * @param noticeIds 公告ID集合
     * @return 结果
     */
    @Override
    public int deleteNoticeByIds(Long[] noticeIds) {
        return baseMapper.deleteBatchIds(java.util.Arrays.asList(noticeIds));
    }

    private LambdaQueryWrapper<SysNotice> buildQueryWrapper(SysNoticeBo bo) {
        LambdaQueryWrapper<SysNotice> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getNoticeTitle()), SysNotice::getNoticeTitle, bo.getNoticeTitle());
        lqw.eq(StringUtils.isNotBlank(bo.getNoticeType()), SysNotice::getNoticeType, bo.getNoticeType());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), SysNotice::getStatus, bo.getStatus());
        if (StringUtils.isNotBlank(bo.getCreateByName())) {
            SysUser sysUser = userMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserName, bo.getCreateByName()));
            lqw.eq(SysNotice::getCreateBy, ObjectUtils.notNullGetter(sysUser, SysUser::getUserId));
        }
        lqw.orderByAsc(SysNotice::getNoticeId);
        return lqw;
    }
}