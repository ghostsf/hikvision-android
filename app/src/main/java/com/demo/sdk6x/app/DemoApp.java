package com.demo.sdk6x.app;

import com.hik.mcrsdk.MCRSDK;
import com.hik.mcrsdk.rtsp.RtspClient;
import com.hikvision.vmsnetsdk.VMSNetSDK;

import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiManager;

/**
 * Application 类
 * @author zhoudaihui
 *
 */
public class DemoApp extends Application {
    private static DemoApp ins;
    
	@Override
    public void onCreate() {
    	super.onCreate();
    	ins = this;
    	System.loadLibrary("gnustl_shared");
    	MCRSDK.init();
        RtspClient.initLib();
        MCRSDK.setPrint(1, null);
        VMSNetSDK.getInstance().openLog(true);
    }
	
	public static DemoApp getIns() {
		return ins;
	}

	/**
	 * 获取登录设备mac地址
	 *
	 * @return
	 */
	public String getMacAddr()
	{
		WifiManager wm = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		String mac = wm.getConnectionInfo().getMacAddress();
		return mac == null ? "" : mac;
	}
}
