package com.github.lisuiheng.astra.sys.service;

import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.sys.domain.bo.SysUserBo;
import com.github.lisuiheng.astra.sys.domain.vo.SysRoleVo;
import com.github.lisuiheng.astra.sys.domain.vo.SysUserInfoVo;
import com.github.lisuiheng.astra.sys.domain.vo.SysUserVo;

import java.util.List;
import java.util.Set;

/**
 * 用户 业务层
 *
 * @author Lion Li
 */
public interface ISysUserService {

    /**
     * 根据用户ID查询用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    SysUserVo selectUserById(Long userId);

    /**
     * 根据用户ID查询用户角色信息
     *
     * @param userId 用户ID
     * @return 角色信息列表
     */
    List<SysRoleVo> selectRolesByUserId(Long userId);

    /**
     * 根据用户ID查询用户菜单权限
     *
     * @param userId 用户ID
     * @return 菜单权限集合
     */
    Set<String> selectMenuListByUserId(Long userId);

    /**
     * 根据条件分页查询已分配用户角色列表
     *
     * @param user      用户信息
     * @param pageQuery 分页
     * @return 用户信息集合信息
     */
    TableDataInfo<SysUserVo> selectAllocatedList(SysUserBo user, PageQuery pageQuery);

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user      用户信息
     * @param pageQuery 分页
     * @return 用户信息集合信息
     */
    TableDataInfo<SysUserVo> selectUnallocatedList(SysUserBo user, PageQuery pageQuery);

    /**
     * 修改用户个人信息
     *
     * @param user 用户信息
     * @return 结果
     */
    int updateUserProfile(SysUserBo user);

    /**
     * 根据条件分页查询用户列表
     *
     * @param user      用户信息
     * @param pageQuery 分页
     * @return 用户信息集合信息
     */
    TableDataInfo<SysUserVo> selectPageUserList(SysUserBo user, PageQuery pageQuery);

    /**
     * 根据用户ID查询用户完整信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    SysUserInfoVo selectSysUserInfoByUserId(Long userId);

    /**
     * 修改用户头像
     *
     * @param userId 用户ID
     * @param avatar 头像地址
     * @return 结果
     */
    boolean updateUserAvatar(Long userId, Long avatar);

    /**
     * 重置用户密码
     *
     * @param userId   用户ID
     * @param password 密码
     * @return 结果
     */
    int resetUserPwd(Long userId, String password);

    /**
     * 根据用户ID查询用户角色组
     *
     * @param userId 用户ID
     * @return 结果
     */
    String selectUserRoleGroup(Long userId);

    /**
     * 根据用户ID查询用户岗位组
     *
     * @param userId 用户ID
     * @return 结果
     */
    String selectUserPostGroup(Long userId);

    /**
     * 校验用户手机号是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    boolean checkPhoneUnique(SysUserBo user);

    /**
     * 校验用户邮箱是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    boolean checkEmailUnique(SysUserBo user);

    /**
     * 修改用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    int updateUser(SysUserBo user);

    /**
     * 校验用户是否允许操作
     *
     * @param userId 用户ID
     */
    void checkUserAllowed(Long userId);

    /**
     * 校验用户是否有数据权限
     *
     * @param userId 用户id
     */
    void checkUserDataScope(Long userId);

    /**
     * 校验用户名是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    boolean checkUserNameUnique(SysUserBo user);

    /**
     * 通过用户ID删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    int deleteUserById(Long userId);

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    int deleteUserByIds(Long[] userIds);

    /**
     * 根据部门ID获取用户列表
     *
     * @param deptId 部门ID
     * @return 用户信息集合
     */
    List<SysUserVo> selectUserListByDeptId(Long deptId);
}