package com.demo.sdk6x.live;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.demo.sdk6x.utils.DebugLog;
import com.hik.mcrsdk.talk.SDKTalkCallInfo;
import com.hik.mcrsdk.talk.SDKTalkLoginInfo;
import com.hik.mcrsdk.talk.TalkClientSDK;
import com.hik.mcrsdk.talk.TalkErrorListener;
import com.hik.mcrsdk.talk.TalkPCMDataLister;
import com.hik.mcrsdk.talk.module.AudioStreamManager;
import com.hik.mcrsdk.talk.module.AudioStreamManager.AudioCallBack;
import com.hik.mcrsdk.talk.module.GatherParams;

/**
 * 对讲控制层
 * 
 * @author weilinfeng
 * @Data 2014-6-17
 */
public class TalkControl {
    /** 变量/常量说明 */
    private static final String TAG                 = "TalkControl";
    /** 停止状态 */
    TalkState                   mTalkState          = TalkState.stoped;
    /** 变量/常量说明 */
    private TalkClientSDK       mTalkClientSDK      = null;
    /** 变量/常量说明 */
    private AudioStreamManager  mAudioStreamManager = null;
    /** 手机采集的PCM数据回调 */
    private AudioCallBack       mAudioCallBack      = new AudioCallBack() {
                                                        @Override
                                                        public void onPCMData(byte[] data, int length) {
                                                            mTalkClientSDK.inputAudioData(data, length);                                                            
                                                        }
                                                    };

    /** TALKSDK的PCM数据回调 */
    private TalkPCMDataLister   mTalkPCMDataLister  = new TalkPCMDataLister() {
                                                        @Override
                                                        public void onTalkPCMDataListener(byte[] data,
                                                                                          int len, long pUser) {
                                                            // DebugLog.error(TAG, "onTalkPCMDataListener()  data len:"+len);
                                                            // TODO Auto-generated method stub
                                                            mAudioStreamManager.inputPCMData(data, len);

                                                        }
                                                    };

    /** TALKSDK的错误回调 */
    private TalkErrorListener   mTalkErrorListener  = new TalkErrorListener() {
                                                        @Override
                                                        public void onTalkErrorListener(int errorCode, String describe,
                                                                                        long pUser) {
                                                            DebugLog.error(TAG, "onTalkPCMDataListener() errorCode:"
                                                                    + errorCode + " describe:" + describe);

                                                        }
                                                    };

    /**
     * 对讲控制层构造函数
     * 
     * @param liveActivity
     */
    public TalkControl() {
        mTalkClientSDK = TalkClientSDK.getInstance();
        mAudioStreamManager = AudioStreamManager.getInstance();
    }
    
    /**
     * 这里对方法做描述
     * 
     * @param talkInfo
     * @param userIndex
     * @return
     * @since V1.0
     */
    public boolean startTalk(TalkCallInfo talkInfo) {
        DebugLog.error(TAG, "startTalk()");

        if (mTalkState == TalkState.started) {
            DebugLog.error(TAG, "startTalk() be started");
            return true;
        }

        if (mTalkState != TalkState.stoped) {
            DebugLog.error(TAG, "startTalk() no stop state");
            return false;
        }

        // 开始对讲
        mTalkState = TalkState.starting;

        new AsyncTask<TalkCallInfo, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(TalkCallInfo... params) {
                DebugLog.error(TAG, "startTalk() doInBackground");
                if (params == null) {
                    DebugLog.error(TAG, "startTalk() doInBackground  params == null");
                    return false;
                }

                final SDKTalkLoginInfo sdkLoginInfo = getSDKTalkLoginInfo(params[0]);
                if (sdkLoginInfo == null) {
                    DebugLog.error(TAG, "startTalk()  getSDKTalkLoginInfo fail");
                    return false;
                }

                final SDKTalkCallInfo sdkTalkInfo = getSDKTalkCallInfo(params[0]);
                if (sdkTalkInfo == null) {
                    DebugLog.error(TAG, "startTalk()  getSDKTalkCallInfo fail");
                    return false;
                }

                boolean bRet = login(sdkLoginInfo);
                if (!bRet) {
                    DebugLog.error(TAG, "startTalk() login fail");
                    return false;
                }

                bRet = startAudioTalk(sdkTalkInfo);
                if (!bRet) {
                    DebugLog.error(TAG, "startTalk() startAudioTalk fail");
                    logout();
                    return false;
                }

                DebugLog.error(TAG, "startTalk() doInBackground success");
                return true;
            }

            protected void onPostExecute(Boolean result) {
                if (result) {
                    // 开始对讲成功
                    DebugLog.error(TAG, "startTalk()  success");
                } else {
                    // 开始对讲失败
                    DebugLog.error(TAG, "startTalk() fail");
                }

                // 开始对讲完成
                mTalkState = TalkState.started;
            };

        }.execute(talkInfo);

