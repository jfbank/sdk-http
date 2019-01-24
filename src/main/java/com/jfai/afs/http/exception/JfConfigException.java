package com.jfai.afs.http.exception;

/**
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/1/15 1:20
 */
public class JfSecurityException extends RuntimeException {

    public JfSecurityException() {
        super();
    }

    public JfSecurityException(String message) {
        super(message);
    }

    public JfSecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    public JfSecurityException(Throwable cause) {
        super(cause);
    }

    protected JfSecurityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
