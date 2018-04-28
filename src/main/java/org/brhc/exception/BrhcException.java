package org.brhc.exception;

/**
 * Created by zyong on 2017/1/24.
 */
public class BrhcException extends RuntimeException {

    public BrhcException(String message) {
        super(message);
    }

    public BrhcException(String message, Throwable cause) {
        super(message, cause);
    }
}
