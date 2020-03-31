/**
 * 
 */
package com.demo.sdk6x.resource;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.demo.sdk6x.callback.MsgCallback;
import com.demo.sdk6x.callback.MsgIds;
import com.demo.sdk6x.constants.Constants;
import com.demo.sdk6x.data.Config;
import com.demo.sdk6x.data.TempData;
import com.hikvision.vmsnetsdk.CameraInfo;
import com.hikvision.vmsnetsdk.ControlUnitInfo;
import com.hikvision.vmsnetsdk.RegionInfo;
import com.hikvision.vmsnetsdk.ServInfo;
import com.hikvision.vmsnetsdk.VMSNetSDK;

/**
 * 获取资源逻辑控制类
 * 
 * @author zhoudaihui
 */
public final class ResourceControl {
    private MsgCallback callback;

    public void setCallback(MsgCallback callback) {
        this.callback = callback;
    }

    /**
     * 请求下一级列表资源
     * 
     * @param pType 父节点资源类型 <li>TYPE_UNKNOWN-首次获取资源，父节点默认id为0</li> <li>TYPE_CTRL_UNIT-父节点为控制中心，父节点资源id传控制中心id即可</li> <li>
     *            TYPE_REGION-父节点为区域，父资源id传区域id即可</li>
     * @param pId 父节点资源id
     */
    public void reqResList(int pType, int pId) {
        switch (pType) {
            case Constants.Resource.TYPE_UNKNOWN:// 第一次请求资源列表
                requestFirstList();
            break;
            case Constants.Resource.TYPE_CTRL_UNIT:// 从控制中心获取子资源列表
                requestSubResFromCtrlUnit(pId);
            break;
            case Constants.Resource.TYPE_REGION:// 从区域获取子资源列表
                requestSubResFromRegion(pId);
            break;
            default:
            break;
        }
    }

    /**
     * 从区域获取下一级资源列表
     * 
     * @param pId 父区域id
     */
    private void requestSubResFromRegion(int pId) {

        boolean responseFlag = false;

        String servAddr = Config.getIns().getServerAddr();
        ServInfo loginData = TempData.getIns().getLoginData();
        if(loginData == null){
            Log.i(Constants.LOG_TAG, "getRegionListFromRegion loginData : " + loginData);
            return;
        }
        int numPerPage = 10000;
        int curPage = 1;

        List<RegionInfo> regionList = new ArrayList<RegionInfo>();
        // 1.从区域获取区域列表
        boolean ret = VMSNetSDK.getInstance().getRegionListFromRegion(servAddr, loginData.getSessionID()
        		, String.valueOf(pId), numPerPage, curPage, regionList);
        Log.i(Constants.LOG_TAG, "getRegionListFromRegion ret : " + ret);
        responseFlag = responseFlag || ret;
        if (!ret) {
            Log.e(Constants.LOG_TAG, "Invoke VMSNetSDK.getRegionListFromRegion failed:" + errDesc());
        }

        List<CameraInfo> cameraList = new ArrayList<CameraInfo>();
        // 2.从区域获取监控点（摄像头）列表
        ret = VMSNetSDK.getInstance().getCameraListFromRegion(servAddr, loginData.getSessionID(), String.valueOf(pId), numPerPage, curPage,
                cameraList);
        Log.i(Constants.LOG_TAG, "getCameraListFromRegion ret : " + ret);
        responseFlag = responseFlag || ret;
        if (!ret) {
            Log.e(Constants.LOG_TAG, "Invoke VMSNetSDK.getCameraListFromRegion failed:" + errDesc());
        }

        List<Object> allData = new ArrayList<Object>();
        allData.addAll(regionList);
        allData.addAll(cameraList);

        if (callback != null) {
            callback.onMsg(responseFlag ? MsgIds.GET_SUB_F_R_SUC : MsgIds.GET_SUB_F_R_FAILED, allData);
        }
    }

