package com.github.lisuiheng.astra.sys.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.lisuiheng.astra.sys.domain.SysTenant;
import com.github.lisuiheng.astra.sys.domain.bo.SysTenantBo;
import com.github.lisuiheng.astra.sys.domain.vo.SysTenantVo;
import com.github.lisuiheng.astra.sys.mapper.SysTenantMapper;
import com.github.lisuiheng.astra.sys.service.ISysTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 * 租户服务实现
 *
 * @author Qoder
 */
@RequiredArgsConstructor
@Service
public class SysTenantServiceImpl implements ISysTenantService {
    private final SysTenantMapper baseMapper;
    @Override
    public List<SysTenantVo> queryList(SysTenantBo sysTenantBo) {
        LambdaQueryWrapper<SysTenant> lqw = buildQueryWrapper(sysTenantBo);
        return baseMapper.selectVoList(lqw);
    }
    private LambdaQueryWrapper<SysTenant> buildQueryWrapper(SysTenantBo bo) {
        LambdaQueryWrapper<SysTenant> lqw = new LambdaQueryWrapper<>();
        lqw.eq(bo.getTenantId() != null, SysTenant::getTenantId, bo.getTenantId());
        lqw.like(bo.getContactUserName() != null, SysTenant::getContactUserName, bo.getContactUserName());
        lqw.eq(bo.getContactPhone() != null, SysTenant::getContactPhone, bo.getContactPhone());
        lqw.like(bo.getCompanyName() != null, SysTenant::getCompanyName, bo.getCompanyName());
        lqw.eq(bo.getStatus() != null, SysTenant::getStatus, bo.getStatus());
        lqw.orderByAsc(SysTenant::getId);
        return lqw;
    }
}