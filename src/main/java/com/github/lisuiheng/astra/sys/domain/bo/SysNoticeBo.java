package com.github.lisuiheng.astra.sys.domain.bo;

import com.github.lisuiheng.astra.common.core.validate.AddGroup;
import com.github.lisuiheng.astra.common.core.validate.EditGroup;
import com.github.lisuiheng.astra.sys.domain.SysNotice;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通知公告业务对象 sys_notice
 *
 * @author ruoyi
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class SysNoticeBo extends SysNotice {

    /**
     * 公告标题
     */
    @NotBlank(message = "公告标题不能为空", groups = { AddGroup.class, EditGroup.class })
    private String noticeTitle;

    /**
     * 公告类型（1通知 2公告）
     */
    @NotBlank(message = "公告类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private String noticeType;

    /**
     * 公告内容
     */
    private String noticeContent;

    /**
     * 公告状态（0正常 1关闭）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人姓名
     */
    private String createByName;
}