/* 
 * @ProjectName iVMS-5060_V3.0
 * @Copyright HangZhou Hikvision System Technology Co.,Ltd. All Right Reserved
 * 
 * @FileName TimerUtil.java
 * @Description 这里对文件进行描述
 * 
 * @author mlianghua
 * @data Jul 12, 2012
 * 
 * @note 这里写本文件的详细功能描述和注释
 * @note 历史记录
 * 
 * @warning 这里写本文件的相关警告
 */
package com.demo.sdk6x.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.demo.sdk6x.R;



import android.content.Context;
import android.os.Handler;
import android.util.Log;

/**
 * 在此对类做相应的描述
 * 
 * @author mlianghua
 * @Data Jul 12, 2012
 */
public class TimerUtil {
    private static final String TAG = "TimerUtil";
    
	private static Handler  mTimeHandler   = null;
    private static boolean  mTimerMark     = false;
    private static Runnable mTimerRunnable = null;
    private static int      mUpdateTime    = 0;

    private TimerUtil() {
    };

    /**
     * 定时器开始定时
     * 
     * @param doThing 定时器处理事情
     * @param updateTime 定时器时间
     * @since V1.0
     */
    public static void startTime(Runnable doThing, int updateTime) {
        if (null == doThing || updateTime < 0) {
            return;
        }

        if (null == mTimeHandler) {
            mTimeHandler = new Handler();
            mTimerRunnable = doThing;
            mUpdateTime = updateTime;
            mTimerMark = true;
        }

        if (mTimerMark) {
            mTimeHandler.postDelayed(mTimerRunnable, 0);
        } else {
            mTimeHandler.postDelayed(null, 0);
        }
    }

    /**
     * 定时器开始定时
     * 
     * @param doThing 定时器处理事情
     * @param updateTime 定时器时间
     * @param mode 定时器模式
     * @since V1.0
     */
    public static void startTime(Runnable doThing, int updateTime, boolean mode) {
        if (null == doThing || updateTime < 0) {
            return;
        }

        if (null == mTimeHandler) {
            mTimeHandler = new Handler();
            mTimerRunnable = doThing;
            mUpdateTime = updateTime;
            mTimerMark = true;
        }

        if (mTimerMark) {
            if (mode) {
                mTimeHandler.postDelayed(mTimerRunnable, 0);
            } else {
                mTimeHandler.postDelayed(mTimerRunnable, updateTime);
            }
        } else {
            mTimeHandler.postDelayed(null, 0);
        }
    }
    
    /**
     * 这里对方法做描述
     * 
     * @since V1.0
     */
    public static void updataTime() {
        if (mTimerMark) {
            mTimeHandler.postDelayed(mTimerRunnable, mUpdateTime);
        } else {
            mTimeHandler.postDelayed(null, 0);
        }
    }

    /**
     * 这里对方法做描述
     * 
     * @since V1.0
     */
    public static void stopTime() {
        mTimerMark = false;
        if (null != mTimeHandler) {
            mTimeHandler.removeCallbacks(mTimerRunnable);
            mTimeHandler = null;
        }
    }
    
    /**
     * time格式 String型的日 如：昨天、今天、8月24日 15:30
     * @param ltime
     * @param context
     * @return
     * @since V1.0
     */
   public synchronized static String getTime_FormatTime_MMDD(long ltime, Context context) {
       String time = "";
       
       Calendar dateCalendar = Calendar.getInstance();
       dateCalendar.setTimeInMillis(ltime);
       
       Calendar targetCalendar = Calendar.getInstance();
       targetCalendar.set(Calendar.HOUR_OF_DAY, 0);
       targetCalendar.set(Calendar.MINUTE, 0);
       
       
       
       if (dateCalendar.after(targetCalendar)) {
           time = context.getString(R.string.today) +" "+ time;
           return time;
       } else {
           targetCalendar.add(Calendar.DATE, -1);
           if (dateCalendar.after(targetCalendar)) {
               time = context.getString(R.string.yesterday) +" "+ time;
               return time;
           }
       }
       
       String otherSDF = context.getString(R.string.mmdd);
       Date date = dateCalendar.getTime();
       SimpleDateFormat sfd = new SimpleDateFormat(otherSDF);
       time = sfd.format(date);
       Log.d(TAG, "getTime_FormatTime_MMDD time:"+time);
       return time;
   }
    
    /**
      * time格式 String型的日 如：昨天、今天、8月24日 15:30
      * @param ltime
      * @param context
      * @return
      * @since V1.0
      */
    public synchronized static String getTime_FormatTime_MMDDHHMM(long ltime, Context context) {
        String time = "";
        
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTimeInMillis(ltime);
        
        int month = dateCalendar.get(Calendar.MONTH) + 1;
        int day = dateCalendar.get(Calendar.DAY_OF_MONTH);
        int hour = dateCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = dateCalendar.get(Calendar.MINUTE);

        time = String.format("%02d:%02d", hour, minute);
        
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.set(Calendar.HOUR_OF_DAY, 0);
        targetCalendar.set(Calendar.MINUTE, 0);
        if (dateCalendar.after(targetCalendar)) {
            time = context.getString(R.string.today) +" "+ time;
            return time;
        } else {
            targetCalendar.add(Calendar.DATE, -1);
            if (dateCalendar.after(targetCalendar)) {
                time = context.getString(R.string.yesterday) +" "+ time;
                return time;
            }
        }
        
        time = String.format(Locale.ENGLISH,"%02d-%02d %02d:%02d", month, day, hour, minute);
        Log.d(TAG, "getTime_FormatTime_MMDDHHMM time:"+time);
        return time;
    }
    
    /**
     * 这里对方法做描述
     * 
     * @return time格式 nnnn-mm-dd hh:mm:ss
     * @since V1.0
     */
    public synchronized static String getTime_nnnnyydd(long t) {
        if (t == 0) {
            return "";
        }

        Calendar cale = Calendar.getInstance();
        cale.setTimeInMillis(t);
        int year = cale.get(Calendar.YEAR);
        int month = cale.get(Calendar.MONTH) + 1;
        int day = cale.get(Calendar.DAY_OF_MONTH);
        int hour = cale.get(Calendar.HOUR_OF_DAY);
        int minute = cale.get(Calendar.MINUTE);
        int second = cale.get(Calendar.SECOND);

        String time = String.format(Locale.ENGLISH, "%d-%02d-%02d %02d:%02d:%02d",  year, month, day, hour, minute, second);
        Log.d(TAG, "getTime_nnnnyydd time:"+time);
        return time;
    }
    
    /**
     * 这里对方法做描述
     * 
     * @return time格式 nnnn-mm-dd hh:mm:ss
     * @since V1.0
     */
    public synchronized static String getTime_nnnnyydd(Calendar cale) {
        if (cale == null) {
            return "";
        }
        
        int year = cale.get(Calendar.YEAR);
        int month = cale.get(Calendar.MONTH) + 1;
        int day = cale.get(Calendar.DAY_OF_MONTH);
        int hour = cale.get(Calendar.HOUR_OF_DAY);
        int minute = cale.get(Calendar.MINUTE);
        int second = cale.get(Calendar.SECOND);
        String time = String.format(Locale.ENGLISH,"%d-%02d-%02d %02d:%02d:%02d", year,month,day, hour, minute, second);
        Log.d(TAG, "getTime_nnnnyydd time:" + time);
        return time;
    }
}
