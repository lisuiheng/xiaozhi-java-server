package com.github.lisuiheng.astra.server.server.controller;

import cn.idev.excel.util.DateUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.lisuiheng.astra.common.domain.R;
import com.github.lisuiheng.astra.server.server.constant.DeviceState;
import com.github.lisuiheng.astra.server.server.model.dto.OtaRequestDTO;
import com.github.lisuiheng.astra.server.server.model.dto.OtaResponseDTO;
import com.github.lisuiheng.astra.server.server.model.dto.OtaResultResponseDTO;
import com.github.lisuiheng.astra.server.server.model.entity.DeviceInfo;
import com.github.lisuiheng.astra.server.server.service.DeviceActivationService;
import com.github.lisuiheng.astra.server.server.service.DeviceInfoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping()
@RequiredArgsConstructor
public class OtaController {

    @Autowired
    private DeviceInfoService deviceInfoService;

    @Autowired
    private DeviceActivationService deviceActivationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${activation.domain:xiaozhi.me}")
    private String activationDomain;

    /**
     * OTA检查接口
     */
    @PostMapping(value = {"/ota", "/ota/"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> ota(@RequestBody String jsonData) throws JsonProcessingException {
        log.debug("ota request: {}", jsonData);

        JsonNode jsonNode = objectMapper.readTree(jsonData);
        String macAddress = jsonNode.get("mac_address").asText();
        String appVersion = jsonNode.path("application").path("version").asText();
        String uuid = jsonNode.has("uuid") ? jsonNode.get("uuid").asText() : null;
        String chipModelName = jsonNode.path("chip_model_name").asText();

        // 尝试通过MAC地址或UUID查询设备
        DeviceInfo deviceInfo = deviceInfoService.queryDeviceBySerialNumber(macAddress);
        if (deviceInfo == null && uuid != null) {
            deviceInfo = deviceInfoService.queryDeviceByUuid(uuid);
        }

        // 构建响应Map
        Map<String, Object> response = new LinkedHashMap<>();

        // websocket信息
        Map<String, Object> websocket = new HashMap<>();
//        websocket.put("url", "ws://192.168.3.61:8001/ws/voice");
        websocket.put("url", "ws://192.168.3.15:8001/ws/voice");
        websocket.put("token", "test-token");
        response.put("websocket", websocket);

        // server_time信息
        Map<String, Object> serverTime = new HashMap<>();
        serverTime.put("timestamp", System.currentTimeMillis());
        serverTime.put("timezone_offset", 480);
        response.put("server_time", serverTime);

        // firmware信息
        Map<String, Object> firmware = new HashMap<>();
        firmware.put("version", appVersion);
        firmware.put("url", "");
        response.put("firmware", firmware);

        // 如果未查询到设备信息，添加activation信息
        if (deviceInfo == null) {
            // 生成激活数据
            Map<String, Object> activationData = generateActivationData(macAddress, uuid, chipModelName, appVersion);
            String activationCode = (String) activationData.get("code");
            String challenge = (String) activationData.get("challenge");

            Map<String, Object> activation = new HashMap<>();
            activation.put("code", activationCode);
            activation.put("message", activationDomain + "\n" + activationCode);
            activation.put("challenge", challenge);
            activation.put("timeout_ms", 30 * 60 * 1000); // 30分钟超时
            response.put("activation", activation);

            // 记录设备首次出现（创建临时记录）
            recordFirstSeenDevice(macAddress, uuid, chipModelName, appVersion);

            log.info("New device detected: MAC={}, UUID={}, Chip={}, Version={}",
                    macAddress, uuid, chipModelName, appVersion);
        } else {
            // 设备已存在，更新最后连接时间
            deviceInfo.setLastSeenTime(LocalDateTime.now());
            deviceInfoService.updateById(deviceInfo);
            log.info("Existing device: MAC={}, UUID={}, Version={}",
                    macAddress, deviceInfo.getUuid(), appVersion);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 生成激活数据（包含设备信息缓存）
     */
    private Map<String, Object> generateActivationData(String macAddress, String uuid, String chipModel, String version) {
        // 生成6位数字激活码
        String activationCode = String.format("%06d", new Random().nextInt(1000000));

        // 生成挑战码（UUID）
        String challenge = UUID.randomUUID().toString();

        // 存储激活信息到Redis，有效期30分钟
        String activationKey = "activation:code:" + activationCode;
        Map<String, String> activationData = new HashMap<>();
        activationData.put("mac_address", macAddress);
        activationData.put("challenge", challenge);
        activationData.put("generated_time", String.valueOf(System.currentTimeMillis()));

        // 缓存设备信息，用于后续添加设备时使用
        activationData.put("device_mac", macAddress);
        activationData.put("device_uuid", uuid != null ? uuid : "");
        activationData.put("device_chip_model", chipModel != null ? chipModel : "");
        activationData.put("device_app_version", version != null ? version : "");
        activationData.put("device_detected_time", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        redisTemplate.opsForHash().putAll(activationKey, activationData);
        redisTemplate.expire(activationKey, 30 * 60, TimeUnit.SECONDS);

        // 存储挑战码用于验证，有效期10分钟
        String challengeKey = "activation:challenge:" + challenge;
        redisTemplate.opsForValue().set(challengeKey, macAddress, 10 * 60, TimeUnit.SECONDS);

        // 同时缓存MAC到激活码的映射，方便通过MAC查找激活码
        String macToCodeKey = "activation:mac_to_code:" + macAddress;
        redisTemplate.opsForValue().set(macToCodeKey, activationCode, 30 * 60, TimeUnit.SECONDS);

        Map<String, Object> result = new HashMap<>();
        result.put("code", activationCode);
        result.put("challenge", challenge);

        log.info("Generated activation data: MAC={}, Code={}, Challenge={}, UUID={}, Chip={}, Version={}",
                macAddress, activationCode, challenge, uuid, chipModel, version);
        return result;
    }

    /**
     * 记录首次出现的设备
     */
    private void recordFirstSeenDevice(String macAddress, String uuid, String chipModel, String version) {
        try {
            // 可以在这里创建临时设备记录或记录到日志
            log.info("First seen - MAC: {}, UUID: {}, Chip: {}, Version: {}",
                    macAddress, uuid, chipModel, version);
        } catch (Exception e) {
            log.error("Failed to record first seen device", e);
        }
    }


    /**
     * 激活接口 - 设备通过HMAC验证（使用请求头参数）
     */
    @PostMapping("/ota/activate")
    @Operation(summary = "激活设备")
    public ResponseEntity<Void> activate(
            @RequestHeader(value = "Activation-Version", required = false) String activationVersion,
            @RequestHeader(value = "Device-Id", required = false) String deviceId,
            @RequestHeader(value = "Client-Id", required = false) String serialNumber,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage,
            HttpServletRequest request) {

//        printAllHeaders(request);
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
//                "status", "error",
//                "message", "HMAC签名无效"
//        ));
        // 创建设备记录或更新现有记录
        DeviceInfo deviceInfo = deviceInfoService.queryDeviceBySerialNumber(serialNumber);
        if (deviceInfo == null) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }

//        // 更新设备状态
//        deviceInfo.setDeviceState(DeviceState.ACTIVE);
//        deviceInfo.setActivatedTime(LocalDateTime.now());
//        deviceInfo.setLastSeenTime(LocalDateTime.now());
//
//
//
//        // 记录用户代理和语言信息
//        deviceInfo.setUserAgent(userAgent);
//        deviceInfo.setLanguage(acceptLanguage);
//
//        deviceInfoService.saveOrUpdate(deviceInfo);
//
//        log.info("设备激活成功: SerialNumber={}, DeviceId={}", serialNumber, deviceId);
//
        return ResponseEntity.ok().build();
    }

    /**
     * 打印所有请求头信息
     */
    private void printAllHeaders(HttpServletRequest request) {
        log.info("========== 激活接口 - 所有请求头信息 ==========");

        // 获取所有请求头名称
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            Enumeration<String> headers = request.getHeaders(headerName);

            List<String> headerValues = new ArrayList<>();
            while (headers.hasMoreElements()) {
                headerValues.add(headers.nextElement());
            }

            String values = String.join(", ", headerValues);
            log.info("Header: {} = {}", headerName, values);
        }

        // 打印其他请求信息
        log.info("HTTP Method: {}", request.getMethod());
        log.info("Request URL: {}", request.getRequestURL());
        log.info("Query String: {}", request.getQueryString());
        log.info("Remote Address: {}", request.getRemoteAddr());
        log.info("Remote Host: {}", request.getRemoteHost());
        log.info("Remote Port: {}", request.getRemotePort());
        log.info("Local Address: {}", request.getLocalAddr());
        log.info("Local Port: {}", request.getLocalPort());
        log.info("Content Type: {}", request.getContentType());
        log.info("Content Length: {}", request.getContentLength());
        log.info("Character Encoding: {}", request.getCharacterEncoding());

        log.info("========== 请求头信息结束 ==========");
    }

    /**
     * 验证HMAC签名
     */
    private boolean verifyHmac(String serialNumber, String challenge, String receivedHmac) {
        try {
            // 验证挑战码是否有效
            String challengeKey = "activation:challenge:" + challenge;
            String storedMac = redisTemplate.opsForValue().get(challengeKey);
            if (storedMac == null || !storedMac.equals(serialNumber)) {
                log.warn("Invalid or expired challenge: MAC={}, Challenge={}", serialNumber, challenge);
                return false;
            }

            // 获取设备密钥
            String deviceKey = getDeviceKey(serialNumber);
            if (deviceKey == null) {
                log.error("Device key not found: MAC={}", serialNumber);
                return false;
            }

            // 计算期望的HMAC
            byte[] keyBytes = Base64.getDecoder().decode(deviceKey);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKeySpec);
            byte[] expectedHmac = mac.doFinal(challenge.getBytes(StandardCharsets.UTF_8));

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
        // 1. 首先从数据库查询设备密钥
        DeviceInfo deviceInfo = deviceInfoService.queryDeviceBySerialNumber(serialNumber);
        if (deviceInfo != null && deviceInfo.getSecretKey() != null) {
            return deviceInfo.getSecretKey();
        }

        // 2. 如果数据库中没有，尝试根据设备信息生成（需要与生产时算法一致）
        try {
            String companySalt = "your-company-secret-salt"; // 应该从配置文件中读取
            String seed = companySalt + ":" + serialNumber.replace(":", "");

            byte[] hash = java.security.MessageDigest.getInstance("SHA-256")
                    .digest(seed.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash).substring(0, 32);
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
     * 手动激活接口（用于Web界面）
     */
    @PostMapping("/activate/manual")
    public ResponseEntity<Map<String, Object>> manualActivate(@RequestBody Map<String, String> request) {
        String activationCode = request.get("code");
        String challenge = request.get("challenge");
        String macAddress = request.get("mac_address");

        if (activationCode == null || challenge == null || macAddress == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Missing required parameters"
            ));
        }

        // 验证激活码和挑战码
        String activationKey = "activation:code:" + activationCode;
        Map<Object, Object> activationData = redisTemplate.opsForHash().entries(activationKey);

        if (activationData.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "status", "error",
                    "message", "Invalid or expired activation code"
            ));
        }

        String storedMac = (String) activationData.get("mac_address");
        String storedChallenge = (String) activationData.get("challenge");

        if (!macAddress.equals(storedMac) || !challenge.equals(storedChallenge)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "status", "error",
                    "message", "Invalid challenge or device mismatch"
            ));
        }

