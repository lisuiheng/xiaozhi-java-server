package com.github.lisuiheng.astra.sys.domain.vo;
import com.github.lisuiheng.astra.sys.domain.SysClient;
import io.github.linpeilie.annotations.AutoMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import java.io.Serial;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.io.Serializable;
import java.util.List;
/**
 * 授权管理视图对象 sys_client
 *
 * @author Qoder
 * @date 2023-05-15
 */
@Data
@AutoMapper(target = SysClient.class)
public class SysClientVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
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
    private List<String> grantTypeList;
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
    @JsonSerialize(using = ToStringSerializer.class)
    private Long activeTimeout;
    /**
     * token固定超时时间
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long timeout;
    /**
     * 状态（0正常 1停用）
     */
    private String status;
}