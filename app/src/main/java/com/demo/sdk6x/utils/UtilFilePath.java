package com.demo.sdk6x.utils;

import java.io.File;
import java.io.IOException;
/**
 * 获取抓拍、录像路径类
 * 
 * @author huangweifeng
 * @Data 2013-10-23
 */
public class UtilFilePath {

    /**
     * 获取图片目录
     * 
     * @return Pictrue dir path.
     * @since V1.0
     */
    public static File getPictureDirPath() {
        File SDFile = null;
        File mIVMSFolder = null;
        try {
            SDFile = android.os.Environment.getExternalStorageDirectory();
            String path = SDFile.getAbsolutePath() + File.separator + "HIKVISION";
            mIVMSFolder = new File(path);
            if ((null != mIVMSFolder) && (!mIVMSFolder.exists())) {
                mIVMSFolder.mkdir();
                mIVMSFolder.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mIVMSFolder;
    }

    /**
     * 获取录像目录
     * 
     * @return Video dir path.
     * @since V1.0
     */
    public static File getVideoDirPath() {
        File SDFile = null;
        File mIVMSFolder = null;
        try {
            SDFile = android.os.Environment.getExternalStorageDirectory();
            mIVMSFolder = new File(SDFile.getAbsolutePath() + File.separator + "HIKVISION");
            if ((null != mIVMSFolder) && (!mIVMSFolder.exists())) {
                mIVMSFolder.mkdir();
                mIVMSFolder.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mIVMSFolder;
    }
}
