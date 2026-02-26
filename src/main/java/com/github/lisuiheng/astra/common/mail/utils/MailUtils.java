package com.github.lisuiheng.astra.common.mail.utils;

import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;

/**
 * 邮件工具类
 */
public class MailUtils {

    /**
     * 发送文本邮件
     *
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     * @throws Exception 发送异常
     */
    public static void sendText(String to, String subject, String content) throws Exception {
        // 使用hutool的MailUtil发送邮件
        MailAccount account = new MailAccount();
        // 这里使用默认配置，实际使用时应从配置文件读取
        MailUtil.send(account, to, subject, content, false);
    }
}