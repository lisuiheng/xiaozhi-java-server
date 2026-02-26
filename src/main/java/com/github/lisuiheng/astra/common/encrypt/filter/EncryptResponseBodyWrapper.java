package com.github.lisuiheng.astra.common.encrypt.filter;

import cn.hutool.core.util.CharsetUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import com.github.lisuiheng.astra.common.encrypt.utils.EncryptUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * 加密响应参数工具类
 *
 * @author wdhcr
 */
public class EncryptResponseBodyWrapper extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    private PrintWriter printWriter;

    public EncryptResponseBodyWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return new ServletOutputStream() {
            @Override
            public void write(int b) throws IOException {
                byteArrayOutputStream.write(b);
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setWriteListener(WriteListener listener) {
            }
        };
    }

    @Override
    public PrintWriter getWriter() {
        if (this.printWriter == null) {
            this.printWriter = new PrintWriter(byteArrayOutputStream);
        }
        return this.printWriter;
    }

    /**
     * 获取加密内容
     *
     * @param response 响应
     * @param publicKey 公钥
     * @param headerFlag 头部标识
     * @return 加密后内容
     */
    public String getEncryptContent(HttpServletResponse response, String publicKey, String headerFlag) {
        String content = byteArrayOutputStream.toString(StandardCharsets.UTF_8);
        // 生成 AES 密钥
        String aesKey = EncryptUtils.encryptByBase64(java.util.UUID.randomUUID().toString().replaceAll("-", ""));
        // 用 RSA 加密 AES 密钥
        String encryptAesKey;
        try {
            encryptAesKey = EncryptUtils.encryptByAes(aesKey, publicKey);
            // 设置头部
            response.setHeader(headerFlag, encryptAesKey);
            // 加密内容
            return EncryptUtils.encryptByAes(content, aesKey);
        } catch (Exception e) {
            // 如果加密失败，直接返回原始内容
            return content;
        }
    }
}