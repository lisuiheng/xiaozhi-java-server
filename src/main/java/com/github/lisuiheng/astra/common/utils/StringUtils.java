package com.github.lisuiheng.astra.common.utils;

import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 */
public class StringUtils {

    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");

    /**
     * 空字符串
     */
    public static final String EMPTY = "";

    /**
     * 替换字符串中的某些字符序列
     * 
     * @param text 文本
     * @param searchList 查找列表
     * @param replacementList 替换列表
     * @return 结果
     */
    public static String replaceEach(final String text, final String[] searchList, final String[] replacementList) {
        if (text == null || text.isEmpty() || searchList == null || searchList.length == 0 || replacementList == null || replacementList.length == 0) {
            return text;
        }
        
        if (searchList.length != replacementList.length) {
            throw new IllegalArgumentException("Search and Replace array lengths don't match: " + searchList.length + " vs " + replacementList.length);
        }
        
        String result = text;
        for (int i = 0; i < searchList.length; i++) {
            if (searchList[i] != null && replacementList[i] != null) {
                result = result.replace(searchList[i], replacementList[i]);
            }
        }
        
        return result;
    }
    
    /**
     * 按指定分隔符分割字符串
     * 
     * @param str 待分割的字符串
     * @param separator 分隔符
     * @return 分割后的字符串数组
     */
    public static String[] split(final String str, final String separator) {
        if (str == null) {
            return null;
        }
        if (separator == null || separator.isEmpty()) {
            return new String[]{str};
        }
        return str.split(separator);
    }
    
