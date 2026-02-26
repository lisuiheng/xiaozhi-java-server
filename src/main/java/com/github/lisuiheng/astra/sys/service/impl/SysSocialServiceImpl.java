package com.github.lisuiheng.astra.sys.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.lisuiheng.astra.sys.domain.SysSocial;
import com.github.lisuiheng.astra.sys.domain.bo.SysSocialBo;
import com.github.lisuiheng.astra.sys.domain.vo.SysSocialVo;
import com.github.lisuiheng.astra.sys.mapper.SysSocialMapper;
import com.github.lisuiheng.astra.sys.service.ISysSocialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 社交服务实现
 *
 * @author Qoder
 */
@RequiredArgsConstructor
@Service
public class SysSocialServiceImpl implements ISysSocialService {
    private final SysSocialMapper baseMapper;
    
    @Override
    public SysSocialVo queryById(String id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public List<SysSocialVo> queryList(SysSocialBo bo) {
        LambdaQueryWrapper<SysSocial> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    public List<SysSocialVo> queryListByUserId(Long userId) {
        LambdaQueryWrapper<SysSocial> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SysSocial::getUserId, userId);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    public Boolean insertByBo(SysSocialBo bo) {
        SysSocial sysSocial = new SysSocial();
        sysSocial.setAuthId(bo.getAuthId());
        sysSocial.setSource(bo.getSource());
        sysSocial.setAccessToken(bo.getAccessToken());
        sysSocial.setExpireIn(bo.getExpireIn());
        sysSocial.setRefreshToken(bo.getRefreshToken());
        sysSocial.setOpenId(bo.getOpenId());
        sysSocial.setUserId(bo.getUserId());
        sysSocial.setAccessCode(bo.getAccessCode());
        sysSocial.setUnionId(bo.getUnionId());
        sysSocial.setScope(bo.getScope());
        sysSocial.setUserName(bo.getUserName());
        sysSocial.setNickName(bo.getNickName());
        sysSocial.setEmail(bo.getEmail());
        sysSocial.setAvatar(bo.getAvatar());
        sysSocial.setTokenType(bo.getTokenType());
        sysSocial.setIdToken(bo.getIdToken());
        sysSocial.setMacAlgorithm(bo.getMacAlgorithm());
        sysSocial.setMacKey(bo.getMacKey());
        sysSocial.setCode(bo.getCode());
        sysSocial.setOauthToken(bo.getOauthToken());
        sysSocial.setOauthTokenSecret(bo.getOauthTokenSecret());
        
        return baseMapper.insert(sysSocial) > 0;
    }

    @Override
    public Boolean updateByBo(SysSocialBo bo) {
        SysSocial sysSocial = new SysSocial();
        sysSocial.setId(bo.getId());
        sysSocial.setAuthId(bo.getAuthId());
        sysSocial.setSource(bo.getSource());
        sysSocial.setAccessToken(bo.getAccessToken());
        sysSocial.setExpireIn(bo.getExpireIn());
        sysSocial.setRefreshToken(bo.getRefreshToken());
        sysSocial.setOpenId(bo.getOpenId());
        sysSocial.setUserId(bo.getUserId());
        sysSocial.setAccessCode(bo.getAccessCode());
        sysSocial.setUnionId(bo.getUnionId());
        sysSocial.setScope(bo.getScope());
        sysSocial.setUserName(bo.getUserName());
        sysSocial.setNickName(bo.getNickName());
        sysSocial.setEmail(bo.getEmail());
        sysSocial.setAvatar(bo.getAvatar());
        sysSocial.setTokenType(bo.getTokenType());
        sysSocial.setIdToken(bo.getIdToken());
        sysSocial.setMacAlgorithm(bo.getMacAlgorithm());
        sysSocial.setMacKey(bo.getMacKey());
        sysSocial.setCode(bo.getCode());
        sysSocial.setOauthToken(bo.getOauthToken());
        sysSocial.setOauthTokenSecret(bo.getOauthTokenSecret());
        
        return baseMapper.updateById(sysSocial) > 0;
    }

    @Override
    public Boolean deleteWithValidById(Long socialId) {
        return baseMapper.deleteById(socialId) > 0;
    }

    @Override
    public List<SysSocialVo> selectByAuthId(String authId) {
        LambdaQueryWrapper<SysSocial> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SysSocial::getAuthId, authId);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SysSocial> buildQueryWrapper(SysSocialBo bo) {
        LambdaQueryWrapper<SysSocial> lqw = new LambdaQueryWrapper<>();
        lqw.eq(bo.getId() != null, SysSocial::getId, bo.getId());
        lqw.eq(bo.getUserId() != null, SysSocial::getUserId, bo.getUserId());
        lqw.eq(bo.getAuthId() != null, SysSocial::getAuthId, bo.getAuthId());
        lqw.eq(bo.getSource() != null, SysSocial::getSource, bo.getSource());
        lqw.eq(bo.getUserName() != null, SysSocial::getUserName, bo.getUserName());
        lqw.orderByDesc(SysSocial::getCreateTime);
        return lqw;
    }
}