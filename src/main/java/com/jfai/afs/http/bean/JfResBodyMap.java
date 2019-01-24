package com.jfai.afs.http.bean;

import com.jfai.afs.http.constant.HttpConst;
import com.jfai.afs.http.utils.CommonUtil;

import java.util.HashMap;
import java.util.Map;

import static com.jfai.afs.http.utils.CommonUtil.getString;

/**
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/1/14 21:56
 */
public class JfResBodyImpl extends HashMap<String,Object> implements JfResBody{



    @Override
    public Integer getCode() {
        Object code = this.get(HttpConst.CODE);

        return CommonUtil.getInteger(code);
    }

    @Override
    public JfResBody setCode(Integer code) {
        this.put(HttpConst.CODE, code);
        return this;
    }

    @Override
    public String getMessage() {
        return getString(HttpConst.MESSAGE);
    }

    @Override
    public JfResBody setMessage(String message) {
        this.put(HttpConst.MESSAGE, message);
        return this;
    }

    @Override
    public String getCause() {
        return getString(HttpConst.CAUSE);
    }

    @Override
    public JfResBody setCause(String cause) {
        this.put(HttpConst.CAUSE, cause);
        return this;
    }

    @Override
    public String getRequestId() {
        return getString(HttpConst.REQUEST_ID);
    }


    @Override
    public JfResBody setRequestId(String requestId) {
        this.put(HttpConst.REQUEST_ID, requestId);
        return this;
    }

    @Override
    public Long getStartTime() {
        return CommonUtil.getLong(HttpConst.START_TIME);
    }

    @Override
    public JfResBody setStartTime(Long startTime) {
        this.put(HttpConst.START_TIME, startTime);
        return this;
    }

    @Override
    public Long getSpendTime() {
        return CommonUtil.getLong(HttpConst.SPEND_TIME);
    }

    @Override
    public JfResBody setSpendTime(Long spendTime) {
        this.put(HttpConst.SPEND_TIME, spendTime);
        return this;
    }

    @Override
    public Object getData() {
        return this.get(HttpConst.DATA);
    }

    @Override
    public JfResBody setData(Object data) {
        this.put(HttpConst.DATA, data);
        return this;
    }

    @Override
    public Boolean getEncryption() {
        Object enc = this.get(HttpConst.ENCRYPTION);
        if (enc == null) {
            // 缺省表示没有加密
            return false;
        }
        if (enc instanceof Boolean) {
            return (Boolean) enc;
        }
        return Boolean.parseBoolean(String.valueOf(enc));
    }

    @Override
    public JfResBody setEncryption(Boolean encryption) {
        this.put(HttpConst.ENCRYPTION, encryption);
        return this;
    }

    @Override
    public Boolean getZip() {
        Object zip = this.get(HttpConst.ZIP);
        if (zip == null) {
            // 缺省表示没有加密
            return false;
        }
        if (zip instanceof Boolean) {
            return (Boolean) zip;
        }
        return Boolean.parseBoolean(String.valueOf(zip));
    }

    @Override
    public JfResBody setZip(Boolean zip) {
        this.put(HttpConst.ZIP, zip);
        return this;
    }

    @Override
    public String getSign() {
        return getString(HttpConst.SIGN);
    }

    @Override
    public JfResBody setSign(String sign) {
        this.put(HttpConst.SIGN, sign);
        return this;
    }

    @Override
    public String getSessionKey() {
        return getString(HttpConst.SESSION_KEY);
    }

    @Override
    public JfResBody setSessionKey(String sessionKey) {
        this.put(HttpConst.SESSION_KEY, sessionKey);
        return this;
    }

    @Override
    public String getAppKey() {
        return getString(HttpConst.APP_KEY);
    }

    @Override
    public JfResBody setAppKey(String appKey) {
        this.put(HttpConst.APP_KEY, appKey);
        return this;
    }

    @Override
    public Map<String, Object> toMap() {
        return this;
    }


}
