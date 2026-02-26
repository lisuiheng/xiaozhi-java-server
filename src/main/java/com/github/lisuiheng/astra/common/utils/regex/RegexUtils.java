package com.github.lisuiheng.astra.common.utils.regex;


import cn.hutool.core.util.ReUtil;
import com.github.lisuiheng.astra.common.constant.RegexConstants;

/**
 * 正则相关工具类
 *
 * @author xiaozhi
 */
public final class RegexUtils extends ReUtil {

    /**
     * 从输入字符串中提取匹配的部分，如果没有匹配则返回默认值
     *
     * @param input        要提取的输入字符串
     * @param regex        用于匹配的正则表达式，可以使用 {@link RegexConstants} 中定义的常量
     * @param defaultInput 如果没有匹配时返回的默认值
     * @return 如果找到匹配的部分，则返回匹配的部分，否则返回默认值
     */
    public static String extractFromString(String input, String regex, String defaultInput) {
        try {
            String str = ReUtil.get(regex, input, 1);
            return str == null ? defaultInput : str;
        } catch (Exception e) {
            return defaultInput;
        }
    }

    /**
     * 检查输入字符串是否与给定的正则表达式匹配
     *
     * @param regex 正则表达式
     * @param input 输入字符串
     * @return 如果匹配返回true，否则返回false
     */
    public static boolean isMatch(String regex, String input) {
        return ReUtil.isMatch(regex, input);
    }
}