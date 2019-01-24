package com.jfai.afs.http.exception;

/**玖富SDK runtime exception 的顶级类
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/1/21 10:21
 */
public class JfWebRuntimeException extends RuntimeException{

    public JfWebRuntimeException() {
    }

    public JfWebRuntimeException(String message) {
        super(message);
    }

    public JfWebRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public JfWebRuntimeException(Throwable cause) {
        super(cause);
    }

    public JfWebRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
