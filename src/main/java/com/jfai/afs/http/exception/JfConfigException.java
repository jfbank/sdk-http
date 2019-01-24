package com.jfai.afs.http.exception;

/**
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/1/15 1:20
 */
public class JfConfigException extends JfWebRuntimeException {

    public JfConfigException() {
        super();
    }

    public JfConfigException(String message) {
        super(message);
    }

    public JfConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public JfConfigException(Throwable cause) {
        super(cause);
    }

    protected JfConfigException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
