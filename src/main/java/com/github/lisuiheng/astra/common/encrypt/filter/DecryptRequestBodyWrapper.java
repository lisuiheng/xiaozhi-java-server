package com.github.lisuiheng.astra.common.encrypt.filter;

import cn.hutool.core.io.IoUtil;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import com.github.lisuiheng.astra.common.constant.Constants;
import com.github.lisuiheng.astra.common.encrypt.utils.EncryptUtils;
import org.springframework.http.MediaType;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 解密请求参数工具类
 *
 * @author wdhcr
 */
public class DecryptRequestBodyWrapper extends HttpServletRequestWrapper {

    private final byte[] body;

    public DecryptRequestBodyWrapper(HttpServletRequest request, String privateKey, String headerFlag) throws IOException {
        super(request);
        // 获取 AES 密码 采用 RSA 加密
        String headerRsa = request.getHeader(headerFlag);
        String decryptAes;
        String aesPassword;
        String requestBody;
        String decryptBody;
        
        try {
            decryptAes = EncryptUtils.decryptByRsa(headerRsa, privateKey);
            // 解密 AES 密码
            aesPassword = EncryptUtils.decryptByBase64(decryptAes);
            request.setCharacterEncoding(Constants.UTF8);
            byte[] readBytes = IoUtil.readBytes(request.getInputStream(), false);
            requestBody = new String(readBytes, StandardCharsets.UTF_8);
            // 解密 body 采用 AES 加密
            decryptBody = EncryptUtils.decryptByAes(requestBody, aesPassword);
        } catch (Exception e) {
            // 如果解密失败，抛出异常
            throw new IOException("解密失败", e);
        }
        
        body = decryptBody.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }


    @Override
    public int getContentLength() {
        return body.length;
    }

    @Override
    public long getContentLengthLong() {
        return body.length;
    }

    @Override
    public String getContentType() {
        return MediaType.APPLICATION_JSON_VALUE;
    }


    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public int read() {
                return bais.read();
            }

            @Override
            public int available() {
                return body.length;
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }
        };
    }
}