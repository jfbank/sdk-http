package com.jfai.afs.http.constant;

import java.nio.charset.Charset;

public interface HttpConstant {

    Charset UTF8 = Charset.forName("UTF-8");


    //
    //----Key 列表----
    //
    String APP_KEY="appKey";
    String APP_SECRET="appSecret";
    /**
     * 毫秒值
     */
    String TIMESTAMP="timestamp";
    String START_TIME="startTime";
    String SPEND_TIME="spendTime";
    String DATA="data";
    String SESSION_KEY="sessionKey";
    String METHOD="method";
    String PATH="path";
    String SIGN="sign";

    String CODE = "code";
    String MESSAGE = "message";
    String CAUSE = "cause";
    String REQUEST_ID = "requestId";
    String HOST_ID = "hostId";
    String VERSION = "version";
    String ENCRYPTION = "encryption";
//    String AUTHORIZATION = "Authorization";
//    String SIGNATURE="Signature";
    String ZIP = "zip";

    //----加密类别----//
    enum Encryption {
        MD5,
        SHA1,
        SHA256,
        ;
    }
}
