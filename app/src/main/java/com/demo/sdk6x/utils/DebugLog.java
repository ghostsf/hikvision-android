package com.demo.sdk6x.utils;

import android.util.Log;

/**
 * 打印日志类
 * @author huangweifeng
 * @Data 2013-10-15
 */
public class DebugLog {
  
        /**
          * 设置是否打印日志
          * @param isPrintLog
          * void
          * @since V1.0
          */
        public static void setLogOption(boolean isPrintLog) {
            DEBUG = isPrintLog;
        }
        
        private static  boolean DEBUG = true;
        
        private DebugLog() {
        }
        
        /**
          * 这里对方法做描述
          * @param tag
          * @param desc
          * void
          * @since V1.0
          */
        public static void debug(String tag, String desc) {
            if (DEBUG) Log.d(tag, desc);
        }

        /**
          * 这里对方法做描述
          * @param tag
          * @param desc
          * void
          * @since V1.0
          */
        public static void verbose(String tag, String desc) {
            if (DEBUG) Log.v(tag, desc);
        }

        /**
          * 这里对方法做描述
          * @param tag
          * @param desc
          * void
          * @since V1.0
          */
        public static void warn(String tag, String desc) {
            if (DEBUG) Log.w(tag, desc);
        }

        /**
          * 这里对方法做描述
          * @param tag
          * @param desc
          * void
          * @since V1.0
          */
        public static void info(String tag, String desc) {
            if (DEBUG) Log.i(tag, desc);
        }

        /**
          * 这里对方法做描述
          * @param tag
          * @param desc
          * void
          * @since V1.0
          */
        public static void error(String tag, String desc) {
            if (DEBUG) Log.e(tag, desc);
        }
    
}
