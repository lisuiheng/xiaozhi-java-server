package com.github.lisuiheng.astra.server.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.lisuiheng.astra.common.domain.R;
import com.github.lisuiheng.astra.server.user.mapper.UserMapper;
import com.github.lisuiheng.astra.server.user.model.dto.*;
import com.github.lisuiheng.astra.server.user.model.entity.User;
import com.github.lisuiheng.astra.server.user.model.vo.AgentSimpleVO;
import com.github.lisuiheng.astra.server.user.model.vo.DeviceSimpleVO;
import com.github.lisuiheng.astra.server.user.model.vo.UserLoginVO;
import com.github.lisuiheng.astra.server.user.model.vo.UserSimpleVO;
import com.github.lisuiheng.astra.server.user.model.vo.UserVO;
import com.github.lisuiheng.astra.server.user.service.UserService;
import com.github.lisuiheng.astra.common.util.PasswordUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "用户管理", description = "用户相关接口")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserController {

    private final UserService userService;


    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }


    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册接口")
    public R<UserVO> register(@Valid @RequestBody UserRegisterDTO dto,
                              HttpServletRequest request) {
        try {
            dto.setClientIp(getClientIp(request));
            UserVO userVO = userService.register(dto);
            return R.ok("注册成功", userVO);
        } catch (Exception e) {
            log.error("用户注册失败: username={}", dto.getUsername(), e);
            return R.fail(e.getMessage());
        }
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录接口")
    public R<UserLoginVO> login(@Valid @RequestBody UserLoginDTO dto,
                                HttpServletRequest request) {
        try {
            dto.setClientIp(getClientIp(request));
            UserLoginVO loginVO = userService.login(dto);
            return R.ok("登录成功", loginVO);
        } catch (Exception e) {
            log.error("用户登录失败: username={}", dto.getUsername(), e);
            return R.fail(e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    @Operation(summary = "获取用户详情", description = "根据用户ID获取用户详细信息")
    @Parameter(name = "userId", description = "用户ID", required = true)
    public R<UserVO> getUserById(@PathVariable @NotBlank(message = "用户ID不能为空") String userId) {
        try {
            UserVO userVO = userService.getUserById(userId);
            return R.ok(userVO);
        } catch (Exception e) {
            log.error("获取用户详情失败: userId={}", userId, e);
            return R.fail(e.getMessage());
        }
    }

    @PutMapping("/{userId}")
    @Operation(summary = "更新用户信息", description = "更新指定用户的信息")
    public R<UserVO> updateUser(@PathVariable String userId,
                                @Valid @RequestBody UserUpdateDTO dto) {
        try {
            UserVO userVO = userService.updateUser(userId, dto);
            return R.ok("更新成功", userVO);
        } catch (Exception e) {
            log.error("更新用户信息失败: userId={}", userId, e);
            return R.fail(e.getMessage());
        }
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "删除用户", description = "逻辑删除用户")
    public R<Void> deleteUser(@PathVariable String userId) {
        try {
            userService.deleteUser(userId);
            return R.ok("删除成功");
        } catch (Exception e) {
            log.error("删除用户失败: userId={}", userId, e);
            return R.fail(e.getMessage());
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出接口")
    public R<Void> logout(@RequestHeader("Authorization") String token) {
        try {
            // TODO: 这里可以添加token失效逻辑
            log.info("用户登出: token={}", token.substring(0, Math.min(20, token.length())));
            return R.ok("登出成功");
        } catch (Exception e) {
            log.error("用户登出失败", e);
            return R.fail("登出失败");
        }
    }

    @GetMapping("/{userId}/devices")
    @Operation(summary = "获取用户的设备列表", description = "分页获取用户的所有设备")
    public R<Page<DeviceSimpleVO>> getUserDevices(
            @PathVariable String userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            Page<DeviceSimpleVO> result = userService.getUserDevices(userId, page, size);
            return R.ok(result);
        } catch (Exception e) {
            log.error("获取用户设备列表失败: userId={}", userId, e);
            return R.fail(e.getMessage());
        }
    }

    @GetMapping("/{userId}/agents")
    @Operation(summary = "获取用户创建的智能体列表", description = "分页获取用户创建的所有智能体")
    public R<Page<AgentSimpleVO>> getUserAgents(
            @PathVariable String userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            Page<AgentSimpleVO> result = userService.getUserAgents(userId, page, size);
            return R.ok(result);
        } catch (Exception e) {
            log.error("获取用户智能体列表失败: userId={}", userId, e);
            return R.fail(e.getMessage());
        }
    }

    @GetMapping("/list")
    @Operation(summary = "用户列表查询", description = "分页查询用户列表，支持多条件查询")
    public R<Page<UserVO>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            UserQueryDTO queryDTO) {
        try {
            Page<UserVO> result = userService.queryUserList(queryDTO, page, size);
            return R.ok(result);
        } catch (Exception e) {
            log.error("查询用户列表失败", e);
            return R.fail("查询失败");
        }
    }

    @GetMapping("/profile")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的个人信息")
    public R<UserVO> getCurrentUser(@RequestHeader("X-User-Id") String userId) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                return R.fail("用户未登录");
            }

            UserVO userVO = userService.getUserById(userId);
            return R.ok(userVO);
        } catch (Exception e) {
            log.error("获取当前用户信息失败: userId={}", userId, e);
            return R.fail("获取用户信息失败");
        }
    }

    @PutMapping("/profile")
    @Operation(summary = "更新当前用户信息", description = "更新当前登录用户的个人信息")
    public R<UserVO> updateCurrentUser(@RequestHeader("X-User-Id") String userId,
                                       @Valid @RequestBody UserUpdateDTO dto) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                return R.fail("用户未登录");
            }

            UserVO userVO = userService.updateUser(userId, dto);
            return R.ok("更新成功", userVO);
        } catch (Exception e) {
            log.error("更新当前用户信息失败: userId={}", userId, e);
            return R.fail(e.getMessage());
        }
    }

    @PostMapping("/change-password")
    @Operation(summary = "修改密码", description = "修改当前用户的密码")
    public R<Void> changePassword(@RequestHeader("X-User-Id") String userId,
                                  @RequestBody PasswordChangeDTO dto) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                return R.fail("用户未登录");
            }

            // 验证新密码和确认密码是否一致
            if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
                return R.fail("新密码和确认密码不一致");
            }

            // 修改密码
            userService.changePassword(userId, dto.getOldPassword(), dto.getNewPassword());
            
            log.info("用户修改密码成功: userId={}", userId);
            return R.ok("密码修改成功");
        } catch (Exception e) {
            log.error("修改密码失败: userId={}", userId, e);
            return R.fail(e.getMessage());
        }
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 对于多个代理的情况，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    /**
     * 统计用户设备数量
     */
    private Long countUserDevices(String userId) {
        // 这里需要注入DeviceInfoMapper
        // 为了简化，这里返回模拟数据
        return 5L;
    }

    /**
     * 统计用户智能体数量
     */
    private Long countUserAgents(String userId) {
        // 这里需要注入AgentMapper
        // 为了简化，这里返回模拟数据
        return 3L;
    }
}


