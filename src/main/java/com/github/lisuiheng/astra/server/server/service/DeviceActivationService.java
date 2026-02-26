package com.github.lisuiheng.astra.server.server.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class DeviceActivationService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${activation.domain:xiaozhi.me}")
    private String activationDomain;

    private static final long ACTIVATION_CODE_EXPIRE = 30 * 60; // 30分钟
    private static final long CHALLENGE_EXPIRE = 10 * 60; // 10分钟

    /**
     * 生成激活数据
     */
    public Map<String, Object> generateActivationData(String macAddress) {
        // 生成6位数字激活码
        String activationCode = String.format("%06d", new Random().nextInt(1000000));

        // 生成挑战码（UUID）
        String challenge = UUID.randomUUID().toString();

        // 存储到Redis
        String activationKey = "activation:code:" + activationCode;
        Map<String, String> activationData = new HashMap<>();
        activationData.put("mac_address", macAddress);
        activationData.put("challenge", challenge);
        activationData.put("generated_time", String.valueOf(System.currentTimeMillis()));

        redisTemplate.opsForHash().putAll(activationKey, activationData);
        redisTemplate.expire(activationKey, ACTIVATION_CODE_EXPIRE, TimeUnit.SECONDS);

        // 存储挑战码用于验证
        String challengeKey = "activation:challenge:" + challenge;
        redisTemplate.opsForValue().set(challengeKey, macAddress, CHALLENGE_EXPIRE, TimeUnit.SECONDS);

        Map<String, Object> result = new HashMap<>();
        result.put("code", activationCode);
        result.put("challenge", challenge);
        result.put("message", activationDomain + "\n" + activationCode);

        log.info("Generated activation data: MAC={}, Code={}, Challenge={}",
                macAddress, activationCode, challenge);

        return result;
    }

    /**
     * 验证激活码
     */
    public boolean verifyActivationCode(String activationCode, String challenge, String macAddress) {
        String activationKey = "activation:code:" + activationCode;
        Map<Object, Object> activationData = redisTemplate.opsForHash().entries(activationKey);

        if (activationData.isEmpty()) {
            return false;
        }

        String storedMac = (String) activationData.get("mac_address");
        String storedChallenge = (String) activationData.get("challenge");

        if (!macAddress.equals(storedMac) || !challenge.equals(storedChallenge)) {
            return false;
        }

        // 验证成功，删除激活码记录
        redisTemplate.delete(activationKey);

        return true;
    }

    /**
     * 验证HMAC
     */
    public boolean verifyHmac(String serialNumber, String challenge, String receivedHmac) {
        try {
            // 验证挑战码是否有效
            String challengeKey = "activation:challenge:" + challenge;
            String storedMac = redisTemplate.opsForValue().get(challengeKey);
            if (storedMac == null || !storedMac.equals(serialNumber)) {
                log.warn("Invalid or expired challenge: MAC={}, Challenge={}", serialNumber, challenge);
                return false;
            }

            // 获取设备密钥（这里需要根据实际实现获取）
            String deviceKey = getDeviceKey(serialNumber);
            if (deviceKey == null) {
                log.error("Device key not found: MAC={}", serialNumber);
                return false;
            }

            // 计算期望的HMAC
            javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(
                    java.util.Base64.getDecoder().decode(deviceKey), "HmacSHA256");
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            mac.init(secretKeySpec);
            byte[] expectedHmac = mac.doFinal(challenge.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            // 比较HMAC
            String expectedHmacHex = bytesToHex(expectedHmac);
            boolean isValid = expectedHmacHex.equalsIgnoreCase(receivedHmac);

            if (isValid) {
                // 验证成功后删除挑战码记录
                redisTemplate.delete(challengeKey);
            }

            return isValid;

        } catch (Exception e) {
            log.error("HMAC verification failed for MAC: {}", serialNumber, e);
            return false;
        }
    }

    /**
     * 获取设备密钥
     */
    private String getDeviceKey(String serialNumber) {
        // TODO: 这里需要根据实际实现获取设备密钥
        // 临时示例：生成一个测试密钥
        try {
            String seed = "your-company-secret-salt-" + serialNumber;
            byte[] hash = java.security.MessageDigest.getInstance("SHA-256")
                    .digest(seed.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.Base64.getEncoder().encodeToString(hash).substring(0, 32);
        } catch (Exception e) {
            log.error("Failed to generate device key", e);
            return null;
        }
    }

    /**
     * 字节数组转16进制字符串
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * 获取激活消息
     */
    public String getActivationMessage(String activationCode) {
        return activationDomain + "\n" + activationCode;
    }
}