package com.jfai.afs.http.bean;

import com.jfai.afs.http.utils.JSONUtil;

/**
 * <code>HttpVo</code>的实现类, 用于接收封装响应数据
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/1/14 21:56
 */
public class JfResBody extends AbstractHttpVo {

    protected String requestId;
    protected Integer code;
    protected String message;
    protected String cause;
    protected Long startTime;
    protected Long spendTime;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getSpendTime() {
        return spendTime;
    }

    public void setSpendTime(Long spendTime) {
        this.spendTime = spendTime;
    }

    @Override
    public String toString(boolean pretty) {
        if (!pretty) {
            return toString();
        }
        final StringBuilder sb = new StringBuilder(this.getClass().getSimpleName()).append("{").append("\n");
        // data 字段格式化一下
        sb.append("\t").append("requestId='").append(requestId).append("',\n");
        sb.append("\t").append("code=").append(code).append(",\n");
        sb.append("\t").append("message='").append(message).append("',\n");
        sb.append("\t").append("cause='").append(cause).append("',\n");
        sb.append("\t").append("startTime=").append(startTime).append(",\n");
        sb.append("\t").append("spendTime=").append(spendTime).append(",\n");
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
        final StringBuilder sb = new StringBuilder("IResBody{");
        sb.append("requestId='").append(requestId).append('\'');
        sb.append(", code=").append(code);
        sb.append(", message='").append(message).append('\'');
        sb.append(", cause='").append(cause).append('\'');
        sb.append(", startTime=").append(startTime);
        sb.append(", spendTime=").append(spendTime);
        sb.append(", data='").append(data).append('\'');
        sb.append(", encryption=").append(encryption);
        sb.append(", zip=").append(zip);
        sb.append(", sessionKey='").append(sessionKey).append('\'');
        sb.append(", sign='").append(sign).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
