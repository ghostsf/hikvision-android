package com.demo.sdk6x.playback;

/**
 * 回放回调接口
 * @author huangweifeng
 * @Data 2013-10-28
 */
public interface PlayBackCallBack {
    /**
     * 播放引擎消息回调接口
     * 
     * @param message 消息
     * @since V1.0
     */
    public void onMessageCallback(int message);
}
