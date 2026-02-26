package com.github.lisuiheng.astra.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.common.utils.MapstructUtils;
import com.github.lisuiheng.astra.common.utils.StringUtils;
import com.github.lisuiheng.astra.sys.domain.entity.SysLogininfor;
import com.github.lisuiheng.astra.sys.domain.bo.SysLogininforBo;
import com.github.lisuiheng.astra.sys.domain.vo.SysLogininforVo;
import com.github.lisuiheng.astra.sys.mapper.SysLogininforMapper;
import com.github.lisuiheng.astra.sys.service.ISysLogininforService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 系统访问记录 服务层处理
 *
 * @author Lion Li
 */
@Service
@RequiredArgsConstructor
public class SysLogininforServiceImpl extends ServiceImpl<SysLogininforMapper, SysLogininfor> implements ISysLogininforService {

    private final SysLogininforMapper baseMapper;

    /**
     * 分页查询登录日志列表
     *
     * @param logininforBo 查询条件
     * @param pageQuery  分页参数
     * @return 登录日志分页列表
     */
    @Override
    public TableDataInfo<SysLogininforVo> selectPageLogininforList(SysLogininforBo logininforBo, PageQuery pageQuery) {
        LambdaQueryWrapper<SysLogininfor> lqw = buildQueryWrapper(logininforBo);
        Page<SysLogininfor> page = pageQuery.build();
        IPage<SysLogininforVo> voPage = baseMapper.selectVoPage(page, lqw);
        return TableDataInfo.build(voPage);
    }

    /**
     * 查询系统登录日志集合
     *
     * @param logininforBo 访问记录
     * @return 登录记录集合
     */
    @Override
    public List<SysLogininforVo> selectLogininforList(SysLogininforBo logininforBo) {
        LambdaQueryWrapper<SysLogininfor> lqw = buildQueryWrapper(logininforBo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SysLogininfor> buildQueryWrapper(SysLogininforBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SysLogininfor> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotBlank(bo.getIpaddr()), SysLogininfor::getIpaddr, bo.getIpaddr());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), SysLogininfor::getStatus, bo.getStatus());
        lqw.like(StringUtils.isNotBlank(bo.getUserName()), SysLogininfor::getUserName, bo.getUserName());
        lqw.between(params.get("beginTime") != null && params.get("endTime") != null,
                SysLogininfor::getLoginTime, params.get("beginTime"), params.get("endTime"));
        lqw.orderByDesc(SysLogininfor::getInfoId);
        return lqw;
    }

    /**
     * 新增系统登录日志
     *
     * @param bo 访问记录信息
     */
    @Override
    public void insertLogininfor(SysLogininforBo bo) {
        SysLogininfor logininfor = MapstructUtils.convert(bo, SysLogininfor.class);
        baseMapper.insert(logininfor);
    }

    /**
     * 查询系统登录日志集合
     *
     * @return 登录记录集合
     */
    @Override
    public List<SysLogininfor> selectLogininforList() {
        LambdaQueryWrapper<SysLogininfor> lqw = new LambdaQueryWrapper<>();
        lqw.orderByDesc(SysLogininfor::getLoginTime);
        return baseMapper.selectList(lqw);
    }

    /**
     * 批量删除系统登录日志
     *
     * @param infoIds 需要删除的登录日志ID
     * @return 结果
     */
    @Override
    public int deleteLogininforByIds(Long[] infoIds) {
        return baseMapper.deleteByIds(Arrays.asList(infoIds));
    }

    /**
     * 清空系统登录日志
     */
    @Override
    public void cleanLogininfor() {
        baseMapper.delete(new LambdaQueryWrapper<>());
    }
}