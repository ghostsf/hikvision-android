package com.demo.sdk6x.playback;

import com.hik.mcrsdk.rtsp.ABS_TIME;

import android.view.SurfaceView;


/**
 * 回放时传递的参数类
 * 
 * @author huangweifeng
 * @Data 2013-10-28
 */
public class PlayBackParams {
	/**
	 * 播放控件
	 * */
	public SurfaceView surfaceView;
	/**
	 * 回放地址
	 * */
	public String url;
	/**
	 * 登录设备的用户名
	 * */
	public String name;
	/**
	 * 登录设备的密码
	 * */
	public String passwrod;
	/**
	 * 回放开始时间
	 * */
	public ABS_TIME startTime;
	/**
	 * 回放结束时间
	 * */
	public ABS_TIME endTime;
}
