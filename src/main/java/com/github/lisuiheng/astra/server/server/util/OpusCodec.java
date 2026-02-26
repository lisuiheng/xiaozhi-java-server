package com.github.lisuiheng.astra.server.server.util;


import com.github.lisuiheng.astra.common.utils.StringUtils;
import com.sun.jna.ptr.PointerByReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tomp2p.opuswrapper.Opus;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

@Slf4j
@Service
public class OpusCodec {
    private final PointerByReference encoder;
    private final PointerByReference decoder;
    private static final int MAX_FRAME_SIZE = 6 * 960;

    public OpusCodec() {
        System.setProperty("opus.lib", "/usr/lib/x86_64-linux-gnu/libopus.so");
        System.setProperty("jna.debug_load", "true");
        System.setProperty("jna.debug_load.jna", "true");

        IntBuffer errorBuffer = IntBuffer.allocate(1);
        this.encoder = Opus.INSTANCE.opus_encoder_create(24000, 1, Opus.OPUS_APPLICATION_VOIP, errorBuffer);
        this.decoder = Opus.INSTANCE.opus_decoder_create(16000, 1, errorBuffer);
    }

    /**
     * 编码 PCM 数据为 Opus 格式
     */
    public byte[] encode(ShortBuffer pcmBuffer) {
        ByteBuffer encodedData = ByteBuffer.allocate(MAX_FRAME_SIZE);
        int bytesEncoded = Opus.INSTANCE.opus_encode(encoder, pcmBuffer, pcmBuffer.remaining(), encodedData, MAX_FRAME_SIZE);

        if (bytesEncoded < 0) {
            logError("opus_encode", bytesEncoded);
            return null;
        }

        byte[] result = new byte[bytesEncoded];
        encodedData.get(result, 0, bytesEncoded);
        return result;
    }

    /**
     * 编码 PCM 数据为 Opus 格式
     */
    public byte[] encode(byte[] pcmData) {
        ShortBuffer pcmBuffer = ByteBuffer.wrap(pcmData)
                .order(ByteOrder.LITTLE_ENDIAN)
                .asShortBuffer();
        return encode(pcmBuffer);
    }


    /**
     * 解码 Opus 数据为 PCM 格式，并返回 byte[]
     */
    public byte[] decodeToByteArray(byte[] audioBuffer) throws IOException {
        try {
            ShortBuffer pcmBuffer = ShortBuffer.allocate(MAX_FRAME_SIZE);
            int bytesDecode = Opus.INSTANCE.opus_decode(
                    this.decoder,
                    audioBuffer,
                    audioBuffer.length,
                    pcmBuffer,
                    MAX_FRAME_SIZE,
                    0
            );
            if (bytesDecode < 0) {
                log.error("Opus.INSTANCE.opus_decode failed with error code: {}", bytesDecode);
                switch (bytesDecode) {
                    case Opus.OPUS_BAD_ARG:
                        log.error("Invalid argument provided to opus_decode.");
                        break;
                    case Opus.OPUS_BUFFER_TOO_SMALL:
                        log.error("The output buffer is too small.");
                        break;
                    case Opus.OPUS_INTERNAL_ERROR:
                        log.error("An internal error occurred.");
                        break;
                    case Opus.OPUS_INVALID_PACKET:
                        String hex = StringUtils.bytesToHex(audioBuffer, Math.min(16, audioBuffer.length));
                        log.info("数据包头: {}", hex);
                        log.error("The Opus packet is invalid.");
                        break;
                    case Opus.OPUS_UNIMPLEMENTED:
                        log.error("The requested feature is not implemented.");
                        break;
                    case Opus.OPUS_INVALID_STATE:
                        log.error("The decoder state is invalid.");
                        break;
                    default:
                        log.error("Unknown error occurred.");
                        break;
                }
                return null;
            }
            // 将 ShortBuffer 转换为 byte[]
            byte[] pcmByteArray = new byte[bytesDecode * 2]; // 每个 short 占 2 字节
            ByteBuffer byteBuffer = ByteBuffer.wrap(pcmByteArray); // 包装为 ByteBuffer
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN); // 设置字节顺序（小端序）

            // 将 ShortBuffer 中的数据写入 ByteBuffer
            for (int i = 0; i < bytesDecode; i++) {
                byteBuffer.putShort(pcmBuffer.get(i));
            }

            return pcmByteArray;
        } catch (Exception e) {
            log.error("Opus.INSTANCE.opus_decode failed", e);
        }
        return null;
    }

    /**
     * 记录 Opus 错误日志
     */
    private void logError(String method, int errorCode) {
        switch (errorCode) {
            case Opus.OPUS_BAD_ARG:
                log.error("{}: Invalid argument provided.", method);
                break;
            case Opus.OPUS_BUFFER_TOO_SMALL:
                log.error("{}: The output buffer is too small.", method);
                break;
            case Opus.OPUS_INTERNAL_ERROR:
                log.error("{}: An internal error occurred.", method);
                break;
            case Opus.OPUS_INVALID_PACKET:
                log.error("{}: The Opus packet is invalid.", method);
                break;
            case Opus.OPUS_UNIMPLEMENTED:
                log.error("{}: The requested feature is not implemented.", method);
                break;
            case Opus.OPUS_INVALID_STATE:
                log.error("{}: The decoder state is invalid.", method);
                break;
            default:
                log.error("{}: Unknown error occurred.", method);
                break;
        }
    }

    /**
     * 释放 Opus 编码器和解码器资源
     */
    public void close() {
        if (encoder != null) {
            Opus.INSTANCE.opus_encoder_destroy(encoder);
        }
        if (decoder != null) {
            Opus.INSTANCE.opus_decoder_destroy(decoder);
        }
    }
}