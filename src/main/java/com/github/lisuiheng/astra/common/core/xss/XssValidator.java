package com.github.lisuiheng.astra.common.core.xss;

import cn.hutool.core.util.ObjectUtil;
import com.github.lisuiheng.astra.common.utils.StringUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 自定义xss校验注解
 *
 * @author ruoyi
 */
public class XssValidator implements ConstraintValidator<Xss, String>
{
    @Override
    public void initialize(Xss constraintAnnotation) {
        // 初始化方法，如果需要可以在这里进行初始化
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context)
    {
        if (ObjectUtil.isNull(value))
        {
            return true;
        }
        return !StringUtils.containsIgnoreCase(value, "<script") &&
                !StringUtils.containsIgnoreCase(value, "javascript:") &&
                !StringUtils.containsIgnoreCase(value, "onerror") &&
                !StringUtils.containsIgnoreCase(value, "onload");
    }
}