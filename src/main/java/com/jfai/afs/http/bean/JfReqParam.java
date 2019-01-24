package com.jfai.afs.http.bean;

import com.jfai.afs.http.utils.JSONUtil;

/**
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/1/17 23:32
 */
public class JfReqParam extends AbstractHttpVo {
    protected String appKey;
    protected Long timestamp;

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString(boolean pretty) {
        if (!pretty) {
            return toString();
        }
        final StringBuilder sb = new StringBuilder(this.getClass().getSimpleName()).append("{").append("\n");
        // data 字段格式化一下
        sb.append("\t").append("appKey='").append(appKey).append("',\n");
        sb.append("\t").append("timestamp=").append(timestamp).append(",\n");
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
        final StringBuilder sb = new StringBuilder("JfReqParam{");
        sb.append("appKey='").append(appKey).append('\'');
        sb.append(", timestamp=").append(timestamp);
        sb.append(", data='").append(data).append('\'');
        sb.append(", encryption=").append(encryption);
        sb.append(", zip=").append(zip);
        sb.append(", sessionKey='").append(sessionKey).append('\'');
        sb.append(", sign='").append(sign).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