    /**
     * 从控制中心获取下一级资源列表
     * 
     * @param pId 父控制中心id
     */
    private void requestSubResFromCtrlUnit(int pId) {

        boolean responseFlag = false;

        String servAddr = Config.getIns().getServerAddr();
        ServInfo loginData = TempData.getIns().getLoginData();
        if(loginData == null){
            Log.i(Constants.LOG_TAG, "requestSubResFromCtrlUnit loginData:" + loginData);
            return;
        }
        String sessionID = loginData.getSessionID();
        int controlUnitID = pId;// 控制中心id
        int numPerPage = 10000;// 此处取10000，表示每页获取的数量，这个数值可以根据实际情况进行修改
        int curPage = 1;// 当前获取的数据是第几页
        List<ControlUnitInfo> ctrlUnitList = new ArrayList<ControlUnitInfo>();

        // 1.从控制中心获取控制中心
        boolean ret = VMSNetSDK.getInstance().getControlUnitList(servAddr, sessionID, String.valueOf(controlUnitID), numPerPage,
                curPage, ctrlUnitList);
        Log.i(Constants.LOG_TAG, "getControlUnitList ret:" + ret);
        responseFlag = responseFlag || ret;

        if (!ret) {
            Log.e(Constants.LOG_TAG, "Invoke VMSNetSDK.getControlUnitList failed:" + errDesc());
        }
        List<RegionInfo> regionList = new ArrayList<RegionInfo>();
        // 2.从控制中心获取区域列表
        ret = VMSNetSDK.getInstance().getRegionListFromCtrlUnit(servAddr, sessionID, String.valueOf(controlUnitID), numPerPage,
                curPage, regionList);
        Log.i(Constants.LOG_TAG, "getRegionListFromCtrlUnit ret:" + ret);
        responseFlag = responseFlag || ret;
        if (!ret) {
            Log.e(Constants.LOG_TAG, "Invoke VMSNetSDK.getRegionListFromCtrlUnit failed:" + errDesc());
        }

        List<CameraInfo> cameraList = new ArrayList<CameraInfo>();
        // 3.从控制中心获取摄像头列表
        ret = VMSNetSDK.getInstance().getCameraListFromCtrlUnit(servAddr, sessionID, String.valueOf(controlUnitID), numPerPage,
                curPage, cameraList);
        Log.i(Constants.LOG_TAG, "getCameraListFromCtrlUnit ret:" + ret);
        responseFlag = responseFlag || ret;
        if (!ret) {
            Log.e(Constants.LOG_TAG, "Invoke VMSNetSDK.getCameraListFromCtrlUnit failed:" + errDesc());
        }

        List<Object> allData = new ArrayList<Object>();
        allData.addAll(ctrlUnitList);
        allData.addAll(regionList);
        allData.addAll(cameraList);

        Log.i(Constants.LOG_TAG, "allData size is " + allData.size());
        if (callback != null) {
            callback.onMsg(responseFlag ? MsgIds.GET_SUB_F_C_SUC : MsgIds.GET_SUB_F_C_FAIL, allData);
        }
    }

    /**
     * 第一次请求资源列表
     */
    private void requestFirstList() {
        String servAddr = Config.getIns().getServerAddr();
        ServInfo loginData = TempData.getIns().getLoginData();
        if (loginData == null) {
            Log.i(Constants.LOG_TAG, "requestFirstList loginData:" + loginData);
            return;
        }
        String sessionID = loginData.getSessionID();
        int controlUnitID = 0;// 首次获取数据，表示根目录
        int numPerPage = 10000;// 此处传10000，由于实际不可能有那么多，表示获取所有数据
        int curPage = 1;
        List<ControlUnitInfo> ctrlUnitList = new ArrayList<ControlUnitInfo>();
        // 获取控制中心列表
        boolean ret = VMSNetSDK.getInstance().getControlUnitList(servAddr, sessionID, String.valueOf(controlUnitID), numPerPage,
                curPage, ctrlUnitList);
        Log.i(Constants.LOG_TAG, "getControlUnitList ret:" + ret);
        if (ctrlUnitList != null && !ctrlUnitList.isEmpty()) {
            for (ControlUnitInfo info : ctrlUnitList) {
                Log.i(Constants.LOG_TAG, "name:" + info.getName() + ",controlUnitID:" + info.getControlUnitID() + ",parentID:"
                        + info.getParentID());
            }
        }
        Log.i(Constants.LOG_TAG, "allData size is " + ctrlUnitList.size());
        if (!ret) {
            Log.e(Constants.LOG_TAG, "Invoke VMSNetSDK.getControlUnitList failed:" + errDesc());
        }
        if (callback != null) {
            callback.onMsg(ret ? MsgIds.GET_C_F_NONE_SUC : MsgIds.GET_C_F_NONE_FAIL, ctrlUnitList);
        }

    }

    /**
     * 错误描述
     * 
     * @return
     */
    private String errDesc() {
        return "errorDesc:" + VMSNetSDK.getInstance().getLastErrorDesc() + ",errorCode:"
                + VMSNetSDK.getInstance().getLastErrorCode();
    }
}
