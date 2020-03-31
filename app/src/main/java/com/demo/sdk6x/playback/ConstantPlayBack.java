package com.demo.sdk6x.playback;

/**
 * 回放中用到的常量
 * 
 * @author huangweifeng
 * @Data 2013-10-28
 */
public class ConstantPlayBack {
	private static final int ERR_BASE = 1000;
	/**
	 * 启动取流失败
	 * */
	public static final int START_RTSP_FAIL = ERR_BASE;
	/**
	 * 启动取流成功
	 * */
	public static final int START_RTSP_SUCCESS = ERR_BASE + 1;
	/**
	 * 暂停失败
	 * */
	public static final int PAUSE_FAIL = ERR_BASE + 2;
	/**
	 * 暂停成功
	 * */
	public static final int PAUSE_SUCCESS = ERR_BASE + 3;
	/**
	 * 恢复播放失败
	 * */
	public static final int RESUEM_FAIL = ERR_BASE + 4;
	/**
	 * 恢复播放成功
	 * */
	public static final int RESUEM_SUCCESS = ERR_BASE + 5;
	/**
	 * 启动播放失败
	 * */
	public static final int START_OPEN_FAILED = ERR_BASE + 6;
	/**
	 * 回放成功
	 * */
	public static final int PLAY_DISPLAY_SUCCESS = ERR_BASE + 7;
	/**
	 * SD卡不可用
	 * */
	public static final int SD_CARD_UN_USEABLE = ERR_BASE + 8;
	/**
	 * SD卡空间不足
	 * */
	public static final int SD_CARD_SIZE_NOT_ENOUGH = ERR_BASE + 9;
	/**
	 * 非播放状态不能抓怕
	 */
	public static final int CAPTURE_FAILED_NPLAY_STATE = ERR_BASE + 10;
	/**
	 * 非播放状态不能暂停
	 */
	public static final int PAUSE_FAIL_NPLAY_STATE = ERR_BASE + 11;
	/**
	 * 非暂停状态不需要恢复
	 */
	public static final int RESUEM_FAIL_NPAUSE_STATE = ERR_BASE + 12;
	/**
	 * 非播放状态不能录像
	 */
	public static final int RECORD_FAIL_NPLAY_STATE = ERR_BASE + 13;
	/**
	 * 非播放状态不能开启音频
	 */
	public static final int AUDIO_START_FAIL_NPLAY_STATE = ERR_BASE + 14;
	/**
	 * 非播放状态不能关闭音频
	 */
	public static final int AUDIO_STOP_FAIL_NPLAY_STATE = ERR_BASE + 15;
	/**
	 * 获取录像文件成功
	 */
	public static final int GET_RECORD_FILE_SUCCESS = ERR_BASE + 16;
	/**
	 * 获取录像文件失败
	 */
	public static final int GET_RECORD_FILE_FAIL = ERR_BASE + 17;
	/**
	 * 停止成功
	 */
	public static final int STOP_SUCCESS = ERR_BASE + 18;
	/**
	 * 获取录像文件为空
	 */
	public static final int GET_RECORD_FILE_NULL = ERR_BASE + 19;
	
}
