package com.github.lisuiheng.astra.sys.mapper;

import com.github.lisuiheng.astra.common.core.mapper.BaseMapperPlus;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.github.lisuiheng.astra.sys.domain.bo.SysUserBo;
import com.github.lisuiheng.astra.sys.domain.entity.SysUser;
import com.github.lisuiheng.astra.sys.domain.vo.SysUserVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户表 Mapper接口
 *
 * @author Michelle.Chung
 */
public interface SysUserMapper extends BaseMapperPlus<SysUser, SysUserVo> {

    /**
     * 根据用户名查询用户信息
     *
     * @param userName 用户名
     * @return 用户信息
     */
    @Select("SELECT * FROM sys_user WHERE user_name = #{userName} AND del_flag = '0'")
    SysUser selectUserByUserName(String userName);

    /**
     * 分页查询用户列表
     *
     * @param page 分页参数
     * @param userName 用户名
     * @param status 状态
     * @param phonenumber 手机号
     * @return 用户信息集合
     */
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.github.lisuiheng.astra.sys.domain.vo.SysUserVo> selectPageUserList(
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.github.lisuiheng.astra.sys.domain.entity.SysUser> page,
            @org.apache.ibatis.annotations.Param("userName") String userName,
            @org.apache.ibatis.annotations.Param("status") String status,
            @org.apache.ibatis.annotations.Param("phonenumber") String phonenumber,
            @org.apache.ibatis.annotations.Param("params") java.util.Map<String, Object> params);

    /**
     * 根据条件分页查询已配用户角色列表
     *
     * @param page 分页参数
     * @param userBo    用户信息
     * @param queryWrapper 查询条件
     * @return 用户信息集合信息
     */
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysUserVo> selectAllocatedList(@Param("page") com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysUser> page, @Param("userBo") SysUserBo userBo, @Param(Constants.WRAPPER) com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<SysUser> queryWrapper);

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param page 分页参数
     * @param userBo    用户信息
     * @param queryWrapper 查询条件
     * @return 用户信息集合信息
     */
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysUserVo> selectUnallocatedList(@Param("page") com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysUser> page, @Param("userBo") SysUserBo userBo, @Param(Constants.WRAPPER) com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<SysUser> queryWrapper);

    /**
     * 校验手机号码是否唯一
     *
     * @param phonenumber 手机号码
     * @param userId 用户ID
     * @return 结果
     */
    @Select("SELECT COUNT(1) FROM sys_user WHERE phonenumber = #{phonenumber} AND user_id != #{userId}")
    int checkPhoneUnique(@Param("phonenumber") String phonenumber, @Param("userId") Long userId);

    /**
     * 校验邮箱是否唯一
     *
     * @param email 邮箱
     * @param userId 用户ID
     * @return 结果
     */
    @Select("SELECT COUNT(1) FROM sys_user WHERE email = #{email} AND user_id != #{userId}")
    int checkEmailUnique(@Param("email") String email, @Param("userId") Long userId);

    /**
     * 校验用户名是否唯一
     *
     * @param userName 用户名
     * @param userId 用户ID
     * @return 结果
     */
    @Select("SELECT COUNT(1) FROM sys_user WHERE user_name = #{userName} AND user_id != #{userId}")
    int checkUserNameUnique(@Param("userName") String userName, @Param("userId") Long userId);

    /**
     * 根据部门ID查询用户列表
     *
     * @param deptId 部门ID
     * @return 用户列表
     */
    List<SysUser> selectUserListByDeptId(@Param("deptId") Long deptId);
}