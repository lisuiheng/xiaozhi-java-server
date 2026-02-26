package com.github.lisuiheng.astra.common.encrypt.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.symmetric.AES;

import java.nio.charset.StandardCharsets;

/**
 * 加密工具类
 *
 * @author wdhcr
 */
public class EncryptUtils {

    /**
     * AES加密
     *
     * @param data 待加密数据
     * @param password 密钥
     * @return 加密后数据
     */
    public static String encryptByAes(String data, String password) {
        AES aes = SecureUtil.aes(password.getBytes());
        return aes.encryptBase64(data);
    }

    /**
     * AES解密
     *
     * @param data 待解密数据
     * @param password 密钥
     * @return 解密后数据
     */
    public static String decryptByAes(String data, String password) {
        AES aes = SecureUtil.aes(password.getBytes());
        return new String(aes.decrypt(data), StandardCharsets.UTF_8);
    }

    /**
     * RSA解密
     *
     * @param data 待解密数据
     * @param privateKey 私钥
     * @return 解密后数据
     */
    public static String decryptByRsa(String data, String privateKey) {
        RSA rsa = new RSA(privateKey, null);
        return rsa.decryptStr(data, KeyType.PrivateKey);
    }

    /**
     * Base64解密
     *
     * @param data 待解密数据
     * @return 解密后数据
     */
    public static String decryptByBase64(String data) {
        return Base64.decodeStr(data);
    }

    /**
     * Base64加密
     *
     * @param data 待加密数据
     * @return 加密后数据
     */
    public static String encryptByBase64(String data) {
        return Base64.encode(data);
    }
}