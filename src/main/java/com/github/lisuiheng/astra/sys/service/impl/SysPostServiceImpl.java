package com.github.lisuiheng.astra.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.common.utils.StringUtils;
import com.github.lisuiheng.astra.sys.domain.entity.SysPost;
import com.github.lisuiheng.astra.sys.domain.entity.SysUserPost;
import com.github.lisuiheng.astra.sys.domain.bo.SysPostBo;
import com.github.lisuiheng.astra.sys.domain.vo.SysPostVo;
import com.github.lisuiheng.astra.sys.mapper.SysPostMapper;
import com.github.lisuiheng.astra.sys.mapper.SysUserPostMapper;
import com.github.lisuiheng.astra.sys.service.ISysPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 岗位服务实现类
 * 
 * @author Michelle.Chung
 */
@Service
@RequiredArgsConstructor
public class SysPostServiceImpl extends ServiceImpl<SysPostMapper, SysPost> implements ISysPostService {

    private final SysUserPostMapper userPostMapper;

    @Override
    public TableDataInfo<SysPostVo> selectPagePostList(SysPostBo post, PageQuery pageQuery) {
        LambdaQueryWrapper<SysPost> lqw = buildQueryWrapper(post);
        Page<SysPostVo> page = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
    }

    @Override
    public List<SysPostVo> selectPostList(SysPostBo post) {
        LambdaQueryWrapper<SysPost> lqw = buildQueryWrapper(post);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    public List<SysPostVo> selectPostsByUserId(Long userId) {
        return baseMapper.selectPostsByUserId(userId);
    }

    @Override
    public List<SysPostVo> selectPostAll() {
        return baseMapper.selectVoList(new QueryWrapper<>());
    }

    @Override
    public SysPostVo selectPostById(Long postId) {
        return baseMapper.selectVoById(postId);
    }

    @Override
    public List<Long> selectPostListByUserId(Long userId) {
        List<SysPostVo> posts = baseMapper.selectPostsByUserId(userId);
        return posts.stream().map(SysPostVo::getPostId).collect(Collectors.toList());
    }

    @Override
    public List<SysPostVo> selectPostByIds(List<Long> postIds) {
        return baseMapper.selectVoList(new LambdaQueryWrapper<SysPost>().in(SysPost::getPostId, postIds));
    }

    @Override
    public boolean checkPostNameUnique(SysPostBo post) {
        LambdaQueryWrapper<SysPost> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SysPost::getPostName, post.getPostName());
        if (post.getPostId() != null) {
            lqw.ne(SysPost::getPostId, post.getPostId());
        }
        return baseMapper.selectCount(lqw) == 0;
    }

    @Override
    public boolean checkPostCodeUnique(SysPostBo post) {
        LambdaQueryWrapper<SysPost> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SysPost::getPostCode, post.getPostCode());
        if (post.getPostId() != null) {
            lqw.ne(SysPost::getPostId, post.getPostId());
        }
        return baseMapper.selectCount(lqw) == 0;
    }

    @Override
    public long countPostByPostId(Long postId) {
        return baseMapper.selectCount(new LambdaQueryWrapper<SysPost>().eq(SysPost::getPostId, postId));
    }

    @Override
    public long countUserPostById(Long postId) {
        return userPostMapper.selectCount(new LambdaQueryWrapper<SysUserPost>().eq(SysUserPost::getPostId, postId));
    }

    @Override
    public int deletePostById(Long postId) {
        return baseMapper.deleteById(postId);
    }

    @Override
    public int deletePostByIds(List<Long> postIds) {
        return baseMapper.deleteBatchIds(postIds);
    }

    @Override
    public int insertPost(SysPostBo bo) {
        SysPost post = new SysPost();
        post.setDeptId(bo.getDeptId());
        post.setPostCode(bo.getPostCode());
        post.setPostName(bo.getPostName());
        post.setPostCategory(bo.getPostCategory());
        post.setPostSort(bo.getPostSort());
        post.setStatus(bo.getStatus());
        post.setRemark(bo.getRemark());
        return baseMapper.insert(post);
    }

    @Override
    public int updatePost(SysPostBo bo) {
        SysPost post = new SysPost();
        post.setPostId(bo.getPostId());
        post.setDeptId(bo.getDeptId());
        post.setPostCode(bo.getPostCode());
        post.setPostName(bo.getPostName());
        post.setPostCategory(bo.getPostCategory());
        post.setPostSort(bo.getPostSort());
        post.setStatus(bo.getStatus());
        post.setRemark(bo.getRemark());
        return baseMapper.updateById(post);
    }

    private LambdaQueryWrapper<SysPost> buildQueryWrapper(SysPostBo bo) {
        LambdaQueryWrapper<SysPost> lqw = new LambdaQueryWrapper<>();
        lqw.eq(bo.getDeptId() != null, SysPost::getDeptId, bo.getDeptId())
            .like(StringUtils.isNotBlank(bo.getPostCode()), SysPost::getPostCode, bo.getPostCode())
            .like(StringUtils.isNotBlank(bo.getPostName()), SysPost::getPostName, bo.getPostName())
            .like(StringUtils.isNotBlank(bo.getPostCategory()), SysPost::getPostCategory, bo.getPostCategory())
            .eq(StringUtils.isNotBlank(bo.getStatus()), SysPost::getStatus, bo.getStatus())
            .orderByAsc(SysPost::getPostSort);
        return lqw;
    }
}