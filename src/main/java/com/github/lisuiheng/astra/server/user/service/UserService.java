package com.github.lisuiheng.astra.server.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.lisuiheng.astra.common.util.JwtUtil;
import com.github.lisuiheng.astra.server.user.mapper.UserMapper;
import com.github.lisuiheng.astra.server.user.model.dto.UserLoginDTO;
import com.github.lisuiheng.astra.server.user.model.dto.UserQueryDTO;
import com.github.lisuiheng.astra.server.user.model.dto.UserRegisterDTO;
import com.github.lisuiheng.astra.server.user.model.dto.UserUpdateDTO;
import com.github.lisuiheng.astra.server.user.model.entity.User;
import com.github.lisuiheng.astra.server.user.model.vo.AgentSimpleVO;
import com.github.lisuiheng.astra.server.user.model.vo.DeviceSimpleVO;
import com.github.lisuiheng.astra.server.user.model.vo.UserLoginVO;
import com.github.lisuiheng.astra.server.user.model.vo.UserSimpleVO;
import com.github.lisuiheng.astra.server.user.model.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.lisuiheng.astra.common.utils.StringUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserService extends ServiceImpl<UserMapper, User> {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 用户注册
     */
    public UserVO register(UserRegisterDTO dto) {
        // 检查用户名是否已存在
        User existingUser = userMapper.selectOne(Wrappers.<User>lambdaQuery()
                .eq(User::getUsername, dto.getUsername())
                .eq(User::getDeleted, 0));
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 创建新用户
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setId(String.valueOf(System.currentTimeMillis())); // 简单的ID生成策略
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setStatus(1); // 默认启用
        user.setUserType(0); // 默认普通用户
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());

        userMapper.insert(user);

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        return userVO;
    }

    /**
     * 用户登录
     */
    public UserLoginVO login(UserLoginDTO dto) {
        // 根据用户名查找用户
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery()
                .eq(User::getUsername, dto.getUsername())
                .eq(User::getDeleted, 0));
        
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 更新最后登录时间和IP
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(dto.getClientIp());
        userMapper.updateById(user);

        // 生成Token
        UserLoginVO loginVO = new UserLoginVO();
        loginVO.setToken(JwtUtil.generateToken(user.getId(), user.getUsername()));
        
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        loginVO.setUserInfo(userVO);

        return loginVO;
    }

    /**
     * 根据ID获取用户信息
     */
    public UserVO getUserById(String userId) {
        User user = userMapper.selectById(userId);
        if (user == null || user.getDeleted() == 1) {
            throw new RuntimeException("用户不存在");
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        return userVO;
    }

    /**
     * 更新用户信息
     */
    public UserVO updateUser(String userId, UserUpdateDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null || user.getDeleted() == 1) {
            throw new RuntimeException("用户不存在");
        }

        // 更新用户信息
        BeanUtils.copyProperties(dto, user);
        user.setUpdatedTime(LocalDateTime.now());
        userMapper.updateById(user);

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        return userVO;
    }

    /**
     * 查询用户列表（管理端）
     */
    public Page<UserVO> queryUserList(UserQueryDTO queryDTO, Integer page, Integer size) {
        // 构建查询条件
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(User::getDeleted, 0);

        if (queryDTO != null) {
            if (StringUtils.isNotBlank(queryDTO.getUsername())) {
                wrapper.like(User::getUsername, queryDTO.getUsername());
            }
            if (StringUtils.isNotBlank(queryDTO.getNickname())) {
                wrapper.like(User::getNickname, queryDTO.getNickname());
            }
            if (StringUtils.isNotBlank(queryDTO.getEmail())) {
                wrapper.like(User::getEmail, queryDTO.getEmail());
            }
            if (StringUtils.isNotBlank(queryDTO.getPhone())) {
                wrapper.like(User::getPhone, queryDTO.getPhone());
            }
            if (queryDTO.getStatus() != null) {
                wrapper.eq(User::getStatus, queryDTO.getStatus());
            }
            if (queryDTO.getUserType() != null) {
                wrapper.eq(User::getUserType, queryDTO.getUserType());
            }
            if (StringUtils.isNotBlank(queryDTO.getStartTime())) {
                wrapper.ge(User::getCreatedTime, queryDTO.getStartTime());
            }
            if (StringUtils.isNotBlank(queryDTO.getEndTime())) {
                wrapper.le(User::getCreatedTime, queryDTO.getEndTime());
            }
        }

        wrapper.orderByDesc(User::getCreatedTime);

        // 执行查询
        Page<User> userPage = userMapper.selectPage(new Page<>(page, size), wrapper);

        // 转换为VO
        Page<UserVO> voPage = new Page<>();
        BeanUtils.copyProperties(userPage, voPage);

        List<UserVO> voList = userPage.getRecords().stream()
            .map(user -> {
                UserVO vo = new UserVO();
                BeanUtils.copyProperties(user, vo);

                // 统计设备数量
                Long deviceCount = countUserDevices(user.getId());
                vo.setDeviceCount(deviceCount);

                // 统计智能体数量
                Long agentCount = countUserAgents(user.getId());
                vo.setAgentCount(agentCount);

                return vo;
            })
            .collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 重置用户密码
     */
    @Transactional
    public void resetPassword(String userId, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null || user.getDeleted() == 1) {
            throw new RuntimeException("用户不存在");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        int result = userMapper.updateById(user);
        if (result <= 0) {
            throw new RuntimeException("重置密码失败");
        }

        log.info("重置用户密码成功: userId={}", userId);
    }

    /**
     * 修改用户密码（验证旧密码）
     */
    @Transactional
    public void changePassword(String userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null || user.getDeleted() == 1) {
            throw new RuntimeException("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("旧密码不正确");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        int result = userMapper.updateById(user);
        if (result <= 0) {
            throw new RuntimeException("修改密码失败");
        }

        log.info("用户修改密码成功: userId={}", userId);
    }

    /**
     * 获取用户的设备列表
     */
    public Page<DeviceSimpleVO> getUserDevices(String userId, Integer page, Integer size) {
        // TODO: 需要实现获取用户设备列表的逻辑
        // 这里应该调用deviceService来获取用户的设备列表
        return new Page<>();
    }

    /**
     * 获取用户创建的智能体列表
     */
    public Page<AgentSimpleVO> getUserAgents(String userId, Integer page, Integer size) {
        // TODO: 需要实现获取用户智能体列表的逻辑
        // 这里应该调用agentService来获取用户创建的智能体列表
        return new Page<>();
    }

    /**
     * 启用/禁用用户
     */
    @Transactional
    public void toggleUserStatus(String userId, Integer status) {
        if (status != 0 && status != 1) {
            throw new RuntimeException("状态值必须是0或1");
        }

        User user = userMapper.selectById(userId);
        if (user == null || user.getDeleted() == 1) {
            throw new RuntimeException("用户不存在");
        }

        user.setStatus(status);
        int result = userMapper.updateById(user);
        if (result <= 0) {
            throw new RuntimeException("更新用户状态失败");
        }

        log.info("更新用户状态成功: userId={}, status={}", userId, status);
    }

    /**
     * 删除用户
     */
    @Transactional
    public void deleteUser(String userId) {
        User user = userMapper.selectById(userId);
        if (user == null || user.getDeleted() == 1) {
            throw new RuntimeException("用户不存在");
        }

        user.setDeleted(1);
        int result = userMapper.updateById(user);
        if (result <= 0) {
            throw new RuntimeException("删除用户失败");
        }

        log.info("删除用户成功: userId={}", userId);
    }

    /**
     * 批量删除用户
     */
    @Transactional
    public void batchDeleteUsers(List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        int result = userMapper.update(null,
            new LambdaUpdateWrapper<User>()
                .set(User::getDeleted, 1)
                .in(User::getId, userIds)
        );

        if (result <= 0) {
            throw new RuntimeException("批量删除用户失败");
        }

        log.info("批量删除用户成功: count={}, userIds={}", result, userIds);
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