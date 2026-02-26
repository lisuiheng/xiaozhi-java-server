package com.github.lisuiheng.astra.sys.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ObjectUtil;
import com.github.lisuiheng.astra.common.constant.SystemConstants;
import lombok.RequiredArgsConstructor;
import com.github.lisuiheng.astra.common.domain.R;
import com.github.lisuiheng.astra.common.log.annotation.Log;
import com.github.lisuiheng.astra.common.log.enums.BusinessType;
import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.common.idempotent.annotation.RepeatSubmit;
import com.github.lisuiheng.astra.common.web.core.BaseController;
import com.github.lisuiheng.astra.sys.domain.bo.SysDeptBo;
import com.github.lisuiheng.astra.sys.domain.bo.SysPostBo;
import com.github.lisuiheng.astra.sys.domain.vo.SysPostVo;
import com.github.lisuiheng.astra.sys.service.ISysDeptService;
import com.github.lisuiheng.astra.sys.service.ISysPostService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 岗位信息操作处理
 *
 * @author Michelle.Chung
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/post")
public class SysPostController extends BaseController {

    private final ISysPostService postService;
    private final ISysDeptService deptService;

    /**
     * 获取岗位列表
     */
    @SaCheckPermission("system:post:list")
    @GetMapping("/list")
    public TableDataInfo<SysPostVo> list(SysPostBo post, PageQuery pageQuery) {
        return postService.selectPagePostList(post, pageQuery);
    }

    /**
     * 新增岗位
     */
    @SaCheckPermission("system:post:add")
    @Log(title = "岗位管理", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysPostBo post) {
        if (!postService.checkPostNameUnique(post)) {
            return R.fail("新增岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        } else if (!postService.checkPostCodeUnique(post)) {
            return R.fail("新增岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        return toAjax(postService.insertPost(post));
    }

    /**
     * 根据岗位编号获取详细信息
     *
     * @param postId 岗位ID
     */
    @SaCheckPermission("system:post:query")
    @GetMapping(value = "/{postId}")
    public R<SysPostVo> getInfo(@PathVariable Long postId) {
        return R.ok(postService.selectPostById(postId));
    }

    /**
     * 修改岗位
     */
    @SaCheckPermission("system:post:edit")
    @Log(title = "岗位管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysPostBo post) {
        if (!postService.checkPostNameUnique(post)) {
            return R.fail("修改岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        } else if (!postService.checkPostCodeUnique(post)) {
            return R.fail("修改岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        } else if (SystemConstants.DISABLE.equals(post.getStatus())
            && postService.countUserPostById(post.getPostId()) > 0) {
            return R.fail("该岗位下存在已分配用户，不能禁用!");
        }
        return toAjax(postService.updatePost(post));
    }

    /**
     * 获取部门树列表
     */
    @SaCheckPermission("system:post:list")
    @GetMapping("/deptTree")
    public R<List<Tree<Long>>> deptTree(SysDeptBo dept) {
        return R.ok(deptService.selectDeptTreeList(dept));
    }

    /**
     * 删除岗位
     *
     * @param postIds 岗位ID串
     */
    @SaCheckPermission("system:post:remove")
    @Log(title = "岗位管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{postIds}")
    public R<Void> remove(@PathVariable Long[] postIds) {
        return toAjax(postService.deletePostByIds(Arrays.asList(postIds)));
    }

    /**
     * 获取岗位选择框列表
     */
    @SaCheckPermission("system:post:query")
    @GetMapping("/optionselect")
    public R<List<SysPostVo>> optionselect(@RequestParam(required = false) Long[] postIds, @RequestParam(required = false) Long deptId) {
        List<SysPostVo> list = new ArrayList<>();
        if (ObjectUtil.isNotNull(deptId)) {
            SysPostBo post = new SysPostBo();
            post.setDeptId(deptId);
            list = postService.selectPostList(post);
        } else if (postIds != null) {
            list = postService.selectPostByIds(List.of(postIds));
        }
        return R.ok(list);
    }
}