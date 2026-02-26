package com.github.lisuiheng.astra.common.domain.model;

import lombok.Data;

/**
 * 社交登录对象
 */
@Data
public class SocialLoginBody {

    /**
     * 社交登录来源
     */
    private String source;

    /**
     * 社交登录code
     */
    private String socialCode;

    /**
     * 社交登录state
     */
    private String socialState;
}