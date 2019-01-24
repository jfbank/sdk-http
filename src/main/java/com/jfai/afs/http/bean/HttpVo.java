package com.jfai.afs.http.bean;

import com.alibaba.fastjson.TypeReference;
import com.jfai.afs.http.constant.HttpConst;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/1/14 21:56
 */
public interface JfResBody {


    public Integer getCode();

    public JfResBody setCode(Integer code);

    public String getMessage();

    public JfResBody setMessage(String message);

    public String getCause();

    public JfResBody setCause(String cause);

    public String getRequestId();


    public JfResBody setRequestId(String requestId);

    public Long getStartTime();

    public JfResBody setStartTime(Long startTime);

    public Long getSpendTime();

    public JfResBody setSpendTime(Long spendTime);

    /**
     * @return
     */
    public Object getData();

    public JfResBody setData(Object data);

    public Boolean getEncryption();

    public JfResBody setEncryption(Boolean encryption);

    public Boolean getZip();

    public JfResBody setZip(Boolean zip);

    public String getSign();

    public JfResBody setSign(String sign);

    public String getSessionKey();

    public JfResBody setSessionKey(String sessionKey);

    public String getAppKey();

    public JfResBody setAppKey(String appKey);


    /**所有的字段和值一起构造成Map输出
     * @return
     */
    public Map<String, Object> toMap();





}
