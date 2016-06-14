package de.verygame.xue.exception;

import java.util.Locale;

/**
 * Created by Rico on 10.07.2015.
 *
 * @author Rico Schrage
 */
public class XueSyntaxException extends XueException {

    public XueSyntaxException(int lineNumber, String message) {
        super(String.format(Locale.ENGLISH, "A syntax error has occurred on line %d (%s)", lineNumber, message));
    }

}
