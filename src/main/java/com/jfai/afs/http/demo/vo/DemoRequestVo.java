package com.jfai.afs.http.demo.vo;

import com.jfai.afs.http.constant.HttpConst;

import java.util.HashMap;

/**请求接口封装用的VO
 * <pre>
 *
 *
 * </pre>
 *
 * @author 玖富AI
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2018/10/23 14:28
 */
public class DemoRequestVo extends HashMap<String,Object> {

    public String getAppKey() {
        return String.valueOf(get(HttpConst.APP_KEY));
    }

    public DemoRequestVo setAppKey(String appKey) {
        put(HttpConst.APP_KEY, appKey);
        return this;
    }

    public String getTimestamp() {
        return String.valueOf(get(HttpConst.TIMESTAMP));
    }

    public DemoRequestVo setTimestamp(String timestamp) {
        put(HttpConst.TIMESTAMP, timestamp);
        return this;
    }


    public String getData() {
        return String.valueOf(get(HttpConst.DATA));
    }

    public String getSessionKey() {
        return String.valueOf(get(HttpConst.SESSION_KEY));
    }

    public DemoRequestVo setSessionKey(String sessionKey) {
        put(HttpConst.SESSION_KEY, sessionKey);
        return this;
    }



    public String getSign() {
        return String.valueOf(get(HttpConst.SIGN));
    }

    public DemoRequestVo setSign(String sign) {
        put(HttpConst.SIGN, sign);
        return this;
    }


    public DemoRequestVo setTimestamp(Long timestamp) {
        //return java.lang.String.valueOf(timestamp);
        return setTimestamp(String.valueOf(timestamp));
    }





    public DemoRequestVo setData(Object data) {
        put(HttpConst.DATA, data);
        return this;
    }



    public String getMethod() {
        return String.valueOf(get(HttpConst.METHOD));
    }

    public DemoRequestVo setMethod(String method) {
        put(HttpConst.METHOD, method);
        return this;
    }

    public String getPath() {
        return String.valueOf(get(HttpConst.PATH));
    }

    public DemoRequestVo setPath(String path) {
        put(HttpConst.PATH, path);
        return this;
    }





}