    /**
     * 忽略大小写的字符串索引查找
     * 
     * @param str 源字符串
     * @param searchStr 要查找的字符串
     * @return 找到的索引位置，未找到返回-1
     */
    public static int indexOfIgnoreCase(final String str, final String searchStr) {
        if (str == null || searchStr == null) {
            return -1;
        }
        final int len = searchStr.length();
        final int max = str.length() - len;
        for (int i = 0; i <= max; i++) {
            if (str.regionMatches(true, i, searchStr, 0, len)) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * 格式化字符串
     * 
     * @param str 待格式化的字符串
     * @param args 参数
     * @return 格式化后的字符串
     */
    public static String format(String str, Object... args) {
        if (str == null || args == null || args.length == 0) {
            return str;
        }
        
        String result = str;
        for (int i = 0; i < args.length; i++) {
            result = result.replace("{" + i + "}", args[i] == null ? "" : args[i].toString());
        }
        
        return result;
    }
    
    /**
     * 路径分隔符
     */
    public static final String SEPARATOR = "/";
    
    /**
     * 判断一个字符串是否为 null 或空
     *
     * @param str 待检查的字符串
     * @return 如果字符串为 null 或空则返回 true，否则返回 false
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 判断一个字符串是否非 null 且非空
     *
     * @param str 待检查的字符串
     * @return 如果字符串非 null 且非空则返回 true，否则返回 false
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 判断一个字符串是否为空白（null、空字符串或仅包含空白字符）
     *
     * @param str 待检查的字符串
     * @return 如果字符串为空白则返回 true，否则返回 false
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * 判断一个字符串是否非空白（非 null、非空字符串且不只包含空白字符）
     *
     * @param str 待检查的字符串
     * @return 如果字符串非空白则返回 true，否则返回 false
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
    
    /**
     * 检查字符串是否包含实际文本内容
     * 等价于 Spring Framework 的 StringUtils.hasText() 方法
     *
     * @param str 要检查的字符串
     * @return 如果字符串不为null、长度大于0且包含非空白字符则返回true
     */
    public static boolean hasText(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        // 检查是否包含非空白字符
        int length = str.length();
        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }

        return false;
    }

    /**
     * 检查字符串是否不为空且不为null
     * 等价于 Spring Framework 的 StringUtils.hasLength() 方法
     *
     * @param str 要检查的字符串
     * @return 如果字符串不为null且长度大于0则返回true
     */
    public static boolean hasLength(String str) {
        return str != null && !str.isEmpty();
    }

    /**
     * 去除字符串两端的空白字符，如果为null则返回null
     */
    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    /**
     * 去除字符串两端空白字符，如果为null则返回空字符串
     */
    public static String trimToEmpty(String str) {
        return str == null ? "" : str.trim();
    }

    /**
     * 去除字符串两端空白字符，如果为null或去除后为空则返回null
     */
    public static String trimToNull(String str) {
        if (str == null) {
            return null;
        }
        String trimmed = str.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /**
     * 检查两个字符串是否相等
     *
     * @param str1 第一个字符串
     * @param str2 第二个字符串
     * @return 如果两个字符串相等则返回 true，否则返回 false
     */
    public static boolean equals(CharSequence str1, CharSequence str2) {
        if (str1 == null && str2 == null) {
            return true;
        }
        if (str1 == null || str2 == null) {
            return false;
        }
        return str1.toString().equals(str2.toString());
    }
    
    /**
     * 判断两个字符串是否相等（处理null值）
     */
    public static boolean equals(String str1, String str2) {
        if (str1 == str2) {
            return true;
        }
        if (str1 == null || str2 == null) {
            return false;
        }
        return str1.equals(str2);
    }

    /**
     * 忽略大小写的字符串相等比较
     *
     * @param str1 第一个字符串
     * @param str2 第二个字符串
     * @return 如果两个字符串相等（忽略大小写）则返回 true，否则返回 false
     */
    public static boolean equalsIgnoreCase(CharSequence str1, CharSequence str2) {
        if (str1 == null && str2 == null) {
            return true;
        }
        if (str1 == null || str2 == null) {
            return false;
        }
        return str1.toString().equalsIgnoreCase(str2.toString());
    }
    
    /**
     * 判断两个字符串是否忽略大小写相等（处理null值）
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        if (str1 == str2) {
            return true;
        }
        if (str1 == null || str2 == null) {
            return false;
        }
        return str1.equalsIgnoreCase(str2);
    }
    
    /**
     * 检查字符串是否与数组中的任意一个字符串相等
     *
     * @param str 待检查的字符串
     * @param strArray 字符串数组
     * @return 如果与数组中的任意一个字符串相等则返回true，否则返回false
     */
    public static boolean equalsAny(String str, String... strArray) {
        if (str == null || strArray == null || strArray.length == 0) {
            return false;
        }
        for (String s : strArray) {
            if (equals(str, s)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 检查字符串是否与数组中的任意一个字符串相等（忽略大小写，可变参数版本）
     *
     * @param str 待检查的字符串
     * @param strArray 字符串数组
     * @return 如果与数组中的任意一个字符串相等（忽略大小写）则返回true，否则返回false
     */
    public static boolean equalsAnyIgnoreCase(String str, String... strArray) {
        if (str == null || strArray == null || strArray.length == 0) {
            return false;
        }
        for (String s : strArray) {
            if (equalsIgnoreCase(str, s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查字符串是否包含指定的子字符串
     *
     * @param str       源字符串
     * @param substring 子字符串
     * @return 如果包含子字符串则返回 true，否则返回 false
     */
    public static boolean contains(CharSequence str, CharSequence substring) {
        if (str == null || substring == null) {
            return false;
        }
        return str.toString().contains(substring.toString());
    }

    /**
     * 检查字符串是否包含指定的子字符串（忽略大小写）
     *
     * @param str       源字符串
     * @param substring 子字符串
     * @return 如果包含子字符串（忽略大小写）则返回 true，否则返回 false
     */
    public static boolean containsIgnoreCase(CharSequence str, CharSequence substring) {
        if (str == null || substring == null) {
            return false;
        }
        return indexOfIgnoreCase(str.toString(), substring.toString()) >= 0;
    }

    /**
     * 移除字符串中的指定字符
     *
     * @param str    源字符串
     * @param remove 要移除的字符
     * @return 移除后的字符串
     */
    public static String remove(CharSequence str, char remove) {
        if (str == null || str.length() == 0) {
            return str == null ? null : str.toString();
        }
        
        char[] chars = str.toString().toCharArray();
        StringBuilder sb = new StringBuilder(chars.length);
        
        for (char c : chars) {
            if (c != remove) {
                sb.append(c);
            }
        }
        
        return sb.toString();
    }

    /**
     * 移除字符串中的指定字符串
     *
     * @param str    源字符串
     * @param remove 要移除的字符串
     * @return 移除后的字符串
     */
    public static String remove(CharSequence str, CharSequence remove) {
        if (str == null || remove == null || str.length() == 0 || remove.length() == 0) {
            return str == null ? null : str.toString();
        }
        
        String source = str.toString();
        String target = remove.toString();
        
        return source.replace(target, "");
    }
    
    /**
     * 检查列表中是否包含指定字符串
     *
     * @param list 源列表
     * @param str  要查找的字符串
     * @return 如果列表中包含指定字符串则返回 true，否则返回 false
     */
    public static boolean contains(List<String> list, String str) {
        if (list == null || str == null) {
            return false;
        }
        return list.contains(str);
    }
    
    /**
     * 驼峰转下划线命名
     */
    public static String toUnderScoreCase(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        // 前置字符是否大写
        boolean preCharIsUpperCase = true;
        // 当前字符是否大写
        boolean curreCharIsUpperCase = true;
        // 下一字符是否大写
        boolean nexteCharIsUpperCase = true;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (i > 0) {
                preCharIsUpperCase = Character.isUpperCase(str.charAt(i - 1));
            } else {
                preCharIsUpperCase = false;
            }

            curreCharIsUpperCase = Character.isUpperCase(c);

            if (i < str.length() - 1) {
                nexteCharIsUpperCase = Character.isUpperCase(str.charAt(i + 1));
            }

            if (preCharIsUpperCase && curreCharIsUpperCase && !nexteCharIsUpperCase) {
                sb.append("_");
            } else if ((i != 0 && !preCharIsUpperCase) && curreCharIsUpperCase) {
                sb.append("_");
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }
    
    /**
     * 按指定分隔符分割字符串为列表
     * 
     * @param str 待分割的字符串
     * @param separator 分隔符
     * @return 分割后的字符串列表
     */
    public static List<String> splitList(final String str, final String separator) {
        if (str == null) {
            return null;
        }
        String[] parts = str.split(separator);
        java.util.List<String> result = new java.util.ArrayList<>();
        for (String part : parts) {
            if (part != null) {
                result.add(part);
            }
        }
        return result;
    }
    
    /**
     * 按逗号分割字符串为列表
     * 
     * @param str 待分割的字符串
     * @return 分割后的字符串列表
     */
    public static List<String> splitList(final String str) {
        if (str == null) {
            return null;
        }
        return splitList(str, ",");
    }

    // 辅助方法：字节数组转Hex
    public static String bytesToHex(byte[] bytes, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(String.format("%02X ", bytes[i]));
        }
        return sb.toString();
    }

    // 生成指定长度的随机数
    public static Integer generateRandomNumber(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive");
        }
        Random random = new Random();
        int min = (int) Math.pow(10, length - 1);
        int max = (int) Math.pow(10, length) - 1;
        return random.nextInt(max - min + 1) + min;
    }

    /**
     * 生成指定长度的随机字符串
     *
     * @param length 字符串长度
     * @return 随机字符串
     */
    public static String generateRandomString(int length) {
        if (length <= 0) {
            return "";
        }

        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }

        return sb.toString();
    }
    
    /**
     * 检查字符串是否为URL（http(s)://开头）
     *
     * @param link 链接
     * @return 结果
     */
    public static boolean ishttp(String link) {
        if (link == null) {
            return false;
        }
        // 检查是否以 http:// 或 https:// 开头
        return link.toLowerCase().startsWith("http://") || link.toLowerCase().startsWith("https://");
    }
    
    /**
     * 首字母大写
     *
     * @param str 字符串
     * @return 首字母大写后的字符串
     */
    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    /**
     * 检查对象是否不为null
     *
     * @param obj 待检查的对象
     * @return 如果对象不为null则返回true，否则返回false
     */
    public static boolean isNotNull(Object obj) {
        return obj != null;
    }
    
    /**
     * 检查Long对象是否不为null
     *
     * @param obj 待检查的Long对象
     * @return 如果Long对象不为null则返回true，否则返回false
     */
    public static boolean isNotNull(Long obj) {
        return obj != null;
    }
    
    /**
     * 检查字符串是否以数组中的任意一个字符串结尾
     *
     * @param str 待检查的字符串
     * @param suffixes 后缀字符串数组
     * @return 如果与数组中的任意一个字符串结尾则返回true，否则返回false
     */
    public static boolean endsWithAny(String str, String... suffixes) {
        if (str == null || suffixes == null || suffixes.length == 0) {
            return false;
        }
        for (String suffix : suffixes) {
            if (str.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 使用逗号连接字符串列表
     *
     * @param list 字符串列表
     * @return 用逗号连接的字符串
     */
    public static String joinComma(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return String.join(",", list);
    }
    
    /**
     * 使用逗号连接字符串数组
     *
     * @param array 字符串数组
     * @return 用逗号连接的字符串
     */
    public static String joinComma(String[] array) {
        if (array == null || array.length == 0) {
            return "";
        }
        return String.join(",", array);
    }
    
    /**
     * 检查一个字符串是否包含另一个字符串中的任何字符
     *
     * @param str 要检查的字符串
     * @param searchChars 要搜索的字符
     * @return 如果str包含searchChars中的任何字符则返回true，否则返回false
     */
    public static boolean containsAny(CharSequence str, CharSequence searchChars) {
        if (isEmpty(str) || isEmpty(searchChars)) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (searchChars.toString().indexOf(ch) >= 0) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 检查一个字符串是否为空
     *
     * @param str 要检查的字符串
     * @return 如果字符串为null或空则返回true，否则返回false
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }
    
    /**
     * 检查字符串是否以数组中的任意一个字符串开头（忽略大小写）
     *
     * @param str 待检查的字符串
     * @param prefixes 前缀字符串数组
     * @return 如果与数组中的任意一个字符串开头（忽略大小写）则返回true，否则返回false
     */
    public static boolean startWithAnyIgnoreCase(String str, String... prefixes) {
        if (str == null || prefixes == null || prefixes.length == 0) {
            return false;
        }
        for (String prefix : prefixes) {
            if (str.toLowerCase().startsWith(prefix.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 从字符串末尾移除指定的字符序列
     *
     * @param str 要处理的字符串
     * @param stripChars 要移除的字符序列，如果为null则视为移除空白字符
     * @return 处理后的字符串
     */
    public static String stripEnd(String str, String stripChars) {
        int end;
        if (str == null || (end = str.length()) == 0) {
            return str;
        }

        if (stripChars == null) {
            // 移除空白字符
            while (end != 0 && Character.isWhitespace(str.charAt(end - 1))) {
                end--;
            }
        } else {
            // 移除指定字符
            int stripLen = stripChars.length();
            if (stripLen == 0) {
                return str;
            }
            while (end != 0 && stripChars.indexOf(str.charAt(end - 1)) != -1) {
                end--;
            }
        }
        return str.substring(0, end);
    }
    
    /**
     * 如果字符串为null或空白，则返回默认值，否则返回原字符串
     *
     * @param str 被检查的字符串
     * @param defaultStr 默认值
     * @return 非空白的字符串或默认值
     */
    public static String blankToDefault(String str, String defaultStr) {
        return isBlank(str) ? defaultStr : str;
    }
    
    /**
     * 检查字符串是否与多个指定值之一相等
     *
     * @param str 待检查的字符串
     * @param str1 第一个比较字符串
     * @param str2 第二个比较字符串
     * @return 如果与任一字符串相等则返回true，否则返回false
     */
    public static boolean equalsAny(String str, String str1, String str2) {
        return equals(str, str1) || equals(str, str2);
    }

    /**
     * 获取字符串中最后一次出现分隔符之后的部分
     * 
     * @param str 源字符串
     * @param separator 分隔符
     * @return 最后一次出现分隔符之后的部分，如果没有找到分隔符则返回空字符串
     */
    public static String substringAfterLast(String str, String separator) {
        if (isEmpty(str) || isEmpty(separator)) {
            return "";
        }
        int pos = str.lastIndexOf(separator);
        if (pos == -1 || pos == (str.length() - separator.length())) {
            return "";
        }
        return str.substring(pos + separator.length());
    }
}