package com.jfai.afs.http.demo.vo;

import com.jfai.afs.http.constant.HttpConst;

import java.util.HashMap;

/**
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2018/10/28 16:45
 */
public class DemoResponseVo extends HashMap<String,Object> {

    public DemoResponseVo setCode(String code) {
        put(HttpConst.CODE, code);
        return this;
    }

    public String getCode() {
        return String.valueOf(get(HttpConst.CODE));
    }

    public DemoResponseVo setMessage(String message) {
        put(HttpConst.MESSAGE, message);
        return this;
    }

    public String getMessage() {
        return String.valueOf(get(HttpConst.MESSAGE));
    }

    public DemoResponseVo setRequestId(String requestId) {
        put(HttpConst.REQUEST_ID, requestId);
        return this;
    }
    public String getRequestId(){
        return String.valueOf(get(HttpConst.REQUEST_ID));
    }


    public DemoResponseVo setData(Object data) {
        put(HttpConst.DATA, data);
        return this;
    }


    public String getStartTime() {
        return String.valueOf(get(HttpConst.START_TIME));
    }

    public DemoResponseVo setStartTime(String startTime) {
        put(HttpConst.START_TIME, startTime);
        return this;
    }

    public String getSpendTime() {
        return String.valueOf(get(HttpConst.SPEND_TIME));
    }

    public DemoResponseVo setSpendTime(String spendTime) {
        put(HttpConst.SPEND_TIME, spendTime);
        return this;
    }
}
