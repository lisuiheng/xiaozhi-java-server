package com.github.lisuiheng.astra.sys.domain.bo;

import com.github.lisuiheng.astra.common.core.domain.BaseEntity;
import com.github.lisuiheng.astra.common.core.validate.AddGroup;
import com.github.lisuiheng.astra.common.core.validate.EditGroup;
import com.github.lisuiheng.astra.sys.domain.entity.SysTenantPackage;
import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMapping;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 租户套餐业务对象 sys_tenant_package
 *
 * @author Qoder
 */

@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysTenantPackage.class, reverseConvertGenerate = false)
public class SysTenantPackageBo extends BaseEntity {

    /**
     * 租户套餐id
     */
    @NotNull(message = "租户套餐id不能为空", groups = { EditGroup.class })
    private Long packageId;

    /**
     * 套餐名称
     */
    @NotBlank(message = "套餐名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String packageName;

    /**
     * 关联菜单id
     */
    @AutoMapping(target = "menuIds", expression = "java(source.getMenuIds() != null ? com.github.lisuiheng.astra.common.utils.StringUtils.joinComma(java.util.Arrays.stream(source.getMenuIds()).map(String::valueOf).collect(java.util.stream.Collectors.toList())) : \"\")")
    private Long[] menuIds;

    /**
     * 备注
     */
    private String remark;

    /**
     * 菜单树选择项是否关联显示
     */
    private Boolean menuCheckStrictly;

    /**
     * 状态（0正常 1停用）
     */
    private String status;
}