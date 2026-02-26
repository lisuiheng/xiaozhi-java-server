package com.github.lisuiheng.astra.common.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import cn.hutool.extra.spring.SpringUtil;
import com.github.lisuiheng.astra.common.exception.ServiceException;

import java.util.Set;

/**
 * 校验工具类
 */
public class ValidatorUtils {

    private static final Validator validator = SpringUtil.getBean(Validator.class);

    /**
     * 校验对象
     *
     * @param object 待校验对象
     * @param groups 待校验的组
     * @throws ServiceException 校验不通过抛出异常
     */
    public static void validate(Object object, Class<?>... groups) {
        Set<ConstraintViolation<Object>> validate = validator.validate(object, groups);
        if (!validate.isEmpty()) {
            throw new ServiceException(validate.iterator().next().getMessage());
        }
    }
}