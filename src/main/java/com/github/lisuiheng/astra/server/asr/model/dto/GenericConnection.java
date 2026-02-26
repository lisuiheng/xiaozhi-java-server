package com.github.lisuiheng.astra.server.asr.model.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lisuiheng.astra.server.asr.model.dto.outbound.OutboundMessage;
import com.github.lisuiheng.astra.server.asr.model.dto.outbound.TtsOutbound;
import com.github.lisuiheng.astra.server.server.model.entity.DeviceInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public abstract class GenericConnection {
    protected String sessionId;
    protected DeviceInfo deviceInfo;
    protected ObjectMapper objectMapper;

    public GenericConnection(ObjectMapper objectMapper, DeviceInfo deviceInfo, String sessionId) {
        this.objectMapper = objectMapper;
        this.deviceInfo = deviceInfo;
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getDeviceId() {
        return deviceInfo != null ? deviceInfo.getSerialNumber() : null;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    // 抽象方法，子类必须实现
    public abstract void sendMessage(Object message);
    public abstract void close();
    public abstract boolean isOpen();
    public abstract ProtocolType getProtocolType();

    public void sendText(OutboundMessage text) throws IOException {
        if (text instanceof TtsOutbound) {
            String state = ((TtsOutbound) text).getState();
            if (state.equals("stop")) {
                log.info("");
            }
        }
        sendMessage(text);
    }

    public abstract void sendBinary(byte[] data) throws IOException;
}