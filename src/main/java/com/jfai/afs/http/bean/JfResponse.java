package com.jfai.afs.http.bean;

/**
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/1/15 10:57
 */
public class JfResponse {

    protected Integer statusCode;
    /**
     * 响应体
     */
    protected JfResBody body;

    /**
     * http响应码是否是 2xx
     * @return
     */
    public boolean is2xx() {
        if (statusCode == null) {
            return false;
        }
        return statusCode / 100 == Series.SUCCESSFUL.value();
    }

    public boolean is3xx() {
        if (statusCode == null) {
            return false;
        }
        return statusCode / 100 == Series.REDIRECTION.value();
    }

    public boolean is4xx() {
        if (statusCode == null) {
            return false;
        }
        return statusCode / 100 == Series.CLIENT_ERROR.value();
    }

    public boolean is5xx() {
        if (statusCode == null) {
            return false;
        }
        return statusCode / 100 == Series.SERVER_ERROR.value();
    }

    public JfResponse() {
    }

    public JfResponse(Integer statusCode, JfResBody body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public JfResponse setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public JfResBody getBody() {
        return body;
    }

    public JfResponse setBody(JfResBody body) {
        this.body = body;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JfResponse{");
        sb.append("statusCode=").append(statusCode);
        sb.append(", body=").append(body);
        sb.append('}');
        return sb.toString();
    }

    /**
     * Enumeration of HTTP status series.
     *
     */
    public enum Series {

        INFORMATIONAL(1),
        SUCCESSFUL(2),
        REDIRECTION(3),
        CLIENT_ERROR(4),
        SERVER_ERROR(5);

        private final int value;

        Series(int value) {
            this.value = value;
        }

        /**
         * Return the integer value of this status series. Ranges from 1 to 5.
         */
        public int value() {
            return this.value;
        }

        public static Series valueOf(int status) {
            int seriesCode = status / 100;
            for (Series series : values()) {
                if (series.value == seriesCode) {
                    return series;
                }
            }
            throw new IllegalArgumentException("No matching constant for [" + status + "]");
        }
    }
}