        // 验证成功，创建设备记录
        DeviceInfo deviceInfo = deviceInfoService.queryDeviceBySerialNumber(macAddress);
        if (deviceInfo == null) {
            deviceInfo = new DeviceInfo();
            deviceInfo.setId(UUID.randomUUID().toString());
            deviceInfo.setSerialNumber(macAddress);
            deviceInfo.setDeviceKind("esp32");
            deviceInfo.setName("Device-" + macAddress.substring(Math.max(0, macAddress.length() - 6)));
        }

        deviceInfo.setDeviceState(DeviceState.ACTIVE);
        deviceInfo.setActivatedTime(LocalDateTime.now());
        deviceInfo.setLastSeenTime(LocalDateTime.now());
        deviceInfoService.saveOrUpdate(deviceInfo);

        // 删除激活码记录
        redisTemplate.delete(activationKey);

        log.info("Manual activation successful: MAC={}, Code={}", macAddress, activationCode);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Device activated successfully"
        ));
    }

    /**
     * OTA结果处理接口
     */
    @PostMapping("/result")
    public ResponseEntity<OtaResultResponseDTO> handleUpdateResult(@RequestBody Object resultDTO) {
        log.info("处理OTA更新结果: {}", resultDTO);
        // TODO: 实际项目中需要实现具体的业务逻辑
        return ResponseEntity.ok(new OtaResultResponseDTO(true, "处理成功", null));
    }

    /**
     * 获取设备激活状态
     */
    @GetMapping("/device/status/{macAddress}")
    public ResponseEntity<Map<String, Object>> getDeviceStatus(@PathVariable String macAddress) {
        DeviceInfo deviceInfo = deviceInfoService.queryDeviceBySerialNumber(macAddress);

        if (deviceInfo == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "not_found",
                    "message", "Device not found"
            ));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("serial_number", deviceInfo.getSerialNumber());
        response.put("uuid", deviceInfo.getUuid());
        response.put("device_state", deviceInfo.getDeviceState());
        response.put("activated_time", deviceInfo.getActivatedTime());
        response.put("last_seen_time", deviceInfo.getLastSeenTime());
        response.put("program_version", deviceInfo.getProgramVer());

        return ResponseEntity.ok(response);
    }
}