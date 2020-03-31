package com.demo.sdk6x.playback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;

import com.demo.sdk6x.R;
import com.demo.sdk6x.constants.Constants;
import com.demo.sdk6x.data.Config;
import com.demo.sdk6x.data.TempData;
import com.demo.sdk6x.utils.DebugLog;
import com.demo.sdk6x.utils.TimerUtil;
import com.demo.sdk6x.utils.UIUtil;
import com.demo.sdk6x.utils.UtilAudioPlay;
import com.demo.sdk6x.utils.UtilFilePath;
import com.hik.mcrsdk.rtsp.ABS_TIME;
import com.hik.mcrsdk.rtsp.NCGPlaybackInfo;
import com.hik.mcrsdk.rtsp.PlaybackInfo;
import com.hik.mcrsdk.rtsp.RtspClient;
import com.hikvision.vmsnetsdk.CameraInfoEx;
import com.hikvision.vmsnetsdk.RecordInfo;
import com.hikvision.vmsnetsdk.ServInfo;
import com.hikvision.vmsnetsdk.VMSNetSDK;
import com.hikvision.vmsnetsdk.netLayer.mag.MAGServer;
import com.hikvision.vmsnetsdk.netLayer.msp.deviceInfo.DeviceInfo;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * 回放UI类
 * 
 * @author xiadaidai
 * @Data 2015-06-03
 */
public class PlayBackActivity extends Activity implements OnClickListener, PlayBackCallBack {

    /**
     * 日志
     */
    private static final String TAG             = "PlayBackActivity";
    /**
     * 播放视图控件
     */
    private SurfaceView         mSurfaceView;
    /**
     * 开始按钮
     */
    private Button              mStartButton;
    /**
     * 停止按钮
     */
    private Button              mStopButton;
    /**
     * 暂停按钮
     */
    private Button              mPauseButton;
    /**
     * 抓拍按钮
     */
    private Button              mCaptureButton;
    /**
     * 录像按钮
     */
    private Button              mRecordButton;
    /**
     * 音频按钮
     */
    private Button              mAudioButton;
    /** 等待框 */
    private ProgressBar         mProgressBar;
    /**
     * 控制层对象
     */
    private PlayBackControl mControl;
    /**
     * 创建消息对象
     */
    private Handler             mMessageHandler;
    /**
     * 回放时的参数对象
     */
    private PlayBackParams mParamsObj;
    /**
     * 是否暂停标签
     */
    private boolean             mIsPause;

    /**
     * 音频是否开启
     */
    private boolean             mIsAudioOpen;
    /**
     * 是否正在录像
     */
    private boolean             mIsRecord;

    private String              mCameraID;
    private VMSNetSDK mVmsNetSDK      = null;
    private String              mDeviceID       = "";
    
    private ServInfo mServInfo;
    /**
     * 监控点详细信息对象
     */
    private CameraInfoEx cameraInfoEx;
    /**
	 * 获取监控点详情结果
	 */
	private boolean getCameraDetailInfoResult = false;
	/**
	 * 获取设备详情结果
	 */
	private boolean getDeviceInfoResult = false;

	private DeviceInfo deviceInfo;
    private String                      serAddr                     = null;
    private String                      sessionid                   = null;

    private Calendar                    mPlaybackTime               = null;
    // 录像存储介质
    private int                         mPlaybackMedium             = -1;
    private List<Integer>               mPlaybackMediums            = null;

    /**
     * 回放地址
     */
    private String                      mPlaybackUrl                = "";
    /** 起始时间 */
    private Calendar                    mStartCalendar              = null;
    /** 结束时间 */
    private Calendar                    mEndCalendar                = null;
	private RecordInfo mRecordInfo                 = null;

    private String ncg_beginTime;

