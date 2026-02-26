package com.github.lisuiheng.astra.server.user.service;

/**
 * TTS音频监听器
 */
public interface AudioListener {
    void onAudioReceived(byte[] audio);
    void onSentenceStart(String text);
    void onSentenceEnd(String text);
    void onSessionStarted();
    void onSessionFinished();
    void onSessionError(String error);
}