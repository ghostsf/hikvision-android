package com.demo.sdk6x.live;

/**
 * 对讲信息
 * @author weilinfeng
 * @Data 2014-6-17
 */
public class TalkCallInfo {
    /** 服务器IP，用MAG的IP */
    public String servIP;
    /** 注册服务器端口，MAG对讲端口 */
    public int servPort;
    /** 客户端UserID，用登陆MSP的SessionID表示 */
    public String userID;
    /** 接收方的UserID，使用对讲器的indexCode，用于对讲服务器对讲(目前接VAG可使用deviceIndexCode) */
    public String toUserID;
}
