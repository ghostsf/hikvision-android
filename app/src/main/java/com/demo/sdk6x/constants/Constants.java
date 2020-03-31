package com.demo.sdk6x.constants;

public final class Constants {
    private Constants() {
    }

    /**
     * 日志tag名
     */
    public static String LOG_TAG = "TFNTEST";

    /**
     * Intent key常量
     */
    public static interface IntentKey {
        /**
         * 控制中心id
         */
        String CONTROL_UNIT_ID = "control_unit_id";
        /**
         * 区域id
         */
        String REGION_ID       = "region_id";
        /**
         * 监控点id
         */
        String CAMERA_ID       = "camera_id";
        /** 设备ID*/
        String DEVICE_ID       = "device_id";
    }

    public static interface Resource {
        /**
         * 控制中心
         */
        int TYPE_CTRL_UNIT = 1;
        /**
         * 区域
         */
        int TYPE_REGION    = 2;
        /**
         * 未知
         */
        int TYPE_UNKNOWN   = 3;
    }

    /**
     * 登录逻辑相关常量
     */
    public static interface Login {

        /**
         * 显示进度
         */
        int SHOW_LOGIN_PROGRESS   = 2;
        /**
         * 取消进度提示
         */
        int CANCEL_LOGIN_PROGRESS = 3;

        /**
         * 登录成功
         */
        int LOGIN_SUCCESS         = 4;
        /**
         * 登录失败
         */
        int LOGIN_FAILED          = 5;
        /**
         * 登录失败,携带原因码
         */
        int LOGIN_FAIL_WITH_CODE = 6;
        /**
         * 需要显示验证码
         */
        int LOGIN_NEED_VERIFCODE = 7;
        /**
         * 验证码错误
         */
        int LOGIN_CHECK_FAIL_VERIFYCODE_ERR = 8;
        /**
         * 验证码无效
         */
        int LOGIN_CHECK_FAIL_VERIFYCODE_INVALIDE = 9;
    }


    public interface LoginCode{
        int SUCCESS_LOGIN = 200;
        int SUCCESS_CODE_FIRST_LOGIN = 20030;
        int SUCCESS_CODE_PWD_WEAK = 20031;
        int SUCCESS_CODE_PWD_STALE = 20032;
    }
}
