package com.github.lisuiheng.astra.sys.mapper;

import com.github.lisuiheng.astra.common.core.mapper.BaseMapperPlus;
import com.github.lisuiheng.astra.sys.domain.entity.SysPost;
import com.github.lisuiheng.astra.sys.domain.vo.SysPostVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 岗位信息表 Mapper接口
 *
 * @author Lion Li
 */
public interface SysPostMapper extends BaseMapperPlus<SysPost, SysPostVo> {

    /**
     * 根据用户ID查询岗位名称
     *
     * @param userId 用户ID
     * @return 岗位名称集合
     */
    @Select("SELECT p.post_name FROM sys_post p INNER JOIN sys_user_post up ON p.post_id = up.post_id WHERE up.user_id = #{userId}")
    List<String> selectPostNamesByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询岗位信息
     *
     * @param userId 用户ID
     * @return 岗位列表
     */
    @Select("SELECT p.post_id, p.dept_id, p.post_code, p.post_name, p.post_category, p.post_sort, p.status, p.remark, p.create_time, d.dept_name FROM sys_post p INNER JOIN sys_user_post up ON p.post_id = up.post_id LEFT JOIN sys_dept d ON p.dept_id = d.dept_id WHERE up.user_id = #{userId}")
    List<SysPostVo> selectPostsByUserId(@Param("userId") Long userId);
}