    private String ncg_endTime;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playback_activity);
        
        getPlayBackInfo();
        
        initPlayback();
        
        setUpView();

        init();
        queryPlaybackInfo();
    }
    
    /**
     * 初始化回放
     * 
     * @since V1.0
     */
    private void initPlayback() {
        mPlaybackTime = getDefaultCurrentTime();
        mPlaybackTime.set(Calendar.HOUR_OF_DAY, 0);
        mPlaybackTime.set(Calendar.MINUTE, 0);
        mPlaybackTime.set(Calendar.SECOND, 0);
        mPlaybackTime.set(Calendar.MILLISECOND, 0);
    }
    
    /**
     * 获取默认的开始时间(获取具体年月日)
     * 
     * @return
     * @since V1.0
     */
    private Calendar getDefaultCurrentTime() {
        /* 默认时间为0 */
        Calendar currentTimeCalendar = Calendar.getInstance();
        currentTimeCalendar.set(Calendar.HOUR_OF_DAY, 0);
        currentTimeCalendar.set(Calendar.MINUTE, 0);
        currentTimeCalendar.set(Calendar.SECOND, 0);
        currentTimeCalendar.set(Calendar.MILLISECOND, 0);
        return currentTimeCalendar;
    }
    
	/**
     * 该方法用来获取回放的信息，使用者自己实现 void
     * 
     * @since V1.0
     */
    private void getPlayBackInfo() {
        mCameraID = getIntent().getStringExtra(Constants.IntentKey.CAMERA_ID);
    }
    
    /**
     * 初始化
     * 
     * @since V1.0
     */
    private void init() {
        // 打开日志的开关
        DebugLog.setLogOption(true);
        mMessageHandler = new MyHandler();
        // 创建和cms平台交互的对象
        mVmsNetSDK = VMSNetSDK.getInstance();
//        // 初始化远程回放控制层对象
        mControl = new PlayBackControl();
        // 设置远程回放控制层回调
        mControl.setPlayBackCallBack(this);
        // 创建远程回放需要的参数
        mParamsObj = new PlayBackParams();
        // 播放控件
        mParamsObj.surfaceView = mSurfaceView;
        
        mServInfo = TempData.getIns().getLoginData();
//        cameraInfo = TempData.getIns().getCameraInfo();
        
        cameraInfoEx = new CameraInfoEx();
		cameraInfoEx.setId(mCameraID);
		
		serAddr = Config.getIns().getServerAddr();
		sessionid = mServInfo.getSessionID();
    }
    
    /**
     * 进行远程回放录像查询
     * 
     * @since V1.0
     */
    public void queryPlaybackInfo() {
        new Thread(new Runnable() {

            @Override
            public void run() {
            	
            	getCameraDetailInfoResult = PlayBackActivity.this.mVmsNetSDK.getCameraInfoEx(serAddr, sessionid,
						mCameraID, cameraInfoEx);
				Log.i(Constants.LOG_TAG,
						"result is :" + getCameraDetailInfoResult);
				
				mDeviceID = cameraInfoEx.getDeviceId();
				Log.i(Constants.LOG_TAG, "mDeviceID is :" + mDeviceID);
				deviceInfo = new DeviceInfo();

				//获取设备信息
				getDeviceInfoResult = PlayBackActivity.this.mVmsNetSDK.getDeviceInfo(serAddr, sessionid, mDeviceID,
						deviceInfo);
				if (!getDeviceInfoResult || null == deviceInfo) {
					deviceInfo.setLoginName("admin");
					deviceInfo.setLoginPsw("12345");
				}
				
				mParamsObj.name = deviceInfo.getLoginName();
                mParamsObj.passwrod = deviceInfo.getLoginPsw();

				DebugLog.info(Constants.LOG_TAG, "ret is :" + getDeviceInfoResult + "----------------"
						+ deviceInfo.getDeviceName() + "--------"
						+ "deviceLoginName is " + mParamsObj.name + "---"
						+ "deviceLoginPassword is " + mParamsObj.passwrod + "-----"
						+ "deviceID is " + mDeviceID);
            	
                mStartCalendar = Calendar.getInstance();
                mEndCalendar = Calendar.getInstance();
            
                /* 其他回放录像,默认回放起始时间为0 */
                mStartCalendar.setTimeInMillis(mPlaybackTime.getTimeInMillis());
                Log.d(TAG, "queryPlaybackTimes,startTimeCalendar:" + TimerUtil.getTime_nnnnyydd(mStartCalendar));

                /* 回放结束时间重置为起始时间后的23小时59分59秒 */
                mEndCalendar.setTimeInMillis(mStartCalendar.getTimeInMillis() + 24 * 3600 * 1000 - 1000);
                Log.d(TAG, "queryPlaybackTimes,endTimeCalendar:" + TimerUtil.getTime_nnnnyydd(mEndCalendar));
                
                // 查询录像库中的时间对象，注意Calendar时间，使用前请先了解下Calendar
                com.hikvision.vmsnetsdk.ABS_TIME startTime = new com.hikvision.vmsnetsdk.ABS_TIME(mStartCalendar);
                com.hikvision.vmsnetsdk.ABS_TIME endTime = new com.hikvision.vmsnetsdk.ABS_TIME(mEndCalendar);

                setParamsObjTime(startTime, endTime);
                mRecordInfo = new RecordInfo();
                if (mVmsNetSDK == null) {
                    Log.e(Constants.LOG_TAG, "mVmsNetSDK is " + null);
                    return;
                }
                
                boolean ret = queryRecord(startTime, endTime, mRecordInfo);
                if (!ret) {
                    Log.e(TAG, "queryPlaybackTimes queryRecordByMedium fail");
                    return ;
                }
                
                if (mRecordInfo != null) {
                    Log.i(Constants.LOG_TAG, "segmentListPlayUrl : " + mRecordInfo.segmentListPlayUrl);
                }
            }
        }).start();
    }

    private boolean queryRecord(com.hikvision.vmsnetsdk.ABS_TIME startTime
    		, com.hikvision.vmsnetsdk.ABS_TIME endTime, RecordInfo recordInfo) {
    	
    	if (startTime == null || endTime == null) {
            Log.e(TAG, "queryPlaybackTimes play time is null");
            return false;
        }

        if (recordInfo == null) {
            Log.e(TAG, "queryPlaybackTimes recordInfo == null");
            return false;
        }
        // cms平台地址
        String servHeadAddr = mServInfo.getMagServer().getMagHttpRequestAddrHead(true);
        Log.e(TAG, "servHeadAddr == " + servHeadAddr);
        // 查询的录像类型，1-计划录像，2-移动录像，16-手动录像，4-报警录像
        String recordType = "1,2,4,16";
        
        List<Integer> recordPoses = null;
        if (null != cameraInfoEx) {
            recordPoses = cameraInfoEx.getRecordPos();
        }

        if ((null == recordPoses) || (recordPoses.size() == 0)) {
            Log.e(TAG, "queryPlaybackTimes queryCameraRecord null == recordPos");
            sendMessageCase(ConstantPlayBack.GET_RECORD_FILE_FAIL);
            return false;
        }

        mPlaybackMediums = recordPoses;
        int i = 0;
        while (true) {

            /* 查询录像失败 */
            if (i == recordPoses.size()) {
                /* 查询录像失败 */
                int errorCode = PlayBackActivity.this.mVmsNetSDK.getLastErrorCode();
                if (errorCode == VMSNetSDK.VMSNETSDK_MSP_NO_DATA) {
                	Log.e(TAG, "查询录像失败，没有录像文件");
                } else if (errorCode == VMSNetSDK.VMSNETSDK_MSP_SESSION_ERROR) {
                	Log.e(TAG, "查询录像失败，sessionid异常");
                } else {
                	Log.e(TAG, "查询录像失败，错误码：" + PlayBackActivity.this.mVmsNetSDK.getLastErrorCode());
                }
                Log.e(TAG, "queryPlaybackTimes queryCameraRecord fail, errorCode:" + PlayBackActivity.this.mVmsNetSDK.getLastErrorCode());
                
                sendMessageCase(ConstantPlayBack.GET_RECORD_FILE_FAIL);
                return false;
            }

            mPlaybackMedium = recordPoses.get(i);
            recordInfo.recSegmentList.clear();

            int cascadeFlag = cameraInfoEx.getCascadeFlag();
            boolean ret =false;
            if(cascadeFlag == 0){//非级联
                ret = PlayBackActivity.this.mVmsNetSDK.queryCameraRecord(servHeadAddr,
                        sessionid, mCameraID, recordType,
                        String.valueOf(mPlaybackMedium), startTime, endTime, recordInfo);
            }else{//级联
                String begin = getPlaybackTime(mStartCalendar);
                String end = getPlaybackTime(mEndCalendar);
                Log.d(TAG, "mPlaybackMedium:" + mPlaybackMedium);
                ret = PlayBackActivity.this.mVmsNetSDK.queryNcgCameraRecord(
                        servHeadAddr,
                        sessionid,
                        mCameraID,
                        23,
                        mPlaybackMedium,
                        begin,
                        end,
                        recordInfo);
            }
//            boolean ret = PlayBackActivity.this.mVmsNetSDK.queryCameraRecord(servHeadAddr, sessionid, mCameraID, recordType,
//                    String.valueOf(mPlaybackMedium), startTime, endTime, recordInfo);
            if (ret) {
                Log.d(TAG, "queryPlaybackTimes queryCameraRecord success, mPlaybackMediums:" + mPlaybackMediums);
                if (recordInfo != null && recordInfo.recSegmentList != null)
                {
                    //add by zhoujian reason:级联回放
                    if(cascadeFlag==1){
                        ncg_beginTime = recordInfo.recSegmentList.get(0).getNcg_startTime();
                        ncg_endTime = recordInfo.recSegmentList.get(recordInfo.recSegmentList.size() - 1).getNcg_endTime();
                    }
                    //add by zhoujian reason:级联回放
                }
                break;
            }

            i++;
        }
        
        sendMessageCase(ConstantPlayBack.GET_RECORD_FILE_SUCCESS);
        
        
        return true;
    }
    
    private void setPlaybackUrl(String url, Calendar startTime, Calendar endTime) {
        if (null == url || url.equals("") || null == startTime || null == endTime) {
            return;
        }
        
    	//获取播放Token
  		String mToken = PlayBackActivity.this.mVmsNetSDK.getPlayToken(mServInfo.getSessionID());
  		DebugLog.info(Constants.LOG_TAG, "mToken is :" + mToken);
       		
        if (mServInfo != null) {
            PlaybackInfo playbackInfo = new PlaybackInfo();
            MAGServer magserver = mServInfo.getMagServer();
            if (magserver != null && magserver.getMagStreamSerAddr() != null) {
                playbackInfo.setMagIp(magserver.getMagStreamSerAddr());
                playbackInfo.setMagPort(magserver.getMagStreamSerPort());
                Log.d(TAG, "setPlaybackUrl magIP:" + magserver.getMagStreamSerAddr());
                Log.d(TAG, "setPlaybackUrl magPort:" + magserver.getMagStreamSerPort());
            }
            int magVersion = VMSNetSDK.getInstance().isNewMagVersion();
            playbackInfo.setMagVersion(magVersion);
            playbackInfo.setPlaybackUrl(url);
            playbackInfo.setToken(mToken);
            playbackInfo.setBegin(getPlaybackTime(startTime));
            playbackInfo.setEnd(getPlaybackTime(endTime));
            playbackInfo.setmcuNetID(mServInfo.getAppNetId());
            Log.d(TAG, "setPlaybackUrl url:" + url);
            Log.d(TAG, "setPlaybackUrl token:" + mToken);
            mPlaybackUrl = RtspClient.getInstance().generatePlaybackUrl(playbackInfo);
        }

        Log.e(TAG, "setPlaybackUrl() PlaybackUrl:" + mPlaybackUrl);
    }
    
    //格式化时间数据
    private String getPlaybackTime(Calendar calendar)
    {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        String format_month = formatDate(month);//格式为00;
        String format_day= formatDate(day);//格式为00;
        String format_hour= formatDate(hour);//格式为00;
        String format_minute= formatDate(minute);//格式为00;
        String format_second= formatDate(second);//格式为00;
        StringBuffer buffer = new StringBuffer();
        buffer.append(year)
            .append(format_month)
            .append(format_day)
            .append("T")
            .append(format_hour)
            .append(format_minute)
            .append(format_second)
            .append("Z");
        return buffer.toString();
    }
    
    
    private String formatDate(int date)
    {
        String format_date = ""+date;
        if(date<10){
            format_date = "0"+date;
        }
        return format_date;
    }
    
    /**
     * 设置远程回放取流的开始时间和结束时间
     * 
     * @param startTime
     * @param endTime
     * @since V1.0
     */
    protected void setParamsObjTime(com.hikvision.vmsnetsdk.ABS_TIME startTime, com.hikvision.vmsnetsdk.ABS_TIME endTime) {
        if (startTime == null || endTime == null) {
            Log.e(Constants.LOG_TAG, "setParamsObjTime():: startTime is " + startTime + "endTime is " + endTime);
        }
        // 取流库中的时间对象
        ABS_TIME rtspEndTime = new ABS_TIME();
        ABS_TIME rtspStartTime = new ABS_TIME();

        // 设置播放结束时间
        rtspEndTime.setYear(endTime.dwYear);
        // 之所以要加1，是由于我们查询接口中的时间和取流中的时间采用的是两个自定义的时间类，这个地方开发者按照demo中实现就可以了。
        rtspEndTime.setMonth(endTime.dwMonth + 1);
        rtspEndTime.setDay(endTime.dwDay);
        rtspEndTime.setHour(endTime.dwHour);
        rtspEndTime.setMinute(endTime.dwMinute);
        rtspEndTime.setSecond(endTime.dwSecond);

        // 设置开始播放时间
        rtspStartTime.setYear(startTime.dwYear);
        // 之所以要加1，是由于我们查询接口中的时间和取流中的时间采用的是两个自定义的时间类，这个地方开发者按照demo中实现就可以了。
        rtspStartTime.setMonth(startTime.dwMonth + 1);
        rtspStartTime.setDay(startTime.dwDay);
        rtspStartTime.setHour(startTime.dwHour);
        rtspStartTime.setMinute(startTime.dwMinute);
        rtspStartTime.setSecond(startTime.dwSecond);

        if (mParamsObj != null) {
            // 设置开始远程回放的开始时间和结束时间。
            mParamsObj.endTime = rtspEndTime;
            mParamsObj.startTime = rtspStartTime;
        }
    }
    
    /**
     * 初始化控件 void
     * 
     * @since V1.0
     */
    private void setUpView() {
        mSurfaceView = (SurfaceView) findViewById(R.id.playbackSurfaceView);

        mStartButton = (Button) findViewById(R.id.playBackStart);
        mStartButton.setOnClickListener(this);

        mStopButton = (Button) findViewById(R.id.playBackStop);
        mStopButton.setOnClickListener(this);

        mPauseButton = (Button) findViewById(R.id.playBackPause);
        mPauseButton.setOnClickListener(this);

        mCaptureButton = (Button) findViewById(R.id.playBackCapture);
        mCaptureButton.setOnClickListener(this);

        mRecordButton = (Button) findViewById(R.id.playBackRecord);
        mRecordButton.setOnClickListener(this);

        mAudioButton = (Button) findViewById(R.id.playBackRadio);
        mAudioButton.setOnClickListener(this);

        mProgressBar = (ProgressBar) findViewById(R.id.playBackProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playBackStart:
                startBtnOnClick();
            break;

            case R.id.playBackStop:
                stopBtnOnClick();
            break;

            case R.id.playBackPause:
                pauseBtnOnClick();
            break;

            case R.id.playBackCapture:
                captureBtnOnClick();
            break;

            case R.id.playBackRecord:
                recordBtnOnClick();
            break;

            case R.id.playBackRadio:
                audioBtnOnClick();
            break;

        }
    }

    /**
     * 音频按钮 void
     * 
     * @since V1.0
     */
    private void audioBtnOnClick() {
        if (null != mControl) {
            if (mIsAudioOpen) {
                mControl.stopAudio();
                mIsAudioOpen = false;
                UIUtil.showToast(PlayBackActivity.this, "关闭音频");
                mAudioButton.setText("开启音频");
            } else {
                boolean ret = mControl.startAudio();
                if (!ret) {
                    mIsAudioOpen = false;
                    UIUtil.showToast(PlayBackActivity.this, "开启音频失败");
                } else {
                    mIsAudioOpen = true;
                    // 开启音频成功，并不代表一定有声音，需要设备开启声音。
                    UIUtil.showToast(PlayBackActivity.this, "开启音频成功");
                    mAudioButton.setText("关闭音频");
                }
            }
        }
    }

    /**
     * 启动播放 void
     * 
     * @since V1.0
     */
    private void startBtnOnClick() {
        mProgressBar.setVisibility(View.VISIBLE);
        
        if (null != mProgressBar) {
            new Thread() {
                @Override
                public void run() {
                	super.run();
                    if (null != mControl) {
                        /*start modify by zhoujian reason：级联回放*/
                        int cascadeFlag = cameraInfoEx.getCascadeFlag();
                        if(cascadeFlag == 0){//非级联
                            setPlaybackUrl(mRecordInfo.segmentListPlayUrl, mStartCalendar, mEndCalendar);
                        }else{
                            setNcgPlaybackUrl(mCameraID,mStartCalendar, mEndCalendar);
                        }
//                        setPlaybackUrl(mRecordInfo.segmentListPlayUrl, mStartCalendar, mEndCalendar);
                        mParamsObj.url = mPlaybackUrl;
                    	if(mControl.PLAYBACK_PLAY == mControl.getPlayBackState()){
                    		mControl.stopPlayBack();
                    	}
                    	
                    	if(mControl.PLAYBACK_INIT == mControl.getPlayBackState() 
                    			|| mControl.PLAYBACK_RELEASE == mControl.getPlayBackState()){
                    		mControl.startPlayBack(mParamsObj);
                    	}
                    }
                }
            }.start();
        }
    }

    /**
     * 停止播放 void
     * 
     * @since V1.0
     */
    private void stopBtnOnClick() {
        if (null != mControl) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    mControl.stopPlayBack();
                }
            }.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mControl.stopPlayBack();
    }

    /**
     * 暂停、回放播放 void
     * 
     * @since V1.0
     */
    private void pauseBtnOnClick() {
        if (null != mControl) {
            new Thread() {
                @Override
                public void run() {
                    if (!mIsPause) {
                        mControl.pausePlayBack();
                    } else {
                        mControl.resumePlayBack();
                    }
                    super.run();
                }
            }.start();
        }
    }

    /**
     * 抓拍 void
     * 
     * @since V1.0
     */
    private void captureBtnOnClick() {
        if (null != mControl) {
            // 随即生成一个1到10000的数字，用于抓拍图片名称的一部分，区分图片
            int recordIndex = new Random().nextInt(10000);
            boolean ret = mControl.capture(UtilFilePath.getPictureDirPath().getAbsolutePath(), "Picture" + recordIndex
                    + ".jpg");
            if (ret) {
                UIUtil.showToast(PlayBackActivity.this, "抓拍成功");
                UtilAudioPlay.playAudioFile(PlayBackActivity.this, R.raw.paizhao);
            } else {
                UIUtil.showToast(PlayBackActivity.this, "抓拍失败");
                DebugLog.error(TAG, "captureBtnOnClick():: 抓拍失败");
            }
        }
    }

    /**
     * 录像 void
     * 
     * @since V1.0
     */
    private void recordBtnOnClick() {
        if (null != mControl) {
            if (!mIsRecord) {                
             // 随即生成一个1到10000的数字，用于录像名称的一部分，区分图片，开发者可以根据实际情况修改区分录像名称的方法
				int recordIndex = new Random().nextInt(10000);
				boolean ret = mControl.startRecord(UtilFilePath.getVideoDirPath()
						.getAbsolutePath(), "Video" + recordIndex + ".mp4");
				if (ret) {
					UIUtil.showToast(PlayBackActivity.this, "启动录像成功");
					mIsRecord = true;
					mRecordButton.setText("停止录像");
				} else {
					UIUtil.showToast(PlayBackActivity.this, "启动录像失败");
					DebugLog.error(Constants.LOG_TAG, "recordBtnOnClick():: 启动录像失败");
				}
            } else {
                mControl.stopRecord();
                mIsRecord = false;
                UIUtil.showToast(PlayBackActivity.this, "停止录像成功");
                mRecordButton.setText("开始录像");
            }
            
            
            
        }
    }

    @Override
    public void onMessageCallback(int message) {
        sendMessageCase(message);
    }

    /**
     * 发送消息
     * 
     * @param i void
     * @since V1.0
     */
    private void sendMessageCase(int i) {
        if (null != mMessageHandler) {
            Message msg = Message.obtain();
            msg.arg1 = i;
            mMessageHandler.sendMessage(msg);
        }
    }

    @SuppressLint("HandlerLeak")
    class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case ConstantPlayBack.START_RTSP_SUCCESS:
                    UIUtil.showToast(PlayBackActivity.this, "启动取流库成功");
                break;

                case ConstantPlayBack.START_RTSP_FAIL:
                    UIUtil.showToast(PlayBackActivity.this, "启动取流库失败");
                    if (null != mProgressBar) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                break;

                case ConstantPlayBack.PAUSE_SUCCESS:
                    UIUtil.showToast(PlayBackActivity.this, "暂停成功");
                    mPauseButton.setText("恢复");
                    mIsPause = true;
                break;

                case ConstantPlayBack.PAUSE_FAIL:
                    UIUtil.showToast(PlayBackActivity.this, "暂停失败");
                    mPauseButton.setText("暂停");
                    mIsPause = false;

                break;

                case ConstantPlayBack.RESUEM_FAIL:
                    UIUtil.showToast(PlayBackActivity.this, "恢复播放失败");
                    mPauseButton.setText("恢复");
                    mIsPause = true;
                break;

                case ConstantPlayBack.RESUEM_SUCCESS:
                    UIUtil.showToast(PlayBackActivity.this, "恢复播放成功");
                    mPauseButton.setText("暂停");
                    mIsPause = false;
                break;

                case ConstantPlayBack.START_OPEN_FAILED:
                    UIUtil.showToast(PlayBackActivity.this, "启动播放库失败");
                    if (null != mProgressBar) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                break;

                case ConstantPlayBack.PLAY_DISPLAY_SUCCESS:
                    if (null != mProgressBar) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                break;
                case ConstantPlayBack.CAPTURE_FAILED_NPLAY_STATE:
                    UIUtil.showToast(PlayBackActivity.this, "非播放状态不能抓怕");
                break;
                case ConstantPlayBack.PAUSE_FAIL_NPLAY_STATE:
                    UIUtil.showToast(PlayBackActivity.this, "非播放状态不能暂停");
                break;
                case ConstantPlayBack.RECORD_FAIL_NPLAY_STATE:
    				UIUtil.showToast(PlayBackActivity.this, "非播放状态不能录像");
    			break;
    			case ConstantPlayBack.AUDIO_START_FAIL_NPLAY_STATE:
    				UIUtil.showToast(PlayBackActivity.this, "非播放状态不能开启音频");
    			break;
                case ConstantPlayBack.RESUEM_FAIL_NPAUSE_STATE:
                    UIUtil.showToast(PlayBackActivity.this, "非播放状态");
                break;
                case ConstantPlayBack.GET_RECORD_FILE_SUCCESS:
                	UIUtil.showToast(PlayBackActivity.this, "获取录像文件成功");
                	if (null != mProgressBar) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                	mStartButton.setEnabled(true);
                    mStopButton.setEnabled(true);
                    mPauseButton.setEnabled(true);
                    mCaptureButton.setEnabled(true);
                    mRecordButton.setEnabled(true);
                    mAudioButton.setEnabled(true);
                break;
                case ConstantPlayBack.GET_RECORD_FILE_FAIL:
                	UIUtil.showToast(PlayBackActivity.this, "获取录像文件失败");
                	if (null != mProgressBar) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                break;
                
                case ConstantPlayBack.GET_RECORD_FILE_NULL:
                	UIUtil.showToast(PlayBackActivity.this, "查询录像文件失败");
                	if (null != mProgressBar) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                	break;
                case ConstantPlayBack.STOP_SUCCESS:
                	UIUtil.showToast(PlayBackActivity.this, "停止成功");
                break;
                case RtspClient.RTSPCLIENT_MSG_CONNECTION_EXCEPTION:
                    if (null != mProgressBar) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                    UIUtil.showToast(PlayBackActivity.this, "RTSP链接异常");
                break;
                
            }
        }
    }

    /**
     * 级联回放地址组装方法
     * @param cameraId 监控点ID
     * @param startTimeCalendar 开始时间
     * @param endTimeCalendar 结束时间
     */
    private void setNcgPlaybackUrl(String cameraId,Calendar startTimeCalendar, Calendar endTimeCalendar)
    {
        if (null == cameraId || cameraId.equals("") || null == startTimeCalendar || null == endTimeCalendar)
        {
            return;
        }
        String token = null;
        // 如果需要在做校验，怎获取tokenId
        if (mServInfo != null && mServInfo.isTokenVerify())
        {
            token = VMSNetSDK.getInstance().getPlayToken(mServInfo.getSessionID());
            if (token == null || token.equalsIgnoreCase(""))
            {
                Log.e(TAG, "setNcgPlaybackUrl() getPlayToken failed! and ErrorCode: "
                        + VMSNetSDK.getInstance().getLastErrorCode());
            }
        }
        mPlaybackUrl = "";
//        deviceInfo = new DeviceInfo();
//		//获取设备信息
//		getDeviceInfoResult = PlayBackActivity.this.mVmsNetSDK.getDeviceInfo(serAddr, sessionid, mDeviceID,
//				deviceInfo);
//		if (!getDeviceInfoResult || null == deviceInfo || deviceInfo.equals("")||
//				TextUtils.isEmpty(deviceInfo.getLoginName())
//				||TextUtils.isEmpty(deviceInfo.getLoginPsw())) {
//			deviceInfo.setLoginName("admin");
//			deviceInfo.setLoginPsw("12345");
//		}

        //获取设备信息
        if(null == deviceInfo || deviceInfo.equals("")||
                TextUtils.isEmpty(deviceInfo.getLoginPsw())||
                TextUtils.isEmpty(deviceInfo.getLoginName())){
            deviceInfo.setLoginName("admin");
            deviceInfo.setLoginPsw("12345");
        }
        //modify add
        if (mServInfo != null)
        {
            MAGServer magserver = mServInfo.getMagServer();
            if (magserver != null && magserver.getMagStreamSerAddr() != null)
            {
                String ip = magserver.getMagStreamSerAddr();
                int port = magserver.getMagStreamSerPort();
                Log.d(TAG, "setNcgPlaybackUrl magIP:" + magserver.getMagStreamSerAddr());
                Log.d(TAG, "setNcgPlaybackUrl magPort:" + magserver.getMagStreamSerPort());
                NCGPlaybackInfo ncgPlaybackInfo =new NCGPlaybackInfo();
                ncgPlaybackInfo.setBegin(ncg_beginTime);
                ncgPlaybackInfo.setCameraIndexCode(cameraId);
                ncgPlaybackInfo.setEnd(ncg_endTime);
                ncgPlaybackInfo.setFileName("record.mp4");
                ncgPlaybackInfo.setMagIp(ip);
                ncgPlaybackInfo.setMagPort(port);
                ncgPlaybackInfo.setRecordPos(mPlaybackMedium);
                ncgPlaybackInfo.setRecordType(23);
                ncgPlaybackInfo.setToken(token);
                StringBuilder urlBuilder = new StringBuilder();
                mPlaybackUrl = RtspClient.getInstance().generateNCGPlaybackUrl(ncgPlaybackInfo);
                urlBuilder.append(mPlaybackUrl)
                        .append("&cnid=").append(mServInfo.getAppNetId())
                        .append("|&startTime=").append(startTimeCalendar.getTimeInMillis())
                        .append("&endTime=").append(endTimeCalendar.getTimeInMillis())
                        .append("&deviceName=").append(deviceInfo.getLoginName()).append("&devicePassword=")
                        .append(deviceInfo.getLoginPsw());
                mPlaybackUrl = urlBuilder.toString();
                Log.d(TAG, "setNcgPlaybackUrl mPlaybackUrl:" + mPlaybackUrl);
            }
        }
    }
}
