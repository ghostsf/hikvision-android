/* 
 * @ProjectName iVMS-5060_V3.0
 * @Copyright HangZhou Hikvision System Technology Co.,Ltd. All Right Reserved
 * 
 * @FileName CameraID.java
 * @Description 这里对文件进行描述
 * 
 * @author mlianghua
 * @data Jun 28, 2012
 * 
 * @note 这里写本文件的详细功能描述和注释
 * @note 历史记录
 * 
 * @warning 这里写本文件的相关警告
 */
package com.demo.sdk6x.data;

import com.hikvision.vmsnetsdk.CameraInfoEx;
import com.hikvision.vmsnetsdk.RealPlayURL;

/**
 * 在此对类做相应的描述
 *
 * @author mlianghua
 * @Data Jun 28, 2012
 */
public class LiveCameraInfo {

    /**
     * 监控点ID
     */
    private String mCameraId = "";

    /**
     * 监控点名字
     */
    private String mCameraName = "";

    /**
     * 设备ID
     */
    private String mDeviceId = "";

    /**
     * 监控点RtspUrl
     */
    private RealPlayURL mRtspUrl = null;

    /**
     * 云台端口
     */
    private int mPTZPort = 0;

    /**
     * 云台服务器地址
     */
    private String mPTZServer = "";

    /**
     * 级联标识，0-非级联，1-级联
     */
    private int cascadeFlag = 0;

    /**
     * 设备的网域ID
     */
    private int deviceNetID = 0;

    /**
     * 获取监控点Rtsp Url.
     *
     * @return the mRtspUrl
     */
    public RealPlayURL getmRtspUrl() {
        return mRtspUrl;
    }

    /**
     * 设置监控点Rtsp Url.
     *
     * @param rtspUrl the mRtspUrl to set.
     */
    public void setmRtspUrl(RealPlayURL rtspUrl) {
        mRtspUrl = rtspUrl;
    }

    /**
     * 获取监控点 Name.
     *
     * @return the mCameraID
     */
    public String getmCameraId() {
        return mCameraId;
    }

    /**
     * 设置监控点 Name.
     *
     * @param cameraId the mCameraID to set
     */
    public void setmCameraId(String cameraId) {
        mCameraId = cameraId;
    }

    /**
     * 获取监控点 Name.
     *
     * @return the mCameraName
     */
    public String getmCameraName() {
        return mCameraName;
    }

    /**
     * 设置监控点 Name.
     *
     * @param cameraName the mCameraName to set
     */
    public void setmCameraName(String cameraName) {
        mCameraName = cameraName;
    }

    public String getDeviceId() {
        return mDeviceId;
    }

    public void setDeviceId(String mDeviceId) {
        this.mDeviceId = mDeviceId;
    }

    public void setPTZPort(int ptzPort) {
        mPTZPort = ptzPort;
    }

    public int getPTZPort() {
        return mPTZPort;
    }

    public void setPTZServer(String ptzServer) {
        mPTZServer = ptzServer;
    }

    public String getPTZServer() {
        return mPTZServer;
    }

    public int getCascadeFlag() {
        return cascadeFlag;
    }

    public void setCascadeFlag(int cascadeFlag) {
        this.cascadeFlag = cascadeFlag;
    }

    public int getDeviceNetID() {
        return deviceNetID;
    }

    public void setDeviceNetID(int deviceNetID) {
        this.deviceNetID = deviceNetID;
    }

    /**
     * 设置参数
     *
     * @param cameraInfoEx
     */
    public void setParams(CameraInfoEx cameraInfoEx) {
        // TODO Auto-generated method stub
        setmCameraId(cameraInfoEx.getId());
        setmCameraName(cameraInfoEx.getName());
        setDeviceId(cameraInfoEx.getDeviceId());
        setPTZPort(cameraInfoEx.getAcsPort());
        setPTZServer(cameraInfoEx.getAcsIP());
        setCascadeFlag(cameraInfoEx.getCascadeFlag());
        setDeviceNetID(cameraInfoEx.getDeviceNetId());
    }

}
