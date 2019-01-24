package com.jfai.afs.http.exception;

/**
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/1/19 0:28
 */
public class JfParseException extends JfWebRuntimeException{

    public JfParseException() {
        super();
    }

    public JfParseException(String message) {
        super(message);
    }

    public JfParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public JfParseException(Throwable cause) {
        super(cause);
    }

    protected JfParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
