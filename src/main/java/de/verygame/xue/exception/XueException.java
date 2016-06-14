package de.verygame.xue.exception;

/**
 * @author Rico Schrage
 */
public class XueException extends RuntimeException  {

    public XueException(String msg) {
        super(msg);
    }

    public XueException(Throwable throwable) {
        super(throwable);
    }

}
