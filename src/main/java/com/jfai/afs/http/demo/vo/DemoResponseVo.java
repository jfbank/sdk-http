package com.jfai.afs.http.demo.vo;

import com.jfai.afs.http.constant.HttpConstant;

import java.util.HashMap;

/**
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2018/10/28 16:45
 */
public class DemoResponseVo extends HashMap<String,Object> {

    public DemoResponseVo setCode(String code) {
        put(HttpConstant.CODE, code);
        return this;
    }

    public String getCode() {
        return String.valueOf(get(HttpConstant.CODE));
    }

    public DemoResponseVo setMessage(String message) {
        put(HttpConstant.MESSAGE, message);
        return this;
    }

    public String getMessage() {
        return String.valueOf(get(HttpConstant.MESSAGE));
    }

    public DemoResponseVo setRequestId(String requestId) {
        put(HttpConstant.REQUEST_ID, requestId);
        return this;
    }
    public String getRequestId(){
        return String.valueOf(get(HttpConstant.REQUEST_ID));
    }


    public DemoResponseVo setData(Object data) {
        put(HttpConstant.DATA, data);
        return this;
    }


    public String getStartTime() {
        return String.valueOf(get(HttpConstant.START_TIME));
    }

    public DemoResponseVo setStartTime(String startTime) {
        put(HttpConstant.START_TIME, startTime);
        return this;
    }

    public String getSpendTime() {
        return String.valueOf(get(HttpConstant.SPEND_TIME));
    }

    public DemoResponseVo setSpendTime(String spendTime) {
        put(HttpConstant.SPEND_TIME, spendTime);
        return this;
    }
}
