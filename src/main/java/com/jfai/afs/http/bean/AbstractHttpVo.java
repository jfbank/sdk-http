package com.jfai.afs.http.bean;

import com.alibaba.fastjson.TypeReference;
import com.jfai.afs.http.utils.JSONUtil;
import com.jfai.afs.http.utils.TypeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/1/17 23:29
 */
public abstract class AbstractHttpVo implements HttpVo, Serializable {
    protected static final Logger log = LoggerFactory.getLogger(AbstractHttpVo.class);

    /**
     * 确保始终 json 字符串
     */
    protected String data;
    /**
     * 标记 data 是否被加密
     */
    protected Boolean encryption;
    /**
     * 标记 data 是否被压缩
     */
    protected Boolean zip;
    /**
     * 加密data的AES密钥, 动态生成.
     */
    protected String sessionKey;
    /**
     * 数据签名. 响应或请求对象, 除sign自身外所有非空成员拼接的字符串作为签名正文.
     */
    protected String sign;





    @Override
    public String getData() {
        return this.data;
    }

    @Override
    public <T> T getData(Class<T> type) {
        if (this.data == null) {
            return null;
        }
        return TypeUtil.parseObject(this.data, type);
    }

    @Override
    public <T> T getData(TypeReference<T> ref) {
        if (this.data == null) {
            return null;
        }

        return TypeUtil.parseObject(this.data, ref);
    }


    @Override
    public void setData(Object data) {
        if (data == null) {
            return;
        }

        // 转json
        //this.data = JSON.toJSONString(data);
        this.data = JSONUtil.toJsonAdapt(data);
    }

    @Override
    public Boolean getEncryption() {
        return this.encryption;
    }

    @Override
    public void setEncryption(Boolean encryption) {
        this.encryption = encryption;
    }

    @Override
    public Boolean getZip() {
        return zip;
    }

    @Override
    public void setZip(Boolean zip) {
        this.zip = zip;
    }

    @Override
    public String getSessionKey() {
        return sessionKey;
    }

    @Override
    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    @Override
    public String getSign() {
        return sign;
    }

    @Override
    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public Map<String, Object> toMap()  {

        Map<String, Object> map = new HashMap<String, Object>();
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(this.getClass());
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            String key = property.getName();
            // 忽略'class'
            if (key.compareToIgnoreCase("class") == 0) {
                continue;
            }
            Method getter = property.getReadMethod();
            Object value = null;
            try {
                value = getter != null ? getter.invoke(this) : null;
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }


            map.put(key, value);
        }

        log.trace("toMap: {}",map);

        return map;
    }

    @Override
    public String toString(boolean pretty) {
        if (!pretty) {
            return toString();
        }
        final StringBuilder sb = new StringBuilder(this.getClass().getSimpleName()).append("{").append("\n");
        // data 字段格式化一下
        sb.append("\t").append("data='").append(JSONUtil.formatJson(data)).append("',\n");
        sb.append("\t").append("encryption=").append(encryption).append(",\n");
        sb.append("\t").append("zip=").append(zip).append(",\n");
        sb.append("\t").append("sessionKey='").append(sessionKey).append('\'').append(",\n");
        sb.append("\t").append("sign='").append(sign).append('\'').append("\n");
        sb.append('}');
        return sb.toString();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AbstractHttpVo{");
        sb.append("data='").append(data).append('\'');
        sb.append(", encryption=").append(encryption);
        sb.append(", zip=").append(zip);
        sb.append(", sessionKey='").append(sessionKey).append('\'');
        sb.append(", sign='").append(sign).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
