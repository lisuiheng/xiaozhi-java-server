package com.github.lisuiheng.astra.sys.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 个人信息Vo
 *
 * @author Michelle.Chung
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProfileUserVo extends SysUserVo {

    /**
     * 用户ID (防止序列化)
     */
    @JsonIgnore
    private static final long serialVersionUID = 1L;
}