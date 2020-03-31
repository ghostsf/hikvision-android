/**
 * 
 */
package com.demo.sdk6x.callback;

/**
 * 消息id常量类
 * 
 * @author zhoudaihui
 * 
 */
public interface MsgIds {
	/**
	 * 获取控制中心列表成功
	 */
	int GET_C_F_NONE_SUC = 0x00;
	/**
	 * 获取控制中心列表失败
	 */
	int GET_C_F_NONE_FAIL = 0x01;
	/**
	 * 调用getControlUnitList失败
	 */
	int GET_CU_F_CU_FAIL = 0x02;
	/**
	 * 调用getRegionListFromCtrlUnit失败
	 */
	int GET_R_F_C_FAIL = 0x03;
	/**
	 * 调用getCameraListFromCtrlUnit失败
	 */
	int GET_C_F_C_FAIL = 0x04;
	/**
	 * 从控制中心获取下级资源列表成功
	 */
	int GET_SUB_F_C_SUC = 0x05;
	/**
	 * 从控制中心获取下级资源列表成失败
	 */
	int GET_SUB_F_C_FAIL = 0x06;
	/**
	 * 调用getRegionListFromRegion失败
	 */
	int GET_R_F_R_FAIL = 0x07;
	/**
	 * 调用getCameraListFromRegion失败
	 */
	int GET_C_F_R_FAIL = 0x08;
	/**
	 * 从区域获取下级列表成功
	 */
	int GET_SUB_F_R_SUC = 0x09;
	/**
	 * 从区域获取下级列表失败
	 */
	int GET_SUB_F_R_FAILED = 0x0A;

}
