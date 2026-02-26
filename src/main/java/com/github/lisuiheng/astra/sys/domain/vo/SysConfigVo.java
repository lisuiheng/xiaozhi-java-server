package com.github.lisuiheng.astra.sys.domain.vo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import java.io.Serial;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.io.Serializable;
import java.util.Date;
/**
 * 参数配置视图对象 sys_config
 *
 * @author Qoder
 */
@Data
public class SysConfigVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 参数主键
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long configId;
    /**
     * 参数名称
     */
    private String configName;
    /**
     * 参数键名
     */
    private String configKey;
    /**
     * 参数键值
     */
    private String configValue;
    /**
     * 系统内置（Y是 N否）
     */
    private String configType;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}