        DebugLog.error(TAG, "startTalk() complete");
        return true;
    }

    public boolean stopTalk() {
        DebugLog.error(TAG, "stopTalk()");
        if (mTalkState == TalkState.stoped) {
            DebugLog.error(TAG, "startTalk() be stoped");
            return true;
        }
        else if (mTalkState != TalkState.started) {
            DebugLog.error(TAG, "startTalk() no started");
            return false;
        }

        mTalkState = TalkState.stoping;

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                // 停止对讲
                stopAudioTalk();
                
                // 登出
                logout();
                
                return true;
            }

            protected void onPostExecute(Boolean result) {
                if (result) {
                    // 停止对讲成功
                    DebugLog.error(TAG, "stopTalk() success");
                }
                else {
                    // 停止对讲失败
                    DebugLog.error(TAG, "stopTalk() fail");
                }

                // 停止结束
                mTalkState = TalkState.stoped;
            };

        }.execute();

        DebugLog.error(TAG, "stopTalk() complete");
        return true;
    }
    
    /**
     * 重启对讲（不注销登录的情况下，停止上次对讲，开启本次对讲）
     * 
     * @param talkInfo
     * @param userIndex
     * @return
     * @since V1.0
     */
    public boolean reStartTalk(TalkCallInfo talkInfo) {
        DebugLog.error(TAG, "reStartTalk()");
        
        if (mTalkState == TalkState.starting) {
            DebugLog.error(TAG, "reStartTalk() be starting");
            return false;
        }
        
        if (mTalkState == TalkState.stoping) {
            DebugLog.error(TAG, "reStartTalk() be stoping");
            return false;
        }

        // 开始对讲
        mTalkState = TalkState.starting;
        
        new AsyncTask<TalkCallInfo, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(TalkCallInfo... params) {
                DebugLog.error(TAG, "startTalk() doInBackground");
                if (params == null) {
                    DebugLog.error(TAG, "startTalk() doInBackground  params == null");
                    return false;
                }

                final SDKTalkLoginInfo sdkLoginInfo = getSDKTalkLoginInfo(params[0]);
                if (sdkLoginInfo == null) {
                    DebugLog.error(TAG, "startTalk() doInBackground getSDKTalkLoginInfo fail");
                    return false;
                }

                final SDKTalkCallInfo sdkTalkInfo = getSDKTalkCallInfo(params[0]);
                if (sdkTalkInfo == null) {
                    DebugLog.error(TAG, "startTalk() doInBackground getSDKTalkCallInfo fail");
                    return false;
                }
                
                // 停止音频对讲
                stopAudioTalk();
                
                logout();
                
                boolean bRet = login(sdkLoginInfo);
                if (!bRet) {
                    DebugLog.error(TAG, "startTalk() doInBackground login fail");
                    return false;
                }
                
                // 开启对讲
                bRet = startAudioTalk(sdkTalkInfo);
                if (!bRet) {
                    DebugLog.error(TAG, "startTalk() doInBackground startAudioTalk fail");
                    logout();
                    return false;
                }

                DebugLog.error(TAG, "startTalk() doInBackground success");
                return true;
            }

            protected void onPostExecute(Boolean result) {
                if (result) {
                    // 开始对讲成功
                    DebugLog.error(TAG, "reStartTalk()  success");
                } else {
                    // 开始对讲失败
                    DebugLog.error(TAG, "reStartTalk()  fail");
                }

                // 开始对讲完成
                mTalkState = TalkState.started;
            };

        }.execute(talkInfo);

        DebugLog.error(TAG, "startTalk() complete");
        return true;
    }
    

    /**
     * 返回当前状态
     * 
     * @return
     * @since V1.0
     */
    public TalkState getState() {
        return mTalkState;
    }

    /**
     * 登陆对讲服务器
     * 
     * @return
     * @since V1.0
     */
    private boolean login(SDKTalkLoginInfo sdkLoginInfo) {
        DebugLog.error(TAG, "login()");
        if (sdkLoginInfo == null) {
            DebugLog.error(TAG, "login() sdkLoginInfo == null");
            return false;
        }

        // 设置错误回调
        boolean bRet = mTalkClientSDK.addTalkErrorListener(mTalkErrorListener);
        if (!bRet) {
            DebugLog.error(TAG, "login()  mTalkClientSDK addTalkErrorListener fail" + mTalkClientSDK.getErrorCode());
            return false;
        }

        bRet = mTalkClientSDK.login(sdkLoginInfo, 1);
        if (!bRet) {
            DebugLog.error(TAG, "login()  mTalkClientSDK login fail");
            return false;
        }

        DebugLog.error(TAG, "login() success");
        return true;
    }

    /**
     * 登出对讲服务器
     * 
     * @since V1.0
     */
    private void logout() {
        mTalkClientSDK.logout();
        DebugLog.error(TAG, "logout() success");
    }

    /**
     * 开始对讲
     * 
     * @return
     * @since V1.0
     */
    private boolean startAudioTalk(SDKTalkCallInfo sdkTalkInfo) {
        DebugLog.error(TAG, "startAudioTalk()");
        
     // 设置数据回调
        boolean bRet = mTalkClientSDK.addTalkPCMDataLister(mTalkPCMDataLister);
        if(!bRet) {
            DebugLog.error(TAG, "login()  mTalkClientSDK addTalkPCMDataLister fail"); 
            return false;
        }
        
        bRet = mTalkClientSDK.startTalk(sdkTalkInfo);
        if(!bRet) {
            DebugLog.error(TAG, "startTalk()  mTalkClientSDK startTalk fail"); 
            return false;
        }
        
        int encodeType = mTalkClientSDK.getDeviceEncodeType();
        if(encodeType == -1) {
            DebugLog.error(TAG, "startTalk()  getDeviceEncodeType fail encodeType:"+encodeType); 
            return false;
        }
        DebugLog.error(TAG, "startTalk()  getDeviceEncodeType encodeType:"+encodeType); 
        
        // 设置手机采集的PCM数据回调
        mAudioStreamManager.setCallBack(mAudioCallBack);

        GatherParams gatherParam = new GatherParams();
        if(encodeType == TalkClientSDK.TYPE_G711_A || encodeType == TalkClientSDK.TYPE_G711_U || encodeType == TalkClientSDK.TYPE_G726) {
            gatherParam.setSampleRate(GatherParams.SAMPLE_RATE_8000);
        }
        else if(encodeType == TalkClientSDK.TYPE_G722){
            gatherParam.setSampleRate(GatherParams.SAMPLE_RATE_16000);
        }
        
        gatherParam.setChannels(GatherParams.CHANNEL_MONO);
        gatherParam.setBitPerSample(GatherParams.PCM_16BIT);
        bRet = mAudioStreamManager.startGather(gatherParam);
        if(!bRet) {
            DebugLog.error(TAG, "startTalk()  mAudioStreamManager startGather fail"); 
            return false;
        }

        GatherParams playParam = new GatherParams();
        if(encodeType == TalkClientSDK.TYPE_G711_A || encodeType == TalkClientSDK.TYPE_G711_U || encodeType == TalkClientSDK.TYPE_G726) {
            playParam.setSampleRate(GatherParams.SAMPLE_RATE_8000);
        }
        else if(encodeType == TalkClientSDK.TYPE_G722){
            playParam.setSampleRate(GatherParams.SAMPLE_RATE_16000);
        }
        else {
            DebugLog.error(TAG, "startTalk()  mAudioStreamManager startGather fail"); 
            return false;
        }
        
        playParam.setChannels(GatherParams.CHANNEL_MONO);
        playParam.setBitPerSample(GatherParams.PCM_16BIT);
        bRet = mAudioStreamManager.startPlay(playParam);
        if(!bRet) {
            DebugLog.error(TAG, "startTalk()  mAudioStreamManager startPlay fail"); 
            return false;
        }
        
        DebugLog.error(TAG, "startTalk() success"); 
        return true;
    }

    /**
     * 停止对讲
     * 
     * @since V1.0
     */
    private void stopAudioTalk() {
        mTalkClientSDK.stopTalk();

        mAudioStreamManager.stopGather();

        mAudioStreamManager.stopPlay();

        DebugLog.error(TAG, "stopTalk() success");
    }

    /**
     * 获取对讲SDK登陆信息
     * 
     * @param loginInfo
     * @return
     * @since V1.0
     */
    private SDKTalkLoginInfo getSDKTalkLoginInfo(TalkCallInfo talkInfo) {
        if (talkInfo == null) {
            DebugLog.error(TAG, "getSDKTalkLoginInfo() talkInfo == null");
            return null;
        }

        SDKTalkLoginInfo sdkLoginInfo = new SDKTalkLoginInfo();
        // 设备PUID,如果是cu，则传cuUserID
        sdkLoginInfo.puid = talkInfo.userID;
        // 服务器IP，用MAG的IP
        sdkLoginInfo.servIP = talkInfo.servIP;
        // 注册服务器端口，MAG对讲端口
        sdkLoginInfo.servPort = talkInfo.servPort;
        // 客户端UserID，用登陆MSP的SessionID表示
        sdkLoginInfo.userID = talkInfo.userID;
        // 登录对讲服务器的密码，暂时不用，可以为空或者12345
        sdkLoginInfo.password = "12345";
        // 终端类型，0：单兵；1：客户端（包括mpu）；2：车载设
        sdkLoginInfo.type = TalkClientSDK.TYPE_CU;
        // 音频编码类型
        sdkLoginInfo.codecType = TalkClientSDK.TYPE_G711_A;

        return sdkLoginInfo;
    }

    /**
     * 获取对讲SDK呼叫信息
     * 
     * @param talkInfo
     * @return
     * @since V1.0
     */
    private SDKTalkCallInfo getSDKTalkCallInfo(TalkCallInfo talkInfo) {
        if (talkInfo == null) {
            DebugLog.error(TAG, "getSDKTalkCallInfo() talkInfo == null");
            return null;
        }

        SDKTalkCallInfo sdkTalkCallInfo = new SDKTalkCallInfo();
        // 客户端UserID，用登陆MSP的SessionID表示
        sdkTalkCallInfo.fromUserID = talkInfo.userID;
        // 接收方的UserID，使用对讲器的indexCode，用于对讲服务器对讲(目前接VAG可使用deviceIndexCode)
        sdkTalkCallInfo.toUserID = talkInfo.toUserID;
        // 设备序列号，用于VAG对讲(目前接VAG可使用deviceIndexCode)
        sdkTalkCallInfo.deviceIndexCode = talkInfo.toUserID;
        // 设备类型，用于VAG对讲30000
        sdkTalkCallInfo.deviceType = 30000;
        // 对讲通道编号，用于VAG对讲，目前统一传1
        sdkTalkCallInfo.channelNum = 1;

        return sdkTalkCallInfo;
    }

}
