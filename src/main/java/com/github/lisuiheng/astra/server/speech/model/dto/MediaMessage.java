package com.github.lisuiheng.astra.server.speech.model.dto;

import com.github.lisuiheng.astra.server.asr.model.dto.outbound.TtsOutbound;
import com.github.lisuiheng.astra.server.speech.constant.MediaType;
import lombok.Data;

@Data
public class MediaMessage {
    private final MediaType type;
    private final byte[] audioData;
    private final TtsOutbound ttsMessage;

    // 音频消息构造函数
    public MediaMessage(byte[] audioData) {
        this.type = MediaType.AUDIO;
        this.audioData = audioData;
        this.ttsMessage = null;
    }

    // TTS消息构造函数
    public MediaMessage(TtsOutbound ttsMessage) {
        this.type = MediaType.TTS;
        this.audioData = null;
        this.ttsMessage = ttsMessage;
    }
}