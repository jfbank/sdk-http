package com.jfai.afs.http.bean;

import com.alibaba.fastjson.TypeReference;
import com.jfai.afs.http.exception.JfParseException;

import java.beans.IntrospectionException;
import java.text.ParseException;
import java.util.Map;

/**
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/1/14 21:56
 */
public interface HttpVo {




    /**
     * data 始终是字符串
     */
    public String getData();

    /**解析出指定类型
     * @param type
     * @param <T>
     */
    public <T> T getData(Class<T> type);

    /**
     * @param ref 这里使用fastjson框架的 TypeRef
     * @param <T>
     * @return
     */
    public <T> T getData(TypeReference<T> ref);

    /**
     * to json adaptedly
     * @param data
     * @return
     */
    public void setData(Object data);

    /**为了兼容, 所有Boolean类型统一用包装类.
     * 否则, 在拼接签名字符串时会和js拼接的效果不一致.
     * @return
     */
    public Boolean getEncryption();

    public void setEncryption(Boolean encryption);

    public Boolean getZip();

    public void setZip(Boolean zip);

    public String getSign();

    public void setSign(String sign);

    public String getSessionKey();

    public void setSessionKey(String sessionKey);



    /**所有的字段和值一起构造成Map输出
     * @return
     */
    public Map<String, Object> toMap();


    /**格式化方式 toString
     * @param pretty
     * @return
     */
    public String toString(boolean pretty);


}
