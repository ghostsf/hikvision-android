/**
 * 
 */
package com.demo.sdk6x.callback;

/**
 * 消息回调接口
 * @author zhoudaihui
 * 
 */
public interface MsgCallback {
	/**
	 * @param msgId 消息id
	 * @param data  回调返回的数据
	 */
	void onMsg(int msgId, Object data);
}
