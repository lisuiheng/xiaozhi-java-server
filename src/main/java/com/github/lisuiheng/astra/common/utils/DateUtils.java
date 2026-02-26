package com.github.lisuiheng.astra.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 时间工具类
 *
 * @author
 */
public class DateUtils {

    /**
     * 计算两个时间点的差值（天、小时、分钟、秒），当值为0时不显示该单位
     *
     * @param endDate 结束时间
     * @param nowDate 当前时间
     * @return 时间差字符串，格式为 "x天 x小时 x分钟 x秒"，若为 0 则不显示
     */
    public static String getTimeDifference(Date endDate, Date nowDate) {
        long diffInMillis = endDate.getTime() - nowDate.getTime();
        long day = TimeUnit.MILLISECONDS.toDays(diffInMillis);
        long hour = TimeUnit.MILLISECONDS.toHours(diffInMillis) % 24;
        long min = TimeUnit.MILLISECONDS.toMinutes(diffInMillis) % 60;
        long sec = TimeUnit.MILLISECONDS.toSeconds(diffInMillis) % 60;
        // 构建时间差字符串，条件是值不为0才显示
        StringBuilder result = new StringBuilder();
        if (day > 0) {
            result.append(String.format("%d天 ", day));
        }
        if (hour > 0) {
            result.append(String.format("%d小时 ", hour));
        }
        if (min > 0) {
            result.append(String.format("%d分钟 ", min));
        }
        if (sec > 0) {
            result.append(String.format("%d秒", sec));
        }
        return result.length() > 0 ? result.toString().trim() : "0秒";
    }

    /**
     * 获取当前日期和时间
     *
     * @return 当前日期和时间的 Date 对象表示
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * 将指定日期格式化为 YYYY-MM-DD 格式的字符串
     *
     * @param date 要格式化的日期对象
     * @return 格式化后的日期字符串
     */
    public static String formatDate(final Date date) {
        return parseDateToStr("yyyy-MM-dd", date);
    }

    /**
     * 将指定日期格式化为 YYYY-MM-DD HH:MM:SS 格式的字符串
     *
     * @param date 要格式化的日期对象
     * @return 格式化后的日期时间字符串
     */
    public static String formatDateTime(final Date date) {
        return parseDateToStr("yyyy-MM-dd HH:mm:ss", date);
    }

    /**
     * 将指定日期按照指定格式进行格式化
     *
     * @param format 要使用的日期时间格式，例如"YYYY-MM-DD HH:MM:SS"
     * @param date   要格式化的日期对象
     * @return 格式化后的日期时间字符串
     */
    public static String parseDateToStr(final String format, final Date date) {
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 将指定格式的日期时间字符串转换为 Date 对象
     *
     * @param format 要解析的日期时间格式，例如"YYYY-MM-DD HH:MM:SS"
     * @param ts     要解析的日期时间字符串
     * @return 解析后的 Date 对象
     * @throws RuntimeException 如果解析过程中发生异常
     */
    public static Date parseDateTime(final String format, final String ts) {
        try {
            return new SimpleDateFormat(format).parse(ts);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将 LocalDateTime 对象转换为 Date 对象
     *
     * @param temporalAccessor 要转换的 LocalDateTime 对象
     * @return 转换后的 Date 对象
     */
    public static Date toDate(LocalDateTime temporalAccessor) {
        ZonedDateTime zdt = temporalAccessor.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /**
     * 将 LocalDate 对象转换为 Date 对象
     *
     * @param temporalAccessor 要转换的 LocalDate 对象
     * @return 转换后的 Date 对象
     */
    public static Date toDate(LocalDate temporalAccessor) {
        LocalDateTime localDateTime = LocalDateTime.of(temporalAccessor, LocalTime.of(0, 0, 0));
        ZonedDateTime zdt = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }
}