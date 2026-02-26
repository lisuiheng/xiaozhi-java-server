package com.github.lisuiheng.astra.sys.controller;

import com.github.lisuiheng.astra.common.domain.R;
import com.github.lisuiheng.astra.common.satoken.utils.LoginHelper;
import com.github.lisuiheng.astra.common.web.core.BaseController;
import com.github.lisuiheng.astra.sys.domain.vo.SysSocialVo;
import com.github.lisuiheng.astra.sys.service.ISysSocialService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 社会化关系
 *
 * @author Qoder
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/social")
public class SysSocialController extends BaseController {

    private final ISysSocialService socialUserService;

    /**
     * 查询社会化关系列表
     */
    @GetMapping("/list")
    public R<List<SysSocialVo>> list() {
        return R.ok(socialUserService.queryListByUserId(LoginHelper.getUserId()));
    }
}