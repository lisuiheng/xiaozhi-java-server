package com.github.lisuiheng.astra.sys.domain;
import com.github.lisuiheng.astra.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.lisuiheng.astra.sys.domain.vo.SysClientVo;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.io.Serial;
/**
 * 授权管理对象 sys_client
 *
 * @author Qoder
 * @date 2023-05-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_client")
@AutoMapper(target = SysClientVo.class)
public class SysClient extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(value = "id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 客户端id
     */
    private String clientId;
    /**
     * 客户端key
     */
    private String clientKey;
    /**
     * 客户端秘钥
     */
    private String clientSecret;
    /**
     * 授权类型
     */
    private String grantType;
    /**
     * 设备类型
     */
    private String deviceType;
    /**
     * token活跃超时时间
     */
    private Long activeTimeout;
    /**
     * token固定超时时间
     */
    private Long timeout;
    /**
     * 状态（0正常 1停用）
     */
    private String status;
    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;
}