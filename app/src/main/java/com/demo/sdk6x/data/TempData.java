package com.demo.sdk6x.data;

import com.hikvision.vmsnetsdk.CameraInfo;
import com.hikvision.vmsnetsdk.CameraInfoEx;
import com.hikvision.vmsnetsdk.ServInfo;
import com.hikvision.vmsnetsdk.netLayer.msp.cameraInfo.CameraDetailInfo;

public final class TempData {
	private static TempData ins = new TempData();
	public static TempData getIns() {
		return ins;
	}
	/**
	 * 登录返回的数据
	 */
	private ServInfo loginData;

	/**
	 * 监控点信息，用作临时传递数据用
	 */
	private CameraInfo cameraInfo;
	
	private CameraInfoEx cameraInfoEx;

	public static TempData getInstance() {
		return ins;
	}

	/**
	  * 设置登录成功返回的信息
	  * @param loginData
	  * @since V1.0
	  */
	public void setLoginData(ServInfo loginData) {
		this.loginData = loginData;
	}

	/**
	  * 获取登录成功返回的信息
	  * @return
	  * @since V1.0
	  */
	public ServInfo getLoginData() {
		return loginData;
	}

	/**
	  * 保存监控点信息
	  * @param cameraInfo
	  * @since V1.0
	  */
	public void setCameraInfo(CameraInfo cameraInfo) {
		this.cameraInfo = cameraInfo;
	}

	/**
	  * 获取监控点信息
	  * @return
	  * @since V1.0
	  */
	public CameraInfo getCameraInfo() {
		return cameraInfo;
	}
	
	
	public void setCameraInfoEx(CameraInfoEx cameraInfoEx) {
		this.cameraInfoEx = cameraInfoEx;
	}
	
	public CameraInfoEx getCameraInfoEx() {
		return cameraInfoEx;
	}
	
}
